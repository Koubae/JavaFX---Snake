package com.koubae.view;

import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewManager {

    private Stage stage;
    private ViewAppArch viewArch;
    private Map<String, View> views = new HashMap<>();

    public ViewManager() {
    }


    public Map<String, View> getViews() {
        return views;
    }

    public View getViews(String key) {
        if (Objects.equals(key, "") || key == null || !views.containsKey(key)) {
            return null;
        }
        return views.get(key);
    }

    public Scene getViews(String key, boolean getRegion) {
        if (Objects.equals(key, "") || key == null || !views.containsKey(key)) {
            return null;
        }
        if (!getRegion) {
            return null;
        }
        return views.get(key).getView();
    }

    public void setViews(String key, Scene scene) {
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/game.css")).toExternalForm());


        View newView = new View(scene); // Wrap the Region view into an actual View object
        views.put(key, newView);
    }


    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stageNew) {
        stage = stageNew;
    }

    public ViewAppArch getViewArch() {
        return viewArch;
    }

    public void setViewArch(ViewAppArch viewArchNew) {
        viewArch = viewArchNew;
    }

    public void viewSwitch(String key) {
        Scene view = getViews(key, true);
        if (view == null) {
            throw new IllegalArgumentException("View " + view + " not Found!");
        }
        stage.setScene(view);
        stage.setTitle("View Main");
        // fixme: why should we re-set the location at stage change???
        setupStageLocation(stage, 1, false, true);
    }

    // ---------------------------------
    //  Utilities methods
    // ---------------------------------

    /**
     * @credit john16384 https://community.oracle.com/tech/developers/discussion/2390272/how-to-use-dual-monitor-in-javafx2
     * @param stage
     * @param screenNumber
     * @param fullScreen
     * @param maxSize
     */
    private void setupStageLocation(Stage stage, int screenNumber, boolean fullScreen, boolean maxSize) {
        ObservableList<Screen> screens = Screen.getScreens();
        Screen screen = screens.size() <= screenNumber ? Screen.getPrimary() : screens.get(screenNumber);

        Rectangle2D bounds = screen.getBounds();
        boolean primary = screen.equals(Screen.getPrimary());    // WORKAROUND: this doesn't work nice in combination with full screen, so this hack is used to prevent going fullscreen when screen is not primary

        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());

        if (maxSize) {
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
        } else {
            stage.setWidth(viewArch.WIN_WIDTH_MIN);
            stage.setHeight(viewArch.WIM_HEIGHT_MIN);
        }
        stage.setFullScreen(fullScreen);

        if(primary) {
            // Do some other settings
        } else {
            stage.toFront();
        }

    }
}
