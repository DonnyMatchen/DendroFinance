package com.donny.dendrofinance.types;

import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonItem;
import com.donny.dendrofinance.util.ExportableToJson;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LDate implements ExportableToJson, Comparable<LDate> {
    private final Date DATE;
    private final Instance CURRENT_INSTANCE;

    public LDate(Date date, Instance curInst) {
        DATE = date;
        CURRENT_INSTANCE = curInst;
    }

    public LDate(long l, Instance curInst) {
        DATE = new Date(l);
        CURRENT_INSTANCE = curInst;
    }

    public LDate(JsonDecimal dec, Instance curInst) {
        DATE = new Date(dec.decimal.longValue());
        CURRENT_INSTANCE = curInst;
    }

    public LDate(int year, int month, int day, Instance curInst) {
        Calendar date = Calendar.getInstance();
        date.set(year, month - 1, day, 0, 0, 0);
        long temp = date.getTime().getTime();
        DATE = new Date(temp - (temp % 1000));
        CURRENT_INSTANCE = curInst;
    }

    public LDate(int year, int month, int day, int hour, int minute, int second, int milli, Instance curInst) {
        Calendar date = Calendar.getInstance();
        date.set(year, month - 1, day, hour, minute, second);
        long temp = date.getTime().getTime();
        DATE = new Date(temp - (temp % 1000) + milli);
        CURRENT_INSTANCE = curInst;
    }

    public LDate(int year, int month, int day, int hour, int minute, int second, Instance curInst) {
        this(year, month, day, hour, minute, second, 0, curInst);
    }

    public LDate(String raw, Instance curInst) {
        if (raw.equalsIgnoreCase("now")) {
            DATE = new Date();
            CURRENT_INSTANCE = curInst;
        } else {
            raw = raw.replace("|", "");
            while (raw.contains("  ")) {
                raw = raw.replace("  ", " ");
            }
            if (raw.contains(" ")) {
                String[] split = raw.split(" ");
                String[] dayParts = split[0].split("/");
                String[] timeParts = split[1].split(":");
                int year, month, day, hour, minute, second, milli;
                if (dayParts.length == 3) {
                    if (dayParts[2].length() == 4) {
                        year = Integer.parseInt(dayParts[2]);
                    } else if (dayParts[2].length() == 2) {
                        year = 2000 + Integer.parseInt(dayParts[2]);
                    } else {
                        year = now(curInst).getYear();
                    }
                } else {
                    year = now(curInst).getYear();
                }
                if (curInst.american) {
                    month = Integer.parseInt(dayParts[0]);
                    day = Integer.parseInt(dayParts[1]);
                } else {
                    month = Integer.parseInt(dayParts[1]);
                    day = Integer.parseInt(dayParts[0]);
                }
                hour = Integer.parseInt(timeParts[0]);
                minute = Integer.parseInt(timeParts[1]);
                milli = 0;
                second = 0;
                if (timeParts.length >= 3) {
                    if (timeParts[2].contains(".")) {
                        StringBuilder temp = new StringBuilder(timeParts[2].split("\\.")[1]);
                        while (temp.length() < 3) {
                            temp.append("0");
                        }
                        milli = Integer.parseInt(temp.toString());
                        second = Integer.parseInt(timeParts[2].split("\\.")[0]);
                    } else {
                        second = Integer.parseInt(timeParts[2]);
                    }
                }
                Calendar date = Calendar.getInstance();
                date.set(year, month - 1, day, hour, minute, second);
                long temp = date.getTime().getTime();
                DATE = new Date(temp - (temp % 1000) + milli);
                CURRENT_INSTANCE = curInst;
            } else {
                String[] dayParts = raw.split("/");
                int year, month, day;
                if (dayParts.length == 3) {
                    if (dayParts[2].length() == 4) {
                        year = Integer.parseInt(dayParts[2]);
                    } else if (dayParts[2].length() == 2) {
                        year = 2000 + Integer.parseInt(dayParts[2]);
                    } else {
                        year = now(curInst).getYear();
                    }
                } else {
                    year = now(curInst).getYear();
                }
                if (curInst.american) {
                    month = Integer.parseInt(dayParts[0]);
                    day = Integer.parseInt(dayParts[1]);
                } else {
                    month = Integer.parseInt(dayParts[1]);
                    day = Integer.parseInt(dayParts[0]);
                }
                Calendar date = Calendar.getInstance();
                date.set(year, month - 1, day, 0, 0, 0);
                long temp = date.getTime().getTime();
                DATE = new Date(temp - (temp % 1000));
                CURRENT_INSTANCE = curInst;
            }
        }
    }

    public static LDate now(Instance curInst) {
        return new LDate(new Date(), curInst);
    }

    public static int lastDay(int year, int month, Instance curInst) {
        switch (month) {
            case 2 -> {
                return year % 4 == 0 ? 29 : 28;
            }
            case 1, 3, 5, 7, 8, 10, 12 -> {
                return 31;
            }
            case 4, 6, 9, 11 -> {
                return 30;
            }
            default -> {
                curInst.LOG_HANDLER.error(LDate.class, "Invalid Month Selected!");
                return 0;
            }
        }
    }

    public int lastMonth() {
        return (getMonth() - 1) % 12 == 0 ? 12 : (getMonth() - 1) % 12;
    }

    public long getTime() {
        return DATE.getTime();
    }

    public int getYear() {
        DateFormat f = new SimpleDateFormat("yyyy");
        return Integer.parseInt(f.format(DATE));
    }

    public int getMonth() {
        DateFormat f = new SimpleDateFormat("MM");
        return Integer.parseInt(f.format(DATE));
    }

    public int getDay() {
        DateFormat f = new SimpleDateFormat("dd");
        return Integer.parseInt(f.format(DATE));
    }

    public Date getDate() {
        return DATE;
    }

    public LDate yesterday() {
        return new LDate(new Date(DATE.getTime() - 86400000), CURRENT_INSTANCE);
    }

    public LDate tomorrow() {
        return new LDate(new Date(DATE.getTime() + 86400000), CURRENT_INSTANCE);
    }

    public String getQuarter() {
        return switch (getMonth()) {
            case 1, 2, 3 -> "Q1";
            case 4, 5, 6 -> "Q2";
            case 7, 8, 9 -> "Q3";
            case 10, 11, 12 -> "Q4";
            default -> "";
        };
    }

    public String getSemi() {
        return switch (getMonth()) {
            case 1, 2, 3, 4, 5, 6 -> "S1";
            case 7, 8, 9, 10, 11, 12 -> "S2";
            default -> "";
        };
    }

    public String getMonthString() {
        return switch (getMonth()) {
            case 1 -> "January";
            case 2 -> "February";
            case 3 -> "March";
            case 4 -> "April";
            case 5 -> "May";
            case 6 -> "June";
            case 7 -> "July";
            case 8 -> "August";
            case 9 -> "September";
            case 10 -> "October";
            case 11 -> "November";
            case 12 -> "December";
            default -> "INVALID";
        };
    }

    public String getMonthStringShort() {
        return switch (getMonth()) {
            case 1 -> "Jan";
            case 2 -> "Feb";
            case 3 -> "Mar";
            case 4 -> "Apr";
            case 5 -> "May";
            case 6 -> "Jun";
            case 7 -> "Jul";
            case 8 -> "Aug";
            case 9 -> "Sep";
            case 10 -> "Oct";
            case 11 -> "Nov";
            case 12 -> "Dec";
            default -> "INVALID";
        };
    }

    public boolean equals(LDate date) {
        return DATE.getTime() == date.DATE.getTime();
    }

    @Override
    public int compareTo(LDate date) {
        return Long.compare(getTime(), date.getTime());
    }

    public int compareTo(int y, int m, int d) {
        String[] thisVals = new SimpleDateFormat("yyyy/MM/dd").format(DATE).split("/");
        int year = Integer.parseInt(thisVals[0]);
        int month = Integer.parseInt(thisVals[1]);
        int day = Integer.parseInt(thisVals[2]);
        if (year == y) {
            if (month == m) {
                return Integer.compare(day, d);
            } else {
                return Integer.compare(month, m);
            }
        } else {
            return Integer.compare(year, y);
        }
    }

    @Override
    public JsonDecimal export() {
        return new JsonDecimal(DATE.getTime());
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean milli) {
        if (CURRENT_INSTANCE.day) {
            if (CURRENT_INSTANCE.american) {
                return new SimpleDateFormat("MM/dd/yyyy").format(DATE);
            } else {
                return new SimpleDateFormat("dd/MM/yyyy").format(DATE);
            }
        } else {
            if (milli) {
                if (CURRENT_INSTANCE.american) {
                    return new SimpleDateFormat("MM/dd/yyyy | HH:mm:ss.SSS zzz").format(DATE);
                } else {
                    return new SimpleDateFormat("dd/MM/yyyy | HH:mm:ss.SSS zzz").format(DATE);
                }
            } else {
                if (CURRENT_INSTANCE.american) {
                    return new SimpleDateFormat("MM/dd/yyyy | HH:mm:ss zzz").format(DATE);
                } else {
                    return new SimpleDateFormat("dd/MM/yyyy | HH:mm:ss zzz").format(DATE);
                }
            }
        }
    }

    public String toDateString() {
        if (CURRENT_INSTANCE.american) {
            return new SimpleDateFormat("MM/dd/yyyy").format(DATE);
        } else {
            return new SimpleDateFormat("dd/MM/yyyy").format(DATE);
        }
    }

    public String toTimeString() {
        return new SimpleDateFormat("HH:mm:ss.SSS zzz").format(DATE);
    }
}
