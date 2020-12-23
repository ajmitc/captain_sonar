package sonar.game;

import java.util.HashMap;
import java.util.Map;

public class Systems{
    private Map<System, SystemTracker> systems = new HashMap<>();
    
	public Systems(){
		systems.put(System.MINE, new SystemTracker(3));
		systems.put(System.TORPEDO, new SystemTracker(3));
		systems.put(System.SONAR, new SystemTracker(3));
		systems.put(System.SILENCE, new SystemTracker(6));
		systems.put(System.DRONE, new SystemTracker(4));
		systems.put(System.SCENARIO, new SystemTracker(4)); // or 6

		systems.get(System.DRONE).setCharges(3);
	}
        
        
	public void charge(System what){
		if (systems.containsKey(what)) {
			if (systems.get(what).getCharges() < systems.get(what).getMaxCharges()) {
				systems.get(what).charge();
			}
		}
	}
    
    
	public boolean isCharged(System what) {
		if (systems.containsKey(what))
			return systems.get(what).getCharges() == systems.get(what).getMaxCharges();
		return false;
	}
	
	public void reset(System what) {
		if (systems.containsKey(what))
			systems.get(what).setCharges(0);
	}

	public int getChargeLevel(System what){
		if (systems.containsKey(what)){
			return systems.get(what).getCharges();
		}
		return 0;
	}

	public int getMaxChargeLevel(System what){
		if (systems.containsKey(what)){
			return systems.get(what).getMaxCharges();
		}
		return 0;
	}

	private static class SystemTracker{
			private int charges = 0;
			private int maxCharges = 3;

			public SystemTracker(int max){
				this.maxCharges = max;
			}

		public int getCharges() {
			return charges;
		}

		public void setCharges(int charges) {
			this.charges = charges;
		}

		public void charge(){
				this.charges += 1;
		}

		public int getMaxCharges() {
			return maxCharges;
		}

		public void setMaxCharges(int maxCharges) {
			this.maxCharges = maxCharges;
		}
	}
}