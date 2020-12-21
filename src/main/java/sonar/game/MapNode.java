package sonar.game;

import java.awt.*;
import java.util.*;

public class MapNode {
	private int x;
	private int y;
	private Set<Submarine> visitedBy = new HashSet<>();
	private Map<Submarine, MapNode> visitedFrom = new HashMap<>();
	private Set<Submarine> mines = new HashSet<>();
	private boolean island = false;

	public MapNode(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public MapNode(int x, int y, boolean island) {
		this(x, y);
		this.island = island;
	}

	public boolean isIsland() {
		return island;
	}


	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Point getPoint() {
		return new Point(x, y);
	}

	public void setIsland(boolean island) {
		this.island = island;
	}

	public Set<Submarine> getVisitedBy() {
		return visitedBy;
	}

	public Map<Submarine, MapNode> getVisitedFrom() {
		return visitedFrom;
	}

	public Set<Submarine> getMines() {
		return mines;
	}

	public String toString(){
		char cy = (char) ('A' + y);
		return "" + cy + (x + 1);
	}
}
