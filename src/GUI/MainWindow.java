package GUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainWindow extends JFrame implements ActionListener {
    private static final int WINDOW_WIDTH = 300;
    private static final int WINDOW_HEIGHT = 180;

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

    public MainWindow(ClickerWindowListener listener) {

        this.listener = listener;
        Border raisedBevel = BorderFactory.createRaisedBevelBorder();
        Border loweredBevel = BorderFactory.createLoweredBevelBorder();
        compound = BorderFactory.createCompoundBorder(raisedBevel, loweredBevel);

        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocation(WINDOW_WIDTH, WINDOW_HEIGHT);
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

        setResizable(false);
        setAlwaysOnTop(true);
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
        } else if (src == btnSettings) {
            listener.settingsClicked();
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
}