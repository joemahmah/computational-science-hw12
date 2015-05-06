package geneticsHomework;

/**
 * A quick enum to send movement commands to the Animal.
 *
 * @author Peter Dong
 */
public enum Direction {

    LEFT, RIGHT, UP, DOWN;

    public static Direction rand() {
        return Direction.values()[Arena.getRandom().nextInt(4)];
    }
}
