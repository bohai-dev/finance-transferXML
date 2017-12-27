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
    private String bookNo = "00-0002";
    
    /**
     * 手续费
     */
    private BigDecimal charge = BigDecimal.ZERO;
    
    /**
     * 留存手续费
     */
    private BigDecimal remain = BigDecimal.ZERO;
    
    /**
     * 大连上交手续费
     */
    private BigDecimal dl = BigDecimal.ZERO;
    
    /**
     * 上海上交手续费
     */
    private BigDecimal sh = BigDecimal.ZERO;
    
    /**
     * 郑州上交手续费
     */
    private BigDecimal zz = BigDecimal.ZERO;
    
    /**
     * 中金所上交手续费
     */
    private BigDecimal zj = BigDecimal.ZERO;
    
    /**
     * 累加手续费
     * @param exchange 交易所代码
     * @param charge 手续费
     * @param remain 留存手续费
     * @param handOn 上交手续费
     */
    public void addCharge(String exchange, BigDecimal charge, BigDecimal remain, BigDecimal handOn){
        
        this.charge = this.charge.add(charge);
        this.remain = this.remain.add(remain);
        if("CFFEX".equals(exchange)){
            //中金所
            this.zj = this.zj.add(handOn);
        }else if ("CZCE".equals(exchange)) {
            //郑州
            this.zz = this.zz.add(handOn);
        }else if ("DCE".equals(exchange)) {
            //大连
            this.dl = this.dl.add(handOn);
        }else if ("SHFE".equals(exchange)) {
            //上海
            this.sh = this.sh.add(handOn);
        }
        
    }

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

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    
}
