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

import com.bohai.finance.Dom4jTest;
import com.bohai.finance.model.Account;
import com.bohai.finance.util.DateFormatterUtil;

/**
 * 生成凭证类
 * @author BHQH-CXYWB
 *
 */
public class VoucherService {

    static BigDecimal ZERO = new BigDecimal("0");
    
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
    
    
    public Map<String,Account> generateXML(File infile, String targetPath) throws DocumentException, IOException{
        
        Map<String, Account> map= new HashMap<String, Account>();
        
        int dateIndex = 0;
        
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
            }
            
        }
        
        for(int i =3 ; i < rows.size() ; i++){
            
            Element row = rows.get(i);
            
            List<Element> cells = row.elements("Cell");
            
                Element firstCell = cells.get(0).element("Data");
                String firstStr = firstCell.getText();
                if(firstStr.indexOf("总计") > -1){
                    break;
                }
            
                Element bankCell = cells.get(bankIndex).element("Data");
                String bankName = bankCell.getTextTrim();
                Element inCell = cells.get(inIndex).element("Data");
                String in = inCell.getTextTrim().replaceAll(",", "");
                Element outCell = cells.get(outIndex).element("Data");
                String out = outCell.getTextTrim().replaceAll(",", "");
                
                Account account = map.get(bankName);
                if(account == null){
                    account = new Account();
                    account.setBankName(bankName);
                    account.setIn(new BigDecimal(in));
                    account.setOut(new BigDecimal(out));
                    map.put(bankName, account);
                }else {
                    account.setIn(account.getIn().add(new BigDecimal(in)));
                    account.setOut(account.getOut().add(new BigDecimal(out)));
                    map.put(bankName, account);
                }
                
        }
        Document outDoc = this.createDocument(map);
        this.write(outDoc,targetPath);
        
        return map;
    }
    
    
    
    public Document createDocument(Map<String, Account> map) {
        Document document = DocumentHelper.createDocument();
        Element ufinterface = document.addElement("ufinterface")
                .addAttribute("account", "002")
                .addAttribute("billtype", "vouchergl")
                .addAttribute("businessunitcode", "develop")
                .addAttribute("filename", "")
                .addAttribute("groupcode", "01")
                .addAttribute("isexchange", "")
                .addAttribute("orgcode", "00")
                .addAttribute("receiver", "")
                .addAttribute("replace", "")
                .addAttribute("roottag", "")
                .addAttribute("sender", "NC_OA");
                

        Element voucher = ufinterface.addElement("voucher");
        
        Element voucher_head = voucher.addElement("voucher_head");
        voucher_head.addElement("year").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
        voucher_head.addElement("pk_accountingbook").setText("00-0002");
        voucher_head.addElement("period").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
        voucher_head.addElement("prepareddate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
        voucher_head.addElement("pk_prepared").setText("yy01");
        voucher_head.addElement("pk_org").setText("00");
        voucher_head.addElement("pk_org_v").setText("00");
        voucher_head.addElement("pk_system").setText("GL");
        voucher_head.addElement("voucherkind").setText("0");
        voucher_head.addElement("discardflag").setText("N");
        voucher_head.addElement("attachment").setText("0");
        voucher_head.addElement("signflag").setText("N");
        
        Element details = voucher_head.addElement("details");
        
        int i = 1;
        for (Entry<String, Account> m :map.entrySet())  {
            
            Account account = m.getValue();
            if(account.getIn() != null && account.getIn().compareTo(ZERO) > 0){
                Element item = details.addElement("item");
                item.addElement("detailindex").setText(""+i++);
                item.addElement("explanation").setText(account.getBankName()+"入金");
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText("0");
                item.addElement("localdebitamount").setText("0");
                item.addElement("accsubjcode").setText("11240101");//科目 TODO
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
                item.addElement("pk_accasoa").setText("11240101");//TODO
            }
            
            if(account.getOut() != null && account.getOut().compareTo(ZERO) > 0){
                Element item = details.addElement("item");
                item.addElement("detailindex").setText(""+i++);
                item.addElement("explanation").setText(account.getBankName()+"出金");
                item.addElement("verifydate").setText(DateFormatterUtil.getDateStrByFormatter(new Date(), "yyyy-MM-dd"));
                item.addElement("debitamount").setText("0");
                item.addElement("localdebitamount").setText("0");
                item.addElement("accsubjcode").setText("11240401");//科目 TODO
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
                item.addElement("pk_accasoa").setText("11240401");//TODO
            }
        }
        return document;
    }
}
