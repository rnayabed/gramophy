package Gramophy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("dash.fxml"));
        primaryStage.setMinWidth(1290.0);
        primaryStage.setMinHeight(772.0);
        primaryStage.setTitle("Gramophy");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            if(Main.dash.player.isActive) Main.dash.player.stop();
            Main.dash.gcThread.stop();
        });
    }

    public static dashController dash;

    public static void main(String[] args) {
        launch(args);
    }
}
