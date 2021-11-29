package gui;

import agent.MSAgent;
import agent.Position;
import javafx.application.Platform;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import level.Board;
import level.RandomBoardGenerator;

/**
 * The event handler and brain for the entire GUI.
 */
public class Controller {

    /**
     * Constructor is private and only this single instance can be used.
     */
    public static Controller controller = new Controller();

    private Player player = Player.HUMAN;
    private Size size = Size.SMALL;
    private Board board;
    private GameState state = GameState.IDLE;
    private RandomBoardGenerator boardGenerator;
    private BoardButtons boardButtons;
    private BorderPane root;
    private MinesweeperMenu menu;
    private Footer footer;
    private Stage stage;
    private int clicksToWin;
    private MSAgent agent;

    /**
     * Constructor. Initially, a human playing 8x8 game is created.
     */
    private Controller() {
        this.player = Player.HUMAN;
        this.size = Size.SMALL;
        this.state = GameState.IDLE;
        this.boardGenerator = new RandomBoardGenerator();
        this.board = null;
        this.boardButtons = null;
        this.root = new BorderPane();
        this.menu = new MinesweeperMenu();
        this.footer = new Footer();
        this.root.setTop(this.menu);
        this.board = this.boardGenerator.create(8, 8, 10, false, false);
        this.clicksToWin = 8 * 8 - 10;
        this.boardButtons = new BoardButtons(this.board);
        this.root.setCenter(this.boardButtons);
        this.root.setBottom(this.footer);
        this.footer.getPlay().setDisable(true);
        this.agent = null;
    }

    /**
     * Quit program.
     */
    public void exit() {
        Platform.exit();
    }

    /**
     * Create a new game.
     */
    public void newGame() {
        this.state = GameState.IDLE;
        switch (size) {
            case SMALL:
                this.board = this.boardGenerator.create(8, 8, 10, false, false);
                this.footer.getBombsLeft().setAmountLeft(10);
                this.clicksToWin = 8 * 8 - 10;
                if (this.player == Player.Computer) this.agent = new MSAgent(8,8,10);
                break;
            case MEDIUM:
                this.board = this.boardGenerator.create(16, 16, 40, false, false);
                this.footer.getBombsLeft().setAmountLeft(40);
                this.clicksToWin = 16 * 16 - 40;
                if (this.player == Player.Computer) this.agent = new MSAgent(16,16,40);
                break;
            case LARGE:
                this.board = this.boardGenerator.create(24, 24, 99, false, false);
                this.footer.getBombsLeft().setAmountLeft(99);
                this.clicksToWin = 24 * 24 - 99;
                if (this.player == Player.Computer) this.agent = new MSAgent(24,24,99);
        }
        this.footer.getTimer().restartPlayClock();
        this.footer.setStatus(this.state);
        if (this.player == Player.HUMAN) footer.getPlay().setDisable(true);
        else footer.getPlay().setDisable(false);
        this.boardButtons = new BoardButtons(this.board);
        this.root.setCenter(this.boardButtons);
    }

    /**
     * Changes player
     *
     * @param player Player enum
     */
    public void setPlayer(Player player) {
        this.player = player;
        newGame();
    }

    /**
     * Changes size.
     *
     * @param size Size enum
     */
    public void setSize(Size size) {
        this.size = size;
        newGame();
        this.resizeStage();
    }

    /**
     * @return UI root.
     */
    public BorderPane getRoot() {
        return this.root;
    }

