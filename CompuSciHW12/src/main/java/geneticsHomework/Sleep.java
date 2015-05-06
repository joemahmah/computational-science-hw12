/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticsHomework;

/**
 *
 * @author mhrcek
 */
public class Sleep extends Turn{

    /**
     * Sleep is a turn state which will not consume energy as metabolic energy is
     * already factored in to the simulation.
     * 
     * @param animal
     * @return 
     */
    @Override
    protected double energyConsumption(Animal animal) {
        return 0;
    }

    @Override
    protected boolean doTurn(Animal animal) {
        animal.sleep();
        return true;
    }
    
}
