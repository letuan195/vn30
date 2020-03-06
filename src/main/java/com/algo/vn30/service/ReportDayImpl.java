package com.algo.vn30.service;

import com.algo.vn30.entity.*;
import com.algo.vn30.persistence.DailyDataPersistence;
import com.algo.vn30.persistence.FreeFloatDataPersistence;
import com.algo.vn30.persistence.FreeFloatRealDataPersistence;
import com.algo.vn30.persistence.SecurityPersistence;
import com.algo.vn30.utils.CsvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Service;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReportDayImpl implements ReportDayService {

    @Autowired
    private SecurityPersistence securityPersistence;
    @Autowired
    private DailyDataPersistence dailyDataPersistence;
    @Autowired
    private FreeFloatDataPersistence freeFloatDataPersistence;
    @Autowired
    private FreeFloatRealDataPersistence freeFloatRealDataPersistence;

    @Override
    public String getReport(Date date) {
        List<SecurityImpl> securitiesList = securityPersistence.findByExchange("HSX");
        List<Stock> stockListHSX = new ArrayList<>();
        boolean isPreVN30 = false;
        int count = 0;

        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int day = localDate.getDayOfMonth();
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        int yearPre = localDate.getYear() - 1;
        String path = "D:/Result/vn30_" + day + "-" + month + "-" + year + ".csv";

        Date dateStart = null;
        Date dateEnd = date;
        List<Date> dateHasData = freeFloatRealDataPersistence.findNewestByDate(date);
        Date dateNewFreeFloat = dateHasData.get(0);
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            dateStart = format.parse(Integer.toString(yearPre) + "-" + Integer.toString(month) + "-" + Integer.toString(day));
        } catch (Exception e) {

        }
        List<String> listPreVN30Code = new ArrayList<>();
        List<FreeFloatRealDataImpl> historicalFreeFloatRealDataList = freeFloatRealDataPersistence.findByDate(dateNewFreeFloat);
        for (int i = 0; i < historicalFreeFloatRealDataList.size(); i++) {
            if (historicalFreeFloatRealDataList.get(i).getType().compareTo("VN30") == 0 && historicalFreeFloatRealDataList.get(i).getStt() <= 30) {
                for (int j = 0; j < securitiesList.size(); j++) {
                    if (securitiesList.get(j).getId().compareTo(historicalFreeFloatRealDataList.get(i).getSec_id()) == 0) {
                        listPreVN30Code.add(securitiesList.get(j).getName());
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < securitiesList.size(); i++) {
            if (listPreVN30Code.contains(securitiesList.get(i).getName())) {
                isPreVN30 = true;
            } else {
                isPreVN30 = false;
            }
            List<DailyDataImpl> historicalDailyDataList = dailyDataPersistence.findBySecIdAndDateAfter(securitiesList.get(i).getId(), dateStart, dateEnd);
            List<FreeFloatRealDataImpl> historicalFreeFloatRealDataList2 = freeFloatRealDataPersistence.findBySecIdAndDate(securitiesList.get(i).getId(), dateNewFreeFloat);
            if (historicalDailyDataList.size() == 0) {
                count++;
                continue;
            }
            if (historicalFreeFloatRealDataList2.size() > 0) {
                Stock newStock = new Stock(securitiesList.get(i).getId(), securitiesList.get(i).getName(), securitiesList.get(i).getDate_of_listing(), isPreVN30, historicalFreeFloatRealDataList2.get(0).getFree_float_adj().doubleValue() / 100, historicalDailyDataList);
                stockListHSX.add(newStock);
            } else {
                Stock newStock = new Stock(securitiesList.get(i).getId(), securitiesList.get(i).getName(), securitiesList.get(i).getDate_of_listing(), isPreVN30, historicalDailyDataList);
                stockListHSX.add(newStock);
            }
        }
        VN30Result vn30Result = VN30.getVN30(stockListHSX);
        List<String[]> resultsCSV = new ArrayList<>();

        Boolean write = false;
        resultsCSV.add(new String[]{"Series Number", "Name", "Market Cap Currnet", "Market Cap Avg", "Market Cap Adjusted", "FreeFloat", "Traded Value", "Turnover",
                "PreVn30", "Status", "Real Move", "No. of Months from Listing for New Listed Share", "TOP 5 of Market Cap Current", "Median of Top 90% Market Cap Adjusted",
                "Compare with Median of Top 90% Market Cap Adjusted", "Cumulative Top 90% Traded Value", "Reason for rejection"});
        DecimalFormat f = new DecimalFormat("##.0000000");

        for (int i = 0; i < stockListHSX.size(); i++) {
            int index = vn30Result.getlStockVN30().indexOf(stockListHSX.get(i));
            if (index == -1) continue;
            count++;
            if (index < 30) {
                resultsCSV.add(new String[]{"" + count, stockListHSX.get(i).getCode(), "" + BigDecimal.valueOf(stockListHSX.get(i).getGtvhCurrent()).toPlainString(),
                        "" + BigDecimal.valueOf(stockListHSX.get(i).getGTVH()).toPlainString(), "" + BigDecimal.valueOf(stockListHSX.get(i).getGTVH_f()).toPlainString(),
                        "" + stockListHSX.get(i).getF(), "" + BigDecimal.valueOf(stockListHSX.get(i).getGTGD()).toPlainString(),
                        "" + f.format(stockListHSX.get(i).getTurnover() / 100), getCodePre30(stockListHSX.get(i)), "VN30", getInOutVN30(stockListHSX.get(i), index),
                        getLenlistedDate(stockListHSX.get(i).getLenFromListedDate()), getPositiontop5GTVHCurrent(vn30Result.getTop5GTVHCurrent(), stockListHSX.get(i)),
                        getMedianGTVH_f(vn30Result, stockListHSX.get(i).getCode()), getOperator(stockListHSX.get(i), vn30Result.getMedianGTVH_f()), getCumulativeGTGDTop90(vn30Result.getlStockVN30().get(index)), " "}) ;
                continue;
            }
            if (index >= 30 && index < 35) {
                resultsCSV.add(new String[]{"" + count, stockListHSX.get(i).getCode(), "" + BigDecimal.valueOf(stockListHSX.get(i).getGtvhCurrent()).toPlainString(),
                        "" + BigDecimal.valueOf(stockListHSX.get(i).getGTVH()).toPlainString(), "" + BigDecimal.valueOf(stockListHSX.get(i).getGTVH_f()).toPlainString(),
                        "" + stockListHSX.get(i).getF(), "" + BigDecimal.valueOf(stockListHSX.get(i).getGTGD()).toPlainString(),
                        "" + f.format(stockListHSX.get(i).getTurnover() / 100), getCodePre30(stockListHSX.get(i)), "Substitute", getInOutVN30(stockListHSX.get(i), index),
                        getLenlistedDate(stockListHSX.get(i).getLenFromListedDate()), getPositiontop5GTVHCurrent(vn30Result.getTop5GTVHCurrent(), stockListHSX.get(i)),
                        getMedianGTVH_f(vn30Result, stockListHSX.get(i).getCode()), getOperator(stockListHSX.get(i), vn30Result.getMedianGTVH_f()), getCumulativeGTGDTop90(vn30Result.getlStockVN30().get(index)),
                        getReasonDisqualifi(vn30Result, stockListHSX.get(i))});
                continue;
            }
            if (index >= 35) {
                resultsCSV.add(new String[]{"" + count, stockListHSX.get(i).getCode(), "" + BigDecimal.valueOf(stockListHSX.get(i).getGtvhCurrent()).toPlainString(),
                        "" + BigDecimal.valueOf(stockListHSX.get(i).getGTVH()).toPlainString(), "" + BigDecimal.valueOf(stockListHSX.get(i).getGTVH_f()).toPlainString(),
                        "" + stockListHSX.get(i).getF(), "" + BigDecimal.valueOf(stockListHSX.get(i).getGTGD()).toPlainString(),
                        "" + f.format(stockListHSX.get(i).getTurnover() / 100), getCodePre30(stockListHSX.get(i)), " ", getInOutVN30(stockListHSX.get(i), index),
                        getLenlistedDate(stockListHSX.get(i).getLenFromListedDate()), getPositiontop5GTVHCurrent(vn30Result.getTop5GTVHCurrent(), stockListHSX.get(i)),
                        getMedianGTVH_f(vn30Result, stockListHSX.get(i).getCode()), getOperator(stockListHSX.get(i), vn30Result.getMedianGTVH_f()), getCumulativeGTGDTop90(vn30Result.getlStockVN30().get(index)),
                        getReasonDisqualifi(vn30Result, stockListHSX.get(i))});
            }
        }
        resultsCSV.add(new String[]{" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "});
//        resultsCSV.add(new String[]{"Stock REMOVE", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "});
        count = 0;
        for (int i = 0; i < vn30Result.getLsStockHOSE().size(); i++) {
            if (!vn30Result.getlStockVN30().contains(vn30Result.getLsStockHOSE().get(i)) && vn30Result.getLsStockHOSE().get(i).getIsPreVN30()) {
                count++;
                resultsCSV.add(new String[]{"" + count * -1, vn30Result.getLsStockHOSE().get(i).getCode(), "" + BigDecimal.valueOf(vn30Result.getLsStockHOSE().get(i).getGtvhCurrent()).toPlainString(),
                        "" + BigDecimal.valueOf(vn30Result.getLsStockHOSE().get(i).getGTVH()).toPlainString(), "" + BigDecimal.valueOf(vn30Result.getLsStockHOSE().get(i).getGTVH_f()).toPlainString(),
                        "" + vn30Result.getLsStockHOSE().get(i).getF(), "" + BigDecimal.valueOf(vn30Result.getLsStockHOSE().get(i).getGTGD()).toPlainString(),
                        "" + f.format(vn30Result.getLsStockHOSE().get(i).getTurnover() / 100), getCodePre30(vn30Result.getLsStockHOSE().get(i)), " ", "REMOVE",
                        getLenlistedDate(vn30Result.getLsStockHOSE().get(i).getLenFromListedDate()), getPositiontop5GTVHCurrent(vn30Result.getTop5GTVHCurrent(), vn30Result.getLsStockHOSE().get(i)),
                        " ", getOperator(vn30Result.getLsStockHOSE().get(i), vn30Result.getMedianGTVH_f()), getCumulativeGTGDTop90(vn30Result.getLsStockHOSE().get(i)), getReasonDisqualifi(vn30Result, vn30Result.getLsStockHOSE().get(i))});
            }
        }
        for (int i = 0; i < vn30Result.getlStockVN30().size() + 5; i++) {
            if (!vn30Result.getlStockVN30().contains(vn30Result.getLsStockHOSE().get(i)) &&
                    !vn30Result.getlStockVN30().contains(vn30Result.getLsStockHOSE().get(i)) && !vn30Result.getLsStockHOSE().get(i).getIsPreVN30() && !vn30Result.getLsStockHOSE().get(i).getHasWarning() &&
                    ((vn30Result.getLsStockHOSE().get(i).getTurnover() > 0.04 && vn30Result.getLsStockHOSE().get(i).getTurnover() < 0.05) || (vn30Result.getLsStockHOSE().get(i).getCumulativeGTGD() < 0.93) ||
                            (vn30Result.getLsStockHOSE().get(i).getF() < 0.1 && vn30Result.getLsStockHOSE().get(i).getGTVH_f() > 0.9 * vn30Result.getMedianGTVH_f()))) {
                count++;
                resultsCSV.add(new String[]{"" + count * -1, vn30Result.getLsStockHOSE().get(i).getCode(), "" + BigDecimal.valueOf(vn30Result.getLsStockHOSE().get(i).getGtvhCurrent()).toPlainString(),
                        "" + BigDecimal.valueOf(vn30Result.getLsStockHOSE().get(i).getGTVH()).toPlainString(), "" + BigDecimal.valueOf(vn30Result.getLsStockHOSE().get(i).getGTVH_f()).toPlainString(),
                        "" + vn30Result.getLsStockHOSE().get(i).getF(), "" + BigDecimal.valueOf(vn30Result.getLsStockHOSE().get(i).getGTGD()).toPlainString(),
                        "" + f.format(vn30Result.getLsStockHOSE().get(i).getTurnover() / 100), getCodePre30(vn30Result.getLsStockHOSE().get(i)), " ", " ",
                        getLenlistedDate(vn30Result.getLsStockHOSE().get(i).getLenFromListedDate()), getPositiontop5GTVHCurrent(vn30Result.getTop5GTVHCurrent(), vn30Result.getLsStockHOSE().get(i)),
                        " ", getOperator(vn30Result.getLsStockHOSE().get(i), vn30Result.getMedianGTVH_f()), getCumulativeGTGDTop90(vn30Result.getLsStockHOSE().get(i)), getReasonDisqualifi(vn30Result, vn30Result.getLsStockHOSE().get(i))});
            }
        }
        CsvUtil.writeData(path, resultsCSV);
        return path;
    }

    public String getReportStartDay(Date date) {
        List<SecurityImpl> securitiesList = securityPersistence.findByExchange("HSX");
        List<Stock> stockListHSX = new ArrayList<>();
        boolean isPreVN30 = false;
        int count = 0;

        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int day = localDate.getDayOfMonth();
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        int yearPre = localDate.getYear() - 1;
        String path = "D:/Result/vn30_" + day + "-" + month + "-" + year + "_(Data From 01-07-2019).csv";

        Date dateStart = null;
        Date dateEnd = date;
        List<Date> dateHasData = freeFloatRealDataPersistence.findNewestByDate(date);
        Date dateNewFreeFloat = dateHasData.get(0);
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            dateStart = format.parse("2019-06-30");
        } catch (Exception e) {

        }
        List<String> listPreVN30Code = new ArrayList<>();
        List<FreeFloatRealDataImpl> historicalFreeFloatRealDataList = freeFloatRealDataPersistence.findByDate(dateNewFreeFloat);
        for (int i = 0; i < historicalFreeFloatRealDataList.size(); i++) {
            if (historicalFreeFloatRealDataList.get(i).getType().compareTo("VN30") == 0 && historicalFreeFloatRealDataList.get(i).getStt() <= 30) {
                for (int j = 0; j < securitiesList.size(); j++) {
                    if (securitiesList.get(j).getId().compareTo(historicalFreeFloatRealDataList.get(i).getSec_id()) == 0) {
                        listPreVN30Code.add(securitiesList.get(j).getName());
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < securitiesList.size(); i++) {

            if (listPreVN30Code.contains(securitiesList.get(i).getName())) {
                isPreVN30 = true;
            } else {
                isPreVN30 = false;
            }
            List<DailyDataImpl> historicalDailyDataList = dailyDataPersistence.findBySecIdAndDateAfter(securitiesList.get(i).getId(), dateStart, dateEnd);
            List<FreeFloatRealDataImpl> historicalFreeFloatRealDataList2 = freeFloatRealDataPersistence.findBySecIdAndDate(securitiesList.get(i).getId(), dateNewFreeFloat);
            if (historicalDailyDataList.size() == 0) {
                count++;
                continue;
            }
            if (historicalFreeFloatRealDataList2.size() > 0) {
                Stock newStock = new Stock(securitiesList.get(i).getId(), securitiesList.get(i).getName(), securitiesList.get(i).getDate_of_listing(), isPreVN30, historicalFreeFloatRealDataList2.get(0).getFree_float_adj().doubleValue() / 100, historicalDailyDataList);
                stockListHSX.add(newStock);
            } else {
                Stock newStock = new Stock(securitiesList.get(i).getId(), securitiesList.get(i).getName(), securitiesList.get(i).getDate_of_listing(), isPreVN30, historicalDailyDataList);
                stockListHSX.add(newStock);
            }
        }
        VN30Result vn30Result = VN30.getVN30(stockListHSX);
        List<String[]> resultsCSV = new ArrayList<>();

        Boolean write = false;
        resultsCSV.add(new String[]{"Series Number", "Name", "Market Cap Currnet", "Market Cap Avg", "Market Cap Adjusted", "FreeFloat", "Traded Value", "Turnover",
                "PreVn30", "Status", "Real Move", "No. of Months from Listing for New Listed Share", "TOP 5 of Market Cap Current", "Median of Top 90% Market Cap Adjusted",
                "Compare with Median of Top 90% Market Cap Adjusted", "Cumulative Top 90% Traded Value", "Reason for rejection"});
        DecimalFormat f = new DecimalFormat("##.0000000");

        for (int i = 0; i < stockListHSX.size(); i++) {
            int index = vn30Result.getlStockVN30().indexOf(stockListHSX.get(i));
            if (index == -1) continue;
            count++;
            if (index < 30) {
                resultsCSV.add(new String[]{"" + count, stockListHSX.get(i).getCode(), "" + BigDecimal.valueOf(stockListHSX.get(i).getGtvhCurrent()).toPlainString(),
                        "" + BigDecimal.valueOf(stockListHSX.get(i).getGTVH()).toPlainString(), "" + BigDecimal.valueOf(stockListHSX.get(i).getGTVH_f()).toPlainString(),
                        "" + stockListHSX.get(i).getF(), "" + BigDecimal.valueOf(stockListHSX.get(i).getGTGD()).toPlainString(),
                        "" + f.format(stockListHSX.get(i).getTurnover() / 100), getCodePre30(stockListHSX.get(i)), "VN30", getInOutVN30(stockListHSX.get(i), index),
                        getLenlistedDate(stockListHSX.get(i).getLenFromListedDate()), getPositiontop5GTVHCurrent(vn30Result.getTop5GTVHCurrent(), stockListHSX.get(i)),
                        getMedianGTVH_f(vn30Result, stockListHSX.get(i).getCode()), getOperator(stockListHSX.get(i), vn30Result.getMedianGTVH_f()), getCumulativeGTGDTop90(vn30Result.getlStockVN30().get(index)), " "}) ;
                continue;
            }
            if (index >= 30 && index < 35) {
                resultsCSV.add(new String[]{"" + count, stockListHSX.get(i).getCode(), "" + BigDecimal.valueOf(stockListHSX.get(i).getGtvhCurrent()).toPlainString(),
                        "" + BigDecimal.valueOf(stockListHSX.get(i).getGTVH()).toPlainString(), "" + BigDecimal.valueOf(stockListHSX.get(i).getGTVH_f()).toPlainString(),
                        "" + stockListHSX.get(i).getF(), "" + BigDecimal.valueOf(stockListHSX.get(i).getGTGD()).toPlainString(),
                        "" + f.format(stockListHSX.get(i).getTurnover() / 100), getCodePre30(stockListHSX.get(i)), "Substitute", getInOutVN30(stockListHSX.get(i), index),
                        getLenlistedDate(stockListHSX.get(i).getLenFromListedDate()), getPositiontop5GTVHCurrent(vn30Result.getTop5GTVHCurrent(), stockListHSX.get(i)),
                        getMedianGTVH_f(vn30Result, stockListHSX.get(i).getCode()), getOperator(stockListHSX.get(i), vn30Result.getMedianGTVH_f()), getCumulativeGTGDTop90(vn30Result.getlStockVN30().get(index)),
                        getReasonDisqualifi(vn30Result, stockListHSX.get(i))});
                continue;
            }
            if (index >= 35) {
                resultsCSV.add(new String[]{"" + count, stockListHSX.get(i).getCode(), "" + BigDecimal.valueOf(stockListHSX.get(i).getGtvhCurrent()).toPlainString(),
                        "" + BigDecimal.valueOf(stockListHSX.get(i).getGTVH()).toPlainString(), "" + BigDecimal.valueOf(stockListHSX.get(i).getGTVH_f()).toPlainString(),
                        "" + stockListHSX.get(i).getF(), "" + BigDecimal.valueOf(stockListHSX.get(i).getGTGD()).toPlainString(),
                        "" + f.format(stockListHSX.get(i).getTurnover() / 100), getCodePre30(stockListHSX.get(i)), " ", getInOutVN30(stockListHSX.get(i), index),
                        getLenlistedDate(stockListHSX.get(i).getLenFromListedDate()), getPositiontop5GTVHCurrent(vn30Result.getTop5GTVHCurrent(), stockListHSX.get(i)),
                        getMedianGTVH_f(vn30Result, stockListHSX.get(i).getCode()), getOperator(stockListHSX.get(i), vn30Result.getMedianGTVH_f()), getCumulativeGTGDTop90(vn30Result.getlStockVN30().get(index)),
                        getReasonDisqualifi(vn30Result, stockListHSX.get(i))});
            }
        }
        resultsCSV.add(new String[]{" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "});
//        resultsCSV.add(new String[]{"Stock REMOVE", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "});
        count = 0;
        for (int i = 0; i < vn30Result.getLsStockHOSE().size(); i++) {
            if (!vn30Result.getlStockVN30().contains(vn30Result.getLsStockHOSE().get(i)) && vn30Result.getLsStockHOSE().get(i).getIsPreVN30()) {
                count++;
                resultsCSV.add(new String[]{"" + count * -1, vn30Result.getLsStockHOSE().get(i).getCode(), "" + BigDecimal.valueOf(vn30Result.getLsStockHOSE().get(i).getGtvhCurrent()).toPlainString(),
                        "" + BigDecimal.valueOf(vn30Result.getLsStockHOSE().get(i).getGTVH()).toPlainString(), "" + BigDecimal.valueOf(vn30Result.getLsStockHOSE().get(i).getGTVH_f()).toPlainString(),
                        "" + vn30Result.getLsStockHOSE().get(i).getF(), "" + BigDecimal.valueOf(vn30Result.getLsStockHOSE().get(i).getGTGD()).toPlainString(),
                        "" + f.format(vn30Result.getLsStockHOSE().get(i).getTurnover() / 100), getCodePre30(vn30Result.getLsStockHOSE().get(i)), " ", "REMOVE",
                        getLenlistedDate(vn30Result.getLsStockHOSE().get(i).getLenFromListedDate()), getPositiontop5GTVHCurrent(vn30Result.getTop5GTVHCurrent(), vn30Result.getLsStockHOSE().get(i)),
                        " ", getOperator(vn30Result.getLsStockHOSE().get(i), vn30Result.getMedianGTVH_f()), getCumulativeGTGDTop90(vn30Result.getLsStockHOSE().get(i)), getReasonDisqualifi(vn30Result, vn30Result.getLsStockHOSE().get(i))});
            }
        }
        for (int i = 0; i < vn30Result.getlStockVN30().size() + 5; i++) {
            if (!vn30Result.getlStockVN30().contains(vn30Result.getLsStockHOSE().get(i)) &&
                    !vn30Result.getlStockVN30().contains(vn30Result.getLsStockHOSE().get(i)) && !vn30Result.getLsStockHOSE().get(i).getIsPreVN30() && !vn30Result.getLsStockHOSE().get(i).getHasWarning() &&
                    ((vn30Result.getLsStockHOSE().get(i).getTurnover() > 0.04 && vn30Result.getLsStockHOSE().get(i).getTurnover() < 0.05) || (vn30Result.getLsStockHOSE().get(i).getCumulativeGTGD() < 0.93) ||
                            (vn30Result.getLsStockHOSE().get(i).getF() < 0.1 && vn30Result.getLsStockHOSE().get(i).getGTVH_f() > 0.9 * vn30Result.getMedianGTVH_f()))) {
                count++;
                resultsCSV.add(new String[]{"" + count * -1, vn30Result.getLsStockHOSE().get(i).getCode(), "" + BigDecimal.valueOf(vn30Result.getLsStockHOSE().get(i).getGtvhCurrent()).toPlainString(),
                        "" + BigDecimal.valueOf(vn30Result.getLsStockHOSE().get(i).getGTVH()).toPlainString(), "" + BigDecimal.valueOf(vn30Result.getLsStockHOSE().get(i).getGTVH_f()).toPlainString(),
                        "" + vn30Result.getLsStockHOSE().get(i).getF(), "" + BigDecimal.valueOf(vn30Result.getLsStockHOSE().get(i).getGTGD()).toPlainString(),
                        "" + f.format(vn30Result.getLsStockHOSE().get(i).getTurnover() / 100), getCodePre30(vn30Result.getLsStockHOSE().get(i)), " ", " ",
                        getLenlistedDate(vn30Result.getLsStockHOSE().get(i).getLenFromListedDate()), getPositiontop5GTVHCurrent(vn30Result.getTop5GTVHCurrent(), vn30Result.getLsStockHOSE().get(i)),
                        " ", getOperator(vn30Result.getLsStockHOSE().get(i), vn30Result.getMedianGTVH_f()), getCumulativeGTGDTop90(vn30Result.getLsStockHOSE().get(i)), getReasonDisqualifi(vn30Result, vn30Result.getLsStockHOSE().get(i))});
            }
        }
        CsvUtil.writeData(path, resultsCSV);
        return path;
    }

    private String getCodePre30(Stock stock) {
        if (stock.getIsPreVN30()) return stock.getCode();
        else return " ";
    }

    private String getInOutVN30(Stock stock, int index) {
        if (stock.getIsPreVN30()) {
            if (index > 30) return "REMOVE";
            else return " ";
        }
        else {
            if (index < 30) return "ADD";
            else return " ";
        }
    }
    private String getOperator(Stock stock, Double median) {
        if (stock.getF() < 0.1) {
            if (stock.getGTVH_f() > median) return ">";
            else if (stock.getGTVH_f() < median) return "<";
            else return "=";
        }
        return " ";
    }
    private String getPositiontop5GTVHCurrent(List<Stock> top5GTVHCurrent, Stock stock) {
        if (top5GTVHCurrent.indexOf(stock) != -1) return Integer.toString(top5GTVHCurrent.indexOf(stock) + 1);
        else return " ";
    }

    private String getLenlistedDate(int lenListedDate) {
        if (lenListedDate < 6) return Integer.toString(lenListedDate);
        return " ";
    }

    private String getMedianGTVH_f(VN30Result vn30Result, String code) {
        if (code.compareTo(vn30Result.getCodeMedian()) == 0) {
            return BigDecimal.valueOf(vn30Result.getMedianGTVH_f()).toPlainString();
        }
        return " ";
    }

    private String getCumulativeGTGDTop90(Stock stock) {
        if (stock.getCumulativeGTGD() >= 0.9 && stock.getCumulativeGTGD() < 0.93) {
            return BigDecimal.valueOf(stock.getCumulativeGTGD()).toPlainString();
        }
        return " ";
    }

    private String getReasonDisqualifi(VN30Result vn30Result, Stock stock) {
        List<String> lStrOutput = new ArrayList<>();
        String strOutput = "";
//        if (stock.getIsPreVN30()) {
//            System.out.println(stock.getCode());
//            if (stock.getHasWarning()) strOutput += "Has Warning";
//            if (stock.getLenFromListedDate() < 6 && (stock.getLenFromListedDate() < 3 || !vn30Result.getTop5GTVHCurrent().contains(stock)))
//                lStrOutput.add("Length from Listed Date too short");
//            if (stock.getF() < 0.1 && stock.getGTVH_f() < vn30Result.getMedianGTVH_f())
//                lStrOutput.add("Free Float < 10% and GTVH_f < Median top 90% GTVH_f");
//            if (stock.getTurnover() < 0.05 && (stock.getTurnover() < 0.04 || !stock.getIsPreVN30()))
//                lStrOutput.add("Turnover too small");
//            if (!vn30Result.getlStockVN30().contains(stock) && lStrOutput.size() == 0) lStrOutput.add("Traded value not in top 90% GTGD");
//            if (vn30Result.getlStockVN30().contains(stock) && vn30Result.getlStockVN30().indexOf(stock) > 40)
//                lStrOutput.add("Outside the top 40 GTVH");
//        }
//        for (int i = 0; i < lStrOutput.size(); i++) strOutput += lStrOutput.get(i) + "; ";

        return strOutput;
    }

    public void getMedian(List<String> stringList) {
        List<SecurityImpl> securitiesList = securityPersistence.findByExchange("HSX");
        List<Stock> stockListHSX = new ArrayList<>();

        Date dateStart = null;
        Date dateEnd = null;

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            dateStart = format.parse("2018-12-31");
            dateEnd = format.parse("2020-02-14");
        } catch (Exception e) {

        }
        for (int i = 0; i < securitiesList.size(); i++) {
            if (!stringList.contains(securitiesList.get(i).getName())) continue;
            List<DailyDataImpl> historicalDailyDataList = dailyDataPersistence.findBySecIdAndDateAfter(securitiesList.get(i).getId(), dateStart, dateEnd);
            Stock newStock = new Stock(securitiesList.get(i).getId(), securitiesList.get(i).getName(), historicalDailyDataList);
            stockListHSX.add(newStock);
        }
        try {
            FileOutputStream fos = new FileOutputStream("D:/Result/median.csv");
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeChars("Time");
            for (int i = 0; i < stockListHSX.size(); i++) {
                dos.writeChars(";" + stockListHSX.get(i).getCode());
            }
            dos.writeChars("\n");
            for (int j = 0; j < stockListHSX.get(0).getMonthList().size(); j++) {
                dos.writeChars(stockListHSX.get(0).getMonthList().get(j).toString());
                for (int i = 0; i < stockListHSX.size(); i++) {
                    dos.writeChars(";" + stockListHSX.get(i).getMedianList().get(j).toString());
                }
                dos.writeChars("\n");
            }
            dos.close();
            fos.close();
        } catch (Exception e) {

        }

    }
}
