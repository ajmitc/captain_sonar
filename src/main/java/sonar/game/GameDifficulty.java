package sonar.game;

public enum GameDifficulty {
    EASY("Easy"),
	NORMAL("Normal"),
	HARD("Hard");

	private String name;
	GameDifficulty(String name){
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
