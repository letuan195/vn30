package com.algo.vn30.entity;


import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public class DailyDataModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sec_id")
    private Long sec_id;

    @Column(name = "date")
    private Date date;

    @Column(name = "market_cap")
    private Long market_cap;

    @Column(name = "shares")
    private Long shares;

    @Column(name = "free_shares")
    private Long free_shares;

    @Column(name = "close")
    private Double close;

    @Column(name = "trade_value")
    private Double trade_value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSec_id() {
        return sec_id;
    }

    public void setSec_id(Long sec_id) {
        this.sec_id = sec_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getMarket_cap() {
        return market_cap;
    }

    public void setMarket_cap(Long market_cap) {
        this.market_cap = market_cap;
    }

    public Long getShares() {
        return shares;
    }

    public void setShares(Long shares) {
        this.shares = shares;
    }

    public Long getFree_shares() { return free_shares; }

    public void setFree_shares(Long free_shares) { this.free_shares = free_shares; }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Double getTrade_value() {
        return trade_value;
    }

    public void setTrade_value(Double trade_value) {
        this.trade_value = trade_value;
    }
}
