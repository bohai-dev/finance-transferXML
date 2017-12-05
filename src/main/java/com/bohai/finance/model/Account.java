package com.bohai.finance.model;

import java.math.BigDecimal;

public class Account {

    /**
     * 银行名称
     */
    private String bankName;
    
    /**
     * 账号
     */
    private String accountNo;
    
    /**
     * 入金
     */
    private BigDecimal in;
    
    /**
     * 出金
     */
    private BigDecimal out;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
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
