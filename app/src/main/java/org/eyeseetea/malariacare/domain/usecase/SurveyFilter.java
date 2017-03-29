package org.eyeseetea.malariacare.domain.usecase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SurveyFilter {

    boolean all = true;
    boolean thisWeek;
    boolean thisMonth;
    boolean lastWeek;
    boolean lastMonth;
    boolean last6Days;
    boolean last6Weeks;
    boolean last6Month;

    public boolean isAll() {
        return all;
    }

    public boolean isThisWeek() {
        return thisWeek;
    }

    public void setThisWeek(boolean thisWeek) {
        all = false;
        this.thisWeek = thisWeek;
    }

    public boolean isThisMonth() {
        return thisMonth;
    }

    public void setThisMonth(boolean thisMonth) {
        all = false;
        this.thisMonth = thisMonth;
    }

    public boolean isLastWeek() {
        return lastWeek;
    }

    public void setLastWeek(boolean last_week) {
        all = false;
        this.lastWeek = last_week;
    }

    public boolean isLastMonth() {
        return lastMonth;
    }

    public void setLastMonth(boolean lastMonth) {
        all = false;
        this.lastMonth = lastMonth;
    }

    public boolean isLast6Days() {
        return last6Days;
    }

    public void setLast6Days(boolean last6Days) {
        all = false;
        this.last6Days = last6Days;
    }

    public boolean isLast6Weeks() {
        return last6Weeks;
    }

    public void setLast6Weeks(boolean last6Weeks) {
        all = false;
        this.last6Weeks = last6Weeks;
    }

    public boolean isLast6Month() {
        return last6Month;
    }

    public void setLast6Month(boolean last6Month) {
        all = false;
        this.last6Month = last6Month;
    }

    public Date getStartFilterDate(Calendar calendar) {
        if (isLast6Days()) {
            calendar.add(Calendar.DAY_OF_YEAR, -6);
        } else if (isLast6Weeks()) {
            calendar.add(Calendar.WEEK_OF_YEAR, -6);
        } else if (isLast6Month()) {
            calendar.add(Calendar.MONTH, -6);
        } else if (isLastWeek()) {
            calendar.add(Calendar.WEEK_OF_YEAR, -2);
        } else if (isThisWeek()) {
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
        } else if (isThisMonth()) {
            calendar.add(Calendar.MONTH, -1);
        } else if (isLastMonth()) {
            calendar.add(Calendar.MONTH, -2);
        }
        return calendar.getTime();
    }

    public Date getEndFilterDate(Calendar calendar) {
        if (isLastWeek()) {
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
        } else if (isLastMonth()) {
            calendar.add(Calendar.MONTH, -1);
        }
        return calendar.getTime();
    }

    public boolean isDateBetweenDates(Date dateInTheMiddle, Date startDate, Date endDate) {
        System.out.println(
                "start: " + getHumanReadableDate(startDate) + " middle: " + getHumanReadableDate(
                        dateInTheMiddle) + " end " + getHumanReadableDate(endDate));
        return dateInTheMiddle.getTime() >= startDate.getTime()
                && dateInTheMiddle.getTime() <= endDate.getTime();
    }

    private String getHumanReadableDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String dateAsString = sdf.format(date);
        return dateAsString;

    }
}
