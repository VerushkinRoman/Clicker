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
    private static final int DRUG_DELAY = 150;
    private static final Logger commonLogger = Logger.getLogger(Clicker.class.getName());
    private final MainWindow window;
    private final ScriptForClicker script;
    private final Clipboard clipboard;
    private final SettingsWindow settings;
    private final Robot robot;
    private Thread scriptThread;
    private Thread refreshColorData;
    private StringSelection stringSelection;
    private NativeMouseEvent mouseCoordinates;
    private int x = 100;
    private int y = 100;
    private int color;
    private int keySave = 88;
    private int keyStart = 67;
    private int keyStop = 68;
    private long lastTimestamp = System.currentTimeMillis();
    private boolean isChangeStartBtn = false;
    private boolean isChangeStopBtn = false;

    public Clicker(ScriptForClicker script) throws AWTException {
        scriptThread = new Thread((Runnable) script);
        refreshColorData = new Thread(new RefreshColorData());
        robot = new Robot();
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
        registerHook();
        GlobalScreen.setEventDispatcher(new SwingDispatchService());
        GlobalScreen.addNativeKeyListener(this);
        GlobalScreen.addNativeMouseMotionListener(this);
        window = new MainWindow(this);
        window.setStatus(scriptThread.isAlive());
        settings = new SettingsWindow(this);
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        this.script = script;
        Handler fileHandler = getNewFileHandler();
        commonLogger.addHandler(new ConsoleHandler());
        commonLogger.addHandler(fileHandler);
        commonLogger.setUseParentHandlers(false);
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

    static void registerHook() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    static void unregisterHook() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }

    public void click(int x, int y) {
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public synchronized void drag(int oldX, int oldY, int newX, int newY, int steps) {
        try {
            robot.mouseMove(oldX, oldY);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseMove(oldX + 1, oldY);
            script.wait(DRUG_DELAY);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            script.wait(DRUG_DELAY);
            if (steps == 0) steps = 1;
            int xDiff = (newX - oldX) / steps;
            int yDiff = (newY - oldY) / steps;
            for (int i = 1; i <= steps; i++) {
                robot.mouseMove(oldX + xDiff * i, oldY + yDiff * i);
                script.wait(DRUG_DELAY);
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
        if (scriptThread.isAlive()) {
            window.setStatus(!scriptThread.isAlive());
            scriptThread.interrupt();
            window.requestFocus();
        }
        Telegram.stop();
    }

    @Override
    public void settingsClicked() {
        settings.setVisible(true);
    }

    @Override
    public void changeStartBtn() {
        isChangeStartBtn = true;
        settings.setNewButton("start", "");
    }

    @Override
    public void changeStopBtn() {
        isChangeStopBtn = true;
        settings.setNewButton("stop", "");
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
        //don't need
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        //don't need
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        lastTimestamp = System.currentTimeMillis();
        int key = nativeKeyEvent.getKeyCode();
        putLog("Key pressed: " + key);
        if (key == keySave) {
            setCurrentMousePositionData();
            window.setSavedData(x, y, color);
            stringSelection = new StringSelection(
                    "if(clicker.getColor(" + x + ", " + y + ") == " +
                            color + ") {\n" + "clicker.putLog(\"\");\n" + "}"
            );
            clipboard.setContents(stringSelection, stringSelection);
        } else if (key == keyStop) {
            stop();
        } else if (key == keyStart) {
            start();
        }
        if (isChangeStartBtn) {
            keyStart = key;
            settings.setNewButton("start", "OK");
            isChangeStartBtn = false;
        } else if (isChangeStopBtn) {
            keyStop = key;
            settings.setNewButton("stop", "OK");
            isChangeStartBtn = false;
        }
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
        mouseCoordinates = nativeMouseEvent;
        if (!scriptThread.isAlive() && !refreshColorData.isAlive()) {
            refreshColorData = new Thread(new RefreshColorData());
            refreshColorData.start();
        }
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {
        //don't need
    }

    private void setCurrentMousePositionData() {
        x = mouseCoordinates.getX();
        y = mouseCoordinates.getY();
        color = robot.getPixelColor(x, y).getRGB();
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