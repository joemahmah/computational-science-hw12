package geneticsHomework;

public class Boot {

	/**
	 * Driver class for ecology simulation
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */

	private static void addAnimal(Arena arena, int number, Class<? extends Animal> type) throws InstantiationException, IllegalAccessException {
		for (int i = 0; i < number; ++i) {
			Animal newAnimal = type.newInstance();
			arena.addRandomAnimal(newAnimal);
		}
	}
	
	private static final double TIME_INCREMENT = 1;
	private static final int TIMER_SPEED = 50;
	
	public static void main(String args[]) throws InstantiationException, IllegalAccessException {

		// These control the size of the grid
		final int xSize = 30;
		final int ySize = 30;
		final int cellSize = 10;

		final int nHerbivore = 30;
		
		Arena mymap = new Arena(xSize, ySize, cellSize);

		for (int ix = 0; ix < mymap.getXDim(); ++ix) {
			for (int iy = 0; iy < mymap.getYDim(); ++iy) {
					mymap.changeCell(ix, iy, new FoodCell(mymap, ix, iy));
			}
		}
		
		addAnimal(mymap, nHerbivore, Hare.class);

		Viewer.runViewer(mymap, TIME_INCREMENT, TIMER_SPEED);
	}

}
