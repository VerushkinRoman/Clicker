package core;

import GUI.ClickerWindowListener;
import GUI.MainWindow;
import GUI.SettingsWindow;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.dispatcher.SwingDispatchService;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseMotionListener;
import telegram.Telegram;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.*;

public class Clicker implements ClickerWindowListener, NativeKeyListener, NativeMouseMotionListener {

    private static final int REFRESH_DELAY = 300;
    private static final int DRAG_DELAY = 150;
    private static final int DRAG_STEPS = 10;
    private static final Logger commonLogger = Logger.getLogger(Clicker.class.getName());
    private final MainWindow window;
    private final Settings settings;
    private final ScriptForClicker script;
    private final Clipboard clipboard;
    private final SettingsWindow settingsWindow;
    private final Robot robot;
    private final Telegram telegram;
    private Thread telegramThread;
    private Thread scriptThread;
    private Thread refreshColorData;
    private NativeMouseEvent mouseCoordinates;
    private int x;
    private int y;
    private int color;
    private long lastTimestamp = System.currentTimeMillis();
    private boolean isChangeStartBtn = false;
    private boolean isChangeStopBtn = false;
    private final String start = "start";
    private final String stop = "stop";

    public Clicker(ScriptForClicker script, String... options) throws AWTException {
        this.script = script;
        scriptThread = new Thread((Runnable) script);
        refreshColorData = new Thread(new RefreshColorData());
        telegram = new Telegram();
        telegramThread = new Thread(telegram);
        robot = new Robot();
        window = new MainWindow(this, options);
        if (options.length > 0) script.optionSelected(options[0]);
        window.setStatus(scriptThread.isAlive());
        settings = new Settings();
        settingsWindow = new SettingsWindow(this);
        settingsWindow.setNewButton(start, settings.getKeyStart());
        settingsWindow.setNewButton(stop, settings.getKeyStop());
        window.setAlwaysOnTop(settings.isAlwaysOnTop());
        setWindowKeys();
        waitForMainWindow();
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        initLoggers();
        registerHook();
    }

