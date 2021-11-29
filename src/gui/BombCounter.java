package gui;

import javafx.scene.control.Label;

/**
 * Created by Jonni on 3/19/2017.
 *
 * Label for remaining bombs.
 */
public class BombCounter extends Label {
    private int amountLeft;

    /**
     * Constructor.
     *
     * @param amountLeft bombs remaining.
     */
    public BombCounter(int amountLeft) {
        this.setAmountLeft(amountLeft);
    }

    /**
     * Set bombs remaining.
     *
     * @param amountLeft bombs left.
     */
    public void setAmountLeft(int amountLeft) {
        this.amountLeft = amountLeft;
        this.setBombText();
    }

    /**
     * Increase bombs by one.
     */
    public void incrementBombsLeft() {
        this.amountLeft++;
        this.setBombText();
    }

    /**
     * Decrease bombs by one.
     */
    public void decrementBombsLeft() {
        this.amountLeft--;
        this.setBombText();
    }

    /**
     * Change text.
     */
    private void setBombText() {
        // TODO: generalize for more than 99 and handle negatives better
        if (this.amountLeft > 10) {
            this.setText(String.format(" %d", this.amountLeft));
        } else if (this.amountLeft >= 0) {
            this.setText(String.format("  %d", this.amountLeft));
        } else if (this.amountLeft > -10) {
            this.setText(String.format(" %d", this.amountLeft));
        } else {
            this.setText(String.format("%d", this.amountLeft));
        }
    }
}
