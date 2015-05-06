package geneticsHomework;

import java.awt.Color;

/**
 * A child class of Cell which includes food that grows back over time
 *
 * @author Peter Dong
 */
public abstract class FoodCell extends Cell {

    protected double foodAmount;
    protected double GROWTH_PER_TURN = 100;
    protected double FOOD_MAX = 10000;

    /**
     * @param map - the Arena that the Cell belongs to
     * @param x - the x coordinate of the Cell
     * @param y - the y coordinate of the Cell
     * @param food - the amount of food the Cell begins with
     */
    public FoodCell(Arena map, int x, int y) {
        super(map, x, y);
    }

    @Override
    public double howMuchFood() {
        return foodAmount;
    }

    public abstract void adjustForSeason();

    @Override
    public void beginningOfTurn() {
        
        if(!(foodAmount <= 0) && !(foodAmount > 0)){
            foodAmount = FOOD_MAX * Arena.getRandom().nextDouble();
        }
        
        adjustForSeason();
        foodAmount += GROWTH_PER_TURN * (Arena.getRandom().nextDouble() + .5); //Fluxuates growth rates
        if (foodAmount > FOOD_MAX) {
            foodAmount = FOOD_MAX;
        }
        super.beginningOfTurn();
    }

    @Override
    protected Color getColor() {
        double brightness = 1 - ((double) foodAmount / FOOD_MAX * 165 / 240);
        return Color.getHSBColor(75.0f / 240, 175.0f / 240, (float) (brightness));
    }

    @Override
    public double eatFood(double amount) {
        if (amount <= foodAmount) {
            foodAmount -= amount;
            return amount;
        } else {
            double temp = foodAmount;
            foodAmount = 0;
            return temp;
        }
    }
}
