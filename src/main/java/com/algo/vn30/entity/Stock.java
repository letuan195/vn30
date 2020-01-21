package com.algo.vn30.entity;

import com.algo.vn30.persistence.DailyDataPersistence;
import com.algo.vn30.persistence.SecurityPersistence;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Stock {
    private Long id;
    private String code;
    private Double gtvh;
    private Double gtvhCurrent;
    private Double f;
    private Double gtvh_f;
    private Double gtgd;
    private Double turnover;
    private Boolean hasWarning;
    private Boolean isPreVN30;
    private Integer lenFromListedDate;

    public Stock(Long id, String code, Boolean isPreVN30, List<DailyDataImpl> historicalDailyDataListCurrent, List<DailyDataImpl> historicalDailyDataList) {
        this.id = id;
        this.code = code;
        this.isPreVN30 = isPreVN30;
        this.hasWarning = false;

        this.f = historicalDailyDataListCurrent.get(0).getFree_shares().doubleValue() / historicalDailyDataList.get(0).getShares().doubleValue();
        this.gtvhCurrent = historicalDailyDataList.get(0).getMarket_cap().doubleValue();

        Date date = historicalDailyDataList.get(0).getDate();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int monthTemp = localDate.getMonthValue();
        int count = 1;
        int countDay = 0;
        Double median = 0.0;
        Double sum = 0.0;
        Double sumGTVH = 0.0;
        List<Double> ddGTGD = new ArrayList<>();
        for (int i = 0; i < historicalDailyDataList.size(); i++) {
            countDay += 1;
            sumGTVH += historicalDailyDataList.get(i).getMarket_cap();
            date = historicalDailyDataList.get(i).getDate();
            localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int monthCurent = localDate.getMonthValue();
            if (monthCurent != monthTemp) {
                monthTemp = monthCurent;
                count += 1;
                Collections.sort(ddGTGD);
                int index = ddGTGD.size();
                if (index % 2 == 0) { median = (ddGTGD.get(index / 2) + ddGTGD.get(index / 2 - 1)) / 2; }
                else { median = ddGTGD.get((index -1) / 2); }
                sum += median;
                ddGTGD = new ArrayList<>();
                ddGTGD.add(historicalDailyDataList.get(i).getTrade_value());
                if (count > 12) { break; }
            }
            else {
                ddGTGD.add(historicalDailyDataList.get(i).getTrade_value());
            }
        }
        if (count <= 12) {
            this.lenFromListedDate = count;
            Collections.sort(ddGTGD);
            int index = ddGTGD.size();
            if (index % 2 == 0) { median = (ddGTGD.get(index / 2) + ddGTGD.get(index / 2 - 1)) / 2; }
            else { median = ddGTGD.get((index -1) / 2); }
            sum += median;
            this.gtgd = sum / count;
        }
        else {
            this.lenFromListedDate = count - 1;
            this.gtgd = sum / (count - 1);
        }
        this.gtvh = sumGTVH / countDay;
        this.gtvh_f = this.gtvh * this.f;
        this.turnover = this.gtgd / this.gtvh_f * 100;
    }

    public Stock(Long id, String code, Boolean isPreVN30, Double f, List<DailyDataImpl> historicalDailyDataList) {
        this.id = id;
        this.code = code;
        this.isPreVN30 = isPreVN30;
        this.hasWarning = false;

        this.f = f;
        this.gtvhCurrent = historicalDailyDataList.get(0).getMarket_cap().doubleValue();

        Date date = historicalDailyDataList.get(0).getDate();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int monthTemp = localDate.getMonthValue();
        int count = 1;
        int countDay = 0;
        Double median = 0.0;
        Double sum = 0.0;
        Double sumGTVH = 0.0;
        List<Double> ddGTGD = new ArrayList<>();
        for (int i = 0; i < historicalDailyDataList.size(); i++) {
            countDay += 1;
            sumGTVH += historicalDailyDataList.get(i).getMarket_cap();
            date = historicalDailyDataList.get(i).getDate();
            localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int monthCurent = localDate.getMonthValue();
            if (monthCurent != monthTemp) {
                monthTemp = monthCurent;
                count += 1;
                Collections.sort(ddGTGD);
                int index = ddGTGD.size();
                if (index % 2 == 0) { median = (ddGTGD.get(index / 2) + ddGTGD.get(index / 2 - 1)) / 2; }
                else { median = ddGTGD.get((index -1) / 2); }
                sum += median;
                ddGTGD = new ArrayList<>();
                ddGTGD.add(historicalDailyDataList.get(i).getTrade_value());
                if (count > 12) { break; }
            }
            else {
                ddGTGD.add(historicalDailyDataList.get(i).getTrade_value());
            }
        }
        if (count <= 12) {
            this.lenFromListedDate = count;
            Collections.sort(ddGTGD);
            int index = ddGTGD.size();
            if (index % 2 == 0) { median = (ddGTGD.get(index / 2) + ddGTGD.get(index / 2 - 1)) / 2; }
            else { median = ddGTGD.get((index -1) / 2); }
            sum += median;
            this.gtgd = sum / count;
        }
        else {
            this.lenFromListedDate = count - 1;
            this.gtgd = sum / (count - 1);
        }
        this.gtvh = sumGTVH / countDay;
        this.gtvh_f = this.gtvh * this.f;
        this.turnover = this.gtgd / this.gtvh_f * 100;
    }

    public Double getF() { return f; }

    public Double getGTVH() { return gtvh; }

    public Double getGTVH_f() { return gtvh_f; }

    public Double getTurnover() { return turnover; }

    public Long getId() { return id; }

    public String getCode() { return code; }

    public Boolean getHasWarning() { return hasWarning; }

    public Double getGTGD() { return gtgd; }

    public Boolean getIsPreVN30() { return isPreVN30; }

    public Integer getLenFromListedDate() { return lenFromListedDate; }

    public Double getGtvhCurrent() { return gtvhCurrent; }

    public void setCode(String code) { this.code = code; }

    public void setF(Double f) { this.f = f; }

    public void setGTVH(Double GTVH) { this.gtvh = GTVH; }

    public void setGTVH_f(Double GTVH_f) { this.gtvh_f = GTVH_f; }

    public void setId(Long id) { this.id = id; }

    public void setTurnover(Double turnover) { this.turnover = turnover; }

    public void setHasWarning(Boolean hasWarning) { this.hasWarning = hasWarning; }

    public void setGTGD(Double gtgd) { this.gtgd = gtgd; }

    public void setIsPreVN30(Boolean isPreVN30) { this.isPreVN30 = isPreVN30; }

    public void setLenFromListedDate(Integer lenFromListedDate) { this.lenFromListedDate = lenFromListedDate; }

    public void setGtvhCurrent(Double gtvhCurrent) { this.gtvhCurrent = gtvhCurrent; }
}
