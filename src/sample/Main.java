/**
 * This file is part of the CS 4398 Software Engineering Project, Spring 2017 class -- Group 2
 * Group Members
 * @author Gregory Pontejos
 * @author Donovan Wells
 * @author Jason Villegas
 * @author Kingsley Nyaosi
 */

package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    /**
     * This function instantiates the Main Controller modal
     * @param primaryStage to be displayed
     * @throws Exception if resource is not found
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Scanner");
        Scene nScene = new Scene(root, 900, 600);
        nScene.getStylesheets().clear();
        nScene.getStylesheets().add("theme.css");
        primaryStage.setScene(nScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}