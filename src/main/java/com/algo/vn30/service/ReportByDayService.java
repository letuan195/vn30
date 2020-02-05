package com.algo.vn30.service;

import com.algo.vn30.entity.*;
import com.algo.vn30.persistence.DailyDataPersistence;
import com.algo.vn30.persistence.FreeFloatDataPersistence;
import com.algo.vn30.persistence.FreeFloatRealDataPersistence;
import com.algo.vn30.persistence.SecurityPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReportByDayService implements ReportByDayImpl {

    @Autowired
    private SecurityPersistence securityPersistence;
    @Autowired
    private DailyDataPersistence dailyDataPersistence;
    @Autowired
    private FreeFloatDataPersistence freeFloatDataPersistence;
    @Autowired
    private FreeFloatRealDataPersistence freeFloatRealDataPersistence;

    @Override
    public void getReport(Date date) {
        List<SecurityImpl> securitiesList = securityPersistence.findByExchange("HSX");
        List<Stock> stockListHSX = new ArrayList<>();
        Boolean isPreVN30 = false;
        int count = 0;

        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Integer day = localDate.getDayOfMonth();
        Integer month = localDate.getMonthValue();
        Integer year = localDate.getYear();
        Integer yearPre = localDate.getYear() - 1;

        Date dateStart = null;
        Date dateEnd = date;
        List<Date> dateHasData = freeFloatRealDataPersistence.findNewestByDate(date);
        Date dateNewFreeFloat = dateHasData.get(0);
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            dateStart = format.parse(yearPre.toString() + "-" + month.toString() + "-" + day.toString());
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
//            System.out.println(securitiesList.get(i).getName());

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
//            System.out.println(historicalFreeFloatRealDataList2.size());
            if (historicalFreeFloatRealDataList2.size() > 0) {
                Stock newStock = new Stock(securitiesList.get(i).getId(), securitiesList.get(i).getName(), isPreVN30, historicalFreeFloatRealDataList2.get(0).getFree_float_adj().doubleValue() / 100, historicalDailyDataList);
                stockListHSX.add(newStock);
            }
            else {
                Stock newStock = new Stock(securitiesList.get(i).getId(), securitiesList.get(i).getName(), isPreVN30, historicalDailyDataListCurrent, historicalDailyDataList);
                stockListHSX.add(newStock);
            }
        }
//        System.out.println(count);
        List<Stock> stockVN30 = VN30.getVN30(stockListHSX);
        System.out.println("Write File!!!!");
        try {
            Boolean write = false;
            FileOutputStream fos = new FileOutputStream("D:/Result/vn30_" + day.toString() + "-" + month.toString() + "-" + year.toString() + ".csv");
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeChars("sep=;\n");
            dos.writeChars("STT;Name;GTVH;FreeFloat;GTGD;TurnOver;PreVN30;Real Move\n");

            for (int i = 0; i < 30; i++) {
                write = false;
                for (int j = 0; j < listPreVN30Code.size(); j++) {
                    if (stockVN30.get(i).getCode().compareTo(listPreVN30Code.get(j)) == 0) {
                        dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                                ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover() / 100) +
                                ";" + stockVN30.get(i).getCode() + ";VN30" + "\n");
                        write = true;
                        break;
                    }
                }
                if (!write) {
                    dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                            ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover() / 100) + ";;VN30;ADD\n");
                }
            }

            for (int i = 30; i < 35; i++) {
                write = false;
                for (int j = 0; j < listPreVN30Code.size(); j++) {
                    if (stockVN30.get(i).getCode().compareTo(listPreVN30Code.get(j)) == 0) {
                        dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                                ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover() / 100) +
                                ";" + stockVN30.get(i).getCode() + ";Substitute;REMOVE" + "\n");
                        write = true;
                        break;
                    }
                }
                if (!write) {
                    dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                            ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover() / 100) + ";;Substitute\n");
                }
            }

            for (int i = 35; i < stockVN30.size(); i++) {
                write = false;
                for (int j = 0; j < listPreVN30Code.size(); j++) {
                    if (stockVN30.get(i).getCode().compareTo(listPreVN30Code.get(j)) == 0) {
                        dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                                ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover() / 100) +
                                ";" + stockVN30.get(i).getCode() + ";;REMOVE" + "\n");
                        write = true;
                        break;
                    }
                }
                if (!write) {
                    dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                            ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover() / 100) + ";;\n");
                }
            }
            fos.close();
            dos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
}
