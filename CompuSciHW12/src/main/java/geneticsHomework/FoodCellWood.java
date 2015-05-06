/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticsHomework;

import java.awt.Color;

/**
 *
 * @author mhrcek
 */
public class FoodCellWood extends FoodCell {

    public FoodCellWood(Arena map, int x, int y) {
        super(map, x, y);
        this.FOOD_MAX = 30000;
        this.GROWTH_PER_TURN = (double) FOOD_MAX / 200.0;
    }

    @Override
    protected Color getColor() {
        double brightness = 1 - ((double) foodAmount / FOOD_MAX * 200 / 240);
        float color = 75.0f/240;
        
        if(getMap().getViewer().getSeason() == Season.WINTER){
            color = 180f/360;
        } else if(getMap().getViewer().getSeason() == Season.FALL){
            color = 45f/360;
        }
        
        return Color.getHSBColor(color, 175.0f / 240, (float) (brightness));
    }

    @Override
    public void adjustForSeason() {
        Season currentSeason = getMap().getViewer().getSeason();
        
        switch(currentSeason){
            case WINTER:
                GROWTH_PER_TURN = 0;
                break;
            case SPRING:
                GROWTH_PER_TURN = FOOD_MAX/80;
                break;
            case SUMMER:
                GROWTH_PER_TURN = FOOD_MAX/250;
            case FALL:
                GROWTH_PER_TURN = FOOD_MAX/1000;
        }
    }

}
