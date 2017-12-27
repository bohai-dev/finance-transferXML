package com.bohai.finance.model;

import java.math.BigDecimal;

/**
 * 营业部手续费
 * @author caojia
 */
public class MarketDepCharge {
    
    /**
     * 账簿编号
     */
    private String bookNo;
    
    /**
     * 手续费
     */
    private BigDecimal charge;
    
    /**
     * 上交手续费
     */
    private BigDecimal handOnCharge;
    
    /**
     * 留存手续费
     */
    private BigDecimal remainCharge;

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public BigDecimal getHandOnCharge() {
        return handOnCharge;
    }

    public void setHandOnCharge(BigDecimal handOnCharge) {
        this.handOnCharge = handOnCharge;
    }

    public BigDecimal getRemainCharge() {
        return remainCharge;
    }

    public void setRemainCharge(BigDecimal remainCharge) {
        this.remainCharge = remainCharge;
    }
    
    

}
