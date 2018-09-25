package com.model;


import java.util.*;
/**
 * MeetingPO
 * Usage:
 *
 * @author dgeng
 * create time 2018/9/25 - 上午9:41
 */
public class MeetingPO {

    private Date day;
    private String beginTime;
    private String endTime;
    private String organization;

    public MeetingPO() {
    }

    public MeetingPO(Date day, String beginTime, String endTime, String organization) {
        this.day = day;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.organization = organization;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
