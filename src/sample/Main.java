package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

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