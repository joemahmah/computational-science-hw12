package geneticsHomework;

/**
 * The basic class for grass-eating Animals that eat from FoodCells
 * 
 * @author Peter Dong
 *
 */
abstract public class Herbivore extends Animal {
	
	@Override
	protected double doEating() {
		return getCell().eatFood(this.dailyEnergyMax());
	}
	

}
