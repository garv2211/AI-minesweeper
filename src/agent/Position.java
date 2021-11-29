package agent;

/**
 * Created by Jonni on 3/20/2017.
 *
 * A representation of a position on the board for java collections.
 */
public class Position {

    /**
     * Instance creator from string.
     *
     * @param positionString String format for point, '(x,y)'
     * @return instance of position
     */
    public static Position fromString(String positionString) {
        // This is just used in tests, to convert output into objects.
        String[] numbers = positionString.split(",");
        return new Position(
                Integer.parseInt(numbers[0].substring(1, numbers[0].length())),
                Integer.parseInt(numbers[1].substring(0, numbers[1].length() - 1))
        );
    }

    private int x;
    private int y;

    /**
     * @param x horizontal coordinate
     * @param y vertical coordinate
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return horizontal coordinate
     */
    public int getX() {
        return this.x;
    }

    /**
     * @return vertical coordinate
     */
    public int getY() {
        return this.y;
    }

    @Override
    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }

    @Override
    public boolean equals(Object o) {
        Position other = (Position)o;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return this.x * 31 + this.y;
    }
}
