package sonar.game;

public class Submarine {
    private Systems systems = new Systems();
    private Engineering engineering = new Engineering();
    private int subDamage = 0;
    private int maxDamage = 4;
    private MapNode currentLocation;

    public Submarine(){

    }

    public int getSubDamage() {
        return subDamage;
    }

    public void setSubDamage(int subDamage) {
        this.subDamage = subDamage;
        if (this.subDamage >= this.maxDamage)
            this.subDamage = this.maxDamage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    public void incSubDamage(){
        setSubDamage(this.subDamage + 1);
    }

    public MapNode getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(MapNode currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Systems getSystems() {
        return systems;
    }

    public Engineering getEngineering() {
        return engineering;
    }
}
