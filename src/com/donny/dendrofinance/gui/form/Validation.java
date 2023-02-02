package com.donny.dendrofinance.gui.form;

import com.donny.dendrofinance.account.AWColumn;
import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.*;
import com.donny.dendrofinance.types.LAccountSet;
import com.donny.dendrofinance.types.LDate;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.ParseException;

public class Validation {
    public static void require(JTextField field) throws ValidationFailedException {
        field.setText(field.getText().replace("\"", ""));
        if (field.getText().equals("")) {
            field.setBackground(DendroFactory.WRONG);
            throw new ValidationFailedException("Field cannot be empty");
        } else {
            field.setBackground(DendroFactory.CONTENT);
        }
    }

    public static void require(JTextArea field) throws ValidationFailedException {
        field.setText(field.getText().replace("\"", ""));
        if (field.getText().equals("")) {
            field.setBackground(DendroFactory.WRONG);
            throw new ValidationFailedException("Field cannot be empty");
        } else {
            field.setBackground(DendroFactory.CONTENT);
        }
    }

    public static LDate validateDate(JTextField field, Instance curInst) throws ValidationFailedException {
        if (field.getText().replace("\"", "").equals("")) {
            return LDate.now(curInst);
        } else {
            try {
                field.setBackground(DendroFactory.CONTENT);
                return new LDate(field.getText(), curInst);
            } catch (ParseException e) {
                field.setBackground(DendroFactory.WRONG);
                throw new ValidationFailedException("Field is not a valid date");
            }
        }
    }

    public static BigDecimal validateDecimal(JTextField field) throws ValidationFailedException {
        require(field);
        field.setBackground(DendroFactory.CONTENT);
        return Cleaning.cleanNumber(field.getText());
    }

    public static BigDecimal validateDecimalAllowPercent(JTextField field, MathContext precision) throws ValidationFailedException {
        require(field);
        field.setBackground(DendroFactory.CONTENT);
        return Cleaning.cleanNumberAllowPercent(field.getText(), precision);
    }

    public static BigInteger validateInteger(JTextField field) throws ValidationFailedException {
        require(field);
        field.setBackground(DendroFactory.CONTENT);
        return Cleaning.cleanInteger(field.getText());
    }

    public static String validateString(JTextField field) throws ValidationFailedException {
        require(field);
        field.setBackground(DendroFactory.CONTENT);
        return field.getText();
    }

    public static String validateStringAllowEmpty(JTextField field) {
        field.setText(field.getText().replace("\"", ""));
        field.setBackground(DendroFactory.CONTENT);
        return field.getText();
    }

    public static String validateString(JTextArea field) throws ValidationFailedException {
        require(field);
        field.setBackground(DendroFactory.CONTENT);
        return field.getText();
    }

    public static String validateStringAllowEmpty(JTextArea field) {
        field.setText(field.getText().replace("\"", ""));
        field.setBackground(DendroFactory.CONTENT);
        return field.getText();
    }

    public static JsonItem validateJson(JTextArea field) throws ValidationFailedException {
        if (field.getText().equals("")) {
            throw new ValidationFailedException("JSONs cannot be blank");
        }
        try {
            field.setBackground(DendroFactory.CONTENT);
            return JsonItem.digest(field.getText());
        } catch (JsonFormattingException e) {
            field.setBackground(DendroFactory.WRONG);
            throw new ValidationFailedException("Field is not a valid JSON");
        }
    }

    public static JsonObject validateJsonObject(JTextArea field) throws ValidationFailedException {
        JsonItem item = validateJson(field);
        if (item.getType() == JsonType.OBJECT) {
            field.setBackground(DendroFactory.CONTENT);
            return (JsonObject) item;
        } else {
            field.setBackground(DendroFactory.WRONG);
            throw new ValidationFailedException("Field is not a valid JSON Object");
        }
    }

    public static JsonArray validateJsonArray(JTextArea field) throws ValidationFailedException {
        JsonItem item = validateJson(field);
        if (item.getType() == JsonType.ARRAY) {
            field.setBackground(DendroFactory.CONTENT);
            return (JsonArray) item;
        } else {
            field.setBackground(DendroFactory.WRONG);
            throw new ValidationFailedException("Field is not a valid JSON Array");
        }
    }

    public static LAccountSet validateAccountSet(JTextField field, Instance curInst) throws ValidationFailedException {
        String raw = field.getText();
        raw = raw.replace("{", "").replace("}", "").replace(" ", "");
        for (String seg : raw.split(",")) {
            if (!seg.contains("!") || !seg.contains("(") || !seg.contains(")") ||
                    seg.split("!").length != 2 || seg.split("\\(").length != 2) {
                field.setBackground(DendroFactory.WRONG);
                field.select(field.getText().indexOf(seg), field.getText().indexOf(seg) + seg.length());
                throw new ValidationFailedException("Segment \"" + seg + "\" is not a valid ACV");
            }
            String[] first = seg.split("!");
            if (AWColumn.fromString(first[0]) == AWColumn.TRACKER) {
                if (!first[0].equals("T")) {
                    field.select(field.getText().indexOf(seg), field.getText().indexOf(seg) + first[0].length());
                    field.setBackground(DendroFactory.WRONG);
                    throw new ValidationFailedException("Segment \"" + seg + "\" is not in a valid Column");
                }
            }
            String[] second = first[1].replace(")", "").split("\\(");
            Account a = curInst.ACCOUNTS.getElement(second[0]);
            if (a == null) {
                field.setBackground(DendroFactory.WRONG);
                field.select(field.getText().indexOf(second[0]), field.getText().indexOf(second[0]) + second[0].length());
                throw new ValidationFailedException("Segment \"" + seg + "\" is not in a valid account");
            }
            try {
                Double.parseDouble(second[1]);
            } catch (NumberFormatException ex) {
                field.setBackground(DendroFactory.WRONG);
                field.select(field.getText().indexOf(second[1]), field.getText().indexOf(second[1]) + second[1].length());
                throw new ValidationFailedException("Segment \"" + seg + "\" does not have a valid value");
            }
        }
        field.setBackground(DendroFactory.CONTENT);
        return new LAccountSet(field.getText(), curInst);
    }
}
