package xyz.posse.clicker.GUI;

public interface ClickerWindowListener {

    void start();

    void stop();

    void settingsClicked();

    void changeStartBtn();

    void changeStopBtn();

    void alwaysOnTop(boolean onTop);

    void optionSelected(String option);

    void logPressed();

    void editorPressed(boolean editorPressed);
}