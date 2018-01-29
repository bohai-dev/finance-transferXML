package com.bohai.finance.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bohai.finance.util.DataConfig;

/**
 * 投资者信息接口
 * @author caojia
 *
 */
public class InvestorService {
    
    static Logger logger = Logger.getLogger(InvestorService.class);
    
    static final String PREFIX = "I";

    /**
     * 保存客户信息
     * @param file
     * @throws DocumentException 
     * @throws IOException 
     */
    public void saveInvestors(File infile) throws DocumentException, IOException{
        
        Document document = this.parse(infile);
        
        Element root = document.getRootElement();
        
        Element workSheet = root.element("Worksheet");
        
        Element table = workSheet.element("Table");
        
        List<Element> rows = table.elements("Row");
        
        
        Document investorDocument = DocumentHelper.createDocument();
        Element investorRoot = investorDocument.addElement("root");
        
        for(int i =1 ; i < rows.size() ; i++){
            
            Element row = rows.get(i);
            
            List<Element> cells = row.elements("Cell");
            if(cells == null || cells.get(0).element("Data") == null || cells.get(0).element("Data").getTextTrim() == null){
                break;
            }
            
            //投资者代码
            String investorNo = cells.get(0).element("Data").getTextTrim();
            
            //营业部名称
            String depName = cells.get(1).element("Data").getTextTrim();
            
            investorRoot.addElement(PREFIX+investorNo)
            .addAttribute(DataConfig.INVESTOR_DEP_NAME, depName);
            
        }
        this.write(investorDocument, DataConfig.INVESTOR_DATA_URL);
        
    }
    
    public String getDepNameByInvestorNo(String investorNo) throws Exception{
        
        String depName = "";
        SAXReader reader = new SAXReader();
        Document document = reader.read(DataConfig.INVESTOR_DATA_URL);
        Element root = document.getRootElement();
        Element e = root.element(PREFIX+investorNo);
        if(e == null){
            throw new Exception("查询客户所属营业部失败，无此客户信息："+investorNo);
        }
        depName = e.attributeValue(DataConfig.INVESTOR_DEP_NAME);
        if(depName == null || depName.equals("")){
            throw new Exception("查询客户所属营业部失败，无此客户信息："+investorNo);
        }
        logger.debug("投资者："+investorNo+"所在营业部："+depName);
        return depName;
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
