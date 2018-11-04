/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

/**
 *
 * @author debayan
 * openFXML v2.1
 * A simple java code for opening FXML files
 * Copyright 2018 DX Corporation
 * All Rights Reserved
 */
public class openFXML extends Application{
    
    openFXML(String fileName, boolean closePreviousWindow, ActionEvent event) throws IOException
    {
        console.pln("Init "+fileName.replace(".fxml","")+" ...");
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fileName));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        //stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.setTitle("DX Gramophy");
        stage.setScene(new Scene(root1));
        stage.show();
        console.pln("...Done!");
        if(closePreviousWindow)
        {
            console.pln("Disposing previous Window...");
            Node node = (Node) event.getSource();
            Stage s= (Stage) node.getScene().getWindow();
            s.close();
            console.pln("...Done!");
        } 
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}