package geneticsHomework;

import java.awt.Color;
import java.util.List;

public class Hare extends Herbivore {

	@Override
	protected double metabolicConsumption() {
		return 1;
	}

	@Override
	protected double maxEnergyStorage() {
		return 1;
	}

	@Override
	protected int ageOfSexualMaturity() {
		return 1;
	}

	@Override
	protected int gestationTime() {
		return 1;
	}

	@Override
	protected int litterSize() {
		return 1;
	}

	@Override
	protected double initialEnergy() {
		return 1;
	}

	@Override
	protected double energyAsFood() {
		return 1;
	}

	@Override
	protected double dailyEnergyMax() {
		return 1;
	}

	@Override
	public double energyToMate() {
		return 1;
	}

	@Override
	public double energyToMove() {
		return 1;
	}

	@Override
	public double energyToEat() {
		return 1;
	}

	@Override
	public String getName() {
		return "Hare";
	}

	@Override
	protected Turn userDefinedChooseMove() {
		List<Animal> others = getCell().getOtherAnimals(this);
		for (Animal ani : others) {
			if (checkMateability(ani)) {
				return new Mate(ani);
			}
		}
		
		if (getCell().howMuchFood() > 1) {
			return new HerbivoreEat();
		} else {
			int ran = getRandom().nextInt(4);
			if (ran == 0) {
				return new Move(Direction.DOWN);
			} else if (ran == 1) {
				return new Move(Direction.UP);
			} else if (ran == 2) {
				return new Move(Direction.LEFT);
			} else {
				return new Move(Direction.RIGHT);
			}
		}
	}

	@Override
	protected int getNTurns() {
		return 1;
	}

	@Override
	protected Color getColor() {
		return Color.RED;
	}

	@Override
	protected void randomGenes(List<Gene> genes) {
		genes.add(new Gene(GeneType.MALE, Arena.getRandom().nextBoolean()));
	}


}
