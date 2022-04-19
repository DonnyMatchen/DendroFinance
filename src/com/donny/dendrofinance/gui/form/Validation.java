package com.donny.dendrofinance.gui.form;

import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.ItemField;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.*;
import com.donny.dendrofinance.types.LDate;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.BigInteger;

public class Validation {
    public static void require(JTextField field) throws ValidationFailedException {
        field.setText(field.getText().replace("\"", ""));
        if (field.getText().equals("")) {
            field.setBackground(DendroFactory.WRONG);
            throw new ValidationFailedException();
        } else {
            field.setBackground(DendroFactory.CONTENT);
        }
    }

    public static void require(JTextArea field) throws ValidationFailedException {
        field.setText(field.getText().replace("\"", ""));
        if (field.getText().equals("")) {
            field.setBackground(DendroFactory.WRONG);
            throw new ValidationFailedException();
        } else {
            field.setBackground(DendroFactory.CONTENT);
        }
    }

    public static void require(ItemField field) throws ValidationFailedException {
        field.setText(field.getText().replace("\"", ""));
        if (field.getText().equals("")) {
            field.setTextBackground(DendroFactory.WRONG);
            throw new ValidationFailedException();
        } else {
            field.setTextBackground(DendroFactory.CONTENT);
        }
    }

    public static LDate validateDate(JTextField field, LDate orig, Instance curInst) throws ValidationFailedException {
        require(field);
        long add = 0;
        if (!field.getText().contains(("t(+0)"))) {
            if (field.getText().contains(("t("))) {
                add = Long.parseLong(field.getText().split(" ")[0].replace("t(", "").replace(")", ""));
            } else {
                try {
                    field.setBackground(DendroFactory.CONTENT);
                    return new LDate(field.getText(), curInst);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    field.setBackground(DendroFactory.WRONG);
                    throw new ValidationFailedException();
                }
            }
        }
        field.setBackground(DendroFactory.CONTENT);
        return new LDate(orig.getTime() + add, curInst);
    }

    public static BigDecimal validateDecimal(JTextField field) throws ValidationFailedException {
        require(field);
        try {
            field.setBackground(DendroFactory.CONTENT);
            return new BigDecimal(field.getText());
        } catch (NumberFormatException e) {
            field.setBackground(DendroFactory.WRONG);
            throw new ValidationFailedException();
        }
    }

    public static BigInteger validateInteger(JTextField field) throws ValidationFailedException {
        require(field);
        try {
            field.setBackground(DendroFactory.CONTENT);
            return new BigInteger(field.getText());
        } catch (NumberFormatException e) {
            field.setBackground(DendroFactory.WRONG);
            throw new ValidationFailedException();
        }
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

    public static String validateString(ItemField field) throws ValidationFailedException {
        require(field);
        field.setTextBackground(DendroFactory.CONTENT);
        return field.getText();
    }

    public static JsonItem validateJson(JTextArea field) throws ValidationFailedException {
        if (field.getText().equals("")) {
            field.setBackground(DendroFactory.WRONG);
            throw new ValidationFailedException();
        } else {
            try {
                field.setBackground(DendroFactory.CONTENT);
                return JsonItem.sanitizeDigest(field.getText());
            } catch (JsonFormattingException e) {
                field.setBackground(DendroFactory.WRONG);
                throw new ValidationFailedException();
            }
        }
    }

    public static JsonObject validateJsonObject(JTextArea field) throws ValidationFailedException {
        JsonItem item = validateJson(field);
        if (item.getType() == JsonType.OBJECT) {
            field.setBackground(DendroFactory.CONTENT);
            return (JsonObject) item;
        } else {
            field.setBackground(DendroFactory.WRONG);
            throw new ValidationFailedException();
        }
    }

    public static JsonArray validateJsonArray(JTextArea field) throws ValidationFailedException {
        JsonItem item = validateJson(field);
        if (item.getType() == JsonType.ARRAY) {
            field.setBackground(DendroFactory.CONTENT);
            return (JsonArray) item;
        } else {
            field.setBackground(DendroFactory.WRONG);
            throw new ValidationFailedException();
        }
    }
}
