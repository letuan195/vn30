package com.algo.vn30.service;

import org.springframework.stereotype.Service;

import java.util.Date;

public interface ReportDayService {
    String getReport(Date date);
}
