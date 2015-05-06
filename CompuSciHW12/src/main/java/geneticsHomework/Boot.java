package geneticsHomework;

public class Boot {

    /**
     * Driver class for ecology simulation
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static void addAnimal(Arena arena, int number, Class<? extends Animal> type) throws InstantiationException, IllegalAccessException {
        for (int i = 0; i < number; ++i) {
            Animal newAnimal = Animal.makeRandomAnimal(type);//type.newInstance();
            newAnimal.randAge();
            arena.addRandomAnimal(newAnimal);
        }
    }

    private static final double TIME_INCREMENT = 1;
    private static final int TIMER_SPEED = 50;

    public static void main(String args[]) throws InstantiationException, IllegalAccessException {

        // These control the size of the grid
        final int xSize = 65;
        final int ySize = 45;
        final int cellSize = 18;

        final int nHerbivore = 60;

        Arena mymap = new Arena(xSize, ySize, cellSize);

        for (int ix = 0; ix < mymap.getXDim(); ++ix) {
            for (int iy = 0; iy < mymap.getYDim(); ++iy) {
                if (Animal.getRandom().nextInt(100) < 15) {
                    mymap.changeCell(ix, iy, new FoodCellWood(mymap, ix, iy));
                } else {
                    mymap.changeCell(ix, iy, new FoodCellGrass(mymap, ix, iy));
                }
            }
        }

        for (int iy = 0; iy < mymap.getYDim(); iy++) {
            mymap.changeCell(mymap.getXDim() / 2, iy, new WallCell(mymap, mymap.getXDim() / 2, iy));
        }

        for (int ix = 0; ix < mymap.getXDim(); ix++) {
            mymap.changeCell(ix, mymap.getYDim() / 2, new WallCell(mymap, ix, mymap.getYDim() / 2));
        }

        addAnimal(mymap, nHerbivore, Hare.class);

        Viewer.runViewer(mymap, TIME_INCREMENT, TIMER_SPEED);
    }

}
