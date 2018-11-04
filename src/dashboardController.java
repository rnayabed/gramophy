/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import java.io.File;
import java.io.IOException;
import java.io.*;
import java.net.*;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.util.Duration;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author debayan
 */
public class dashboardController implements Initializable {
    public static boolean repeat = false;
    static boolean isSongAlreadyPlaying = false;
    String currentTitle;
    String curretnAlbum;
    String currenttmpArtist;
    static boolean isSongAsigned = false;
    static boolean isSeekBeingChanged = false;
    static String currentSongName = "";
    static String currentAlbum = "";
    static String currentArtist = "";
    static Double currentSongDuration = 0.00;
    
    
    
    static boolean isMusicInterfaceShown = false;
    
    double currentSongMinRaw;
    StringBuilder toBeChecked;
    int pointIndex;
    int min;
    
    boolean isFirstTimeUse = true;
    int c;
    boolean didPointCome = false;
    char currentChar;
    char currentChar2;
    char currentChar3;
    
    String tmpSongSeek = "";
    StringBuilder toBeShown = new StringBuilder();
    double secs;
    StringBuilder secString;
    String currentSongMinString;
    
    static int currentSongIndex = 0;
    
    MediaPlayer player = null;
    
    @FXML
    private JFXButton returnButtonSettings;
    
    @FXML
    private ImageView currentAlbumArt;
    
    @FXML
    private JFXProgressBar downloadingProgressBar;
    
    @FXML
    private JFXButton upDownButton;
    
    @FXML
    private ImageView upDownImageView;
    
    @FXML
    private AnchorPane musicInterfacePane;
    
    @FXML
    private JFXButton downloadAlbumArtButton;
    
    @FXML
    private JFXProgressBar songLocationProgressBar;
    
    @FXML
    private AnchorPane mainPane;
    
    @FXML
    private ImageView playPauseButtonImage1;
    
    @FXML
    private AnchorPane aboutPane;
    
    @FXML
    private AnchorPane settingsPane;
    
    @FXML
    private JFXButton aboutButton;
    
    @FXML
    private JFXButton applyChangesButton;
    
    @FXML
    private Label artistLabel;
    
    @FXML
    private JFXButton pauseStartButton;
   
    @FXML
    private ImageView playPauseButtonImage;
    
    @FXML
    private JFXProgressBar pBarLoading;
    
    @FXML
    private JFXButton quitButton;
    
    @FXML
    private Label albumArtDownloadCurrentStatusLabel;
    
    @FXML
    private Slider songSeek;
    
    @FXML
    private JFXButton previousButton;

    @FXML
    private JFXButton nextButton;
    
    @FXML
    private Label cL;
     
    @FXML
    private Label albumLabel;
    
    @FXML
    private Label loadingMusicFilesLabel;
    
    @FXML
    private Label itsLonelyHereLabel;
    
    @FXML
    private JFXButton checkForSongsButton;
    
    @FXML
    private Label itsLonelyHere2Label;
    
    @FXML
    private JFXListView songListview;
    
    @FXML
    private Label songNameLabel;
    
    @FXML
    private JFXButton settingsButton;
    
    @FXML
    private JFXButton stopButton;
    
    @FXML
    private JFXButton resetToDefaultButton;
    
    @FXML
    private JFXColorPicker colorChooser;
    
    @FXML
    private AnchorPane headerPane;
    
    @FXML
    private AnchorPane footerPane;
    
    @FXML
    private Label versionLabel;
    
    @FXML
    private Label totalMusicInfoLabel;

    @FXML
    private JFXButton repeatButton;

