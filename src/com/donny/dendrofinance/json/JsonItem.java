package com.donny.dendrofinance.json;

public abstract class JsonItem {
    public final JsonType TYPE;

    public JsonItem(JsonType type) {
        TYPE = type;
    }

    public static JsonItem digest(String raw) throws JsonFormattingException {
        if (raw.length() == 0) {
            return new JsonNull();
        } else {
            return switch (raw.charAt(0)) {
                case '{' -> new JsonObject(raw);
                case '[' -> new JsonArray(raw);
                case '"' -> new JsonString(raw);
                case 'T', 't', 'F', 'f' -> new JsonBool(raw);
                case 'n' -> new JsonNull();
                default -> new JsonDecimal(raw);
            };
        }
    }

    public static String sanitize(String raw) {
        boolean string = false;
        StringBuilder sb = new StringBuilder();
        for (char c : raw.toCharArray()) {
            if (c == '"') {
                string = !string;
            }
            if (string || (c != ' ' && c != '\n' && c != '\t' && c != '\r')) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static JsonItem sanitizeDigest(String raw) throws JsonFormattingException {
        return digest(sanitize(raw));
    }

    public JsonType getType() {
        return TYPE;
    }

    public String print() {
        return print(0);
    }

    public abstract String print(int scope);

    protected String indent(int scope) {
        if (scope == 0) {
            return "";
        } else {
            return " ".repeat(2 * scope);
        }
    }
}
