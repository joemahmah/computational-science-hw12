package geneticsHomework;

import java.awt.Color;
import java.util.List;

public class Hare extends Herbivore {

    public Hare() {
        super();
        metabolicConsumption = 500;
    }

    @Override
    protected double metabolicConsumption() {
        return metabolicConsumption;
    }

    @Override
    protected double maxEnergyStorage() {
        
        if(getGenotype().getGene(GeneType.EXTREME_STORAGE)){
            return metabolicConsumption * 90;
        }
        
        return metabolicConsumption * 60; //30000 Calories (~20 lbs fat)
    }

    @Override
    protected double maxEnergyStoragePerDay() {
        return metabolicConsumption * 10;
    }

    @Override
    protected int ageOfSexualMaturity() {
        return YEAR / 2; //1 Year Old 
    }

    @Override
    protected int gestationTime() {
        
        if(getGenotype().getGene(GeneType.FAST_BREEDER)){
            return (getRandom().nextInt(6) + 25); // 25 - 30 days
        }
        
        return (getRandom().nextInt(6) + 35); //35 - 40 days
    }

    @Override
    protected int litterSize() {
        
        if(getGenotype().getGene(GeneType.MASS_BREEDER)){
            return (getRandom().nextInt(5) + 3); // 3 - 7 hares
        }
        
        return (getRandom().nextInt(3) + 2); //2 - 4 hares
    }

    @Override
    protected double initialEnergy() {
        return metabolicConsumption * (getRandom().nextInt(3) + 5); //5 - 8 days of no food
    }

    @Override
    protected double energyAsFood() {
        
        if(getGenotype().getGene(GeneType.EXTRA_TASTY)){
            return (8 * 3500 + getEnergyReserve());
        }
        
        return (4 * 3500 + getEnergyReserve());
    }

    @Override
    protected double turnEnergyMax() {
        
        if(getGenotype().getGene(GeneType.SLOW_EATER)){
            return metabolicConsumption * .75;
        }
        
        return metabolicConsumption * 1.25;
    }

    @Override
    public double energyToMate() {
        return metabolicConsumption / 2;
    }

    @Override
    public double energyToMove() {
        return metabolicConsumption / 6;
    }

    @Override
    public double energyToEat() {
        return metabolicConsumption / 4;
    }

    @Override
    public String getName() {
        if (getColor() == Color.RED) {
            return "Hare (Red)";
        } else if (getColor() == Color.BLUE) {
            return "Hare (Blue)";
        } else if (getColor() == Color.WHITE) {
            return "Hare (White)";
        }
        return "Hare (Black)";
    }

    @Override
    protected Turn userDefinedChooseMove() {
        List<Animal> others = getCell().getOtherAnimals(this);
        for (Animal ani : others) {
            if (checkMateability(ani) && (getArena().getViewer().getSeason() == Season.SPRING)) {
                return new Mate(ani);
            }
        }

        if ((getCell().howMuchFood() > energyToEat() && (getConsumptionThisDay() / maxEnergyStoragePerDay()) < 1 && getEnergyReserve() / maxEnergyStorage() < (1.0 - 1.0 / turnEnergyMax())) || getEnergyReserve() / maxEnergyStorage() < .05) {
            return new HerbivoreEat();
        }

        int xPos = getCell().getX();
        int yPos = getCell().getY();
        double foodLeft = 0, foodRight = 0, foodUp = 0, foodDown = 0;

        if (xPos > 0) {
            foodLeft += getArena().getCell(xPos - 1, yPos).howMuchFood();
        }

        if (xPos < getArena().getXDim() - 1) {
            foodRight += getArena().getCell(xPos + 1, yPos).howMuchFood();
        }

        if (yPos > 0) {
            foodUp += getArena().getCell(xPos, yPos - 1).howMuchFood();
        }

        if (yPos < getArena().getYDim() - 1) {
            foodDown += getArena().getCell(xPos, yPos + 1).howMuchFood();
        }

        double[] dirValues = {foodLeft, foodRight, foodUp, foodDown};
        int max = 0;
        for (int i = 0; i < dirValues.length; i++) {
            if (dirValues[i] > dirValues[max]) {
                max = i;
            }
        }

        if (dirValues[max] > energyToEat() + energyToMove()) {
            return new Move(Direction.values()[max]);
        }

        if (Arena.getRandom().nextBoolean()) {
            return new Move(Direction.rand());
        } else {
            return new Sleep();
        }
    }

    @Override
    protected int getNTurns() {
        return turnsPerDay;
    }

    @Override
    protected Color getColor() {
        if (getGenotype().getGene(GeneType.COLOR_RED)) {
            if (getGenotype().getGene(GeneType.COLOR_BLUE)) {
                return Color.WHITE;
            } else {
                return Color.RED;
            }
        } else {
            if (getGenotype().getGene(GeneType.COLOR_BLUE)) {
                return Color.BLUE;
            } else {
                return Color.BLACK;
            }
        }
    }

    @Override
    protected void randomGenes(List<Gene> genes) {
        genes.add(new Gene(GeneType.MALE, Arena.getRandom().nextBoolean()));
        genes.add(new Gene(GeneType.COLOR_RED, Arena.getRandom().nextBoolean()));
        genes.add(new Gene(GeneType.COLOR_BLUE, Arena.getRandom().nextBoolean()));
        genes.add(new Gene(GeneType.EXTRA_TASTY, Arena.getRandom().nextBoolean()));
        genes.add(new Gene(GeneType.EXTREME_STORAGE, Arena.getRandom().nextBoolean()));
        genes.add(new Gene(GeneType.FAST_BREEDER, Arena.getRandom().nextBoolean()));
        genes.add(new Gene(GeneType.MASS_BREEDER, Arena.getRandom().nextBoolean()));
        genes.add(new Gene(GeneType.SLOW_EATER, Arena.getRandom().nextBoolean()));
    }

}
