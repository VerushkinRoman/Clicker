package xyz.posse.clicker.core;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.nio.file.StandardOpenOption.APPEND;

class Settings {
    private final Path path = Paths.get("config.cfg");
    private final Charset charset = StandardCharsets.UTF_8;
    private final String defaultKeySave = "F12";
    private final String defaultKeyStart = "F9";
    private final String defaultKeyStop = "F10";
    private final String defaultAlwaysOnTop = "true";
    private final String keyStartPrefix = "Key \"Start\" = ";
    private final String keyStopPrefix = "Key \"Stop\" = ";
    private final String keySavePrefix = "Key \"Save\" = ";
    private final String alwaysOnTopPrefix = "Always on top = ";

    private final Map<String, String> defaultConfig = new HashMap<>() {{
        put(keySavePrefix, defaultKeySave);
        put(keyStartPrefix, defaultKeyStart);
        put(keyStopPrefix, defaultKeyStop);
        put(alwaysOnTopPrefix, defaultAlwaysOnTop);
    }};

    private Map<String, String> config = new HashMap<>() {{
        put(keySavePrefix, "");
        put(keyStartPrefix, "");
        put(keyStopPrefix, "");
        put(alwaysOnTopPrefix, "");
    }};


    Settings() {
        initSettings();
    }

    private void initSettings() {
        try {
            if (!Files.exists(path)) {
                createConfigFile();
            } else {
                readConfigFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createConfigFile() throws IOException {
        Files.createFile(path);
        Set<Map.Entry<String, String>> entrySet = defaultConfig.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            Files.writeString(path, entry.getKey() + entry.getValue() + "\n", APPEND);
        }
        config = new HashMap<>(defaultConfig);
    }

    private void readConfigFile() throws IOException {
        String content = Files.readString(path, charset);
        String[] strings = content.split("\n");
        Set<Map.Entry<String, String>> entrySet = defaultConfig.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            String matchedString = null;
            for (String string : strings) {
                if (string.contains(entry.getKey())) {
                    matchedString = string;
                }
            }
            if (matchedString == null) {
                config.put(entry.getKey(), defaultConfig.get(entry.getKey()));
                Files.writeString(path, entry.getKey() + defaultConfig.get(entry.getKey()), APPEND);
            } else config.put(entry.getKey(), matchedString.substring(entry.getKey().length()));
        }
    }

    private void replaceString(String oldString, String newString) {
        try {
            String content = Files.readString(path, charset);
            content = content.replace(oldString, newString);
            Files.writeString(path, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getKeyStart() {
        return config.get(keyStartPrefix);
    }

    public void setKeyStart(String key) {
        replaceString(keyStartPrefix + config.get(keyStartPrefix), keyStartPrefix + key);
        config.put(keyStartPrefix, key);
    }

    public String getKeyStop() {
        return config.get(keyStopPrefix);
    }

    public void setKeyStop(String key) {
        replaceString(keyStopPrefix + config.get(keyStopPrefix), keyStopPrefix + key);
        config.put(keyStopPrefix, key);
    }

    public String getKeySave() {
        return config.get(keySavePrefix);
    }

    public void setKeySave(String key) {
        replaceString(keySavePrefix + config.get(keySavePrefix), keySavePrefix + key);
        config.put(keySavePrefix, key);
    }

    public boolean isAlwaysOnTop() {
        return Boolean.parseBoolean(config.get(alwaysOnTopPrefix));
    }

    public void setAlwaysOnTop(boolean onTop) {
        replaceString(alwaysOnTopPrefix + config.get(alwaysOnTopPrefix), alwaysOnTopPrefix + onTop);
        config.put(alwaysOnTopPrefix, String.valueOf(onTop));
    }
}