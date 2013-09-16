package com.galaev.thermostat;

import android.app.Application;
import com.galaev.thermostat.activities.MainActivity;
import com.galaev.thermostat.storage.Day;
import com.galaev.thermostat.storage.DayTimetable;
import com.galaev.thermostat.storage.TemperatureSwitch;
import com.galaev.thermostat.storage.TimeOfDay;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application class.
 * All info is stored here.
 */
public class ThermostatApp extends Application {

    private Day day;
    private String time;
    private TimeOfDay timeOfDay;
    private double currentTemperature;
    private double dayTemperature;
    private double nightTemperature;
    private boolean programEnabled;
    private MainActivity activity;
    private TemperatureSwitch nextSwitch;
    private EnumMap<Day, DayTimetable> weekProgram;
    private Timer timer;
    private boolean timerStarted;
    Logger logger = Logger.getAnonymousLogger();

    public ThermostatApp() {
        day = Day.MONDAY;
        time = "17:20";
        timeOfDay = TimeOfDay.DAY;
        currentTemperature = 20.0;
        dayTemperature = 23.0;
        nightTemperature = 17.0;
        programEnabled = true;
        nextSwitch = null;
        timer = new Timer();
        weekProgram = new EnumMap<Day, DayTimetable>(Day.class);
        for (Day day : Day.values()) {
            weekProgram.put(day, new DayTimetable());
        }
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
        activity.setTime(time);
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(double currentTemperature) {
        this.currentTemperature = currentTemperature;
        activity.setCurrentTemperature(currentTemperature);
    }

    public double getDayTemperature() {
        return dayTemperature;
    }

    public void setDayTemperature(double dayTemperature) {
        this.dayTemperature = dayTemperature;
    }

    public double getNightTemperature() {
        return nightTemperature;
    }

    public void setNightTemperature(double nightTemperature) {
        this.nightTemperature = nightTemperature;
    }

    public boolean isProgramEnabled() {
        return programEnabled;
    }

    public void setProgramEnabled(boolean programEnabled) {
        this.programEnabled = programEnabled;
        activity.setProgramEnabled(programEnabled);
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
        activity.setDay(day.toString());
        nextSwitch = null;
    }

    public void theNextDay() {
        day = Day.values()[(day.ordinal() + 1) % Day.values().length];
        activity.setDay(day.toString());
        nextSwitch = null;
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    public MainActivity getActivity() {
        return activity;
    }

    public void addSwitch(TemperatureSwitch s) {
        DayTimetable timetable = weekProgram.get(s.getDay());
        timetable.addSwitch(s);
    }

    public void resetScheduledSwitch() {
        nextSwitch = null;
    }

    public DayTimetable getTimetable(Day day) {
        return weekProgram.get(day);
    }

    public void syncProgram() {
        TemperatureSwitch[] switches = weekProgram.get(day).toArray();
        logger.log(Level.INFO, Arrays.toString(switches));
        if (nextSwitch == null) {
            for (TemperatureSwitch switche : switches) {
                if (switche.getTime().compareTo(time) >= 0) {
                    nextSwitch = switche;
                    break;
                }
            }
        }
        if (nextSwitch != null && nextSwitch.getTime().compareTo(time) <= 0) {
            logger.log(Level.INFO, time + " - the next is " + nextSwitch);
            if (nextSwitch.getTimeOfDay() == TimeOfDay.DAY) {
                setCurrentTemperature(dayTemperature);
                setTimeOfDay(TimeOfDay.DAY);
            } else {
                setCurrentTemperature(nightTemperature);
                setTimeOfDay(TimeOfDay.NIGHT);
            }
            nextSwitch = weekProgram.get(day).upcomingSwitch(nextSwitch);
        }
    }

    public Timer getTimer() {
        return timer;
    }

    public boolean isTimerStarted() {
        return timerStarted;
    }

    public void setTimerStarted(boolean timerStarted) {
        this.timerStarted = timerStarted;
    }

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(TimeOfDay timeOfDay) {
        this.timeOfDay = timeOfDay;
        activity.setTimeOfDay(timeOfDay);
    }
}
