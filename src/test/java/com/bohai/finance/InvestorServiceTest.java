package com.bohai.finance;

import java.io.File;
import java.io.IOException;

import org.dom4j.DocumentException;
import org.junit.Test;

import com.bohai.finance.service.DeptService;
import com.bohai.finance.service.InvestorService;

public class InvestorServiceTest {
    
    @Test
    public void save() throws DocumentException, IOException{
        InvestorService service = new InvestorService();
        service.saveInvestors(new File("F:/财务转XML/测试/投资者信息查询_20171130.xml"));
    }
    
    @Test
    public void getDepNameByInvestorNo() throws Exception{
        InvestorService service = new InvestorService();
        
        String name = service.getDepNameByInvestorNo("10100018");
        
        DeptService deptService = new DeptService();
        deptService.getDepNameByName(name);
    }

}
