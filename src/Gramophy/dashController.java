package Gramophy;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Filter;

public class dashController implements Initializable {
    @FXML
    public HBox musicPlayerPane;
    @FXML
    public ImageView albumArtImgView;
    @FXML
    public Label songNameLabel;
    @FXML
    public Label artistLabel;
    @FXML
    public HBox musicPlayerButtonBar;
    @FXML
    public Label nowDurLabel;
    @FXML
    public JFXSlider songSeek;
    @FXML
    public Label totalDurLabel;
    @FXML
    public JFXListView<HBox> musicLibraryListView;
    @FXML
    public JFXTextField musicLibrarySearchTextField;
    @FXML
    public HBox songInfoMusicPane;
    @FXML
    public JFXButton musicPanePlayPauseButton;
    @FXML
    public ImageView musicPanePlayPauseButtonImageView;
    @FXML
    public VBox musicPaneControls;
    @FXML
    public Label browseButton;
    @FXML
    public Label libraryButton;
    @FXML
    public Label settingsButton;
    @FXML
    public VBox browsePane;
    @FXML
    public VBox libraryPane;
    @FXML
    public VBox settingsPane;
    @FXML
    public JFXTextField youtubeSearchField;
    @FXML
    public JFXListView<HBox> youtubeListView;
    @FXML
    public JFXButton youtubeSearchButton;
    @FXML
    public JFXButton musicPaneNextButton;
    @FXML
    public JFXButton musicPanePreviousButton;
    @FXML
    public JFXButton musicPaneShuffleButton;
    @FXML
    public ImageView musicPaneShuffleImageView;
    @FXML
    public ImageView musicPaneRepeatImageView;
    @FXML
    public JFXButton musicPaneRepeatButton;
    @FXML
    public JFXSpinner youtubeLoadingSpinner;
    @FXML
    public StackPane alertStackPane;

    String[] allowedExtensions = {"mp3"};

    private Font robotoRegular15 = new Font("Roboto-Regular",15);

    final private Image shuffleIconWhite = new Image(getClass().getResourceAsStream("assets/baseline_shuffle_white_18dp.png"));
    final private Image shuffleIconGreen = new Image(getClass().getResourceAsStream("assets/baseline_shuffle_green_18dp.png"));

    final private Image repeatIconWhite = new Image(getClass().getResourceAsStream("assets/baseline_repeat_white_18dp.png"));
    final private Image repeatIconGreen = new Image(getClass().getResourceAsStream("assets/baseline_repeat_green_18dp.png"));

    final Paint PAINT_GREEN = Paint.valueOf("#0e9654");
    final Paint PAINT_WHITE = Paint.valueOf("#ffffff");

    ArrayList<HashMap<String,Object>> songs = new ArrayList<>();

    boolean alreadyMusicLibrarySearchRunning = false;
    Player player = new Player();

    public Thread gcThread;

    public boolean isShuffle = false;
    public boolean isRepeat = false;

    int youtubePageNo = 1;
    String youtubeQueryURLStr;
    String youtubeAPI_KEY = "AIzaSyBdCjfmdiSOzLsd2emTAmpcWv92V6le16I";
    String youtubeNextPageToken;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.dash = this;
        loadConfig();
        loadLibrary();


