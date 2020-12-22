package sonar.game;

import sonar.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Engineering{
	private List<SubComponent> components = new ArrayList<>();
	
	public Engineering() {
		components.add(new SubComponent(1, ComponentType.MINE_TORPEDO, Direction.WEST, 1));
		components.add(new SubComponent(2, ComponentType.SILENCE_SCENARIO, Direction.WEST, 1));
		components.add(new SubComponent(3, ComponentType.DRONE_SONAR, Direction.WEST, 1));
		components.add(new SubComponent(4, ComponentType.DRONE_SONAR, Direction.WEST, 0));
		components.add(new SubComponent(5, ComponentType.REACTOR, Direction.WEST, 0));
		components.add(new SubComponent(6, ComponentType.REACTOR, Direction.WEST, 0));

		components.add(new SubComponent(7, ComponentType.SILENCE_SCENARIO, Direction.NORTH, 2));
		components.add(new SubComponent(8, ComponentType.SILENCE_SCENARIO, Direction.NORTH, 2));
		components.add(new SubComponent(9, ComponentType.MINE_TORPEDO, Direction.NORTH, 2));
		components.add(new SubComponent(10, ComponentType.DRONE_SONAR, Direction.NORTH, 0));
		components.add(new SubComponent(11, ComponentType.MINE_TORPEDO, Direction.NORTH, 0));
		components.add(new SubComponent(12, ComponentType.REACTOR, Direction.NORTH, 0));

		components.add(new SubComponent(13, ComponentType.DRONE_SONAR, Direction.SOUTH, 3));
		components.add(new SubComponent(14, ComponentType.SILENCE_SCENARIO, Direction.SOUTH, 3));
		components.add(new SubComponent(15, ComponentType.MINE_TORPEDO, Direction.SOUTH, 3));
		components.add(new SubComponent(16, ComponentType.MINE_TORPEDO, Direction.SOUTH, 0));
		components.add(new SubComponent(17, ComponentType.REACTOR, Direction.SOUTH, 0));
		components.add(new SubComponent(18, ComponentType.SILENCE_SCENARIO, Direction.SOUTH, 0));

		components.add(new SubComponent(19, ComponentType.DRONE_SONAR, Direction.EAST, 2));
		components.add(new SubComponent(20, ComponentType.SILENCE_SCENARIO, Direction.EAST, 3));
		components.add(new SubComponent(21, ComponentType.MINE_TORPEDO, Direction.EAST, 1));
		components.add(new SubComponent(22, ComponentType.REACTOR, Direction.EAST, 0));
		components.add(new SubComponent(23, ComponentType.DRONE_SONAR, Direction.EAST, 0));
		components.add(new SubComponent(24, ComponentType.REACTOR, Direction.EAST, 0));
	}


	public boolean damage(SubComponent component){
		// Damage a component.  Return true if all components damaged in same compartment of the sub.  Return false otherwise.
		component.setDamaged(true);
		// check if sub damaged or chain repaired
		boolean subdamaged = true;
		boolean chainRepaired = true;
		for (SubComponent comp: components) {
			if (comp.getDirection() == component.getDirection()) {
				if (!comp.isDamaged())
					subdamaged = false;
				if (comp.getChain() == component.getChain()) // same chain
					if (!comp.isDamaged())
						chainRepaired = false;
			}
		}
		if (chainRepaired) {
			for (SubComponent comp : components) {
				if (comp.getChain() == component.getChain()) // same chain
					comp.setDamaged(false);
			}
		}
		return subdamaged;
	}


	public SubComponent getRandomUndamagedComponent (Direction dir) {
		List<SubComponent> undamaged = components.stream().filter(c -> c.getDirection() == dir && !c.isDamaged()).collect(Collectors.toList());
		int i = Util.getRandomInt(0, undamaged.size());
		return undamaged.get(i);
	}

	public List<SubComponent> getSubComponents() {
		return components;
	}

	public SubComponent getSubComponent(int id){
		for (SubComponent subComponent: components){
			if (subComponent.getId() == id)
				return subComponent;
		}
		return null;
	}

	public List<SubComponent> getSubComponents(Direction direction){
		return components.stream().filter(c -> c.getDirection() == direction).collect(Collectors.toList());
	}

	public List<SubComponent> getSubComponents(int chain){
		return components.stream().filter(c -> c.getChain() == chain).collect(Collectors.toList());
	}

	public static class SubComponent {
		private int id;
		private ComponentType type;
		private Direction direction;
		private int chain;
		private boolean damaged;

		public SubComponent(int id, ComponentType type, Direction direction, int chain) {
			this.id = id;
			this.type = type;
			this.direction = direction;
			this.chain = chain;
			this.damaged = false;
		}

		public int getId() {
			return id;
		}

		public boolean isDamaged() {
			return damaged;
		}

		public void setDamaged(boolean damaged) {
			this.damaged = damaged;
		}

		public ComponentType getType() {
			return type;
		}

		public Direction getDirection() {
			return direction;
		}

		public int getChain() {
			return chain;
		}

		public String toString(){
			return "" + this.type;
		}
	}
}