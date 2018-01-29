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
 * 中金所申报
 * @author caojia
 *
 */
public class CFFEXDeclareService {
    
    static Logger logger = Logger.getLogger(CFFEXDeclareService.class);
    
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
        
        int investorNoIndex = 0;
        
        int moneyIndex = 0;
        
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
            
            if("投资者代码".equals(value)){
                investorNoIndex = j;
            }else if ("原始金额".equals(value)) {
                moneyIndex = j;
            }
        }
        
        
        Map<String, BigDecimal> bookMap = new HashMap<>();
        //总部资金
        BigDecimal headDep = BigDecimal.ZERO;
        for(int i =4 ; i < rows.size() ; i++){
            Element row = rows.get(i);
            
            List<Element> cells = row.elements("Cell");
            
            Element firstCell = cells.get(0).element("Data");
            String firstStr = firstCell.getText();
            if(firstStr.indexOf("总计") > -1){
                break;
            }
            
            //投资者代码
            Element investorCell = cells.get(investorNoIndex).element("Data");
            String investorNo = investorCell.getTextTrim();
            
            //金额
            Element moneyCell = cells.get(moneyIndex).element("Data");
            String money = moneyCell.getTextTrim().replaceAll(",", "");
            //投资者代码
            InvestorService investorService = new InvestorService();
            //根据投资者代码查询营业部名称
            String depName = deptService.getDepNameByName(investorService.getDepNameByInvestorNo(investorNo));
            //营业部属性
            BusinessDepartment businessDepartment = deptMap.get(depName);
            
            if(businessDepartment == null){
                throw new Exception("营业部信息未维护："+depName);
            }else {
                if("00-0002".equals(businessDepartment.getBookNo())){
                    //总部账簿
                    headDep.add(new BigDecimal(money));
                }else {
                    BigDecimal depBook = bookMap.get(businessDepartment.getBookNo());
                    if(depBook == null){
                        bookMap.put(businessDepartment.getBookNo(), new BigDecimal(money));
                    }else {
                        depBook.add(new BigDecimal(money));
                    }
                }
            }
        }
        
        Document outDoc = this.createDocument(headDep,bookMap , date);
        
        this.write(outDoc,targetPath);
        
    }
    
    
    public Document createDocument(BigDecimal headDep,Map<String, BigDecimal> bookMap ,String date) throws ParseException {
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
        
        //总部凭证
        {
            Element voucher = ufinterface.addElement("voucher");
            
            Element voucher_head = voucher.addElement("voucher_head");
            voucher_head.addElement("year").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
            //账簿号
            voucher_head.addElement("pk_accountingbook").setText("00-0002");
            
            voucher_head.addElement("period").setText(date);
            voucher_head.addElement("prepareddate").setText(preparedDate);
            voucher_head.addElement("pk_prepared").setText("yy01");
            voucher_head.addElement("pk_org").setText("00");
            voucher_head.addElement("pk_org_v").setText("00");
            voucher_head.addElement("pk_system").setText("GL");
            voucher_head.addElement("pk_vouchertype").setText("01");//凭证类别
            voucher_head.addElement("voucherkind").setText("0");
            voucher_head.addElement("discardflag").setText("N");
            voucher_head.addElement("attachment").setText("0");
            voucher_head.addElement("signflag").setText("N");
            
            Element details = voucher_head.addElement("details");
            //总部部门
            int i =1;
            {
                //应付货币保证金（2006）
                Element item = details.addElement("item");
                item.addElement("detailindex").setText(""+i++);
                item.addElement("explanation").setText("中金所申报费"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText(headDep.abs().setScale(2, RoundingMode.HALF_UP).toString());
                item.addElement("localdebitamount").setText(headDep.abs().setScale(2, RoundingMode.HALF_UP).toString());
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
            //总数
            BigDecimal total = headDep;
            //各营业部
            for (Entry<String, BigDecimal> m :bookMap.entrySet())  {
                
                String bookNo = m.getKey();
                BigDecimal depMoney = m.getValue();
                
                total.add(depMoney);
                
                Element item = details.addElement("item");
                item.addElement("detailindex").setText(""+i++);
                item.addElement("explanation").setText("中金所申报费"+date);
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText(depMoney.abs().setScale(2, RoundingMode.HALF_UP).toString());
                item.addElement("localdebitamount").setText(depMoney.abs().setScale(2, RoundingMode.HALF_UP).toString());
                item.addElement("accsubjcode").setText("22410101");//科目  应付货币保证金 
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
                ass.addElement("pk_Checkvalue").setText(bookNo.substring(0, 2));  //和营业部挂往来
            }
            {
                //贷方总和
                Element item = details.addElement("item");
                item.addElement("detailindex").setText(""+i++);
                item.addElement("explanation").setText("中金所申报费"+date);
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
                item.addElement("creditamount").setText(total.abs().setScale(2, RoundingMode.HALF_UP).toString());//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText(total.abs().setScale(2, RoundingMode.HALF_UP).toString());
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText("11240404");//
            }
            
        }
        
        //营业部凭证
        {
            for (Entry<String, BigDecimal> m :bookMap.entrySet())  {
                String bookNo = m.getKey();
                BigDecimal depMoney = m.getValue();
                Element voucher = ufinterface.addElement("voucher");
                
                Element voucher_head = voucher.addElement("voucher_head");
                voucher_head.addElement("year").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                //账簿号
                voucher_head.addElement("pk_accountingbook").setText(bookNo);
                
                voucher_head.addElement("period").setText(date);
                voucher_head.addElement("prepareddate").setText(preparedDate);
                voucher_head.addElement("pk_prepared").setText("yy01");
                voucher_head.addElement("pk_org").setText(bookNo.substring(0, 2));
                voucher_head.addElement("pk_org_v").setText(bookNo.substring(0, 2));
                voucher_head.addElement("pk_system").setText("GL");
                voucher_head.addElement("pk_vouchertype").setText("01");//凭证类别
                voucher_head.addElement("voucherkind").setText("0");
                voucher_head.addElement("discardflag").setText("N");
                voucher_head.addElement("attachment").setText("0");
                voucher_head.addElement("signflag").setText("N");
                
                Element details = voucher_head.addElement("details");
                {
                    //借方
                    Element item = details.addElement("item");
                    item.addElement("detailindex").setText("1");
                    item.addElement("explanation").setText("中金所申报费"+date);
                    item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                    item.addElement("debitamount").setText(depMoney.abs().setScale(2, RoundingMode.HALF_UP).toString());
                    item.addElement("localdebitamount").setText(depMoney.abs().setScale(2, RoundingMode.HALF_UP).toString());
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
                    //贷方
                    Element item = details.addElement("item");
                    item.addElement("detailindex").setText("2");
                    item.addElement("explanation").setText("中金所申报费"+date);
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
                    item.addElement("creditamount").setText(depMoney.abs().setScale(2, RoundingMode.HALF_UP).toString());//贷方金额
                    item.addElement("groupcreditamount").setText("0");
                    item.addElement("globalcreditamount").setText("0");
                    item.addElement("localcreditamount").setText(depMoney.abs().setScale(2, RoundingMode.HALF_UP).toString());
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
