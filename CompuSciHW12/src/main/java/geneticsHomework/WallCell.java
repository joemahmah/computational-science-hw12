package geneticsHomework;

import java.awt.Color;

public class WallCell extends Cell {

    private static final boolean PASSABLE = false;
    
    public WallCell(Arena map, int x, int y) {
        super(map, x, y);
    }

    @Override
    public boolean isMoveable() {
        if (getMap().getViewer().getSeason() == Season.WINTER && (getMap().getViewer().getDay() / 360) % 5 == 0 && PASSABLE) {
            return true;
        } else{
            return false;
        }
    }

    @Override
    protected Color getColor() {
        return Color.DARK_GRAY;
    }

}
