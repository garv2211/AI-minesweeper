package gui;

import javafx.scene.control.*;

/**
 * Created by Jonni on 3/6/2017.
 *
 * Top bar of the GUI.
 */
public class MinesweeperMenu extends MenuBar {

    private Menu size, playAs;

    /**
     * Constructor that sets up all events.
     */
    public MinesweeperMenu() {
        Menu file, preferences;
        MenuItem newGame, exit;
        RadioMenuItem player, computer, beginner, intermediate , expert;

        file = new Menu("File");
        preferences = new Menu("Preferences");
        this.size = new Menu("Size");
        this.playAs = new Menu("Player");

        newGame = new MenuItem("New Game");
        newGame.setOnAction(event -> Controller.controller.newGame());

        exit = new MenuItem("Exit");
        exit.setOnAction(event -> Controller.controller.exit());

        player = new RadioMenuItem("Human");
        player.setOnAction(event -> Controller.controller.setPlayer(Player.HUMAN));
        computer = new RadioMenuItem("Computer");
        computer .setOnAction(event -> Controller.controller.setPlayer(Player.Computer));

        beginner = new RadioMenuItem("Small");
        beginner.setOnAction(event -> Controller.controller.setSize(Size.SMALL));
        intermediate = new RadioMenuItem("Medium");
        intermediate.setOnAction(event -> Controller.controller.setSize(Size.MEDIUM));
        expert = new RadioMenuItem("Large");
        expert.setOnAction(event -> Controller.controller.setSize(Size.LARGE));

        ToggleGroup play = new ToggleGroup();
        player.setToggleGroup(play);
        player.setSelected(true);
        computer.setToggleGroup(play);

        ToggleGroup difficulty = new ToggleGroup();
        beginner.setToggleGroup(difficulty);
        beginner.setSelected(true);
        intermediate.setToggleGroup(difficulty);
        expert.setToggleGroup(difficulty);

        this.size.getItems().addAll(beginner, intermediate, expert);
        this.playAs.getItems().addAll(player, computer);

        file.getItems().addAll(newGame, exit);
        preferences.getItems().addAll(this.size, this.playAs);
        this.getMenus().addAll(file, preferences);

        exit.setOnAction((event) -> Controller.controller.exit());
    }

    /**
     * Disables settings.
     * @param enable true to disable, false to enable
     */
    public void setPreferences(boolean enable) {
        this.size.setDisable(enable);
        this.playAs.setDisable(enable);
    }
}
