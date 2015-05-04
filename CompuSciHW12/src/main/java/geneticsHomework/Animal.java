package geneticsHomework;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This is the basic Animal class that must live in an Arena.  Inherit from this class to overload essential functions.
 * 
 * @author Peter Dong
 */
abstract public class Animal implements Drawable {

	private Cell current;
	
	private boolean dead = false;
	private boolean performedAction = false;
	
	// This determines the width of the border around the animal when drawn
	static private final double sizeScale = .15;

	private int age = 0;
	
	private double energyReserve = 0;
	
	private int gestationElapsed = 0;
	private List<Animal> fetus = new ArrayList<>();
	
	private Genotype genotype;
	
	static public Animal makeRandomAnimal(Class<? extends Animal> type) {
		Animal newAnimal = createAnimal(type);
		newAnimal.addGenotype(newAnimal.randomGenotype());
		return newAnimal;
	}
	
	static private Animal createAnimal(Class<? extends Animal> type) {
		Animal newAnimal;
		try {
			newAnimal = type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Constructor cannot have any arguments!");
		}
		return newAnimal;
	}
	
	static public Animal makeAnimal(Class<? extends Animal> type, Genotype gen) {
		Animal newAnimal = createAnimal(type);
		newAnimal.addGenotype(gen);
		return newAnimal;
	}
	
	protected Animal() {
		energyReserve = initialEnergy();
	}
	
	private void addGenotype(Genotype genotype) {
		this.genotype = genotype;
	}
	
	/**
	 * @return The amount of energy the animal consumes per turn just by its basal metabolic processes
	 */
	abstract protected double metabolicConsumption();
	/**
	 * @return The maximum amount of energy an animal can store.
	 */
	abstract protected double maxEnergyStorage();
	
	/**
	 * @return How many days it takes to be able to mate
	 */
	abstract protected int ageOfSexualMaturity();
	/**
	 * @return How long it takes to bear a litter
	 */
	abstract protected int gestationTime();
	
	/**
	 * @return The size of a litter.  This may fluctuate if you want; it will be called once per mating session
	 */
	abstract protected int litterSize();
	
	/**
	 * @return The amount of energy an Animal starts with
	 */
	abstract protected double initialEnergy();
	
	/**
	 * @return The amount of energy this Animal would give a predator when devoured
	 */
	abstract protected double energyAsFood();
	
	/**
	 * @return The maximum amount of food an Animal can eat in a day
	 */
	abstract protected double dailyEnergyMax();
	
	/**
	 * @return The amount of energy required to mate
	 */
	abstract public double energyToMate();
	/**
	 * @return The amount of energy required to move to another location
	 */
	abstract public double energyToMove();
	/**
	 * @return The amount of energy required to forage for, catch, and eat food
	 */
	abstract public double energyToEat();
	
	/**
	 * This allows Animals to move multiple times in each turn.  Return 1 for default behavior.
	 * @return number of moves per turn the Animal gets
	 */
	abstract protected int getNTurns();
	
	/**
	 * This allows each animal to be its own color, or to determine its color dynamically.
	 * @return the color in which the Animal is drawn on the map. 
	 */
	abstract protected Color getColor();
	
	// This draws a box in the cell with a border, with a color determined by getColor()
	@Override
	public void Draw(Graphics graph, int x, int y) {
		if (isDead()) {
			graph.setColor(getColor().darker());
		} else {
			graph.setColor(getColor());
		}
		
		int offset = (int)(getXSize() * sizeScale);
		
		if (isDead()) {
			graph.fillOval(x + offset, y + offset, getXSize() - (2 *offset - 1), getYSize() - (2 * offset - 1));	
		} else {
			graph.fillRect(x + offset, y + offset, getXSize() - (2 *offset - 1), getYSize() - (2 * offset - 1));
		}		
	}

	@Override
	public int getXSize() {
		return current.getXSize();
	}

	@Override
	public int getYSize() {
		return current.getYSize();
	}
	
	/**
	 * This gives the name of the species which inherits from Animal, for display purposes
	 * @return name of the species
	 */
	abstract public String getName();
	
