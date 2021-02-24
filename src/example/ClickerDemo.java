package example;

import core.Clicker;
import core.ScriptForClicker;

import java.awt.*;
import java.time.LocalTime;
import java.time.ZoneId;

public class ClickerDemo extends Thread implements ScriptForClicker {

    private String msg = "Something gone wrong";
    private String telegramChatID = "123456789"; //Example. Use your own!
    private String telegramApiToken = "1234567890:ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789"; //Example. Use your own!

    private String option1 = "Drag something";
    private String option2 = "Clicking by circle";
    private String option3 = "Click once";
    private String[] options = {option1, option2, option3};

    private String option;

    private LocalTime startTime = LocalTime.of(15, 55);
    private LocalTime endTime = LocalTime.of(16, 0);
    private ZoneId zoneId = ZoneId.of("Europe/Moscow");
    private LocalTime now;

    private Clicker clicker;

    private ClickerDemo() throws AWTException {
        clicker = new Clicker(this, options);
        clicker.setTelegramData(telegramChatID, telegramApiToken);
    }

    public static void main(String[] args) throws AWTException {
        new ClickerDemo();
    }

    @Override
    public synchronized void run() {
        try {
            while (!interrupted()) {
                if (option.equals(option1)) {
                    dragSomething();
                } else if (option.equals(option2) || option.equals(option3)) {
                    clicking();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void clicking() throws InterruptedException {

        if (clicker.getColor(500, 500) == -1) { //white color
            clicker.putLog("if white at 500x500 then click 500x500");
            clicker.click(500, 500);
            wait(3000);
        }

        if (option.equals(option3)) {
            clicker.stop();
        }
    }

    private void dragSomething() throws InterruptedException {

        if (clicker.getColor(300, 300) == -1) {
            clicker.putLog("if white at 300x300 then drag from 200x200 to 500x500");
            clicker.drag(200, 200, 500, 500, 5, 100);
            wait(5000);
            clicker.startTelegram(msg, 20, true); //Start (restart) telegram message with delay 20 seconds.
        }

        if (clicker.getColor(900, 900) == -1) {
            clicker.putLog("if white at 900x900 then stop telegram message");
            clicker.stopTelegram();
        }

        now = LocalTime.now(zoneId);
        if (!now.isBefore(startTime) && now.isAfter(endTime)) {
            clicker.stopTelegram();
        }
    }

    @Override
    public void optionSelected(String option) {
        this.option = option;
    }
}