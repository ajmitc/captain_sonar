package sonar;

import sonar.view.View;

public class Main {
    public static void main(String ... args){
        Model model = new Model();
        View view = new View(model);
        new Controller(model, view);

        view.getFrame().setVisible(true);
    }
}
