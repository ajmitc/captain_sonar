package sonar.game.comp_ai;

import sonar.Model;
import sonar.game.CaptainCommand;
import sonar.game.Direction;
import sonar.game.MapNode;
import sonar.game.Submarine;
import sonar.util.Util;
import sonar.view.View;

import java.util.ArrayList;
import java.util.List;

public class NormalOpponent extends EasyOpponent{

    public NormalOpponent(Model model, View view, Submarine submarine){
        super(model, view, submarine);
    }

    @Override
    public CaptainCommand getCaptainCommand() {
        // Determine valid directions
        List<Direction> options = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            MapNode node = model.getGame().getGamemap().getInDir(dir, submarine.getCurrentLocation().getPoint());
            if (node != null && !node.getVisitedBy().contains(submarine) && !node.isIsland())
                options.add(dir);
            if (options.isEmpty())
                return CaptainCommand.getSurfaceCommand();
        }
        // TODO Choose best direction based on possible player locations
		List<Integer> quadrantLocations = getMostProbableQuadrantPlayerLocation();
		return CaptainCommand.getDirectionCommand(getDirectionTowardPlayerLocations(quadrantLocations));
    }

    @Override
    public System getSystemToCharge() {
        return null;
    }

    @Override
    public System getSystemToActivate() {
        return null;
    }

    @Override
    public List<CaptainCommand> getSilenceMovements() {
        logger.info("AI is activating silence!");
        // AI may move up to 4 spaces
        int numMovements = Util.getRandomInt(0, 5);
        List<CaptainCommand> commands = new ArrayList<>();
        for (int i = 0; i < numMovements; ++i) {

        }
        return commands;
    }
}
