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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.management.ObjectName;
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
    public JFXListView<HBox> playlistListView;
    @FXML
    public JFXButton playlistImportSongsFromYouTubePlaylistButton;
    @FXML
    public HBox musicPaneSongInfo;
    @FXML
    public HBox musicPaneMiscControls;
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
    public Label playlistButton;
    @FXML
    public VBox browsePane;
    @FXML
    public VBox libraryPane;
    @FXML
    public VBox settingsPane;
    @FXML
    public VBox playlistPane;
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
    public JFXSpinner musicPaneSpinner;
    @FXML
    public Label playlistNameLabel;
    @FXML
    public JFXMasonryPane playlistsMasonryPane;
    @FXML
    public ScrollPane playlistsScrollPane;
    @FXML
    public StackPane alertStackPane;
    @FXML
    public StackPane importSongsFromYouTubePopupStackPane;

    String[] allowedExtensions = {"mp3"};

    private Font robotoRegular15 = new Font("Roboto-Regular",15);
    private Font robotoRegular35 = new Font("Roboto-Regular",35);

    final private Image shuffleIconWhite = new Image(getClass().getResourceAsStream("assets/baseline_shuffle_white_18dp.png"));
    final private Image shuffleIconGreen = new Image(getClass().getResourceAsStream("assets/baseline_shuffle_green_18dp.png"));

    final private Image repeatIconWhite = new Image(getClass().getResourceAsStream("assets/baseline_repeat_white_18dp.png"));
    final private Image repeatIconGreen = new Image(getClass().getResourceAsStream("assets/baseline_repeat_green_18dp.png"));

    final Paint PAINT_GREEN = Paint.valueOf("#0e9654");
    final Paint PAINT_WHITE = Paint.valueOf("#ffffff");

    static HashMap<String,ArrayList<HashMap<String,Object>>> cachedPlaylist = new HashMap<>();

    Player player = new Player();

    public Thread gcThread;

    public boolean isShuffle = false;
    public boolean isRepeat = false;


    String currentPlaylist = "";
    int youtubePageNo = 1;
    String youtubeQueryURLStr;
    String youtubeAPI_KEY = "AIzaSyBdCjfmdiSOzLsd2emTAmpcWv92V6le16I";
    String youtubeNextPageToken;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.dash = this;
        loadConfig();
        loadLibrary();

        browseButton.setOnMouseClicked(event -> {
            switchPane(1);
        });

        libraryButton.setOnMouseClicked(event -> {
            switchPane(2);
        });

        playlistButton.setOnMouseClicked(event -> {
            switchPane(3);
        });

        settingsButton.setOnMouseClicked(event -> {
            switchPane(4);
        });

        switchPane(2);

        youtubeSearchButton.setOnMouseClicked(event -> {
            searchYouTube();
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

        gcThread.start();


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

    private void loadPlaylist(String playlistName)
    {
        if(!currentPlaylist.equals("YouTube"))
        {
            System.out.println("CLEAR");
            Platform.runLater(()->playlistListView.getItems().clear());
        }
        else
        {
            if(!currentPlaylist.equals(playlistName))
            {
                Platform.runLater(()->playlistListView.getItems().clear());
            }
        }

        currentPlaylist = playlistName;

        if(currentPlaylist.equals("My Music"))
            playlistImportSongsFromYouTubePlaylistButton.setVisible(false);
        else
            playlistImportSongsFromYouTubePlaylistButton.setVisible(true);

        refreshPlaylistsUI();
        updatePlaylistsFiles();
        
        ArrayList<HashMap<String,Object>> songs = cachedPlaylist.get(playlistName);

        Platform.runLater(()->playlistNameLabel.setText(playlistName));

        i2 = 0;
        for(HashMap<String,Object> eachSong : songs)
        {
            System.out.println("Asd");

            Label title = new Label();
            title.setFont(robotoRegular15);
            title.setPrefWidth(500);

            Label artist = new Label();
            artist.setFont(robotoRegular15);
            artist.setPrefWidth(370);;

            if(eachSong.get("location").toString().equals("local"))
            {
                title.setText(eachSong.get("title").toString());
                artist.setText(eachSong.get("artist").toString());
            }
            else if(eachSong.get("location").toString().equals("youtube"))
            {
                title.setText(eachSong.get("title").toString());
                artist.setText(eachSong.get("channelTitle").toString());
            }
            else
                System.out.println(eachSong.get("location").toString()+"Zxc");




            Platform.runLater(()->{
                HBox eachMusicHBox = new HBox(title, artist);
                eachMusicHBox.setAlignment(Pos.CENTER_LEFT);
                eachMusicHBox.setSpacing(10);
                eachMusicHBox.setCache(true);
                eachMusicHBox.setCacheHint(CacheHint.SPEED);
                eachMusicHBox.setId(i2+"");

                eachMusicHBox.setOnMouseClicked(event -> {
                    if(player.isActive)
                        player.stop();

                    player = new Player(playlistName,Integer.parseInt(((Node)event.getSource()).getId()));
                });

                playlistListView.getItems().add(eachMusicHBox);
                i2++;
            });

        }
    }
    
    private void refreshPlaylistsUI()
    {
        if(cachedPlaylist.size()==playlistsMasonryPane.getChildren().size()) return;

        Platform.runLater(()->playlistsMasonryPane.getChildren().clear());

        for(String eachPlaylistName : cachedPlaylist.keySet())
        {
            if(!eachPlaylistName.equals("YouTube"))
            {
                String[] playlistNameSplitArr = eachPlaylistName.split(" ");

                VBox eachPlaylistVBox = new VBox();
                eachPlaylistVBox.setPrefSize(200,200);
                eachPlaylistVBox.setPadding(new Insets(15));

                for(String e : playlistNameSplitArr)
                {
                    Label l = new Label(e);
                    l.setFont(robotoRegular35);
                    eachPlaylistVBox.getChildren().add(l);
                }

                eachPlaylistVBox.getStyleClass().add("card");

                JFXRippler r = new JFXRippler(eachPlaylistVBox);
                r.setOnMouseClicked(event -> {
                    new Thread(new Task<Void>() {
                        @Override
                        protected Void call(){
                            try
                            {
                                String playlistName = ((Node) event.getSource()).getId();
                                loadPlaylist(playlistName);
                                Thread.sleep(150);
                                if(!player.currentPlaylistName.equals(playlistName))
                                    Platform.runLater(()->switchPane(3));
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }).start();
                });
                r.setId(eachPlaylistName);
                Platform.runLater(()->playlistsMasonryPane.getChildren().add(r));
            }
        }

        VBox addNewPlaylistVBox = new VBox();
        addNewPlaylistVBox.setPrefSize(200,200);
        addNewPlaylistVBox.setPadding(new Insets(15));
        Label l = new Label("Create\nNew\nPlaylist");
        l.setFont(robotoRegular35);
        addNewPlaylistVBox.getChildren().add(l);
        addNewPlaylistVBox.getStyleClass().add("card");
        JFXRippler r = new JFXRippler(addNewPlaylistVBox);
        r.setOnMouseClicked(event -> {
            new Thread(new Task<Void>() {
                @Override
                protected Void call(){
                    try
                    {
                        JFXRippler r = (JFXRippler) event.getSource();
                        VBox c = (VBox) r.getChildren().get(0);
                        c.setSpacing(10);
                        Platform.runLater(()->c.getChildren().clear());

                        Label l = new Label("Enter Name of New Playlist");
                        l.setFont(robotoRegular15);
                        JFXTextArea pName = new JFXTextArea();
                        pName.setFont(robotoRegular35);
                        pName.setStyle("-fx-text-fill: WHITE;");
                        JFXButton addButton = new JFXButton("ADD");
                        addButton.setTextFill(PAINT_GREEN);
                        JFXButton cancelButton = new JFXButton("CANCEL");
                        cancelButton.setTextFill(Color.RED);

                        cancelButton.setOnMouseClicked(event1 -> {
                            Label l2 = new Label("Create\nNew\nPlaylist");
                            l2.setFont(robotoRegular35);
                            Platform.runLater(()->{
                                c.getChildren().clear();
                                c.getChildren().add(l2);
                            });
                        });

                        addButton.setOnMouseClicked(event1 -> {
                            if(pName.getText().length()>0)
                            {
                                cachedPlaylist.put(pName.getText(),new ArrayList<>());
                                refreshPlaylistsUI();
                                updatePlaylistsFiles();
                            }
                            else
                            {
                                showErrorAlert("Uh Oh!","Please enter a valid Playlist Name");
                            }
                        });


                        Platform.runLater(()->c.getChildren().addAll(l,pName,addButton,cancelButton));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    return null;
                }
            }).start();
        });

        Platform.runLater(()->playlistsMasonryPane.getChildren().add(r));

    }



    public String oldSearchQuery = "";
    private void searchYouTube()
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
                    ArrayList<HashMap<String,Object>> songs;
                    String youtubeSearchQuery = URLEncoder.encode(youtubeSearchField.getText(),StandardCharsets.UTF_8);

                    if(!oldSearchQuery.equals(youtubeSearchQuery))
                    {
                        oldSearchQuery = youtubeSearchQuery;
                        youtubePageNo = 1;
                        songs = new ArrayList<>();
                    }
                    else
                    {
                        songs = cachedPlaylist.get("YouTube");
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

                    int x = songs.size();

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

                            Image ix = new Image(defaultThumbnailURL);
                            ImageView thumbnailImgView = new ImageView(ix);
                            Label titleLabel = new Label(title);
                            titleLabel.setFont(robotoRegular15);
                            Label channelTitleLabel = new Label(channelTitle);
                            channelTitleLabel.setFont(robotoRegular15);
                            VBox vbox = new VBox(titleLabel,channelTitleLabel);
                            vbox.setSpacing(5);
                            HBox videoHBox = new HBox(thumbnailImgView,vbox);
                            videoHBox.setSpacing(10);
                            videoHBox.setId(x+"");
                            HBox.setHgrow(videoHBox, Priority.ALWAYS);

                            HashMap<String, Object> songDetails = new HashMap<>();
                            songDetails.put("location","youtube");
                            songDetails.put("videoID",videoId);
                            songDetails.put("thumbnail",defaultThumbnailURL);
                            songDetails.put("title",title);
                            songDetails.put("channelTitle",channelTitle);

                            songs.add(songDetails);
                            videoHBox.setOnMouseClicked(event -> {
                                new Thread(new Task<Void>() {
                                    @Override
                                    protected Void call(){
                                        if(player.isActive)
                                        {
                                            player.stop();
                                        }
                                        player = new Player("YouTube",Integer.parseInt(((Node)event.getSource()).getId()));
                                        return null;
                                    }
                                }).start();
                            });

                            JFXButton saveToPlaylistButton = new JFXButton("Save To Playlist");
                            saveToPlaylistButton.setId(x+"");
                            System.out.println("X : "+x);
                            saveToPlaylistButton.setTextFill(PAINT_GREEN);
                            saveToPlaylistButton.setOnMouseClicked(event -> {
                                new Thread(new Task<Void>() {
                                    @Override
                                    protected Void call() throws Exception {
                                        customisePlaylist(cachedPlaylist.get("YouTube").get(Integer.parseInt(((Node)event.getSource()).getId())));
                                        return null;
                                    }
                                }).start();
                            });

                            JFXButton downloadButton = new JFXButton("Download");
                            downloadButton.setTextFill(PAINT_GREEN);

                            VBox vv = new VBox(saveToPlaylistButton,downloadButton);

                            HBox mainHBox = new HBox(videoHBox,vv);
                            mainHBox.setAlignment(Pos.CENTER_LEFT);


                            Platform.runLater(()-> youtubeListView.getItems().add(mainHBox));



                            x++;
                        }
                    }

                    cachedPlaylist.put("YouTube",songs);
                    loadPlaylist("YouTube");


                    if(youtubePageNo>1)
                    {
                        if(player.currentPlaylistName.equals("YouTube"))
                            loadPlaylist("YouTube");
                    }

                    if(youtubeListView.getItems().size()>0)
                    {
                        Thread.sleep(100);
                        JFXButton loadMoreButton = new JFXButton("Load More");
                        loadMoreButton.setFont(robotoRegular15);
                        loadMoreButton.setTextFill(PAINT_GREEN);
                        Platform.runLater(()->youtubeListView.getItems().add(new HBox(loadMoreButton)));
                        loadMoreButton.setOnMouseClicked(event -> {
                            youtubePageNo++;
                            searchYouTube();
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
                playlistButton.setTextFill(PAINT_WHITE);
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
                playlistButton.setTextFill(PAINT_WHITE);
                libraryPane.toFront();
                FadeInUp fiu = new FadeInUp(libraryPane);
                fiu.setSpeed(2.0);
                fiu.play();
            }
            else if(paneNo == 3)
            {
                browseButton.setTextFill(PAINT_WHITE);
                libraryButton.setTextFill(PAINT_WHITE);
                playlistButton.setTextFill(PAINT_GREEN);
                settingsButton.setTextFill(PAINT_WHITE);
                playlistPane.toFront();
                FadeInUp fiu = new FadeInUp(playlistPane);
                fiu.setSpeed(2.0);
                fiu.play();
            }
            else if(paneNo == 4)
            {
                browseButton.setTextFill(PAINT_WHITE);
                libraryButton.setTextFill(PAINT_WHITE);
                playlistButton.setTextFill(PAINT_WHITE);
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

    private HashMap<String,String> config = new HashMap<>();

    private void loadConfig()
    {
        String[] configArr = io.readFileArranged("config","::");

        String musicLibDir = configArr[0];
        if(musicLibDir.equals("NULL"))
            musicLibDir = System.getProperty("user.home")+"/Music/";

        config.put("music_lib_path",musicLibDir);
    }


    int i2 = 0;


    int filesProcessed = 0;
    int totalFiles = 0;
    private void loadLibrary()
    {
        new Thread(new Task<Void>() {
            @Override
            protected Void call(){
                try
                {
                    io.log("Loading music library ...");

                    File[] songsFiles = io.getFilesInFolder(config.get("music_lib_path"));

                    ArrayList<HashMap<String,Object>> thisPlaylist = new ArrayList<>();

                    filesProcessed = 0;
                    totalFiles = 0;
                    for(File eachSong : songsFiles)
                    {
                        if(!eachSong.getName().endsWith(".mp3")) continue;

                        totalFiles++;
                        try
                        {
                            Media m = new Media(eachSong.toURI().toString());
                            MediaPlayer tmpPlayer = new MediaPlayer(m);

                            tmpPlayer.setOnReady(()->{
                                ObservableMap<String,Object> songMetadata = m.getMetadata();

                                HashMap<String, Object> songDetails = new HashMap<>();
                                songDetails.put("location","local");
                                songDetails.put("source",m.getSource());
                                songDetails.put("duration",getSecondsToSimpleString(m.getDuration().toSeconds()));
                                songDetails.put("album_art",songMetadata.get("image"));
                                songDetails.put("artist",songMetadata.get("artist"));
                                songDetails.put("title",songMetadata.get("title"));

                                thisPlaylist.add(songDetails);
                                System.out.println("asdx");
                                filesProcessed++;
                            });

                            tmpPlayer.setOnError(()-> {
                                tmpPlayer.getError().printStackTrace();
                                filesProcessed++;
                            });

                            Thread.sleep(150);
                        }
                        catch (MediaException e)
                        {
                            filesProcessed++;
                        }
                    }

                    while(filesProcessed<totalFiles)
                    {
                        Thread.sleep(50);
                    }

                    cachedPlaylist.put("My Music",thisPlaylist);

                    File[] playlistFiles = io.getFilesInFolder("playlists/");
                    for(File eachPlaylistFile : playlistFiles)
                    {
                        System.out.println("playlists/"+eachPlaylistFile.getName());

                        String contentRaw = io.readFileRaw("playlists/"+eachPlaylistFile.getName());
                        String[] contentArr = contentRaw.split("::");

                        String playlistName = eachPlaylistFile.getName();
                        ArrayList<HashMap<String,Object>> songs = new ArrayList<>();

                        if(!contentRaw.equals("empty"))
                        {
                            filesProcessed = 0;
                            totalFiles = 0;
                            for(int i =0;i<contentArr.length;i++)
                            {
                                totalFiles++;
                                String[] eachSongContentArr = contentArr[i].split("<>");
                                HashMap<String,Object> sd = new HashMap<>();
                                sd.put("location",eachSongContentArr[0]);
                                sd.put("source",eachSongContentArr[1]);

                                if(eachSongContentArr[0].equals("local"))
                                {
                                    Media m = new Media(eachSongContentArr[1]);
                                    MediaPlayer tmpPlayer = new MediaPlayer(m);

                                    tmpPlayer.setOnReady(()->{
                                        ObservableMap<String,Object> songMetadata = m.getMetadata();
                                        HashMap<String, Object> songDetails = new HashMap<>();
                                        songDetails.put("duration",getSecondsToSimpleString(m.getDuration().toSeconds()));
                                        songDetails.put("album_art",songMetadata.get("image"));
                                        songDetails.put("artist",songMetadata.get("artist"));
                                        songDetails.put("title",songMetadata.get("title"));
                                        thisPlaylist.add(songDetails);
                                        System.out.println("asdx");
                                        filesProcessed++;
                                    });

                                    tmpPlayer.setOnError(()-> {
                                        tmpPlayer.getError().printStackTrace();
                                        filesProcessed++;
                                    });
                                }
                                else if(eachSongContentArr[0].equals("youtube"))
                                {
                                    sd.put("videoID",eachSongContentArr[1]);
                                    sd.put("thumbnail",eachSongContentArr[2]);
                                    sd.put("title",eachSongContentArr[3]);
                                    sd.put("channelTitle",eachSongContentArr[4]);
                                    filesProcessed++;
                                }

                                songs.add(sd);
                            }

                            while(filesProcessed<totalFiles)
                            {
                                Thread.sleep(50);
                            }
                        }


                        cachedPlaylist.put(playlistName,songs);
                    }

                    refreshPlaylistsUI();
                    System.out.println("done");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        }).start();
    }

    public void customisePlaylist(HashMap<String,Object> newSong)
    {
        VBox vb = new VBox();
        vb.setSpacing(10);
        for(String playlistName : cachedPlaylist.keySet())
        {
            if(playlistName.equals("YouTube") || playlistName.equals("My Music")) continue;

            JFXRadioButton rb = new JFXRadioButton(playlistName);

            boolean found = false;
            for(HashMap<String,Object> sd : cachedPlaylist.get(playlistName))
            {
                if(sd.get("location").toString().equals(newSong.get("location").toString()))
                {
                    if(sd.get("location").toString().equals("youtube"))
                    {
                        if(sd.get("title").toString().equals(newSong.get("title").toString()) && sd.get("channelTitle").toString().equals(newSong.get("channelTitle").toString()))
                            found = true;
                    }
                    else if(sd.get("location").toString().equals("local"))
                    {
                        if(sd.get("title").toString().equals(newSong.get("title").toString()))
                            found = true;
                    }
                }
            }

            if(found) rb.setSelected(true);
            rb.setTextFill(WHITE_PAINT);
            rb.setId(playlistName);
            vb.getChildren().add(rb);
        }

        JFXDialogLayout l = new JFXDialogLayout();
        l.getStyleClass().add("dialog_style");
        Label headingLabel = new Label("Save To Playlist");
        headingLabel.setTextFill(WHITE_PAINT);
        headingLabel.setFont(Font.font("Roboto Regular",25));
        l.setHeading(headingLabel);


        l.setBody(vb);
        JFXButton confirmButton = new JFXButton("CONFIRM");
        confirmButton.setTextFill(WHITE_PAINT);
        JFXButton cancelButton = new JFXButton("CANCEL");
        cancelButton.setTextFill(Color.RED);

        l.setActions(confirmButton,cancelButton);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                importSongsFromYouTubePopupStackPane.getChildren().clear();
            }
        });

        JFXDialog popupDialog = new JFXDialog(importSongsFromYouTubePopupStackPane,l, JFXDialog.DialogTransition.CENTER);
        popupDialog.setOverlayClose(false);
        popupDialog.getStyleClass().add("dialog_box");

        confirmButton.setOnMouseClicked(event -> {
            new Thread(new Task<Void>() {
                @Override
                protected Void call() {
                    try
                    {
                        for(Node eachRadioButton : vb.getChildren())
                        {
                            JFXRadioButton eachrb = (JFXRadioButton) eachRadioButton;
                            ArrayList<HashMap<String,Object>> allSongs = cachedPlaylist.get(eachrb.getId());
                            boolean found = false;

                            HashMap<String,Object> foundSong = new HashMap<>();
                            for(HashMap<String,Object> sd : cachedPlaylist.get(eachrb.getId()))
                            {
                                if(sd.get("location").toString().equals(newSong.get("location").toString()))
                                {
                                    if(sd.get("location").toString().equals("youtube"))
                                    {
                                        if(sd.get("title").toString().equals(newSong.get("title").toString()) && sd.get("channelTitle").toString().equals(newSong.get("channelTitle").toString()))
                                        {
                                            found = true;
                                            foundSong = sd;
                                        }
                                    }
                                    else if(sd.get("location").toString().equals("local"))
                                    {
                                        if(sd.get("title").toString().equals(newSong.get("title").toString()))
                                        {
                                            found = true;
                                            foundSong = sd;
                                        }
                                    }
                                }
                            }


                            if(eachrb.isSelected())
                            {
                                if(!found) allSongs.add(newSong);
                            }
                            else
                            {
                                if(found) allSongs.remove(foundSong);
                            }

                            cachedPlaylist.replace(eachrb.getId(),allSongs);
                        }

                        updatePlaylistsFiles();
                        refreshPlaylistsUI();
                        loadPlaylist(currentPlaylist);

                        popupDialog.close();
                        popupDialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
                            @Override
                            public void handle(JFXDialogEvent event) {
                                importSongsFromYouTubePopupStackPane.toBack();
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
        });

        cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                popupDialog.close();
                popupDialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
                    @Override
                    public void handle(JFXDialogEvent event) {
                        importSongsFromYouTubePopupStackPane.toBack();
                    }
                });
            }
        });


        Platform.runLater(()->{
            importSongsFromYouTubePopupStackPane.toFront();
            popupDialog.show();
        });

    }

    public void updatePlaylistsFiles()
    {
        for(String playlistName : cachedPlaylist.keySet())
        {
            if(playlistName.equals("My Music")) continue;
            ArrayList<HashMap<String,Object>> songs = cachedPlaylist.get(playlistName);
            StringBuilder playlistFileContent = new StringBuilder();
            for(HashMap<String,Object> songDetails : songs)
            {
                playlistFileContent.append(songDetails.get("location")+"<>");
                if(songDetails.get("location").equals("youtube"))
                {
                    playlistFileContent.append(songDetails.get("videoID")+"<>");
                    playlistFileContent.append(songDetails.get("thumbnail")+"<>");
                    playlistFileContent.append(songDetails.get("title")+"<>");
                    playlistFileContent.append(songDetails.get("channelTitle")+"<>");
                }
                else
                {
                    playlistFileContent.append(songDetails.get("source")+"<>");
                }
                playlistFileContent.append("::");
            }

            if(songs.size() == 0) playlistFileContent.append("empty");

            File f = new File("playlists/"+playlistName);
            if(f.exists())
            {
                if(!playlistFileContent.toString().equals(io.readFileRaw("playlists/"+playlistName)))
                {
                    System.out.println("not equal");
                    io.writeToFile(playlistFileContent.toString(),"playlists/"+playlistName);
                }
            }
            else
            {
                io.writeToFile(playlistFileContent.toString(),"playlists/"+playlistName);
            }

        }
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
        contentLabel.setFont(robotoRegular15);
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

    @FXML
    private void playlistImportSongsFromYouTubePlaylist()
    {
        JFXDialogLayout l = new JFXDialogLayout();
        l.getStyleClass().add("dialog_style");
        Label headingLabel = new Label("Import Music from YouTube Playlist");
        headingLabel.setTextFill(WHITE_PAINT);
        headingLabel.setFont(Font.font("Roboto Regular",25));
        l.setHeading(headingLabel);

        Label l1 = new Label("Enter YouTube Playlist Link below (Must be PUBLIC)");
        l1.setFont(robotoRegular15);
        l1.setTextFill(WHITE_PAINT);
        l1.setWrapText(true);

        JFXTextField l2 = new JFXTextField();
        l2.setFont(robotoRegular15);
        l2.setStyle("-fx-inner-text: WHITE;");

        VBox bodyVBox = new VBox(l1,l2);

        l.setBody(bodyVBox);
        JFXButton confirmButton = new JFXButton("IMPORT");
        confirmButton.setTextFill(WHITE_PAINT);
        JFXButton cancelButton = new JFXButton("CANCEL");
        cancelButton.setTextFill(Color.RED);

        l.setActions(confirmButton,cancelButton);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                importSongsFromYouTubePopupStackPane.getChildren().clear();
            }
        });

        confirmButton.setOnMouseClicked(event -> {
            if(l2.getText().length()==0)
            {
                showErrorAlert("Uh Oh!","Please enter a valid YouTube Playlist URL!");
                return;
            }
            else
            {

            }
        });

        JFXDialog popupDialog = new JFXDialog(importSongsFromYouTubePopupStackPane,l, JFXDialog.DialogTransition.CENTER);
        popupDialog.setOverlayClose(false);
        popupDialog.getStyleClass().add("dialog_box");
        cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                popupDialog.close();
                popupDialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
                    @Override
                    public void handle(JFXDialogEvent event) {
                        importSongsFromYouTubePopupStackPane.toBack();
                    }
                });
            }
        });


        Platform.runLater(()->{
            importSongsFromYouTubePopupStackPane.toFront();
            popupDialog.show();
        });
    }
}
