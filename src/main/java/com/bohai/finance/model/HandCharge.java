package com.bohai.finance.model;

import java.math.BigDecimal;

/**
 * 盈亏手续费凭证
 * @author xs
 *
 */
public class HandCharge {

    
    /**
     * 大连交易所盈亏
     */
    private BigDecimal handDl = BigDecimal.ZERO;
    
    /**
     * 上期所盈亏
     */
    private BigDecimal handSh = BigDecimal.ZERO;
    
    /**
     * 郑商所盈亏
     */
    private BigDecimal handZz = BigDecimal.ZERO;
    
    /**
     * 中金所盈亏
     */
    private BigDecimal handZj = BigDecimal.ZERO;
    
    /**
     * 能源盈亏
     */
    private BigDecimal handNy = BigDecimal.ZERO;
    
    public void addCharge(String exchange, BigDecimal charge){
        if("CFFEX".equals(exchange)){
            //中金所
            this.handZj = this.handZj.add(charge);
        } else if ("CZCE".equals(exchange)) {
            //郑州
            this.handZz = this.handZz.add(charge);
        } else if ("DCE".equals(exchange)) {
            //大连
            this.handDl = this.handDl.add(charge);
        } else if ("SHFE".equals(exchange)) {
            //上海
            this.handSh = this.handSh.add(charge);
        }else if ("INE".equals(exchange)) {
            //能源
            this.handNy = this.handNy.add(charge);
        }
    }


    public BigDecimal getHanddl() {
        return handDl;
    }

    public void setHanddl(BigDecimal handDl) {
        this.handDl = handDl;
    }

    public BigDecimal getHandsh() {
        return handSh;
    }

    public void setHandsh(BigDecimal handSh) {
        this.handSh = handSh;
    }

    public BigDecimal getHandzz() {
        return handZz;
    }

    public void setHandzz(BigDecimal handZz) {
        this.handZz = handZz;
    }

    public BigDecimal getHandzj() {
        return handZj;
    }

    public void setHandzj(BigDecimal handZj) {
        this.handZj = handZj;
    }


    public BigDecimal getHandNy() {
        return handNy;
    }


    public void setHandNy(BigDecimal handNy) {
        this.handNy = handNy;
    }
    
}
