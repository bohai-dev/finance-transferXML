package com.bohai.finance.model;

import java.math.BigDecimal;

/**
 * 总部盈亏凭证
 * @author caojia
 *
 */
public class HeadProfit {

    /**
     * 账本编号
     */
    private String bookNo = "00-0002";
    
    /**
     * 总盈亏
     */
    private BigDecimal totalProfit = BigDecimal.ZERO;
    
    /**
     * 大连交易所盈亏
     */
    private BigDecimal dl = BigDecimal.ZERO;
    
    /**
     * 上期所盈亏
     */
    private BigDecimal sh = BigDecimal.ZERO;
    
    /**
     * 郑商所盈亏
     */
    private BigDecimal zz = BigDecimal.ZERO;
    
    /**
     * 中金所盈亏
     */
    private BigDecimal zj = BigDecimal.ZERO;
    
    private BigDecimal ny = BigDecimal.ZERO;
    
    public void addProfit(String exchange, BigDecimal profit){
        this.totalProfit = this.totalProfit.add(profit);
        if("CFFEX".equals(exchange)){
            //中金所
            this.zj = this.zj.add(profit);
        }else if ("CZCE".equals(exchange)) {
            //郑州
            this.zz = this.zz.add(profit);
        }else if ("DCE".equals(exchange)) {
            //大连
            this.dl = this.dl.add(profit);
        }else if ("SHFE".equals(exchange)) {
            //上海
            this.sh = this.sh.add(profit);
        }else if ("INE".equals(exchange)) {
            this.ny = this.ny.add(profit);
        }
    }

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

    public BigDecimal getNy() {
        return ny;
    }

    public void setNy(BigDecimal ny) {
        this.ny = ny;
    }
    
}
