package sonar.game;

public class CaptainCommand {
    private Direction direction;
    private boolean dropMine;
    private boolean triggerMine;
    private boolean surface;
    private boolean silence;
    private boolean drone;
    private boolean torpedo;
    private boolean sonar;
    private boolean custom;

    public CaptainCommand(){
        this(null);
    }

    public CaptainCommand(Direction direction){
        this.direction = direction;
    }

    public static CaptainCommand getDirectionCommand(Direction direction){
        CaptainCommand command = new CaptainCommand(direction);
        return command;
    }

    public static CaptainCommand getDropMineCommand(){
        CaptainCommand command = new CaptainCommand();
        command.dropMine = true;
        return command;
    }

    public static CaptainCommand getTriggerMineCommand(){
        CaptainCommand command = new CaptainCommand();
        command.triggerMine = true;
        return command;
    }

    public static CaptainCommand getSurfaceCommand(){
        CaptainCommand command = new CaptainCommand();
        command.surface = true;
        return command;
    }

    public static CaptainCommand getSilenceCommand(){
        CaptainCommand command = new CaptainCommand();
        command.silence = true;
        return command;
    }

    public static CaptainCommand getLaunchDroneCommand(){
        CaptainCommand command = new CaptainCommand();
        command.drone = true;
        return command;
    }

    public static CaptainCommand getLaunchTorpedoCommand(){
        CaptainCommand command = new CaptainCommand();
        command.torpedo = true;
        return command;
    }

    public static CaptainCommand getActivateSonarCommand(){
        CaptainCommand command = new CaptainCommand();
        command.sonar = true;
        return command;
    }

    public static CaptainCommand getCustomScenarioCommand(){
        CaptainCommand command = new CaptainCommand();
        command.custom = true;
        return command;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isDropMine() {
        return dropMine;
    }

    public boolean isTriggerMine() {
        return triggerMine;
    }

    public boolean isSurface() {
        return surface;
    }

    public boolean isSilence() {
        return silence;
    }

    public boolean isDrone() {
        return drone;
    }

    public void setDrone(boolean drone) {
        this.drone = drone;
    }

    public boolean isSonar() {
        return sonar;
    }

    public boolean isTorpedo() {
        return torpedo;
    }

    public boolean isCustom() {
        return custom;
    }
}
