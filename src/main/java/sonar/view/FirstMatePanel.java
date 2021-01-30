package sonar.view;

import sonar.Model;
import sonar.game.System;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class FirstMatePanel extends JPanel {
    private static final int SYSTEM_RECT_WIDTH  = 53;
    private static final int SYSTEM_RECT_HEIGHT = 44;
    private static final int DAMAGE_RECT_WIDTH  = 35;
    private static final int DAMAGE_RECT_HEIGHT = 20;

    private static final int CHARGE_SHAPE_SIZE = 10;
    private static final Map<System, List<Point>> SYSTEM_CHARGE_COORDS = new HashMap<>();

    static {
        Arrays.stream(System.values()).forEach(s -> SYSTEM_CHARGE_COORDS.put(s, new ArrayList<>()));
        SYSTEM_CHARGE_COORDS.get(System.MINE).add(new Point(147, 129));
        SYSTEM_CHARGE_COORDS.get(System.MINE).add(new Point(175, 156));
        SYSTEM_CHARGE_COORDS.get(System.MINE).add(new Point(172, 193));

        SYSTEM_CHARGE_COORDS.get(System.DRONE).add(new Point(340, 132));
        SYSTEM_CHARGE_COORDS.get(System.DRONE).add(new Point(367, 156));
        SYSTEM_CHARGE_COORDS.get(System.DRONE).add(new Point(365, 192));
        SYSTEM_CHARGE_COORDS.get(System.DRONE).add(new Point(340, 218));

        SYSTEM_CHARGE_COORDS.get(System.SILENCE).add(new Point(530, 130));
        SYSTEM_CHARGE_COORDS.get(System.SILENCE).add(new Point(558, 155));
        SYSTEM_CHARGE_COORDS.get(System.SILENCE).add(new Point(555, 193));
        SYSTEM_CHARGE_COORDS.get(System.SILENCE).add(new Point(533, 217));
        SYSTEM_CHARGE_COORDS.get(System.SILENCE).add(new Point(493, 217));
        SYSTEM_CHARGE_COORDS.get(System.SILENCE).add(new Point(470, 193));

        SYSTEM_CHARGE_COORDS.get(System.TORPEDO).add(new Point(148, 310));
        SYSTEM_CHARGE_COORDS.get(System.TORPEDO).add(new Point(173, 333));
        SYSTEM_CHARGE_COORDS.get(System.TORPEDO).add(new Point(173, 371));

        SYSTEM_CHARGE_COORDS.get(System.SONAR).add(new Point(340, 310));
        SYSTEM_CHARGE_COORDS.get(System.SONAR).add(new Point(366, 332));
        SYSTEM_CHARGE_COORDS.get(System.SONAR).add(new Point(366, 370));

        SYSTEM_CHARGE_COORDS.get(System.SCENARIO).add(new Point(534, 310));
        SYSTEM_CHARGE_COORDS.get(System.SCENARIO).add(new Point(560, 332));
        SYSTEM_CHARGE_COORDS.get(System.SCENARIO).add(new Point(560, 370));
        SYSTEM_CHARGE_COORDS.get(System.SCENARIO).add(new Point(534, 394));
        SYSTEM_CHARGE_COORDS.get(System.SCENARIO).add(new Point(500, 394));
        SYSTEM_CHARGE_COORDS.get(System.SCENARIO).add(new Point(475, 370));
    }

    private Model model;
    private View view;
    private Image firstMateImage;

    private Map<System, Rectangle> systemRectangleMap = new HashMap<>();
    private List<Rectangle> damageRectangles = new ArrayList<>();

    private int mx, my;

    public FirstMatePanel(Model model, View view){
        super();
        this.model = model;
        this.view = view;

        firstMateImage = ImageUtil.get("first_mate.jpg", 650);

        systemRectangleMap.put(System.MINE, new Rectangle(103, 153, SYSTEM_RECT_WIDTH, SYSTEM_RECT_HEIGHT));
        systemRectangleMap.put(System.SCENARIO, new Rectangle(492, 331, SYSTEM_RECT_WIDTH, SYSTEM_RECT_HEIGHT));
        systemRectangleMap.put(System.SILENCE, new Rectangle(487, 153, SYSTEM_RECT_WIDTH, SYSTEM_RECT_HEIGHT));
        systemRectangleMap.put(System.DRONE, new Rectangle(296, 155, SYSTEM_RECT_WIDTH, SYSTEM_RECT_HEIGHT));
        systemRectangleMap.put(System.SONAR, new Rectangle(295, 332, SYSTEM_RECT_WIDTH, SYSTEM_RECT_HEIGHT));
        systemRectangleMap.put(System.TORPEDO, new Rectangle(104, 330, SYSTEM_RECT_WIDTH, SYSTEM_RECT_HEIGHT));

        damageRectangles.add(new Rectangle(448, 65, DAMAGE_RECT_WIDTH, DAMAGE_RECT_HEIGHT));
        damageRectangles.add(new Rectangle(486, 65, DAMAGE_RECT_WIDTH, DAMAGE_RECT_HEIGHT));
        damageRectangles.add(new Rectangle(525, 65, DAMAGE_RECT_WIDTH, DAMAGE_RECT_HEIGHT));
        damageRectangles.add(new Rectangle(566, 65, DAMAGE_RECT_WIDTH, DAMAGE_RECT_HEIGHT));

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

        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(firstMateImage, 0, 0, null);

        g.setColor(Color.BLACK);
        for (System system: System.values()){
            g.draw(systemRectangleMap.get(system));
        }

        g.setColor(Color.RED);
        for (int i = 0; i < model.getGame().getPlayerSub().getSubDamage(); ++i){
            g.fill(damageRectangles.get(i));
        }

        for (System system: System.values()){
            int level = model.getGame().getPlayerSub().getSystems().getChargeLevel(system);
            int maxLevel = model.getGame().getPlayerSub().getSystems().getMaxChargeLevel(system);
            for (int i = 1; i <= level; ++i){
                Point p = SYSTEM_CHARGE_COORDS.get(system).get(i - 1);
                g.setColor(i == maxLevel? Color.GREEN: Color.ORANGE);
                g.fillOval(p.x - (CHARGE_SHAPE_SIZE / 2), p.y - (CHARGE_SHAPE_SIZE / 2), CHARGE_SHAPE_SIZE, CHARGE_SHAPE_SIZE);
                g.setColor(Color.BLACK);
                g.drawOval(p.x - (CHARGE_SHAPE_SIZE / 2), p.y - (CHARGE_SHAPE_SIZE / 2), CHARGE_SHAPE_SIZE, CHARGE_SHAPE_SIZE);
            }
        }

        g.setColor(Color.WHITE);
        g.drawString(mx + ", " + my, 50, 50);
    }

    public System getClickedSystem(int mx, int my){
        for (System system: systemRectangleMap.keySet()){
            Rectangle r = systemRectangleMap.get(system);
            if (r.contains(mx, my))
                return system;
        }
        return null;
    }

    public void refresh(){
        repaint();
    }
}
