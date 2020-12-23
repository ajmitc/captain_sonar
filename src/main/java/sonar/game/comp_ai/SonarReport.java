package sonar.game.comp_ai;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SonarReport {
    private Integer row = null;
    private Integer column = null;
    private Integer sector = null;

    public SonarReport(){

    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Integer getSector() {
        return sector;
    }

    public void setSector(Integer sector) {
        this.sector = sector;
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        if (row != null){
            list.add("Row " + row);
        }
        if (column != null){
            list.add("Column " + column);
        }
        if (sector != null){
            list.add("Sector " + sector);
        }
        return list.stream().collect(Collectors.joining(", "));
    }
}
