package sonar.game.comp_ai;

import sonar.Model;
import sonar.game.CaptainCommand;
import sonar.game.Direction;
import sonar.game.MapNode;
import sonar.game.Submarine;
import sonar.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class OpponentAI {
    protected Logger logger;
    protected Model model;
    protected View view;
    protected Submarine submarine;

    protected Integer lastKnownPlayerSurfacingSector;

    public OpponentAI(Model model, View view, Submarine submarine){
        this.model = model;
        this.view = view;
    }

    public abstract CaptainCommand getCaptainCommand();

    public abstract System getSystemToCharge();

    public abstract System getSystemToActivate();

    public abstract List<CaptainCommand> getSilenceMovements();

    public abstract List<CaptainCommand> getPlayerSurfacedMovements();

    public abstract int getDroneSectorQuery();

    public abstract void setDroneSectorQueryResponse(int sectorQuery, boolean response);

    public abstract MapNode getDropMineLocation();

    public abstract MapNode getTriggerMineLocation();

    public List<Integer> getMostProbableQuadrantPlayerLocation(){
        List<Integer> quadrant_locations = new ArrayList<>();
        // TODO implement this
        return quadrant_locations;
    }

    public Direction getDirectionTowardPlayerLocations(List<Integer> quadrantLocations) {
        Direction dir = Direction.NORTH;
        // TODO implement this
        return dir;
    }

    private boolean isPossiblePlayerLocation(int x, int y) {
		/*
		loc = (x, y)
		expected_sector = None
		sonar_info = None
		for dir in self.player_movements[::-1]:
			if dir.startswith( Gamemap.DRONE ):
				# Sonar(<sector>=YES|NO)
				d = dir[ (dir.find("(") + 1) : dir.find(")") ]
				sector, value = d.split( "=" )
				if value == "yes":
					expected_sector = int(sector)
				else:
					expected_sector = None
			elif dir.startswith( Gamemap.SONAR ):
				# (row=%s, col=%s, sector=%s)
				d = dir[ (dir.find("(") + 1) : dir.find(")") ]
				rowstr, colstr, sectorstr = d.split( "," )
				sonar_info = {
					'row': rowstr.strip().split( "=" )[ 1 ],
					'col': colstr.strip().split( "=" )[ 1 ],
					'sector': sectorstr.strip().split( "=" )[ 1 ]
				}
			elif dir == Gamemap.MINE:
				pass
			elif dir == Gamemap.SILENCE:
				# Player could have moved 0-4 spaces in a single direction
				pass
			elif dir == Gamemap.SCENARIO:
				pass
			else:
				ret = True
				opp_dir = self.gamemap.get_opposite_direction( dir )
				loc = self.gamemap.get_coord_in_dir( opp_dir, loc )
				node = self.gamemap.get( loc )
				if node is None:
					ret = False
				if ret and expected_sector is not None and not self.gamemap.is_in_sector( expected_sector, loc );
					ret = False
				if ret and sonar_info is not None:
					# Only one of the info is true
					row_true = False
					col_true = False
					sec_true = False
					for key in [ 'row', 'col', 'sector' ]:
						if sonar_info[ key ] != "":
							if key == 'row' and int(sonar_info[ key ]) == loc[ 1 ]:
								row_true = True
							elif key == 'col' and int(sonar_info[ key ]) == loc[ 0 ]:
								col_true = True
							elif key == 'sector' and self.gamemap.is_in_sector( int(sonar_info[ key ]), loc ):
								sec_true = Trues
					# if all are false, then this loc cannot be where the player is
					if not row_true and not col_true and not sec_true:
						ret = False
				# Drone and Sonar info is only valid for one movement
				expected_sector = None
				sonar_info = None
				if not ret:
					return ret
		return True
		*/
        return false;
	}

    public void setLastKnownPlayerSurfacingSector(Integer lastKnownPlayerSurfacingSector) {
        this.lastKnownPlayerSurfacingSector = lastKnownPlayerSurfacingSector;
    }
}