        musicLibrarySearchTextField.setOnKeyReleased(event -> {
            try {
                Thread.sleep(50);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if(!alreadyMusicLibrarySearchRunning)
            {
                setMusicLibraryPredicate(event);
            }
        });

        musicLibraryListView.setItems(fl);


        new Thread(new Task<Void>() {
            @Override
            protected Void call(){
                try
                {
                    while(true)
                    {
                        Thread.sleep(30000);
                        System.gc();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        });

        browseButton.setOnMouseClicked(event -> {
            switchPane(1);
        });

        libraryButton.setOnMouseClicked(event -> {
            switchPane(2);
        });

        settingsButton.setOnMouseClicked(event -> {
            switchPane(3);
        });

        switchPane(2);

        youtubeSearchButton.setOnMouseClicked(event -> {
            searchYoutube();
        });

        gcThread = new Thread(new Task<Void>(){
            @Override
            protected Void call() throws Exception
            {
                while (true)
                {
                    Thread.sleep(30000);
                    System.gc();
                }
            }
        });


        musicPaneShuffleButton.setOnMouseClicked(event -> {
            if(isShuffle)
            {
                isShuffle = false;
                musicPaneShuffleImageView.setImage(shuffleIconWhite);
            }
            else
            {
                isShuffle = true;
                musicPaneShuffleImageView.setImage(shuffleIconGreen);
            }
        });

        musicPaneRepeatButton.setOnMouseClicked(event -> {
            if(isRepeat)
            {
                isRepeat = false;
                musicPaneRepeatImageView.setImage(repeatIconWhite);
            }
            else
            {
                isRepeat = true;
                musicPaneRepeatImageView.setImage(repeatIconGreen);
            }
        });
    }

    public String oldSearchQuery = "";
    private void searchYoutube()
    {
        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                Platform.runLater(()->{
                    youtubeSearchButton.setDisable(true);
                    youtubeSearchField.setDisable(true);
                    youtubeLoadingSpinner.setProgress(-1);
                });

                try
                {
                    String youtubeSearchQuery = URLEncoder.encode(youtubeSearchField.getText(),StandardCharsets.UTF_8);

                    if(!oldSearchQuery.equals(youtubeSearchQuery))
                    {
                        oldSearchQuery = youtubeSearchQuery;
                        youtubePageNo = 1;
                    }

                    if(youtubePageNo==1)
                        youtubeQueryURLStr = "https://www.googleapis.com/youtube/v3/search?part=snippet&q="+youtubeSearchQuery+"&maxResults=10&key="+youtubeAPI_KEY;
                    else
                        youtubeQueryURLStr = "https://www.googleapis.com/youtube/v3/search?part=snippet&q="+youtubeSearchQuery+"&maxResults=10&pageToken="+youtubeNextPageToken+"&key="+youtubeAPI_KEY;

                    InputStream is = new URL(youtubeQueryURLStr).openStream();

                    BufferedReader bf = new BufferedReader(new InputStreamReader(is));

                    StringBuilder sb = new StringBuilder();

                    while(true)
                    {
                        int charNo = bf.read();
                        if(charNo == -1) break;
                        sb.append((char) charNo);
                    }

                    bf.close();

                    JSONObject youtubeResponse = new JSONObject(sb.toString());

                    youtubeNextPageToken = youtubeResponse.getString("nextPageToken");

                    JSONArray resultsArray = youtubeResponse.getJSONArray("items");

                    if(youtubePageNo == 1)
                    {
                        Platform.runLater(()->{
                            youtubeListView.getItems().clear();
                            youtubeListView.setVisible(false);
                        });
                    }
                    else
                    {
                        Platform.runLater(()->{
                            youtubeListView.getItems().remove(youtubeListView.getItems().size()-1);
                        });
                    }

                    for(int i = 0;i<resultsArray.length();i++)
                    {
                        JSONObject eachItem = resultsArray.getJSONObject(i);

                        JSONObject idObj = eachItem.getJSONObject("id");
                        String kindObj = idObj.getString("kind");


                        if(kindObj.equals("youtube#video"))
                        {
                            JSONObject snippet = eachItem.getJSONObject("snippet");

                            String title = snippet.getString("title");
                            String channelTitle = snippet.getString("channelTitle");

                            JSONObject thumbnail = snippet.getJSONObject("thumbnails");
                            String defaultThumbnailURL = thumbnail.getJSONObject("default").getString("url");

                            String videoId = idObj.getString("videoId");

                            ImageView thumbnailImgView = new ImageView(new Image(defaultThumbnailURL));
                            Label titleLabel = new Label(title);
                            titleLabel.setFont(robotoRegular15);
                            Label channelTitleLabel = new Label(channelTitle);
                            channelTitleLabel.setFont(robotoRegular15);
                            VBox vbox = new VBox(titleLabel,channelTitleLabel);
                            vbox.setSpacing(5);
                            HBox videoHBox = new HBox(thumbnailImgView,vbox);
                            videoHBox.setSpacing(10);


                            videoHBox.setOnMouseClicked(event -> {
                                new Thread(new Task<Void>() {
                                    @Override
                                    protected Void call(){
                                        if(player.isActive)
                                        {
                                            player.stop();
                                        }
                                        player = new Player(videoId+"::"+defaultThumbnailURL+"::"+title+"::"+channelTitle+"::",2);
                                        return null;
                                    }
                                }).start();
                            });


                            Platform.runLater(()-> youtubeListView.getItems().add(videoHBox));
                        }
                    }

                    if(youtubeListView.getItems().size()>0)
                    {
                        Thread.sleep(100);
                        JFXButton loadMoreButton = new JFXButton("Load More");
                        loadMoreButton.setFont(robotoRegular15);
                        loadMoreButton.setTextFill(PAINT_GREEN);
                        youtubeListView.getItems().add(new HBox(loadMoreButton));
                        loadMoreButton.setOnMouseClicked(event -> {
                            youtubePageNo++;
                            searchYoutube();
                        });
                    }

                    Platform.runLater(()-> youtubeListView.setVisible(true));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                Platform.runLater(()->{
                    youtubeListView.setDisable(false);
                    youtubeSearchButton.setDisable(false);
                    youtubeSearchField.setDisable(false);
                    youtubeLoadingSpinner.setProgress(0);
                });
                return null;
            }
        }).start();
    }

    private void switchPane(int paneNo)
    {
        // 1 : Browse, 2 : Library, 3: Settings
        if(currentSelectedPane!=paneNo)
        {
            if(paneNo == 1)
            {
                browseButton.setTextFill(PAINT_GREEN);
                libraryButton.setTextFill(PAINT_WHITE);
                settingsButton.setTextFill(PAINT_WHITE);
                browsePane.toFront();
                FadeInUp fiu = new FadeInUp(browsePane);
                fiu.setSpeed(2.0);
                fiu.play();
            }
            else if(paneNo == 2)
            {
                browseButton.setTextFill(PAINT_WHITE);
                libraryButton.setTextFill(PAINT_GREEN);
                settingsButton.setTextFill(PAINT_WHITE);
                libraryPane.toFront();
                FadeInUp fiu = new FadeInUp(libraryPane);
                fiu.setSpeed(2.0);
                fiu.play();
            }
            else if(paneNo == 3)
            {
                browseButton.setTextFill(PAINT_WHITE);
                libraryButton.setTextFill(PAINT_WHITE);
                settingsButton.setTextFill(PAINT_GREEN);
                settingsPane.toFront();
                FadeInUp fiu = new FadeInUp(settingsPane);
                fiu.setSpeed(2.0);
                fiu.play();
            }
            musicPlayerPane.toFront();
            currentSelectedPane = paneNo;
        }
    }

    int currentSelectedPane = 0;

    private void setMusicLibraryPredicate(Event event)
    {
        alreadyMusicLibrarySearchRunning = true;
        fl.setPredicate(eachHBox ->{
            Label titleLabel = (Label) eachHBox.getChildren().get(0);
            String title = titleLabel.getText();
            JFXTextField x = (JFXTextField) event.getSource();
            if(title.toLowerCase().contains(x.getText().toLowerCase()))
                return true;
            else
                return false;
        });
        alreadyMusicLibrarySearchRunning = false;
    }

    private HashMap<String,String> config = new HashMap<>();

    private void loadConfig()
    {
        String[] configArr = io.readFileArranged("config","::");

        String musicLibDir = configArr[0];
        if(musicLibDir.equals("NULL"))
            musicLibDir = System.getProperty("user.home")+"/Music/";

        config.put("music_lib_path",musicLibDir);
    }

    ObservableList<HBox> liblists = FXCollections.observableArrayList();
    FilteredList<HBox> fl = new FilteredList<>(liblists, e -> true);

    private void loadLibrary()
    {
        new Thread(new Task<Void>() {
            @Override
            protected Void call(){
                try
                {
                    io.log("Loading music library ...");

                    File[] songsFiles = io.getFilesInFolder(config.get("music_lib_path"),allowedExtensions);

                    for(File eachSong : songsFiles)
                    {
                        Media m = new Media(eachSong.toURI().toString());
                        MediaPlayer tmpPlayer = new MediaPlayer(m);

                        tmpPlayer.setOnReady(()->{
                            ObservableMap<String,Object> songMetadata = m.getMetadata();

                            HashMap<String, Object> songDetails = new HashMap<>();
                            songDetails.put("source",m.getSource());
                            songDetails.put("duration",getSecondsToSimpleString(m.getDuration().toSeconds()));
                            songDetails.put("album_art",songMetadata.get("image"));
                            songDetails.put("artist",songMetadata.get("artist"));
                            songDetails.put("title",songMetadata.get("title"));

                            songs.add(songDetails);

                            Label title = new Label(songMetadata.get("title").toString());
                            title.setFont(robotoRegular15);
                            title.setPrefWidth(500);
                            Label artist = new Label(songMetadata.get("artist").toString());
                            artist.setFont(robotoRegular15);
                            artist.setPrefWidth(370);
                            Label duration = new Label(getSecondsToSimpleString(m.getDuration().toSeconds()));
                            duration.setFont(robotoRegular15);
                            duration.setPrefWidth(95);

                            HBox eachMusicHBox = new HBox(title, artist,duration);
                            eachMusicHBox.setAlignment(Pos.CENTER_LEFT);
                            eachMusicHBox.setSpacing(10);
                            eachMusicHBox.setCache(true);
                            eachMusicHBox.setCacheHint(CacheHint.SPEED);
                            eachMusicHBox.setId(m.getSource());

                            eachMusicHBox.setOnMouseClicked(event -> {
                                if(player.isActive)
                                    player.stop();
                                player = new Player(m.getSource(),1);
                            });

                            liblists.add(eachMusicHBox);
                        });
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        }).start();
    }

    public String getSecondsToSimpleString(double userSeconds)
    {
        double mins = userSeconds/60;
        String minsStr = mins + "";
        int index = minsStr.indexOf('.');
        String str1 = minsStr.substring(0,index);
        String minsStr2 = minsStr.substring(index+1);
        double secs = Double.parseDouble("0."+minsStr2) * 60;
        String str2 = (int) secs+"";
        if(secs<10) str2 = 0 + str2;
        return str1+":"+str2;
    }

    private final Paint WHITE_PAINT = Paint.valueOf("#ffffff");
    public void showErrorAlert(String heading, String content)
    {
        JFXDialogLayout l = new JFXDialogLayout();
        l.getStyleClass().add("dialog_style");
        Label headingLabel = new Label(heading);
        headingLabel.setTextFill(WHITE_PAINT);
        headingLabel.setFont(Font.font("Roboto Regular",25));
        l.setHeading(headingLabel);
        Label contentLabel = new Label(content);
        contentLabel.setFont(Font.font("Roboto Regular",15));
        contentLabel.setTextFill(WHITE_PAINT);
        contentLabel.setWrapText(true);
        l.setBody(contentLabel);
        JFXButton okButton = new JFXButton("OK");
        okButton.setTextFill(WHITE_PAINT);
        l.setActions(okButton);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                alertStackPane.getChildren().clear();
            }
        });

        JFXDialog alertDialog = new JFXDialog(alertStackPane,l, JFXDialog.DialogTransition.CENTER);
        alertDialog.setOverlayClose(false);
        alertDialog.getStyleClass().add("dialog_box");
        okButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                alertDialog.close();
                alertDialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
                    @Override
                    public void handle(JFXDialogEvent event) {
                        alertStackPane.toBack();
                    }
                });
            }
        });


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                alertStackPane.toFront();
                alertDialog.show();
            }
        });

    }
}
