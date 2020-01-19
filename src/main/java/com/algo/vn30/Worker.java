package com.algo.vn30;

import com.algo.vn30.service.Vn30FilterService;
import com.algo.vn30.util.DateTimeUtil;
import com.algo.vn30.util.LoggingUtil;
import com.algo.vn30.worker.AbstractWorker;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Worker extends AbstractWorker {
    private static Logger logger = LoggingUtil.createLogger(Worker.class);

    @Autowired
    private Vn30FilterService vn30FilterService;

    @Override
    public void onStarted() {
        logger.info("VN30: Start");
        vn30FilterService.filter(DateTimeUtil.parseVnDate("31/12/2019"));
    }

    @Override
    public void onStoping() {
        logger.info("VN30: Stop");
    }
}
