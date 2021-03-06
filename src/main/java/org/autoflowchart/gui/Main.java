package org.autoflowchart.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application
{
    public Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));



        primaryStage.setTitle("AutoFlowchart");
        primaryStage.setScene(new Scene(root, 960, 720));
        primaryStage.show();
        primaryStage.getIcons().add(new Image("images/logo.png"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
