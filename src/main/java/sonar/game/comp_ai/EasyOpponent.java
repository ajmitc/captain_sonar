package sonar.game.comp_ai;

import sonar.Model;
import sonar.game.*;
import sonar.game.System;
import sonar.util.Util;
import sonar.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EasyOpponent extends OpponentAI{
    private Direction lastDirection;

    // If < 0 than opponent NOT in this sector, if > 0, opponent IS in this sector, 0 == unknown
    private int droneSectorResponse;

    public EasyOpponent(Model model, View view, Submarine submarine){
        super(model, view, submarine);
        logger = Logger.getLogger(EasyOpponent.class.getName());
    }

    @Override
    public List<CaptainCommand> getCaptainCommands() {
        List<CaptainCommand> commands = new ArrayList<>();

        // Attempt to activate a system
        List<System> chargedSystems = submarine.getSystems().getChargedSystems();
        if (!chargedSystems.isEmpty()){
            System system = chargedSystems.get(Util.getRandomInt(0, chargedSystems.size()));
            CaptainCommand activateCommand = CaptainCommand.getActivateCommand(system);
            if (activateCommand != null)
                commands.add(activateCommand);
        }

        // Determine valid directions
        List<Direction> options = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            MapNode node = model.getGame().getGamemap().getInDir(dir, submarine.getCurrentLocation().getPoint());
            if (node != null && !node.getVisitedBy().contains(submarine) && !node.isIsland())
                options.add(dir);
        }
        if (options.isEmpty()) {
            lastDirection = null;
            return Collections.singletonList(CaptainCommand.getSurfaceCommand());
        }
        Direction choice = options.get(Util.getRandomInt(0, options.size()));
        commands.add(CaptainCommand.getDirectionCommand(choice));
        lastDirection = choice;
        return commands;
    }

    @Override
    public System getSystemToCharge() {
        List<System> options = new ArrayList<>();
        for (System system: submarine.getSystems().getUnchargedSystems()){
            options.add(system);
        }
        if (options.isEmpty())
            return null;
        return options.get(Util.getRandomInt(0, options.size()));
    }

    @Override
    public Engineering.SubComponent getSubComponentToDamage() {
        if (lastDirection != null){
            // Get undamaged components
            List<Engineering.SubComponent> subComponents =
                    submarine.getEngineering().getSubComponents(lastDirection).stream()
                            .filter(sc -> !sc.isDamaged())
                            .collect(Collectors.toList());
            return subComponents.get(Util.getRandomInt(0, subComponents.size()));
        }
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
        List<MapNode> neighbors =
                model.getGame().getGamemap().getNeighbors(submarine.getCurrentLocation().getPoint()).stream()
                        .filter(mn -> !mn.isIsland() && !mn.getVisitedBy().contains(submarine))
                        .collect(Collectors.toList());
        if (neighbors.isEmpty())
            return null;
        return neighbors.get(Util.getRandomInt(0, neighbors.size()));
    }

    /**
     * Get location of mine to trigger
     * @return
     */
    @Override
    public MapNode getTriggerMineLocation() {
        List<MapNode> mineLocations = model.getGame().getGamemap().getNodesWithMines(submarine);
        if (mineLocations.isEmpty())
            return null;
        return mineLocations.get(Util.getRandomInt(0, mineLocations.size()));
    }

    @Override
    public int getDroneSectorQuery() {
        return Util.getRandomInt(1, 10);
    }

    @Override
    public void setDroneSectorQueryResponse(int sectorQuery, boolean response) {
        // AI asked if player in sectorQuery, if response == true: yes, otherwise, no
        droneSectorResponse = sectorQuery * (response? 1: -1);
    }

    @Override
    public MapNode getTorpedoTargetLocation() {
        List<MapNode> targetNodes = model.getGame().getGamemap().getNodesWithinTorpedoRange(submarine.getCurrentLocation());
        while (true){
            MapNode target = targetNodes.get(Util.getRandomInt(0, targetNodes.size()));
            if (model.getGame().getGamemap().getOrthogonalDistanceBetween(submarine.getCurrentLocation(), target) < 2)
                continue;
            return target;
        }
    }

    @Override
    public SonarReport getSonarReport() {
        SonarReport sonarReport = new SonarReport();

        // True values
        Integer column = model.getGame().getEnemySub().getCurrentLocation().getX();
        Integer row    = model.getGame().getEnemySub().getCurrentLocation().getY();
        Integer sector = model.getGame().getGamemap().getSector(model.getGame().getEnemySub().getCurrentLocation().getPoint());

        // First choice - True
        List<String> choices = new ArrayList<>();
        choices.add("row");
        choices.add("column");
        choices.add("sector");
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
