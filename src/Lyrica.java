/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
/**
 *
 * @author debayan
 */
public class Lyrica extends Application {


    
    public static String version = "3.0";
    public static String songDirectory = "";
    public static String themeColor = "#7c43bd";
    public static int noOfSongs = 0;
    public static List songList = new ArrayList();
    public static List albumArtList = new ArrayList();
    public static Stage dashStage = null;
    public static Stage stage;
    public static String lastFmApiKey = "b093153a2ceee21e29d31028be5799a7";
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
        
        Scene scene = new Scene(root);
        dashStage = stage;
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setOpacity(0.95);
        stage.setResizable(false);
        stage.setMaxHeight(590.0);
        stage.setMaxWidth(995.0);
        stage.setTitle("DX Gramophy");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        console.pln("Gramophy");
        console.pln("Codename lyrica");
        console.pln("By Debayan");
        console.pln("ladiesman6969/dxBeta");
        console.pln("Open-Source Java Based Music Player");
        console.pln("v"+version);
        console.pln("\n");
        console.pln("Looking for files...");
        ActionEvent event = new ActionEvent();
        filer f = new filer();
        if(f.doesFileExists("files/songLocation.dx"))
        {
            songDirectory = f.readFileAsString("files/songLocation.dx");
            console.pln("Checking song location for files...");
            console.pln("'"+songDirectory+"'");
            if(songDirectory.equals("NULL"))
            {
                new mentionSongDirectory().setVisible(true);
            }
            else
            {
                console.pln("Checking theme Color...");
                if(f.doesFileExists("files/colorConfig.dx"))
                {
                    themeColor = f.readFileAsString("files/colorConfig.dx");
                    console.pln("Launching...");
                    launch(args);
                    console.pln("...Done!");
                }
                else
                {
                    console.pln("ERROR OCCURED");
                    new criticalError("colorConfig.dx NOT FOUND").setVisible(true);
                }
            }
        }
        else
        {
            console.pln("ERROR OCCURED");
            new criticalError("songLocation.dx NOT FOUND").setVisible(true);
        }

        
    }
    
}
