package com.donny.dendrofinance.fileio;

import com.donny.dendrofinance.gui.password.UnkPasswordGui;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonItem;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.util.Partitioner;
import com.fasterxml.jackson.core.JsonFactory;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class FileHandler {
    private final Instance CURRENT_INSTANCE;

    public FileHandler(Instance curInst) {
        CURRENT_INSTANCE = curInst;
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "FileHandler Initiated");
    }

    /*
     *  READ
     */

    public String read(File file) {
        ensure(file.getParentFile());
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, Instance.CHARSET))) {
            boolean flag = true;
            while (flag) {
                int x = reader.read();
                if (x != -1) {
                    output.append((char) x);
                } else {
                    flag = false;
                }
            }
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "file read: " + file.getAbsolutePath());
            return output.toString().replace("\r", "");
        } catch (IOException e) {
            if (file.exists()) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), file.getPath() + " could not be read from");
            } else {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), file.getPath() + " does not exist");
            }
            return "";
        }
    }

    public String read(File dir, String file) {
        return read(new File(dir.getPath() + File.separator + file));
    }

    public JsonItem readJson(File file) {
        ensure(file.getParentFile());
        try {
            JsonItem item;
            if(CURRENT_INSTANCE.large) {
                item = JsonItem.digest(new JsonFactory().createParser(file));
            } else {
                item = JsonItem.digest(new JsonFactory().createParser(read(file)));
            }
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
        return readJson(new File(dir.getAbsoluteFile() + File.separator + name));
    }

    public JsonItem readDecryptJson(File file) {
        ensure(file.getParentFile());
        try {
            JsonItem item;
            if(CURRENT_INSTANCE.large) {
                item = JsonItem.digest(new JsonFactory().createParser(new DecryptionInputStream(file, CURRENT_INSTANCE)));
            } else {
                String code = read(file);
                String[] codes = code.split(" ");
                ArrayList<Byte> bytes = new ArrayList<>();
                for(String cod : codes) {
                    for(byte b : CURRENT_INSTANCE.ENCRYPTION_HANDLER.decrypt(cod)) {
                        bytes.add(b);
                    }
                }
                byte[] raw = new byte[bytes.size()];
                for(int i = 0; i < bytes.size(); i++) {
                    raw[i] = bytes.get(i);
                }
                item = JsonItem.digest(new JsonFactory().createParser(new String(raw, Instance.CHARSET).substring(6)));
            }
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "Encrypted json file read: " + file);
            return item;
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "The json file is malformed: " + file + "\n" + e);
            return null;
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Something went wrong while trying to read json file: " + file + "\n" + e);
            return null;
        }
    }

    public JsonItem readDecryptJson(File dir, String name) {
        return readDecryptJson(new File(dir.getAbsoluteFile() + File.separator + name));
    }

    public JsonItem readDecryptJsonUnknownPassword(File file, JFrame caller) {
        ensure(file.getParentFile());
        EncryptionHandler decrypt = UnkPasswordGui.getTestPassword(caller, file.getName(), CURRENT_INSTANCE);
        if (decrypt != null) {
            try {
                JsonItem item;
                if(CURRENT_INSTANCE.large) {
                    item = JsonItem.digest(new JsonFactory().createParser(new DecryptionInputStream(file, decrypt, CURRENT_INSTANCE)));
                } else {
                    String code = read(file);
                    String[] codes = code.split(" ");
                    ArrayList<Byte> bytes = new ArrayList<>();
                    for(String cod : codes) {
                        for(byte b : decrypt.decrypt(cod)) {
                            bytes.add(b);
                        }
                    }
                    byte[] raw = new byte[bytes.size()];
                    for(int i = 0; i < bytes.size(); i++) {
                        raw[i] = bytes.get(i);
                    }
                    item = JsonItem.digest(new JsonFactory().createParser(new String(raw, Instance.CHARSET).substring(6)));
                }
                CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "Encrypted json file read: " + file);
                return item;
            } catch (JsonFormattingException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "The json file is malformed: " + file + "\n" + e);
                return null;
            } catch (IOException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Something went wrong while trying to read json file: " + file + "\n" + e);
                return null;
            }
        } else {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Password Entry Failed");
            return null;
        }
    }

    public JsonItem readDecryptJsonUnknownPassword(File dir, String name, JFrame caller) {
        return readDecryptJsonUnknownPassword(new File(dir.getAbsoluteFile() + File.separator + name), caller);
    }

    /*
     *  WRITE
     */

    public void write(File file, String output) {
        ensure(file.getParentFile());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, Instance.CHARSET))) {
            writer.write(output);
            writer.close();
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "file written: " + file.getAbsolutePath());
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), file.getPath() + " could not be written to");
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), output);
        }
    }

    public void write(File dir, String file, String output) {
        write(new File(dir.getPath() + File.separator + file), output);
    }

    public void writeJson(File file, JsonItem item) {
        ensure(file.getParentFile());
        try (FileWriter writer = new FileWriter(file, Instance.CHARSET)) {
            if(CURRENT_INSTANCE.large) {
                JsonItem.save(item, writer);
            } else {
                write(file, item.print());
            }
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "json file written: " + file.getAbsolutePath());
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to write json file: " + file + "\n" + e);
        }
    }

    public void writeJson(File dir, String name, JsonItem item) {
        writeJson(new File(dir.getAbsoluteFile() + File.separator + name), item);
    }

    public void writeEncryptJson(File file, JsonItem item) {
        ensure(file.getParentFile());
        if(CURRENT_INSTANCE.large) {
            try (EncryptionOutputStream stream = new EncryptionOutputStream(file, CURRENT_INSTANCE)) {
                stream.write("passwd".getBytes(Instance.CHARSET));
                JsonItem.saveEncrypt(item, stream);
                CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "json file written and encrypted: " + file.getAbsolutePath());
            } catch (IOException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to write encrypted json file: " + file + "\n" + e);
            }
        } else {
            String json = "passwd" + item.toString();
            StringBuilder out = new StringBuilder();
            for(byte[] segment : Partitioner.partition(json.getBytes(Instance.CHARSET), CURRENT_INSTANCE.blockSize*16)){
                out.append(" ").append(CURRENT_INSTANCE.ENCRYPTION_HANDLER.encrypt(segment));
            }
            write(file, out.toString());
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "json file written and encrypted: " + file.getAbsolutePath());
        }
    }

    public void writeEncryptJson(File dir, String name, JsonItem item) {
        writeEncryptJson(new File(dir.getAbsoluteFile() + File.separator + name), item);
    }

    public void writeEncryptJsonUnknownPassword(File file, JsonItem item, JFrame caller) {
        ensure(file.getParentFile());
        EncryptionHandler encrypt = UnkPasswordGui.getTestPassword(caller, file.getName(), CURRENT_INSTANCE);
        if (encrypt != null) {
            if(CURRENT_INSTANCE.large) {
                try (EncryptionOutputStream stream = new EncryptionOutputStream(file, encrypt, CURRENT_INSTANCE)) {
                    stream.write("passwd".getBytes(Instance.CHARSET));
                    JsonItem.saveEncrypt(item, stream);
                    CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "Json file written and encrypted: " + file.getAbsolutePath());
                } catch (IOException e) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to write encrypted json file: " + file + "\n" + e);
                }
            } else {
                String json = "passwd" + item.toString();
                StringBuilder out = new StringBuilder();
                for(byte[] segment : Partitioner.partition(json.getBytes(Instance.CHARSET), CURRENT_INSTANCE.blockSize*16)){
                    out.append(" ").append(encrypt.encrypt(segment));
                }
                write(file, out.toString());
                CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "json file written and encrypted: " + file.getAbsolutePath());
            }
        } else {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Password Entry Failed");
        }
    }

    public void writeEncryptJsonUnknownPassword(File dir, String name, JsonItem item, JFrame caller) {
        writeEncryptJsonUnknownPassword(new File(dir.getAbsoluteFile() + File.separator + name), item, caller);
    }

    /*
     *  APPEND
     */

    public void append(File file, String output) {
        ensure(file.getParentFile());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, Instance.CHARSET, true))) {
            writer.write(output);
            writer.close();
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "file appended to: " + file.getAbsolutePath());
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), file.getPath() + " could not be appended to");
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), output);
        }
    }

    public void append(File dir, String file, String output) {
        append(new File(dir.getPath() + File.separator + file), output);
    }

    /*
     *  DELETE
     */

    public void delete(File file) {
        ensure(file.getParentFile());
        file.delete();
        CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "file deleted: " + file.getAbsolutePath());
    }

    public void delete(File dir, String file) {
        delete(new File(dir.getPath() + File.separator + file));
    }

    public void deleteR(File root) {
        File[] rootList = root.listFiles();
        if (root.isDirectory() && rootList != null) {
            for (File f : rootList) {
                deleteR(f);
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

    public JsonItem getTemplate(String path) {
        return getResource("templates/" + path);
    }

    /*
     *  PRIVATE PRICE HISTORIES
     */

    public JsonObject getPrivateStock(String name) {
        File directory = new File(CURRENT_INSTANCE.data.getPath() + File.separator + "P_Stock");
        JsonObject history = null;
        File[] directoryList = directory.listFiles();
        if (directory.exists() && directoryList != null) {
            for (File f : directoryList) {
                if (f.getName().contains(name)) {
                    history = (JsonObject) readJson(f);
                }
            }
        }
        if (history != null) {
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "Private stock history read: " + name);
            return history;
        } else {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "No History File found: " + name);
            return null;
        }
    }

    public JsonObject getPrivateInventory(String id) {
        File directory = new File(CURRENT_INSTANCE.data.getPath() + File.separator + "P_Inventory");
        JsonObject history = null;
        File[] directoryList = directory.listFiles();
        if (directory.exists() && directoryList != null) {
            for (File f : directoryList) {
                if (f.getName().contains(id)) {
                    history = (JsonObject) readJson(f);
                }
            }
        }
        if (history != null) {
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "Private inventory history read: " + id);
            return history;
        } else {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "No History File found: " + id);
            return null;
        }
    }

    /*
     *  STREAMING
     */

    public JsonItem hit(String url) {
        try {
            return JsonItem.digest(new JsonFactory().createParser(new URL(url).openStream()));
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Bad Json at:" + url + "\n" + e);
            return null;
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Error connecting:" + url + "\n" + e);
            return null;
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
