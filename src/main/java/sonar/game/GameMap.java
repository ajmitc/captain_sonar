package sonar.game;

import sonar.util.Util;
import sonar.view.ImageUtil;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GameMap {
	private static Logger logger = Logger.getLogger(GameMap.class.getName());

	//SURFACE = "Surface"

	//MINE = "Mine"  # indicates where the player has dropped a mine
	//SILENCE = "Silence"  # indicates the player has entered silence mode
	//SCENARIO = "Scenario"  # indicates the player has activated the scenario-specific system

	private int width = 0;
	private int height = 0;

	private List<List<MapNode>> mapNodes = new ArrayList<>();

	private Image mapImage;

	public GameMap() {

	}


	public boolean load(String imgFilename, String defFilename) {
		try {
			mapImage = ImageUtil.get(imgFilename, 650);
			BufferedReader br = new BufferedReader(new InputStreamReader(GameMap.class.getClassLoader().getResourceAsStream(defFilename)));
			String line;
			int lineIndex = 0;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (!line.equals("")) {
					if (lineIndex == 0) {
						String[] parts = line.split("x");
						width = Integer.decode(parts[0]);
						height = Integer.decode(parts[1]);
					} else {
						if (line.length() != width) {
							logger.info("Line " + (lineIndex + 1) + " has invalid length (length should be " + width + ")");
							return false;
						} else {
							List<MapNode> row = new ArrayList<>();
							for (int i = 0; i < line.length(); ++i) {
								MapNode mapNode = new MapNode(i, lineIndex - 1);
								if (line.charAt(i) != '.') {
									mapNode.setIsland(true);
								}
								row.add(mapNode);
							}
							mapNodes.add(row);
						}
					}
				}
				++lineIndex;
			}
			br.close();
		} catch (FileNotFoundException fnfe) {
			logger.severe("Unable to find map file: " + fnfe);
			return false;
		} catch (IOException ioe) {
			logger.severe("Caught IOException while reading map file: " + ioe);
			return false;
		}
		return true;
	}

	public List<MapNode> getNodeList() {
		List<MapNode> allNodes = new ArrayList<>();
		mapNodes.stream().forEach(l -> allNodes.addAll(l));
		return allNodes;
	}

	public MapNode get(int x, int y) {
		if (y < 0 || y >= mapNodes.size())
			return null;
		if (x < 0 || x >= mapNodes.get(y).size())
			return null;
		return mapNodes.get(y).get(x);
	}

	public MapNode get(Point p) {
		return get(p.x, p.y);
	}

	public MapNode getInDir(Direction dir, Point p) {
		Point c = getCoordInDir(dir, p);
		return get(c.x, c.y);
	}


	public MapNode getRandomLocation() {
		while (true) {
			int y = Util.getRandomInt(0, mapNodes.size() - 1);
			int x = Util.getRandomInt(0, mapNodes.get(y).size() - 1);
			MapNode n = get(x, y);
			if (!n.isIsland())
				return n;
		}
	}


	public Point getCoordInDir(Direction dir, Point coord) {
		if (dir == Direction.NORTH)
			return new Point(coord.x, coord.y - 1);
		if (dir == Direction.SOUTH)
			return new Point(coord.x, coord.y + 1);
		if (dir == Direction.WEST)
			return new Point(coord.x - 1, coord.y);
		if (dir == Direction.EAST)
			return new Point(coord.x + 1, coord.y);
		return coord;
	}


	public List<MapNode> getNeighbors(Point coord) {
		List<MapNode> neighbors = new ArrayList<>();
		neighbors.add(get(coord.x, coord.y - 1));
		neighbors.add(get(coord.x + 1, coord.y - 1));
		neighbors.add(get(coord.x + 1, coord.y));
		neighbors.add(get(coord.x + 1, coord.y + 1));
		neighbors.add(get(coord.x, coord.y + 1));
		neighbors.add(get(coord.x - 1, coord.y + 1));
		neighbors.add(get(coord.x - 1, coord.y));
		neighbors.add(get(coord.x - 1, coord.y - 1));
		return neighbors.stream().filter(n -> n == null).collect(Collectors.toList());
	}

	public Map<Direction, MapNode> getNeighborMap(Point coord) {
		Map<Direction, MapNode> neighbors = new HashMap<>();
		if (get(coord.x, coord.y - 1) != null)
			neighbors.put(Direction.NORTH, get(coord.x, coord.y - 1));
		if (get(coord.x + 1, coord.y) != null)
			neighbors.put(Direction.EAST, get(coord.x + 1, coord.y));
		if (get(coord.x, coord.y + 1) != null)
			neighbors.put(Direction.SOUTH, get(coord.x, coord.y + 1));
		if (get(coord.x - 1, coord.y) != null)
			neighbors.put(Direction.WEST, get(coord.x - 1, coord.y));
		return neighbors;
	}


	public int getSector(Point coord) {
		// Return the sector (zero-based) containing the coord (or self.current if coord is None)
		int totalRows = mapNodes.size();
		int totalSectorRows = totalRows / 5;
		int sectorRow = coord.y / 5;
		int sectorCol = coord.x / 5;
		return sectorRow * totalSectorRows + sectorCol;
	}


	public boolean isInSector(int sector, Point coord) {
		int totalRows = mapNodes.size();
		int totalSectorRows = totalRows / 5;
		int startRow = (int) ((sector / totalSectorRows) * 5);     // inclusive
		int stopRow = startRow + 5;                                // exclusive
		int startCol = (sector % totalSectorRows) * 5;             // exclusive
		int stopCol = startCol + 5;
		return startCol <= coord.x && coord.x < stopCol && startRow <= coord.y && coord.y < stopRow;
	}


	public List<MapNode> getNodesWithMines(Submarine submarine) {
		return getNodeList().stream().filter(n -> n.getMines().contains(submarine)).collect(Collectors.toList());
	}

	public Image getMapImage() {
		return mapImage;
	}
}
        
