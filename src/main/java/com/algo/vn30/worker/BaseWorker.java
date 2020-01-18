package com.algo.vn30.worker;

public interface BaseWorker {

    void start(String workerName) throws Exception;
    void stop() throws Exception;
    void onStarted() throws Exception;
    void onStoping() throws Exception;

}
