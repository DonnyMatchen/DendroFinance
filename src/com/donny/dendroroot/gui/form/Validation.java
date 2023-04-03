package com.donny.dendroroot.gui.form;

import com.donny.dendroroot.gui.customswing.DendroFactory;
import com.donny.dendroroot.instance.Instance;
import com.donny.dendroroot.json.*;
import com.donny.dendroroot.types.LDate;

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
                throw new ValidationFailedException("Field is not a valid date: " + field.getText());
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
}
