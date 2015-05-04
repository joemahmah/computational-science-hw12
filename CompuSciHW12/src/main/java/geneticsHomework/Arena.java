package geneticsHomework;

import java.awt.Color;
import java.awt.Graphics;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.Iterator;

/**
 * @author Peter Dong
 * 
 * This is a container that holds the Cells in the simulation and governs their general behavior.
 * It calls the Cells each turn and tells them to do their work.
 *
 */
public class Arena implements Drawable {

	private int xsize;
	private int ysize;

	private Map<Coord, Cell> map = new TreeMap<Coord, Cell>();

	private FileWriter file;

//	static private int randomSeed = 0;
	static private Random ran = new Random();
	/**
	 * @return the random number generator that all classes should use
	 */
	static public Random getRandom() {
		return ran;
	}
	static public void setSeed(long seed) {
		ran = new Random(seed);
	}

	private boolean printout = false;

	// This is used for the countAnimals function
	private Map<String, Integer> aniMap = new HashMap<String, Integer>();

	private Map<String, Color> colorMap = new HashMap<String, Color>();

	private Map<String, Integer> finalMap = new TreeMap<>();

	private List<String> herbivoreNames = new ArrayList<>();

	public boolean isHerbivore(String name) {
		return herbivoreNames.contains(name);
	}

	/**
	 * @author Peter Dong
	 * 
	 * This is a small private class that is used to easily store coordinates for a 2 x 2 grid.
	 * The cool thing is, it doesn't even need accessors for this usage.
	 */
	private class Coord implements Comparable<Coord> {

		private int x;
		private int y;

		public Coord(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object arg0) {
			if (arg0 == this)
				return true;

			if (arg0 == null || !(arg0 instanceof Coord)) {
				return false;
			}
			Coord icoor = (Coord)arg0;
			return (x == icoor.x && y == icoor.y);
		}

		@Override
		public int compareTo(Coord icoord) {
			if (x == icoord.x) {
				if (y == icoord.y) {
					return 0;
				}
				else if (y < icoord.y) {
					return -1;
				}
				else {
					return 1;
				}
			}
			else if (x < icoord.x) {
				return -1;
			}
			else {
				return 1;
			}
		}
	}

	/**
	 * @param xsize - the size of the arena, in Cells
	 * @param ysize - the size of the arena, in Cells
	 */
	public Arena(int xsize, int ysize, int cellSize) {
		this.xsize = xsize;
		this.ysize = ysize;
		Cell.setSize(cellSize);
		initialize();
	}

	/**
	 * Tells the Arena whether or not to print the status to the standard output
	 * @param print - whether or not to print out the status
	 */
	public void setPrintout(boolean print) {
		printout = print;
	}

	/**
	 * This attaches a file to the Arena for output
	 * @param file - a FileWriter object, ready to use
	 */
	public void setFile(FileWriter file) {
		this.file = file;
	}

	@Override
	public void Draw(Graphics graph, int x, int y) {
		for (Coord icoord : map.keySet()) {
			map.get(icoord).Draw(graph, x + icoord.x * map.get(icoord).getXSize(),
					y + icoord.y * map.get(icoord).getYSize());
		}
	}

	@Override
	public int getXSize() { // This assumes that all cells are the same size!
		return getCell(0, 0).getXSize() * (getXDim() + 1);
	}

	@Override
	public int getYSize() {
		return getCell(0, 0).getYSize() * (getYDim() + 2);
	}

	/**
	 * @return the number of Cells in the Arena, in the x direction
	 */
	public int getXDim() {
		return xsize;
	}

	/**
	 * @return the number of Cells in the Arena, in the y direction
	 */
	public int getYDim() {
		return ysize;
	}

	/**
	 * This is called by the constructor to fill the map with default Cells
	 */
	protected void initialize() {
		for (int ix = 0; ix < xsize; ++ix) {
			for (int iy = 0; iy < ysize; ++iy) {
				map.put(new Coord(ix, iy), new Cell(this, ix, iy));
			}
		}
	}

