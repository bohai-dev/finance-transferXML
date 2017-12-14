package com.bohai.finance.model;

import java.math.BigDecimal;

public class BusinessDepartment {
    
    private String deptName;
    
    private String subjectCode;
    
    private String subjectName;
    
    private String assCode;
    
    private String assValue;
    
    private BigDecimal in;
    
    private BigDecimal out;
    
    private String date;
    
    /**
     * 账簿编号（营业部凭证用）
     */
    private String bookNo;
    

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getAssCode() {
        return assCode;
    }

    public void setAssCode(String assCode) {
        this.assCode = assCode;
    }

    public String getAssValue() {
        return assValue;
    }

    public void setAssValue(String assValue) {
        this.assValue = assValue;
    }

    public BigDecimal getIn() {
        return in;
    }

    public void setIn(BigDecimal in) {
        this.in = in;
    }

    public BigDecimal getOut() {
        return out;
    }

    public void setOut(BigDecimal out) {
        this.out = out;
    }


}
