package sonar.view;

import javax.swing.*;
import java.awt.*;

public class CaptainCommandPanel extends JPanel {
    private JButton btnNorth, btnSouth, btnEast, btnWest, btnSurfaceDive, btnSilence, btnUndoMove;
    private JButton btnDropMine, btnTriggerMine;
    private JButton btnLaunchTorpedo, btnLaunchDrone, btnActivateSonar;
    private JButton btnActivateCustomScenarioSystem;
    private JButton btnEndTurn;
    private JCheckBox ckbShowEnemySub;

    public CaptainCommandPanel(){
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        btnNorth         = new JButton("North");
        btnSouth         = new JButton("South");
        btnEast          = new JButton("East");
        btnWest          = new JButton("West");
        btnSurfaceDive   = new JButton("Surface");
        btnSilence       = new JButton("Silence");
        btnUndoMove      = new JButton("Undo Move");

        btnDropMine      = new JButton("Drop Mine");
        btnTriggerMine   = new JButton("Trigger Mine");
        btnLaunchTorpedo = new JButton("Launch Torpedo");
        btnLaunchDrone   = new JButton("Launch Drone");
        btnActivateSonar = new JButton("Activate Sonar");
        btnActivateCustomScenarioSystem = new JButton("Activate Custom");

        btnEndTurn       = new JButton("End Turn");

        ckbShowEnemySub  = new JCheckBox("Show Enemy Sub");

        btnSilence.setForeground(Color.RED);
        btnLaunchTorpedo.setForeground(Color.RED);
        btnTriggerMine.setForeground(Color.RED);

        btnEndTurn.setBackground(Color.YELLOW);

        JPanel directionPanel = new JPanel(new GridLayout(3, 3));
        directionPanel.add(btnSilence);
        directionPanel.add(btnNorth);
        directionPanel.add(new JLabel(""));
        directionPanel.add(btnWest);
        directionPanel.add(btnSurfaceDive);
        directionPanel.add(btnEast);
        directionPanel.add(new JLabel(""));
        directionPanel.add(btnSouth);
        directionPanel.add(btnUndoMove);
        //directionPanel.add(new JLabel(""));

        JPanel systemPanel = new JPanel(new GridLayout(3, 2));
        systemPanel.add(btnDropMine);
        systemPanel.add(btnTriggerMine);
        systemPanel.add(btnLaunchTorpedo);
        systemPanel.add(btnLaunchDrone);
        systemPanel.add(btnActivateSonar);
        systemPanel.add(btnActivateCustomScenarioSystem);

        JPanel endTurnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        endTurnPanel.add(btnEndTurn);

        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.PAGE_AXIS));

        JPanel showEnemySubPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        showEnemySubPanel.add(ckbShowEnemySub);
        optionPanel.add(showEnemySubPanel);

        add(directionPanel);
        add(systemPanel);
        add(endTurnPanel);
        add(optionPanel);
    }

    public JButton getBtnNorth() {
        return btnNorth;
    }

    public JButton getBtnSouth() {
        return btnSouth;
    }

    public JButton getBtnEast() {
        return btnEast;
    }

    public JButton getBtnWest() {
        return btnWest;
    }

    public JButton getBtnSurfaceDive() {
        return btnSurfaceDive;
    }

    public JButton getBtnSilence() {
        return btnSilence;
    }

    public JButton getBtnUndoMove() {
        return btnUndoMove;
    }

    public JButton getBtnDropMine() {
        return btnDropMine;
    }

    public JButton getBtnTriggerMine() {
        return btnTriggerMine;
    }

    public JButton getBtnLaunchTorpedo() {
        return btnLaunchTorpedo;
    }

    public JButton getBtnLaunchDrone() {
        return btnLaunchDrone;
    }

    public JButton getBtnActivateSonar() {
        return btnActivateSonar;
    }

    public JButton getBtnActivateCustomScenarioSystem() {
        return btnActivateCustomScenarioSystem;
    }

    public JButton getBtnEndTurn() {
        return btnEndTurn;
    }

    public JCheckBox getCkbShowEnemySub() {
        return ckbShowEnemySub;
    }
}
