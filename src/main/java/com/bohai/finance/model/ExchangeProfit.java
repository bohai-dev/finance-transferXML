package com.bohai.finance.model;

import java.math.BigDecimal;

/**
 * 盈亏手续费凭证
 * @author xs
 *
 */
public class ExchangeProfit {
  
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
    
    public void addProfit(String exchange, BigDecimal profit){

        if("CFFEX".equals(exchange)){
            //中金所
            this.zj = this.zj.add(profit);
        } else if ("CZCE".equals(exchange)) {
            //郑州
            this.zz = this.zz.add(profit);
        } else if ("DCE".equals(exchange)) {
            //大连
            this.dl = this.dl.add(profit);
        } else if ("SHFE".equals(exchange)) {
            //上海
            this.sh = this.sh.add(profit);
        }
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
    
}
