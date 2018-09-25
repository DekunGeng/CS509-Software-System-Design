package com.model;


import java.util.*;
/**
 * AvailDayPO
 * Usage:
 *
 * @author dgeng
 * create time 2018/9/25 - 上午10:05
 */
public class AvailDayPO {

    private Date startDay;
    private Date endDay;

    public AvailDayPO() {
    }

    public AvailDayPO(Date startDay, Date endDay) {
        this.startDay = startDay;
        this.endDay = endDay;
    }

    public Date getStartDay() {
        return startDay;
    }

    public void setStartDay(Date startDay) {
        this.startDay = startDay;
    }

    public Date getEndDay() {
        return endDay;
    }

    public void setEndDay(Date endDay) {
        this.endDay = endDay;
    }
}
