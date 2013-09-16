package com.galaev.thermostat.storage;

public enum Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    @Override
    public String toString() {
        String name = name().toLowerCase();
        return name.replace(name.charAt(0), Character.toUpperCase(name.charAt(0)));
    }
}
