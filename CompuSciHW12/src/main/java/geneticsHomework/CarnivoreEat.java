package geneticsHomework;

/**
 * The Eat command for Carnivores, who need to specify an Herbivore to eat.
 * 
 * @author Peter Dong
 *
 */
public class CarnivoreEat extends Eat {

	private Animal prey;
 	
	public CarnivoreEat(Animal prey) {
		this.prey = prey;
	}

	@Override
	public boolean doTurn(Animal animal) {
		Carnivore myCarn = (Carnivore)animal;
		myCarn.setPrey(prey);
		return myCarn.eat();
	}
	
	

}
