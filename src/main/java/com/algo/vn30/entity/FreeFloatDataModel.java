package com.algo.vn30.entity;


import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public class FreeFloatDataModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "stt")
    private Long stt;

    @Column(name = "sec_id")
    private Long sec_id;

    @Column(name = "date")
    private Date date;

    @Column(name = "free_float")
    private Double free_float;

    @Column(name = "free_float_adj")
    private Integer free_float_adj;

    @Column(name = "type")
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStt() { return stt; }

    public void setStt(Long stt) { this.stt = stt; }

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

    public Double getFree_float() {
        return free_float;
    }

    public void setFree_float(Double free_float) {
        this.free_float = free_float;
    }

    public Integer getFree_float_adj() { return free_float_adj; }

    public void setFree_float_adj(Integer free_float_adj) {
        this.free_float_adj = free_float_adj;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }
}
