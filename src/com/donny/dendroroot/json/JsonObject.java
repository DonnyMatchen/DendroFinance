package com.donny.dendroroot.json;

import com.donny.dendroroot.xarc.XarcOutputStream;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class JsonObject extends JsonItem {
    private final LinkedHashMap<String, JsonItem> CONTENTS;

    public JsonObject() {
        super(JsonType.OBJECT);
        CONTENTS = new LinkedHashMap<>();
    }

    public JsonItem get(String key) {
        return CONTENTS.get(key);
    }

    public JsonItem get(String[] keys) {
        for (String key : keys) {
            if (containsKey(key)) {
                return get(key);
            }
        }
        return null;
    }

    public JsonString getString(String key) {
        return (JsonString) get(key);
    }

    public JsonString getString(String[] keys) {
        for (String key : keys) {
            if (containsKey(key)) {
                return getString(key);
            }
        }
        return null;
    }

    public JsonDecimal getDecimal(String key) {
        return (JsonDecimal) get(key);
    }

    public JsonDecimal getDecimal(String[] keys) {
        for (String key : keys) {
            if (containsKey(key)) {
                return getDecimal(key);
            }
        }
        return null;
    }

    public JsonBool getBoolean(String key) {
        return (JsonBool) get(key);
    }

    public JsonBool getBoolean(String[] keys) {
        for (String key : keys) {
            if (containsKey(key)) {
                return getBoolean(key);
            }
        }
        return null;
    }

    public JsonArray getArray(String key) {
        return (JsonArray) get(key);
    }

    public JsonArray getArray(String[] keys) {
        for (String key : keys) {
            if (containsKey(key)) {
                return getArray(key);
            }
        }
        return null;
    }

    public JsonObject getObject(String key) {
        return (JsonObject) get(key);
    }

    public JsonObject getObject(String[] keys) {
        for (String key : keys) {
            if (containsKey(key)) {
                return getObject(key);
            }
        }
        return null;
    }

    public void put(String key, JsonItem item) {
        CONTENTS.put(key, item);
    }

    public void remove(String key) {
        CONTENTS.remove(key);
    }

    public void remove(String[] keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    public boolean containsKey(String key) {
        return CONTENTS.containsKey(key);
    }

    public boolean containsKey(String[] keys) {
        for (String key : keys) {
            if (containsKey(key)) {
                return true;
            }
        }
        return false;
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

    @Override
    protected void stream(FileWriter writer) throws IOException {
        if (CONTENTS.keySet().size() == 0) {
            writer.write("{}");
        } else {
            writer.write("{");
            ArrayList<String> keys = new ArrayList<>(CONTENTS.keySet());
            int x = keys.size();
            for (int i = 0; i < x; i++) {
                writer.write('"' + keys.get(i) + "\":");
                CONTENTS.get(keys.get(i)).stream(writer);
                if (i < x - 1) {
                    writer.write(",");
                } else {
                    writer.write("}");
                }
            }
        }
    }

    @Override
    protected void stream(XarcOutputStream out) {
        if (CONTENTS.keySet().size() == 0) {
            out.write("{}");
        } else {
            out.write("{");
            ArrayList<String> keys = new ArrayList<>(CONTENTS.keySet());
            int x = keys.size();
            for (int i = 0; i < x; i++) {
                out.write('"' + keys.get(i) + "\":");
                CONTENTS.get(keys.get(i)).stream(out);
                if (i < x - 1) {
                    out.write(",");
                } else {
                    out.write("}");
                }
            }
        }
    }
}
