package com.koubae.view;

import com.koubae.game.Game;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static javafx.geometry.Pos.CENTER;

public class ViewAppArch  {

    final public int WIN_WIDTH_MIN = 640;
    final public int WIM_HEIGHT_MIN = 480;
    static final public int GRID_COLS = 70;
    static final public int GRID_ROWS = 40;
    final public int GRID_BLOCK_WIDTH = 20;
    final public int GRID_BLOCK_HEIGHT = 20;

    static final public String BLOCK_CSS_EMPTY = "grid-block";
    static final public String BLOCK_CSS_SNAKE = "snake-body";
    static final public String BLOCK_CSS_SNAKE_DEAD = "snake-body-dead";
    static final public String BLOCK_CSS_FOOD = "food";
    final private ViewManager viewManager;
    public List<List<Rectangle>> playGrid;

    public Game currentGame = null;


    public enum ViewAppElement {
        VIEW_PRIMARY("VIEW_PRIMARY"),
        VIEW_GAMEON("VIEW_GAMEON");

        public String view;
        ViewAppElement(String v) {
            view = v;
        }
    }


    public ViewAppArch(ViewManager vm) {
        viewManager = vm;
        playGrid = new ArrayList<>();
        buildPlayGrid();

    }

    /**
     * Builds Application play grid
     */
    private void buildPlayGrid() {
        for (int i = 0; i < GRID_ROWS; i++) {
            List<Rectangle> col = new ArrayList<>();

            for (int y = 0; y < GRID_COLS; y++) {
                Rectangle node = new Rectangle(GRID_BLOCK_WIDTH, GRID_BLOCK_HEIGHT);
                node.getStyleClass().add(BLOCK_CSS_EMPTY);
                col.add(node);
            }
            playGrid.add(col);
        }
    }

    public void buildAppViews(Stage stage, ArrayList<String> input) {
        viewPrimary(stage);
        viewGame(input);
    }

    private void viewPrimary(Stage stage) {
        final String key = ViewAppElement.VIEW_PRIMARY.view;

        Label title = new Label("Snake Game");
        Font font = Font.font("Courier New", FontWeight.BOLD, 55);
        title.setFont(font);
        HBox wrapper = new HBox(title);
        wrapper.setAlignment(CENTER);
        wrapper.prefHeightProperty().bind(stage.heightProperty().multiply(0.5)); // Set the screen to half

        Button changeView = new Button("Play");
        changeView.getStyleClass().addAll("btn", "btn-playgame");

        changeView.setOnAction(e -> viewManager.viewSwitch(ViewAppElement.VIEW_GAMEON.view));

        HBox content = new HBox(changeView);
        content.setAlignment(CENTER);

        VBox view = new VBox(wrapper, content);
        view.setPadding(new Insets(10));
        view.setSpacing(4);


        view.setBackground(new Background(
                new BackgroundFill(
                        new LinearGradient(0, 0, 0, 1, true,
                                CycleMethod.NO_CYCLE,
                                new Stop(0, Color.web( "#26244f")),
                                new Stop(1, Color.web("#c603fc"))
                        ), CornerRadii.EMPTY, Insets.EMPTY
                )
        ));
        double screenYHalf = Screen.getPrimary().getBounds().getHeight() / 2;

        final double MAX_Y = screenYHalf - 50;
        final IntegerProperty i = new SimpleIntegerProperty(0);
        final int[] currentYPos = {10};
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(
                Duration.seconds(Game.FRAME_RATE),
                event -> {
                    if (title.localToScene(title.getBoundsInLocal()).getMaxY() > MAX_Y) {
                        timeline.stop();
                    } else {
                        title.setTranslateY(currentYPos[0]);
                        currentYPos[0] = currentYPos[0] + 3;
                    }
                });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        // Add view to the viewManager
        Scene scene = new Scene(view);
        viewManager.setViews(key, scene);
    }

