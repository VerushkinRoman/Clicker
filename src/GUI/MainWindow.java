package GUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainWindow extends JFrame implements ActionListener {

    private static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    private static final int X_LOCATION = SCREEN_WIDTH / 5 * 4;
    private static final int Y_LOCATION = SCREEN_HEIGHT / 5;
    private static final int WINDOW_WIDTH = 310;
    private static final int WINDOW_HEIGHT = 200;

    private final ClickerWindowListener listener;

    private final JPanel btnPanel = new JPanel();
    private final JPanel topPanel = new JPanel();
    private final JLabel lblX = new JLabel("X");
    private final JLabel lblXData = new JLabel();
    private final JLabel lblXSavedData = new JLabel();
    private final JLabel lblY = new JLabel("Y");
    private final JLabel lblYData = new JLabel();
    private final JLabel lblYSavedData = new JLabel();
    private final JLabel lblColor = new JLabel("Color");
    private final JLabel lblColorData = new JLabel();
    private final JLabel lblColorSavedData = new JLabel();
    private final JLabel lblCurrent = new JLabel("Current");
    private final JLabel lblSaved = new JLabel("Saved");
    private final JButton btnRun = new JButton("Run");
    private final JButton btnStop = new JButton("Stop");
    private final JButton btnSettings = new JButton("Settings");
    private final JCheckBox cbAlwaysOnTop = new JCheckBox("On top");
    private final Border compound;
    private JComboBox<String> option;

    public MainWindow(ClickerWindowListener listener, String... strings) {

        getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        this.listener = listener;
        Border raisedBevel = BorderFactory.createRaisedBevelBorder();
        Border loweredBevel = BorderFactory.createLoweredBevelBorder();
        compound = BorderFactory.createCompoundBorder(raisedBevel, loweredBevel);

        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocation(X_LOCATION, Y_LOCATION);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Clicker");

        if (strings.length > 0) {
            option = new JComboBox<>(strings);
            option.setSelectedIndex(0);
            option.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
            option.setFocusable(false);
            option.addActionListener(this);
            add(option, BorderLayout.CENTER);
        }

        topPanel.setLayout(new GridLayout(0, 3));
        topPanel.add(new JLabel("Data"));
        topPanel.add(lblCurrent);
        topPanel.add(lblSaved);
        topPanel.add(lblX);
        topPanel.add(lblXData);
        topPanel.add(lblXSavedData);
        topPanel.add(lblY);
        topPanel.add(lblYData);
        topPanel.add(lblYSavedData);
        topPanel.add(lblColor);
        topPanel.add(lblColorData);
        topPanel.add(lblColorSavedData);

        lblCurrent.setOpaque(true);
        lblSaved.setOpaque(true);
        btnRun.setOpaque(true);
        btnStop.setOpaque(true);
        cbAlwaysOnTop.setSelected(true);

        btnRun.addActionListener(this);
        btnStop.addActionListener(this);
        cbAlwaysOnTop.addActionListener(this);
        btnSettings.addActionListener(this);
        btnPanel.add(btnRun);
        btnPanel.add(btnStop);
        btnPanel.add(btnSettings);
        btnPanel.add(cbAlwaysOnTop);

        add(topPanel, BorderLayout.NORTH);
        setBorder(this, compound);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setVisible(true);
    }

    private ArrayList<Component> setBorder(final Container c, Border border) {
        Component[] comps = c.getComponents();
        ArrayList<Component> compList = new ArrayList<>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container) {
                compList.addAll(setBorder((Container) comp, border));
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setHorizontalAlignment(SwingConstants.CENTER);
                    ((JLabel) comp).setBorder(border);
                }
            }
        }
        return compList;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnRun) {
            listener.start();
        } else if (src == btnStop) {
            listener.stop();
        } else if (src == cbAlwaysOnTop) {
            setAlwaysOnTop(cbAlwaysOnTop.isSelected());
            listener.alwaysOnTop(cbAlwaysOnTop.isSelected());
        } else if (src == btnSettings) {
            listener.settingsClicked();
        } else if (src == option) {
            listener.optionSelected((String) option.getSelectedItem());
        } else throw new RuntimeException("Undefined source: " + src);
    }

    public void setColorInfo(int x, int y, int color) {
        lblXData.setText(String.valueOf(x));
        lblYData.setText(String.valueOf(y));
        lblColorData.setText(String.valueOf(color));
        lblCurrent.setBackground(new Color(color));
    }

    public void setSavedData(int x, int y, int color) {
        lblXSavedData.setText(String.valueOf(x));
        lblYSavedData.setText(String.valueOf(y));
        lblColorSavedData.setText(String.valueOf(color));
        lblSaved.setBackground(new Color(color));
    }

    public void setStatus(boolean running) {
        if (running) {
            btnRun.setBackground(Color.GREEN);
            btnStop.setBackground(null);
        } else {
            btnRun.setBackground(null);
            btnStop.setBackground(Color.RED);
        }
    }
}