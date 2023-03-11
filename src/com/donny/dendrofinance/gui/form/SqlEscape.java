package com.donny.dendrofinance.gui.form;

public class SqlEscape {
    public static String feed(String raw) {
        return raw.replace("'", "\u000b");
    }

    public static String eat(String token) {
        return token.replace("\u000b", "'");
    }
}
