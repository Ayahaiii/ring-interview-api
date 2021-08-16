package com.monetware.ringinterview.business.controller;

import com.monetware.ringinterview.business.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Simo
 * @date 2020-02-17
 */
@Component
public class ScheduleController {


    @Autowired
    private ScheduleService scheduleService;

    /**
     * 每天凌晨0点执行
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void everyDayExecute() {
        scheduleService.deleteExpireAtZero();
    }
}
