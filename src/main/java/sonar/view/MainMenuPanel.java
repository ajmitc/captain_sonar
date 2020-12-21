package sonar.view;

import sonar.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuPanel extends JPanel {
    private Model model;
    private View view;

    private JButton btnNewGame;

    public MainMenuPanel(Model model, View view){
        super();
        this.model = model;
        this.view = view;

        btnNewGame = new JButton("New Game");

        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JPanel boxPanel = new JPanel();
        boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.PAGE_AXIS));
        boxPanel.add(btnNewGame);
        boxPanel.add(btnExit);

        JPanel rightpanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rightpanel.add(boxPanel);

        Image cover = ImageUtil.get("cover.jpg");

        JLabel lblCover = new JLabel(new ImageIcon(cover));

        setLayout(new GridLayout(1, 2));
        add(lblCover);
        add(rightpanel);
    }

    public JButton getBtnNewGame() {
        return btnNewGame;
    }
}
