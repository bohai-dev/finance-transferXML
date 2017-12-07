package com.bohai.finance.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

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

}
