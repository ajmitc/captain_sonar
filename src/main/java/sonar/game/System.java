package sonar.game;

public enum System {
    MINE("Drop Mine"),
	TORPEDO("Torpedo"),
	SONAR("Sonar"),
	SILENCE("Silence"),
	DRONE("Drone"),
	SCENARIO("Scenario");

    private String name;
    System(String name){
    	this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
