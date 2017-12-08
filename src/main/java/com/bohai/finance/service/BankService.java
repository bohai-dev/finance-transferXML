package com.bohai.finance.service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bohai.finance.model.Bank;
import com.bohai.finance.util.DataConfig;

public class BankService {
    
    public List<Bank> queryBanks() throws DocumentException{
        
        List<Bank> list = new ArrayList<Bank>();
        
        SAXReader reader = new SAXReader();
        Document document = reader.read(DataConfig.BANK_DATA_URL);
        
        Element root = document.getRootElement();
        for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
            Element element = it.next();
            Bank bank = new Bank();
            bank.setBankName(element.getName());
            bank.setSubjectCode(element.attributeValue(DataConfig.BANK_BANK_NAME));
            bank.setSubjectName(element.attributeValue(DataConfig.BANK_SUBJECT_NAME));
            bank.setSubjectCode(element.attributeValue(DataConfig.BANK_SUBJECT_CODE));
            bank.setAccountNo(element.attributeValue(DataConfig.BANK_ACCOUNT_NO));
            list.add(bank);
        }
        
        return list;
        
    }
    
    public void updateAccountNoByBankName(String bankName, String accountNo) throws DocumentException, IOException{
        
        SAXReader reader = new SAXReader();
        Document document = reader.read(DataConfig.BANK_DATA_URL);
        
        Element bank = document.getRootElement().element(bankName);
        if(bank != null){
            bank.attribute(DataConfig.BANK_ACCOUNT_NO).setValue(accountNo);
        }
        
      //设置文件编码  
        /*OutputFormat xmlFormat = new OutputFormat();  
        xmlFormat.setEncoding("UTF-8"); 
        // 设置换行 
        xmlFormat.setNewlines(true); 
        // 生成缩进 
        xmlFormat.setIndent(true); 
        // 使用4个空格进行缩进, 可以兼容文本编辑器 
        xmlFormat.setIndent("    "); */
        
        try (FileWriter fileWiter = new FileWriter(DataConfig.BANK_DATA_URL)) {
            XMLWriter writer = new XMLWriter(fileWiter);
            writer.write(document);
            writer.close();
        }
    }

}