    private void viewGame(ArrayList<String> input) {
        final String key = ViewAppElement.VIEW_GAMEON.view;

        // ---------------------------------------------
        //  Navbar
        // ---------------------------------------------

        Button changeView = new Button("Back to Home");
        changeView.getStyleClass().addAll("btn", "btn-snake-green");
        changeView.setOnAction(e -> viewManager.viewSwitch(ViewAppElement.VIEW_PRIMARY.view));

        HBox nabar = new HBox(changeView);
        nabar.setPadding(new Insets(10));
        nabar.setBackground(new Background(
                new BackgroundFill(Color.CHARTREUSE, CornerRadii.EMPTY, Insets.EMPTY) // GAINSBORO DARKSLATEGRAY 	CHARTREUSE
        ));
        nabar.getStyleClass().addAll("nav-top");

        // ---------------------------------------------
        //  Player-board
        // ---------------------------------------------

        Label scoreLabel = new Label("Score: ");
        Label score = new Label("0");

        Label roundsLabel = new Label("Food Eaten: ");
        Label rounds = new Label("0");

        scoreLabel.getStyleClass().addAll("babel-text", "text-white");
        score.getStyleClass().addAll("text-white");

        roundsLabel.getStyleClass().addAll("babel-text", "text-white");
        rounds.getStyleClass().addAll("text-white");

        Button newGameBtn = new Button("New Game");
        newGameBtn.getStyleClass().addAll("btn", "btn-playgame");
        newGameBtn.setOnAction(e -> {
            if (currentGame != null) {
                currentGame.resetGame();
                currentGame = null;
            }
            // Clear current Game!!!
            Game game = new Game(score, rounds, input, playGrid);
            currentGame = game;
            game.gameLoop();
        });

        GridPane dashBoard = new GridPane();
        dashBoard.setPadding( new Insets(0, 10, 0, 40) );
        dashBoard.setHgap(20);
        dashBoard.setVgap(10);

        dashBoard.add( newGameBtn, 0, 0);
        dashBoard.add(scoreLabel, 4, 0);
        dashBoard.add(score, 5, 0);
        dashBoard.add(roundsLabel, 7, 0);
        dashBoard.add(rounds, 9, 0);


        HBox playerBoard = new HBox(dashBoard);
        playerBoard.setPadding(new Insets(5, 10, 0, 10));
        playerBoard.setAlignment(CENTER);


        // ---------------------------------------------
        //  Play - Screen
        // ---------------------------------------------

        VBox view = new VBox(nabar, playerBoard);
        // Style the view
        view.setPadding(new Insets(0, 5, 5, 5));
        view.setSpacing(4);

        view.setBackground(new Background(
                new BackgroundFill(Color.DARKSLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY)
        ));

        HBox board = new HBox();
        VBox playScreen = new VBox(board);
        playScreen.getStyleClass().addAll("playScreen");
        board.setAlignment(CENTER);
        playScreen.setAlignment(CENTER);
        view.getChildren().add(playScreen);

        TilePane grid = new TilePane();
        grid.getStyleClass().add("grid");
        grid.setPrefColumns(GRID_COLS);
        grid.setPrefRows(GRID_ROWS);
        grid.setTileAlignment( Pos.CENTER );

        for (List<Rectangle> row : playGrid) {
            grid.getChildren().addAll(row);
        }

        board.getChildren().addAll(grid);


        // Add view to the viewManager
        Scene scene = new Scene(view);
        // Event handler to keep track of user latest pressed key!
        scene.setOnKeyPressed(
                new EventHandler<KeyEvent>()
                {
                    public void handle(KeyEvent e)
                    {
                        String code = e.getCode().toString();

                        // only add once... prevent duplicates
                        if ( !input.contains(code) )
                            input.add( code );
                    }
                });

        scene.setOnKeyReleased(
                new EventHandler<KeyEvent>()
                {
                    public void handle(KeyEvent e)
                    {
                        String code = e.getCode().toString();
                        input.remove( code );
                    }
                });
        viewManager.setViews(key, scene);

    }

}
