package com.koubae.entity;

import com.koubae.view.ViewAppArch;
import javafx.animation.AnimationTimer;
import javafx.scene.shape.Rectangle;

import java.util.LinkedList;
import java.util.List;

public class SnakeSnake {
    final public LinkedList<int[]> snakePosition = new LinkedList<>();
    final private String snakeDefaultStartDirection = "RIGHT";
    public String lastDirection = snakeDefaultStartDirection;

    private int score = 0;
    private int foodEaten = 0;

    public SnakeSnake() {
        // Set-up snake initial position
        int[] bodyPart1 = {18, 33}; // head
        int[] bodyPart2 = {18, 32};
        int[] bodyPart3 = {18, 31};
        snakePosition.add(bodyPart1);
        snakePosition.add(bodyPart2);
        snakePosition.add(bodyPart3);

        boolean debug = true;
        if (debug) { // add more body parts
            int additionalBodyParts = 10;
            for (int i = 0; i < additionalBodyParts; i++ ) {
                int[] b = {18, 30 - i};
                snakePosition.add(b);
            }
        }


    }

    public int getScore() { return this.score; }
    public int getFoodEaten() { return this.foodEaten; }

    public void setScore() {
        this.score = this.score + 5;
    }
    public void setFoodEaten() {
        this.foodEaten ++;
    }

    private void move() {
        for (int i = snakePosition.size() - 1; i >= 1; i--) {
            int[] pos = snakePosition.get(i);
            int[] posNext = snakePosition.get(i-1);
            pos[0] = posNext[0];
            pos[1] = posNext[1];
        }

    }

    public void moveUp() {
        move();
        lastDirection = "UP";
        snakePosition.getFirst()[0] =  snakePosition.getFirst()[0] - 1;

    }
    public void moveDown() {
        move();
        lastDirection = "DOWN";
        snakePosition.getFirst()[0] =  snakePosition.getFirst()[0] + 1;

    }
    public void moveLeft() {
        move();
        lastDirection = "LEFT";
        snakePosition.getFirst()[1] =  snakePosition.getFirst()[1] - 1;
    }
    public void moveRight() {
        move();
        lastDirection = "RIGHT";
        snakePosition.getFirst()[1] =  snakePosition.getFirst()[1] + 1;
    }


    public void eat(int posY, int posX) {
        int[] newTail = {posY, posX};
        snakePosition.addLast(newTail);
    }

    public void die(List<List<Rectangle>> playGrid) {

        for (int[] coordinates : snakePosition) { // Make all snake red in once!
            int pointY = coordinates[0];
            int pointX = coordinates[1];

            Rectangle bodyPart = playGrid.get(pointY).get(pointX);
            bodyPart.getStyleClass().clear();
            bodyPart.getStyleClass().add(ViewAppArch.BLOCK_CSS_SNAKE_DEAD);

        }

        final long[] preTime = {System.nanoTime()};
        final float maxTime = 0.1F;
        int[] nextCoordinates = {0};
        final int[] maxIndex = {snakePosition.size() -1};

        // Animation for the Snake to slowly disappear : (
        new AnimationTimer()
        {
            @Override public void handle(long currentNanoTime)
            {
                if (nextCoordinates[0] > maxIndex[0]) {
                    this.stop();
                }
                double t = (currentNanoTime - preTime[0]) / 1_000_000_000.0;
                if (t > maxTime) {
                    int[] coordinates = snakePosition.get(nextCoordinates[0]);
                    Rectangle bodyPart = playGrid.get(coordinates[0]).get(coordinates[1]);
                    bodyPart.getStyleClass().clear();
                    bodyPart.getStyleClass().add(ViewAppArch.BLOCK_CSS_EMPTY);
                    preTime[0] = currentNanoTime;
                    nextCoordinates[0] ++;
                }

            }
        }.start();


    }

    public boolean didSnakeBiteItself() {
        int[] snakeHead = snakePosition.getFirst();
        return isPositionWithSnake(snakeHead, false);
    }

    public boolean isPositionWithSnake(int[] position, boolean fullBody) {
        int positionY = position[0];
        int positionX = position[1];

        int i = 0;
        int total = snakePosition.size() - 1;
        if (!fullBody) {  // Skip the head
            i = 1;
            total --;
        }
        for (; i < total; i++) {
            int[] pos = snakePosition.get(i);
            if (pos[0] == positionY && pos[1] == positionX) {
                return true;
            }
        }
        return false;

    }

}
