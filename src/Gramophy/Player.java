package Gramophy;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
import animatefx.animation.FadeOutDown;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
            if(isActive)
            {
                new Thread(new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        setPos((Main.dash.songSeek.getValue()/100) * totalCurr);
                        return null;
                    }
                }).start();
            }
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

        if(Main.dash.musicPaneControls.getOpacity()==0)
        {
            Platform.runLater(()->{
                Main.dash.musicPanePlayPauseButtonImageView.setImage(pauseIcon);
                new FadeInUp(Main.dash.musicPaneSongInfo).play();
                new FadeInUp(Main.dash.musicPaneControls).play();
                new FadeInUp(Main.dash.musicPaneMiscControls).play();
            });
        }

        playSong(inputIndex);
    }

    private void playSong(int index)
    {
        new Thread(new Task<Void>() {
            @Override
            protected Void call(){
                try
                {
                    Platform.runLater(()->{
                        Main.dash.songSeek.setValue(0);
                        Main.dash.songSeek.setDisable(true);
                        Main.dash.totalDurLabel.setText("0:00");
                        Main.dash.nowDurLabel.setText("0:00");
                        Main.dash.musicPlayerButtonBar.setDisable(true);
                        Main.dash.musicPaneSpinner.setProgress(-1);
                    });

                    HashMap<String,Object> songDetails = dashController.cachedPlaylist.get(currentPlaylistName).get(index);

                    songIndex = index;

                    if(songDetails.get("location").toString().equals("local"))
                    {
                        source = songDetails.get("source").toString();
                        Platform.runLater(()->{
                            Main.dash.songNameLabel.setText(songDetails.get("title").toString());
                            Main.dash.artistLabel.setText(songDetails.get("artist").toString());
                            Main.dash.albumArtImgView.setImage((Image) songDetails.get("album_art"));
                        });
                    }
                    else if(songDetails.get("location").toString().equals("youtube"))
                    {
                        Image x = new Image(songDetails.get("thumbnail").toString());
                        Platform.runLater(()->{
                            Main.dash.songNameLabel.setText(songDetails.get("title").toString());
                            Main.dash.artistLabel.setText(songDetails.get("channelTitle").toString());
                            Main.dash.albumArtImgView.setImage(x);
                        });

                        String videoURL = songDetails.getOrDefault("videoURL","null").toString();
                        if(videoURL.equals("null"))
                        {
                            String youtubeDLQuery = "youtube-dl.exe -f 18 -g https://www.youtube.com/watch?v="+songDetails.get("videoID");
                            System.out.println(youtubeDLQuery);
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
                                System.out.println("ERROR!");
                                return null;
                            }

                            videoURL = result.substring(0,result.length()-1);
                            songDetails.put("videoURL",videoURL);
                            dashController.cachedPlaylist.get(currentPlaylistName).get(songIndex).put("videoURL",videoURL);
                        }

                        source = videoURL;

                        System.out.println(source);
                    }

                    System.out.println(songIndex+", "+(dashController.cachedPlaylist.get(currentPlaylistName).size()-1));


                    Platform.runLater(()->{
                        Main.dash.songSeek.setDisable(false);
                        Main.dash.musicPlayerButtonBar.setDisable(false);
                        Main.dash.musicPaneSpinner.setProgress(0);;
                    });

                    media = new Media(source);
                    mediaPlayer = new MediaPlayer(media);

                    media.setOnError(()-> {
                        media.getError().printStackTrace();
                    });


                    mediaPlayer.setOnReady(()->{
                        io.log("Start Playing ...");

                        totalCurr = media.getDuration().toSeconds();


                        Platform.runLater(()->{
                            Main.dash.totalDurLabel.setText(Main.dash.getSecondsToSimpleString(media.getDuration().toSeconds()));
                        });

                        play();
                        startUpdating();
                    });

                    mediaPlayer.setOnEndOfMedia(()->{
                        if(Main.dash.isRepeat)
                            setPos(0);
                        else
                        {
                            if(Main.dash.isShuffle)
                                playNextRandom();
                            else
                                playNext();
                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        }).start();
    }

    private void playNext()
    {
        if(songIndex<(dashController.cachedPlaylist.get(currentPlaylistName).size()-1))
        {
            mediaPlayer.stop();
            playSong((songIndex+1));
        }
    }

    private void playNextRandom()
    {
        mediaPlayer.stop();
        playSong((new Random().nextInt(dashController.cachedPlaylist.get(currentPlaylistName).size())));
    }

    private void playPrevious()
    {
        if(songIndex>0)
        {
            mediaPlayer.stop();
            playSong((songIndex-1));
        }
    }

    public Player()
    {
        isPlaying = false;
        isActive = false;
    }

    private void play()
    {
        System.out.println("ad");
        Main.dash.musicPanePlayPauseButtonImageView.setImage(pauseIcon);
        mediaPlayer.play();
        System.out.println("fad");
        isPlaying = true;
        isActive = true;
    }

    public void setPos(double newDurSecs)
    {
        if(isActive)
        {
            mediaPlayer.seek(new Duration(newDurSecs*1000));
        }
    }

    public void pauseResume()
    {
        if(mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED))
        {
            mediaPlayer.play();
            isPlaying = true;
            Main.dash.musicPanePlayPauseButtonImageView.setImage(pauseIcon);
        }
        else if(mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING))
        {
            mediaPlayer.pause();
            isPlaying = false;
            Main.dash.musicPanePlayPauseButtonImageView.setImage(playIcon);
        }
    }

    public void stop()
    {
        isPlaying = false;
        isActive = false;
        mediaPlayer.stop();
    }

    public void closePlayer()
    {
        new FadeOutDown(Main.dash.musicPaneSongInfo).play();
        new FadeOutDown(Main.dash.musicPaneControls).play();
        new FadeOutDown(Main.dash.musicPaneMiscControls).play();
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
                            String currentSimpleTimeStamp = Main.dash.getSecondsToSimpleString(currSec);
                            Platform.runLater(()->Main.dash.nowDurLabel.setText(currentSimpleTimeStamp));

                            double currentProgress = (currSec/totalCurr)*100;
                            if(!Main.dash.songSeek.isValueChanging())
                            {
                                Main.dash.songSeek.setValue(currentProgress);
                            }
                        }
                        Thread.sleep(100);
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
}
