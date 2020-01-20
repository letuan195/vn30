package com.algo.vn30.entity;


import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public class SecurityModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "full_name")
    private String full_name;

    @Column(name = "date_of_listing")
    private Date date_of_listing;

    @Column(name = "initial_listing_price")
    private Double initial_listing_price;

    @Column(name = "charter_capital")
    private Long charter_capital;

    @Column(name = "listing_volume")
    private Long listing_volume;

    @Column(name = "last_updated")
    private Date last_updated;

    @Column(name = "exchange")
    private String exchange;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public Date getDate_of_listing() {
        return date_of_listing;
    }

    public void setDate_of_listing(Date date_of_listing) {
        this.date_of_listing = date_of_listing;
    }

    public Double getInitial_listing_price() {
        return initial_listing_price;
    }

    public void setInitial_listing_price(Double initial_listing_price) {
        this.initial_listing_price = initial_listing_price;
    }

    public Long getCharter_capital() {
        return charter_capital;
    }

    public void setCharter_capital(Long charter_capital) {
        this.charter_capital = charter_capital;
    }

    public Long getListing_volume() {
        return listing_volume;
    }

    public void setListing_volume(Long listing_volume) {
        this.listing_volume = listing_volume;
    }

    public Date getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(Date last_updated) {
        this.last_updated = last_updated;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }
}
