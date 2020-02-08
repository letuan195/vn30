package com.algo.vn30.entity;

import com.algo.vn30.persistence.DailyDataPersistence;
import com.algo.vn30.persistence.SecurityPersistence;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
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

    public Stock(Long id, String code, Date listedDate, Boolean isPreVN30, List<DailyDataImpl> historicalDailyDataList) {
        this.id = id;
        this.code = code;
        this.isPreVN30 = isPreVN30;
        this.hasWarning = false;
        if (code.compareTo("HNG") == 0)
        {
            this.hasWarning = true;
        }

        for (int i = 0; i < historicalDailyDataList.size(); i++) {
            if (historicalDailyDataList.get(i).getFree_shares() != null && historicalDailyDataList.get(i).getShares() != null) {
                this.f = historicalDailyDataList.get(i).getFree_shares().doubleValue() / historicalDailyDataList.get(i).getShares().doubleValue();
                break;
            }
        }
        this.gtvhCurrent = historicalDailyDataList.get(0).getMarket_cap().doubleValue();

        Date date = historicalDailyDataList.get(0).getDate();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int monthTemp = localDate.getMonthValue();
        int count = 0;
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
            }
            else {
                ddGTGD.add(historicalDailyDataList.get(i).getTrade_value());
            }
        }
        if (ddGTGD.size() != 0) {
            int index = ddGTGD.size();
            Collections.sort(ddGTGD);
            if (index % 2 == 0) {
                median = (ddGTGD.get(index / 2) + ddGTGD.get(index / 2 - 1)) / 2;
            } else {
                median = ddGTGD.get((index - 1) / 2);
            }
            count += 1;
            sum += median;
            this.gtgd = sum / count;
        }

//        if (count <= 12) {
//            Collections.sort(ddGTGD);
//            int index = ddGTGD.size();
//            if (index % 2 == 0) { median = (ddGTGD.get(index / 2) + ddGTGD.get(index / 2 - 1)) / 2; }
//            else { median = ddGTGD.get((index -1) / 2); }
//            sum += median;
//            this.gtgd = sum / count;
//        }
//        else {
//            this.gtgd = sum / (count - 1);
//        }
        Period period = Period.between(listedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                historicalDailyDataList.get(0).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        this.lenFromListedDate = period.getYears() * 12 + period.getMonths();
//        this.lenFromListedDate = Period.between(listedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
//                historicalDailyDataList.get(0).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).getMonths();
        this.gtvh = sumGTVH / countDay;
        this.gtvh_f = this.gtvh * this.f;
        this.turnover = this.gtgd / this.gtvh_f * 100;
    }

    public Stock(Long id, String code, Date listedDate, Boolean isPreVN30, Double f, List<DailyDataImpl> historicalDailyDataList) {
        this.id = id;
        this.code = code;
        this.isPreVN30 = isPreVN30;
        this.hasWarning = false;
        if (code.compareTo("HNG") == 0)
        {
            this.hasWarning = true;
        }
        this.f = f;
        this.gtvhCurrent = historicalDailyDataList.get(0).getMarket_cap().doubleValue();

        Date date = historicalDailyDataList.get(0).getDate();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int monthTemp = localDate.getMonthValue();
        int count = 0;
        int countDay = 0;
        Double median = 0.0;
        Double sum = 0.0;
        Double sumGTVH = 0.0;
        List<Double> ddGTGD = new ArrayList<>();
        for (int i = 0; i < historicalDailyDataList.size(); i++) {
            if (code.compareTo("CTD") == 0) {
                System.out.println(historicalDailyDataList.get(i).getDate());
                System.out.println(i);
                System.out.println(historicalDailyDataList.size());
            }
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
                if (index % 2 == 0) {
                    median = (ddGTGD.get(index / 2) + ddGTGD.get(index / 2 - 1)) / 2;
                } else {
                    median = ddGTGD.get((index - 1) / 2);
                }
                sum += median;
                ddGTGD = new ArrayList<>();
                ddGTGD.add(historicalDailyDataList.get(i).getTrade_value());
            } else {
                ddGTGD.add(historicalDailyDataList.get(i).getTrade_value());
            }
        }
        if (ddGTGD.size() != 0) {
            int index = ddGTGD.size();
            Collections.sort(ddGTGD);
            if (index % 2 == 0) {
                median = (ddGTGD.get(index / 2) + ddGTGD.get(index / 2 - 1)) / 2;
            } else {
                median = ddGTGD.get((index - 1) / 2);
            }
            count += 1;
            sum += median;
            this.gtgd = sum / count;
        }

//        if (count <= 12) {
//            Collections.sort(ddGTGD);
//            int index = ddGTGD.size();
//            if (index % 2 == 0) { median = (ddGTGD.get(index / 2) + ddGTGD.get(index / 2 - 1)) / 2; }
//            else { median = ddGTGD.get((index -1) / 2); }
//            sum += median;
//            this.gtgd = sum / count;
//        }
//        else {
//            this.gtgd = sum / (count - 1);
//        }
        Period period = Period.between(listedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                historicalDailyDataList.get(0).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        this.lenFromListedDate = period.getYears() * 12 + period.getMonths();
//        this.lenFromListedDate = Period.between(historicalDailyDataList.get(historicalDailyDataList.size() - 1).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
//                historicalDailyDataList.get(0).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).getMonths();
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
