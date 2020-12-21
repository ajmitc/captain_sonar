package sonar.game;

public enum Direction {
    NORTH("North"),
    SOUTH("South"),
	EAST("East"),
	WEST("West");

    private String name;
    Direction(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Direction getOpposite(){
        if (this == NORTH)
            return SOUTH;
        if (this == SOUTH)
            return NORTH;
        if (this == EAST)
            return WEST;
        if (this == WEST)
            return EAST;
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
