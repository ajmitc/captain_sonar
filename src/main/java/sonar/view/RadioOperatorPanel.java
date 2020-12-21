package sonar.view;

import sonar.Model;

import javax.swing.*;
import java.awt.*;

public class RadioOperatorPanel extends JPanel {
    private Model model;
    private View view;

    public RadioOperatorPanel(Model model, View view){
        super();
        this.model = model;
        this.view = view;
    }

    @Override
    public void paintComponent(Graphics graphics){
        Graphics2D g = (Graphics2D) graphics;

        g.drawImage(model.getGame().getGamemap().getMapImage(), 0, 0, null);
    }

    public void refresh(){
        repaint();
    }
}
