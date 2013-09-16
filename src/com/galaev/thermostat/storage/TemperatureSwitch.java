package com.galaev.thermostat.storage;

/**
 * Change from day to night or from night to day.
 */
public class TemperatureSwitch implements Comparable<TemperatureSwitch> {

    private Day day;
    private String time;
    private TimeOfDay timeOfDay;

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(TimeOfDay timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    @Override
    public int compareTo(TemperatureSwitch another) {
        return time.compareTo(another.time);
    }

    @Override
    public String toString() {
        return timeOfDay + " " + time + " on " + day;
    }
}
