package Gramophy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jnativehook.GlobalScreen;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        AnchorPane root = FXMLLoader.load(getClass().getResource("dash.fxml"));
        primaryStage.setTitle("Gramophy");
        primaryStage.setMinWidth(950);
        primaryStage.setMinHeight(570);
        Scene s = new Scene(root);
        primaryStage.setScene(s);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("assets/app_icon.png")));
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            try
            {
                GlobalScreen.unregisterNativeHook();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
