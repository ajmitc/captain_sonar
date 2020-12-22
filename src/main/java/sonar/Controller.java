package sonar;

import sonar.game.*;
import sonar.game.System;
import sonar.game.comp_ai.EasyOpponent;
import sonar.game.comp_ai.NormalOpponent;
import sonar.game.comp_ai.OpponentAI;
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

	public Controller(Model model, View view){
        this.model = model;
        this.view = view;

        this.view.getMainMenuPanel().getBtnNewGame().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setGame(new Game());
                model.getGame().setPlayerSub(new Submarine());
                model.getGame().setEnemySub(new Submarine());
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

		this.view.getGamePanel().getCaptainPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (model.getGame().getGamePhaseStep() == GamePhaseStep.PLAYER_TURN_DROP_MINE){
					MapNode selected = view.getGamePanel().getCaptainPanel().getMapNodeAt(e.getX(), e.getY());
					if (selected == null)
					    return;
					if (selected.isIsland()){
						logger.warning("Cannot place mine on island");
						return;
					}
					if (selected.getVisitedBy().contains(model.getGame().getPlayerSub())){
						logger.warning("Cannot place mine on path");
						return;
					}
					selected.getMines().add(model.getGame().getPlayerSub());
					model.getGame().getPlayerSub().getSystems().reset(System.MINE);
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
								view.getGamePanel().getCaptainPanel().addCaptainsLog("MOVE " + direction);
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
						JOptionPane.showConfirmDialog(view.getFrame(), "You must move first");
						return;
					}
					Engineering.SubComponent subComponent = view.getGamePanel().getEngineerPanel().getClickedSubComponent(e.getX(), e.getY());
					if (subComponent != null){
						if (subComponent.isDamaged()){
							logger.warning("Component already damaged, select a different component");
						}
						subComponent.setDamaged(true);
						view.refresh();
						playerDamagedComponent = true;
						view.getGamePanel().getCaptainPanel().addCaptainsLog("DAMAGE COMPONENT " + subComponent);
						handleComponentDamaged(subComponent, model.getGame().getPlayerSub());
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

							MapNode startLoc = model.getGame().getGamemap().getRandomLocation();
							startLoc.getVisitedBy().add(model.getGame().getPlayerSub());
							model.getGame().getPlayerSub().setCurrentLocation(startLoc);
							logger.info("Placed Player sub on map at " + startLoc);

							startLoc = model.getGame().getGamemap().getRandomLocation();
							startLoc.getVisitedBy().add(model.getGame().getEnemySub());
							model.getGame().getEnemySub().setCurrentLocation(startLoc);
							logger.info("Placed AI sub on map at " + startLoc);

							//model.getGame().getGamemap().printMap(model.getGame().getPlayerSub().getCurrentLocation());
							model.getGame().setDifficulty(GameDifficulty.EASY);

							switch (model.getGame().getDifficulty()){
								case EASY -> opponentAI = new EasyOpponent(model, view, enemySub);
								case NORMAL -> opponentAI = new NormalOpponent(model, view, enemySub);
								case HARD -> opponentAI = new NormalOpponent(model, view, enemySub);
							}

							model.getGame().setGamePhaseStep(GamePhaseStep.END_PHASE);
							break;
						case END_PHASE:
							logger.info("Starting game...");
						    model.getGame().setGamePhase(GamePhase.PLAYER_TURN);
							break;
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
							view.getGamePanel().getCaptainCommandPanel().getBtnEndTurn().setEnabled(false);
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
							if (playerMoved && playerDamagedComponent && playerChargedSystem){
								view.getGamePanel().getCaptainCommandPanel().getBtnEndTurn().setEnabled(true);
							}
							return;
						case PLAYER_TURN_SILENCE:
							// TODO Manage this
							break;
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
							model.getGame().setGamePhaseStep(GamePhaseStep.END_PHASE);
							break;
						case END_PHASE:
							model.getGame().setGamePhase(GamePhase.PLAYER_TURN);
							break;
					}
					break;
			}
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
				// TODO Player gets to take 3 turns
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
					JOptionPane.showConfirmDialog(view.getFrame(), "YES, Enemy sub in Sector " + actualSector);
				else
					JOptionPane.showConfirmDialog(view.getFrame(), "NO, Enemy sub NOT in Sector " + actualSector);
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

			submarine.getSystems().reset(System.TORPEDO);
		}
		else if (command.isSonar()){

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
			model.getGame().getEnemySub().getEnemySubMovements().add(command.getDirection());
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

	private void handleTriggerMine(MapNode mineLocation, Submarine submarine){
		if (submarine == model.getGame().getPlayerSub()) {
			MapNode enemyLocation = model.getGame().getEnemySub().getCurrentLocation();
			if (enemyLocation == mineLocation) {
				// Direct hit - 2 damage
				model.getGame().getEnemySub().incSubDamage();
				model.getGame().getEnemySub().incSubDamage();
				logger.info("Direct hit on enemy sub!");
			} else if (model.getGame().getGamemap().getNeighbors(mineLocation.getPoint()).contains(enemyLocation)) {
				// Indirect hit - 1 damage
				model.getGame().getEnemySub().incSubDamage();
				logger.info("Indirect hit on enemy sub");
			} else {
				logger.info("ALL CLEAR");
			}
		}
		else {
			MapNode playerLocation = model.getGame().getPlayerSub().getCurrentLocation();
			if (playerLocation == mineLocation) {
				// Direct hit - 2 damage
				model.getGame().getPlayerSub().incSubDamage();
				model.getGame().getPlayerSub().incSubDamage();
				logger.info("Direct hit on player sub!");
			} else if (model.getGame().getGamemap().getNeighbors(mineLocation.getPoint()).contains(playerLocation)) {
				// Indirect hit - 1 damage
				model.getGame().getPlayerSub().incSubDamage();
				logger.info("Indirect hit on player sub");
			} else {
				logger.info("ALL CLEAR");
			}
		}
		mineLocation.getMines().remove(submarine);
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
	}


	/*
	public Direction getPlayerDirection( self ):
		while True:
			inp = raw_input( "Direction and systems: " ).strip()
			parts = inp.split( " " )
			handled_dir = False
			for inp in parts:
				self.print_debug( "Handling '%s'" % inp )
				if inp.lower() in [ "n", "north" ]:
					if len(self.player_movements) > 0 and self.player_movements[ -1 ] == Gamemap.SOUTH:
						print "Node already visited!"
						continue
					self.player_movements.append( Gamemap.NORTH )
					handled_dir = True
				elif inp.lower() in [ "s", "south" ]:
					if len(self.player_movements) > 0 and self.player_movements[ -1 ] == Gamemap.NORTH:
						print "Node already visited!"
						continue
					self.player_movements.append( Gamemap.SOUTH )
					handled_dir = True
				elif inp.lower() in [ "e", "east" ]:
					if len(self.player_movements) > 0 and self.player_movements[ -1 ] == Gamemap.WEST:
						print "Node already visited!"
						continue
					self.player_movements.append( Gamemap.EAST )
					handled_dir = True
				elif inp.lower() in [ "w", "west" ]:
					if len(self.player_movements) > 0 and self.player_movements[ -1 ] == Gamemap.EAST:
						print "Node already visited!"
						continue
					self.player_movements.append( Gamemap.WEST )
					handled_dir = True
				elif inp.lower().startswith( "surf" ):
					self.player_movements.append( Gamemap.SURFACE )
					handled_dir = True
				elif inp.lower() in [ 'm', 'mine' ]:
					# Player has dropped a mine
					print "You have dropped a mine"
					self.player_movements.append( Gamemap.MINE )
				elif inp.lower() in [ 'tm', 'triggermine', 'detonate' ]:
					# Player has detonated a mine
					self.player_detonate_mine()
				elif inp.lower() in [ 't', 'tor', 'torpedo' ]:
					# Player has launched a torpedo
					self.player_activate_torpedo()
				elif inp.lower() in [ 'd', 'dro', 'drone' ]:
					# Player is asking for a specific quadrant
					self.player_activate_drone()
				elif inp.lower().startswith( 'son' ):
					# Player should receive one true and one false piece of information (row, column, sector)
					self.player_activate_sonar()
				elif inp.lower().startswith( 'sil' ):
					# Player activating silence mode
					self.player_movements.append( Gamemap.SILENCE )
					self.player_activate_silence()
				elif inp.lower().startswith( 'sc' ):
					# Player activating scenario system
					self.player_movements.append( Gamemap.SCENARIO )
					self.player_activate_scenario()
			if not self.stop and not handled_dir:
				print "Invalid direction or no direction given"
				continue
			break
	*/

	public void playerActivateTorpedo() {
		/*
		while (true){
			xy = raw_input( "Where is the impact (xy)? " ).strip().lower()
			if xy == 'cancel':
				break
			xy = xy.replace( " ", "" )
			x = xy[ 0 ]
			y = xy[ 1: ]
			x = ord(x) - ord('a')
			try:
				y = int(y) - 1
			except:
				print "Invalid Y coordinate - must be a number"
				continue
			if self.handle_impact_at( x, y ):
				break
		*/
	}

	private void playerActivateDrone() {
	    /*
		while True:
			sector = raw_input( "When sector? " )
			try:
				sector = int(sector)
			except:
				print "Invalid sector"
				continue
			sector -= 1  # sector must be zero-based
			if self.gamemap.get_sector() == sector:
				print "AI Responds with YES"
			else:
				print "AI Responds with NO"
			break
                    */
	}

	private void playerActivateSonar() {
	    /*
		# AI must give one true and one false piece of information
		sector = self.gamemap.get_sector()
		trues = {
			'Row': str(self.gamemap.current.y + 1),
			'Col': chr(self.gamemap.current.x + ord('A')),
			'Sector': str(sector + 1)
		}
		falses = {
			"Row": [ str(i + 1) for i in xrange( len(self.gamemap.map) ) if i != self.gamemap.current.y ],
			'Col': [ chr(i + ord('A')) for i in xrange( len(self.gamemap.map) ) if i != self.gamemap.current.x ],
			'Sector': [ str(i + 1) for i in xrange( (len(self.gamemap.map) / 5) * (len(self.gamemap.map) / 5) ) if i != sector ]
		}
		true_cat = random.choice( trues.keys() )
		false_cat = None
		while True:
			false_cat = random.choice( falses.keys() )
			if false_cat == true_cat:
				continue
			break
		false_choice = random.choice( falses[ false_cat ] )
		choices = [ "%s: %s" % (true_cat, trues[ true_cat ]), "%s: %s" % (false_cat, false_choice) ]
		self.print_debug( "%s: %s (TRUE)" % (true_cat, trues[ true_cat ] ) )
		self.print_debug( "%s: %s (FALSE)" % (false_cat, false_choice ) )
		random.shuffle( choices )
		for c in choices:
			print c
		*/
	}

	private void playerActivateSilence() {
		logger.info("Player activates silence!");
	}


	private void playerActivateScenario() {
		logger.info("Player activates scenario system!");
	}


	private void playerDetonateMine() {
	    /*
		while True:
			inp = raw_input( "Detonate mine at xy: " ).strip().lower()
			if inp == 'cancel':
				break
			xy = xy.replace( " ", "" )
			x = xy[ 0 ]
			y = xy[ 1: ]
			x = ord(x) - ord('a')
			if x < 0 or x >= len(self.gamemap.map):
				print "Invalid X coordinate"
				continue
			try:
				y = int(y) - 1
			except:
				print "Invalid Y coordinate - must be a number"
				continue
			if self.handle_impact_at( x, y ):
				break
		*/
	}

	private void handleImpactAt(int x, int y) {
	    /*
		print "Handling impact at [%d, %d]" % (x, y)
		impactat = self.gamemap.get( [x,y] )
		if impactat is None:
			print "Invalid coordinate"
			return False
		neighbors = self.gamemap.get_neighbors( [x,y] )
		# Destroy any mines at 'impactat' location
		impactat.mine = False
		# Check if direct hit on AI sub
		if impactat == self.gamemap.current:
			print "DIRECT HIT!"
			self.sub_damage += 2
		elif self.gamemap.current in neighbors:
			print "INDIRECT HIT!"
			self.sub_damage += 1
		else:
			print "ALL CLEAR!"
		if self.sub_damage >= self.MAX_DAMAGE:
			print "AI Sub Sank!"
			self.stop = True
		return True
		*/
	}

	private void doCompTurn(boolean silence) {
		/*
		self.analyze_player_map()
		d = self.comp_choose_direction()
		if not silence or self.debug:
			self.print_debug( "AI Sub moving %s" % d )
		n = self.gamemap.get_in_dir( d )
		n.visited = True
		self.gamemap.current = n
		self.gamemap.print_map()
		self.comp_charge_system()
		self.comp_damage_system( d )
		if self.check_gameover():
			return
		self.comp_activate_system()
		if not silence or self.debug:
			print "\n==================\nAI Sub Moves %s\n==================\n" % d
		*/
	}

	private void analyzePlayerMap() {
	    /*
		""" Attempt to find the player.  Populate the possible_player_locations list. """
		self.possible_player_locations = []
		for y in xrange( self.gamemap.height ):
			for x in xrange( self.gamemap.width ):
				node = self.gamemap.get( (x, y) )
				if self.is_possible_player_location( x, y ):
					self.possible_player_locations.append( (x,y) )
		self.print_debug( "Found %d possible player locations" % len(self.possible_player_locations) )
		#for loc in self.possible_player_locations:
			#self.print_debug( "   %s" % str(loc) )
		*/
	}

	private void compChargeSystem() {
	    /*
		priority = [
			Systems.TORPEDO,
			Systems.MINE,
			Systems.SONAR,
			Systems.SILENCE,
			Systems.DRONE,
			#Systems.SCENARIO,
		]
		for system in priority:
			if not self.systems.is_charged( system ):
				self.systems.charge( system )
				self.print_debug( "AI Charging %s" % str(system) )
				break
		*/
	}

	/*
	def comp_damage_system( self, dir ):
		comp = self.engineering.get_random_undamaged_component( dir )
		self.print_debug( "AI Damaging %s" % str(comp) )
		if self.engineering.damage( comp ):
			  self.sub_damage += 1


	def comp_activate_system( self ):
		potential_systems = []
		if True or self.difficulty == self.EASY:
			potential_systems += [
				Systems.TORPEDO,
				Systems.MINE,
				Systems.DRONE,
				Systems.SONAR,
				Systems.SILENCE,
				#Systems.SCENARIO,
				Systems.TRIGGER_MINE
			]
		for system in potential_systems:
			if self.systems.is_charged( system ) and self.should_activate_system( system ):
				if self.use_system( system ):
					break


	def should_activate_system( self, what ):
		if system == Systems.TORPEDO:
			return len(self.possible_player_locations) < 10
		elif system == Systems.MINE:
			return True
		elif system == Systems.SONAR:
			return True
		elif system == Systems.SILENCE:
			return True
		elif system == Systems.DRONE:
			return True
		elif system == Systems.CUSTOM:
			return False
		elif system == Systems.TRIGGER_MINE:
			return True
		return False


	def use_system( self, system ):
		print "AI using system %s" % system
		if system == Systems.TORPEDO:
			self.ai_fire_torpedo()
		elif system == Systems.MINE:
			self.ai_drop_mine()
		elif system == Systems.SONAR:
			self.ai_use_sonar()
		elif system == Systems.SILENCE:
			self.ai_use_silence()
		elif system == Systems.DRONE:
			self.ai_use_drone()
		elif system == Systems.CUSTOM:
			self.ai_use_scenario()
		elif system == Systems.TRIGGER_MINE:
			self.ai_trigger_mine()
		self.systems.reset( system )
		return False


	def ai_fire_torpedo( self ):
		# Only fire the torpedo if the AI has a reasonable guess of where the player's sub is
		if len(self.possible_player_locations) == 0:
			print "Huh, AI has no possible player sub locations.  That's not right... Are you cheating?"
			return
		coord = random.choice( self.possible_player_locations )
		impactat = self.gamemap.get( coord )
		while True:
			print "AI fired torpedo, impact at %s" % (self.gamemap.format_coord( impactat ))
			print "1) DIRECT HIT!"
			print "2) INDIRECT HIT!"
			print "3) ALL CLEAR!"
			inp = raw_input( "> " ).strip().lower()
			if inp == "1":
				break
			elif inp == "2":
				break
			elif inp == "3":
				break
			else:
				print "Invalid option"


	def ai_drop_mine( self ):
		neighbors = self.gamemap.get_neighbors()
		neighbors = [ n for n in neighbors if not n.island and not n.mine ]
		neighbor = random.choice( neighbors )
		neighbor.mine = True
    */


	public void ai_trigger_mine() {
			    /*
		# Choose mine
		mines = self.gamemap.get_nodes_with_mines()
		if len(mines) == 0:
			return
		node = random.choice( mines )
		while True:
			print "AI triggering mine at %s" % self.gamemap.format_coord( [node.x, node.y] )
			print "1) DIRECT HIT!"
			print "2) INDIRECT HIT!"
			print "3) ALL CLEAR!"
			inp = raw_input( "> " ).strip().lower()
			if inp == "1":
				break
			elif inp == "2":
				break
			elif inp == "3":
				break
			else:
				print "Invalid option"
			     */
	}


	public void ai_use_sonar() {
				/*
		while True:
			print "AI is activating sonar!"
			print "You must tell one true and one false regarding row, column, and sector"
			print "Enter a value for two of the following three questions (one value must be true and one value must be false).  Leave one of the questions blank."
			row = raw_input( "Row? " ).strip().lower()
			if row != "":
				try:
					row = int(row) - 1
				except:
					print "Invalid value"
					continue
				if row < 0 or row >= len(self.gamemap.map):
					print "Invalid row number"
					continue
			col = raw_input( "Column? " ).strip().lower()
			if col != "":
				if len(col) > 1:
					print "Invalid column letter"
					continue
				col = ord(col) - ord("a")
				if col < 0 or col >= len(self.gamemap.map):
					print "Invalid column letter"
					continue
			sector = ""
			if row == "" or col == "":
				sector = raw_input( "Sector? " ).strip().lower()
				try:
					sector = int(sector) - 1
				except:
					print "Invalid sector"
					continue
			blanks = [ v for v in [ row, col, sector ] if v == "" ]
			if len(blanks) != 1:
				print "You must answer 2 questions and leave one blank"
				continue
			self.player_movements.append( "%s(row=%s, col=%s, sector=%s)" % (Gamemap.SONAR, str(row), str(col), str(sector)) )

				 */
	}


	private void ai_use_drone() {
				/*
		while True:
			print "AI is activating a drone!"
			sectors = {}
			for coord in self.possible_player_locations:
				s = self.gamemap.get_sector( coord )
				if s not in sectors.keys():
					sectors[ s ] = 0
				sectors[ s ] += 1
			if len(sectors) > 0:
				# Find the sector with the most possible locations
				sector = max(sectors, key=sectors.get)
			else:
				sector = random.choice( [ 1, 2, 3, 4 ] )
			inp = raw_input( "Are you in Sector %d? " % sector ).strip().lower()
			answer = "no"
			if inp in [ 'y', 'yes' ]:
				answer = 'yes'
			self.player_movements.append( "%s(%d=%s)" % (Gamemap.DRONE, sector, answer) )
				 */
	}


	private void checkGameover() {
		if (model.getGame().getPlayerSub().getSubDamage() == model.getGame().getPlayerSub().getMaxDamage()) {
			logger.info("Player Sub has taken too much damage! You Lose!");
			model.getGame().setGamePhase(GamePhase.GAMEOVER);
		}
		if (model.getGame().getEnemySub().getSubDamage() == model.getGame().getEnemySub().getMaxDamage()) {
			logger.info("AI Sub has taken too much damage!  You Win!");
			model.getGame().setGamePhase(GamePhase.GAMEOVER);
		}
	}
}
