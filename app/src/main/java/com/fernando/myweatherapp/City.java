package com.fernando.myweatherapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class City {
    private String name;
    private String country;
    private String temperature;
    private String icon;
    private String timeZone;

    public City() {

    }

    public City(String name, String country, String temperature, String icon, String timeZone) {
        this.name = name;
        this.country = country;
        this.temperature = temperature;
        this.icon = icon;
        this.timeZone = timeZone;
    }

    public String getName() {
        return name.toUpperCase();
    }

    public String getCountry() {
        return country;
    }

    public String getTemperature() {
        return parseTemperature(temperature) + "º";
    }

    public String getIcon() {
        return icon;
    }

    public String getTimeZone() {
        return parseTime(timeZone);
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    private String parseTemperature(String temperature) {
        int temp = (int) (Double.valueOf(temperature) - 273.15);
        return String.valueOf(temp);
    }

    public String parseTime(String timeZone) {
        Calendar calendar = Calendar.getInstance();

        long timeZoneDifference = 0;
        long currentTime = calendar.getTimeInMillis();
        long cityTimeZone = Integer.parseInt(timeZone) * 1000;
        long localTimeZone = Calendar.getInstance().getTimeZone().getOffset(System.currentTimeMillis());

        timeZoneDifference = Math.abs(difference(cityTimeZone, Calendar.getInstance().getTimeZone().getOffset(System.currentTimeMillis())));

        if (cityTimeZone > localTimeZone) {
            currentTime = calendar.getTimeInMillis() + timeZoneDifference;
        } else if (cityTimeZone < localTimeZone) {
            currentTime = calendar.getTimeInMillis() - timeZoneDifference;
        }

        SimpleDateFormat spt = new SimpleDateFormat("HH:mm");
        String current = spt.format(currentTime);

        return current;
    }

    private Long difference(long x, long y) {
        long r = x - y;
        if (((x ^ y) & (x ^ r)) < 0) {
            throw new ArithmeticException("long overflow");
        }
        return r;
    }

    @Override
    public String toString() {
        return name + "," + country;
    }
}