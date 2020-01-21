package com.algo.vn30.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VN30 {
    public static void sortListStockByGTGD(List<Stock> lsStocksVNAllShare) {
        for (int i = 0; i < lsStocksVNAllShare.size() - 1; i++) {
            for (int j = i + 1; j < lsStocksVNAllShare.size(); j++) {
                if (lsStocksVNAllShare.get(i).getGTGD() < lsStocksVNAllShare.get(j).getGTGD()) {
                    Collections.swap(lsStocksVNAllShare, i, j);
                }
            }
        }
    }

    public static void sortListStockByGTVH_f(List<Stock> lsStocks) {
        for (int i = 0; i < lsStocks.size() - 1; i++) {
            for (int j = i + 1; j < lsStocks.size(); j++) {
                if (lsStocks.get(i).getGTVH_f() < lsStocks.get(j).getGTVH_f()) {
                    Collections.swap(lsStocks, i, j);
                }
            }
        }
    }

    public static void sortListStockByGTVH(List<Stock> lsStocks) {
        for (int i = 0; i < lsStocks.size() - 1; i++) {
            for (int j = i + 1; j < lsStocks.size(); j++) {
                if (lsStocks.get(i).getGTVH() < lsStocks.get(j).getGTVH()) {
                    Collections.swap(lsStocks, i, j);
                }
            }
        }
    }

    public static void sortListStockByGTVHCurrent(List<Stock> lsStocks) {
        for (int i = 0; i < lsStocks.size() - 1; i++) {
            for (int j = i + 1; j < lsStocks.size(); j++) {
                if (lsStocks.get(i).getGtvhCurrent() < lsStocks.get(j).getGtvhCurrent()) {
                    Collections.swap(lsStocks, i, j);
                }
            }
        }
    }

    public static void sortCode(List<Stock> lsStocks) {
        for (int i = 0; i < lsStocks.size() - 1; i++) {
            for (int j = i + 1; j < lsStocks.size(); j++) {
                if (lsStocks.get(j).getCode().compareTo(lsStocks.get(i).getCode()) < 0) {
                    Collections.swap(lsStocks, i, j);
                }
            }
        }
    }

    public static List<Stock> getTop5StockGTVH(List<Stock> lsStocks) {
        List<Stock> lsTop5GTVH = new ArrayList<>();
        sortListStockByGTVHCurrent(lsStocks);

        for (int i = 0; i < 5; i++) lsTop5GTVH.add(lsStocks.get(i));

        return lsTop5GTVH;
    }

    public static Double calMedianGTVH_f(List<Stock> lsStocks) {
        Double median;
        sortListStockByGTVH_f(lsStocks);
        Double sumGTVH_f = 0.0;
        Double sumCumulativeGTVH_f = 0.0;
        int index = 0;

        for (int i = 0; i < lsStocks.size(); i++) { sumGTVH_f += lsStocks.get(i).getGTVH_f(); }
        for (int i = 0; i < lsStocks.size(); i++) {
            sumCumulativeGTVH_f += lsStocks.get(i).getGTVH_f();
            index += 1;
            if (sumCumulativeGTVH_f / sumGTVH_f >= 0.9) { break; }
        }

        if (index % 2 == 0) { median = (lsStocks.get(index / 2).getGTVH_f() + lsStocks.get(index / 2 - 1).getGTVH_f()) / 2; }
        else { median = lsStocks.get((index -1) / 2).getGTVH_f(); }

        return median;
    }

    public static List<Stock> getListStocksCumulativeGTGD(List<Stock> lsStocksVNAllShare) {
        List<Stock> lsStockCumulative = new ArrayList<>();
        sortListStockByGTGD(lsStocksVNAllShare);
        Double sumGTGD = 0.0;
        Double sumCumulativeGTGD = 0.0;
        Double threshHold = 0.9;

        for (int i = 0; i < lsStocksVNAllShare.size(); i++) {
            sumGTGD += lsStocksVNAllShare.get(i).getGTGD();
        }

        for (int i = 0; i < lsStocksVNAllShare.size(); i++) {
            sumCumulativeGTGD += lsStocksVNAllShare.get(i).getGTGD();
            lsStockCumulative.add(lsStocksVNAllShare.get(i));
            if (sumCumulativeGTGD / sumGTGD >= threshHold) {
                if (lsStockCumulative.size() < 50) { threshHold += 0.01; }
                else { break; }
            }
        }
        sortListStockByGTVH(lsStockCumulative);

        return lsStockCumulative;
    }

    public static List<Stock> getVN30(List<Stock> lsStockHOSE) {
        List<Stock> lsVN30 = new ArrayList<>();
        List<Stock> lsVN30preventive = new ArrayList<>();

//        sortListStockByGTVH(lsStockHOSE);
//        return lsStockHOSE;

        // II.1
        for (int i = 0; i < lsStockHOSE.size(); i++) {
            if (lsStockHOSE.get(i).getHasWarning()) {
                lsStockHOSE.remove(lsStockHOSE.get(i));
                i--;
            }
        }
//
        System.out.println("II.1");
        for (int i = 0; i < lsStockHOSE.size(); i++) {
            if (lsStockHOSE.get(i).getCode() == "SAB" || lsStockHOSE.get(i).getCode() == "DPM" || lsStockHOSE.get(i).getCode() == "EIB") {
                System.out.println(lsStockHOSE.get(i).getCode());
            }
        }
//
//        //II.2
        List<Stock> lsTop5GTVH = getTop5StockGTVH(lsStockHOSE);
        for (int i = 0; i < lsStockHOSE.size(); i++) {
            if (lsStockHOSE.get(i).getLenFromListedDate() < 6 && (lsStockHOSE.get(i).getLenFromListedDate() < 3 || !lsTop5GTVH.contains(lsStockHOSE.get(i)))) {
                lsStockHOSE.remove(lsStockHOSE.get(i));
                i--;
            }
        }
//
        System.out.println("II.2");
        for (int i = 0; i < lsStockHOSE.size(); i++) {
            if (lsStockHOSE.get(i).getCode().compareTo("PPC") == 0 || lsStockHOSE.get(i).getCode().compareTo("KDH") == 0 || lsStockHOSE.get(i).getCode().compareTo("SAB") == 0) {
                System.out.println(lsStockHOSE.get(i).getCode());
            }
        }
//
        //II.3
        Double median = calMedianGTVH_f(lsStockHOSE);
        for (int i = 0; i < lsStockHOSE.size(); i++) {
            if (lsStockHOSE.get(i).getF() < 0.1) {
                if (lsStockHOSE.get(i).getGTVH_f() < median) {
                    lsStockHOSE.remove(lsStockHOSE.get(i));
                    i--;
                }
            }
        }

//        sortListStockByGTVH(lsStockHOSE);
//        return lsStockHOSE;
//
        System.out.println("II.3");
        for (int i = 0; i < lsStockHOSE.size(); i++) {
            if (lsStockHOSE.get(i).getCode().compareTo("PPC") == 0 || lsStockHOSE.get(i).getCode().compareTo("KDH") == 0 || lsStockHOSE.get(i).getCode().compareTo("SAB") == 0) {
                System.out.println(lsStockHOSE.get(i).getCode());
            }
        }
//
//        //II.4
        for (int i = 0; i < lsStockHOSE.size(); i++) {
            if (lsStockHOSE.get(i).getTurnover() < 0.05) {
                if (lsStockHOSE.get(i).getTurnover() < 0.04 || !lsStockHOSE.get(i).getIsPreVN30()) {
                    lsStockHOSE.remove(lsStockHOSE.get(i));
                    i--;
                }
            }
        }
//
        System.out.println("II.4");
        for (int i = 0; i < lsStockHOSE.size(); i++) {
            if (lsStockHOSE.get(i).getCode().compareTo("PPC") == 0 || lsStockHOSE.get(i).getCode().compareTo("KDH") == 0 || lsStockHOSE.get(i).getCode().compareTo("SAB") == 0) {
                System.out.println(lsStockHOSE.get(i).getCode());
            }
        }
//
//        II.5 bỏ qua
//         Ưu tiên (bỏ qua đk cảnh báo)
        List<Stock> lsA3 = getListStocksCumulativeGTGD(lsStockHOSE);
//        List<Stock> lsA3_temp = new ArrayList<>();
//        return lsA3;
        for (int i = 0; i < 20; i++) { lsVN30.add(lsA3.get(i)); }
        for (int i = 20; i < 40; i++) {
            if (lsA3.get(i).getIsPreVN30() && lsVN30.size() < 30) {
                lsVN30.add(lsA3.get(i));
//                lsA3_temp.add(lsA3.get(i));
            }
        }
////
        for (int i = 0; i < lsA3.size(); i++) {
            if (!lsVN30.contains(lsA3.get(i)) && lsVN30.size() < 30) { lsVN30.add(lsA3.get(i)); }
        }
//        sortCode(lsVN30);
        sortListStockByGTVH(lsVN30);
        for (int i = 0; i < lsA3.size(); i++) {
            if (!lsVN30.contains(lsA3.get(i)) && lsVN30.size() < 51) { lsVN30.add(lsA3.get(i)); }
        }
////
        return lsVN30;
    }
}
