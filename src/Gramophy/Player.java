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

    dashController dash;
    String youtubeExecName = "youtube-dl.exe";

    public Player()
    {
        isPlaying = false;
        isActive = false;
    }
    public Player(String inputPlaylistName, int inputIndex, dashController td, boolean isUnix)
    {
        if(isUnix) youtubeExecName = "./youtube-dl";
        dash = td;
        this.currentPlaylistName = inputPlaylistName;

        Platform.runLater(()->{
            dash.musicPanePreviousButton.setDisable(false);
            dash.musicPaneNextButton.setDisable(false);
            dash.musicPaneShuffleButton.setDisable(false);
            dash.musicPaneRepeatButton.setDisable(false);
        });

        dash.songSeek.setOnMouseClicked(event -> {
            setPos((dash.songSeek.getValue()/100) * totalCurr);
            //dash.refreshSlider(dash.songSeek);
        });

        dash.musicPanePlayPauseButton.setOnMouseClicked(event -> {
            pauseResume();
        });

        dash.musicPaneNextButton.setOnMouseClicked(event -> {
            playNext();
        });

        dash.musicPanePreviousButton.setOnMouseClicked(event -> {
            playPrevious();
        });

        playSong(inputIndex);
    }

    private void show()
    {
        if(dash.musicPaneControls.getOpacity()==0)
        {
            dash.musicPanePlayPauseButtonImageView.setImage(pauseIcon);
            new FadeInUp(dash.musicPaneSongInfo).play();
            new FadeInUp(dash.musicPaneControls).play();
            new FadeInUp(dash.musicPaneMiscControls).play();
        }
    }

    public void setVolume(float volume)
    {
        if(isActive)
        {
            mediaPlayer.setVolume(volume);
            if(volume == 0.0)
                dash.volumeIconImageView.setImage(dash.muteIcon);
            else
                dash.volumeIconImageView.setImage(dash.notMuteIcon);
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
                        dash.songSeek.setValue(0);
                        dash.songSeek.setDisable(true);
                        dash.totalDurLabel.setText("0:00");
                        dash.totalDurLabel.setVisible(false);
                        dash.nowDurLabel.setText("0:00");
                        dash.nowDurLabel.setVisible(false);
                        dash.musicPlayerButtonBar.setDisable(true);
                        dash.musicPaneSpinner.setVisible(true);
                        dash.albumArtImgView.setOpacity(0.7);
                    });

                    HashMap<String,Object> songDetails = dash.cachedPlaylist.get(currentPlaylistName).get(index);

                    songIndex = index;

                    isActive = true;

                    if(songDetails.get("location").toString().equals("local"))
                    {
                        source = songDetails.get("source").toString();
                        Platform.runLater(()->{
                            dash.songNameLabel.setText(songDetails.get("title").toString());
                            dash.artistLabel.setText(songDetails.get("artist").toString());
                            if(songDetails.containsKey("album_art"))
                            {
                                try {
                                    Image x = SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream((byte[]) songDetails.get("album_art"))),null);
                                    dash.albumArtImgView.setImage(x);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                dash.albumArtImgView.setImage(dash.defaultAlbumArt);
                            }

                            show();
                        });
                    }
                    else if(songDetails.get("location").toString().equals("youtube"))
                    {
                        Image x = new Image(songDetails.get("thumbnail").toString());
                        Platform.runLater(()->{
                            dash.songNameLabel.setText(songDetails.get("title").toString());
                            dash.artistLabel.setText(songDetails.get("channelTitle").toString());
                            dash.albumArtImgView.setImage(x);

                            show();
                        });


                        String videoURL = songDetails.getOrDefault("videoURL","null").toString();
                        if(videoURL.equals("null"))
                        {
                            String youtubeDLQuery = youtubeExecName +" -f 18 -g https://www.youtube.com/watch?v="+songDetails.get("videoID");
                            Process p = Runtime.getRuntime().exec(youtubeDLQuery);
                            InputStream i = p.getInputStream();
                            InputStream e = p.getErrorStream();

                            String result = "";
                            while(true)
                            {
                                int c = i.read();
                                if(c == -1) break;
                                result+= (char) c;
                            }

                            if(result.length() == 0)
                            {
                                //get errors
                                String errResult = "";
                                while(true)
                                {
                                    int c = e.read();
                                    if(c == -1) break;
                                    errResult+= (char) c;
                                }

                                dash.showErrorAlert("Uh OH!","Unable to play, probably because Age Restricted/Live Video. If not, check connection and try again!\n\n"+errResult);
                                stop();
                                hide();
                                return null;
                            }

                            videoURL = result.substring(0,result.length()-1);
                            System.out.println(videoURL);
                            songDetails.put("videoURL",videoURL);
                            dash.cachedPlaylist.get(currentPlaylistName).get(songIndex).put("videoURL",videoURL);
                        }


                        source = videoURL;

                    }


                    if(!isActive || index!=songIndex)
                    {
                        System.out.println("gayyy");
                        return null;
                    }

                    System.out.println("starting ...");

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
                            dash.songSeek.setDisable(false);
                            dash.musicPlayerButtonBar.setDisable(false);
                            dash.musicPaneSpinner.setVisible(false);
                            dash.albumArtImgView.setOpacity(1.0);
                            dash.totalDurLabel.setText(dash.getSecondsToSimpleString(media.getDuration().toSeconds()));
                            dash.totalDurLabel.setVisible(true);
                            dash.nowDurLabel.setVisible(true);
                            dash.musicPanePlayPauseButtonImageView.setImage(pauseIcon);
                        });

                        mediaPlayer.play();
                        isPlaying = true;

                        startUpdating();
                    });

                    mediaPlayer.setOnEndOfMedia(()->{
                        if(dash.isShuffle)
                            playNextRandom();
                        else
                        {
                            if(dash.isRepeat) setPos(0);
                            else
                            {
                                if(songIndex==(dash.cachedPlaylist.get(currentPlaylistName).size()-1))
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


                    for(Node eachNode : dash.playlistListView.getItems())
                    {
                        HBox x = (HBox) eachNode;

                        if(x.getChildren().get(0).getId().equals(songIndex+""))
                        {
                            System.out.println("yay!");
                            System.out.println("'"+songIndex+"' '"+x.getChildren().get(0).getId()+"'");
                            Platform.runLater(()->dash.playlistListView.getSelectionModel().select(x));
                        }
                    }
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
        if(songIndex<(dash.cachedPlaylist.get(currentPlaylistName).size()-1))
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
        if(dash.isRepeat) setPos(0);
        else
        {
            if(isPlaying)
            {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
            mediaPlayer.dispose();
            playSong((new Random().nextInt(dash.cachedPlaylist.get(currentPlaylistName).size())));
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
                    dash.musicPanePlayPauseButtonImageView.setImage(pauseIcon);
                    mediaPlayer.play();
                }
                else if(mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING))
                {
                    isPlaying = false;
                    dash.musicPanePlayPauseButtonImageView.setImage(playIcon);
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
            try
            {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
            catch (Exception e)
            {
                System.out.println("disposing ...");
            }
        }
        isActive = false;
    }

    public void hide()
    {
        new FadeOutDown(dash.musicPaneSongInfo).play();
        new FadeOutDown(dash.musicPaneControls).play();
        FadeOutDown x = new FadeOutDown(dash.musicPaneMiscControls);
        x.setOnFinished(event -> Platform.runLater(()->{
            dash.songNameLabel.setText("");
            dash.artistLabel.setText("");
            dash.albumArtImgView.setImage(dash.defaultAlbumArt);
        }));
        x.play();
    }

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
                            String currentSimpleTimeStamp = dash.getSecondsToSimpleString(currSec);
                            Platform.runLater(()->dash.nowDurLabel.setText(currentSimpleTimeStamp));

                            double currentProgress = (currSec/totalCurr)*100;
                            if(!dash.songSeek.isValueChanging())
                            {
                                dash.songSeek.setValue(currentProgress);
                                //dash.refreshSlider(dash.songSeek);
                                currentP = currentProgress;
                            }
                        }
                        Thread.sleep(500);
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
