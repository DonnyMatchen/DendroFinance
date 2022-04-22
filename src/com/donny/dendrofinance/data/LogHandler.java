package com.donny.dendrofinance.data;

import com.donny.dendrofinance.instance.Instance;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogHandler {
    public final Instance CURRENT_INSTANCE;
    public final int LOG_LEVEL;
    private final StringBuilder LOG, TRACE;

    public LogHandler(int logLevel, Instance curInst) {
        CURRENT_INSTANCE = curInst;
        LOG_LEVEL = logLevel;
        LOG = new StringBuilder();
        TRACE = new StringBuilder();
        trace(this.getClass(), "Log Handler Initiated");
    }

    public void print(String str, boolean print) {
        Date now = new Date();
        DateFormat format = new SimpleDateFormat("{MMM dd yyyy} (hh:mm:ss a z) ");
        LOG.append(format.format(now)).append(str).append("\n");
        if (print) {
            System.out.println(format.format(now) + str);
        }
    }

    public void save() {
        File dir = new File(CURRENT_INSTANCE.data.getPath() + File.separator + "Logs");
        if (!dir.exists()) {
            dir.mkdir();
        }
        Date now = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH;mm;ss_z");
        CURRENT_INSTANCE.FILE_HANDLER.writePlain(dir, format.format(now) + ".log", LOG.toString());
        CURRENT_INSTANCE.FILE_HANDLER.writePlain(dir, format.format(now) + ".trace.log", TRACE.toString());
    }

    public void fatal(Class cause, String message) {
        print("[" + cause.toString().split(" ")[1].substring(24) + "/FATAL] " + message, LOG_LEVEL >= LogLevel.LOG_LEVEL_FATAL);
    }

    public void error(Class cause, String message) {
        print("[" + cause.toString().split(" ")[1].substring(24) + "/ERROR] " + message, LOG_LEVEL >= LogLevel.LOG_LEVEL_ERROR);
    }

    public void warn(Class cause, String message) {
        print("[" + cause.toString().split(" ")[1].substring(24) + "/WARN] " + message, LOG_LEVEL >= LogLevel.LOG_LEVEL_WARN);
    }

    public void info(Class cause, String message) {
        print("[" + cause.toString().split(" ")[1].substring(24) + "/INFO] " + message, LOG_LEVEL >= LogLevel.LOG_LEVEL_INFO);
    }

    public void debug(Class cause, String message) {
        print("[" + cause.toString().split(" ")[1].substring(24) + "/DEBUG] " + message, LOG_LEVEL >= LogLevel.LOG_LEVEL_DEBUG);
    }

    public final void trace(Class cause, String message) {
        String str = "[" + cause.toString().split(" ")[1].substring(24) + "/TRACE] " + message;
        Date now = new Date();
        DateFormat format = new SimpleDateFormat("{MMM dd yyyy} (hh:mm:ss a z) ");
        TRACE.append(format.format(now)).append(str).append("\n");
        if (LOG_LEVEL >= LogLevel.LOG_LEVEL_TRACE) {
            System.out.println(format.format(now) + str);
        }
    }

    public static class LogLevel {
        public static final int LOG_LEVEL_OFF = 0;
        public static final int LOG_LEVEL_FATAL = 1;
        public static final int LOG_LEVEL_ERROR = 2;
        public static final int LOG_LEVEL_WARN = 3;
        public static final int LOG_LEVEL_INFO = 4;
        public static final int LOG_LEVEL_DEBUG = 5;
        public static final int LOG_LEVEL_TRACE = 6;

        private final String NAME;

        public LogLevel(String name) {
            NAME = name;
        }

        public int getLevel() {
            return switch (NAME) {
                case "off" -> LOG_LEVEL_OFF;
                case "fatal" -> LOG_LEVEL_FATAL;
                case "error" -> LOG_LEVEL_ERROR;
                case "warn" -> LOG_LEVEL_WARN;
                case "debug" -> LOG_LEVEL_DEBUG;
                case "trace", "all" -> LOG_LEVEL_TRACE;
                default -> LOG_LEVEL_INFO;
            };
        }

        public String getName() {
            return NAME;
        }
    }
}