    String fileNameAttr = "file://";
     
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        repeatButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!repeat)
                {
                    repeat = true;
                    repeatButton.setRipplerFill(Paint.valueOf("#f10c0c"));
                }
                else
                {
                    repeat = false;
                    repeatButton.setRipplerFill(Paint.valueOf("#00d307"));
                }
            }
        });
        versionLabel.setText("Version "+Lyrica.version);
        if(Lyrica.themeColor.equals("#7c43bd"))
        {
            resetToDefaultButton.setDisable(true);
        }
        else
        {
            resetToDefaultButton.setDisable(false);
        }
        colorChooser.setValue(Color.web(Lyrica.themeColor));
        headerPane.setStyle("-fx-background-color : "+Lyrica.themeColor);
        footerPane.setStyle("-fx-background-color : "+Lyrica.themeColor);
        if(System.getProperty("os.name").equals("Linux"))
        {
            fileNameAttr = "file://";
        }
        else
        {
            fileNameAttr = "file:///";
        }
        try
        {
            searchForSongs();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    refreshSeek();
    refreshSeek2(); 
       
        mainPane.toFront();
        
    }    
    
    public void refreshSeek2()
    {
        Task t2 = new Task<Void>() {
        @Override public Void call() {
            while(true)
            {
                if(songSeek.isValueChanging()== false && isSongAlreadyPlaying && isSongAsigned)
                {
                    currentSongMinRaw = player.getCurrentTime().toMinutes();
                    currentSongMinString = Double.toString(currentSongMinRaw);
                    toBeChecked  = new StringBuilder();
                    toBeChecked.append("0.");
                    pointIndex = currentSongMinString.indexOf(".");
                    toBeChecked.append(currentSongMinString.charAt(pointIndex+1));
                    toBeChecked.append(currentSongMinString.charAt(pointIndex+2));
                    secs = Double.parseDouble(toBeChecked.toString())*60.0;
                    
                    StringBuilder secString = new StringBuilder();
                    if (secs<10)
                    {
                        secString.append("0");
                        secString.append(Character.toString(Double.toString(secs).charAt(0)));
                    }
                    else if (secs>10)
                    {
                        secString.append(Character.toString(Double.toString(secs).charAt(0)));
                        secString.append(Character.toString(Double.toString(secs).charAt(1)));
                    }
                    min = (int) (currentSongMinRaw + (currentSongMinRaw - Double.parseDouble(toBeChecked.toString())));
                    
                    
                    toBeShown = new StringBuilder();
                    toBeShown.append(min);
                    toBeShown.append(":");
                    toBeShown.append(secString.toString());
                       
                        
                        /*
                    PREVIOUS METHOD TO DETERMINE LENGTH
                    DEPRECATED SINCE v1.3
                    tmpSongSeek = Double.toString(player.getCurrentTime().toMinutes());
                        toBeShown = new StringBuilder();
                        boolean didPointCome = false;
                        for(int cx = 0;cx< tmpSongSeek.length(); cx++)
                        {
                            currentChar = tmpSongSeek.charAt(cx);
                            if(cx != tmpSongSeek.length()-1 && cx != tmpSongSeek.length()-2)
                            {
                                currentChar2 = tmpSongSeek.charAt(cx+1);
                                currentChar3 = tmpSongSeek.charAt(cx+2);
                            }
                            
                            if(tmpSongSeek.charAt(cx) == '.')
                            {
                    
                                didPointCome = true;
                                toBeShown.append(":");
                                toBeShown.append(tmpSongSeek.charAt(cx+1));
                                toBeShown.append(tmpSongSeek.charAt(cx+2));
                                break;
                            }
            
                if(didPointCome == false)
                {
                    toBeShown.append(tmpSongSeek.charAt(cx));
                }
                            
                                                        
                        }
                        
                        
                        didPointCome = false;*/
                        
                        Platform.runLater(new Runnable() {
    public void run() {
        cL.setText(toBeShown.toString());
    }
});
                    
                }
              
                
               
                
                
                try
                {
                    Thread.sleep(600);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
               
                }
                
                  
            }
        };
    Thread xx = new Thread(t2);
    xx.setDaemon(true);
    xx.start();
    }
    
    public void refreshSeek()
    {
            
        
    
    Task t2 = new Task<Void>() {
        @Override public Void call() {
            while(true)
            {
                if(songSeek.isValueChanging()== false && isSongAlreadyPlaying && isSongAsigned)
                {
                    
                        
                        double c2 = player.getCurrentTime().toMinutes();
               
                        double s2 = (c2/currentSongDuration)*100;
                        
                        songSeek.setValue(s2);
                        songLocationProgressBar.setProgress(s2/100);

                    
                }
              
                
               
                
                
                try
                {
                    Thread.sleep(500);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
               
                }
                
                  
            }
        };
    Thread xx = new Thread(t2);
    xx.setDaemon(true);
    xx.start();
     
    }
    static String tmpSongName = "";
    static String tmpArtist = "";
    static boolean isErrorx = false;
    @FXML
    public void searchForSongs() throws IOException
    {
        pBarLoading.setOpacity(1);
        loadingMusicFilesLabel.setOpacity(1);
        
        try 
        {
            //Media tmpMedia = null;
            
            File folder = new File(Lyrica.songDirectory);
            File[] listOfFiles = folder.listFiles();
            int i;
            int tmpNoOfSongs = 0;
            String tmpFileNameExtension;
            for(i = 0; i<listOfFiles.length ; i++)
            {
                 tmpFileNameExtension = FilenameUtils.getExtension(listOfFiles[i].getName());
                if(listOfFiles[i].isFile() && tmpFileNameExtension.equalsIgnoreCase("mp3"))
                {
                   
                    
                    String songName = listOfFiles[i].getName();
                    String songNameToBeProcessed = stringToUrl(songName);
                    
                   
                    
                    Media tmpMedia = new Media(fileNameAttr+Lyrica.songDirectory+"/"+songNameToBeProcessed);
                    player = new MediaPlayer(tmpMedia);
                    isErrorx = false;
                    player.setOnReady(new Runnable(){
                    @Override
                    public void run()
                    {
                        
                        try
                        {
                            tmpSongName = tmpMedia.getMetadata().get("title").toString();
                            tmpSongName = tmpSongName.replace(" ","");
                            tmpSongName = tmpSongName.replace(" ","");
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                        
                        try
                        {
                            tmpArtist = tmpMedia.getMetadata().get("artist").toString().toString();
                            tmpArtist = tmpArtist.replace(" ", "");
                            tmpArtist = tmpArtist.replace(" ", "");
                        }
                        catch(Exception e)
                        {
                            isErrorx = true;
                        }
                        
                        if(isErrorx)
                        {
                            Lyrica.albumArtList.add("NULL");
                            console.pln("ERROR");
                        }
                        else
                        {
                            filer f = new filer();
                            if(f.doesFileExists("files/albumArt/"+tmpSongName+tmpArtist+".png"))
                            {
                                Lyrica.albumArtList.add(System.getProperty("user.dir")+stringToUrl("/files/albumArt/"+tmpSongName+tmpArtist+".png"));
                            }
                            else
                            {
                                Lyrica.albumArtList.add("NULL");
                                
                            }
                        }
                    }
                    });
                    
                    if (tmpFileNameExtension.equalsIgnoreCase("mp3"))
                    {
                        
                        Lyrica.songList.add(listOfFiles[i].getName());
                        tmpNoOfSongs++;
                    }
                }
            }
            player = null;
            
            Lyrica.noOfSongs = tmpNoOfSongs;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        pBarLoading.setOpacity(0);
        loadingMusicFilesLabel.setOpacity(0);
        
        if(Lyrica.noOfSongs == 0)
        {
            itsLonelyHereLabel.setOpacity(1);
            itsLonelyHere2Label.setOpacity(1);
            checkForSongsButton.setOpacity(1);
            itsLonelyHere2Label.setText("We were not able to search for songs in '"+Lyrica.songDirectory+"' ...\nPlease copy some songs over there");
            musicInterfacePane.setOpacity(0);
            mainPane.setOpacity(1);
            mainPane.toFront();
            songListview.toBack();
        }
        else
        {
            
            checkForSongsButton.setDisable(true);
            songListview.getItems().clear();
            songListview.getItems().add("Song Name");
            int k;
            for(k=0; k<Lyrica.songList.size(); k++)
            {
                String nameToBeAdded = Lyrica.songList.get(k).toString().replace(".mp3","");
                nameToBeAdded = nameToBeAdded.replace(".MP3", "");
                songListview.getItems().add(nameToBeAdded);
            }
            songListview.setOpacity(1);
        }
    }
    
    @FXML
    public void playSelectedSong()
    {
        int selected = (songListview.getSelectionModel().getSelectedIndex() - 1);
        if(selected != -1)
        {
            
            console.pln("SELECTED : "+Lyrica.songList.get(selected));
            playSong(Lyrica.songDirectory+"/"+Lyrica.songList.get(selected), Lyrica.songList.get(selected).toString(), selected);
        }
        
        
    }
    
    public void playSong(String fileName,String fileNameWithoutLocation, int songIndex)
    {
        
        currentSongIndex = songIndex;
        playPauseButtonImage.setImage(new Image(dashboardController.class.getResourceAsStream("/resources/pause.png")));
        playPauseButtonImage1.setImage(new Image(dashboardController.class.getResourceAsStream("/resources/pauseBlack.png")));
        cL.setOpacity(1);
        pauseStartButton.setDisable(false);
        
        totalMusicInfoLabel.setOpacity(1);
        stopButton.setOpacity(1);
       
        String fname = "asds";
        
        try
        {
            upDownButton.setDisable(false);
            
            fname = stringToUrl(fileName);
            console.pln(fname);
        }
            
        catch(Exception e)
        {
            e.printStackTrace();
        }
         

        console.pln("Album Art Location : "+Lyrica.albumArtList.get(songIndex).toString());
        
        if(Lyrica.albumArtList.get(songIndex).toString().equals("NULL"))
        {
            currentAlbumArt.setImage(new Image(dashboardController.class.getResourceAsStream("/resources/noAlbumArt.png")));
        }
        else
        {
            currentAlbumArt.setImage(new Image("file:///"+Lyrica.albumArtList.get(songIndex).toString()));
        }
        
        
        Media pick = new Media(fileNameAttr+fname);
        
        String tmpcurrentSongName = fileNameWithoutLocation.replace(".mp3","");
        currentSongName = tmpcurrentSongName.replace(".MP3","");
        
        
        if(isSongAlreadyPlaying)
        {
            player.stop();
        }
        player = null;
        boolean wasAvailable = false;
        try
        {
            player = new MediaPlayer(pick);
            wasAvailable = true;
        }
        catch(Exception e)
        {
            console.pln("Unable to play music file...");
            wasAvailable = false;
        }
        
        
        
        player.setOnEndOfMedia(
                            new Runnable() {

        @Override
        public void run() {
            if(repeat)
            {
                int newSongIndex = currentSongIndex;
                String newSong = Lyrica.songList.get(newSongIndex).toString();
                String songDirectory = Lyrica.songDirectory;
                console.pln("NEXT BUTTON CLICKED");
                playSong(songDirectory+"/"+newSong,newSong,newSongIndex);
                songListview.getSelectionModel().select(newSongIndex+1);
                songListview.getFocusModel().focus(newSongIndex+1);
                songListview.scrollTo(newSongIndex+1);
            }
            else
                nextButtonClicked();
            }
                            }
                    );
        player.setOnReady(new Runnable() {

        @Override
        public void run() {
            
            
            currentTitle=tmpcurrentSongName;
            currentAlbum = "Unknown";
            currentArtist = "Unknown Artist";
            currentSongDuration = pick.getDuration().toMinutes();
            currentSongMinString = Double.toString(currentSongDuration);
            toBeChecked  = new StringBuilder();
            toBeChecked.append("0.");
            pointIndex = currentSongMinString.indexOf(".");
            toBeChecked.append(currentSongMinString.charAt(pointIndex+1));
            toBeChecked.append(currentSongMinString.charAt(pointIndex+2));
            secs = Double.parseDouble(toBeChecked.toString())*60.0;
                    
            secString = new StringBuilder();
            if (secs<10)
            {
                secString.append("0");
                secString.append(Character.toString(Double.toString(secs).charAt(0)));
            }
            else if (secs>10)
            {
                secString.append(Character.toString(Double.toString(secs).charAt(0)));
                secString.append(Character.toString(Double.toString(secs).charAt(1)));
            }
            min = (int) (currentSongDuration + (currentSongDuration - Double.parseDouble(toBeChecked.toString())));
           
            
            toBeShown = new StringBuilder();
            toBeShown.append(min);
            toBeShown.append(":");
            toBeShown.append(secString.toString());
                    
            totalMusicInfoLabel.setText(toBeShown.toString());

                console.pln("Song Duration :"+currentSongDuration);
                try
                {
                    currentTitle = pick.getMetadata().get("title").toString();
                }
                catch(Exception e)
                {
                    currentTitle = tmpcurrentSongName;
                }
            
                try
                {
                    currentAlbum = pick.getMetadata().get("album").toString();
                }
                catch(Exception e)
                {
                    currentAlbum = "Unknown";
                }
                
                try
                {
                    currentArtist = pick.getMetadata().get("artist").toString();
                }
                catch(Exception e)
                {
                    currentArtist = "Unknown Artist";
                }
                
                artistLabel.setText(currentArtist);
                artistLabel.setOpacity(1);
                refreshSongInfo(currentTitle, currentAlbum);
                console.pln(pick.getMetadata().toString());
                player.play(); 
                isSongAlreadyPlaying = true;
                isSongAsigned = true;

            }
        });
        
        
        
      
    }
    
    @FXML
    public void pauseStartButtonClicked(ActionEvent event)
    {
        if(isSongAlreadyPlaying)
        {
            playPauseButtonImage.setImage(new Image(dashboardController.class.getResourceAsStream("/resources/play.png")));
            playPauseButtonImage1.setImage(new Image(dashboardController.class.getResourceAsStream("/resources/playBlack.png")));
            isSongAlreadyPlaying = false;
            player.pause();
        }
        else
        {
            if(isSongAsigned)
            {
                playPauseButtonImage.setImage(new Image(dashboardController.class.getResourceAsStream("/resources/pause.png")));
                playPauseButtonImage1.setImage(new Image(dashboardController.class.getResourceAsStream("/resources/pauseBlack.png")));
                player.play();
                isSongAlreadyPlaying = true;
            }
        }
    }
    
    public void refreshSongInfo(String titlePassed, String albumPassed)
    {
        songNameLabel.setText(titlePassed);
        albumLabel.setText(albumPassed);
    }
    
    @FXML
    public void stopButtonClicked()
    {
        if(isSongAsigned)
        {
            upDownButton.setDisable(true);
            player.stop();
            player = null;
            isSongAlreadyPlaying = false;
            isSongAsigned = false;
            artistLabel.setOpacity(0);
            songLocationProgressBar.setProgress(0);
            isMusicInterfaceShown = false;
            aboutPane.setOpacity(0);
            /*mainPane.setStyle("-fx-opacity:1;");
            musicInterfacePane.setStyle("-fx-opacity : 0;");
            musicInterfacePane.toBack();
            mainPane.toFront();*/
            
            fadeTransition(musicInterfacePane,mainPane);
            aboutPane.setOpacity(0);
            settingsPane.setOpacity(0);
            musicInterfacePane.setOpacity(0);
            mainPane.setOpacity(1);
            mainPane.toFront();
            upDownImageView.setImage(new Image(dashboardController.class.getResourceAsStream("/resources/upWhite.png")));
            upDownButton.setDisable(true);
            settingsButton.setDisable(false);
            aboutButton.setDisable(false);
            pauseStartButton.setDisable(true);
            playPauseButtonImage.setImage(new Image(dashboardController.class.getResourceAsStream("/resources/play.png")));
            refreshSongInfo("Select A Song","To Be Played");
        }
    }
    
    @FXML
    public void quitButtonClicked(ActionEvent event)
    {
        console.pln("Quitting...");
        System.exit(0);
    }
    
    @FXML
    public void selectAnotherSongDirectory(ActionEvent event)
    {
        new mentionSongDirectory().setVisible(true);
        Node node = (Node) event.getSource();
        Stage s= (Stage) node.getScene().getWindow();
        s.close();
    }
    
    @FXML
    public void nextButtonClicked()
    {
        int newSongIndex;
        String newSong;
        String songDirectory;
        if(currentSongIndex == Lyrica.songList.size() - 1)
        {
            stopButtonClicked();
        }
        else
        {
            newSongIndex = currentSongIndex +1;
            newSong = Lyrica.songList.get(newSongIndex).toString();
            songDirectory = Lyrica.songDirectory;
            console.pln("NEXT BUTTON CLICKED");
            playSong(songDirectory+"/"+newSong,newSong,newSongIndex);
            songListview.getSelectionModel().select(newSongIndex+1);
            songListview.getFocusModel().focus(newSongIndex+1);
            songListview.scrollTo(newSongIndex+1);
        }

    }
    
    
    @FXML
    public void previousButtonClicked()
    {
        int newSongIndex;
        String newSong;
        String songDirectory;
        if(currentSongIndex == 0)
        {
            // index 1
            newSongIndex = Lyrica.songList.size() - 1;
            newSong = Lyrica.songList.get(newSongIndex).toString();
            songDirectory = Lyrica.songDirectory;
        }
        else
        {
            newSongIndex = currentSongIndex -1;
            newSong = Lyrica.songList.get(newSongIndex).toString();
            songDirectory = Lyrica.songDirectory;
        }
        console.pln("PREVIOUS BUTTON CLICKED");
        playSong(songDirectory+"/"+newSong,newSong,newSongIndex);
        songListview.getSelectionModel().select(newSongIndex+1);
        songListview.getFocusModel().focus(newSongIndex+1);
        songListview.scrollTo(newSongIndex+1);
    }
    
    
    @FXML
    public void aboutButtonClicked(ActionEvent event)
    {
        mainPane.setOpacity(0);
        aboutPane.setOpacity(1);
        musicInterfacePane.setOpacity(0);
        settingsPane.toBack();
        aboutPane.toFront();
        aboutButton.setDisable(true);
        upDownButton.setDisable(true);
        settingsButton.setDisable(true);
        upDownImageView.setImage(new Image(dashboardController.class.getResourceAsStream("/resources/upWhite.png")));
        isMusicInterfaceShown = false;
    }
    
    @FXML
    public void aboutReturnButtonClicked(ActionEvent event)
    {
        aboutPane.setStyle("-fx-opacity:0;");
        mainPane.setStyle("-fx-opacity:1;");
        musicInterfacePane.setStyle("-fx-opacity : 0;");
        upDownButton.setDisable(false);
        aboutButton.setDisable(false);
        mainPane.toFront();
        aboutButton.setDisable(false);
        settingsButton.setDisable(false);
    }
    
    @FXML
    public void newSongSeek()
    {
        double newSongTime = ((songSeek.getValue()/100)*currentSongDuration)*60000;
        player.seek(Duration.millis(newSongTime));
    }
    
    @FXML
    public void settingsButtonClicked(ActionEvent event)
    {
        mainPane.setOpacity(0);;
        settingsPane.setOpacity(1);
        aboutPane.setOpacity(0);
        musicInterfacePane.setOpacity(0);
        aboutButton.setDisable(true);
        upDownButton.setDisable(true);
        settingsButton.setDisable(true);
        settingsPane.toFront();
        upDownImageView.setImage(new Image(dashboardController.class.getResourceAsStream("/resources/upWhite.png")));
        isMusicInterfaceShown = false;
    }
    
    @FXML
    public void settingsGoBackButtonClicked(ActionEvent event)
    {
        downloadingProgressBar.setOpacity(0);
        albumArtDownloadCurrentStatusLabel.setOpacity(0);
        mainPane.setOpacity(1);
        settingsPane.setOpacity(0);
        aboutButton.setDisable(false);
        upDownButton.setDisable(false);
        settingsButton.setDisable(false);
        mainPane.toFront();
    }
    
    
    @FXML
    private void colorChooserClicked(ActionEvent event)
    {
        applyChangesButton.setDisable(false);
    }
    
    @FXML
    private void changeThemeColorButtonClicked(ActionEvent event) throws Exception
    {
        String newThemeColor = colorChooser.getValue().toString();
        newThemeColor = newThemeColor.replace("0x","#");
        newThemeColor = newThemeColor.replace("ff","");
        
        console.pln("Writing to colorConfig.dx ...");
        
        filer.writeToFile("files/colorConfig.dx", newThemeColor);
        
        console.pln("...Done!");
        
        console.pln("Changing local Theme Color...");
        
        Lyrica.themeColor = newThemeColor;
        
        console.pln("...Done!");
        colorChooser.setValue(Color.web(Lyrica.themeColor));
        headerPane.setStyle("-fx-background-color : "+Lyrica.themeColor);
        footerPane.setStyle("-fx-background-color : "+Lyrica.themeColor);   
        resetToDefaultButton.setDisable(false);
        applyChangesButton.setDisable(true);
    }
    
    @FXML
    public void resetToDefaultButtonClicked(ActionEvent event) throws Exception
    {
        String newThemeColor = "#7c43bd";
        
        console.pln("Writing to colorConfig.dx ...");
        
        filer.writeToFile("files/colorConfig.dx", newThemeColor);
        
        console.pln("...Done!");
        
        console.pln("Changing local Theme Color...");
        
        Lyrica.themeColor = newThemeColor;
        
        console.pln("...Done!");
        
        colorChooser.setValue(Color.web(Lyrica.themeColor));
        headerPane.setStyle("-fx-background-color : "+Lyrica.themeColor);
        footerPane.setStyle("-fx-background-color : "+Lyrica.themeColor);  
        resetToDefaultButton.setDisable(true);
        applyChangesButton.setDisable(true);
    }
    
    
    
    @FXML
    private void minimizeDashButtonClicked(ActionEvent event)
    {
        Lyrica.dashStage.setIconified(true);
    }
    
    @FXML
    private void upDownButtonClicked(ActionEvent event)
    {
        if(!isMusicInterfaceShown)
        {
            isMusicInterfaceShown = true;
            aboutPane.setOpacity(0);
            settingsPane.setOpacity(0);
            aboutButton.setDisable(true);
            settingsButton.setDisable(true);
            fadeTransition(mainPane,musicInterfacePane);
            musicInterfacePane.toFront();
            upDownImageView.setImage(new Image(dashboardController.class.getResourceAsStream("/resources/downWhite.png")));
        }
        else
        {
            isMusicInterfaceShown = false;
            aboutPane.setOpacity(0);
            settingsPane.setOpacity(0);
            aboutButton.setDisable(false);
            settingsButton.setDisable(false);
            fadeTransition(musicInterfacePane,mainPane);
            mainPane.toFront();
            upDownImageView.setImage(new Image(dashboardController.class.getResourceAsStream("/resources/upWhite.png")));
        }
    }
    
    
    public void fadeTransition(Node fromNode, Node toNode)
    {
        FadeTransition ft = new FadeTransition();
        ft.setDuration(Duration.millis(300));
        ft.setNode(fromNode);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        
        FadeTransition ft2 = new FadeTransition();
        ft2.setDuration(Duration.millis(300));
        ft2.setNode(toNode);
        ft2.setFromValue(0.0);
        ft2.setToValue(1.0);
        
        ft.play();
        ft2.play();
        
        fromNode.toBack();
        toNode.toFront();
    }
    
    public String stringToUrl(String input)
    {
        int j;
        String output="";            
        for(j = 0; j<input.length(); j++)
        {
            if(input.charAt(j) == '/' )
            {
                output += "/";
            }
            else if(Character.toString(input.charAt(j)).equals("\\"))
            {
                output += Character.toString(input.charAt(j));
            }
            else
            {
                if(input.charAt(j) == ' ')
                {
                    output += "%20";
                }
                else
                {
                    try
                    {
                        output+=URLEncoder.encode(Character.toString(input.charAt(j)), "UTF-8");
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        
        return output;
    }
    
    
    public String getAlbumArtLink(String currentArtistName, String currentTrackName)
    {
        String toBeReturned = "";
        try
        {
            if(!currentArtistName.equals("Unknown") && !currentAlbum.equals("Unknown"))
            {
                String urlStringRaw = "http://ws.audioscrobbler.com/2.0/?method=track.getInfo&api_key="+Lyrica.lastFmApiKey+"&artist="+currentArtistName+"&track="+currentTrackName+"&format=json";
                String urlString = urlStringRaw.replace(" ","");
                console.pln(urlString);
                URL obj = new URL(urlString);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
              	    response.append(inputLine);
                }
                in.close();
                String jsonData = response.toString();
               
                toBeReturned = new JSONObject(jsonData).getJSONObject("track").getJSONObject("album").getJSONArray("image").getJSONObject(3).get("#text").toString();
            }
            
        }
        catch(Exception e)
        {
            console.pln("INFO NOT FOUND");
            toBeReturned = "NaN";
        }
        console.pln(toBeReturned);
        return toBeReturned;
    }
    
    static int noOfErrs = 0;
    static int jx;
    static int xc=0;
    @FXML
    public void downloadAlbumArtButtonClicked(ActionEvent event)
    {
        if(player!=null)
        {
            player.stop();
            player = null;
            isSongAlreadyPlaying = false;
            isSongAsigned = false;
            artistLabel.setOpacity(0);
            songLocationProgressBar.setProgress(0);
            isMusicInterfaceShown = false;
            upDownImageView.setImage(new Image(dashboardController.class.getResourceAsStream("/resources/upWhite.png")));
            pauseStartButton.setDisable(true);
            playPauseButtonImage.setImage(new Image(dashboardController.class.getResourceAsStream("/resources/play.png")));
            refreshSongInfo("Select A Song","To Be Played");
        }
        
        
        
        returnButtonSettings.setDisable(true);
        quitButton.setDisable(true);
        upDownButton.setDisable(true);
        downloadAlbumArtButton.setDisable(true);
        downloadingProgressBar.setOpacity(1);
        albumArtDownloadCurrentStatusLabel.setOpacity(1);
        console.pln("Downloading Album Art...");
        albumArtDownloadCurrentStatusLabel.setText("Downloading Album Art...");
        
        for(jx = 0; jx<Lyrica.songList.size(); jx++)
        {
            console.pln("Checking for album Art for :"+Lyrica.songList.get(jx));
            String songUrl = stringToUrl(Lyrica.songDirectory+"/"+Lyrica.songList.get(jx));
            Media tmpMedia = new Media(fileNameAttr+songUrl);
            MediaPlayer tmpPlayer = new MediaPlayer(tmpMedia);
            tmpPlayer.setOnReady(new Runnable(){
            @Override
            public void run()
            {
                xc++;
                String artistName = "";
                String trackName = "";
                boolean isError = false;
                try
                {
                    artistName = tmpMedia.getMetadata().get("artist").toString();
                }
                catch(Exception e)
                {
                    isError = true;
                }
                
                try
                {
                    trackName = tmpMedia.getMetadata().get("title").toString();
                }
                catch(Exception e)
                {
                    isError = true;
                }
                
                if(!isError)
                {
                    console.p("Checking whether album Art is already present for "+trackName);
                    console.pln("...");
                    
                    artistName = artistName.replace(" ","");
                    trackName = trackName.replace(" ","");
                    String tmpArtistName = artistName.replace(" ","");
                    String tmpTrackName = trackName.replace(" ","");
                    String finalAlbumArtPhotoName = "files/albumArt/"+tmpTrackName+tmpArtistName+".png";
                    console.pln(finalAlbumArtPhotoName);
                    filer x = new filer();
                    if(x.doesFileExists(finalAlbumArtPhotoName))
                    {
                        console.pln("Album Art already present... no need to download!");
                    }
                    else
                    {
                        
                        String albumArtLink = getAlbumArtLink(artistName, trackName);
                    
                        if(albumArtLink.equals("NaN"))
                        {
                            console.pln("NO SUITABLE ALBUM ART FOUND...");
                        }
                        else
                        {
                            boolean isErr = false;
                            try
                            {
                                URL urlx = new URL(albumArtLink);
                                InputStream in = new BufferedInputStream(urlx.openStream());
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                byte[] buf = new byte[1024];
                                int n = 0;
                                while (-1!=(n=in.read(buf)))
                                {
                                    out.write(buf, 0, n);
                                }
                                out.close();
                                in.close();
                                byte[] response = out.toByteArray(); 
                                console.pln(finalAlbumArtPhotoName);
                                FileOutputStream fos = new FileOutputStream(finalAlbumArtPhotoName);
                                fos.write(response);
                                fos.close();
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                                isErr = true;
                            }
                        
                            if(isErr)
                            {
                                noOfErrs++;
                                console.pln("Unable to download album Art...");
                            }
                            else
                            {
                                console.pln("Downloaded image sucessfully!");
                            }
                        }
                    }
                }
                else
                {
                    console.pln("File does not meet the minimum requirements for checking for album Art");
                }
                
                if(xc==Lyrica.songList.size()-1)
                {
                    console.pln("Refreshing Album Art...");
                    //refresh the album List
                    int fx;
                    for(fx = 0; fx<Lyrica.songList.size(); fx++)
                    {
                        
                        String songUrlx = stringToUrl(Lyrica.songDirectory+"/"+Lyrica.songList.get(fx));
                        Media tmpMediax = new Media(fileNameAttr+songUrlx);
                        MediaPlayer vb = new MediaPlayer(tmpMediax);
                        Lyrica.albumArtList.clear();
                        vb.setOnReady(new Runnable(){
                        @Override
                        public void run()
                        {
                            boolean isErrorxy = false;
                            String tmpSongNam="", tmpArtis="";
                            try
                            {
                                tmpSongNam = tmpMediax.getMetadata().get("title").toString();
                                tmpSongNam = tmpSongNam.replace(" ","");
                                tmpSongNam = tmpSongNam.replace(" ","");
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        
                            try
                            {
                                tmpArtis = tmpMediax.getMetadata().get("artist").toString().toString();
                                tmpArtis = tmpArtis.replace(" ", "");
                                tmpArtis = tmpArtis.replace(" ", "");
                            }
                            catch(Exception e)
                            {
                                isErrorxy = true;
                                e.printStackTrace();
                            }
                        
                            if(isErrorxy)
                            {
                                Lyrica.albumArtList.add("NULL");
                                console.pln("ERROR");
                            }
                            else
                            {
                                console.pln("files/albumArt/"+tmpSongNam+tmpArtis+".png");
                                filer f = new filer();
                                if(f.doesFileExists("files/albumArt/"+tmpSongNam+tmpArtis+".png"))
                                {
                                    Lyrica.albumArtList.add(System.getProperty("user.dir")+stringToUrl("/files/albumArt/"+tmpSongNam+tmpArtis+".png"));
                                }
                                else
                                {
                                    Lyrica.albumArtList.add("NULL");                         
                                }
                            }
                        }
                        });
                    }
                    
                    returnButtonSettings.setDisable(false);
                    quitButton.setDisable(false);
                    downloadAlbumArtButton.setDisable(false);
                    downloadingProgressBar.setOpacity(0);
                    albumArtDownloadCurrentStatusLabel.setOpacity(0);
                }
                
                
            }
            });
            
            
        }
        
        
        
        
    }
}
