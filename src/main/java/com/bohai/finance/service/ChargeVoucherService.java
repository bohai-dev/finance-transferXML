package com.bohai.finance.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;

import com.bohai.finance.model.BusinessDepartment;

/**
 * 手续费凭证文件生成
 * @author BHQH-CXYWB
 *
 */
public class ChargeVoucherService {
    
    
    public void generateVoucher(File infile, String targetPath, String date) throws Exception{
        
        //先查询总部
        Map<String, BusinessDepartment> deptMap= new HashMap<String, BusinessDepartment>();
        
        DeptService deptService = new DeptService();
        List<BusinessDepartment> depts = deptService.queryDepts();
        if(depts !=null){
            for (BusinessDepartment businessDepartment : depts) {
                
            }
        }
    }

}