	protected Genotype getGenotype() {
		return genotype;
	}
		
	/**
	 * This returns the cell where the Animal is currently located 
	 * @return cell
	 */
	public Cell getCell() {
		return current;
	}
	
	/**
	 * @return the current Arena in which the Animal resides
	 */
	public Arena getArena() {
		return current.getMap();
	}

	/**
	 * This sets the Animal's position.  Should not usually be used except in initialization; move() is a better function for general use
	 * @param cell - the Animal's new location
	 */
	public void setCell(Cell cell) {
		current = cell;
	}
	
	/**
	 * Safely and properly moves the Animal to a new location in a way that keeps track of all the relevant pointing properties.
	 * @param newCell - the new location of the Animal
	 */
	public boolean move(Cell newCell) {
		if (newCell.isMoveable()) {
			current.move(this, newCell);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * The main function of an Animal - every turn, the Arena calls this on each Animal.  Each turn has a beginning,
	 * a middle which repeats Nturns times, and an end.
	 */
	public void doTurn() {
		beginningOfTurn();
		
		for (int iturn = 0; iturn < getNTurns(); ++iturn)
		{
			chooseMove();
		}
		endOfTurn();
		performedAction = true;
	}

	
	/**
	 * This makes sure an Animal dies if it is out of food.
	 */
	protected void beginningOfTurn() {
		if (energyReserve < 0) {
			die();
		}		
	}

	/**
	 * This delivers a child if gestation time is complete, and otherwise passes control to the inherited class 
	 */
	protected void chooseMove() {
		if (isPregnant() && gestationElapsed >= gestationTime()) {
			makeChild();
		} else {			
			Turn myturn = userDefinedChooseMove();
			if (myturn != null) {
				myturn.turn(this);
			}
		}
	}
	
	/**
	 * This class is where the inherited Animal decides what to do next.
	 * @return the Turn chosen by the inherited Animal
	 */
	abstract protected Turn userDefinedChooseMove();

	/**
	 * This increases age and gestation time and also reduces the energy of the Animal each turn.
	 */
	protected void endOfTurn() {
		++age;		
		if (isPregnant()) {
			++gestationElapsed;
		}
		energyReserve -= metabolicConsumption();
	}
	
	/**
	 * @return the Animal's current amount of energy.
	 */
	protected double getEnergyReserve() {
		return energyReserve;
	}
	
	/**
	 * Adds energy to the Animal
	 * @param energy - the amount of energy to add
	 */
	protected void addEnergy(double energy) {
		energyReserve += energy;
		double maxEn = maxEnergyStorage();
		if (energyReserve > maxEn) {
			energyReserve = maxEn;
		}
	}
	
	protected void removeEnergy(double energy) {
		energyReserve -= energy;
	}
	
	/**
	 * @return the Animal's current age, in days
	 */
	protected int getAge() {
		return age;
	}
	
	/**
	 * @return the number of days this Animal has been with child
	 */
	protected int getGestationTime() {
		return gestationElapsed;
	}
	
	/**
	 * @return whether the Animal is able to procreate
	 */
	public boolean sexuallyMature() {
		return age >= ageOfSexualMaturity();
	}
	
	/**
	 * Tells the Animal to eat, and makes sure food does not exceed the maximum possible
	 * @return whether the Animal actually ate anything.
	 */
	public boolean eat() {
		double food = doEating();
		
		addEnergy(food);
		
		return food > 0;
	}
	
	/**
	 * The Animal-specific method of eating, overridden by Carnivore and Herbivore
	 * @return the amount of food consumed by the Animal
	 */
	abstract protected double doEating();
	
	/**
	 * @return whether the Animal is male
	 */
	public boolean isMale() {
		return genotype.getGene(GeneType.MALE);
	}
	
	/**
	 * @return whether the Animal is pregnant
	 */
	public boolean isPregnant() {
		return !fetus.isEmpty();
	}
	
	/**
	 * Creates a new child
	 */
	private void makeChild() {
		for (Animal ani : fetus) {
			getCell().addAnimal(ani);
		}
		fetus.clear();
		gestationElapsed = 0;
	}
	
	/**
	 * Mates with another Animal to create an offspring 
	 * @param other - the other Animal
	 * @return whether mating was successful
	 */
	protected boolean mate(Animal other) {
		if (!checkMateability(other)) {
			return false;
		}
		
		int nKids = litterSize();
		for (int i = 0; i < nKids; ++i) {
			fetus.add(createOffspring(other));
		}

		return true;
	}
	
	/**
	 * Checks whether another Animal can be mated with
	 * @param other - the other Animal
	 * @return whether it is possible to mate
	 */
	protected boolean checkMateability(Animal other) {
		return sexuallyMature() && other.sexuallyMature() && !isPregnant() && !other.isPregnant()
				&& isMale() != other.isMale() && checkType(other)
				&& getCell().getX() == other.getCell().getX() && getCell().getY() == other.getCell().getY();
	}
	
	/**
	 * This determines whether the other animal is of the same type as the current one
	 * @param other - the other animal to compare to
	 * @return whether or not they are the same type of animal
	 */
	protected boolean checkType(Animal other) {
		return other.getClass() == this.getClass();
	}
	
	/**
	 * Creates a new Animal as a result of mating
	 * @return the new Animal
	 */
	protected Animal createOffspring(Animal other) {
		try {
			Genotype gen = new Genotype(getGenotype().meiosis(), other.getGenotype().meiosis());
			Animal returntype = this.getClass().newInstance();
			returntype.addGenotype(gen);
			return returntype;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException ("This shouldn't be possible!");
			// The try-catch is necessary because of Java type-safety rules, even though this should be foolproof
		}
	}
	
	protected Genotype randomGenotype() {
		List<Gene> fatherGenes = new ArrayList<>();
		List<Gene> motherGenes = new ArrayList<>();
		
		randomGenes(fatherGenes);
		randomGenes(motherGenes);
		
		GeneSet father = new GeneSet(fatherGenes);
		GeneSet mother = new GeneSet(motherGenes);
		return new Genotype(father, mother);
	}
	
	/**
	 * @return The proper genotype of this Animal, but with alleles randomized.
	 */
	protected abstract void randomGenes(List<Gene> genes);

	
	/**
	 * Resets the Animal at the beginning of each turn.
	 */
	protected void reset() {
		performedAction = false;
	}
	
	/**
	 * Call this function to kill the Animal.
	 * The Arena will automatically remove any dead Animals at the beginning of the next turn.
	 */
	protected void die() {
		dead = true;
	}
	
	/**
	 * @return whether the Animal is dead. 
	 */
	public boolean isDead() {
		return dead;
	}
	
	/**
	 * This moves the Animal one square in the indicated direction, if possible.
	 * @param dir - The direction that the Animal is moving  
	 * @return - Whether the move was actually possible
	 */
	public boolean makeMove(Direction dir) {
		if (dir == null) return false;
		switch(dir) {

		case LEFT:
			if (current.getX() > 0) {
				return move(current.getMap().getCell(current.getX() - 1, current.getY()));
			} else {
				return false;
			}

		case UP:
			if (current.getY() > 0) {
				return move(current.getMap().getCell(current.getX(), current.getY() - 1));
			} else {
				return false;
			}

		case RIGHT:
			if (current.getX() < current.getMap().getXDim() - 1) {
				return move(current.getMap().getCell(current.getX() + 1, current.getY()));
			} else {
				return false;
			}

		case DOWN:
			if (current.getY() < current.getMap().getYDim() - 1) {
				return move(current.getMap().getCell(current.getX(), current.getY() + 1));
			} else {
				return false;
			}

		default:
			throw new RuntimeException("Reached unreachable code in makeMove()!");

		}
	}
	
	/**
	 * This is needed to avoid double-counting Animals that move during their turn.
	 * @return whether the Animal has already performed its Action this turn.
	 */
	public boolean performedAction() {
		return performedAction;
	}
	
	/**
	 * @return the random number generator used for this class
	 */
	static protected Random getRandom() {
		return Arena.getRandom();
	}
}
