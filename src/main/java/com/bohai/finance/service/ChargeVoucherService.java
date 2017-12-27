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
import com.bohai.finance.model.MarketDepCharge;
import com.bohai.finance.util.ApplicationConfig;
import com.bohai.finance.util.DateFormatterUtil;

/**
 * 手续费凭证文件生成
 * @author BHQH-CXYWB
 *
 */
public class ChargeVoucherService {
    
    static Logger logger = Logger.getLogger(ChargeVoucherService.class);
    
    
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
        //手续费列
        int chargeIndex = 0;
        //上交手续费列
        int handOnChargeIndex = 0;
        //留存手续费列
        int remainChargeIndex = 0;
        
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
            }else if ("手续费".equals(value)) {
                chargeIndex = j;
            }else if ("上交手续费".equals(value)) {
                handOnChargeIndex = j;
            }else if ("留存手续费".equals(value)) {
                remainChargeIndex = j;
            }
        }
        
        /**
         * 营业部手续费账簿
         */
        Map<String, MarketDepCharge> bookMap = new HashMap<String, MarketDepCharge>();
        /**
         * 总部账簿
         */
        HeadCharge headCharge = new HeadCharge();
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
                //手续费
                Element chargeCell = cells.get(chargeIndex).element("Data");
                String charge = chargeCell.getTextTrim().replaceAll(",", "");
                //上交手续费
                Element handOnChargeCell = cells.get(handOnChargeIndex).element("Data");
                String handOnCharge = handOnChargeCell.getTextTrim().replaceAll(",", "");
                //留存手续费
                Element remainChargeCell = cells.get(remainChargeIndex).element("Data");
                String remainCharge = remainChargeCell.getTextTrim().replaceAll(",", "");
                
                BusinessDepartment businessDepartment = deptMap.get(deptName);
                if(businessDepartment == null){
                    throw new Exception("营业部信息未维护："+deptName);
                }else {
                    
                    if("00-0002".equals(businessDepartment.getBookNo())){
                        //总部账簿
                        headCharge.addCharge(exchange, new BigDecimal(charge), new BigDecimal(remainCharge), new BigDecimal(handOnCharge));
                    }else {
                        //营业部账簿
                        MarketDepCharge marketDepCharge = bookMap.get(businessDepartment.getBookNo());
                        if(marketDepCharge == null){
                            MarketDepCharge depCharge = new MarketDepCharge();
                            depCharge.setBookNo(businessDepartment.getBookNo());
                            depCharge.setCharge(new BigDecimal(charge));
                            depCharge.setHandOnCharge(new BigDecimal(handOnCharge));
                            depCharge.setRemainCharge(new BigDecimal(remainCharge));
                            bookMap.put(depCharge.getBookNo(), depCharge);
                        }else {
                            marketDepCharge.setCharge(marketDepCharge.getCharge().add(new BigDecimal(charge)));
                            marketDepCharge.setHandOnCharge(marketDepCharge.getHandOnCharge().add(new BigDecimal(handOnCharge)));
                            marketDepCharge.setRemainCharge(marketDepCharge.getRemainCharge().add(new BigDecimal(remainCharge)));
                        }
                    }
                    
                }
        }
        
        Document outDoc = this.createDocument(headCharge,bookMap , date);
        
        this.write(outDoc,targetPath);
        
        
    }
    
    
    public Document createDocument(HeadCharge headCharge,Map<String, MarketDepCharge> bookMap ,String date) throws ParseException {
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
         * 总部手续费凭证
         */
        {
            Element voucher = ufinterface.addElement("voucher");
            
            Element voucher_head = voucher.addElement("voucher_head");
            voucher_head.addElement("year").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
            //账簿号
            voucher_head.addElement("pk_accountingbook").setText(headCharge.getBookNo());
            
            voucher_head.addElement("period").setText(date);
            voucher_head.addElement("prepareddate").setText(preparedDate);
            voucher_head.addElement("pk_prepared").setText("yy01");
            voucher_head.addElement("pk_org").setText(headCharge.getBookNo().substring(0, 2));
            voucher_head.addElement("pk_org_v").setText(headCharge.getBookNo().substring(0, 2));
            voucher_head.addElement("pk_system").setText("GL");
            voucher_head.addElement("pk_vouchertype").setText("01");//凭证类别
            voucher_head.addElement("voucherkind").setText("0");
            voucher_head.addElement("discardflag").setText("N");
            voucher_head.addElement("attachment").setText("0");
            voucher_head.addElement("signflag").setText("N");
            
            Element details = voucher_head.addElement("details");
            {
                //借方 手续费
                Element item = details.addElement("item");
                item.addElement("detailindex").setText("1");
                item.addElement("explanation").setText("手续费"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText(headCharge.getCharge().setScale(2, RoundingMode.HALF_UP).toString());
                item.addElement("localdebitamount").setText(headCharge.getCharge().setScale(2, RoundingMode.HALF_UP).toString());
                item.addElement("accsubjcode").setText("2006");//科目  应付货币保证金 
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
                //贷方 留存手续费
                Element item = details.addElement("item");
                item.addElement("detailindex").setText("2");
                item.addElement("explanation").setText("留存手续费"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText("0");
                item.addElement("localdebitamount").setText("0");
                item.addElement("accsubjcode").setText("60010101");//科目  交易手续费
                item.addElement("price").setText("0");//单价
                item.addElement("excrate2").setText("1");
                item.addElement("debitquantity").setText("0");//借方数量
                item.addElement("groupdebitamount").setText("0");
                item.addElement("globaldebitamount").setText("0");
                item.addElement("creditquantity").setText("0");//贷方数量
                item.addElement("creditamount").setText(headCharge.getRemain().setScale(2, RoundingMode.HALF_UP).toString());//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText(headCharge.getRemain().setScale(2, RoundingMode.HALF_UP).toString());
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText("60010101");//
            }
            {
                //贷方上交手续费大商所
                Element item = details.addElement("item");
                item.addElement("detailindex").setText("3");
                item.addElement("explanation").setText("上交手续费"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText("0");
                item.addElement("localdebitamount").setText("0");
                item.addElement("accsubjcode").setText("11240104");//科目  
                item.addElement("price").setText("0");//单价
                item.addElement("excrate2").setText("1");
                item.addElement("debitquantity").setText("0");//借方数量
                item.addElement("groupdebitamount").setText("0");
                item.addElement("globaldebitamount").setText("0");
                item.addElement("creditquantity").setText("0");//贷方数量
                item.addElement("creditamount").setText(headCharge.getDl().setScale(2, RoundingMode.HALF_UP).toString());//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText(headCharge.getDl().setScale(2, RoundingMode.HALF_UP).toString());
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText("11240104");//
            }
            {
                //贷方上交手续费上期所
                Element item = details.addElement("item");
                item.addElement("detailindex").setText("4");
                item.addElement("explanation").setText("上交手续费"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText("0");
                item.addElement("localdebitamount").setText("0");
                item.addElement("accsubjcode").setText("11240204");//科目  交易手续费
                item.addElement("price").setText("0");//单价
                item.addElement("excrate2").setText("1");
                item.addElement("debitquantity").setText("0");//借方数量
                item.addElement("groupdebitamount").setText("0");
                item.addElement("globaldebitamount").setText("0");
                item.addElement("creditquantity").setText("0");//贷方数量
                item.addElement("creditamount").setText(headCharge.getSh().setScale(2, RoundingMode.HALF_UP).toString());//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText(headCharge.getSh().setScale(2, RoundingMode.HALF_UP).toString());
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText("11240204");//
            }
            {
                //贷方上交手续费郑商所
                Element item = details.addElement("item");
                item.addElement("detailindex").setText("5");
                item.addElement("explanation").setText("上交手续费"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText("0");
                item.addElement("localdebitamount").setText("0");
                item.addElement("accsubjcode").setText("11240304");//科目  交易手续费
                item.addElement("price").setText("0");//单价
                item.addElement("excrate2").setText("1");
                item.addElement("debitquantity").setText("0");//借方数量
                item.addElement("groupdebitamount").setText("0");
                item.addElement("globaldebitamount").setText("0");
                item.addElement("creditquantity").setText("0");//贷方数量
                item.addElement("creditamount").setText(headCharge.getZz().setScale(2, RoundingMode.HALF_UP).toString());//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText(headCharge.getZz().setScale(2, RoundingMode.HALF_UP).toString());
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText("11240304");//
            }
            {
                //贷方上交手续费中金所
                Element item = details.addElement("item");
                item.addElement("detailindex").setText("6");
                item.addElement("explanation").setText("上交手续费"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText("0");
                item.addElement("localdebitamount").setText("0");
                item.addElement("accsubjcode").setText("11240404");//科目  交易手续费
                item.addElement("price").setText("0");//单价
                item.addElement("excrate2").setText("1");
                item.addElement("debitquantity").setText("0");//借方数量
                item.addElement("groupdebitamount").setText("0");
                item.addElement("globaldebitamount").setText("0");
                item.addElement("creditquantity").setText("0");//贷方数量
                item.addElement("creditamount").setText(headCharge.getZj().setScale(2, RoundingMode.HALF_UP).toString());//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText(headCharge.getZj().setScale(2, RoundingMode.HALF_UP).toString());
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText("11240404");//
            }
            
        }
        
        
        for (Entry<String, MarketDepCharge> m :bookMap.entrySet())  {
            
            
            MarketDepCharge depCharge = m.getValue();
            
            int i = 1;
            /**
             * 手续费凭证开始
             */
            Element voucher = ufinterface.addElement("voucher");
            
            Element voucher_head = voucher.addElement("voucher_head");
            voucher_head.addElement("year").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
            //账簿号
            voucher_head.addElement("pk_accountingbook").setText(depCharge.getBookNo());
            
            voucher_head.addElement("period").setText(date);
            voucher_head.addElement("prepareddate").setText(preparedDate);
            voucher_head.addElement("pk_prepared").setText("yy01");
            voucher_head.addElement("pk_org").setText(depCharge.getBookNo().substring(0, 2));
            voucher_head.addElement("pk_org_v").setText(depCharge.getBookNo().substring(0, 2));
            voucher_head.addElement("pk_system").setText("GL");
            voucher_head.addElement("pk_vouchertype").setText("01");//凭证类别
            voucher_head.addElement("voucherkind").setText("0");
            voucher_head.addElement("discardflag").setText("N");
            voucher_head.addElement("attachment").setText("0");
            voucher_head.addElement("signflag").setText("N");
            
            Element details = voucher_head.addElement("details");
            
            {
                //借方 手续费
                Element item = details.addElement("item");
                item.addElement("detailindex").setText(""+i++);
                item.addElement("explanation").setText("手续费"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText(depCharge.getCharge().setScale(2, RoundingMode.HALF_UP).toString());
                item.addElement("localdebitamount").setText(depCharge.getCharge().setScale(2, RoundingMode.HALF_UP).toString());
                item.addElement("accsubjcode").setText("2006");//科目  应付货币保证金 
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
                item.addElement("pk_accasoa").setText("2006");//TODO
            }
            
            
            {
                //贷方  留存手续费
                Element item = details.addElement("item");
                item.addElement("detailindex").setText(""+i++);
                item.addElement("explanation").setText("留存手续费"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText("0");
                item.addElement("localdebitamount").setText("0");
                item.addElement("accsubjcode").setText("60010101");//科目  交易手续费
                item.addElement("price").setText("0");//单价
                item.addElement("excrate2").setText("1");
                item.addElement("debitquantity").setText("0");//借方数量
                item.addElement("groupdebitamount").setText("0");
                item.addElement("globaldebitamount").setText("0");
                item.addElement("creditquantity").setText("0");//贷方数量
                item.addElement("creditamount").setText(depCharge.getRemainCharge().setScale(2, RoundingMode.HALF_UP).toString());//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText(depCharge.getRemainCharge().setScale(2, RoundingMode.HALF_UP).toString());
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText("60010101");//
            }
            
            {
                //上交手续费  和总部挂往来
                Element item = details.addElement("item");
                item.addElement("detailindex").setText(""+i++);
                item.addElement("explanation").setText("上交手续费"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText("0");
                item.addElement("localdebitamount").setText("0");
                item.addElement("accsubjcode").setText("22410101");//科目  应付货币保证金 
                item.addElement("price").setText("0");//单价
                item.addElement("excrate2").setText("1");
                item.addElement("debitquantity").setText("0");//借方数量
                item.addElement("groupdebitamount").setText("0");
                item.addElement("globaldebitamount").setText("0");
                item.addElement("creditquantity").setText("0");//贷方数量
                item.addElement("creditamount").setText(depCharge.getHandOnCharge().setScale(2, RoundingMode.HALF_UP).toString());//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText(depCharge.getHandOnCharge().setScale(2, RoundingMode.HALF_UP).toString());
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText("22410101");//
                Element ass = item.addElement("ass").addElement("item");
                ass.addElement("pk_Checktype").setText("0005"); //辅助核算项编码   内部客商
                ass.addElement("pk_Checkvalue").setText("00");  //和总部挂往来
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
