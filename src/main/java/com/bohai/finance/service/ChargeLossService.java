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
import com.bohai.finance.model.ExchangeProfit;
import com.bohai.finance.model.HandCharge;
import com.bohai.finance.model.MarketDepProfit;
import com.bohai.finance.util.ApplicationConfig;
import com.bohai.finance.util.DateFormatterUtil;

/**
 * 盈亏手续费凭证
 * @author xs
 *
 */
public class ChargeLossService {

    static Logger logger = Logger.getLogger(ChargeLossService.class);
    
    
    
public void chargeLoss(File infile, String targetPath, String date) throws Exception{
        
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
        //上交手续费
        int handOnChargeIndex = 0;
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
            } else if ("交易所代码".equals(value)) {
                exchangeIndex = j;
            } else if ("盈亏".equals(value)) {
                profitIndex = j;
            } else if ("上交手续费".equals(value)) {
            	handOnChargeIndex = j;
            }
        }
        
        /**
         * 营业部盈亏账簿
         */
		Map<String, MarketDepProfit> bookMap = new HashMap<String, MarketDepProfit>();
        
        //其它客户盈亏手续费(上交手续费)
        HandCharge handCharge = new HandCharge();
        //其它客户盈亏手续费(盈亏)
        ExchangeProfit exchangeProfit = new ExchangeProfit();
        
		for (int i = 4; i < rows.size(); i++) {

			Element row = rows.get(i);

			List<Element> cells = row.elements("Cell");

			Element firstCell = cells.get(0).element("Data");
			String firstStr = firstCell.getText();
			if (firstStr.indexOf("总计") > -1) {
				break;
			}

			// 营业部名称
			Element deptCell = cells.get(depIndex).element("Data");
			String deptName = deptCell.getTextTrim();
			// 交易所代码
			Element exchangeCell = cells.get(exchangeIndex).element("Data");
			String exchange = exchangeCell.getTextTrim();
			// 盈亏
			Element profitCell = cells.get(profitIndex).element("Data");
			String profit = profitCell.getTextTrim().replaceAll(",", "");
			// 上交手续费
			Element handOnChargeCell = cells.get(handOnChargeIndex).element("Data");
			String handOnCharge = handOnChargeCell.getTextTrim().replaceAll(",", "");

			BusinessDepartment businessDepartment = deptMap.get(deptName);
			if (businessDepartment == null) {
				throw new Exception("营业部信息未维护：" + deptName);
			} else {
				
				if(!"00-0002".equals(businessDepartment.getBookNo())){
					//其它客户盈亏手续费(上交手续费)
					handCharge.addCharge(exchange, new BigDecimal(handOnCharge));
					//其它客户盈亏手续费(盈亏)
					exchangeProfit.addProfit(exchange, new BigDecimal(profit));
				}
				//营业部盈亏
				MarketDepProfit marketDepProfit = bookMap.get(businessDepartment.getBookNo());
				
				if (marketDepProfit == null) {
					MarketDepProfit depProfit = new MarketDepProfit();
					depProfit.setBookNo(businessDepartment.getBookNo());
					depProfit.setTotalProfit(new BigDecimal(profit).subtract(new BigDecimal(handOnCharge)));
					bookMap.put(depProfit.getBookNo(), depProfit);
				} else {
					marketDepProfit.setTotalProfit(marketDepProfit.getTotalProfit()
							.add(new BigDecimal(profit).subtract(new BigDecimal(handOnCharge))));
				}
			}
		}
        
        Document outDoc = this.createDocument(handCharge, exchangeProfit, bookMap , date);
        
        this.write(outDoc,targetPath);
        
        
    }

	public Document createDocument(HandCharge handCharge, ExchangeProfit exchangeProfit,
			Map<String, MarketDepProfit> bookMap, String date) throws ParseException {
		
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
		Element voucher = ufinterface.addElement("voucher");

		Element voucher_head = voucher.addElement("voucher_head");
		voucher_head.addElement("year").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
		// 账簿号
		voucher_head.addElement("pk_accountingbook").setText("00-0002");

		voucher_head.addElement("period").setText(date);
		voucher_head.addElement("prepareddate").setText(preparedDate);
		voucher_head.addElement("pk_prepared").setText("yy01");
		voucher_head.addElement("pk_org").setText("00");
		voucher_head.addElement("pk_org_v").setText("00");
		voucher_head.addElement("pk_system").setText("GL");
		voucher_head.addElement("pk_vouchertype").setText("01");// 凭证类别
		voucher_head.addElement("voucherkind").setText("0");
		voucher_head.addElement("discardflag").setText("N");
		voucher_head.addElement("attachment").setText("0");
		voucher_head.addElement("signflag").setText("N");
		Element details = voucher_head.addElement("details");
		//大商所（上交手续费）
		Element item = details.addElement("item");
		item.addElement("detailindex").setText("1");
		item.addElement("explanation").setText("其它客户盈亏手续费" + date);
		item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
		// 借方金额
		item.addElement("debitamount").setText(
				handCharge.getHanddl().setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(-1)).toString()); 
		// 借方金额
		item.addElement("localdebitamount").setText(
				handCharge.getHanddl().setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(-1)).toString());
		item.addElement("accsubjcode").setText("11240104");// 科目
		item.addElement("price").setText("0");// 单价
		item.addElement("excrate2").setText("1");
		item.addElement("debitquantity").setText("0");
		item.addElement("groupdebitamount").setText("0");
		item.addElement("globaldebitamount").setText("0");
		item.addElement("creditquantity").setText("0");
		// 贷方金额
		item.addElement("creditamount").setText("0");
		item.addElement("groupcreditamount").setText("0");
		item.addElement("globalcreditamount").setText("0");
		// 贷方金额
		item.addElement("localcreditamount").setText("0");
		item.addElement("pk_currtype").setText("CNY");
		item.addElement("pk_accasoa").setText("11240104");
		
		//郑州商所（上交手续费）
		Element item1 = details.addElement("item");
		item1.addElement("detailindex").setText("2");
		item1.addElement("explanation").setText("其它客户盈亏手续费" + date);
		item1.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
		// 借方金额
		item1.addElement("debitamount").setText(
				handCharge.getHandzz().setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(-1)).toString()); 
		// 借方金额
		item1.addElement("localdebitamount").setText(
				handCharge.getHandzz().setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(-1)).toString());
		item1.addElement("accsubjcode").setText("11240304");// 科目
		item1.addElement("price").setText("0");// 单价
		item1.addElement("excrate2").setText("1");
		item1.addElement("debitquantity").setText("0");
		item1.addElement("groupdebitamount").setText("0");
		item1.addElement("globaldebitamount").setText("0");
		item1.addElement("creditquantity").setText("0");
		// 贷方金额
		item1.addElement("creditamount").setText("0");
		item1.addElement("groupcreditamount").setText("0");
		item1.addElement("globalcreditamount").setText("0");
		// 贷方金额
		item1.addElement("localcreditamount").setText("0");
		item1.addElement("pk_currtype").setText("CNY");
		item1.addElement("pk_accasoa").setText("11240304");
		
		//中金所（上交手续费）
		Element item2 = details.addElement("item");
		item2.addElement("detailindex").setText("3");
		item2.addElement("explanation").setText("其它客户盈亏手续费" + date);
		item2.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
		// 借方金额
		item2.addElement("debitamount").setText(
				handCharge.getHandzj().setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(-1)).toString()); 
		// 借方金额
		item2.addElement("localdebitamount").setText(
				handCharge.getHandzj().setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(-1)).toString());
		item2.addElement("accsubjcode").setText("11240404");// 科目
		item2.addElement("price").setText("0");// 单价
		item2.addElement("excrate2").setText("1");
		item2.addElement("debitquantity").setText("0");
		item2.addElement("groupdebitamount").setText("0");
		item2.addElement("globaldebitamount").setText("0");
		item2.addElement("creditquantity").setText("0");
		// 贷方金额
		item2.addElement("creditamount").setText("0");
		item2.addElement("groupcreditamount").setText("0");
		item2.addElement("globalcreditamount").setText("0");
		// 贷方金额
		item2.addElement("localcreditamount").setText("0");
		item2.addElement("pk_currtype").setText("CNY");
		item2.addElement("pk_accasoa").setText("11240404");
		
		//上交所（上交手续费）
		Element item3 = details.addElement("item");
		item3.addElement("detailindex").setText("4");
		item3.addElement("explanation").setText("其它客户盈亏手续费" + date);
		item3.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
		// 借方金额
		item3.addElement("debitamount").setText(
				handCharge.getHandsh().setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(-1)).toString()); 
		// 借方金额
		item3.addElement("localdebitamount").setText(
				handCharge.getHandsh().setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(-1)).toString());
		item3.addElement("accsubjcode").setText("11240204");// 科目
		item3.addElement("price").setText("0");// 单价
		item3.addElement("excrate2").setText("1");
		item3.addElement("debitquantity").setText("0");
		item3.addElement("groupdebitamount").setText("0");
		item3.addElement("globaldebitamount").setText("0");
		item3.addElement("creditquantity").setText("0");
		// 贷方金额
		item3.addElement("creditamount").setText("0");
		item3.addElement("groupcreditamount").setText("0");
		item3.addElement("globalcreditamount").setText("0");
		// 贷方金额
		item3.addElement("localcreditamount").setText("0");
		item3.addElement("pk_currtype").setText("CNY");
		item3.addElement("pk_accasoa").setText("11240204");

		int iCount = 5;
        //营业部盈亏手续费凭证
        for (Entry<String, MarketDepProfit> m :bookMap.entrySet())  {
        	
        	if("00-0002".equals(m.getValue().getBookNo())){
        		continue;
        	}
            MarketDepProfit profit = m.getValue();
            if(profit.getTotalProfit().compareTo(BigDecimal.ZERO) > 0) {
            	
        		Element itemMx = details.addElement("item");
        		itemMx.addElement("detailindex").setText(String.valueOf(iCount++));
        		itemMx.addElement("explanation").setText("其它客户盈亏手续费" + date);
        		itemMx.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
        		// 借方金额
        		itemMx.addElement("debitamount").setText("0"); 
        		// 借方金额
        		itemMx.addElement("localdebitamount").setText("0");
        		itemMx.addElement("accsubjcode").setText("22410101");// 科目
        		itemMx.addElement("price").setText("0");// 单价
        		itemMx.addElement("excrate2").setText("1");
        		itemMx.addElement("debitquantity").setText("0");
        		itemMx.addElement("groupdebitamount").setText("0");
        		itemMx.addElement("globaldebitamount").setText("0");
        		itemMx.addElement("creditquantity").setText("0");
        		// 贷方金额
        		itemMx.addElement("creditamount").setText(profit.getTotalProfit().setScale(2, RoundingMode.HALF_UP).abs().toString());
        		itemMx.addElement("groupcreditamount").setText("0");
        		itemMx.addElement("globalcreditamount").setText("0");
        		// 贷方金额
        		itemMx.addElement("localcreditamount").setText(profit.getTotalProfit().setScale(2, RoundingMode.HALF_UP).abs().toString());
        		itemMx.addElement("pk_currtype").setText("CNY");
        		itemMx.addElement("pk_accasoa").setText("22410101");
        		Element ass = itemMx.addElement("ass").addElement("item");
                ass.addElement("pk_Checktype").setText("0005"); //辅助核算项编码   内部客商
                ass.addElement("pk_Checkvalue").setText(m.getValue().getBookNo().substring(0, 2));  //和总部挂往来
        		
            } else {
            	
        		Element itemMx = details.addElement("item");
        		itemMx.addElement("detailindex").setText(String.valueOf(iCount++));
        		itemMx.addElement("explanation").setText("其它客户盈亏手续费" + date);
        		itemMx.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
        		// 借方金额
        		itemMx.addElement("debitamount").setText(profit.getTotalProfit().setScale(2, RoundingMode.HALF_UP).abs().toString()); 
        		// 借方金额
        		itemMx.addElement("localdebitamount").setText(profit.getTotalProfit().setScale(2, RoundingMode.HALF_UP).abs().toString());
        		itemMx.addElement("accsubjcode").setText("22410101");// 科目
        		itemMx.addElement("price").setText("0");// 单价
        		itemMx.addElement("excrate2").setText("1");
        		itemMx.addElement("debitquantity").setText("0");
        		itemMx.addElement("groupdebitamount").setText("0");
        		itemMx.addElement("globaldebitamount").setText("0");
        		itemMx.addElement("creditquantity").setText("0");
        		// 贷方金额
        		itemMx.addElement("creditamount").setText("0");
        		itemMx.addElement("groupcreditamount").setText("0");
        		itemMx.addElement("globalcreditamount").setText("0");
        		// 贷方金额
        		itemMx.addElement("localcreditamount").setText("0");
        		itemMx.addElement("pk_currtype").setText("CNY");
        		itemMx.addElement("pk_accasoa").setText("22410101");
        		Element ass = itemMx.addElement("ass").addElement("item");
                ass.addElement("pk_Checktype").setText("0005"); //辅助核算项编码   内部客商
                ass.addElement("pk_Checkvalue").setText(m.getValue().getBookNo().substring(0, 2));  //和总部挂往来
            }
        }
        
        //上交所（盈亏）
        if (exchangeProfit.getSh().compareTo(BigDecimal.ZERO) > 0) {
    		
    		Element itemSh = details.addElement("item");
    		itemSh.addElement("detailindex").setText(String.valueOf(iCount++));
    		itemSh.addElement("explanation").setText("其它客户盈亏手续费" + date);
    		itemSh.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
    		// 借方金额
    		itemSh.addElement("debitamount").setText(
    				exchangeProfit.getSh().setScale(2, RoundingMode.HALF_UP).abs().toString()); 
    		// 借方金额
    		itemSh.addElement("localdebitamount").setText(
    				exchangeProfit.getSh().setScale(2, RoundingMode.HALF_UP).abs().toString());
    		itemSh.addElement("accsubjcode").setText("11240202");// 科目
    		itemSh.addElement("price").setText("0");// 单价
    		itemSh.addElement("excrate2").setText("1");
    		itemSh.addElement("debitquantity").setText("0");
    		itemSh.addElement("groupdebitamount").setText("0");
    		itemSh.addElement("globaldebitamount").setText("0");
    		itemSh.addElement("creditquantity").setText("0");
    		// 贷方金额
    		itemSh.addElement("creditamount").setText("0");
    		itemSh.addElement("groupcreditamount").setText("0");
    		itemSh.addElement("globalcreditamount").setText("0");
    		// 贷方金额
    		itemSh.addElement("localcreditamount").setText("0");
    		itemSh.addElement("pk_currtype").setText("CNY");
    		itemSh.addElement("pk_accasoa").setText("11240202");
        } else {
        	
    		Element itemSh = details.addElement("item");
    		itemSh.addElement("detailindex").setText(String.valueOf(iCount++));
    		itemSh.addElement("explanation").setText("其它客户盈亏手续费" + date);
    		itemSh.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
    		// 借方金额
    		itemSh.addElement("debitamount").setText("0"); 
    		// 借方金额
    		itemSh.addElement("localdebitamount").setText("0");
    		itemSh.addElement("accsubjcode").setText("11240202");// 科目
    		itemSh.addElement("price").setText("0");// 单价
    		itemSh.addElement("excrate2").setText("1");
    		itemSh.addElement("debitquantity").setText("0");
    		itemSh.addElement("groupdebitamount").setText("0");
    		itemSh.addElement("globaldebitamount").setText("0");
    		itemSh.addElement("creditquantity").setText("0");
    		// 贷方金额
    		itemSh.addElement("creditamount").setText(exchangeProfit.getSh().setScale(2, RoundingMode.HALF_UP).abs().toString());
    		itemSh.addElement("groupcreditamount").setText("0");
    		itemSh.addElement("globalcreditamount").setText("0");
    		// 贷方金额
    		itemSh.addElement("localcreditamount").setText(exchangeProfit.getSh().setScale(2, RoundingMode.HALF_UP).abs().toString());
    		itemSh.addElement("pk_currtype").setText("CNY");
    		itemSh.addElement("pk_accasoa").setText("11240202");
        }
        
        //郑商所（盈亏）
        if (exchangeProfit.getZz().compareTo(BigDecimal.ZERO) > 0) {
    		
    		Element itemZz = details.addElement("item");
    		itemZz.addElement("detailindex").setText(String.valueOf(iCount++));
    		itemZz.addElement("explanation").setText("其它客户盈亏手续费" + date);
    		itemZz.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
    		// 借方金额
    		itemZz.addElement("debitamount").setText(
    				exchangeProfit.getZz().setScale(2, RoundingMode.HALF_UP).abs().toString()); 
    		// 借方金额
    		itemZz.addElement("localdebitamount").setText(
    				exchangeProfit.getZz().setScale(2, RoundingMode.HALF_UP).abs().toString());
    		itemZz.addElement("accsubjcode").setText("11240302");// 科目
    		itemZz.addElement("price").setText("0");// 单价
    		itemZz.addElement("excrate2").setText("1");
    		itemZz.addElement("debitquantity").setText("0");
    		itemZz.addElement("groupdebitamount").setText("0");
    		itemZz.addElement("globaldebitamount").setText("0");
    		itemZz.addElement("creditquantity").setText("0");
    		// 贷方金额
    		itemZz.addElement("creditamount").setText("0");
    		itemZz.addElement("groupcreditamount").setText("0");
    		itemZz.addElement("globalcreditamount").setText("0");
    		// 贷方金额
    		itemZz.addElement("localcreditamount").setText("0");
    		itemZz.addElement("pk_currtype").setText("CNY");
    		itemZz.addElement("pk_accasoa").setText("11240302");
        } else {
        	
    		Element itemZz = details.addElement("item");
    		itemZz.addElement("detailindex").setText(String.valueOf(iCount++));
    		itemZz.addElement("explanation").setText("其它客户盈亏手续费" + date);
    		itemZz.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
    		// 借方金额
    		itemZz.addElement("debitamount").setText("0"); 
    		// 借方金额
    		itemZz.addElement("localdebitamount").setText("0");
    		itemZz.addElement("accsubjcode").setText("11240302");// 科目
    		itemZz.addElement("price").setText("0");// 单价
    		itemZz.addElement("excrate2").setText("1");
    		itemZz.addElement("debitquantity").setText("0");
    		itemZz.addElement("groupdebitamount").setText("0");
    		itemZz.addElement("globaldebitamount").setText("0");
    		itemZz.addElement("creditquantity").setText("0");
    		// 贷方金额
    		itemZz.addElement("creditamount").setText(exchangeProfit.getZz().setScale(2, RoundingMode.HALF_UP).abs().toString());
    		itemZz.addElement("groupcreditamount").setText("0");
    		itemZz.addElement("globalcreditamount").setText("0");
    		// 贷方金额
    		itemZz.addElement("localcreditamount").setText(exchangeProfit.getZz().setScale(2, RoundingMode.HALF_UP).abs().toString());
    		itemZz.addElement("pk_currtype").setText("CNY");
    		itemZz.addElement("pk_accasoa").setText("11240302");
        }
        
        //大连（盈亏）
        if (exchangeProfit.getDl().compareTo(BigDecimal.ZERO) > 0) {
    		
    		Element itemDl = details.addElement("item");
    		itemDl.addElement("detailindex").setText(String.valueOf(iCount++));
    		itemDl.addElement("explanation").setText("其它客户盈亏手续费" + date);
    		itemDl.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
    		// 借方金额
    		itemDl.addElement("debitamount").setText(
    				exchangeProfit.getDl().setScale(2, RoundingMode.HALF_UP).abs().toString()); 
    		// 借方金额
    		itemDl.addElement("localdebitamount").setText(
    				exchangeProfit.getDl().setScale(2, RoundingMode.HALF_UP).abs().toString());
    		itemDl.addElement("accsubjcode").setText("11240102");// 科目
    		itemDl.addElement("price").setText("0");// 单价
    		itemDl.addElement("excrate2").setText("1");
    		itemDl.addElement("debitquantity").setText("0");
    		itemDl.addElement("groupdebitamount").setText("0");
    		itemDl.addElement("globaldebitamount").setText("0");
    		itemDl.addElement("creditquantity").setText("0");
    		// 贷方金额
    		itemDl.addElement("creditamount").setText("0");
    		itemDl.addElement("groupcreditamount").setText("0");
    		itemDl.addElement("globalcreditamount").setText("0");
    		// 贷方金额
    		itemDl.addElement("localcreditamount").setText("0");
    		itemDl.addElement("pk_currtype").setText("CNY");
    		itemDl.addElement("pk_accasoa").setText("11240102");
        } else {
        	
    		Element itemDl = details.addElement("item");
    		itemDl.addElement("detailindex").setText(String.valueOf(iCount++));
    		itemDl.addElement("explanation").setText("其它客户盈亏手续费" + date);
    		itemDl.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
    		// 借方金额
    		itemDl.addElement("debitamount").setText("0"); 
    		// 借方金额
    		itemDl.addElement("localdebitamount").setText("0");
    		itemDl.addElement("accsubjcode").setText("11240102");// 科目
    		itemDl.addElement("price").setText("0");// 单价
    		itemDl.addElement("excrate2").setText("1");
    		itemDl.addElement("debitquantity").setText("0");
    		itemDl.addElement("groupdebitamount").setText("0");
    		itemDl.addElement("globaldebitamount").setText("0");
    		itemDl.addElement("creditquantity").setText("0");
    		// 贷方金额
    		itemDl.addElement("creditamount").setText(exchangeProfit.getDl().setScale(2, RoundingMode.HALF_UP).abs().toString());
    		itemDl.addElement("groupcreditamount").setText("0");
    		itemDl.addElement("globalcreditamount").setText("0");
    		// 贷方金额
    		itemDl.addElement("localcreditamount").setText(exchangeProfit.getDl().setScale(2, RoundingMode.HALF_UP).abs().toString());
    		itemDl.addElement("pk_currtype").setText("CNY");
    		itemDl.addElement("pk_accasoa").setText("11240102");
        }

        //中金所（盈亏）
        if (exchangeProfit.getZj().compareTo(BigDecimal.ZERO) > 0) {
    		
    		Element itemZj = details.addElement("item");
    		itemZj.addElement("detailindex").setText(String.valueOf(iCount++));
    		itemZj.addElement("explanation").setText("其它客户盈亏手续费" + date);
    		itemZj.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
    		// 借方金额
    		itemZj.addElement("debitamount").setText(
    				exchangeProfit.getZj().setScale(2, RoundingMode.HALF_UP).abs().toString()); 
    		// 借方金额
    		itemZj.addElement("localdebitamount").setText(
    				exchangeProfit.getZj().setScale(2, RoundingMode.HALF_UP).abs().toString());
    		itemZj.addElement("accsubjcode").setText("11240402");// 科目
    		itemZj.addElement("price").setText("0");// 单价
    		itemZj.addElement("excrate2").setText("1");
    		itemZj.addElement("debitquantity").setText("0");
    		itemZj.addElement("groupdebitamount").setText("0");
    		itemZj.addElement("globaldebitamount").setText("0");
    		itemZj.addElement("creditquantity").setText("0");
    		// 贷方金额
    		itemZj.addElement("creditamount").setText("0");
    		itemZj.addElement("groupcreditamount").setText("0");
    		itemZj.addElement("globalcreditamount").setText("0");
    		// 贷方金额
    		itemZj.addElement("localcreditamount").setText("0");
    		itemZj.addElement("pk_currtype").setText("CNY");
    		itemZj.addElement("pk_accasoa").setText("11240402");
        } else {
        	
    		Element itemZj = details.addElement("item");
    		itemZj.addElement("detailindex").setText(String.valueOf(iCount++));
    		itemZj.addElement("explanation").setText("其它客户盈亏手续费" + date);
    		itemZj.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
    		// 借方金额
    		itemZj.addElement("debitamount").setText("0"); 
    		// 借方金额
    		itemZj.addElement("localdebitamount").setText("0");
    		itemZj.addElement("accsubjcode").setText("11240402");// 科目
    		itemZj.addElement("price").setText("0");// 单价
    		itemZj.addElement("excrate2").setText("1");
    		itemZj.addElement("debitquantity").setText("0");
    		itemZj.addElement("groupdebitamount").setText("0");
    		itemZj.addElement("globaldebitamount").setText("0");
    		itemZj.addElement("creditquantity").setText("0");
    		// 贷方金额
    		itemZj.addElement("creditamount").setText(exchangeProfit.getZj().setScale(2, RoundingMode.HALF_UP).abs().toString());
    		itemZj.addElement("groupcreditamount").setText("0");
    		itemZj.addElement("globalcreditamount").setText("0");
    		// 贷方金额
    		itemZj.addElement("localcreditamount").setText(exchangeProfit.getZj().setScale(2, RoundingMode.HALF_UP).abs().toString());
    		itemZj.addElement("pk_currtype").setText("CNY");
    		itemZj.addElement("pk_accasoa").setText("11240402");
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
