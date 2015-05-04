package geneticsHomework;

/**
 * Abstract base class for carnivorous Animals that eat other Animals
 * @author Peter Dong
 *
 */
abstract public class Carnivore extends Animal {

	private Animal prey = null;
	
	/**
	 * Sets the prey that the Carnivore will eat
	 * @param target - the target Animal
	 */
	public void setPrey(Animal target) {
		prey = target;
	}
	
	/**
	 * @param target The Animal to examine
	 * @return Whether this Animal is a prey of this Carnivore
	 */
	abstract protected boolean isPrey(Animal target);

	@Override
	protected double doEating() {
		if (prey == null || !isPrey(prey)
				|| prey.getCell().getX() != getCell().getX() || prey.getCell().getY() != getCell().getY()) {
			return 0;
		}
		
		double nutrition = prey.energyAsFood();
		prey.die();
		prey = null;
		return nutrition;		
	}

}
