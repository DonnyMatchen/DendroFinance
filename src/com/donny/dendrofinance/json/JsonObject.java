package com.donny.dendrofinance.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class JsonObject extends JsonItem {
    private final LinkedHashMap<String, JsonItem> CONTENTS;

    public JsonObject() {
        super(JsonType.OBJECT);
        CONTENTS = new LinkedHashMap<>();
    }

    public JsonObject(String raw) throws JsonFormattingException {
        this();
        if (!raw.equals("{}")) {
            raw = raw.substring(1, raw.length() - 1);
            ArrayList<String> rawValues = new ArrayList<>();
            ArrayList<String> keys = new ArrayList<>();
            int array = 0, object = 0;
            boolean itemd = false, string = false, escape = false;
            StringBuilder sb = new StringBuilder();
            for (char c : raw.toCharArray()) {
                if (c == ']' && !string) {
                    array--;
                } else if (c == '}' && !string) {
                    object--;
                } else if (c == '[' && !string) {
                    array++;
                } else if (c == '{' && !string) {
                    object++;
                } else if (c == '"') {
                    if (!escape) {
                        string = !string;
                    }
                }
                if (c == ':' && !itemd) {
                    itemd = true;
                    keys.add(sb.toString().replace("\"", ""));
                    sb = new StringBuilder();
                } else if (c == ',' && itemd && array + object == 0 && !string) {
                    rawValues.add(sb.toString());
                    sb = new StringBuilder();
                    itemd = false;
                } else {
                    if (escape || c != '\\') {
                        if (escape) {
                            switch (c) {
                                case 'n' -> {
                                    sb.append("\n");
                                    escape = false;
                                }
                                case 'r' -> {
                                    sb.append("\r");
                                    escape = false;
                                }
                                case 't' -> {
                                    sb.append("\t");
                                    escape = false;
                                }
                                case 's' -> {
                                    sb.append("\s");
                                    escape = false;
                                }
                                case 'f' -> {
                                    sb.append("\f");
                                    escape = false;
                                }
                                case '\'' -> {
                                    sb.append("'");
                                    escape = false;
                                }
                                case 'b' -> {
                                    sb.append("\b");
                                    escape = false;
                                }
                                case '"' -> {
                                    sb.append("\"");
                                    escape = false;
                                }
                                case '\\' -> {
                                    sb.append("\\");
                                    escape = false;
                                }
                            }
                        } else {
                            sb.append(c);
                        }
                    }
                }
                if (escape) {
                    throw new JsonFormattingException("Invalid Escape");
                }
                if (c == '\\') {
                    escape = true;
                }
            }
            rawValues.add(sb.toString());
            for (int i = 0; i < keys.size(); i++) {
                CONTENTS.put(keys.get(i), JsonItem.digest(rawValues.get(i)));
            }
        }
    }

    public JsonItem get(String key) {
        return CONTENTS.get(key);
    }

    public JsonString getString(String key) {
        return (JsonString) get(key);
    }

    public JsonDecimal getDecimal(String key) {
        return (JsonDecimal) get(key);
    }

    public JsonBool getBoolean(String key) {
        return (JsonBool) get(key);
    }

    public JsonArray getArray(String key) {
        return (JsonArray) get(key);
    }

    public JsonObject getObject(String key) {
        return (JsonObject) get(key);
    }

    public void put(String key, JsonItem item) {
        CONTENTS.put(key, item);
    }

    public void remove(String key) {
        CONTENTS.remove(key);
    }

    public boolean containsKey(String key) {
        return CONTENTS.containsKey(key);
    }

    public ArrayList<String> getFields() {
        return new ArrayList<>(CONTENTS.keySet());
    }

    @Override
    public String toString() {
        if (CONTENTS.isEmpty()) {
            return "{}";
        } else {
            StringBuilder sb = new StringBuilder("{");
            for (String key : CONTENTS.keySet()) {
                sb.append("\"").append(key).append("\":").append(CONTENTS.get(key)).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.append("}").toString();
        }
    }

    @Override
    public String print(int scope) {
        int internal = scope + 1;
        if (CONTENTS.isEmpty()) {
            return "{}";
        } else {
            StringBuilder sb = new StringBuilder("{");
            for (String key : CONTENTS.keySet()) {
                sb.append("\n").append(indent(internal)).append("\"").append(key).append("\": ").append(CONTENTS.get(key).print(internal)).append(",");
            }
            return sb.deleteCharAt(sb.length() - 1).append("\n").append(indent(scope)).append("}").toString();
        }
    }
}
