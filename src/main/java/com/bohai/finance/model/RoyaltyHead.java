package com.bohai.finance.model;

import java.math.BigDecimal;

/**
 * 总部权利金收支
 * 
 * @author caojia
 *
 */
public class RoyaltyHead {

    /**
     * 大连权利金收入
     */
    private BigDecimal dlIn = BigDecimal.ZERO;

    /**
     * 大连权利金支出
     */
    private BigDecimal dlOut = BigDecimal.ZERO;

    /**
     * 郑州权利金收入
     */
    private BigDecimal zzIn = BigDecimal.ZERO;

    /**
     * 郑州权利金支出
     */
    private BigDecimal zzOut = BigDecimal.ZERO;

    /**
     * 上海权利金收入
     */
    private BigDecimal shIn = BigDecimal.ZERO;

    /**
     * 上海权利金支出
     */
    private BigDecimal shOut = BigDecimal.ZERO;

    /**
     * 中金所权利金收入
     */
    private BigDecimal zjIn = BigDecimal.ZERO;

    /**
     * 中金所权利金支出
     */
    private BigDecimal zjOut = BigDecimal.ZERO;

    /**
     * 能源中心权利金收入
     */
    private BigDecimal nyIn = BigDecimal.ZERO;

    /**
     * 能源中心权利金支出
     */
    private BigDecimal nyOut = BigDecimal.ZERO;

    public void addByExchange(String exchange, BigDecimal in, BigDecimal out) {
        if ("CFFEX".equals(exchange)) {
            // 中金所
            this.zjIn = this.zjIn.add(in);
            this.zjOut = this.zjOut.add(out);
        } else if ("CZCE".equals(exchange)) {
            // 郑州
            this.zzIn = this.zzIn.add(in);
            this.zzOut = this.zzOut.add(out);
        } else if ("DCE".equals(exchange)) {
            // 大连
            this.dlIn = this.dlIn.add(in);
            this.dlOut = this.dlOut.add(out);
        } else if ("SHFE".equals(exchange)) {
            // 上海
            this.shIn = this.shIn.add(in);
            this.shOut = this.shOut.add(out);
        } else if ("INE".equals(exchange)) {
            // 能源
            this.nyIn = this.nyIn.add(in);
            this.nyOut = this.nyOut.add(out);
        }
    }

    public BigDecimal getDlIn() {
        return dlIn;
    }

    public void setDlIn(BigDecimal dlIn) {
        this.dlIn = dlIn;
    }

    public BigDecimal getDlOut() {
        return dlOut;
    }

    public void setDlOut(BigDecimal dlOut) {
        this.dlOut = dlOut;
    }

    public BigDecimal getZzIn() {
        return zzIn;
    }

    public void setZzIn(BigDecimal zzIn) {
        this.zzIn = zzIn;
    }

    public BigDecimal getZzOut() {
        return zzOut;
    }

    public void setZzOut(BigDecimal zzOut) {
        this.zzOut = zzOut;
    }

    public BigDecimal getShIn() {
        return shIn;
    }

    public void setShIn(BigDecimal shIn) {
        this.shIn = shIn;
    }

    public BigDecimal getShOut() {
        return shOut;
    }

    public void setShOut(BigDecimal shOut) {
        this.shOut = shOut;
    }

    public BigDecimal getZjIn() {
        return zjIn;
    }

    public void setZjIn(BigDecimal zjIn) {
        this.zjIn = zjIn;
    }

    public BigDecimal getZjOut() {
        return zjOut;
    }

    public void setZjOut(BigDecimal zjOut) {
        this.zjOut = zjOut;
    }

    public BigDecimal getNyIn() {
        return nyIn;
    }

    public void setNyIn(BigDecimal nyIn) {
        this.nyIn = nyIn;
    }

    public BigDecimal getNyOut() {
        return nyOut;
    }

    public void setNyOut(BigDecimal nyOut) {
        this.nyOut = nyOut;
    }

}
