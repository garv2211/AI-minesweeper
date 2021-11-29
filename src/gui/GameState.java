package gui;

/**
 * Created by jonsteinn on 7.3.2017.
 *
 * State for points in the game.
 */
public enum GameState {
    IDLE, PLAYING, WON, LOST;

    @Override
    public String toString() {
        switch (this) {
            case IDLE:
                return "Idle";
            case PLAYING:
                return "Playing";
            case WON:
                return "Won!";
            case LOST:
                return "Lost!";
            default:
                return "Unknown";
        }
    }
}
