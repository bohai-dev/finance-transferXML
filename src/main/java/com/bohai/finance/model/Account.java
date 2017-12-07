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

}
