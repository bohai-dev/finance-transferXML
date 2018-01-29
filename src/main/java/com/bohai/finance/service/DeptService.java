package com.bohai.finance.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.bohai.finance.model.BusinessDepartment;
import com.bohai.finance.util.DataConfig;

public class DeptService {
    
    static Logger logger = Logger.getLogger(DeptService.class);
    
    public List<BusinessDepartment> queryDepts() throws DocumentException{
        
        List<BusinessDepartment> list = new ArrayList<BusinessDepartment>();
        
        SAXReader reader = new SAXReader();
        Document document = reader.read(DataConfig.DEPT_DATA_URL);
        
        Element root = document.getRootElement();
        for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
            Element element = it.next();
            BusinessDepartment dept = new BusinessDepartment();
            dept.setDeptName(element.attributeValue(DataConfig.DEPT_DEPT_NAME));
            dept.setBookNo(element.attributeValue(DataConfig.DEPT_BOOK_NO));
            dept.setSubjectCode(element.attributeValue(DataConfig.DEPT_SUBJECT_CODE));
            dept.setSubjectName(element.attributeValue(DataConfig.DEPT_SUBJECT_NAME));
            dept.setAssCode(element.attributeValue(DataConfig.DEPT_ASS_CODE));
            dept.setAssValue(element.attributeValue(DataConfig.DEPT_ASS_VALUE));
            list.add(dept);
        }
        
        return list;
    }
    
    /**
     * 根据营业部名称获取CTP中的营业部名称
     * @param name
     * @return
     * @throws Exception 
     */
    public String getDepNameByName(String name) throws Exception{
        
        String depName = "";
        SAXReader reader = new SAXReader();
        Document document = reader.read(DataConfig.DEPT_DATA_URL);
        Element root = document.getRootElement();
        Element e = root.element(name);
        if(e == null){
            throw new Exception("查询营业部失败，无此营业部信息："+name);
        }
        depName = e.attributeValue(DataConfig.DEPT_DEPT_NAME);
        if(depName == null || depName.equals("")){
            throw new Exception("查询营业部失败，无此营业部信息："+name);
        }
        logger.debug("营业部："+name+"对应CTP营业部："+depName);
        return depName;
    }

}
