package sonar.view;

import sonar.Model;
import sonar.game.Direction;
import sonar.game.MapNode;

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
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;

        g.fillRect(0, 0, getWidth(), getHeight());
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

        if (model.getProperty("radiooperator.enemy.show", false)){
            model.getGame().getGamemap().getNodeList().forEach(n -> drawOnMap(g, n));
            MapNode currentLocation = model.getGame().getEnemySub().getCurrentLocation();
            if (currentLocation != null) {
                Point p = getScreenLocation(currentLocation);
                drawOnMap(g, "E", p.x, p.y);
            }
        }
    }

    public void setDragLocation(int mx, int my){
        startX = mx;
        startY = my;
        refresh();
    }

    private void drawOnMap(Graphics2D g, MapNode mapNode){
        if (mapNode.getMines().contains(model.getGame().getEnemySub())){
            Point p = getScreenLocation(mapNode);
            drawOnMap(g, "M", p.x, p.y);
        }
        else if (mapNode.getVisitedBy().contains(model.getGame().getEnemySub())){
            MapNode visitedFrom = mapNode.getVisitedFrom().get(model.getGame().getEnemySub());
            if (visitedFrom != null) {
                Point p0 = getScreenLocation(mapNode);
                Point p1 = getScreenLocation(visitedFrom);
                g.setColor(Color.BLACK);
                g.drawLine(p0.x, p0.y, p1.x, p1.y);
            }
        }
    }

    private void drawOnMap(Graphics2D g, String code, int x, int y){
        g.setColor(Color.WHITE);
        g.fillRect(x - 10, y - 10, 20, 20);
        g.setColor(Color.BLACK);
        g.drawString(code, x, y);
    }

    private Point getScreenLocation(MapNode mapNode){
        Point p = new Point(CaptainPanel.XOFFSET + (mapNode.getX() * CaptainPanel.NODE_DISTANCE), CaptainPanel.YOFFSET + (mapNode.getY() * CaptainPanel.NODE_DISTANCE));
        return p;
    }

    public void refresh(){
        repaint();
    }
}
