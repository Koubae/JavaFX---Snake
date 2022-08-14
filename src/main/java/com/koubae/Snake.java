package com.koubae;

import java.util.*;

import com.koubae.entity.SnakeSnake;
import com.koubae.view.ViewAppArch;
import com.koubae.view.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;


public class Snake extends Application {

    private Stage stage;
    final private ViewManager viewManager;
    final private ViewAppArch viewArch;
    public ArrayList<String> input = new ArrayList<>();

    private SnakeSnake snake;

    public Snake() {
        viewManager = new ViewManager();
        snake = new SnakeSnake();
        viewArch = new ViewAppArch(viewManager);
    }


    public static void main(String[] args) {
        launch();
    }


    @Override
    public void start(Stage stageMain) {

        // set-up
        stage = stageMain;
        viewManager.setStage(stageMain);
        viewManager.setViewArch(viewArch);
        viewArch.buildAppViews(stage, input);

        try {
            viewManager.viewSwitch(ViewAppArch.ViewAppElement.VIEW_PRIMARY.view);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

}
