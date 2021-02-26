package xyz.posse.clicker.telegram;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class Telegram implements Runnable {

    private final String urlMask = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
    private String chatID = "0";
    private String apiToken = "0";
    private int delay = 0;
    private String msg = "";
    private boolean repeat = false;
    private int countdown;

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    @Override
    public synchronized void run() {
        try {
            while (repeat) {
                for (countdown = 0; countdown < delay; countdown++) {
                    wait(1000);
                }
                String urlString = String.format(urlMask, apiToken, chatID, msg);
                URL url = new URL(urlString);
                URLConnection connection = url.openConnection();
                new BufferedInputStream(connection.getInputStream());
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}