package sonar.view;

import sonar.Model;
import sonar.game.MapNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CaptainPanel extends JPanel {
    public static final int XOFFSET = 63;
    public static final int YOFFSET = 85;
    public static final int NODE_DISTANCE = 26;
    private static final Stroke PATH_STROKE = new BasicStroke(2.f);

    private static final Point CAPTAINS_LOG_START = new Point(475, 153);
    private static final int MAX_CAPTAINS_LOG_SIZE = 15;
    private static final int CAPTAINS_LOG_YOFFSET = 15;
    private static final Font CAPTAINS_LOG_FONT = new Font("Serif", Font.BOLD, 12);

    private Model model;
    private View view;
    private List<String> captainsLog = new ArrayList<>();

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
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;

        //g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.drawImage(model.getGame().getGamemap().getMapImage(), 0, 0, null);

        g.setStroke(PATH_STROKE);
        model.getGame().getGamemap().getNodeList().forEach(n -> drawOnMap(g, n));

        MapNode currentLocation = model.getGame().getPlayerSub().getCurrentLocation();
        if (currentLocation != null) {
            Point p = getScreenLocation(currentLocation);
            drawSubOnMap(g, p.x, p.y);
        }

        drawCaptainsLog(g);

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

    private void drawCaptainsLog(Graphics2D g){
        Font oldFont = g.getFont();
        g.setFont(CAPTAINS_LOG_FONT);
        g.setColor(Color.BLACK);
        for (int i = 0; i < captainsLog.size(); ++i){
            int x = CAPTAINS_LOG_START.x;
            int y = CAPTAINS_LOG_START.y + (CAPTAINS_LOG_YOFFSET * i);
            g.drawString(captainsLog.get(i), x, y);
        }
        g.setFont(oldFont);
    }

    private void drawSubOnMap(Graphics2D g, int x, int y){
        g.setColor(Color.WHITE);
        g.fillOval(x - 10, y - 10, 20, 20);
        g.setColor(Color.BLACK);
        g.drawString("S", x - 5, y + 5);
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

    public void addCaptainsLog(String text){
        captainsLog.add(text);
        if (captainsLog.size() > MAX_CAPTAINS_LOG_SIZE){
            captainsLog.remove(0);
        }
        refresh();
    }

    public void refresh(){
        repaint();
    }
}
