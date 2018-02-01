package com.bohai.finance.model;

import java.math.BigDecimal;

/**
 * 营业部权利金收支
 * @author caojia
 */
public class RoyaltyDep {
    
    /**
     * 账本号
     */
    private String bookNo;
    
    /**
     * 权利金收入
     */
    private BigDecimal in;
    
    /**
     * 权利金支出
     */
    private BigDecimal out;

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
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
