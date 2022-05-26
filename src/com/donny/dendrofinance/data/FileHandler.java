package com.donny.dendrofinance.data;

import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonItem;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FileHandler {
    private final Instance CURRENT_INSTANCE;

    public FileHandler(Instance curInst) {
        CURRENT_INSTANCE = curInst;
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "FileHandler Initiated");
    }

    public String read(File file) {
        ensure(file.getParentFile());
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, Charset.forName("unicode")))) {
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

    public String readPlain(File file) {
        ensure(file.getParentFile());
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.US_ASCII))) {
            boolean flag = true;
            while (flag) {
                int x = reader.read();
                if (x != -1) {
                    output.append((char) x);
                } else {
                    flag = false;
                }
            }
            reader.close();
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "file read: " + file.getAbsolutePath());
            return output.toString().replace("\r", "");
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), file.getPath() + " could not be read from");
            return "";
        }
    }

    public String readPlain(File dir, String file) {
        return readPlain(new File(dir.getPath() + File.separator + file));
    }

    public String readDecrypt(File file) {
        ensure(file.getParentFile());
        String str = CURRENT_INSTANCE.ENCRYPTION_HANDLER.decrypt(read(file));
        if (str == null) {
            return null;
        } else {
            return str.replace("\r", "");
        }
    }

    public String readDecrypt(File dir, String file) {
        return readDecrypt(new File(dir.getPath() + File.separator + file));
    }

    public String readDecryptUnknownPassword(File file) {
        ensure(file.getParentFile());
        EncryptionHandler decrypt = new EncryptionHandler(CURRENT_INSTANCE);
        //TODO add password gui
        String str = decrypt.decrypt(read(file));
        if (str == null) {
            return null;
        } else {
            return str.replace("\r", "");
        }
    }

    public String readDecryptUnknownPassword(File dir, String file) {
        return readDecryptUnknownPassword(new File(dir.getPath() + File.separator + file));
    }

    public byte[] getTemplate(String path) {
        return getResource("templates/" + path);
    }

    public byte[] getResource(String path) {
        try (InputStream stream = getClass().getResourceAsStream("/com/donny/dendrofinance/resources/" + path)) {
            return stream.readAllBytes();
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Resource not located: " + path);
            return null;
        } catch (NullPointerException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "No such resource: " + path);
            return null;
        }
    }

    public void write(File file, String output) {
        ensure(file.getParentFile());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, Charset.forName("unicode")))) {
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

    public void writePlain(File file, String output) {
        ensure(file.getParentFile());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.US_ASCII))) {
            writer.write(output);
            writer.close();
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "file written: " + file.getAbsolutePath());
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), file.getPath() + " could not be written to");
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), output);
        }
    }

    public void writePlain(File dir, String file, String output) {
        writePlain(new File(dir.getPath() + File.separator + file), output);
    }

    public void writeEncrypt(File file, String output) {
        ensure(file.getParentFile());
        String str = CURRENT_INSTANCE.ENCRYPTION_HANDLER.encrypt(output);
        if (str != null) {
            write(file, str);
        }
    }

    public void writeEncrypt(File dir, String file, String output) {
        writeEncrypt(new File(dir.getPath() + File.separator + file), output);
    }

    public void append(File file, String output) {
        ensure(file.getParentFile());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, Charset.forName("unicode"), true))) {
            writer.write(output);
            writer.close();
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "file appended: " + file.getAbsolutePath());
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), file.getPath() + " could not be written to");
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), output);
        }
    }

    public void append(File dir, String file, String output) {
        append(new File(dir.getPath() + File.separator + file), output);
    }

    public void appendPlain(File file, String output) {
        ensure(file.getParentFile());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.US_ASCII, true))) {
            writer.write(output);
            writer.close();
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "file appended: " + file.getAbsolutePath());
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), file.getPath() + " could not be written to");
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), output);
        }
    }

    public void appendPlain(File dir, String file, String output) {
        appendPlain(new File(dir.getPath() + File.separator + file), output);
    }

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

    public BigDecimal getLatestPrivateStock(String name) {
        JsonArray brackets = getPrivateStock(name);
        return brackets.getObject(brackets.size() - 1).getDecimal("price").decimal;
    }

    public JsonArray getPrivateStock(String name) {
        File directory = new File(CURRENT_INSTANCE.data.getPath() + File.separator + "P_Stock");
        JsonArray history = null;
        File[] directoryList = directory.listFiles();
        if (directory.exists() && directoryList != null) {
            for (File f : directoryList) {
                if (f.getName().contains(name)) {
                    try {
                        history = (JsonArray) JsonItem.sanitizeDigest(readPlain(f));
                    } catch (JsonFormattingException e) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Bad Private Stock File: " + f.getPath());
                    }
                }
            }
        }
        if (history != null) {
            return history;
        } else {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "No History File found!: " + name);
            return null;
        }
    }

    public JsonItem hit(String url) {
        try {
            return JsonItem.sanitizeDigest(streamRaw(url));
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Bad Json at:" + url + "\n" + e);
            return null;
        }
    }

    public String streamRaw(String url) {
        int c = 0;
        try (
                InputStream stream = new URL(url).openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
        ) {
            StringBuilder sb = new StringBuilder();
            while (c != -1) {
                c = reader.read();
                if (c != -1) {
                    sb.append((char) c);
                }
            }
            stream.close();
            return sb.toString();
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Could not connect to " + url + "\n" + e);
            return "";
        }
    }

    public void ensure(File file) {
        if (!file.exists()) {
            if (file.getParentFile().exists()) {
                file.mkdir();
            } else {
                ensure(file.getParentFile());
            }
        }
    }
}
