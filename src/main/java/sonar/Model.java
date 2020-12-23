package sonar;

import sonar.game.Game;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class Model {
    private static Logger logger = Logger.getLogger(Model.class.getName());
    private static final List<String> BOOLEAN_VALUES = Arrays.asList("true", "t", "1", "yes");

    private Properties properties;
    private Game game;

    public Model(){
        try {
            properties = new Properties();
            properties.load(Model.class.getClassLoader().getResourceAsStream("application.properties"));
        }
        catch (Exception e){
            logger.severe("Unable to load application.properties: " + e);
        }
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getProperty(String key, String def){
        if (properties.containsKey(key))
            return "" + properties.get(key);
        return def;
    }

    public boolean getProperty(String key, boolean def){
        if (properties.containsKey(key))
            return BOOLEAN_VALUES.contains(getProperty(key, "").toLowerCase());
        return def;
    }
}
