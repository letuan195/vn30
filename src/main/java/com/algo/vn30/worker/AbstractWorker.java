package com.algo.vn30.worker;

import org.springframework.stereotype.Component;

@Component
public abstract class AbstractWorker implements BaseWorker {
    private String workerName;

    @Override
    public void start(String workerName) {
        this.workerName = workerName;

        onStarted();
    }

    @Override
    public abstract void onStarted();

    @Override
    public abstract void onStoping();

    @Override
    public void stop() {
    }

}
