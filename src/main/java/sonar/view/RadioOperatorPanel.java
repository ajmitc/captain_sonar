package sonar.view;

import sonar.Model;
import sonar.game.Direction;

import javax.swing.*;
import java.awt.*;

public class RadioOperatorPanel extends JPanel {
    private static final Stroke LINE_STROKE = new BasicStroke(2.f);
    private Model model;
    private View view;

    private int startX = 100, startY = 100;
    private int mouseDownX, mouseDownY;

    public RadioOperatorPanel(Model model, View view){
        super();
        this.model = model;
        this.view = view;
    }

    @Override
    public void paintComponent(Graphics graphics){
        Graphics2D g = (Graphics2D) graphics;

        g.drawImage(model.getGame().getGamemap().getMapImage(), 0, 0, null);

        g.setColor(Color.RED);
        g.setStroke(LINE_STROKE);
        g.fillOval(startX - 5, startY - 5, 10, 10);

        int lastX = startX, lastY = startY;
        for (Direction direction: model.getGame().getPlayerSub().getEnemySubMovements()){
            int x = lastX;
            int y = lastY;
            switch (direction) {
                case NORTH -> y = lastY - CaptainPanel.NODE_DISTANCE;
                case SOUTH -> y = lastY + CaptainPanel.NODE_DISTANCE;
                case WEST  -> x = lastX - CaptainPanel.NODE_DISTANCE;
                case EAST  -> x = lastX + CaptainPanel.NODE_DISTANCE;
            }
            g.drawLine(lastX, lastY, x, y);
            lastX = x;
            lastY = y;
        }
    }

    public void setDragLocation(int mx, int my){
        startX = mx;
        startY = my;
        refresh();
    }

    public void refresh(){
        repaint();
    }
}
