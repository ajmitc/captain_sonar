package sonar;

import sonar.game.Game;

public class Model {
    private Game game;

    public Model(){

    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
