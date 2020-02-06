package com.algo.vn30.entity;

import java.util.ArrayList;
import java.util.List;

public class VN30Result {
    private List<Stock> lsStockHOSE;
    private List<Stock> lStockVN30;
    private List<Stock> top5GTVHCurrent;
    private Double medianGTVH_f;
    private Double minGTGDTop90;

    public VN30Result() {
        lsStockHOSE = new ArrayList<>();
        lStockVN30 = new ArrayList<>();
        top5GTVHCurrent = new ArrayList<>();
        medianGTVH_f = 0.0;
        minGTGDTop90 = 10000000000000000.0;
    }

    public void setLsStockHOSE(List<Stock> lsStockHOSE) { this.lsStockHOSE.addAll(lsStockHOSE); }
    public List<Stock> getLsStockHOSE() { return lsStockHOSE; }

    public void setlStockVN30(List<Stock> lStockVN30) { this.lStockVN30.addAll(lStockVN30); }
    public List<Stock> getlStockVN30() { return lStockVN30; }

    public void setTop5GTVHCurrent(List<Stock> top5GTVHCurrent) { this.top5GTVHCurrent.addAll(top5GTVHCurrent); }
    public List<Stock> getTop5GTVHCurrent() { return top5GTVHCurrent; }

    public void setMedianGTVH_f(Double medianGTVH_f) { this.medianGTVH_f = medianGTVH_f; }
    public Double getMedianGTVH_f() { return medianGTVH_f; }

    public void setMinGTGDTop90(Double minGTGDTop90) { this.minGTGDTop90 = minGTGDTop90; }
    public Double getMinGTGDTop90() { return minGTGDTop90; }
}