	/**
	 * Use this function to change the Cells used with inherited ones.
	 * 
	 * @param x - the position of the Cell
	 * @param y - the position of the Cell
	 * @param newCell - the new Cell to replace the old one
	 */
	public void changeCell(int x, int y, Cell newCell) {
		Coord coord = new Coord(x, y);
		map.put(coord, newCell);
	}

	/**
	 * Adds an Animal to a particular location
	 * 
	 * @param x - the location of the Animal
	 * @param y - the location of the Animal
	 * @param an - the Animal to be added
	 */
	public void addAnimal(int x, int y, Animal an) {
		getCell(x, y).addAnimal(an);

		if (!colorMap.containsKey(an)) {
			colorMap.put(an.getName(), an.getColor());
			if (an instanceof Herbivore) {
				herbivoreNames.add(an.getName());
			}
			updateMap();
		}
	}

	/**
	 * Adds an Animal to a random location on the map.
	 * 
	 * @param an - the Animal to be added
	 */
	public void addRandomAnimal(Animal an) {
		int x = ran.nextInt(getXDim());
		int y = ran.nextInt(getYDim());
		addAnimal(x, y, an);
	}

	/**
	 * @param x - the location of the Cell
	 * @param y - the location of the Cell
	 * @return - the Cell at that location
	 */
	public Cell getCell(int x, int y) {
		return map.get(new Coord(x, y));
	}

	/**
	 * The primary method of the Arena, called each turn by the Viewer.
	 * It calls each Cell in turn.
	 */
	public boolean doTurn() {
		for (Cell icell : map.values()) {
			icell.beginningOfTurn();
		}

		for (Cell icell : map.values()) {
			icell.doTurn();
		}

		updateMap();
		updateFinal();

		if (printout) {
			System.out.println(countAnimals());
		}

		if (checkStillGoing()) {
			return true;
		} else {
			outputFinal();
			return false;
		}
	}

	static final int UPPER_LIMIT = 200000;

	private boolean checkStillGoing() {
		if (countTotalAnimals() <= 0) {
			return false;
		} else {
			for (Integer i : aniMap.values()) {
				if (i > UPPER_LIMIT) {
					return false;
				}
			}
		}
		return true;
	}

	private int countTotalAnimals() {
		int total = 0;

		for (Integer i : aniMap.values()) {
			total += i;
		}

		return total;
	}

	private void updateFinal() {
		for (Entry<String, Integer> ent : aniMap.entrySet()) {
			if (finalMap.containsKey(ent.getKey())) {
				finalMap.put(ent.getKey(), finalMap.get(ent.getKey()) + ent.getValue());
			} else {
				finalMap.put(ent.getKey(), ent.getValue());
			}
		}
	}

	private void outputFinal() {
		if (file != null) {
			for (Entry<String, Integer> ent : finalMap.entrySet())
				try {
					file.write(ent.getKey() + '\t' + ent.getValue() + '\t');
				} catch (IOException e) {
					// If there is no valid file attached, just stop
					return;
				}		
			try {
				file.write('\n');
			} catch (IOException e) {
				return;
			}
		}
	}

	public void close() {
		if (file != null) {
			try {
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return A String listing the number of each different type of Animal,
	 * for easy display by the Viewer 
	 */
	public String countAnimals() {
		String response = "";

		List<String> myset = new LinkedList<String>(aniMap.keySet());
		Collections.sort(myset);
		Iterator<String> it = myset.iterator();

		while (it.hasNext()) {
			String thisOne = it.next();
			response = response.concat(thisOne + " " + aniMap.get(thisOne) + "   "); 
		}

		return response;
	}

	/**
	 * This function updates the map used by getAnimalCount() and countAnimals()
	 */
	private void updateMap() {
		for (String str : aniMap.keySet()) {
			aniMap.put(str, 0);
		}

		for (Cell icell : map.values()) {
			icell.countAnimals(aniMap);
		}
	}


	/**
	 * Gives access to count of each Animal, mainly for LeaderBoard
	 * @return the map of how many of each type of Animal
	 */
	public Map<String, Integer> getAnimalCount() {
		return aniMap;
	}

	/**
	 * Returns the color associated with a given Animal.
	 * @param animal - the Animal to find the color of
	 * @return the Animal's color
	 */
	public Color getAnimalColor(String animal) {
		return colorMap.get(animal);
	}

}
