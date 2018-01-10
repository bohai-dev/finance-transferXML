package com.bohai.finance.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bohai.finance.model.BusinessDepartment;
import com.bohai.finance.model.HeadCharge;
import com.bohai.finance.model.HeadProfit;
import com.bohai.finance.model.MarketDepCharge;
import com.bohai.finance.model.MarketDepProfit;
import com.bohai.finance.util.ApplicationConfig;
import com.bohai.finance.util.DateFormatterUtil;

/**
 * 盈亏凭证
 * @author caojia
 *
 */
public class ProfitVoucherService {

    static Logger logger = Logger.getLogger(ProfitVoucherService.class);
    
    
    
public void generateVoucher(File infile, String targetPath, String date) throws Exception{
        
        //先查询所有部门信息
        Map<String, BusinessDepartment> deptMap= new HashMap<String, BusinessDepartment>();
        
        DeptService deptService = new DeptService();
        List<BusinessDepartment> depts = deptService.queryDepts();
        if(depts !=null){
            for (BusinessDepartment businessDepartment : depts) {
                deptMap.put(businessDepartment.getDeptName(), businessDepartment);
            }
        }
        
        //获取列数据
        //部门列
        int depIndex = 0;
        //交易所名称列
        int exchangeIndex = 0;
        //盈亏列
        int profitIndex = 0;
        
        Document document = this.parse(infile);
        
        Element root = document.getRootElement();
        
        Element workSheet = root.element("Worksheet");
        
        Element table = workSheet.element("Table");
        
        List<Element> rows = table.elements("Row");
        
        //获取标题索引
        Element head = rows.get(3);
        List<Element> headCells = head.elements("Cell");
        for(int j = 0 ; j < headCells.size() ; j++){
            
            Element cell = headCells.get(j);
            Element data = cell.element("Data");
            String value = data.getTextTrim();
            
            if("投资者名称".equals(value)){
                depIndex = j;
            }else if ("交易所代码".equals(value)) {
                exchangeIndex = j;
            }else if ("盈亏".equals(value)) {
                profitIndex = j;
            }
        }
        
        /**
         * 营业部盈亏账簿
         */
        Map<String, MarketDepProfit> bookMap = new HashMap<String, MarketDepProfit>();
        /**
         * 总部盈亏账簿
         */
        HeadProfit headProfit = new HeadProfit();
        
        for(int i =4 ; i < rows.size() ; i++){
            
            Element row = rows.get(i);
            
            List<Element> cells = row.elements("Cell");
            
                Element firstCell = cells.get(0).element("Data");
                String firstStr = firstCell.getText();
                if(firstStr.indexOf("总计") > -1){
                    break;
                }
            
                //营业部名称
                Element deptCell = cells.get(depIndex).element("Data");
                String deptName = deptCell.getTextTrim();
                //交易所代码
                Element exchangeCell = cells.get(exchangeIndex).element("Data");
                String exchange = exchangeCell.getTextTrim();
                //盈亏
                Element profitCell = cells.get(profitIndex).element("Data");
                String profit = profitCell.getTextTrim().replaceAll(",", "");
                
                BusinessDepartment businessDepartment = deptMap.get(deptName);
                if(businessDepartment == null){
                    throw new Exception("营业部信息未维护："+deptName);
                }else {
                    
                    if("00-0002".equals(businessDepartment.getBookNo())){
                        //总部账簿
                        headProfit.addProfit(exchange, new BigDecimal(profit));
                    }else {
                        //营业部账簿
                        MarketDepProfit marketDepProfit = bookMap.get(businessDepartment.getBookNo());
                        if(marketDepProfit == null){
                            MarketDepProfit depProfit = new MarketDepProfit();
                            depProfit.setBookNo(businessDepartment.getBookNo());
                            depProfit.setTotalProfit(new BigDecimal(profit));
                            bookMap.put(depProfit.getBookNo(), depProfit);
                        }else {
                            marketDepProfit.setTotalProfit(marketDepProfit.getTotalProfit().add(new BigDecimal(profit)));
                        }
                    }
                }
        }
        
        Document outDoc = this.createDocument(headProfit,bookMap , date);
        
        this.write(outDoc,targetPath);
        
        
    }

