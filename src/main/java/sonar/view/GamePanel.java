package sonar.view;

import sonar.Model;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private Model model;
    private View view;

    private CaptainPanel captainPanel;
    private FirstMatePanel firstMatePanel;
    private RadioOperatorPanel radioOperatorPanel;
    private EngineerPanel engineerPanel;
    private CaptainCommandPanel captainCommandPanel;

    public GamePanel(Model model, View view){
        super(new BorderLayout());
        this.model = model;
        this.view = view;

        captainPanel        = new CaptainPanel(model, view);
        firstMatePanel      = new FirstMatePanel(model, view);
        radioOperatorPanel  = new RadioOperatorPanel(model, view);
        engineerPanel       = new EngineerPanel(model, view);
        captainCommandPanel = new CaptainCommandPanel();

        JPanel boardPanels = new JPanel();
        boardPanels.setLayout(new GridLayout(2, 2));
        boardPanels.add(captainPanel);
        boardPanels.add(firstMatePanel);
        boardPanels.add(radioOperatorPanel);
        boardPanels.add(engineerPanel);

        add(boardPanels, BorderLayout.CENTER);
        add(captainCommandPanel, BorderLayout.EAST);
    }

    public void refresh(){
        captainPanel.refresh();
        firstMatePanel.refresh();
        radioOperatorPanel.refresh();
        engineerPanel.refresh();
    }

    public CaptainCommandPanel getCaptainCommandPanel() {
        return captainCommandPanel;
    }

    public CaptainPanel getCaptainPanel() {
        return captainPanel;
    }

    public EngineerPanel getEngineerPanel() {
        return engineerPanel;
    }

    public FirstMatePanel getFirstMatePanel() {
        return firstMatePanel;
    }

    public RadioOperatorPanel getRadioOperatorPanel() {
        return radioOperatorPanel;
    }
}
