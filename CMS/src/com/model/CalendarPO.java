package com.model;


import java.util.Date;
import java.util.List;

/**
 * Calendar
 * Usage:
 *
 * @author dgeng
 * create time 2018/9/25 - 上午8:22
 */
public class CalendarPO {

    private String name;
    private String startTime;
    private String endTime;
    private int durationOption;
    private List<AvailDayPO> availDays;
    private List<MeetingPO> meeting;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDurationOption() {
        return durationOption;
    }

    public void setDurationOption(int durationOption) {
        this.durationOption = durationOption;
    }

    public List<MeetingPO> getMeeting() {
        return meeting;
    }

    public void setMeeting(List<MeetingPO> meeting) {
        this.meeting = meeting;
    }

    public List<AvailDayPO> getAvailDays() {
        return availDays;
    }

    public void setAvailDays(List<AvailDayPO> availDays) {
        this.availDays = availDays;
    }

}
