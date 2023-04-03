package com.donny.dendroroot.fileio;

import com.donny.dendroroot.gui.password.UnkPasswordGui;
import com.donny.dendroroot.instance.Instance;
import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.json.JsonItem;
import com.fasterxml.jackson.core.JsonFactory;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class FileHandler {
    protected final Instance CURRENT_INSTANCE;

    public FileHandler(Instance curInst) {
        CURRENT_INSTANCE = curInst;
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "FileHandler Initiated");
    }

    /*
     *  READ
     */

    public byte[] readBytes(File file) {
        ensure(file.getParentFile());
        ArrayList<Byte> bytes = new ArrayList<>();
        try (FileInputStream reader = new FileInputStream(file)) {
            boolean flag = true;
            while (flag) {
                int x = reader.read();
                if (x != -1) {
                    bytes.add((byte) x);
                } else {
                    flag = false;
                }
            }
            byte[] out = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); i++) {
                out[i] = bytes.get(i);
            }
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "file read: " + file.getAbsolutePath());
            return out;
        } catch (IOException e) {
            if (file.exists()) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), file.getAbsoluteFile() + " could not be read from");
            } else {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), file.getAbsoluteFile() + " does not exist");
            }
            return null;
        }
    }

    public String read(File file) {
        byte[] read = readBytes(file);
        if (read == null) {
            return null;
        } else {
            return new String(read, Instance.CHARSET).replace("\r", "");
        }
    }

    public String read(File dir, String file) {
        return read(new File(dir.getAbsoluteFile() + File.separator + file));
    }

    public JsonItem readJson(File file) {
        ensure(file.getParentFile());
        try {
            JsonItem item;
            item = JsonItem.digest(new JsonFactory().createParser(file));
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "JSON file read: " + file);
            return item;
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Something went wrong while trying to read json file: " + file + "\n" + e);
            return null;
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "The json file is malformed: " + file + "\n" + e);
            return null;
        }
    }

    public JsonItem readJson(File dir, String name) {
        return readJson(new File(dir, name));
    }

    public byte[] readDecryptUnknownPassword(File file, JFrame caller) {
        ensure(file.getParentFile());
        EncryptionHandler decrypt = UnkPasswordGui.getTestPassword(caller, file.getName(), CURRENT_INSTANCE);
        if (decrypt != null) {
            return decrypt.decrypt(readBytes(file));
        } else {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Password Entry Failed");
            return new byte[0];
        }
    }

    public byte[] readDecryptUnknownPassword(File dir, String name, JFrame caller) {
        return readDecryptUnknownPassword(new File(dir, name), caller);
    }

    public byte[] readDecrypt(File file) {
        return CURRENT_INSTANCE.ENCRYPTION_HANDLER.decrypt(readBytes(file));
    }

    public byte[] readDecrypt(File dir, String name) {
        return readDecrypt(new File(dir, name));
    }

    public JsonItem readDecryptJson(File file) {
        ensure(file.getParentFile());
        try {
            byte[] plain = readDecrypt(file);
            JsonItem item = JsonItem.digest(new String(plain, Instance.CHARSET));
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "Encrypted json file read: " + file);
            return item;
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "The json file is malformed: " + file + "\n" + e);
            return null;
        }
    }

    public JsonItem readDecryptJson(File dir, String name) {
        return readDecryptJson(new File(dir, name));
    }

    public JsonItem readDecryptJsonUnknownPassword(File file, JFrame caller) {
        ensure(file.getParentFile());
        try {
            byte[] plain = readDecryptUnknownPassword(file, caller);
            JsonItem item = JsonItem.digest(new String(plain, Instance.CHARSET));
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "Encrypted json file read: " + file);
            return item;
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "The json file is malformed: " + file + "\n" + e);
            return null;
        }
    }

    public JsonItem readDecryptJsonUnknownPassword(File dir, String name, JFrame caller) {
        return readDecryptJsonUnknownPassword(new File(dir, name), caller);
    }

    /*
     *  WRITE
     */

    public void writeBytes(File file, byte[] bytes) {
        ensure(file.getParentFile());
        try (FileOutputStream writer = new FileOutputStream(file)) {
            writer.write(bytes);
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "file written: " + file.getAbsolutePath());
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), file.getAbsoluteFile() + " could not be written to");
        }
    }

    public void write(File file, String output) {
        writeBytes(file, output.replace("\r", "").getBytes(Instance.CHARSET));
    }

    public void write(File dir, String file, String output) {
        write(new File(dir.getAbsoluteFile() + File.separator + file), output);
    }

    public void writeJson(File file, JsonItem item) {
        ensure(file.getParentFile());
        try (FileWriter writer = new FileWriter(file, Instance.CHARSET)) {
            JsonItem.save(item, writer);
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "json file written: " + file.getAbsolutePath());
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to write json file: " + file + "\n" + e);
        }
    }

    public void writeJson(File dir, String name, JsonItem item) {
        writeJson(new File(dir, name), item);
    }

    public void writeEncrypt(File file, String output, EncryptionHandler handler) {
        writeBytes(file, handler.encrypt(output.getBytes(Instance.CHARSET)));
    }

    public void writeEncrypt(File dir, String name, String output, EncryptionHandler handler) {
        writeEncrypt(new File(dir, name), output);
    }

    public void writeEncrypt(File file, String output) {
        writeEncrypt(file, output, CURRENT_INSTANCE.ENCRYPTION_HANDLER);
    }

    public void writeEncrypt(File dir, String name, String output) {
        writeEncrypt(new File(dir, name), output);
    }

    public void writeEncryptUnknownPassword(File file, String output, JFrame caller) {
        ensure(file.getParentFile());
        EncryptionHandler encrypt = UnkPasswordGui.getTestPassword(caller, file.getName(), CURRENT_INSTANCE);
        if (encrypt != null) {
            writeBytes(file, encrypt.encrypt(output.getBytes(Instance.CHARSET)));
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "file written and encrypted: " + file.getAbsolutePath());
        } else {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Password Entry Failed");
        }
    }

    public void writeEncryptUnknownPassword(File dir, String name, String output, JFrame caller) {
        writeEncryptUnknownPassword(new File(dir, name), output, caller);
    }

    public void writeEncryptJson(File file, JsonItem item, EncryptionHandler handler) {
        writeEncrypt(file, item.toString(), handler);
    }

    public void writeEncryptJson(File dir, String name, JsonItem item, EncryptionHandler handler) {
        writeEncryptJson(new File(dir, name), item, handler);
    }

    public void writeEncryptJson(File file, JsonItem item) {
        writeEncryptJson(file, item, CURRENT_INSTANCE.ENCRYPTION_HANDLER);
    }

    public void writeEncryptJson(File dir, String name, JsonItem item) {
        writeEncryptJson(new File(dir, name), item);
    }

    public void writeEncryptJsonUnknownPassword(File file, JsonItem item, JFrame caller) {
        writeEncryptUnknownPassword(file, item.toString(), caller);
    }

    public void writeEncryptJsonUnknownPassword(File dir, String name, JsonItem item, JFrame caller) {
        writeEncryptJsonUnknownPassword(new File(dir, name), item, caller);
    }

    /*
     *  APPEND
     */

    public void appendBytes(File file, byte[] bytes) {
        ensure(file.getParentFile());
        try (FileOutputStream writer = new FileOutputStream(file, true)) {
            writer.write(bytes);
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "file appended to: " + file.getAbsolutePath());
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), file.getAbsoluteFile() + " could not be appended to");
        }
    }

    public void append(File file, String output) {
        appendBytes(file, output.replace("\r", "").getBytes(Instance.CHARSET));
    }

    public void append(File dir, String file, String output) {
        append(new File(dir.getAbsoluteFile() + File.separator + file), output);
    }

    /*
     *  DELETE
     */

    public void delete(File file) {
        ensure(file.getParentFile());
        if (file.delete()) {
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "file deleted: " + file.getAbsolutePath());
        } else {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "file was not deleted: " + file.getAbsolutePath());
        }
    }

    public void delete(File dir, String file) {
        delete(new File(dir.getAbsoluteFile() + File.separator + file));
    }

    public void deleteRecursive(File root) {
        File[] rootList = root.listFiles();
        if (root.isDirectory() && rootList != null) {
            for (File f : rootList) {
                deleteRecursive(f);
            }
        }
        delete(root);
    }

    /*
     *  RESOURCES
     */

    public JsonItem getResource(String path) {
        try (InputStream stream = getClass().getResourceAsStream("/com/donny/dendrofinance/resources/" + path)) {
            JsonItem item = JsonItem.digest(new JsonFactory().createParser(stream));
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "Resource loaded: " + path);
            return item;
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Resource not located: " + path);
            return null;
        } catch (NullPointerException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "No such resource: " + path);
            return null;
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed resource: " + path);
            return null;
        }
    }

    /*
     *  STREAMING
     */

    public JsonItem hit(String url) throws ApiLimitReachedException {
        try {
            return JsonItem.digest(new JsonFactory().createParser(new URL(url).openStream()));
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Bad Json at: " + url + "\n" + e);
            return null;
        } catch (IOException e) {
            if (e.getMessage().contains("429")) {
                throw new ApiLimitReachedException();
            } else {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Error connecting: " + url + "\n" + e);
                return null;
            }
        }
    }

    /*
     *  UTILITIES
     */

    public void ensure(File file) {
        if (!file.exists()) {
            if (file.getParentFile().exists()) {
                if (!file.mkdir()) {
                    ensure(file);
                } else {
                    CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "folder created: " + file.getAbsolutePath());
                }
            } else {
                ensure(file.getParentFile());
            }
        }
    }
}
