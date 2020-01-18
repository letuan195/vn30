package com.algo.vn30;

import com.algo.vn30.persistence.SecurityPersistence;
import com.algo.vn30.worker.AbstractWorker;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Worker extends AbstractWorker {

    @Autowired
    private SecurityPersistence securityPersistence;

    @Override
    public void onStarted() {
        // do somthing
        System.out.println("run ok");
    }

    @Override
    public void onStoping() {

    }
}
