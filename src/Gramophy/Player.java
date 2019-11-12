package Gramophy;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
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
import java.util.HashMap;
import java.util.Random;

public class Player {

    Media media;
    MediaPlayer mediaPlayer;

    String source;
    double totalCurr;

    HashMap<String, Object> songInfo;

    boolean isPlaying = false;
    boolean isActive = false;

    private Thread updaterThread;

    int songIndex;

    Image playIcon = new Image(getClass().getResourceAsStream("assets/baseline_play_arrow_white_18dp.png"));
    Image pauseIcon = new Image(getClass().getResourceAsStream("assets/baseline_pause_white_18dp.png"));

    int songType;

    public Player(String inputSource, int type)
    {
        try
        {
            Platform.runLater(()->{
                Main.dash.songSeek.setValue(0);
                Main.dash.songSeek.setDisable(true);
                Main.dash.totalDurLabel.setText("0:00");
                Main.dash.nowDurLabel.setText("0:00");
                Main.dash.musicPlayerButtonBar.setDisable(true);
            });

            if(type == 1)
            {
                source = inputSource;

                int i = 0;
                for(HashMap<String,Object> songDetails : Main.dash.songs)
                {
                    if(songDetails.get("source").toString().equals(source))
                    {
                        songIndex = i;
                    }
                    i++;
                }

                if(songIndex == (Main.dash.songs.size() - 1))
                    Main.dash.musicPaneNextButton.setDisable(true);
                else
                    Main.dash.musicPaneNextButton.setDisable(false);

                if(songIndex == 0)
                    Main.dash.musicPanePreviousButton.setDisable(true);
                else
                    Main.dash.musicPanePreviousButton.setDisable(false);

                Main.dash.musicPaneShuffleButton.setDisable(false);
            }
            else if(type == 2)
            {
                String[] inputSourceArranged = inputSource.split("::");
                Platform.runLater(()->{
                    Main.dash.youtubeLoadingSpinner.setProgress(-1);
                    Main.dash.songNameLabel.setText(inputSourceArranged[2]);
                    Main.dash.artistLabel.setText(inputSourceArranged[3]);
                    System.out.println(inputSourceArranged[1]);
                    Main.dash.albumArtImgView.setImage(new Image(inputSourceArranged[1]));

                    Main.dash.musicPaneNextButton.setDisable(true);
                    Main.dash.musicPanePreviousButton.setDisable(true);
                    Main.dash.musicPaneShuffleButton.setDisable(true);
                });
                String youtubeDLQuery = "youtube-dl.exe -f 18 -g https://www.youtube.com/watch?v="+inputSourceArranged[0];
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
                    return;
                }

                source = result.substring(0,result.length()-1);

                System.out.println(source);
            }

            Platform.runLater(()->{
                Main.dash.songSeek.setDisable(false);
                Platform.runLater(()->Main.dash.youtubeLoadingSpinner.setProgress(0));
                Main.dash.musicPlayerButtonBar.setDisable(false);
            });

            System.out.println("asdxx");
            media = new Media(source);
            System.out.println("asdxxxaax");
            mediaPlayer = new MediaPlayer(media);
            System.out.println("acvxsdxx");

            media.setOnError(()-> {
                media.getError().printStackTrace();
            });


            mediaPlayer.setOnReady(()->{
                io.log("Start Playing ...");

                totalCurr = media.getDuration().toSeconds();

                if(type == 1)
                {
                    songInfo = Main.dash.songs.get(songIndex);
                    Platform.runLater(()->{
                        Main.dash.songNameLabel.setText(songInfo.get("title").toString());
                        Main.dash.artistLabel.setText(songInfo.get("artist").toString());
                        Main.dash.albumArtImgView.setImage((Image) songInfo.get("album_art"));
                    });
                }

                if(Main.dash.musicPaneControls.getOpacity()==0)
                {
                    Platform.runLater(()->{
                        Main.dash.musicPanePlayPauseButtonImageView.setImage(pauseIcon);
                        new FadeInUp(Main.dash.musicPaneControls).play();
                        new FadeInUp(Main.dash.songInfoMusicPane).play();
                    });
                }

                Platform.runLater(()->{
                    Main.dash.totalDurLabel.setText(Main.dash.getSecondsToSimpleString(media.getDuration().toSeconds()));
                });

                play();
                startUpdating();
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

            mediaPlayer.setOnEndOfMedia(()->{
                if(Main.dash.isRepeat)
                    setPos(0);
                else
                {
                    if(type==1)
                    {
                        if(Main.dash.isShuffle)
                            playNextRandom();
                        else
                            playNext();
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void playNext()
    {
        stop();
        Main.dash.player = new Player(Main.dash.songs.get((songIndex + 1)).get("source").toString(),1);
    }

    private void playNextRandom()
    {
        stop();
        Main.dash.player = new Player(Main.dash.songs.get(new Random().nextInt(Main.dash.songs.size())).get("source").toString(),1);
    }

    private void playPrevious()
    {
        if(songIndex>0)
        {
            stop();
            Main.dash.player = new Player(Main.dash.songs.get((songIndex - 1)).get("source").toString(),1);
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
