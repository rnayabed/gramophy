package Gramophy;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
import animatefx.animation.FadeOutDown;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Player {

    Media media;
    MediaPlayer mediaPlayer;

    String source;
    double totalCurr;


    boolean isPlaying = false;
    boolean isActive = false;

    private Thread updaterThread;

    int songIndex;

    Image playIcon = new Image(getClass().getResourceAsStream("assets/baseline_play_arrow_white_18dp.png"));
    Image pauseIcon = new Image(getClass().getResourceAsStream("assets/baseline_pause_white_18dp.png"));


    String currentPlaylistName = "";

    public Player(String inputPlaylistName, int inputIndex)
    {
        this.currentPlaylistName = inputPlaylistName;

        Platform.runLater(()->{
            Main.dash.musicPanePreviousButton.setDisable(false);
            Main.dash.musicPaneNextButton.setDisable(false);
            Main.dash.musicPaneShuffleButton.setDisable(false);
            Main.dash.musicPaneRepeatButton.setDisable(false);
        });

        Main.dash.songSeek.setOnMouseClicked(event -> {
            setPos((Main.dash.songSeek.getValue()/100) * totalCurr);
        });

        Main.dash.songSeek.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            sleepDurForSlider = 500;
        });

        Main.dash.songSeek.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            sleepDurForSlider = 250;
        });

        Main.dash.musicPanePlayPauseButton.setOnMouseClicked(event -> {
            pauseResume();
        });

        Main.dash.musicPaneNextButton.setOnMouseClicked(event -> {
            playNext();
        });

        Main.dash.musicPanePreviousButton.setOnMouseClicked(event -> {
            playPrevious();
        });

        playSong(inputIndex);
    }

    private void show()
    {
        if(Main.dash.musicPaneControls.getOpacity()==0)
        {
            Main.dash.musicPanePlayPauseButtonImageView.setImage(pauseIcon);
            new FadeInUp(Main.dash.musicPaneSongInfo).play();
            new FadeInUp(Main.dash.musicPaneControls).play();
            new FadeInUp(Main.dash.musicPaneMiscControls).play();
        }
    }

    public void setVolume(float volume)
    {
        if(isActive)
        {
            mediaPlayer.setVolume(volume);
            if(volume == 0.0)
                Main.dash.volumeIconImageView.setImage(Main.dash.muteIcon);
            else
                Main.dash.volumeIconImageView.setImage(Main.dash.notMuteIcon);
        }
    }


    private void playSong(int index)
    {
        x = new Thread(new Task<Void>() {
            @Override
           protected Void call(){
                try
                {
                    Platform.runLater(()->{
                        Main.dash.songSeek.setValue(0);
                        Main.dash.songSeek.setDisable(true);
                        Main.dash.totalDurLabel.setText("0:00");
                        Main.dash.totalDurLabel.setVisible(false);
                        Main.dash.nowDurLabel.setText("0:00");
                        Main.dash.nowDurLabel.setVisible(false);
                        Main.dash.musicPlayerButtonBar.setDisable(true);
                        Main.dash.musicPaneSpinner.setVisible(true);
                    });

                    HashMap<String,Object> songDetails = Main.dash.cachedPlaylist.get(currentPlaylistName).get(index);

                    songIndex = index;
                    int si = index;

                    for(Node eachNode : Main.dash.playlistListView.getItems())
                    {
                        HBox x = (HBox) eachNode;

                        if(x.getChildren().get(0).getId().equals(songIndex+""))
                        {
                            System.out.println("yay!");
                            System.out.println("'"+songIndex+"' '"+x.getChildren().get(0).getId()+"'");
                            Platform.runLater(()->Main.dash.playlistListView.getSelectionModel().select(x));
                        }
                    }

                    isActive = true;

                    if(songDetails.get("location").toString().equals("local"))
                    {
                        source = songDetails.get("source").toString();
                        Platform.runLater(()->{
                            Main.dash.songNameLabel.setText(songDetails.get("title").toString());
                            Main.dash.artistLabel.setText(songDetails.get("artist").toString());
                            if(songDetails.containsKey("album_art"))
                            {
                                try {
                                    Image x = SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream((byte[]) songDetails.get("album_art"))),null);
                                    Main.dash.albumArtImgView.setImage(x);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                Main.dash.albumArtImgView.setImage(Main.dash.defaultAlbumArt);
                            }

                            show();
                        });
                    }
                    else if(songDetails.get("location").toString().equals("youtube"))
                    {
                        Image x = new Image(songDetails.get("thumbnail").toString());
                        Platform.runLater(()->{
                            Main.dash.songNameLabel.setText(songDetails.get("title").toString());
                            Main.dash.artistLabel.setText(songDetails.get("channelTitle").toString());
                            Main.dash.albumArtImgView.setImage(x);

                            show();
                        });


                        String videoURL = songDetails.getOrDefault("videoURL","null").toString();
                        if(videoURL.equals("null"))
                        {
                            String youtubeDLQuery = "youtube-dl.exe -f 18 -g https://www.youtube.com/watch?v="+songDetails.get("videoID");
                            Process p = Runtime.getRuntime().exec(youtubeDLQuery);
                            InputStream i = p.getInputStream();
                            String result = "";
                            while(true)
                            {
                                int c = i.read();
                                if(c == -1) break;
                                result+= (char) c;
                            }

                            if(result.length() == 0)
                            {
                                Main.dash.showErrorAlert("Uh OH!","Unable to play, probably because Age Restricted. If not, check connection and try again!");
                                stop();
                                hide();
                                return null;
                            }

                            videoURL = result.substring(0,result.length()-1);
                            songDetails.put("videoURL",videoURL);
                            Main.dash.cachedPlaylist.get(currentPlaylistName).get(songIndex).put("videoURL",videoURL);
                        }


                        source = videoURL;

                    }


                    if(!isActive || index!=songIndex)
                    {
                        System.out.println("gayyy");
                        return null;
                    }

                    media = new Media(source);
                    mediaPlayer = new MediaPlayer(media);

                    media.setOnError(()-> {
                        stop();
                        media.getError().printStackTrace();
                    });

                    mediaPlayer.setOnReady(()->{

                        io.log("Start Playing ...");

                        totalCurr = media.getDuration().toSeconds();


                        Platform.runLater(()->{
                            Main.dash.songSeek.setDisable(false);
                            Main.dash.musicPlayerButtonBar.setDisable(false);
                            Main.dash.musicPaneSpinner.setVisible(false);
                            Main.dash.totalDurLabel.setText(Main.dash.getSecondsToSimpleString(media.getDuration().toSeconds()));
                            Main.dash.totalDurLabel.setVisible(true);
                            Main.dash.nowDurLabel.setVisible(true);
                            Main.dash.musicPanePlayPauseButtonImageView.setImage(pauseIcon);
                        });

                        mediaPlayer.play();
                        isPlaying = true;

                        startUpdating();
                    });

                    mediaPlayer.setOnEndOfMedia(()->{
                        if(Main.dash.isShuffle)
                            playNextRandom();
                        else
                        {
                            if(Main.dash.isRepeat) setPos(0);
                            else
                            {
                                if(songIndex==(Main.dash.cachedPlaylist.get(currentPlaylistName).size()-1))
                                {
                                    stop();
                                    hide();
                                }
                                else
                                {
                                    playNext();
                                }
                            }
                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        });
        x.setPriority(1);
        x.start();
    }
    Thread x;

    public void playNext()
    {
        if(songIndex<(Main.dash.cachedPlaylist.get(currentPlaylistName).size()-1))
        {
            if(isPlaying)
            {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
            playSong((songIndex+1));
            System.out.println("gay");
        }
    }

    private void playNextRandom()
    {
        if(Main.dash.isRepeat) setPos(0);
        else
        {
            if(isPlaying)
            {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
            mediaPlayer.dispose();
            playSong((new Random().nextInt(Main.dash.cachedPlaylist.get(currentPlaylistName).size())));
        }
    }

    public void playPrevious()
    {
        if(songIndex>0)
        {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            playSong((songIndex-1));
        }
    }

    public Player()
    {
        isPlaying = false;
        isActive = false;
    }

    public void setPos(double newDurSecs)
    {
        mediaPlayer.seek(new Duration(newDurSecs*1000));
    }

    public void pauseResume()
    {
        new Thread(new Task<>() {
            @Override
            protected Object call() throws Exception {
                if(mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED))
                {
                    isPlaying = true;
                    Main.dash.musicPanePlayPauseButtonImageView.setImage(pauseIcon);
                    mediaPlayer.play();
                }
                else if(mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING))
                {
                    isPlaying = false;
                    Main.dash.musicPanePlayPauseButtonImageView.setImage(playIcon);
                    mediaPlayer.pause();
                }
                return null;
            }
        }).start();
    }

    public void stop()
    {
        if(isPlaying)
        {
            isPlaying = false;
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        isActive = false;
    }

    public void hide()
    {
        new FadeOutDown(Main.dash.musicPaneSongInfo).play();
        new FadeOutDown(Main.dash.musicPaneControls).play();
        FadeOutDown x = new FadeOutDown(Main.dash.musicPaneMiscControls);
        x.setOnFinished(event -> Platform.runLater(()->{
            Main.dash.songNameLabel.setText("");
            Main.dash.artistLabel.setText("");
            Main.dash.albumArtImgView.setImage(Main.dash.defaultAlbumArt);
        }));
        x.play();
    }

    int sleepDurForSlider = 200;
    private void startUpdating()
    {
        updaterThread = new Thread(new Task<Void>() {
            @Override
            protected Void call(){
                try
                {
                    while(isActive)
                    {
                        if(mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING))
                        {
                            double currSec = mediaPlayer.getCurrentTime().toSeconds();
                            String currentSimpleTimeStamp = Main.dash.getSecondsToSimpleString(currSec);
                            Platform.runLater(()->Main.dash.nowDurLabel.setText(currentSimpleTimeStamp));

                            double currentProgress = (currSec/totalCurr)*100;
                            if(!Main.dash.songSeek.isValueChanging())
                            {
                                Main.dash.songSeek.setValue(currentProgress);
                                currentP = currentProgress;
                            }
                        }
                        Thread.sleep(sleepDurForSlider);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        });

        updaterThread.start();
    }

    double currentP = 0.0;
}
