package com.koubae.game;

import com.koubae.entity.SnakeSnake;
import com.koubae.view.ViewAppArch;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Game {

    // Pointers to the Screen Arch
    static final public float FRAME_RATE = (float) 1 / 60;
    public SnakeSnake snake;
    public Label score;
    public Label rounds;
    public ArrayList<String> input;

    // ---- architecture pointers
    public List<List<Rectangle>> playGrid;
    private Timeline gameloop;


    public int[] foodCurrentPosition;
    public Game(
            Label score,
            Label rounds,
            ArrayList<String> input,
            List<List<Rectangle>> playGrid
    ) {
        snake = new SnakeSnake();

        this.score = score;
        this.rounds = rounds;
        this.input = input;
        this.playGrid = playGrid;
        clearGame();
        buildSnake();
    }

    private void clearGame() {
        // Reset scores
        score.setText("0");
        rounds.setText("0");

        playGrid.forEach(row -> {
            row.forEach(col -> {
                col.getStyleClass().clear();
                col.getStyleClass().add(ViewAppArch.BLOCK_CSS_EMPTY);
            });
        });
    }

    public void resetGame() {
        gameloop.stop();
        snake.die(playGrid);
        clearGame();
        // Garbage collect all data
        snake = null;
        gameloop = null;
        playGrid = null;
    }

    private void buildSnake() {

        LinkedList<int[]> snakePosition = snake.snakePosition;
        for (int[] position: snakePosition) {  // Add snake into the grid
            int posY = position[0];
            int posX = position[1];
            Rectangle body = playGrid.get(posY).get(posX);
            body.getStyleClass().add(ViewAppArch.BLOCK_CSS_SNAKE);
        }
        foodCurrentPosition = placeRandomFood();
    }

    public int[] placeRandomFood() { // todo: move me
        int nextX;
        int nextY;
        while (true) {
            nextX = ThreadLocalRandom.current().nextInt(0, ViewAppArch.GRID_COLS);
            nextY = ThreadLocalRandom.current().nextInt(0, ViewAppArch.GRID_ROWS);
            int[] coords = {nextY, nextX};
            boolean busy = snake.isPositionWithSnake(coords, true);
            if (!busy) {
                break;
            }
        }

        Rectangle node = playGrid.get(nextY).get(nextX);
        node.getStyleClass().add(ViewAppArch.BLOCK_CSS_FOOD);
        return new int[]{nextY, nextX};
    }

    // ---------------------------------------------------
    //  Game loop
    // ---------------------------------------------------
    /**
     * Game loop
     * @credit :: https://github.com/tutsplus/Introduction-to-JavaFX-for-Game-Development/blob/master/Example3T.java / https://gamedevelopment.tutsplus.com/tutorials/introduction-to-javafx-for-game-development--cms-23835
     */
    public void gameLoop() {
        gameloop = new Timeline();
        gameloop.setCycleCount(Timeline.INDEFINITE);
        float[] gameSpeed = {1};
        float[] acceleration = {0.0F};
        float[] speed = {FRAME_RATE * 20};
        KeyFrame keyframe = new KeyFrame(
                Duration.seconds(FRAME_RATE),
                event -> {
                    /**
                     * 1) Move snake forward
                     * 2) Turn left
                     * 3) Turn right
                     * 4) Go wall and appear opposite side
                     * 5) Body collision
                     * 6) Keep count of turns
                     *
                     */

                    // Calculate snake movement speeed
                    if (acceleration[0] < gameSpeed[0]) {
                        acceleration[0] = acceleration[0] + speed[0];
                        return;

                    }
                    acceleration[0] = 0;
                    String userInput = "";
                    if (!input.isEmpty()) {
                        userInput = input.get(0);
                    }

                    score.setText(String.valueOf(snake.getScore()));
                    rounds.setText(String.valueOf(snake.getFoodEaten()));

                    LinkedList<int[]> snakePosition = snake.snakePosition;
                    int[] snakeTail = snakePosition.getLast();
                    int snakeTailY = snakeTail[0];
                    int snakeTailX = snakeTail[1];

                    // Snake Movement
                    switch (userInput) {
                        case "W":
                            if (!Objects.equals(snake.lastDirection, "DOWN")) {
                                snake.moveUp();
                            } else {
                                snake.moveDown();
                            }

                            break;
                        case "S":
                            if (!Objects.equals(snake.lastDirection, "UP")) {
                                snake.moveDown();
                            } else  {
                                snake.moveUp();
                            }
                            break;
                        case "D":
                            if (!Objects.equals(snake.lastDirection, "LEFT")) {
                                snake.moveRight();
                            } else {
                                snake.moveLeft();
                            }

                            break;
                        case "A":
                            if (!Objects.equals(snake.lastDirection, "RIGHT")) {
                                snake.moveLeft();
                            } else {
                                snake.moveRight();
                            }
                            break;
                        default:
                            switch (snake.lastDirection) {
                                case "UP":
                                    snake.moveUp();
                                    break;
                                case "DOWN":
                                    snake.moveDown();
                                    break;
                                case "RIGHT":
                                    snake.moveRight();
                                    break;
                                case "LEFT":
                                    snake.moveLeft();
                                    break;
                                default:
                                    snake.moveRight();
                                    break;
                            }
                            break;
                    }


                    int[] snakeHead = snakePosition.getFirst();
                    int snakeHeadY = snakeHead[0];
                    int snakeHeadX = snakeHead[1];

                    // Snake Eating
                    boolean eating = false;
                    if (snakeHeadY == foodCurrentPosition[0] && snakeHeadX == foodCurrentPosition[1]) {
                        eating = true;
                        speed[0] = speed[0] + (FRAME_RATE / 3);
                        Rectangle foodPositionold = playGrid.get(foodCurrentPosition[0]).get(foodCurrentPosition[1]);
                        foodPositionold.getStyleClass().clear();
                        foodCurrentPosition = placeRandomFood();

                        snake.setScore();
                        snake.setFoodEaten();
                    }

                    // ----------------------- Remove Tail or Add tail (eat)
                    if ((snakeTailY <= ViewAppArch.GRID_ROWS-1 && snakeTailY >= 0) || (snakeTailX <= ViewAppArch.GRID_COLS-1 && snakeTailX >= 0)) {

                        try {
                            if (eating) {
                                snake.eat(snakeTailY, snakeTailX);
                            } else {
                                Rectangle tailOld = playGrid.get(snakeTailY).get(snakeTailX);
                                tailOld.getStyleClass().clear();
                                tailOld.getStyleClass().add(ViewAppArch.BLOCK_CSS_EMPTY);
                            }

                        } catch (Exception e) {
                            System.exit(1);
                        }

                    }

                    // Snake Death
                    boolean dead = snake.didSnakeBiteItself();
                    if (dead) {
                        gameloop.stop();
                        snake.die(playGrid);
                        return;
                    }



                    // ----------------------- Move Snake Forward
                    // Reposition head
                    if (snakeHeadY > ViewAppArch.GRID_ROWS-1 ) { // right wall
                        snakeHead[0] = 0; // move to opposite left side!;
                        snakeHeadY = 0;
                    } else if (snakeHeadY < 0) { // left wall
                        snakeHead[0] = ViewAppArch.GRID_ROWS-1; // move to opposite right side!;
                        snakeHeadY = ViewAppArch.GRID_ROWS-1;
                    } else if (snakeHeadX > ViewAppArch.GRID_COLS-1) { // bottom wall
                        snakeHead[1] = 0; // Move to top
                        snakeHeadX = 0;
                    } else if (snakeHeadX < 0) { // top wall
                        snakeHead[1] = ViewAppArch.GRID_COLS-1; // move to bottom
                        snakeHeadX = ViewAppArch.GRID_COLS-1;
                    }

                    // Move head in front by one pos
                    try {
                        Rectangle headNew = playGrid.get(snakeHeadY).get(snakeHeadX);
                        headNew.getStyleClass().add(ViewAppArch.BLOCK_CSS_SNAKE);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        System.exit(1);
                    }


                }
        );

        gameloop.getKeyFrames().add(keyframe);
        gameloop.play();

    }

}