    public Document createDocument(HeadProfit headProfit,Map<String, MarketDepProfit> bookMap ,String date) throws ParseException {
        Document document = DocumentHelper.createDocument();
        Element ufinterface = document.addElement("ufinterface")
                .addAttribute("account", ApplicationConfig.getProperty("account"))//"002"
                .addAttribute("billtype", ApplicationConfig.getProperty("billtype"))//"vouchergl"
                .addAttribute("businessunitcode", ApplicationConfig.getProperty("businessunitcode"))//"develop"
                .addAttribute("filename", "")
                .addAttribute("groupcode", ApplicationConfig.getProperty("groupcode"))//"01"
                .addAttribute("isexchange", "Y")
                .addAttribute("orgcode", ApplicationConfig.getProperty("orgcode"))//"00"
                .addAttribute("receiver", "")
                .addAttribute("replace", "Y")
                .addAttribute("roottag", "")
                .addAttribute("sender", "NC_OA");
                
        String preparedDate = date.substring(9, 17);
        logger.debug("结束日期："+preparedDate);
        preparedDate = DateFormatterUtil.getDateStrByFormatter(DateFormatterUtil.getDateByFormatter(preparedDate, "yyyyMMdd"), "yyyy-MM-dd");
        logger.debug("制单日期："+preparedDate);
        
        /**
         * 总部盈亏凭证
         */
        {
            Element voucher = ufinterface.addElement("voucher");
            
            Element voucher_head = voucher.addElement("voucher_head");
            voucher_head.addElement("year").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
            //账簿号
            voucher_head.addElement("pk_accountingbook").setText(headProfit.getBookNo());
            
            voucher_head.addElement("period").setText(date);
            voucher_head.addElement("prepareddate").setText(preparedDate);
            voucher_head.addElement("pk_prepared").setText("yy01");
            voucher_head.addElement("pk_org").setText(headProfit.getBookNo().substring(0, 2));
            voucher_head.addElement("pk_org_v").setText(headProfit.getBookNo().substring(0, 2));
            voucher_head.addElement("pk_system").setText("GL");
            voucher_head.addElement("pk_vouchertype").setText("01");//凭证类别
            voucher_head.addElement("voucherkind").setText("0");
            voucher_head.addElement("discardflag").setText("N");
            voucher_head.addElement("attachment").setText("0");
            voucher_head.addElement("signflag").setText("N");
            Element details = voucher_head.addElement("details");
            
            
            if(headProfit.getDl().compareTo(BigDecimal.ZERO) > 0){
                //大连盈亏大于0 放在借方
                Element item = details.addElement("item");
                item.addElement("detailindex").setText("1");
                item.addElement("explanation").setText("交易所盈亏"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText(headProfit.getDl().setScale(2, RoundingMode.HALF_UP).abs().toString());
                item.addElement("localdebitamount").setText(headProfit.getDl().setScale(2, RoundingMode.HALF_UP).abs().toString());
                item.addElement("accsubjcode").setText("11240102");//科目  
                item.addElement("price").setText("0");//单价
                item.addElement("excrate2").setText("1");
                item.addElement("debitquantity").setText("0");//借方数量
                item.addElement("groupdebitamount").setText("0");
                item.addElement("globaldebitamount").setText("0");
                item.addElement("creditquantity").setText("0");//贷方数量
                item.addElement("creditamount").setText("0");//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText("0");
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText("11240102");//
            }else {
                //大连盈亏小于0 放在贷方
                Element item = details.addElement("item");
                item.addElement("detailindex").setText("1");
                item.addElement("explanation").setText("交易所盈亏"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText("0");
                item.addElement("localdebitamount").setText("0");
                item.addElement("accsubjcode").setText("11240102");//科目 
                item.addElement("price").setText("0");//单价
                item.addElement("excrate2").setText("1");
                item.addElement("debitquantity").setText("0");//借方数量
                item.addElement("groupdebitamount").setText("0");
                item.addElement("globaldebitamount").setText("0");
                item.addElement("creditquantity").setText("0");//贷方数量
                item.addElement("creditamount").setText(headProfit.getDl().setScale(2, RoundingMode.HALF_UP).abs().toString());//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText(headProfit.getDl().setScale(2, RoundingMode.HALF_UP).abs().toString());
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText("11240102");//
            }
            
            if(headProfit.getSh().compareTo(BigDecimal.ZERO) > 0){
                //上期所盈亏大于0 放在借方
                Element item = details.addElement("item");
                item.addElement("detailindex").setText("2");
                item.addElement("explanation").setText("交易所盈亏"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText(headProfit.getSh().setScale(2, RoundingMode.HALF_UP).abs().toString());
                item.addElement("localdebitamount").setText(headProfit.getSh().setScale(2, RoundingMode.HALF_UP).abs().toString());
                item.addElement("accsubjcode").setText("11240202");//科目  
                item.addElement("price").setText("0");//单价
                item.addElement("excrate2").setText("1");
                item.addElement("debitquantity").setText("0");//借方数量
                item.addElement("groupdebitamount").setText("0");
                item.addElement("globaldebitamount").setText("0");
                item.addElement("creditquantity").setText("0");//贷方数量
                item.addElement("creditamount").setText("0");//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText("0");
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText("11240202");//
            }else {
                //上期所盈亏小于0 放在贷方
                Element item = details.addElement("item");
                item.addElement("detailindex").setText("2");
                item.addElement("explanation").setText("交易所盈亏"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText("0");
                item.addElement("localdebitamount").setText("0");
                item.addElement("accsubjcode").setText("11240202");//科目 
                item.addElement("price").setText("0");//单价
                item.addElement("excrate2").setText("1");
                item.addElement("debitquantity").setText("0");//借方数量
                item.addElement("groupdebitamount").setText("0");
                item.addElement("globaldebitamount").setText("0");
                item.addElement("creditquantity").setText("0");//贷方数量
                item.addElement("creditamount").setText(headProfit.getSh().setScale(2, RoundingMode.HALF_UP).abs().toString());//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText(headProfit.getSh().setScale(2, RoundingMode.HALF_UP).abs().toString());
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText("11240202");//
            }
            
            if(headProfit.getZz().compareTo(BigDecimal.ZERO) > 0){
                //郑商所盈亏大于0 放在借方
                Element item = details.addElement("item");
                item.addElement("detailindex").setText("3");
                item.addElement("explanation").setText("交易所盈亏"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText(headProfit.getZz().setScale(2, RoundingMode.HALF_UP).abs().toString());
                item.addElement("localdebitamount").setText(headProfit.getZz().setScale(2, RoundingMode.HALF_UP).abs().toString());
                item.addElement("accsubjcode").setText("11240302");//科目  
                item.addElement("price").setText("0");//单价
                item.addElement("excrate2").setText("1");
                item.addElement("debitquantity").setText("0");//借方数量
                item.addElement("groupdebitamount").setText("0");
                item.addElement("globaldebitamount").setText("0");
                item.addElement("creditquantity").setText("0");//贷方数量
                item.addElement("creditamount").setText("0");//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText("0");
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText("11240302");//
            }else {
                //郑商所盈亏小于0 放在贷方
                Element item = details.addElement("item");
                item.addElement("detailindex").setText("3");
                item.addElement("explanation").setText("交易所盈亏"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText("0");
                item.addElement("localdebitamount").setText("0");
                item.addElement("accsubjcode").setText("11240302");//科目 
                item.addElement("price").setText("0");//单价
                item.addElement("excrate2").setText("1");
                item.addElement("debitquantity").setText("0");//借方数量
                item.addElement("groupdebitamount").setText("0");
                item.addElement("globaldebitamount").setText("0");
                item.addElement("creditquantity").setText("0");//贷方数量
                item.addElement("creditamount").setText(headProfit.getZz().setScale(2, RoundingMode.HALF_UP).abs().toString());//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText(headProfit.getZz().setScale(2, RoundingMode.HALF_UP).abs().toString());
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText("11240302");//
            }
            
            if(headProfit.getZj().compareTo(BigDecimal.ZERO) > 0){
                //中金所盈亏大于0 放在借方
                Element item = details.addElement("item");
                item.addElement("detailindex").setText("4");
                item.addElement("explanation").setText("交易所盈亏"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText(headProfit.getZj().setScale(2, RoundingMode.HALF_UP).abs().toString());
                item.addElement("localdebitamount").setText(headProfit.getZj().setScale(2, RoundingMode.HALF_UP).abs().toString());
                item.addElement("accsubjcode").setText("11240402");//科目  
                item.addElement("price").setText("0");//单价
                item.addElement("excrate2").setText("1");
                item.addElement("debitquantity").setText("0");//借方数量
                item.addElement("groupdebitamount").setText("0");
                item.addElement("globaldebitamount").setText("0");
                item.addElement("creditquantity").setText("0");//贷方数量
                item.addElement("creditamount").setText("0");//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText("0");
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText("11240402");//
            }else {
                //中金所盈亏小于0 放在贷方
                Element item = details.addElement("item");
                item.addElement("detailindex").setText("4");
                item.addElement("explanation").setText("交易所盈亏"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText("0");
                item.addElement("localdebitamount").setText("0");
                item.addElement("accsubjcode").setText("11240402");//科目 
                item.addElement("price").setText("0");//单价
                item.addElement("excrate2").setText("1");
                item.addElement("debitquantity").setText("0");//借方数量
                item.addElement("groupdebitamount").setText("0");
                item.addElement("globaldebitamount").setText("0");
                item.addElement("creditquantity").setText("0");//贷方数量
                item.addElement("creditamount").setText(headProfit.getZj().setScale(2, RoundingMode.HALF_UP).abs().toString());//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText(headProfit.getZj().setScale(2, RoundingMode.HALF_UP).abs().toString());
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText("11240402");//
            }
            
            if(headProfit.getTotalProfit().compareTo(BigDecimal.ZERO) < 0){
                //总盈亏小于0 放在借方
                Element item = details.addElement("item");
                item.addElement("detailindex").setText("5");
                item.addElement("explanation").setText("交易所盈亏"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText(headProfit.getTotalProfit().setScale(2, RoundingMode.HALF_UP).abs().toString());
                item.addElement("localdebitamount").setText(headProfit.getTotalProfit().setScale(2, RoundingMode.HALF_UP).abs().toString());
                item.addElement("accsubjcode").setText("2006");//科目  
                item.addElement("price").setText("0");//单价
                item.addElement("excrate2").setText("1");
                item.addElement("debitquantity").setText("0");//借方数量
                item.addElement("groupdebitamount").setText("0");
                item.addElement("globaldebitamount").setText("0");
                item.addElement("creditquantity").setText("0");//贷方数量
                item.addElement("creditamount").setText("0");//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText("0");
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText("2006");//
            }else {
                //总盈亏大于0 放在贷方
                Element item = details.addElement("item");
                item.addElement("detailindex").setText("5");
                item.addElement("explanation").setText("交易所盈亏"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText("0");
                item.addElement("localdebitamount").setText("0");
                item.addElement("accsubjcode").setText("2006");//科目 
                item.addElement("price").setText("0");//单价
                item.addElement("excrate2").setText("1");
                item.addElement("debitquantity").setText("0");//借方数量
                item.addElement("groupdebitamount").setText("0");
                item.addElement("globaldebitamount").setText("0");
                item.addElement("creditquantity").setText("0");//贷方数量
                item.addElement("creditamount").setText(headProfit.getTotalProfit().setScale(2, RoundingMode.HALF_UP).abs().toString());//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText(headProfit.getTotalProfit().setScale(2, RoundingMode.HALF_UP).abs().toString());
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText("2006");//
            }
            
            //总部凭证结束
        }
        
        //营业部盈亏凭证
        for (Entry<String, MarketDepProfit> m :bookMap.entrySet())  {
            MarketDepProfit profit = m.getValue();
            
            Element voucher = ufinterface.addElement("voucher");
            
            Element voucher_head = voucher.addElement("voucher_head");
            voucher_head.addElement("year").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
            //账簿号
            voucher_head.addElement("pk_accountingbook").setText(profit.getBookNo());
            
            voucher_head.addElement("period").setText(date);
            voucher_head.addElement("prepareddate").setText(preparedDate);
            voucher_head.addElement("pk_prepared").setText("yy01");
            voucher_head.addElement("pk_org").setText(profit.getBookNo().substring(0, 2));
            voucher_head.addElement("pk_org_v").setText(profit.getBookNo().substring(0, 2));
            voucher_head.addElement("pk_system").setText("GL");
            voucher_head.addElement("pk_vouchertype").setText("01");//凭证类别
            voucher_head.addElement("voucherkind").setText("0");
            voucher_head.addElement("discardflag").setText("N");
            voucher_head.addElement("attachment").setText("0");
            voucher_head.addElement("signflag").setText("N");
            
            Element details = voucher_head.addElement("details");
            if(profit.getTotalProfit().compareTo(BigDecimal.ZERO) > 0){
                //盈亏大于0  应付货币保证金在贷方    与总部往来在借方
                
                {
                    //贷方  应付货币保证金
                    Element item = details.addElement("item");
                    item.addElement("detailindex").setText("1");
                    item.addElement("explanation").setText("交易所盈亏"+date);
                    item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                    item.addElement("debitamount").setText("0");
                    item.addElement("localdebitamount").setText("0");
                    item.addElement("accsubjcode").setText("2006");//科目  交易手续费
                    item.addElement("price").setText("0");//单价
                    item.addElement("excrate2").setText("1");
                    item.addElement("debitquantity").setText("0");//借方数量
                    item.addElement("groupdebitamount").setText("0");
                    item.addElement("globaldebitamount").setText("0");
                    item.addElement("creditquantity").setText("0");//贷方数量
                    item.addElement("creditamount").setText(profit.getTotalProfit().setScale(2, RoundingMode.HALF_UP).abs().toString());//贷方金额
                    item.addElement("groupcreditamount").setText("0");
                    item.addElement("globalcreditamount").setText("0");
                    item.addElement("localcreditamount").setText(profit.getTotalProfit().setScale(2, RoundingMode.HALF_UP).abs().toString());
                    item.addElement("pk_currtype").setText("CNY");
                    item.addElement("pk_accasoa").setText("2006");//
                }
                {
                    //借方 与总部往来
                    Element item = details.addElement("item");
                    item.addElement("detailindex").setText("2");
                    item.addElement("explanation").setText("交易所盈亏"+date);
                    item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                    item.addElement("debitamount").setText(profit.getTotalProfit().setScale(2, RoundingMode.HALF_UP).abs().toString());
                    item.addElement("localdebitamount").setText(profit.getTotalProfit().setScale(2, RoundingMode.HALF_UP).abs().toString());
                    item.addElement("accsubjcode").setText("22410101");//科目  
                    item.addElement("price").setText("0");//单价
                    item.addElement("excrate2").setText("1");
                    item.addElement("debitquantity").setText("0");//借方数量
                    item.addElement("groupdebitamount").setText("0");
                    item.addElement("globaldebitamount").setText("0");
                    item.addElement("creditquantity").setText("0");//贷方数量
                    item.addElement("creditamount").setText("0");//贷方金额
                    item.addElement("groupcreditamount").setText("0");
                    item.addElement("globalcreditamount").setText("0");
                    item.addElement("localcreditamount").setText("0");
                    item.addElement("pk_currtype").setText("CNY");
                    item.addElement("pk_accasoa").setText("22410101");//
                    Element ass = item.addElement("ass").addElement("item");
                    ass.addElement("pk_Checktype").setText("0005"); //辅助核算项编码   内部客商
                    ass.addElement("pk_Checkvalue").setText("00");  //和总部挂往来
                }
            }else {
              //盈亏小于0  应付货币保证金在借方    与总部往来在贷方
                {
                    //借方 与总部往来
                    Element item = details.addElement("item");
                    item.addElement("detailindex").setText("1");
                    item.addElement("explanation").setText("交易所盈亏"+date);
                    item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                    item.addElement("debitamount").setText(profit.getTotalProfit().setScale(2, RoundingMode.HALF_UP).abs().toString());
                    item.addElement("localdebitamount").setText(profit.getTotalProfit().setScale(2, RoundingMode.HALF_UP).abs().toString());
                    item.addElement("accsubjcode").setText("2006");//科目  
                    item.addElement("price").setText("0");//单价
                    item.addElement("excrate2").setText("1");
                    item.addElement("debitquantity").setText("0");//借方数量
                    item.addElement("groupdebitamount").setText("0");
                    item.addElement("globaldebitamount").setText("0");
                    item.addElement("creditquantity").setText("0");//贷方数量
                    item.addElement("creditamount").setText("0");//贷方金额
                    item.addElement("groupcreditamount").setText("0");
                    item.addElement("globalcreditamount").setText("0");
                    item.addElement("localcreditamount").setText("0");
                    item.addElement("pk_currtype").setText("CNY");
                    item.addElement("pk_accasoa").setText("2006");//
                }
                
                {
                    //贷方  与总部往来
                    Element item = details.addElement("item");
                    item.addElement("detailindex").setText("2");
                    item.addElement("explanation").setText("交易所盈亏"+date);
                    item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                    item.addElement("debitamount").setText("0");
                    item.addElement("localdebitamount").setText("0");
                    item.addElement("accsubjcode").setText("22410101");//科目  交易手续费
                    item.addElement("price").setText("0");//单价
                    item.addElement("excrate2").setText("1");
                    item.addElement("debitquantity").setText("0");//借方数量
                    item.addElement("groupdebitamount").setText("0");
                    item.addElement("globaldebitamount").setText("0");
                    item.addElement("creditquantity").setText("0");//贷方数量
                    item.addElement("creditamount").setText(profit.getTotalProfit().setScale(2, RoundingMode.HALF_UP).abs().toString());//贷方金额
                    item.addElement("groupcreditamount").setText("0");
                    item.addElement("globalcreditamount").setText("0");
                    item.addElement("localcreditamount").setText(profit.getTotalProfit().setScale(2, RoundingMode.HALF_UP).abs().toString());
                    item.addElement("pk_currtype").setText("CNY");
                    item.addElement("pk_accasoa").setText("22410101");//
                    Element ass = item.addElement("ass").addElement("item");
                    ass.addElement("pk_Checktype").setText("0005"); //辅助核算项编码   内部客商
                    ass.addElement("pk_Checkvalue").setText("00");  //和总部挂往来
                }
              
            }
        }
        
        
        return document;
    }
    
    /**
     * 解析xml文件
     * @param file xml文件
     * @return
     * @throws DocumentException
     * @throws FileNotFoundException
     */
    public Document parse(File file) throws DocumentException, FileNotFoundException {
        
        InputStream inputStream = new FileInputStream(file);
        
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        return document;
    }
    
    public void write(Document document,String url) throws IOException {
        
        //设置文件编码  
        OutputFormat xmlFormat = new OutputFormat();  
        xmlFormat.setEncoding("UTF-8"); 
        // 设置换行 
        xmlFormat.setNewlines(true); 
        // 生成缩进 
        xmlFormat.setIndent(true); 
        // 使用4个空格进行缩进, 可以兼容文本编辑器 
        xmlFormat.setIndent("    "); 

        // lets write to a file
        try (FileWriter fileWiter = new FileWriter(url)) {
            XMLWriter writer = new XMLWriter(fileWiter,xmlFormat);
            writer.write(document);
            writer.close();
        }
    }
}
