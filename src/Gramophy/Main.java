package Gramophy;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("dash.fxml"));
        primaryStage.setMinWidth(1290.0);
        primaryStage.setMinHeight(772.0);
        primaryStage.setTitle("Gramophy");
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("assets/app_icon.png")));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            try
            {
                GlobalScreen.unregisterNativeHook();
                if(Main.dash.player.isActive) Main.dash.player.stop();
                Main.dash.gcThread.stop();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        ps = primaryStage;
    }

    public static dashController dash;
    public static Stage ps;

    public static void main(String[] args) {
        launch(args);
    }
}
