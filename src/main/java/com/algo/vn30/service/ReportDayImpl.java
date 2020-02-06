package com.algo.vn30.service;

import com.algo.vn30.entity.*;
import com.algo.vn30.persistence.DailyDataPersistence;
import com.algo.vn30.persistence.FreeFloatDataPersistence;
import com.algo.vn30.persistence.FreeFloatRealDataPersistence;
import com.algo.vn30.persistence.SecurityPersistence;
import com.algo.vn30.utils.CsvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        String path = "vn30_" + day + "-" + month + "-" + year + ".csv";

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
            List<DailyDataImpl> historicalDailyDataListCurrent = dailyDataPersistence.findBySecIdAndThisDate(securitiesList.get(i).getId(), dateEnd);
            List<DailyDataImpl> historicalDailyDataList = dailyDataPersistence.findBySecIdAndDateAfter(securitiesList.get(i).getId(), dateStart, dateEnd);
            List<FreeFloatRealDataImpl> historicalFreeFloatRealDataList2 = freeFloatRealDataPersistence.findBySecIdAndDate(securitiesList.get(i).getId(), dateNewFreeFloat);
            if (historicalDailyDataList.size() == 0) {
                count++;
                continue;
            }
            if (historicalFreeFloatRealDataList2.size() > 0) {
                Stock newStock = new Stock(securitiesList.get(i).getId(), securitiesList.get(i).getName(), isPreVN30, historicalFreeFloatRealDataList2.get(0).getFree_float_adj().doubleValue() / 100, historicalDailyDataList);
                stockListHSX.add(newStock);
            } else {
                Stock newStock = new Stock(securitiesList.get(i).getId(), securitiesList.get(i).getName(), isPreVN30, historicalDailyDataListCurrent, historicalDailyDataList);
                stockListHSX.add(newStock);
            }
        }
        List<Stock> stockVN30 = VN30.getVN30(stockListHSX);
        List<String[]> resultsCSV = new ArrayList<>();

        Boolean write = false;
        resultsCSV.add(new String[]{"STT", "Name", "GTVH", "FreeFloat", "GTGD", "Turnover", "PreVn30", "Real Move"});
        DecimalFormat f = new DecimalFormat("##.0000");

        for (int i = 0; i < 30; i++) {
            write = false;
            for (int j = 0; j < listPreVN30Code.size(); j++) {
                if (stockVN30.get(i).getCode().compareTo(listPreVN30Code.get(j)) == 0) {
                    resultsCSV.add(new String[]{"" + (i + 1), stockVN30.get(i).getCode(), "" + BigDecimal.valueOf(stockVN30.get(i).getGTVH()).toPlainString(), "" + stockVN30.get(i).getF(),
                            "" + BigDecimal.valueOf(stockVN30.get(i).getGTGD()).toPlainString(), "" + f.format(stockVN30.get(i).getTurnover() / 100), "" + stockVN30.get(i).getCode(), "VN30"});
                    write = true;
                    break;
                }
            }
            if (!write) {
                resultsCSV.add(new String[]{"" + (i + 1), stockVN30.get(i).getCode(), "" + BigDecimal.valueOf(stockVN30.get(i).getGTVH()).toPlainString(), "" + stockVN30.get(i).getF(),
                        "" + BigDecimal.valueOf(stockVN30.get(i).getGTGD()).toPlainString(), "" + f.format(stockVN30.get(i).getTurnover() / 100), "" + stockVN30.get(i).getCode(), "VN30 (ADD)"});
            }
        }

        for (int i = 30; i < 35; i++) {
            write = false;
            for (int j = 0; j < listPreVN30Code.size(); j++) {
                if (stockVN30.get(i).getCode().compareTo(listPreVN30Code.get(j)) == 0) {
                    resultsCSV.add(new String[]{"" + (i + 1), stockVN30.get(i).getCode(), "" + BigDecimal.valueOf(stockVN30.get(i).getGTVH()).toPlainString(), "" + stockVN30.get(i).getF(),
                            "" + BigDecimal.valueOf(stockVN30.get(i).getGTGD()).toPlainString(), "" + f.format(stockVN30.get(i).getTurnover() / 100), "" + stockVN30.get(i).getCode(), "Substitute (REMOVE)"});
                    write = true;
                    break;
                }
            }
            if (!write) {
                resultsCSV.add(new String[]{"" + (i + 1), stockVN30.get(i).getCode(), "" + BigDecimal.valueOf(stockVN30.get(i).getGTVH()).toPlainString(), "" + stockVN30.get(i).getF(),
                        "" + BigDecimal.valueOf(stockVN30.get(i).getGTGD()).toPlainString(), "" + f.format(stockVN30.get(i).getTurnover() / 100), "", "Substitute"});
            }
        }

        for (int i = 35; i < stockVN30.size(); i++) {
            write = false;
            for (int j = 0; j < listPreVN30Code.size(); j++) {
                if (stockVN30.get(i).getCode().compareTo(listPreVN30Code.get(j)) == 0) {
                    resultsCSV.add(new String[]{"" + (i + 1), stockVN30.get(i).getCode(), "" + BigDecimal.valueOf(stockVN30.get(i).getGTVH()).toPlainString(), "" + stockVN30.get(i).getF(),
                            "" + stockVN30.get(i).getGTGD().toString(), "" + f.format(stockVN30.get(i).getTurnover() / 100), "" + stockVN30.get(i).getCode(), "(REMOVE)"});
                    write = true;
                    break;
                }
            }
            if (!write) {
                resultsCSV.add(new String[]{"" + (i + 1), stockVN30.get(i).getCode(), "" + BigDecimal.valueOf(stockVN30.get(i).getGTVH()).toPlainString(), "" + stockVN30.get(i).getF(),
                        "" + stockVN30.get(i).getGTGD().toString(), "" + f.format(stockVN30.get(i).getTurnover() / 100), "", ""});
            }
        }
        CsvUtil.writeData(path, resultsCSV);
        return path;
    }
}
