package com.bohai.finance.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bohai.finance.model.Bank;
import com.bohai.finance.model.BusinessDepartment;
import com.bohai.finance.model.Headquarters;
import com.bohai.finance.util.DateFormatterUtil;

/**
 * 生成凭证类
 * @author BHQH-CXYWB
 *
 */
public class VoucherService {

    static final BigDecimal ZERO = new BigDecimal("0");
    
    /**
     * 解析xml文件
     * @param url 文件路径
     * @return  Document
     * @throws DocumentException
     * @throws FileNotFoundException
     */
    public Document parse(String url) throws DocumentException, FileNotFoundException {
        
        InputStream inputStream = new FileInputStream(new File(url));
        
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
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
    
    public Document parse(URL url) throws DocumentException, FileNotFoundException {
        
        SAXReader reader = new SAXReader();
        Document document = reader.read(url);
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
    
    
    public Map<String,Bank> generateXML(File infile, String targetPath) throws Exception{
        
        Map<String, Bank> bankMap= new HashMap<String, Bank>();
        BankService bankService = new BankService();
        List<Bank> banks = bankService.queryBanks();
        if(banks != null){
            for (Bank bank : banks) {
                
                bank.setIn(ZERO);
                bank.setOut(ZERO);
                bankMap.put(bank.getBankName(), bank);
            }
        }
        
        Map<String, BusinessDepartment> deptMap= new HashMap<String, BusinessDepartment>();
        DeptService deptService = new DeptService();
        List<BusinessDepartment> depts = deptService.queryDepts();
        if(depts !=null){
            for (BusinessDepartment businessDepartment : depts) {
                businessDepartment.setIn(ZERO);
                businessDepartment.setOut(ZERO);
                deptMap.put(businessDepartment.getDeptName(), businessDepartment);
                
            }
        }
        
        
        int dateIndex = 0;
        
        int depIndex = 0;
        
        int bankIndex = 0;
        
        int inIndex = 0;
        
        int outIndex = 0;
        
        Document document = this.parse(infile);
        
        Element root = document.getRootElement();
        
        Element workSheet = root.element("Worksheet");
        
        Element table = workSheet.element("Table");
        
        List<Element> rows = table.elements("Row");
        
        //获取标题索引
        Element head = rows.get(2);
        List<Element> headCells = head.elements("Cell");
        for(int j = 0 ; j < headCells.size() ; j++){
            
            Element cell = headCells.get(j);
            Element data = cell.element("Data");
            
            String value = data.getTextTrim();
            
            if("交易日".equals(value)){
                dateIndex = j;
            }else if ("银行".equals(value)) {
                bankIndex = j;
            }else if ("入金".equals(value)) {
                inIndex = j;
            }else if ("出金".equals(value)) {
                outIndex = j;
            }else if ("投资者名称".equals(value)) {
                depIndex = j;
            }
            
        }
        
        //以银行为维度
        for(int i =3 ; i < rows.size() ; i++){
            
            Element row = rows.get(i);
            
            List<Element> cells = row.elements("Cell");
            
                Element firstCell = cells.get(0).element("Data");
                String firstStr = firstCell.getText();
                if(firstStr.indexOf("总计") > -1){
                    break;
                }
                
                Element dateCell = cells.get(dateIndex).element("Data");
                String dateStr = dateCell.getTextTrim();
                
                Element bankCell = cells.get(bankIndex).element("Data");
                String bankName = bankCell.getTextTrim();
                Element inCell = cells.get(inIndex).element("Data");
                String in = inCell.getTextTrim().replaceAll(",", "");
                Element outCell = cells.get(outIndex).element("Data");
                String out = outCell.getTextTrim().replaceAll(",", "");
                
                Bank bank = bankMap.get(bankName);
                if(bank == null){
                    throw new Exception("银行信息未维护");
                }else {
                    bank.setIn(bank.getIn().add(new BigDecimal(in)));
                    bank.setOut(bank.getOut().add(new BigDecimal(out)));
                    bank.setDate(dateStr);
                }
                
        }
        
        //以营业部为维度
        for(int i =3 ; i < rows.size() ; i++){
            
            Element row = rows.get(i);
            
            List<Element> cells = row.elements("Cell");
            
                Element firstCell = cells.get(0).element("Data");
                String firstStr = firstCell.getText();
                if(firstStr.indexOf("总计") > -1){
                    break;
                }
            
                Element dateCell = cells.get(dateIndex).element("Data");
                String dateStr = dateCell.getTextTrim();
                Element deptCell = cells.get(depIndex).element("Data");
                String deptName = deptCell.getTextTrim();
                Element inCell = cells.get(inIndex).element("Data");
                String in = inCell.getTextTrim().replaceAll(",", "");
                Element outCell = cells.get(outIndex).element("Data");
                String out = outCell.getTextTrim().replaceAll(",", "");
                
                BusinessDepartment businessDepartment = deptMap.get(deptName);
                if(businessDepartment == null){
                    throw new Exception("营业部信息未维护");
                }else {
                    businessDepartment.setDate(dateStr);
                    businessDepartment.setIn(businessDepartment.getIn().add(new BigDecimal(in)));
                    businessDepartment.setOut(businessDepartment.getOut().add(new BigDecimal(out)));
                }
                
        }
        
        Document outDoc = this.createDocument(bankMap, deptMap);
        this.write(outDoc,targetPath);
        
        return bankMap;
    }
    
    
    
    public Document createDocument(Map<String, Bank> bankMap, Map<String, BusinessDepartment> deptMap) {
        Document document = DocumentHelper.createDocument();
        Element ufinterface = document.addElement("ufinterface")
                .addAttribute("account", "002")
                .addAttribute("billtype", "vouchergl")
                .addAttribute("businessunitcode", "develop")
                .addAttribute("filename", "")
                .addAttribute("groupcode", "01")
                .addAttribute("isexchange", "Y")
                .addAttribute("orgcode", "00")
                .addAttribute("receiver", "")
                .addAttribute("replace", "Y")
                .addAttribute("roottag", "")
                .addAttribute("sender", "NC_OA");
                
        
        int i = 1;
        /**
         * 入金凭证开始
         */
        Element voucherIn = ufinterface.addElement("voucher");
        
        Element voucher_headIn = voucherIn.addElement("voucher_head");
        voucher_headIn.addElement("year").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
        voucher_headIn.addElement("pk_accountingbook").setText("00-0002");
        voucher_headIn.addElement("period").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
        voucher_headIn.addElement("prepareddate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
        voucher_headIn.addElement("pk_prepared").setText("yy01");
        voucher_headIn.addElement("pk_org").setText("00");
        voucher_headIn.addElement("pk_org_v").setText("00");
        voucher_headIn.addElement("pk_system").setText("GL");
        voucher_headIn.addElement("voucherkind").setText("0");
        voucher_headIn.addElement("discardflag").setText("N");
        voucher_headIn.addElement("attachment").setText("0");
        voucher_headIn.addElement("signflag").setText("N");
        
        Element detailsIn = voucher_headIn.addElement("details");
        
        for (Entry<String, Bank> m :bankMap.entrySet())  {
            
            Bank bank = m.getValue();
            if(bank.getIn() != null && bank.getIn().compareTo(ZERO) > 0){
                Element item = detailsIn.addElement("item");
                item.addElement("detailindex").setText(""+i++);
                item.addElement("explanation").setText("银期转账（入）"+bank.getDate()+bank.getBankName());
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText(bank.getIn().toString());
                item.addElement("localdebitamount").setText(bank.getIn().toString());
                item.addElement("accsubjcode").setText(bank.getSubjectCode());//科目 
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
                item.addElement("pk_accasoa").setText(bank.getSubjectCode());//TODO
                Element ass = item.addElement("ass").addElement("item");
                ass.addElement("pk_Checktype").setText("0011"); //银行账户
                ass.addElement("pk_Checkvalue").setText(bank.getAccountNo());
            }
            
        }
        
        Headquarters head = new Headquarters();
        head.setSubjectCode("2006");
        head.setDescription("银期转账（入）");
        head.setIn(ZERO);
        head.setOut(ZERO);
        head.setAssCode("0005");
        head.setAssValue("00");
        String date = "";
        
        for (Entry<String, BusinessDepartment> m :deptMap.entrySet())  {
            
            BusinessDepartment dept = m.getValue();
            date = dept.getDate(); 
            if(dept.getSubjectCode().equals("2006")){
                head.setIn(head.getIn().add(dept.getIn()));
                head.setOut(head.getOut().add(dept.getOut()));
                
            }else if(dept.getIn() != null && dept.getIn().compareTo(ZERO) > 0){
                Element item = detailsIn.addElement("item");
                item.addElement("detailindex").setText(""+i++);
                item.addElement("explanation").setText("银期转账（入）"+dept.getDate());
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText("0");
                item.addElement("localdebitamount").setText("0");
                item.addElement("accsubjcode").setText(dept.getSubjectCode());//科目 
                item.addElement("price").setText("0");//单价
                item.addElement("excrate2").setText("1");
                item.addElement("debitquantity").setText("0");//借方数量
                item.addElement("groupdebitamount").setText("0");
                item.addElement("globaldebitamount").setText("0");
                item.addElement("creditquantity").setText("0");//贷方数量
                item.addElement("creditamount").setText(dept.getIn().toString());//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText(dept.getIn().toString());
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText(dept.getSubjectCode());//TODO
                Element ass = item.addElement("ass").addElement("item");
                ass.addElement("pk_Checktype").setText(dept.getAssCode()); //银行账户
                ass.addElement("pk_Checkvalue").setText(dept.getAssValue());
            }
        }
        
        Element itemIn = detailsIn.addElement("item");
        itemIn.addElement("detailindex").setText(""+i++);
        itemIn.addElement("explanation").setText(head.getDescription()+date);
        itemIn.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
        itemIn.addElement("debitamount").setText("0");
        itemIn.addElement("localdebitamount").setText("0");
        itemIn.addElement("accsubjcode").setText(head.getSubjectCode());//科目 
        itemIn.addElement("price").setText("0");//单价
        itemIn.addElement("excrate2").setText("1");
        itemIn.addElement("debitquantity").setText("0");//借方数量
        itemIn.addElement("groupdebitamount").setText("0");
        itemIn.addElement("globaldebitamount").setText("0");
        itemIn.addElement("creditquantity").setText("0");//贷方数量
        itemIn.addElement("creditamount").setText(head.getIn().toString());//贷方金额
        itemIn.addElement("groupcreditamount").setText("0");
        itemIn.addElement("globalcreditamount").setText("0");
        itemIn.addElement("localcreditamount").setText(head.getIn().toString());
        itemIn.addElement("pk_currtype").setText("CNY");
        itemIn.addElement("pk_accasoa").setText(head.getSubjectCode());
        /*Element assIn = itemIn.addElement("ass").addElement("item");
        assIn.addElement("pk_Checktype").setText(head.getAssCode()); //银行账户
        assIn.addElement("pk_Checkvalue").setText(head.getAssValue());*/
        
        /**
         * 入金凭证结束
         */
        
        /**
         * 出金凭证开始
         */
        i = 1;
        Element voucherOut = ufinterface.addElement("voucher");
        
        Element voucher_headOut = voucherOut.addElement("voucher_head");
        voucher_headOut.addElement("year").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
        voucher_headOut.addElement("pk_accountingbook").setText("00-0002");
        voucher_headOut.addElement("period").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
        voucher_headOut.addElement("prepareddate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
        voucher_headOut.addElement("pk_prepared").setText("yy01");
        voucher_headOut.addElement("pk_org").setText("00");
        voucher_headOut.addElement("pk_org_v").setText("00");
        voucher_headOut.addElement("pk_system").setText("GL");
        voucher_headOut.addElement("voucherkind").setText("0");
        voucher_headOut.addElement("discardflag").setText("N");
        voucher_headOut.addElement("attachment").setText("0");
        voucher_headOut.addElement("signflag").setText("N");
        
        Element detailsOut = voucher_headOut.addElement("details");
        
        for (Entry<String, Bank> m :bankMap.entrySet())  {
            
            Bank bank = m.getValue();
            
            if(bank.getOut() != null && bank.getOut().compareTo(ZERO) > 0){
                Element item = detailsOut.addElement("item");
                item.addElement("detailindex").setText(""+i++);
                item.addElement("explanation").setText("银期转账（出）"+bank.getDate()+bank.getBankName());
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText("0");
                item.addElement("localdebitamount").setText("0");
                item.addElement("accsubjcode").setText(bank.getSubjectCode());//科目 TODO
                item.addElement("price").setText("0");//单价
                item.addElement("excrate2").setText("1");
                item.addElement("debitquantity").setText("0");//借方数量
                item.addElement("groupdebitamount").setText("0");
                item.addElement("globaldebitamount").setText("0");
                item.addElement("creditquantity").setText("0");//贷方数量
                item.addElement("creditamount").setText(bank.getOut().toString());//贷方金额
                item.addElement("groupcreditamount").setText("0");
                item.addElement("globalcreditamount").setText("0");
                item.addElement("localcreditamount").setText(bank.getOut().toString());
                item.addElement("pk_currtype").setText("CNY");
                item.addElement("pk_accasoa").setText(bank.getSubjectCode());//TODO
                Element ass = item.addElement("ass").addElement("item");
                ass.addElement("pk_Checktype").setText("0011"); //银行账户
                ass.addElement("pk_Checkvalue").setText(bank.getAccountNo());
            }
        }
        
        
        for (Entry<String, BusinessDepartment> m :deptMap.entrySet())  {
            
            BusinessDepartment dept = m.getValue();
            if(dept.getIn() != null && dept.getIn().compareTo(ZERO) > 0){
                Element item = detailsOut.addElement("item");
                item.addElement("detailindex").setText(""+i++);
                item.addElement("explanation").setText("银期转账（出）"+dept.getDate());
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText(dept.getOut().toString());
                item.addElement("localdebitamount").setText(dept.getOut().toString());
                item.addElement("accsubjcode").setText(dept.getSubjectCode());//科目 
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
                item.addElement("pk_accasoa").setText(dept.getSubjectCode());//TODO
                Element ass = item.addElement("ass").addElement("item");
                ass.addElement("pk_Checktype").setText(dept.getAssCode()); //辅助核算项编码
                ass.addElement("pk_Checkvalue").setText(dept.getAssValue());
            }
        }
        
        head.setDescription("银期转账（出）");
        //总部出金
        Element itemOut = detailsOut.addElement("item");
        itemOut.addElement("detailindex").setText(""+i++);
        itemOut.addElement("explanation").setText(head.getDescription()+date);
        itemOut.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
        itemOut.addElement("debitamount").setText(head.getOut().toString());
        itemOut.addElement("localdebitamount").setText(head.getOut().toString());
        itemOut.addElement("accsubjcode").setText(head.getSubjectCode());//科目 
        itemOut.addElement("price").setText("0");//单价
        itemOut.addElement("excrate2").setText("1");
        itemOut.addElement("debitquantity").setText("0");//借方数量
        itemOut.addElement("groupdebitamount").setText("0");
        itemOut.addElement("globaldebitamount").setText("0");
        itemOut.addElement("creditquantity").setText("0");//贷方数量
        itemOut.addElement("creditamount").setText("0");//贷方金额
        itemOut.addElement("groupcreditamount").setText("0");
        itemOut.addElement("globalcreditamount").setText("0");
        itemOut.addElement("localcreditamount").setText("0");
        itemOut.addElement("pk_currtype").setText("CNY");
        itemOut.addElement("pk_accasoa").setText(head.getSubjectCode());
        /*Element assOut = itemOut.addElement("ass").addElement("item");
        assOut.addElement("pk_Checktype").setText(head.getAssCode()); 
        assOut.addElement("pk_Checkvalue").setText(head.getAssValue());*/
        
        /**
         * 出金凭证结束
         */
        
        return document;
    }
}
