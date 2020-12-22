package sonar.game;

import java.util.ArrayList;
import java.util.List;

public class Submarine {
    private Systems systems = new Systems();
    private Engineering engineering = new Engineering();
    private int subDamage = 0;
    private int maxDamage = 4;
    private MapNode currentLocation;
    private List<Direction> enemySubMovements = new ArrayList<>();

    // Number of turns left that sub must skip turn
    // Decrement each turn
    private int surfacedSkipTurns = 0;

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

    public int getSurfacedSkipTurns() {
        return surfacedSkipTurns;
    }

    public void setSurfacedSkipTurns(int surfacedSkipTurns) {
        this.surfacedSkipTurns = surfacedSkipTurns;
    }

    public void decSurfacedSkipTurns() {
        this.surfacedSkipTurns -= 1;
        if (this.surfacedSkipTurns < 0)
            this.surfacedSkipTurns = 0;
    }

    public List<Direction> getEnemySubMovements() {
        return enemySubMovements;
    }
}
