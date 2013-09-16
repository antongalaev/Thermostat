package com.galaev.thermostat.storage;

import java.util.TreeSet;

/**
 * Timetable of temperature changes.
 */
public class DayTimetable {

    private TreeSet<TemperatureSwitch> timetable;
    private int dayCount;
    private int nightCount;

    public DayTimetable() {
        timetable = new TreeSet<TemperatureSwitch>();
    }

    public boolean addSwitch(TemperatureSwitch s) {
        boolean added = timetable.add(s);
        if (added) {
            if (s.getTimeOfDay() == TimeOfDay.DAY) {
                ++ dayCount;
            } else {
                ++ nightCount;
            }
        }
        return added;
    }

    public void removeSwitch(TemperatureSwitch s) {
        timetable.remove(s);
        if (s.getTimeOfDay() == TimeOfDay.DAY) {
            -- dayCount;
        } else {
            -- nightCount;
        }
    }

    public int numberOf(TimeOfDay timeOfDay) {
        if (timeOfDay == TimeOfDay.DAY) {
            return dayCount;
        } else {
            return nightCount;
        }
    }

    public TemperatureSwitch upcomingSwitch(TemperatureSwitch current) {
        return timetable.higher(current);
    }

    public TemperatureSwitch[] toArray() {
        TemperatureSwitch[] array = new TemperatureSwitch[timetable.size()];
        timetable.toArray(array);
        return array;
    }
}