    /**
     * Open square as a human player.
     *
     * @param button Button to click
     * @param x coordinate
     * @param y coordinate
     */
    public void buttonUpdate(MinesweeperButton button, int x, int y) {
        if (this.player == Player.Computer || this.state == GameState.LOST || this.state == GameState.WON || button.isDown() || button.getText().equals("#")) return;
        if (this.state == GameState.IDLE) {
            this.state = GameState.PLAYING;
            this.footer.setStatus(this.state);
            this.footer.getTimer().startPlayClock();
        }
        button.click();

        if (this.board.containsBomb(x,y)) {
            button.setText("X");
            button.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
            this.state = GameState.LOST;
            this.footer.setStatus(this.state);
            this.footer.getTimer().stopPlayClock();
        } else {
            this.clicksToWin--;
            int adj = this.board.adjacentBombs(x, y);
            if (adj == 0) {
                // Recursive auto update for 0-squares
                if (board.outOfBounds(x - 1, y - 1)) this.boardButtons.get(x - 1, y - 1).fire();
                if (board.outOfBounds(x, y - 1)) this.boardButtons.get(x, y - 1).fire();
                if (board.outOfBounds(x + 1, y - 1)) this.boardButtons.get(x + 1, y - 1).fire();
                if (board.outOfBounds(x - 1, y)) this.boardButtons.get(x - 1, y).fire();
                if (board.outOfBounds(x + 1, y)) this.boardButtons.get(x + 1, y).fire();
                if (board.outOfBounds(x - 1, y + 1)) this.boardButtons.get(x - 1, y + 1).fire();
                if (board.outOfBounds(x, y + 1)) this.boardButtons.get(x, y + 1).fire();
                if (board.outOfBounds(x + 1, y + 1)) this.boardButtons.get(x + 1, y + 1).fire();
            } else {
                button.setText(Integer.toString(adj));
            }
            button.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
            if (clicksToWin == 0) {
                this.state = GameState.WON;
                this.footer.setStatus(this.state);
                this.footer.getTimer().stopPlayClock();
            }
        }
    }

    /**
     * Run solver.
     */
    public void playAsComputer() {
        if (this.player == Player.HUMAN) return;
        newGame();
        this.state = GameState.PLAYING;
        this.footer.setStatus(this.state);
        this.footer.getTimer().startPlayClock();
        while (this.state != GameState.LOST && this.state != GameState.WON) {
            // Mark all bombs the agent knows of that have not already been marked.
            Position bomb;
            while ((bomb = this.agent.markBomb()) != null) {
                this.boardButtons.get(bomb.getX(), bomb.getY()).setText("#");
                this.footer.getBombsLeft().decrementBombsLeft();
            }
            // Get next move from agent.
            Position pos = this.agent.nextMove();
            this.agent.sendBackResult(pos, computerClick(pos.getX(), pos.getY()));
        }
        if (this.state == GameState.WON) {
            Position bomb;
            while ((bomb = this.agent.markBomb()) != null) {
                this.boardButtons.get(bomb.getX(), bomb.getY()).setText("#");
                this.footer.getBombsLeft().decrementBombsLeft();
            }
        }
    }

    /**
     * Click for computer. Not a UI event.
     *
     * @param x coordinate
     * @param y coordinate
     * @return adjacent number in actual board for (x,y)
     */
    private int computerClick(int x, int y) {
        MinesweeperButton button = this.boardButtons.get(x, y);
        button.click();
        if (this.board.containsBomb(x,y)) {
            // If lost, we just return any number since they game won't continue.
            button.setText("X");
            button.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
            this.state = GameState.LOST;
            this.footer.setStatus(this.state);
            this.footer.getTimer().stopPlayClock();
            return 0;
        } else {
            // If the square does not contain a bomb, we return the number it contains.
            // This is the only communication with the agent.
            this.clicksToWin--;
            int adj = this.board.adjacentBombs(x, y);
            if (adj != 0) button.setText(Integer.toString(adj));
            button.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
            if (clicksToWin == 0) {
                this.state = GameState.WON;
                this.footer.setStatus(this.state);
                this.footer.getTimer().stopPlayClock();
            }
            return adj;
        }
    }

    /**
     * Mark square as bombs for human player.
     *
     * @param button Button to mark
     */
    public void markBomb(MinesweeperButton button) {
        if (this.player == Player.Computer || this.state == GameState.LOST || this.state == GameState.WON) return;
        if (button.getText().equals("#")) {
            button.setText("");
            this.footer.getBombsLeft().incrementBombsLeft();
        } else {
            button.setText("#");
            this.footer.getBombsLeft().decrementBombsLeft();
        }
    }

    /**
     * @param stage Stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Resizing for level changing.
     */
    public void resizeStage() {
        this.stage.sizeToScene();
        this.stage.centerOnScreen();
        this.stage.setWidth(this.stage.getWidth() + 10);
        this.stage.setHeight(this.stage.getHeight() + 10);
    }

}
