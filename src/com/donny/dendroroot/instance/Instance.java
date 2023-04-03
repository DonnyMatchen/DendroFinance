package com.donny.dendroroot.instance;

import com.donny.dendroroot.data.LogHandler;
import com.donny.dendroroot.fileio.EncryptionHandler;
import com.donny.dendroroot.fileio.FileHandler;
import com.donny.dendroroot.gui.customswing.DendroFactory;
import com.donny.dendroroot.json.JsonObject;

import javax.swing.*;
import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

public abstract class Instance {
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    //Major managing objects and handling lists
    public final String IID;
    public final String[] ARGS;
    public final LogHandler LOG_HANDLER;
    public final EncryptionHandler ENCRYPTION_HANDLER;
    public final FileHandler FILE_HANDLER;
    public File data = new File(System.getProperty("user.dir") + File.separator + "data");

    //flags, api keys, and other minor alterable things
    public MathContext precision;
    public boolean log, american, day;
    public LogHandler.LogLevel logLevel;
    public int blockSize;

    public Instance(String iid, String[] args) {
        IID = iid;
        ARGS = args;
        LOG_HANDLER = new LogHandler(this);
        FILE_HANDLER = new FileHandler(this);
        loadInitConfig(args);
        DendroFactory.init(this);
        ENCRYPTION_HANDLER = new EncryptionHandler(this);
    }

    public void loadInitConfig(String[] args) {
        File init = new File(data.getPath() + File.separator + "init.json");
        log = true;
        logLevel = new LogHandler.LogLevel("info");
        boolean a = false, b = false, ll = false;
        for (String arg : args) {
            if (arg.equals("-nl")) {
                log = false;
                a = true;
            } else if (arg.equals("-level")) {
                ll = true;
            } else if (ll) {
                ll = false;
                b = true;
                logLevel = new LogHandler.LogLevel(arg);
            }
        }
        if (init.exists()) {
            JsonObject json = (JsonObject) FILE_HANDLER.readJson(init);
            if (json != null) {
                if (json.containsKey("log") && !a) {
                    log = json.getBoolean("log").bool;
                }
                if (json.containsKey("log-level") && !b) {
                    logLevel = new LogHandler.LogLevel(json.getString("log-level").getString());
                }
            }
        }
    }

    public abstract void save();

    public void conclude(boolean save) {
        if (save) {
            save();
        }
        if (log) {
            LOG_HANDLER.save();
        }
    }

    public String p(BigDecimal d) {
        return (new DecimalFormat("#,##0.00%")).format(d);
    }

    public void installPeriod(JComboBox<String> box) {
        box.removeAllItems();
        box.addItem("Year");
        box.addItem("Q1");
        box.addItem("Q2");
        box.addItem("Q3");
        box.addItem("Q4");
        box.addItem("January");
        box.addItem("February");
        box.addItem("March");
        box.addItem("April");
        box.addItem("May");
        box.addItem("June");
        box.addItem("July");
        box.addItem("August");
        box.addItem("September");
        box.addItem("October");
        box.addItem("November");
        box.addItem("December");
    }
}
