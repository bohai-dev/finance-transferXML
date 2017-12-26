package com.bohai.finance.model;

import java.math.BigDecimal;

/**
 * 总部手续费凭证
 * @author BHQH-CXYWB
 */
public class HeadCharge {
    
    /**
     * 账本编号
     */
    private String bookNo;
    
    /**
     * 大连上交手续费
     */
    private BigDecimal dl;
    
    /**
     * 上海上交手续费
     */
    private BigDecimal sh;
    
    /**
     * 郑州上交手续费
     */
    private BigDecimal zz;
    
    /**
     * 中金所上交手续费
     */
    private BigDecimal zj;
    
    /**
     * 留存手续费
     */
    private BigDecimal remain;

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public BigDecimal getDl() {
        return dl;
    }

    public void setDl(BigDecimal dl) {
        this.dl = dl;
    }

    public BigDecimal getSh() {
        return sh;
    }

    public void setSh(BigDecimal sh) {
        this.sh = sh;
    }

    public BigDecimal getZz() {
        return zz;
    }

    public void setZz(BigDecimal zz) {
        this.zz = zz;
    }

    public BigDecimal getZj() {
        return zj;
    }

    public void setZj(BigDecimal zj) {
        this.zj = zj;
    }

    public BigDecimal getRemain() {
        return remain;
    }

    public void setRemain(BigDecimal remain) {
        this.remain = remain;
    }

}
