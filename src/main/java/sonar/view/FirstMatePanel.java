package sonar.view;

import sonar.Model;
import sonar.game.System;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstMatePanel extends JPanel {
    private static final int SYSTEM_RECT_WIDTH  = 53;
    private static final int SYSTEM_RECT_HEIGHT = 44;
    private static final int DAMAGE_RECT_WIDTH  = 35;
    private static final int DAMAGE_RECT_HEIGHT = 20;

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

        systemRectangleMap.put(System.MINE, new Rectangle(103, 53, SYSTEM_RECT_WIDTH, SYSTEM_RECT_HEIGHT));
        systemRectangleMap.put(System.SCENARIO, new Rectangle(492, 331, SYSTEM_RECT_WIDTH, SYSTEM_RECT_HEIGHT));
        systemRectangleMap.put(System.SILENCE, new Rectangle(487, 153, SYSTEM_RECT_WIDTH, SYSTEM_RECT_HEIGHT));
        systemRectangleMap.put(System.DRONE, new Rectangle(296, 155, SYSTEM_RECT_WIDTH, SYSTEM_RECT_HEIGHT));
        systemRectangleMap.put(System.SONAR, new Rectangle(295, 332, SYSTEM_RECT_WIDTH, SYSTEM_RECT_HEIGHT));
        systemRectangleMap.put(System.TORPEDO, new Rectangle(104, 330, SYSTEM_RECT_WIDTH, SYSTEM_RECT_HEIGHT));

        damageRectangles.add(new Rectangle(448, 67, DAMAGE_RECT_WIDTH, DAMAGE_RECT_HEIGHT));
        damageRectangles.add(new Rectangle(486, 67, DAMAGE_RECT_WIDTH, DAMAGE_RECT_HEIGHT));
        damageRectangles.add(new Rectangle(525, 67, DAMAGE_RECT_WIDTH, DAMAGE_RECT_HEIGHT));
        damageRectangles.add(new Rectangle(566, 67, DAMAGE_RECT_WIDTH, DAMAGE_RECT_HEIGHT));

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

        g.drawImage(firstMateImage, 0, 0, null);

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
