package com.donny.dendroroot.types;

import com.donny.dendroroot.instance.Frequency;
import com.donny.dendroroot.instance.Instance;
import com.donny.dendroroot.json.JsonDecimal;
import com.donny.dendroroot.util.ExportableToJson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class LDate implements ExportableToJson, Comparable<LDate> {
    private static final ArrayList<SimpleDateFormat> MONTH_FIRST = new ArrayList<>(), MONTH_SECOND = new ArrayList<>();

    static {
        ArrayList<String> amerDate = new ArrayList<>(Arrays.asList(
                "MM/dd/yyyy", "MMM/dd/yyyy", "MMMM/dd/yyyy"
        )), otherDate = new ArrayList<>(Arrays.asList(
                "dd/MM/yyyy", "dd/MMM/yyyy", "dd/MMMM/yyyy"
        )), bothDate = new ArrayList<>(Arrays.asList(
                "MMM dd yyyy", "MMM dd, yyyy", "MMMM dd yyyy", "MMMM dd, yyyy",
                "dd MMM yyyy", "dd MMM, yyyy", "dd MMMM yyyy", "dd MMMM, yyyy"
        )), time = new ArrayList<>(Arrays.asList(
                "HH:mm", "HH:mm zzz", "HH:mm:ss", "HH:mm:ss.SSS", "HH:mm:ss zzz", "HH:mm:ss.SSS zzz",
                "hh:mma", "hh:mma zzz", "hh:mm:ssa", "hh:mm:ss.SSSa", "hh:mm:ssa zzz", "hh:mm:ss.SSSa zzz",
                "hh:mm a", "hh:mm a zzz", "hh:mm:ss a", "hh:mm:ss.SSS a", "hh:mm:ss a zzz", "hh:mm:ss.SSS a zzz"
        ));
        amerDate.forEach(d -> {
            MONTH_FIRST.add(new SimpleDateFormat(d));
            time.forEach(t -> {
                MONTH_FIRST.add(new SimpleDateFormat(d + " " + t));
                MONTH_FIRST.add(new SimpleDateFormat(d + ", " + t));
                MONTH_FIRST.add(new SimpleDateFormat(d + "; " + t));
                MONTH_FIRST.add(new SimpleDateFormat(d + " | " + t));
                MONTH_FIRST.add(new SimpleDateFormat(t + " " + d));
                MONTH_FIRST.add(new SimpleDateFormat(t + ", " + d));
                MONTH_FIRST.add(new SimpleDateFormat(t + "; " + d));
                MONTH_FIRST.add(new SimpleDateFormat(t + " | " + d));
            });
        });
        otherDate.forEach(d -> {
            MONTH_SECOND.add(new SimpleDateFormat(d));
            time.forEach(t -> {
                MONTH_SECOND.add(new SimpleDateFormat(d + " " + t));
                MONTH_SECOND.add(new SimpleDateFormat(d + ", " + t));
                MONTH_SECOND.add(new SimpleDateFormat(d + "; " + t));
                MONTH_SECOND.add(new SimpleDateFormat(d + " | " + t));
                MONTH_SECOND.add(new SimpleDateFormat(t + " " + d));
                MONTH_SECOND.add(new SimpleDateFormat(t + ", " + d));
                MONTH_SECOND.add(new SimpleDateFormat(t + "; " + d));
                MONTH_SECOND.add(new SimpleDateFormat(t + " | " + d));
            });
        });
        bothDate.forEach(d -> {
            MONTH_FIRST.add(new SimpleDateFormat(d));
            MONTH_SECOND.add(new SimpleDateFormat(d));
            time.forEach(t -> {
                MONTH_FIRST.add(new SimpleDateFormat(d + " " + t));
                MONTH_FIRST.add(new SimpleDateFormat(d + ", " + t));
                MONTH_FIRST.add(new SimpleDateFormat(d + "; " + t));
                MONTH_FIRST.add(new SimpleDateFormat(d + " | " + t));
                MONTH_FIRST.add(new SimpleDateFormat(t + " " + d));
                MONTH_FIRST.add(new SimpleDateFormat(t + ", " + d));
                MONTH_FIRST.add(new SimpleDateFormat(t + "; " + d));
                MONTH_FIRST.add(new SimpleDateFormat(t + " | " + d));
                MONTH_SECOND.add(new SimpleDateFormat(d + " " + t));
                MONTH_SECOND.add(new SimpleDateFormat(d + ", " + t));
                MONTH_SECOND.add(new SimpleDateFormat(d + "; " + t));
                MONTH_SECOND.add(new SimpleDateFormat(d + " | " + t));
                MONTH_SECOND.add(new SimpleDateFormat(t + " " + d));
                MONTH_SECOND.add(new SimpleDateFormat(t + ", " + d));
                MONTH_SECOND.add(new SimpleDateFormat(t + "; " + d));
                MONTH_SECOND.add(new SimpleDateFormat(t + " | " + d));
            });
        });
        MONTH_FIRST.sort((SimpleDateFormat t1, SimpleDateFormat t2) -> -1 * Integer.compare(t1.toPattern().length(), t2.toPattern().length()));
        MONTH_SECOND.sort((SimpleDateFormat t1, SimpleDateFormat t2) -> -1 * Integer.compare(t1.toPattern().length(), t2.toPattern().length()));
    }

    public static int getMonth(String name) {
        return switch (name.toLowerCase()) {
            case "january", "jan" -> 1;
            case "february", "feb" -> 2;
            case "march", "mar" -> 3;
            case "april", "apr" -> 4;
            case "may" -> 5;
            case "june", "jun" -> 6;
            case "july", "jul" -> 7;
            case "august", "aug" -> 8;
            case "september", "sep" -> 9;
            case "october", "oct" -> 10;
            case "november", "nov" -> 11;
            case "december", "dec" -> 12;
            default -> 0;
        };
    }

    public static LDate now(Instance curInst) {
        return new LDate(new Date(), curInst);
    }

    public static LDate startDay(LDate date) {
        return new LDate(date.getYear(), date.getMonth(), date.getDay(), date.CURRENT_INSTANCE);
    }

    public static LDate endDay(int year, int month, int day, Instance curInst) {
        return endDay(new LDate(year, month, day, curInst));
    }

    public static LDate endDay(LDate date) {
        return new LDate(date.getYear(), date.getMonth(), date.getDay(), 23, 59, 59, 999, date.CURRENT_INSTANCE);
    }

    public static LDate getRange(int range, LDate end, Instance curInst) {
        return new LDate(end.getTime() - 86400000L * range, curInst);
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

    public static LDate[] getRange(int year, int param, Frequency frequency, Instance curInst) {
        switch (frequency) {
            case ANNUAL -> {
                LDate temp = new LDate(year + 1, 1, 1, curInst);
                return new LDate[]{
                        new LDate(year, 1, 1, curInst),
                        new LDate(temp.getTime() - 1, curInst)
                };
            }
            case QUARTERLY -> {
                int sm, em;
                em = 3 * param;
                sm = em - 2;
                LDate end;
                if (em == 12) {
                    end = new LDate(year + 1, 1, 1, curInst);
                } else {
                    end = new LDate(year, em + 1, 1, curInst);
                }
                return new LDate[]{
                        new LDate(year, sm, 1, curInst),
                        new LDate(end.getTime() - 1, curInst)
                };
            }
            case MONTHLY -> {
                int ey, em;
                ey = year;
                em = param + 1;
                if (param > 12) {
                    em = 1;
                    ey = year + 1;
                }
                LDate end;
                end = new LDate(ey, em, 1, curInst);
                return new LDate[]{
                        new LDate(year, param, 1, curInst),
                        new LDate(end.getTime() - 1, curInst)
                };
            }
        }
        return null;
    }

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

    public LDate(String raw, Instance curInst) throws ParseException {
        CURRENT_INSTANCE = curInst;
        if (raw.equalsIgnoreCase("now")) {
            DATE = new Date();
        } else {
            Date candidate = null;
            ArrayList<SimpleDateFormat> formats;
            if (CURRENT_INSTANCE.american) {
                formats = MONTH_FIRST;
            } else {
                formats = MONTH_SECOND;
            }
            for (DateFormat form : formats) {
                try {
                    candidate = form.parse(raw);
                } catch (ParseException e) {
                    continue;
                }
                if (candidate != null) {
                    break;
                }
            }
            if (candidate == null) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Date string could not be parsed!");
                throw new ParseException(raw, 0);
            }
            DATE = candidate;
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
                    return new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss.SSS zzz").format(DATE);
                } else {
                    return new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss.SSS zzz").format(DATE);
                }
            } else {
                if (CURRENT_INSTANCE.american) {
                    return new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss zzz").format(DATE);
                } else {
                    return new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss zzz").format(DATE);
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

    public String toFileSafeString() {
        return new SimpleDateFormat("yyyy-MM-dd_HH;mm;ss_z").format(DATE);
    }

    public String toFileSafeDateString() {
        return new SimpleDateFormat("yyyy-MM-dd").format(DATE);
    }
}
