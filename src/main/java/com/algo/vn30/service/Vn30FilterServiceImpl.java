package com.algo.vn30.service;

import com.algo.vn30.entity.SecurityImpl;
import com.algo.vn30.persistence.DailyDataPersistence;
import com.algo.vn30.persistence.SecurityPersistence;
import com.algo.vn30.util.LoggingUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class Vn30FilterServiceImpl implements Vn30FilterService {
    private static Logger logger = LoggingUtil.createLogger(Vn30FilterServiceImpl.class);

    @Autowired
    private SecurityPersistence securityPersistence;

    @Autowired
    private DailyDataPersistence dailyDataPersistence;

    /**
     * TODO: Tách từng bước ra thành các hàm nhỏ 1, 2, 3,...
     * TODO: Đặt tên lại từng bước
     *
     * @param securities Danh sách cổ phiếu trước khi lọc
     * @return securities Danh sách cổ phiếu sau khi lọc
     */
    private List<SecurityImpl> filter1(List<SecurityImpl> securities) {
        return null;
    }

    private List<SecurityImpl> filter2(List<SecurityImpl> securities) {
        return null;
    }

    private List<SecurityImpl> filter3(List<SecurityImpl> securities) {
        return null;
    }

    private void logSecurities(List<SecurityImpl> securities) {
        for (SecurityImpl security : securities)
            logger.info("VN30: " + security.getName());
    }

    @Override
    public void filter(Date date) {
        // TODO: Thêm cột exchange_id vào bảng security để phân biệt sàn chứng khoán niêm yét mã cổ phiếu
        // TODO: Lấy dữ liệu trong DailyData lưu ý lấy ngày date và N ngày trước đó (N vừa đủ, không lấy dư)
        // TODO: Lấy ra các mã thuộc sàn HOSE
        List<SecurityImpl> securities = null;
        // Từng bước filter
        securities = filter1(securities);
        logger.info("VN30: List of securites after 1st filter");
        logSecurities(securities);
        securities = filter2(securities);
        logger.info("VN30: List of securites after 2nd filter");
        logSecurities(securities);
        securities = filter2(securities);
        logger.info("VN30: List of securites after 3rd filter");
        logSecurities(securities);
    }
}
