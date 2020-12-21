package sonar.game;

public class Game {
    private Mode mode;

    private GamePhase gamePhase;
    private GamePhaseStep gamePhaseStep;

    private GameDifficulty difficulty;
    private Submarine playerSub;
    private Submarine enemySub;
    private GameMap gamemap = new GameMap();

    public Game(){
        mode = Mode.TURNS;
        gamePhase = GamePhase.SETUP;
        gamePhaseStep = GamePhaseStep.START_PHASE;
    }

    public GameDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(GameDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public GameMap getGamemap() {
        return gamemap;
    }

    public void setGamemap(GameMap gamemap) {
        this.gamemap = gamemap;
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public GamePhaseStep getGamePhaseStep() {
        return gamePhaseStep;
    }

    public void setGamePhase(GamePhase gamePhase) {
        this.gamePhase = gamePhase;
        this.gamePhaseStep = GamePhaseStep.START_PHASE;
    }

    public void setGamePhaseStep(GamePhaseStep gamePhaseStep) {
        this.gamePhaseStep = gamePhaseStep;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Submarine getPlayerSub() {
        return playerSub;
    }

    public void setPlayerSub(Submarine playerSub) {
        this.playerSub = playerSub;
    }

    public Submarine getEnemySub() {
        return enemySub;
    }

    public void setEnemySub(Submarine enemySub) {
        this.enemySub = enemySub;
    }
}
