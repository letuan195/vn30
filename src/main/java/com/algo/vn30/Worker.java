package com.algo.vn30;

import com.algo.vn30.entity.*;
import com.algo.vn30.persistence.DailyDataPersistence;
import com.algo.vn30.persistence.FreeFloatDataPersistence;
import com.algo.vn30.persistence.FreeFloatRealDataPersistence;
import com.algo.vn30.persistence.SecurityPersistence;
import com.algo.vn30.service.ReportDayImpl;
import com.algo.vn30.utils.Mail;
import com.algo.vn30.worker.AbstractWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class Worker extends AbstractWorker {

    @Autowired
    private SecurityPersistence securityPersistence;
    @Autowired
    private DailyDataPersistence dailyDataPersistence;
    @Autowired
    private FreeFloatDataPersistence freeFloatDataPersistence;
    @Autowired
    private FreeFloatRealDataPersistence freeFloatRealDataPersistence;

    @Autowired
    ReportDayImpl reportByDay;

    @Override
    public void onStarted() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String path = null;
        try {
            path = reportByDay.getReport(format.parse(format.format(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String root = System.getProperty("user.dir");
        Mail.sendGmail(root + "/data/"+path);
    }

    @Scheduled(cron = "0 0 15 * * *")
    public void getVN30Everyday(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String path = null;
        try {
            path = reportByDay.getReport(format.parse(format.format(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String root = System.getProperty("user.dir");
        Mail.sendGmail(root + "/data/"+path);
    }

    public void vn30() throws ParseException, IOException {
        List<SecurityImpl> securitiesList = securityPersistence.findByExchange("HSX");
        List<Stock> stockListHSX = new ArrayList<>();
        Boolean isPreVN30 = false;

//        File file = new File("D:\\new.txt");
//        BufferedReader br = new BufferedReader(new FileReader(file));
//        String st;
//        String[] spl = new String[0];
//        List<String> lsCode = new ArrayList<>();
//        List<Double> lsFree = new ArrayList<>();
//        while ((st = br.readLine()) != null) {
//            spl = st.split(";", -2);
//            lsCode.add(spl[0]);
//            lsFree.add(Double.parseDouble(spl[1]) / 100);
//        }

        String[] preVN30Code = {"BID", "BVH", "CTD", "CTG", "DPM", "EIB", "FPT", "GAS", "GMD", "HDB", "HPG", "MBB", "MSN", "MWG",
                "NVL", "PNJ", "REE", "ROS", "SAB", "SBT", "SSI", "STB", "TCB", "VCB", "VHM", "VIC", "VJC", "VNM", "VPB", "VRE"};
        List<String> listPreVN30Code = Arrays.asList(preVN30Code);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dateStart = format.parse("2019-02-01");
        Date dateEnd = format.parse("2020-01-30");
        Date dateThis = format.parse("2020-01-20");

        for (int i = 0; i < securitiesList.size(); i++) {
            System.out.println(securitiesList.get(i).getName());
            if (securitiesList.get(i).getName().compareTo("DPM") == 0) {
                System.out.println(securitiesList.get(i).getName());
            }
            if (listPreVN30Code.contains(securitiesList.get(i).getName())) {
                isPreVN30 = true;
            } else {
                isPreVN30 = false;
            }
            List<DailyDataImpl> historicalDailyDataListCurrent = dailyDataPersistence.findBySecIdAndThisDate(securitiesList.get(i).getId(), dateThis);
            List<DailyDataImpl> historicalDailyDataList = dailyDataPersistence.findBySecIdAndDateAfter(securitiesList.get(i).getId(), dateStart, dateEnd);
            List<FreeFloatDataImpl> historicalFreeFloatDataList = freeFloatDataPersistence.findBySecIdAndDate(securitiesList.get(i).getId(), dateThis);
            if (historicalDailyDataList.size() == 0) {
                continue;
            }
            System.out.println(historicalFreeFloatDataList.size());
            if (historicalFreeFloatDataList.size() > 0) {
                Stock newStock = new Stock(securitiesList.get(i).getId(), securitiesList.get(i).getName(), isPreVN30, historicalFreeFloatDataList.get(0).getFree_float_adj().doubleValue() / 100, historicalDailyDataList);
                stockListHSX.add(newStock);
            }
            else {
                Stock newStock = new Stock(securitiesList.get(i).getId(), securitiesList.get(i).getName(), isPreVN30, historicalDailyDataListCurrent, historicalDailyDataList);
                stockListHSX.add(newStock);
            }
        }

        List<Stock> stockVN30 = VN30.getVN30(stockListHSX);
        System.out.println("Write File!!!!");
        try {
            FileOutputStream fos = new FileOutputStream("D:/Result/vn30_30-01-2019.csv");
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeChars("sep=;\n");
            dos.writeChars("STT;Name;GTVH;FreeFloat;GTGD;TurnOver;PreVN30\n");
            Boolean write = false;
            for (int i = 0; i < stockVN30.size(); i++) {
                write = false;
//                System.out.println(stockVN30.get(i).getCode());
                for (int j = 0; j < preVN30Code.length; j++) {
                    if (stockVN30.get(i).getCode().compareTo(preVN30Code[j]) == 0) {
                        if (i < 30) {
                            dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                                    ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover() / 100) +
                                    ";" + stockVN30.get(i).getCode() + "\n");
                            write = true;
                            break;
                        }
                        else {
                            dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                                    ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover() / 100) +
                                    ";" + stockVN30.get(i).getCode() + ";REMOVE" + "\n");
                            write = true;
                            break;
                        }
                    }
                }
                if (!write) {
                    if (i >= 30) {

                        dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                                ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover() / 100) + "\n");
                    }
                    else {
                        dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                                ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover() / 100) + ";;ADD\n");
                    }
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

    public void vn30_backup() throws ParseException, IOException {
        List<SecurityImpl> securitiesList = securityPersistence.findByExchange("HSX");
//        List<SecurityImpl> securitiesList = securityPersistence.findByTest("HSX", 31L);
        List<Stock> stockListHSX = new ArrayList<>();
        Boolean isPreVN30 = false;
        int count = 0;

//        File file = new File("D:\\new.txt");
//        BufferedReader br = new BufferedReader(new FileReader(file));
//        String st;
//        String[] spl = new String[0];
//        List<String> lsCode = new ArrayList<>();
//        List<Double> lsFree = new ArrayList<>();
//        while ((st = br.readLine()) != null) {
//            spl = st.split(";", -2);
//            lsCode.add(spl[0]);
//            lsFree.add(Double.parseDouble(spl[1]) / 100);
//        }

//        String[] preVN30Code = {"BID", "BVH", "CTD", "CTG", "DPM", "EIB", "FPT", "GAS", "GMD", "HDB", "HPG", "MBB", "MSN", "MWG",
//                "NVL", "PNJ", "REE", "ROS", "SAB", "SBT", "SSI", "STB", "TCB", "VCB", "VHM", "VIC", "VJC", "VNM", "VPB", "VRE"};
//        List<String> listPreVN30Code = Arrays.asList(preVN30Code);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dateStart = format.parse("2019-01-01");
        Date dateEnd = format.parse("2019-12-31");
        Date dateThis = format.parse("2020-01-20");
        Date dateThat = format.parse("2019-07-15");
        Date dateTemp = format.parse("2020-01-20");

        List<String> listPreVN30Code = new ArrayList<>();
        List<FreeFloatRealDataImpl> historicalFreeFloatRealDataList = freeFloatRealDataPersistence.findByDate(dateThat);
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
            System.out.println(securitiesList.get(i).getName());
            if (securitiesList.get(i).getName().compareTo("PLX") == 0) {
                System.out.println(securitiesList.get(i).getName());
            }
            if (listPreVN30Code.contains(securitiesList.get(i).getName())) {
                isPreVN30 = true;
            } else {
                isPreVN30 = false;
            }
            List<DailyDataImpl> historicalDailyDataListCurrent = dailyDataPersistence.findBySecIdAndThisDate(securitiesList.get(i).getId(), dateEnd);
            List<DailyDataImpl> historicalDailyDataList = dailyDataPersistence.findBySecIdAndDateAfter(securitiesList.get(i).getId(), dateStart, dateEnd);
            List<FreeFloatRealDataImpl> historicalFreeFloatRealDataList2 = freeFloatRealDataPersistence.findBySecIdAndDate(securitiesList.get(i).getId(), dateThis);
            if (historicalDailyDataList.size() == 0) {
                count++;
                continue;
            }
            System.out.println(historicalFreeFloatRealDataList2.size());
            if (historicalFreeFloatRealDataList2.size() > 0) {
                Stock newStock = new Stock(securitiesList.get(i).getId(), securitiesList.get(i).getName(), isPreVN30, historicalFreeFloatRealDataList2.get(0).getFree_float_adj().doubleValue() / 100, historicalDailyDataList);
                stockListHSX.add(newStock);
            }
            else {
                Stock newStock = new Stock(securitiesList.get(i).getId(), securitiesList.get(i).getName(), isPreVN30, historicalDailyDataListCurrent, historicalDailyDataList);
                stockListHSX.add(newStock);
            }
        }
        for(int i = 0; i < stockListHSX.size(); i++) {
            if (stockListHSX.get(i).getLenFromListedDate() <= 7) {
                System.out.println(stockListHSX.get(i).getCode() + "         " + stockListHSX.get(i).getLenFromListedDate());
            }
        }
        System.out.println(count);
        List<Stock> stockVN30 = VN30.getVN30(stockListHSX);
        System.out.println("Write File!!!!");
        try {
            FileOutputStream fos = new FileOutputStream("D:/Result/vn30_31-12-2019_test.csv");
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeChars("sep=;\n");
            dos.writeChars("STT;Name;GTVH;FreeFloat;GTGD;TurnOver;PreVN30\n");
            Boolean write = false;
            for (int i = 0; i < stockVN30.size(); i++) {
                write = false;
//                System.out.println(stockVN30.get(i).getCode());
                for (int j = 0; j < listPreVN30Code.size(); j++) {
                    if (stockVN30.get(i).getCode().compareTo(listPreVN30Code.get(j)) == 0) {
                        if (i < 30) {
                            dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                                    ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover() / 100) +
                                    ";" + stockVN30.get(i).getCode() + "\n");
                            write = true;
                            break;
                        }
                        else {
                            dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                                    ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover() / 100) +
                                    ";" + stockVN30.get(i).getCode() + ";REMOVE" + "\n");
                            write = true;
                            break;
                        }
                    }
                }
                if (!write) {
                    if (i >= 30) {

                        dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                                ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover() / 100) + "\n");
                    }
                    else {
                        dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                                ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover() / 100) + ";;ADD\n");
                    }
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

    public void vn30_backup2(Date date) throws ParseException, IOException {
        List<SecurityImpl> securitiesList = securityPersistence.findByExchange("HSX");
        List<Stock> stockListHSX = new ArrayList<>();
        Boolean isPreVN30 = false;

        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Integer day = localDate.getDayOfMonth();
        Integer month = localDate.getMonthValue();
        Integer year = localDate.getYear() - 1;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dateStart = format.parse(year.toString() + "-" + month.toString() + "-" + day.toString());
        Date dateEnd = format.parse("2020-01-30");
        Date dateThis = format.parse("2020-1-20");
        Date dateThat = format.parse("2020-1-20");
        Date dateTemp = format.parse("2020-01-20");

        List<String> listPreVN30Code = new ArrayList<>();
        List<FreeFloatRealDataImpl> historicalFreeFloatRealDataList = freeFloatRealDataPersistence.findByDate(dateThat);
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
            System.out.println(securitiesList.get(i).getName());
            if (listPreVN30Code.contains(securitiesList.get(i).getName())) {
                isPreVN30 = true;
            } else {
                isPreVN30 = false;
            }
            List<DailyDataImpl> historicalDailyDataListCurrent = dailyDataPersistence.findBySecIdAndThisDate(securitiesList.get(i).getId(), dateThis);
            List<DailyDataImpl> historicalDailyDataList = dailyDataPersistence.findBySecIdAndDateAfter(securitiesList.get(i).getId(), dateStart, dateEnd);
            List<FreeFloatRealDataImpl> historicalFreeFloatRealDataList2 = freeFloatRealDataPersistence.findBySecIdAndDate(securitiesList.get(i).getId(), dateThis);
            if (historicalDailyDataList.size() == 0) {
                continue;
            }
            System.out.println(historicalFreeFloatRealDataList2.size());
            if (historicalFreeFloatRealDataList2.size() > 0) {
                Stock newStock = new Stock(securitiesList.get(i).getId(), securitiesList.get(i).getName(), isPreVN30, historicalFreeFloatRealDataList2.get(0).getFree_float_adj().doubleValue() / 100, historicalDailyDataList);
                stockListHSX.add(newStock);
            }
            else {
                Stock newStock = new Stock(securitiesList.get(i).getId(), securitiesList.get(i).getName(), isPreVN30, historicalDailyDataListCurrent, historicalDailyDataList);
                stockListHSX.add(newStock);
            }
        }

        List<Stock> stockVN30 = VN30.getVN30(stockListHSX);
        System.out.println("Write File!!!!");
        try {
            FileOutputStream fos = new FileOutputStream("D:/Result/vn30_31-12-2018.csv");
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeChars("sep=;\n");
            dos.writeChars("STT;Name;GTVH;FreeFloat;GTGD;TurnOver;PreVN30\n");
            Boolean write = false;
            for (int i = 0; i < stockVN30.size(); i++) {
                write = false;
//                System.out.println(stockVN30.get(i).getCode());
                for (int j = 0; j < listPreVN30Code.size(); j++) {
                    if (stockVN30.get(i).getCode().compareTo(listPreVN30Code.get(j)) == 0) {
                        if (i < 30) {
                            dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                                    ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover() / 100) +
                                    ";" + stockVN30.get(i).getCode() + "\n");
                            write = true;
                            break;
                        }
                        else {
                            dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                                    ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover() / 100) +
                                    ";" + stockVN30.get(i).getCode() + ";REMOVE" + "\n");
                            write = true;
                            break;
                        }
                    }
                }
                if (!write) {
                    if (i >= 30) {

                        dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                                ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover() / 100) + "\n");
                    }
                    else {
                        dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                                ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover() / 100) + ";;ADD\n");
                    }
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

        @Override
    public void onStoping() {

    }
}
