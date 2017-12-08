package com.bohai.finance.model;

import java.math.BigDecimal;

/**
 * 总部汇总
 * @author BHQH-CXYWB
 *
 */
public class Headquarters {
    
    private String description;
    
    private String subjectCode;
    
    private String assCode;
    
    private String assValue;
    
    private BigDecimal in;
    
    private BigDecimal out;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
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