    private void initLoggers() {
        Handler fileHandler = getNewFileHandler();
        fileHandler.setLevel(Level.INFO);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINE);
        commonLogger.setLevel(Level.FINE);
        commonLogger.addHandler(consoleHandler);
        commonLogger.addHandler(fileHandler);
        commonLogger.setUseParentHandlers(false);
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
        GlobalScreen.setEventDispatcher(new SwingDispatchService());
        GlobalScreen.addNativeKeyListener(this);
        GlobalScreen.addNativeMouseMotionListener(this);
    }

    private void waitForMainWindow() {
        while (!window.isVisible()) {
            Thread.onSpinWait();
        }
    }

    private Handler getNewFileHandler() {
        FileHandler handler = null;
        try {
            handler = new FileHandler("Clicker.txt", 1024 * 10_000, 1, true);
            handler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    DateFormat DATE_FORMAT = new SimpleDateFormat("[HH:mm:ss]");
                    return String.format(
                            "%s: %s", DATE_FORMAT.format(System.currentTimeMillis()),
                            record.getMessage()) + "\n";
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return handler;
    }

    private static void registerHook() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    private static void unregisterHook() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }

    public void click(int x, int y) {
        doClick(x, y);
    }

    public void click(Point point) {
        doClick(point.x, point.y);
    }

    private void doClick(int x, int y) {
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public void drag(int oldX, int oldY, int newX, int newY, int steps, int delay) {
        dragObject(oldX, oldY, newX, newY, steps, delay);
    }

    public void drag(int oldX, int oldY, int newX, int newY, int steps) {
        dragObject(oldX, oldY, newX, newY, steps, DRAG_DELAY);
    }

    public void drag(int oldX, int oldY, int newX, int newY) {
        dragObject(oldX, oldY, newX, newY, DRAG_STEPS, DRAG_DELAY);
    }

    public void drag(Point start, Point end, int steps, int delay) {
        dragObject(start.x, start.y, end.x, end.y, steps, delay);
    }

    public void drag(Point start, Point end, int steps) {
        dragObject(start.x, start.y, end.x, end.y, steps, DRAG_DELAY);
    }

    public void drag(Point start, Point end) {
        dragObject(start.x, start.y, end.x, end.y, DRAG_STEPS, DRAG_DELAY);
    }

    private synchronized void dragObject(int oldX, int oldY, int newX, int newY, int steps, int delay) {
        try {
            robot.mouseMove(oldX, oldY);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            script.wait(delay);
            if (steps == 0) steps = 1;
            int xDiff = (newX - oldX) / steps;
            int yDiff = (newY - oldY) / steps;
            for (int i = 1; i <= steps; i++) {
                robot.mouseMove(oldX + xDiff * i, oldY + yDiff * i);
                script.wait(delay);
            }
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getColor(int x, int y) {
        return robot.getPixelColor(x, y).getRGB();
    }

    public void putLog(String msg) {
        commonLogger.info(msg);
    }

    public void startTelegram(String msg, int delay, boolean repeat) {
        telegram.setMsg(msg);
        telegram.setDelay(delay);
        telegram.setRepeat(repeat);
        if (telegramThread.isAlive()) {
            telegram.setCountdown(0);
        } else {
            telegramThread = new Thread(telegram);
            telegramThread.start();
        }
    }

    public void stopTelegram() {
        telegramThread.interrupt();
    }

    @Override
    public void start() {
        if (!scriptThread.isAlive()) {
            scriptThread = new Thread((Runnable) script);
            scriptThread.start();
        }
        if (refreshColorData.isAlive()) {
            refreshColorData.interrupt();
        }
        window.setStatus(scriptThread.isAlive());
    }

    @Override
    public void stop() {
        scriptThread.interrupt();
        window.setStatus(false);
        window.requestFocus();
        stopTelegram();
    }

    @Override
    public void settingsClicked() {
        settingsWindow.setVisible(true);
    }

    @Override
    public void changeStartBtn() {
        isChangeStartBtn = true;
        settingsWindow.setNewButton(start, "");
    }

    @Override
    public void changeStopBtn() {
        isChangeStopBtn = true;
        settingsWindow.setNewButton(stop, "");
    }

    @Override
    public void alwaysOnTop(boolean onTop) {
        settings.setAlwaysOnTop(onTop);
    }

    @Override
    public void optionSelected(String option) {
        script.optionSelected(option);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
        //don't need
    }

    @Override
    public synchronized void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        try {
            lastTimestamp = System.currentTimeMillis();
            String key = NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode());
            commonLogger.fine("Key pressed: " + key);
            if (key.equals(settings.getKeySave())) {
                setCurrentMousePositionData();
                window.setSavedData(x, y, color);
                StringSelection stringSelection = new StringSelection(
                        "if(clicker.getColor(" + x + ", " + y + ") == " +
                                color + ") {\n" + "clicker.putLog(\"\");\n" + "}"
                );
                clipboard.setContents(stringSelection, stringSelection);
            } else if (key.equals(settings.getKeyStop())) {
                stop();
            } else if (key.equals(settings.getKeyStart())) {
                start();
            }
            if (isChangeStartBtn || isChangeStopBtn) {
                if (isChangeStartBtn) {
                    settings.setKeyStart(key);
                    settingsWindow.setNewButton("start", key);
                } else {
                    settings.setKeyStop(key);
                    settingsWindow.setNewButton("stop", key);
                }
                setWindowKeys();
                isChangeStartBtn = false;
                isChangeStopBtn = false;
                wait(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        //don't need
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
        mouseCoordinates = nativeMouseEvent;
        if (!scriptThread.isAlive() && !refreshColorData.isAlive()) {
            refreshColorData = new Thread(new RefreshColorData());
            refreshColorData.start();
            window.setStatus(scriptThread.isAlive());
        }
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {
        //don't need
    }

    public void setTelegramData(String chatID, String token) {
        telegram.setChatID(chatID);
        telegram.setApiToken(token);
    }

    private void setCurrentMousePositionData() {
        x = mouseCoordinates.getX();
        y = mouseCoordinates.getY();
        color = robot.getPixelColor(x, y).getRGB();
    }

    private void setWindowKeys() {
        window.setKeys(settings.getKeySave(), settings.getKeyStart(), settings.getKeyStop());
    }

    private class RefreshColorData implements Runnable {

        @Override
        public void run() {
            while (!refreshColorData.isInterrupted()) {
                if (System.currentTimeMillis() - lastTimestamp > REFRESH_DELAY && !scriptThread.isAlive()) {
                    setCurrentMousePositionData();
                    window.setColorInfo(x, y, color);
                    lastTimestamp = System.currentTimeMillis();
                }
            }
        }
    }
}