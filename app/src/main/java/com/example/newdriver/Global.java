package com.example.newdriver;


import java.util.Calendar;

public class Global {
    private Calendar licenseDate;
    private Calendar dayNightDate;
    private Calendar nightOnlyDate;
    private Calendar newDriverDate;

    public Calendar getLicenseDate() {
        return licenseDate;
    }

    public Calendar getDayNightDate() {
        return dayNightDate;
    }

    public Calendar getNightOnlyDate() {
        return nightOnlyDate;
    }

    public Calendar getNewDriverDate() {
        return newDriverDate;
    }

    public void setLicenseDate(int year, int month, int day) {
        this.licenseDate = Calendar.getInstance();
        this.licenseDate.set(year, month, day, 0, 0, 0);

        this.dayNightDate = (Calendar) this.licenseDate.clone();
        dayNightDate.add(Calendar.MONTH, 3);

        this.nightOnlyDate = (Calendar) this.licenseDate.clone();
        nightOnlyDate.add(Calendar.MONTH, 6);

        this.newDriverDate = (Calendar) this.licenseDate.clone();
        newDriverDate.add(Calendar.YEAR, 2);
    }
}
