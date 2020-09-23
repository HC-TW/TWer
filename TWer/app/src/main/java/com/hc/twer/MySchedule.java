package com.hc.twer;

public class MySchedule {

    private String scheduleCover;
    private String scheduleName;
    private String scheduleTime;

    public MySchedule(String scheduleCover, String scheduleName, String scheduleTime)
    {
        this.scheduleCover = scheduleCover;
        this.scheduleName = scheduleName;
        this.scheduleTime = scheduleTime;
    }

    public void setScheduleCover(String scheduleCover) {
        this.scheduleCover = scheduleCover;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public String getScheduleCover() {
        return scheduleCover;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }
}
