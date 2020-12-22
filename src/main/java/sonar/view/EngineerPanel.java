package sonar.view;

import sonar.Model;
import sonar.game.Engineering;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class EngineerPanel extends JPanel {
    private static final int SUB_COMP_WIDTH  = 30;
    private static final int SUB_COMP_HEIGHT = 30;
    private static final Stroke DAMAGE_STROKE = new BasicStroke(2.f);

    private Model model;
    private View view;
    private Image engineerImage;

    private Map<Integer, Rectangle> subComponentRectangleMap = new HashMap<>();

    private int mx, my;

    public EngineerPanel(Model model, View view){
        super();
        this.model = model;
        this.view = view;

        engineerImage = ImageUtil.get("engineer.jpg", 650);

        // WEST
        subComponentRectangleMap.put(1, new Rectangle(85, 271, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(2, new Rectangle(157, 271, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(3, new Rectangle(155, 318, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(4, new Rectangle(84, 369, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(5, new Rectangle(120, 369, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(6, new Rectangle(157, 369, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));

        // NORTH
        subComponentRectangleMap.put(7, new Rectangle(211, 271, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(8, new Rectangle(281, 318, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(9, new Rectangle(211, 318, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(10, new Rectangle(211, 369, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(11, new Rectangle(248, 369, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(12, new Rectangle(284, 369, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));

        // SOUTH
        subComponentRectangleMap.put(13, new Rectangle(340, 271, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(14, new Rectangle(340, 318, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(15, new Rectangle(411, 318, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(16, new Rectangle(340, 369, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(17, new Rectangle(380, 369, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(18, new Rectangle(417, 369, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));

        // EAST
        subComponentRectangleMap.put(19, new Rectangle(470, 271, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(20, new Rectangle(470, 318, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(21, new Rectangle(544, 318, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(22, new Rectangle(470, 369, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(23, new Rectangle(508, 369, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));
        subComponentRectangleMap.put(24, new Rectangle(544, 369, SUB_COMP_WIDTH, SUB_COMP_HEIGHT));

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

        g.drawImage(engineerImage, 0, 0, null);

        Stroke oldStroke = g.getStroke();
        g.setColor(Color.RED);
        g.setStroke(DAMAGE_STROKE);
        for (int id = 1; id <= 24; ++id){
            if (model.getGame().getPlayerSub().getEngineering().getSubComponent(id).isDamaged()) {
                Rectangle r = subComponentRectangleMap.get(id);
                g.drawLine(r.x, r.y, r.x + r.width, r.y + r.height);
                g.drawLine(r.x + r.width, r.y, r.x, r.y + r.height);
            }
        }
        g.setStroke(oldStroke);

        g.setColor(Color.WHITE);
        g.drawString(mx + ", " + my, 50, 50);
    }

    public Engineering.SubComponent getClickedSubComponent(int mx, int my){
        for (Integer subComponentId: subComponentRectangleMap.keySet()){
            if (subComponentRectangleMap.get(subComponentId).contains(mx, my))
                return model.getGame().getPlayerSub().getEngineering().getSubComponent(subComponentId);
        }
        return null;
    }

    public void refresh(){
        repaint();
    }
}
