package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsWindow extends JFrame implements ActionListener {

    private static final int WINDOW_WIDTH = 200;
    private static final int WINDOW_HEIGHT = 180;

    private final ClickerWindowListener listener;

    private final JPanel leftPanel = new JPanel();
    private final JPanel rightPanel = new JPanel();
    private final JButton btnCngStart = new JButton("Change Start");
    private final JButton btnCngStop = new JButton("Change Stop");
    private final JButton btnClose = new JButton("Close");
    private final JLabel lblStartBtn = new JLabel();
    private final JLabel lblStopBtn = new JLabel();

    public SettingsWindow(ClickerWindowListener listener) {
        this.listener = listener;

        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocation(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Settings");

        lblStartBtn.setHorizontalAlignment(SwingConstants.CENTER);
        lblStopBtn.setHorizontalAlignment(SwingConstants.CENTER);

        btnCngStart.addActionListener(this);
        btnCngStop.addActionListener(this);
        btnClose.addActionListener(this);

        leftPanel.setLayout(new GridLayout(0, 1));
        leftPanel.add(btnCngStart);
        leftPanel.add(btnCngStop);

        rightPanel.setLayout(new GridLayout(0, 1));
        rightPanel.add(lblStartBtn);
        rightPanel.add(lblStopBtn);
        rightPanel.setPreferredSize(new Dimension(70, 0));

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
        add(btnClose, BorderLayout.SOUTH);

        setResizable(false);
        setAlwaysOnTop(true);
        setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnCngStart) {
            listener.changeStartBtn();
        } else if (src == btnCngStop) {
            listener.changeStopBtn();
        } else if (src == btnClose) {
            this.setVisible(false);
        } else throw new RuntimeException("Undefined source: " + src);
    }

    public void setNewButton(String src, String button) {
        if (src.equals("start")) {
            lblStartBtn.setText(button);
        } else if (src.equals("stop")) {
            lblStopBtn.setText(button);
        }
    }
}