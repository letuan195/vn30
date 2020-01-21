package com.algo.vn30;

import com.algo.vn30.entity.DailyDataImpl;
import com.algo.vn30.entity.SecurityImpl;
import com.algo.vn30.entity.Stock;
import com.algo.vn30.entity.VN30;
import com.algo.vn30.persistence.DailyDataPersistence;
import com.algo.vn30.persistence.SecurityPersistence;
import com.algo.vn30.worker.AbstractWorker;
import com.google.gson.internal.$Gson$Preconditions;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.Security;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @Override
    public void onStarted() {
        // do something
        System.out.println("run ok");
        try { vn30(); }
        catch (Exception i) {

        }
    }

    public void vn30() throws ParseException, IOException {
        List<SecurityImpl> securitiesList = securityPersistence.findByExchange("HSX");
        List<Stock> stockListHSX = new ArrayList<>();
        Boolean isPreVN30 = false;

        File file = new File("D:\\new.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        String[] spl = new String[0];
        List<String> lsCode = new ArrayList<>();
        List<Double> lsFree = new ArrayList<>();
        while ((st = br.readLine()) != null) {
            spl = st.split(";", -2);
            lsCode.add(spl[0]);
            lsFree.add(Double.parseDouble(spl[1]) / 100);
        }

        String[] preVN30Code = {"BID", "BVH", "CTD", "CTG", "DPM", "EIB", "FPT", "GAS", "GMD", "HDB", "HPG", "MBB", "MSN", "MWG",
                "NVL", "PNJ", "REE", "ROS", "SAB", "SBT", "SSI", "STB", "TCB", "VCB", "VHM", "VIC", "VJC", "VNM", "VPB", "VRE"};
        List<String> listPreVN30Code = Arrays.asList(preVN30Code);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dateStart = format.parse("2019-01-01");
        Date dateEnd = format.parse("2019-12-31");
        Date dateThis = format.parse("2020-01-20");

        for (int i = 0; i < securitiesList.size(); i++) {
            System.out.println(securitiesList.get(i).getName());
//            if (securitiesList.get(i).getName().compareTo("YEG") == 0) {
//                System.out.println("here");
//            }
            if (listPreVN30Code.contains(securitiesList.get(i).getName())) {
                isPreVN30 = true;
            } else {
                isPreVN30 = false;
            }
            List<DailyDataImpl> historicalDailyDataListCurrent = dailyDataPersistence.findBySecIdAndThisDate(securitiesList.get(i).getId(), dateThis);
            List<DailyDataImpl> historicalDailyDataList = dailyDataPersistence.findBySecIdAndDateAfter(securitiesList.get(i).getId(), dateStart, dateEnd);
            if (historicalDailyDataList.size() == 0) {
                continue;
            }
            if (lsCode.contains(securitiesList.get(i).getName())) {
                Stock newStock = new Stock(securitiesList.get(i).getId(), securitiesList.get(i).getName(), isPreVN30, lsFree.get(lsCode.indexOf(securitiesList.get(i).getName())), historicalDailyDataList);
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
            FileOutputStream fos = new FileOutputStream("D:/Result/vn30.csv");
            DataOutputStream dos = new DataOutputStream(fos);

            dos.writeChars("STT;Name;GTVH;FreeFloat;GTGD;TurnOver;PreVN30\n");
            Boolean write = false;
            for (int i = 0; i < stockVN30.size(); i++) {
                write = false;
//                System.out.println(stockVN30.get(i).getCode());
                for (int j = 0; j < preVN30Code.length; j++) {
                    if (stockVN30.get(i).getCode().compareTo(preVN30Code[j]) == 0) {
                        dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                                ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover() / 100) +
                                ";" + stockVN30.get(i).getCode() + "\n");
                        write = true;
                        break;
                    }
                }
                if (!write) {
                    dos.writeChars(Integer.toString(i + 1) + ";" + stockVN30.get(i).getCode() + ";" + Double.toString(stockVN30.get(i).getGTVH()) +
                            ";" + Double.toString(stockVN30.get(i).getF()) + ";" + Double.toString(stockVN30.get(i).getGTGD()) + ";" + Double.toString(stockVN30.get(i).getTurnover()) + "\n");
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
