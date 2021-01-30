package sonar;

import sonar.game.*;
import sonar.game.System;
import sonar.game.comp_ai.*;
import sonar.view.View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Controller {
	private static Logger logger = Logger.getLogger(Controller.class.getName());

    private Model model;
    private View view;

    private OpponentAI opponentAI;

    private List<CaptainCommand> captainCommands = new ArrayList<>();
    private List<MapNode> possiblePlayerLocations = new ArrayList<>();
    private boolean playerMoved = false;
    private boolean playerEndTurn = false;
	private boolean playerChargedSystem = false;
	private boolean playerDamagedComponent = false;
	private Direction playerLastMoveDirection = null;

	public Controller(Model model, View view){
        this.model = model;
        this.view = view;

        this.view.getMainMenuPanel().getBtnNewGame().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setGame(new Game());
                model.getGame().setPlayerSub(new Submarine());
                model.getGame().setEnemySub(new Submarine());
				// Ask user to select difficulty
                String diffStr = (String)
						JOptionPane.showInputDialog(
								view.getFrame(),
								"Select AI Difficulty",
								"AI Difficulty",
								JOptionPane.QUESTION_MESSAGE,
								null,
								new String[]{"Easy", "Normal", "Hard"},
								"Normal");
                if (diffStr.equals("Easy")){
                	model.getGame().setDifficulty(GameDifficulty.EASY);
				}
                else if (diffStr.equals("Normal"))
                	model.getGame().setDifficulty(GameDifficulty.NORMAL);
                else if (diffStr.equals("Hard"))
					model.getGame().setDifficulty(GameDifficulty.HARD);
				view.showGame();
                view.refresh();
                run();
            }
        });

        // MOVE NORTH
        this.view.getGamePanel().getCaptainCommandPanel().getBtnNorth().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CaptainCommand command = CaptainCommand.getDirectionCommand(Direction.NORTH);
				handleCaptainCommand(command, model.getGame().getPlayerSub());
				run();
			}
		});

		this.view.getGamePanel().getCaptainCommandPanel().getBtnSouth().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CaptainCommand command = CaptainCommand.getDirectionCommand(Direction.SOUTH);
				handleCaptainCommand(command, model.getGame().getPlayerSub());
				run();
			}
		});

		this.view.getGamePanel().getCaptainCommandPanel().getBtnWest().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CaptainCommand command = CaptainCommand.getDirectionCommand(Direction.WEST);
				handleCaptainCommand(command, model.getGame().getPlayerSub());
				run();
			}
		});

		this.view.getGamePanel().getCaptainCommandPanel().getBtnEast().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CaptainCommand command = CaptainCommand.getDirectionCommand(Direction.EAST);
				handleCaptainCommand(command, model.getGame().getPlayerSub());
				run();
			}
		});

		this.view.getGamePanel().getCaptainCommandPanel().getBtnSurfaceDive().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CaptainCommand command = CaptainCommand.getSurfaceCommand();
				handleCaptainCommand(command, model.getGame().getPlayerSub());
				run();
			}
		});

		this.view.getGamePanel().getCaptainCommandPanel().getBtnSilence().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CaptainCommand command = CaptainCommand.getSilenceCommand();
				handleCaptainCommand(command, model.getGame().getPlayerSub());
				run();
			}
		});

		this.view.getGamePanel().getCaptainCommandPanel().getBtnUndoMove().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.getGamePanel().getCaptainPanel().addCaptainsLog("UNDO MOVE " + playerLastMoveDirection);
				Submarine playerSub = model.getGame().getPlayerSub();
				MapNode current = playerSub.getCurrentLocation();
				MapNode lastNode = model.getGame().getGamemap().getInDir(playerLastMoveDirection.getOpposite(), current.getPoint());

				// Move sub to new location
				current.getVisitedFrom().remove(playerSub);
				current.getVisitedBy().remove(playerSub);
				playerSub.setCurrentLocation(lastNode);
				playerMoved = false;
				playerLastMoveDirection = null;
				model.getGame().getEnemySub().getEnemySubMovements().remove(model.getGame().getEnemySub().getEnemySubMovements().size() - 1);
				run();
			}
		});

		this.view.getGamePanel().getCaptainCommandPanel().getBtnLaunchDrone().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CaptainCommand command = CaptainCommand.getLaunchDroneCommand();
				handleCaptainCommand(command, model.getGame().getPlayerSub());
				run();
			}
		});

		this.view.getGamePanel().getCaptainCommandPanel().getBtnLaunchTorpedo().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CaptainCommand command = CaptainCommand.getLaunchTorpedoCommand();
				handleCaptainCommand(command, model.getGame().getPlayerSub());
				run();
			}
		});

		this.view.getGamePanel().getCaptainCommandPanel().getBtnActivateSonar().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CaptainCommand command = CaptainCommand.getActivateSonarCommand();
				handleCaptainCommand(command, model.getGame().getPlayerSub());
				run();
			}
		});

		this.view.getGamePanel().getCaptainCommandPanel().getBtnDropMine().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CaptainCommand command = CaptainCommand.getDropMineCommand();
				handleCaptainCommand(command, model.getGame().getPlayerSub());
				run();
			}
		});

		this.view.getGamePanel().getCaptainCommandPanel().getBtnTriggerMine().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CaptainCommand command = CaptainCommand.getTriggerMineCommand();
				handleCaptainCommand(command, model.getGame().getPlayerSub());
				run();
			}
		});

		this.view.getGamePanel().getCaptainCommandPanel().getBtnActivateCustomScenarioSystem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CaptainCommand command = CaptainCommand.getCustomScenarioCommand();
				handleCaptainCommand(command, model.getGame().getPlayerSub());
				run();
			}
		});

		this.view.getGamePanel().getCaptainCommandPanel().getBtnEndTurn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				playerEndTurn = true;
				run();
			}
		});

		this.view.getGamePanel().getCaptainCommandPanel().getCkbShowEnemySub().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.setProperty("radiooperator.enemy.show", view.getGamePanel().getCaptainCommandPanel().getCkbShowEnemySub().isSelected()? "true": "false");
				run();
			}
		});

		this.view.getGamePanel().getCaptainPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (model.getGame().getGamePhaseStep() == GamePhaseStep.SETUP_PLACE_PLAYER_SUB){
					MapNode selected = view.getGamePanel().getCaptainPanel().getMapNodeAt(e.getX(), e.getY());
					if (selected == null)
						return;
					if (selected.isIsland()){
						logger.warning("Cannot place submarine on island");
						JOptionPane.showMessageDialog(view.getFrame(), "Cannot place submarine on an island");
						return;
					}
					model.getGame().getPlayerSub().setCurrentLocation(selected);
					selected.getVisitedBy().add(model.getGame().getPlayerSub());
				}
				else if (model.getGame().getGamePhaseStep() == GamePhaseStep.PLAYER_TURN_DROP_MINE){
					MapNode selected = view.getGamePanel().getCaptainPanel().getMapNodeAt(e.getX(), e.getY());
					if (selected == null)
					    return;
					if (selected.isIsland()){
						logger.warning("Cannot place mine on island");
						JOptionPane.showMessageDialog(view.getFrame(), "Cannot place mine on an island");
						return;
					}
					if (selected.getVisitedBy().contains(model.getGame().getPlayerSub())){
						logger.warning("Cannot place mine on path");
						JOptionPane.showMessageDialog(view.getFrame(), "Cannot place mine on your path");
						return;
					}
					selected.getMines().add(model.getGame().getPlayerSub());
					model.getGame().getPlayerSub().getSystems().reset(System.MINE);
					model.getGame().setGamePhaseStep(GamePhaseStep.PLAYER_TURN_COMMANDS);
				}
				else if (model.getGame().getGamePhaseStep() == GamePhaseStep.PLAYER_TURN_FIRE_TORPEDO){
					MapNode selected = view.getGamePanel().getCaptainPanel().getMapNodeAt(e.getX(), e.getY());
					if (selected == null)
						return;
					JOptionPane.showMessageDialog(view.getFrame(), "Torpedo targeting " + selected);
					if (selected.isIsland()){
						logger.warning("Cannot target an island");
						JOptionPane.showMessageDialog(view.getFrame(), "Cannot target an island");
						return;
					}
					// must be within 4 orthogonal spaces
					if (model.getGame().getGamemap().getOrthogonalDistanceBetween(selected, model.getGame().getPlayerSub().getCurrentLocation()) > 4){
						logger.warning("Cannot target location more than 4 orthogonal spaces away");
						JOptionPane.showMessageDialog(view.getFrame(), "Cannot target location more than 4 orthogonal spaces away");
						return;
					}
					view.getGamePanel().getCaptainPanel().addCaptainsLog("FIRE TORPEDO AT " + selected);
					handleTorpedoImpact(selected, model.getGame().getPlayerSub());
					model.getGame().getPlayerSub().getSystems().reset(System.TORPEDO);
					model.getGame().setGamePhaseStep(GamePhaseStep.PLAYER_TURN_COMMANDS);
				}
				else if (model.getGame().getGamePhaseStep() == GamePhaseStep.PLAYER_TURN_COMMANDS){
					MapNode selected = view.getGamePanel().getCaptainPanel().getMapNodeAt(e.getX(), e.getY());
					if (selected.getMines().contains(model.getGame().getPlayerSub())){
						// Trigger mine
						view.getGamePanel().getCaptainPanel().addCaptainsLog("TRIGGER MINE AT " + selected);
						handleTriggerMine(selected, model.getGame().getPlayerSub());
					}
					else {
						Map<Direction, MapNode> neighbors = model.getGame().getGamemap().getNeighborMap(model.getGame().getPlayerSub().getCurrentLocation().getPoint());
						for (Direction direction : neighbors.keySet()) {
							MapNode neighbor = neighbors.get(direction);
							if (neighbor == selected) {
								handleCaptainCommandDirection(CaptainCommand.getDirectionCommand(direction), model.getGame().getPlayerSub());
								break;
							}
						}
					}
				}
				run();
			}
		});

		this.view.getGamePanel().getEngineerPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (model.getGame().getGamePhaseStep() == GamePhaseStep.PLAYER_TURN_COMMANDS){
					if (!playerMoved){
						JOptionPane.showMessageDialog(view.getFrame(), "You must move first");
						return;
					}
					Engineering.SubComponent subComponent = view.getGamePanel().getEngineerPanel().getClickedSubComponent(e.getX(), e.getY());
					if (subComponent != null){
						if (subComponent.isDamaged()){
							logger.warning("Component already damaged, select a different component");
							JOptionPane.showMessageDialog(view.getFrame(), "Component already damaged, choose another");
						}
						else if (playerLastMoveDirection == null){
							logger.warning("You must move before damaging component");
							JOptionPane.showMessageDialog(view.getFrame(), "You must move before damaging a component");
						}
						else if (subComponent.getDirection() != playerLastMoveDirection){
							logger.warning("SubComponent in wrong Control Panel (must match direction moved)");
							JOptionPane.showMessageDialog(view.getFrame(), "You must damage a component in control panel matching direction moved");
						}
						else {
							subComponent.setDamaged(true);
							view.refresh();
							playerDamagedComponent = true;
							view.getGamePanel().getCaptainPanel().addCaptainsLog("DAMAGE COMPONENT " + subComponent);
							handleComponentDamaged(subComponent, model.getGame().getPlayerSub());
						}
					}
                }
				run();
			}
		});

		this.view.getGamePanel().getFirstMatePanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (model.getGame().getGamePhaseStep() == GamePhaseStep.PLAYER_TURN_COMMANDS){
					System clickedSystem = view.getGamePanel().getFirstMatePanel().getClickedSystem(e.getX(), e.getY());
					if (clickedSystem != null){
						if (model.getGame().getPlayerSub().getSystems().isCharged(clickedSystem)){
							// Activate System
							CaptainCommand command = null;
                            switch (clickedSystem){
								case MINE -> command = CaptainCommand.getDropMineCommand();
								case DRONE -> command = CaptainCommand.getLaunchDroneCommand();
								case SONAR -> command = CaptainCommand.getActivateSonarCommand();
								case SILENCE -> command = CaptainCommand.getSilenceCommand();
								case TORPEDO -> command = CaptainCommand.getLaunchTorpedoCommand();
								case SCENARIO -> command = CaptainCommand.getCustomScenarioCommand();
							}
							view.getGamePanel().getCaptainPanel().addCaptainsLog("ACTIVATE " + clickedSystem);
							handleCaptainCommand(command, model.getGame().getPlayerSub());
						}
						else if (!playerChargedSystem){
							view.getGamePanel().getCaptainPanel().addCaptainsLog("CHARGE " + clickedSystem);
							model.getGame().getPlayerSub().getSystems().charge(clickedSystem);
							playerChargedSystem = true;
						}
					}
					run();
				}
			}
		});

		this.view.getGamePanel().getRadioOperatorPanel().addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				super.mouseClicked(e);
				view.getGamePanel().getRadioOperatorPanel().setDragLocation(e.getX(), e.getY());
			}
		});
	}

	public void run() {
		while (model.getGame().getGamePhase() != GamePhase.GAMEOVER) {
			view.refresh();
			switch (model.getGame().getGamePhase()) {
				case SETUP:
				    switch (model.getGame().getGamePhaseStep()) {
						case START_PHASE:
							model.getGame().getGamemap().load("alpha_map.jpg", "alpha_map.txt");

							Submarine playerSub = new Submarine();
							Submarine enemySub = new Submarine();

							model.getGame().setPlayerSub(playerSub);
							model.getGame().setEnemySub(enemySub);

							switch (model.getGame().getDifficulty()){
								case EASY -> opponentAI = new EasyOpponent(model, view, enemySub);
								case NORMAL -> opponentAI = new NormalOpponent(model, view, enemySub);
								case HARD -> opponentAI = new HardOpponent(model, view, enemySub);
							}

							model.getGame().setGamePhaseStep(GamePhaseStep.SETUP_PLACE_PLAYER_SUB);
							break;
						case SETUP_PLACE_PLAYER_SUB: {
							if (model.getGame().getPlayerSub().getCurrentLocation() == null) {
								JOptionPane.showMessageDialog(view.getFrame(), "Place your submarine");
								return;
							}
							model.getGame().setGamePhaseStep(GamePhaseStep.END_PHASE);
							break;
						}
						case END_PHASE: {
							MapNode startLoc = model.getGame().getGamemap().getRandomLocation();
							startLoc.getVisitedBy().add(model.getGame().getEnemySub());
							model.getGame().getEnemySub().setCurrentLocation(startLoc);
							logger.info("Placed AI sub on map at " + startLoc);

							logger.info("Starting game...");
							model.getGame().setGamePhase(GamePhase.PLAYER_TURN);
							break;
						}
					}
					break;
				case PLAYER_TURN:
					switch (model.getGame().getGamePhaseStep()) {
						case START_PHASE:
							logger.info("Starting Player Turn Phase");
							playerMoved = false;
							playerEndTurn = false;
							playerChargedSystem = false;
							playerDamagedComponent = false;
							playerLastMoveDirection = null;
							view.getGamePanel().getCaptainCommandPanel().getBtnEndTurn().setEnabled(false);
							view.getGamePanel().getCaptainCommandPanel().getBtnUndoMove().setEnabled(false);
							if (model.getGame().getPlayerSub().getSurfacedSkipTurns() > 0){
								logger.info("Player Sub Surfaced for " + model.getGame().getPlayerSub().getSurfacedSkipTurns() + " more turns");
								model.getGame().getPlayerSub().decSurfacedSkipTurns();
								model.getGame().setGamePhaseStep(GamePhaseStep.END_PHASE);
							}
							else
								model.getGame().setGamePhaseStep(GamePhaseStep.PLAYER_TURN_COMMANDS);
							break;
						case PLAYER_TURN_COMMANDS:
							if (playerEndTurn) {
								playerEndTurn = false;
								model.getGame().setGamePhaseStep(GamePhaseStep.END_PHASE);
								continue;
							}

							if (playerMoved && !playerDamagedComponent && !playerChargedSystem)
								view.getGamePanel().getCaptainCommandPanel().getBtnUndoMove().setEnabled(true);
							else
								view.getGamePanel().getCaptainCommandPanel().getBtnUndoMove().setEnabled(false);

							if (playerMoved && playerDamagedComponent && playerChargedSystem){
								view.getGamePanel().getCaptainCommandPanel().getBtnEndTurn().setEnabled(true);
							}
							return;
						case PLAYER_TURN_SILENCE:
							// TODO Manage this
							break;
						case PLAYER_TURN_FIRE_TORPEDO:
						case PLAYER_TURN_DROP_MINE:
							return;
						case END_PHASE:
							model.getGame().setGamePhase(GamePhase.AI_TURN);
							break;
					}
					break;
				case AI_TURN:
					switch (model.getGame().getGamePhaseStep()) {
						case START_PHASE:
							logger.info("Starting AI Turn Phase");
							if (model.getGame().getEnemySub().getSurfacedSkipTurns() > 0){
								logger.info("AI Sub Surfaced for " + model.getGame().getEnemySub().getSurfacedSkipTurns() + " more turns");
								model.getGame().getEnemySub().decSurfacedSkipTurns();
								model.getGame().setGamePhaseStep(GamePhaseStep.END_PHASE);
							}
							else {
								model.getGame().setGamePhaseStep(GamePhaseStep.AI_TURN_GET_COMMANDS);
							}
							break;
						case AI_TURN_GET_COMMANDS:
							List<CaptainCommand> commands = opponentAI.getCaptainCommands();
							for (CaptainCommand command: commands){
								handleCaptainCommand(command, opponentAI.getSubmarine());
							}
							model.getGame().setGamePhaseStep(GamePhaseStep.AI_TURN_CHARGE_SYSTEM);
							break;
						case AI_TURN_CHARGE_SYSTEM:
							System system = opponentAI.getSystemToCharge();
							if (system != null)
								opponentAI.getSubmarine().getSystems().charge(system);
							model.getGame().setGamePhaseStep(GamePhaseStep.AI_TURN_DAMAGE_SYSTEM);
							break;
						case AI_TURN_DAMAGE_SYSTEM:
							Engineering.SubComponent subComponent = opponentAI.getSubComponentToDamage();
							if (subComponent != null)
								opponentAI.getSubmarine().getEngineering().damage(subComponent);
							model.getGame().setGamePhaseStep(GamePhaseStep.END_PHASE);
							break;
						case END_PHASE:
							model.getGame().setGamePhase(GamePhase.PLAYER_TURN);
							break;
					}
					break;
				case GAMEOVER:
					return;
			}
			checkGameover();
		}
	}


	private void handleCaptainCommand(CaptainCommand command, Submarine submarine){
		if (command.getDirection() != null)
			handleCaptainCommandDirection(command, submarine);
		else if (command.isSilence()){
			if (submarine == model.getGame().getPlayerSub()){
				model.getGame().setGamePhaseStep(GamePhaseStep.PLAYER_TURN_SILENCE);
				return;
			}
			else {
				List<CaptainCommand> commands = opponentAI.getSilenceMovements();
				for (CaptainCommand captainCommand: commands)
					handleCaptainCommand(captainCommand, submarine);
			}
		}
		else if (command.isSurface()){
			// Captain announces current Sector
			int sector = model.getGame().getGamemap().getSector(submarine.getCurrentLocation().getPoint());

			submarine.setSurfacedSkipTurns(3);

			// Engineer erases all Breakdowns
			repairAllSubComponents(submarine);

			shout("SURFACING IN SECTOR " + sector, submarine);
			if (submarine == model.getGame().getPlayerSub()) {
				logger.info("You announce that you are in sector " + sector);
				opponentAI.setLastKnownPlayerSurfacingSector(sector);
				view.getGamePanel().getCaptainPanel().addCaptainsLog("SURFACE IN SECTOR " + sector);

				// Enemy team takes 3 turns (unless shortened by their own Surface)
				List<CaptainCommand> commands = opponentAI.getPlayerSurfacedMovements();
				for (CaptainCommand command1: commands){
					handleCaptainCommand(command1, model.getGame().getEnemySub());
				}
			}
			else {
				logger.info("Enemy sub has surfaced in sector " + sector);
				view.getGamePanel().getCaptainPanel().addCaptainsLog("ENEMY SURFACED IN SECTOR " + sector);
			}

			// Captain erases his route, keeping only current position and position of his Mines
			MapNode current = submarine.getCurrentLocation();
			model.getGame().getGamemap().getNodeList().forEach(node -> {
				if (node.getVisitedBy().contains(submarine) && node != current) {
					node.getVisitedBy().remove(submarine);
					node.getVisitedFrom().remove(submarine);
				}
			});
			current.getVisitedFrom().remove(submarine);
		}
		else if (command.isDropMine()){
			handleDropMine(command, submarine);
		}
		else if (command.isTriggerMine()){
			// Get enemy sub mine location to trigger
            MapNode mineLocation = opponentAI.getTriggerMineLocation();
            if (mineLocation != null && mineLocation.getMines().contains(submarine)){
				handleTriggerMine(mineLocation, submarine);
			}
		}
		else if (command.isDrone()){
			// Are you in sector X?
			if (submarine == model.getGame().getPlayerSub()){
				// Ask user to select sector to ask about
                Integer sectorQuery = (Integer)
						JOptionPane.showInputDialog(
								view.getFrame(),
								"Ask about which sector?",
								"Activate Drone",
								JOptionPane.QUESTION_MESSAGE,
								null,
								new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9},
								1);
				int actualSector = model.getGame().getGamemap().getSector(model.getGame().getEnemySub().getCurrentLocation().getPoint());
				if (sectorQuery == actualSector)
					JOptionPane.showMessageDialog(view.getFrame(), "YES, Enemy sub in Sector " + sectorQuery);
				else
					JOptionPane.showMessageDialog(view.getFrame(), "NO, Enemy sub NOT in Sector " + sectorQuery);
			}
			else {
				// Get sector from AI
				int sectorQuery = opponentAI.getDroneSectorQuery();
				int actualSector = model.getGame().getGamemap().getSector(model.getGame().getPlayerSub().getCurrentLocation().getPoint());
				opponentAI.setDroneSectorQueryResponse(sectorQuery, sectorQuery == actualSector);
				logger.info("Enemy AI asked if player in sector " + sectorQuery + ", player responded " + (sectorQuery == actualSector? "YES": "NO"));
			}
			submarine.getSystems().reset(System.DRONE);
		}
		else if (command.isTorpedo()){
			if (submarine == model.getGame().getPlayerSub()){
				model.getGame().setGamePhaseStep(GamePhaseStep.PLAYER_TURN_FIRE_TORPEDO);
				JOptionPane.showMessageDialog(view.getFrame(), "Activating Torpedo, choose target location");
			}
			else {
				// Get target location from AI
                MapNode target = opponentAI.getTorpedoTargetLocation();
                if (target != null) {
                	handleTorpedoImpact(target, submarine);
					submarine.getSystems().reset(System.TORPEDO);
				}
			}
		}
		else if (command.isSonar()){
			if (submarine == model.getGame().getPlayerSub()){
				SonarReport sonarReport = opponentAI.getSonarReport();
				shout(sonarReport.toString(), model.getGame().getEnemySub());
			}
			else {
				// TODO Ask player what info to give AI
			}
			submarine.getSystems().reset(System.SONAR);
		}
		else if (command.isCustom()){
			submarine.getSystems().reset(System.SCENARIO);
		}
	}

	private void handleCaptainCommandDirection(CaptainCommand command, Submarine submarine){
		if (playerMoved && submarine == model.getGame().getPlayerSub())
			return;
		MapNode current = submarine.getCurrentLocation();
		MapNode dest = model.getGame().getGamemap().getInDir(command.getDirection(), current.getPoint());
		if (dest == null){
			logger.info("Cannot move " + command.getDirection() + ", no MapNode found");
			return;
		}
		if (dest.isIsland()){
			logger.info("Cannot move " + command.getDirection() + ", island found");
			return;
		}
		if (dest.getVisitedBy().contains(submarine)){
			logger.info("Cannot move " + command.getDirection() + ", MapNode already visited by this submarine");
			return;
		}

		// Move sub to new location
		dest.getVisitedFrom().put(submarine, current);
		dest.getVisitedBy().add(submarine);
		submarine.setCurrentLocation(dest);
		if (model.getGame().getPlayerSub() == submarine) {
			playerMoved = true;
			playerLastMoveDirection = command.getDirection();
			model.getGame().getEnemySub().getEnemySubMovements().add(command.getDirection());
			view.getGamePanel().getCaptainPanel().addCaptainsLog("MOVE " + command.getDirection());
		}
		else {
			view.getGamePanel().getCaptainPanel().addCaptainsLog("[ENEMY] MOVE " + command.getDirection());
			model.getGame().getPlayerSub().getEnemySubMovements().add(command.getDirection());
		}
	}

	private void handleDropMine(CaptainCommand command, Submarine submarine){
		if (!submarine.getSystems().isCharged(System.MINE)){
			logger.info("Mines not charged, cannot drop!");
			return;
		}
		MapNode current = submarine.getCurrentLocation();
		if (current.getMines().contains(submarine)){
			logger.info("This submarine has already dropped a mine here, cannot drop another");
			return;
		}

		// Drop mine 1 node away (not on path)
        if (submarine == model.getGame().getPlayerSub()){
        	// Let player select adjacent node to place mine
			model.getGame().setGamePhaseStep(GamePhaseStep.PLAYER_TURN_DROP_MINE);
			return;
		}
        else {
        	MapNode mapNode = opponentAI.getDropMineLocation();
        	if (mapNode != null){
        		mapNode.getMines().add(submarine);
			}
        	else
        		logger.warning("OpponentAI returned null for Drop Mine Location!");
		}
		submarine.getSystems().reset(System.MINE);
	}

	private void handleTriggerMine(MapNode mineLocation, Submarine attacker){
		if (attacker == model.getGame().getPlayerSub()) {
			MapNode enemyLocation = model.getGame().getEnemySub().getCurrentLocation();
			if (enemyLocation == mineLocation) {
				// Direct hit - 2 damage
				model.getGame().getEnemySub().incSubDamage();
				model.getGame().getEnemySub().incSubDamage();
				logger.info("Direct hit on enemy sub!");
				shout("DIRECT HIT!", model.getGame().getEnemySub());
			} else if (model.getGame().getGamemap().getNeighbors(mineLocation.getPoint()).contains(enemyLocation)) {
				// Indirect hit - 1 damage
				model.getGame().getEnemySub().incSubDamage();
				logger.info("Indirect hit on enemy sub");
				shout("INDIRECT HIT!", model.getGame().getEnemySub());
			} else {
				logger.info("ALL CLEAR");
				shout("ALL CLEAR!", model.getGame().getEnemySub());
			}
		}
		else {
			MapNode playerLocation = model.getGame().getPlayerSub().getCurrentLocation();
			if (playerLocation == mineLocation) {
				// Direct hit - 2 damage
				model.getGame().getPlayerSub().incSubDamage();
				model.getGame().getPlayerSub().incSubDamage();
				logger.info("Direct hit on player sub!");
				shout("DIRECT HIT!", model.getGame().getPlayerSub());
			} else if (model.getGame().getGamemap().getNeighbors(mineLocation.getPoint()).contains(playerLocation)) {
				// Indirect hit - 1 damage
				model.getGame().getPlayerSub().incSubDamage();
				logger.info("Indirect hit on player sub");
				shout("INDIRECT HIT!", model.getGame().getPlayerSub());
			} else {
				logger.info("ALL CLEAR");
				shout("ALL CLEAR!", model.getGame().getPlayerSub());
			}
		}
		mineLocation.getMines().remove(attacker);
	}

	private void handleTorpedoImpact(MapNode mapNode, Submarine attacker){
		if (attacker == model.getGame().getPlayerSub()) {
			MapNode enemyLocation = model.getGame().getEnemySub().getCurrentLocation();
			if (enemyLocation == mapNode) {
				// Direct hit - 2 damage
				model.getGame().getEnemySub().incSubDamage();
				model.getGame().getEnemySub().incSubDamage();
				logger.info("Direct hit on enemy sub!");
				view.getGamePanel().getCaptainPanel().addCaptainsLog("DIRECT HIT!");
				shout("DIRECT HIT!", model.getGame().getEnemySub());
			} else if (model.getGame().getGamemap().getNeighbors(mapNode.getPoint()).contains(enemyLocation)) {
				// Indirect hit - 1 damage
				model.getGame().getEnemySub().incSubDamage();
				logger.info("Indirect hit on enemy sub");
				view.getGamePanel().getCaptainPanel().addCaptainsLog("INDIRECT HIT!");
				shout("INDIRECT HIT!", model.getGame().getEnemySub());
			} else {
				logger.info("ALL CLEAR");
				view.getGamePanel().getCaptainPanel().addCaptainsLog("ALL CLEAR!");
				//JOptionPane.showMessageDialog(view.getFrame(), "[ENEMY CAPTAIN] ALL CLEAR!");
				shout("ALL CLEAR!", model.getGame().getEnemySub());
			}
		}
		else {
			MapNode playerLocation = model.getGame().getPlayerSub().getCurrentLocation();
			if (playerLocation == mapNode) {
				// Direct hit - 2 damage
				model.getGame().getPlayerSub().incSubDamage();
				model.getGame().getPlayerSub().incSubDamage();
				logger.info("Direct hit on player sub!");
				shout("DIRECT HIT!", model.getGame().getPlayerSub());
			} else if (model.getGame().getGamemap().getNeighbors(mapNode.getPoint()).contains(playerLocation)) {
				// Indirect hit - 1 damage
				model.getGame().getPlayerSub().incSubDamage();
				logger.info("Indirect hit on player sub");
				shout("INDIRECT HIT!", model.getGame().getPlayerSub());
			} else {
				logger.info("ALL CLEAR");
				shout("ALL CLEAR!", model.getGame().getPlayerSub());
			}
		}
		if (mapNode.getMines().contains(attacker))
			mapNode.getMines().remove(attacker);
	}

	private void handleComponentDamaged(Engineering.SubComponent subComponent, Submarine submarine){
		Direction direction = subComponent.getDirection();
		int chain = subComponent.getChain();
		boolean reactor = subComponent.getType() == ComponentType.REACTOR;

		if (reactor){
			// Check if all Reactors damaged.  If so, damage sub and repair ALL components
			if (submarine.getEngineering().getSubComponents().stream().filter(c -> c.getType() == ComponentType.REACTOR).allMatch(c -> c.isDamaged())){
				submarine.incSubDamage();
				logger.info("Sub Damaged!");
				shout("Submarine Damaged!", submarine);
				repairAllSubComponents(submarine);
			}
		}
		else {
			// Get all subcomponents in control panel for given direction
            List<Engineering.SubComponent> controlPanel = submarine.getEngineering().getSubComponents(subComponent.getDirection());
			// If all damaged, damage sub and repair ALL components
			if (controlPanel.stream().allMatch(c -> c.isDamaged())){
				submarine.incSubDamage();
				repairAllSubComponents(submarine);
				logger.info("Sub Damaged!");
				shout("Submarine Damaged!", submarine);
			}
			// Get all subcomponents in chain.  If all damaged, repair all damaged components in chain
			else {
				List<Engineering.SubComponent> chainComponents = submarine.getEngineering().getSubComponents(chain);
				if (chainComponents.stream().allMatch(c -> c.isDamaged())){
					chainComponents.stream().forEach(c -> c.setDamaged(false));
					logger.info("Chain repaired!");
				}
			}
		}
	}

	private void repairAllSubComponents(Submarine submarine){
		submarine.getEngineering().getSubComponents().stream().forEach(c -> c.setDamaged(false));
		logger.info("All Components Repaired!");
		shout("All Components Repaired", submarine);
	}

	private void checkGameover() {
		if (model.getGame().getPlayerSub().getSubDamage() == model.getGame().getPlayerSub().getMaxDamage()) {
			logger.info("Player Sub has taken too much damage! You Lose!");
			JOptionPane.showMessageDialog(view.getFrame(), "Your Submarine has taken too much damage! You Lose!");
			model.getGame().setGamePhase(GamePhase.GAMEOVER);
		}
		if (model.getGame().getEnemySub().getSubDamage() == model.getGame().getEnemySub().getMaxDamage()) {
			logger.info("AI Sub has taken too much damage!  You Win!");
			JOptionPane.showMessageDialog(view.getFrame(), "Enemy Submarine has taken too much damage!  You Win!");
			model.getGame().setGamePhase(GamePhase.GAMEOVER);
		}
	}

	private void shout(String text, Submarine submarine){
		String who = "[You] ";
		if (submarine != model.getGame().getPlayerSub())
			who = "[Enemy Captain] ";
		JOptionPane.showMessageDialog(view.getFrame(), who + text);
	}
}
