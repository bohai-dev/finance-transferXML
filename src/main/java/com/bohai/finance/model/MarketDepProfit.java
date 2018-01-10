package com.bohai.finance.model;

import java.math.BigDecimal;

/**
 * 营业部盈亏
 * @author caojia
 */
public class MarketDepProfit {
    
    /**
     * 账本编号
     */
    private String bookNo;
    
    private BigDecimal totalProfit = BigDecimal.ZERO;

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }
    
}
