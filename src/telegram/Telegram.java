package telegram;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class Telegram implements Runnable {

    private String msg;
    private static String chatID = "0";
    private static String apiToken = "0";
    private static boolean repeat = true;
    private int delay;
    private static int countdown;
    private static String urlMask = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
    private static Thread telegram = new Thread();

    private Telegram(String msg, int delay) {
        this.msg = msg;
        this.delay = delay;
        telegram = new Thread(this);
        telegram.start();
    }

    public static void setChatID(String chatID) {
        Telegram.chatID = chatID;
    }

    public static void setApiToken(String apiToken) {
        Telegram.apiToken = apiToken;
    }

    public static void setRepeat(boolean repeat) {
        Telegram.repeat = repeat;
    }

    public static void start(String msg, int delay) {
        if (telegram.isAlive()) {
            countdown = 0;
        } else {
            new Telegram(msg, delay);
        }
    }

    public static void stop() {
        telegram.interrupt();
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