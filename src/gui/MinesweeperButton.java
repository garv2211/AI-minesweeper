package gui;

import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Created by jonsteinn on 7.3.2017.
 *
 * A single minesweeper clickable square.
 */
public class MinesweeperButton extends Button {

    private int x;
    private int y;
    private boolean clicked;

    /**
     * Constructor. Sets events.
     *
     * @param x coordinate
     * @param y coordinate
     */
    public MinesweeperButton(int x, int y) {
        this.x = x;
        this.y = y;
        this.clicked = false;
        // main button
        this.setOnAction(event -> Controller.controller.buttonUpdate(this, this.x, this.y));
        // secondary button
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY && !clicked) Controller.controller.markBomb(this);
        });
        this.setMaxWidth(30);
        this.setMinWidth(30);
        this.setMaxHeight(30);
        this.setMinHeight(30);
    }

    /**
     * @return true iff the button has already been clicked.
     */
    public boolean isDown() {
        return this.clicked;
    }

    /**
     * Sets the button as 'already clicked'.
     */
    public void click() {
        this.clicked = true;
    }
}
