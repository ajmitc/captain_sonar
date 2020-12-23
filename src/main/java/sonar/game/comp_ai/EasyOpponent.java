package sonar.game.comp_ai;

import sonar.Model;
import sonar.game.CaptainCommand;
import sonar.game.Direction;
import sonar.game.MapNode;
import sonar.game.Submarine;
import sonar.util.Util;
import sonar.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class EasyOpponent extends OpponentAI{

    public EasyOpponent(Model model, View view, Submarine submarine){
        super(model, view, submarine);
        logger = Logger.getLogger(EasyOpponent.class.getName());
    }

    @Override
    public List<CaptainCommand> getCaptainCommands() {
        List<CaptainCommand> commands = new ArrayList<>();
        // Determine valid directions
        List<Direction> options = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            MapNode node = model.getGame().getGamemap().getInDir(dir, submarine.getCurrentLocation().getPoint());
            if (node != null && !node.getVisitedBy().contains(submarine) && !node.isIsland())
                options.add(dir);
        }
        if (options.isEmpty())
            return Collections.singletonList(CaptainCommand.getSurfaceCommand());
        Direction choice = options.get(Util.getRandomInt(0, options.size()));
        commands.add(CaptainCommand.getDirectionCommand(choice));
        return commands;
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

    @Override
    public List<CaptainCommand> getPlayerSurfacedMovements() {
        logger.info("AI is moving three times!");
        // AI may move up to 3 spaces, unless shortened by their own surfacing
        List<CaptainCommand> commands = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {

        }
        return commands;
    }

    @Override
    public MapNode getDropMineLocation() {
        MapNode current = submarine.getCurrentLocation();
        List<MapNode> neighbors = model.getGame().getGamemap().getNeighbors(current.getPoint());
        for (MapNode neighbor: neighbors){
            if (neighbor.getVisitedBy().contains(submarine) || neighbor.isIsland())
                continue;
            return neighbor;
        }
        return null;
    }

    /**
     * Get location of mine to trigger
     * @return
     */
    @Override
    public MapNode getTriggerMineLocation() {
        return null;
    }

    @Override
    public int getDroneSectorQuery() {
        return Util.getRandomInt(1, 10);
    }

    @Override
    public void setDroneSectorQueryResponse(int sectorQuery, boolean response) {
        // AI asked if player in sectorQuery, if response == true: yes, otherwise, no
    }

    @Override
    public MapNode getTorpedoTargetLocation() {
        return null;
    }

    @Override
    public SonarReport getSonarReport() {
        SonarReport sonarReport = new SonarReport();

        // True values
        Integer column = model.getGame().getEnemySub().getCurrentLocation().getX();
        Integer row    = model.getGame().getEnemySub().getCurrentLocation().getY();
        Integer sector = model.getGame().getGamemap().getSector(model.getGame().getEnemySub().getCurrentLocation().getPoint());

        // First choice - True
        List<String> choices = Arrays.asList("row", "column", "sector");
        String choice = choices.get(Util.getRandomInt(0, 3));
        choices.remove(choice);
        if (choice.equals("row"))
            sonarReport.setRow(row);
        else if (choice.equals("column"))
            sonarReport.setColumn(column);
        else if (choice.equals("sector"))
            sonarReport.setSector(sector);

        // Second choice - False
        choice = choices.get(Util.getRandomInt(0, 2));
        if (choice.equals("row")) {
            int falseRow = row;
            while (falseRow == row)
                falseRow = Util.getRandomInt(0, 15);
            sonarReport.setRow(falseRow);
        }
        else if (choice.equals("column")) {
            int falseCol = column;
            while (falseCol == column)
                falseCol = Util.getRandomInt(0, 15);
            sonarReport.setColumn(falseCol);
        }
        else if (choice.equals("sector")) {
            int falseSector = sector;
            while (falseSector == sector)
                falseSector = Util.getRandomInt(1, 10);
            sonarReport.setSector(falseSector);
        }

        return sonarReport;
    }
}
