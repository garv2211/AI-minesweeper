package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import level.Board;

/**
 * Created by jonsteinn on 6.3.2017.
 *
 * Data structure for buttons as well as a layout unit for them.
 */
public class BoardButtons extends GridPane {

    private MinesweeperButton[][] access;

    /**
     * Constructor. Creates buttons for a given board.
     *
     * @param board minesweeper board.
     */
    public BoardButtons(Board board) {
        setPadding(new Insets(5, 5, 5, 5));
        this.setAlignment(Pos.CENTER);
        this.access = new MinesweeperButton[board.getWidth()][board.getHeight()];
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                MinesweeperButton button = new MinesweeperButton(i,j);
                this.add(button, i, j);
                this.access[i][j] = button;
            }
        }
    }

    /**
     * Getter for data structure.
     *
     * @param x coordinate
     * @param y coordinate
     * @return A button at (x,y).
     */
    public MinesweeperButton get(int x, int y) {
        return this.access[x][y];
    }
}
