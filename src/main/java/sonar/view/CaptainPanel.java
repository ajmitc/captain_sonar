package sonar.view;

import sonar.Model;
import sonar.game.MapNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CaptainPanel extends JPanel {
    private static final int XOFFSET = 63;
    private static final int YOFFSET = 85;
    private static final int NODE_DISTANCE = 26;

    private Model model;
    private View view;

    private int mx, my;

    public CaptainPanel(Model model, View view){
        super();
        this.model = model;
        this.view = view;

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                mx = e.getX();
                my = e.getY();
                repaint();
            }
        });
    }

    @Override
    public void paintComponent(Graphics graphics){
        Graphics2D g = (Graphics2D) graphics;

        g.drawImage(model.getGame().getGamemap().getMapImage(), 0, 0, null);

        MapNode currentLocation = model.getGame().getPlayerSub().getCurrentLocation();
        Point p = getScreenLocation(currentLocation);
        drawOnMap(g, "C", p.x, p.y);

        model.getGame().getGamemap().getNodeList().forEach(n -> drawOnMap(g, n));

        g.setColor(Color.WHITE);
        g.drawString(mx + ", " + my, 50, 50);
    }

    private void drawOnMap(Graphics2D g, MapNode mapNode){
        if (mapNode.getMines().contains(model.getGame().getPlayerSub())){
            Point p = getScreenLocation(mapNode);
            drawOnMap(g, "M", p.x, p.y);
        }
        else if (mapNode.getVisitedBy().contains(model.getGame().getPlayerSub())){
            MapNode visitedFrom = mapNode.getVisitedFrom().get(model.getGame().getPlayerSub());
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
        Point p = new Point(XOFFSET + (mapNode.getX() * NODE_DISTANCE), YOFFSET + (mapNode.getY() * NODE_DISTANCE));
        return p;
    }

    public MapNode getMapNodeAt(int mx, int my){
        int x = (mx - XOFFSET) / NODE_DISTANCE;
        int y = (my - YOFFSET) / NODE_DISTANCE;
        return model.getGame().getGamemap().get(x, y);
    }

    public void refresh(){
        repaint();
    }
}
