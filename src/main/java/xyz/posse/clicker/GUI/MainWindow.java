package xyz.posse.clicker.GUI;

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
    private static final int WINDOW_WIDTH = 300;
    private static final int WINDOW_HEIGHT = 260;
    private static final int DEFAULT_TEXT_SIZE = SCREEN_HEIGHT / 90;
    private static final int PADDING = 5;
    private static final String savedText = "Saved";
    private static final String runText = "Run";
    private static final String stopText = "Stop";

    private final ClickerWindowListener listener;

    private final JPanel btnPanel = new JPanel();
    private final JPanel additionButtonsPanel = new JPanel();
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
    private final JLabel lblSaved = new JLabel();
    private final JButton btnRun = new JButton();
    private final JButton btnStop = new JButton();
    private final JButton btnSettings = new JButton("Settings");
    private final JButton btnOpenLog = new JButton("LOG");
    private final JCheckBox cbAlwaysOnTop = new JCheckBox("On top");
    private final String defaultFontName = lblX.getFont().toString();
    private final Font defaultFont = new Font(defaultFontName, Font.BOLD, DEFAULT_TEXT_SIZE);
    private final Insets defaultInsets = new Insets(0, 0, 0, 0);
    private JComboBox<String> option;
    private Color lastUsedColor = new Color(0);

    public MainWindow(ClickerWindowListener listener, String... strings) {

        getRootPane().setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));

        this.listener = listener;
        Border raisedBevel = BorderFactory.createRaisedBevelBorder();
        Border loweredBevel = BorderFactory.createLoweredBevelBorder();
        Border compound = BorderFactory.createCompoundBorder(raisedBevel, loweredBevel);

        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setLocation(X_LOCATION, Y_LOCATION);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Clicker");

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
        btnOpenLog.addActionListener(this);

        btnRun.setFont(defaultFont);
        btnStop.setFont(defaultFont);
        cbAlwaysOnTop.setFont(defaultFont);
        btnSettings.setFont(defaultFont);

        btnRun.setMargin(defaultInsets);
        btnStop.setMargin(defaultInsets);
        btnSettings.setMargin(defaultInsets);

        additionButtonsPanel.setLayout(new GridLayout(3, 1));
        additionButtonsPanel.add(btnSettings);
        additionButtonsPanel.add(cbAlwaysOnTop);
        additionButtonsPanel.add(btnOpenLog);

        btnPanel.setLayout(new GridLayout(1, 3));
        btnPanel.add(btnRun);
        btnPanel.add(btnStop);
        btnPanel.add(additionButtonsPanel);

        setBorder(topPanel, compound);

        add(topPanel, BorderLayout.NORTH);
        if (strings.length > 0) {
            option = new JComboBox<>(strings);
            option.setSelectedIndex(0);
            option.setBorder(BorderFactory.createEmptyBorder(PADDING, 0, PADDING, 0));
            option.setFont(defaultFont);
            option.setFocusable(false);
            option.addActionListener(this);
            add(option, BorderLayout.CENTER);
        }
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
                    comp.setFont(defaultFont);
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
        } else if (src == btnOpenLog) {
            listener.logPressed();
        } else throw new RuntimeException("Undefined source: " + src);
    }

    public void setColorInfo(int x, int y, Color color) {
        lblXData.setText(String.valueOf(x));
        lblYData.setText(String.valueOf(y));
        if (!lastUsedColor.equals(color)) {
            lblColorData.setText(String.valueOf(color.getRGB()));
            lblCurrent.setBackground(color);
            lastUsedColor = color;
        }
    }

    public void setSavedData(int x, int y, Color color) {
        lblXSavedData.setText(String.valueOf(x));
        lblYSavedData.setText(String.valueOf(y));
        lblColorSavedData.setText(String.valueOf(color.getRGB()));
        lblSaved.setBackground(color);
    }

    public void setStatus(boolean running) {
        if (running) {
            btnRun.setBackground(Color.GREEN);
            btnStop.setBackground(null);
            lblCurrent.setBackground(null);
            lblXSavedData.setText("");
            lblYSavedData.setText("");
            lblColorSavedData.setText("");
            lblXData.setText("");
            lblYData.setText("");
            lblColorData.setText("");
            lblSaved.setBackground(null);
        } else {
            btnRun.setBackground(null);
            btnStop.setBackground(Color.RED);
        }
    }

    public void setKeys(String saveKey, String runKey, String stopKey) {
        String lblSavedText = savedText + " (" + saveKey + ")";
        String btnRunText = runText + " (" + runKey + ")";
        String btnStopText = stopText + " (" + stopKey + ")";

        lblSaved.setFont(new Font(defaultFontName, Font.BOLD, getFontSize(lblSaved, lblSavedText)));
        lblSaved.setText(lblSavedText);
        btnRun.setFont(new Font(defaultFontName, Font.BOLD, getFontSize(btnRun, btnRunText)));
        btnRun.setText(btnRunText);
        btnStop.setFont(new Font(defaultFontName, Font.BOLD, getFontSize(btnStop, btnStopText)));
        btnStop.setText(btnStopText);
    }

    private int getFontSize(Component component, String text) {
        int stringWidth = component.getFontMetrics(defaultFont).stringWidth(text);
        int componentWidth = component.getWidth();
        double widthRatio = (double) (componentWidth - 10) / (double) stringWidth;
        return (widthRatio < 1) ? (int) (defaultFont.getSize() * widthRatio) : defaultFont.getSize();
    }
}