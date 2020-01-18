package Gramophy;

import animatefx.animation.*;
import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.Mp3File;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.css.Size;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public Slider songSeek;
    @FXML
    public Slider volumeSlider;
    @FXML
    public Label totalDurLabel;
    @FXML
    public ListView<HBox> playlistListView;
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
    public Label downloadsButton;
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
    public VBox downloadsPane;
    @FXML
    public JFXTextField youtubeSearchField;
    @FXML
    public ListView<HBox> youtubeListView;
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
    public JFXButton playSongsFromCurrentPlaylistButton;
    @FXML
    public JFXButton deletePlaylistButton;
    @FXML
    public AnchorPane basePane;
    @FXML
    public JFXTextField selectMusicLibraryField;
    @FXML
    public JFXButton selectMusicLibraryFolderButton;
    @FXML
    public JFXButton applySettingsButton;
    @FXML
    public StackPane importSongsFromYouTubePopupStackPane;
    @FXML
    public JFXTextField youtubeAPIKeyField;
    @FXML
    public JFXTextField recentsPlaylistMaxLimitField;
    @FXML
    public ImageView volumeIconImageView;
    @FXML
    public JFXTextField editPlaylistNameTextField;
    @FXML
    public JFXButton editPlaylistNameButton;
    @FXML
    public JFXButton approvePlaylistNameChangeButton;
    @FXML
    public JFXButton cancelPlaylistNameChangeButton;
    @FXML
    public VBox downloadsVBox;

    JFXAutoCompletePopup<String> youtubeAutoComplete = new JFXAutoCompletePopup<>();

    private Font robotoRegular15 = new Font("Roboto Regular",15);
    private Font robotoRegular35 = new Font("Roboto Regular",35);

    final private Image shuffleIconWhite = new Image(getClass().getResourceAsStream("baseline_shuffle_white_18dp.png"));
    final private Image shuffleIconGreen = new Image(getClass().getResourceAsStream("baseline_shuffle_green_18dp.png"));

    final Image defaultAlbumArt = new Image(getClass().getResourceAsStream("icons8_music_record_120px.png"));

    final private Image redDeleteIcon = new Image(getClass().getResourceAsStream("icons8_delete_bin_24px_1.png"));
    final private Image saveToPlaylistIcon = new Image(getClass().getResourceAsStream("icons8_music_folder_24px.png"));
    final private Image downloadIcon = new Image(getClass().getResourceAsStream("icons8_download_24px.png"));

    final private Image repeatIconWhite = new Image(getClass().getResourceAsStream("baseline_repeat_white_18dp.png"));
    final private Image repeatIconGreen = new Image(getClass().getResourceAsStream("baseline_repeat_green_18dp.png"));

    final public Image muteIcon = new Image(getClass().getResourceAsStream("icons8_mute_24px.png"));
    final public Image notMuteIcon = new Image(getClass().getResourceAsStream("icons8_voice_24px.png"));


    final public Image playIcon = new Image(getClass().getResourceAsStream("baseline_play_arrow_white_18dp.png"));
    final public Image pauseIcon = new Image(getClass().getResourceAsStream("baseline_pause_white_18dp.png"));

    final Paint PAINT_GREEN = Paint.valueOf("#0e9654");
    final Paint PAINT_WHITE = Paint.valueOf("#ffffff");

    HashMap<String,ArrayList<HashMap<String,Object>>> cachedPlaylist = new HashMap<>();

    Player player = new Player();

    public boolean isShuffle = false;
    public boolean isRepeat = false;

    dashController dc;

    String currentPlaylist = "";
    int youtubePageNo = 1;
    String youtubeQueryURLStr;
    String youtubeNextPageToken;

    boolean isUnix = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dc = this;
        String osName = System.getProperty("os.name").toLowerCase();

        if(osName.contains("linux") || osName.contains("mac"))
            isUnix = true;

        loadConfig();
        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                try
                {
                    loadOtherPlaylists();
                    loadLibrary();
                    loadRecents();

                    refreshPlaylistsUI();

                    loadPlaylist("My Music");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        }).start();

        volumeSlider.valueProperty().addListener((observable, oldVal, newVal) ->
        {
            //refreshSlider(volumeSlider);
            player.setVolume(newVal.floatValue()/100);
        });

        /*basePane.widthProperty().addListener(observable -> {
            refreshSlider(songSeek);
            refreshSlider(volumeSlider);
        });*/

        new Thread(new Task<Void>() {
            @Override
            protected Void call(){

                //https://stackoverflow.com/questions/30560212/how-to-remove-the-logging-data-from-jnativehook-library
                Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
                logger.setLevel(Level.OFF);

                try
                {
                    /*Raw Key codes of media buttons
                    Linux :
                    65300 - Play/Pause
                    65303 - Next
                    65302 - Previous

                    Windows :
                    179 - Play/Pause
                    176 - Next
                    177 - Previous
                    178 - Stop
                     */
                    GlobalScreen.registerNativeHook();
                    GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
                        @Override
                        public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

                        }

                        @Override
                        public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
                            if(player.isActive)
                            {
                                int keyEventCode = nativeKeyEvent.getRawCode();
                                if (!isUnix && keyEventCode == 57380)
                                {
                                    player.stop();
                                    player.hide();
                                }
                                else if(!isUnix && keyEventCode == 179 || isUnix && keyEventCode == 65300)
                                    player.pauseResume();
                                else if(!isUnix && keyEventCode == 177 || isUnix && keyEventCode == 65302)
                                    player.playPrevious();
                                else if(!isUnix && keyEventCode == 176 || isUnix && keyEventCode == 65303)
                                    player.playNext();
                            }
                        }

                        @Override
                        public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

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

        browseButton.setOnMouseClicked(event -> {
            switchPane(1);
        });

        libraryButton.setOnMouseClicked(event -> {
            switchPane(2);
        });

        playlistButton.setOnMouseClicked(event -> {
            switchPane(3);
        });

        downloadsButton.setOnMouseClicked(event -> {
            switchPane(4);
        });

        settingsButton.setOnMouseClicked(event -> {
            switchPane(5);
        });

        switchPane(2);

        youtubeSearchButton.setOnMouseClicked(event -> {
            searchYouTube();
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

        youtubeAutoComplete.setSelectionHandler(event -> {
            youtubeSearchField.setText(event.getObject());
            searchYouTube();
        });

        youtubeSearchField.textProperty().addListener((observable, oldValue, newValue)->{
            if(newValue.replace(" ","").equals(""))
            {
                youtubeAutoComplete.hide();
                return;
            }

            if(isYouTubeSearching) return;

            Thread tx = new Thread(new Task<Void>() {
                @Override
                protected Void call() {
                    try
                    {
                        while(!isAlreadySearching)
                        {
                            if(newValue.equals(youtubeSearchField.getText()))
                            {
                                isAlreadySearching = true;
                                String[] suggestions = getYoutubeSuggestions(newValue);

                                if(!isYouTubeSearching)
                                {
                                    Platform.runLater(()->{
                                        youtubeAutoComplete.getSuggestions().setAll(suggestions);
                                        youtubeAutoComplete.show(youtubeSearchField);
                                    });
                                }
                            }
                            else
                                return null;
                        }
                        isAlreadySearching = false;
                    }
                    catch (Exception e)
                    {
                        Platform.runLater(()->youtubeAutoComplete.hide());
                        e.printStackTrace();
                    }
                    return null;
                }
            });
            tx.setPriority(1);
            tx.start();
        });

        editPlaylistNameButton.setOnMouseClicked(event -> {
            editPlaylistNameTextField.setText(currentPlaylist);
            setPlaylistNameChangerState(true);
        });
        cancelPlaylistNameChangeButton.setOnMouseClicked(event -> setPlaylistNameChangerState(false));
        approvePlaylistNameChangeButton.setOnMouseClicked(event -> {
            new Thread(new Task<Void>() {
                @Override
                protected Void call() {
                    try
                    {
                        String newPlaylistName = editPlaylistNameTextField.getText();
                        if(newPlaylistName.length() == 0)
                        {
                            showErrorAlert("Uh Oh!","Name cannot be null");
                        }
                        else
                        {
                            ArrayList<HashMap<String, Object>> songs = cachedPlaylist.get(currentPlaylist);
                            cachedPlaylist.put(newPlaylistName, songs);
                            cachedPlaylist.remove(currentPlaylist);

                            new File("playlists/"+currentPlaylist).delete();
                            currentPlaylist = newPlaylistName;
                            player.currentPlaylistName = currentPlaylist;
                            updatePlaylistsFiles();
                            refreshPlaylistsUI();
                            Platform.runLater(()->{
                                playlistNameLabel.setText(newPlaylistName);
                                setPlaylistNameChangerState(false);
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
        });
    }

    public void setPlaylistNameChangerState(boolean state)
    {
        if(state) editPlaylistNameTextField.setMaxWidth(100);
        else editPlaylistNameTextField.setMaxWidth(0);

        playlistNameLabel.setVisible(!state);
        playlistNameLabel.setManaged(!state);

        editPlaylistNameTextField.setVisible(state);
        editPlaylistNameTextField.setManaged(state);

        editPlaylistNameButton.setVisible(!state);
        editPlaylistNameButton.setManaged(!state);

        approvePlaylistNameChangeButton.setVisible(state);
        approvePlaylistNameChangeButton.setManaged(state);

        cancelPlaylistNameChangeButton.setVisible(state);
        cancelPlaylistNameChangeButton.setManaged(state);
    }

    /*public void refreshSlider(Slider s)
    {
        try
        {
            double val = s.getValue();
            //s.setStyle("-fx-background-insets: 6 0 6 0,6 "+val+" 6 0;}");
            s.setStyle("-fx-background-color: linear-gradient(to right, #0F9D58 "+val+"%, #9e9e9e "+val+"%);");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/

    boolean isAlreadySearching = false;
    boolean isYouTubeSearching = false;

    private boolean isSongPresent(String title, String artist, String playlistName)
    {
        for(HashMap<String,Object> eachSong : cachedPlaylist.get(playlistName))
            if(eachSong.get("title").toString().equals(title) && eachSong.get("artist").toString().equals(artist))
                return true;
        return false;
    }

    private boolean isSongPresent(HashMap<String,Object> songDetails, String playlistName)
    {
        for(HashMap<String,Object> eachSongInPlaylist : cachedPlaylist.get(playlistName))
        {
            if(songDetails.get("location").equals("local"))
            {
                if(songDetails.get("source").equals(eachSongInPlaylist.getOrDefault("source",""))) return true;
            }
            else
            {
                if(songDetails.get("videoID").equals(eachSongInPlaylist.getOrDefault("videoID",""))) return true;
            }
        }
        return false;
    }

    private String[] getYoutubeSuggestions(String searchTerm) throws Exception
    {
        String query = "http://suggestqueries.google.com/complete/search?client=firefox&ds=yt&q="+URLEncoder.encode(searchTerm,StandardCharsets.UTF_8);

        InputStream s = new URL(query).openStream();

        StringBuilder response = new StringBuilder();

        while(true)
        {
            int cInt = s.read();
            if(cInt == -1) break;

            char c = (char) cInt;
            response.append(c);
        }

        String responseStr = response.toString();

        JSONArray largeJSON = new JSONArray(responseStr);
        JSONArray queriesArray = largeJSON.getJSONArray(1);
        String[] tbr = new String[queriesArray.length()+1];

        if(queriesArray.length() == 0 || !queriesArray.getString(0).equals(searchTerm))
        {
            tbr[0] = searchTerm;
            for(int i = 0;i<queriesArray.length();i++)
            {
                tbr[(i+1)] = queriesArray.getString(i);
            }
        }
        else
        {
            for(int i = 0;i<queriesArray.length();i++)
            {
                tbr[i] = queriesArray.getString(i);
            }
        }


        return tbr;
    }

    @FXML
    public void deletePlaylistButtonClicked() throws Exception
    {
        if(player.isActive)
        {
            player.stop();
            player.hide();
        }

        new File("playlists/"+currentPlaylist).delete();
        cachedPlaylist.remove(currentPlaylist);
        refreshPlaylistsUI();
        loadPlaylist("My Music");
    }

    private void loadPlaylist(String playlistName)
    {
        if(!currentPlaylist.equals("YouTube"))
        {
            Platform.runLater(()->playlistListView.getItems().clear());
        }
        else
        {
            if(playlistName.equals("Recents"))
            {
                Platform.runLater(()->playlistListView.getItems().clear());
            }
            else if(!currentPlaylist.equals(playlistName))
            {
                Platform.runLater(()->playlistListView.getItems().clear());
            }
        }

        currentPlaylist = playlistName;


        ArrayList<HashMap<String,Object>> songs = cachedPlaylist.get(playlistName);

        if(playlistName.endsWith("_youtube_playlist"))
        {
            Platform.runLater(()->playlistNameLabel.setText(playlistName.replace("_youtube_playlist","")));
            playlistImportSongsFromYouTubePlaylistButton.setVisible(false);
            playlistImportSongsFromYouTubePlaylistButton.setManaged(false);
            deletePlaylistButton.setVisible(false);
            deletePlaylistButton.setManaged(false);
            editPlaylistNameButton.setVisible(false);
            editPlaylistNameButton.setManaged(false);
        }
        else
        {
            Platform.runLater(()->playlistNameLabel.setText(playlistName));
            if(currentPlaylist.equals("My Music") || currentPlaylist.equals("YouTube") || currentPlaylist.equals("Recents"))
            {
                playlistImportSongsFromYouTubePlaylistButton.setVisible(false);
                playlistImportSongsFromYouTubePlaylistButton.setManaged(false);
                deletePlaylistButton.setVisible(false);
                deletePlaylistButton.setManaged(false);
                editPlaylistNameButton.setVisible(false);
                editPlaylistNameButton.setManaged(false);
            }
            else
            {
                playlistImportSongsFromYouTubePlaylistButton.setVisible(true);
                playlistImportSongsFromYouTubePlaylistButton.setManaged(true);
                deletePlaylistButton.setVisible(true);
                deletePlaylistButton.setManaged(true);
                editPlaylistNameButton.setVisible(true);
                editPlaylistNameButton.setManaged(true);
            }
        }


        if(cachedPlaylist.get(playlistName).size() == 0)
        {
            playSongsFromCurrentPlaylistButton.setDisable(true);

            Label sl = new Label("It's lonely here!");
            sl.setFont(robotoRegular15);
            HBox sx = new HBox(sl);
            sx.setPrefHeight(300);
            sx.setAlignment(Pos.CENTER);
            Platform.runLater(()->playlistListView.getItems().add(sx));
        }
        else
        {
            playSongsFromCurrentPlaylistButton.setDisable(false);
        }

        i2 = 0;
        for(HashMap<String,Object> eachSong : songs)
        {
            Label title = new Label();
            title.setFont(robotoRegular15);
            title.setPrefWidth(500);

            Label artist = new Label();
            artist.setFont(robotoRegular15);
            artist.setPrefWidth(150);

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






            Platform.runLater(()->{

                JFXButton downloadButton = new JFXButton();
                downloadButton.setCache(true);
                downloadButton.setCacheHint(CacheHint.SPEED);
                downloadButton.setGraphic(new ImageView(downloadIcon));
                downloadButton.setTextFill(PAINT_GREEN);
                downloadButton.setId(i2+"");
                downloadButton.setOnMouseClicked(event -> {
                    new Thread(new Task<Void>() {
                        @Override
                        protected Void call(){

                            JFXButton b = (JFXButton) event.getSource();
                            HashMap<String,Object> c = cachedPlaylist.get(currentPlaylist).get(Integer.parseInt(((Node)event.getSource()).getId()));
                            try
                            {
                                addToDownloads(c.get("videoID").toString(),b,c.get("thumbnail").toString(),c.get("title").toString(),c.get("channelTitle").toString());
                            }
                            catch (Exception e)
                            {
                                showErrorAlert("Error!","Unable to download! Check Stacktrace!");
                            }
                            return null;
                        }
                    }).start();
                });

                JFXButton saveToPlaylistButton = new JFXButton();
                saveToPlaylistButton.setCache(true);
                saveToPlaylistButton.setCacheHint(CacheHint.SPEED);
                saveToPlaylistButton.setGraphic(new ImageView(saveToPlaylistIcon));
                saveToPlaylistButton.setTextFill(PAINT_GREEN);
                saveToPlaylistButton.setId(i2+"");
                saveToPlaylistButton.setOnMouseClicked(event -> {
                    new Thread(new Task<Void>() {
                        @Override
                        protected Void call(){
                            try
                            {
                                customisePlaylist(cachedPlaylist.get(playlistName).get(Integer.parseInt(((Node)event.getSource()).getId())));
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }).start();
                });

                JFXButton deleteButton = new JFXButton();
                deleteButton.setCache(true);
                deleteButton.setCacheHint(CacheHint.SPEED);
                deleteButton.setGraphic(new ImageView(redDeleteIcon));
                deleteButton.setRipplerFill(Paint.valueOf("#F75B5B"));
                deleteButton.setTextFill(Color.RED);
                deleteButton.setId(i2+"");
                deleteButton.setOnMouseClicked(event -> {
                    new Thread(new Task<Void>() {
                        @Override
                        protected Void call()
                        {
                            try
                            {
                                int index = Integer.parseInt(((Node) event.getSource()).getId());
                                if(player.songIndex == index && player.isActive)
                                {
                                    player.hide();
                                    player.stop();
                                }
                                if(currentPlaylist.equals("My Music"))
                                {
                                    File f= new File(new URI(cachedPlaylist.get(currentPlaylist).get(index).get("source").toString()));
                                    if(f.exists())
                                    {
                                        boolean x = f.delete();
                                    }
                                    else
                                    {
                                    }
                                }
                                cachedPlaylist.get(currentPlaylist).remove(index);
                                updatePlaylistsFiles();
                                loadPlaylist(currentPlaylist);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }).start();
                });

                HBox vb1 = new HBox(saveToPlaylistButton);

                if(eachSong.get("location").toString().equals("youtube"))
                {
                    vb1.getChildren().add(downloadButton);
                    if(isSongPresent(eachSong.get("title").toString(),eachSong.get("channelTitle").toString(),"My Music"))
                        downloadButton.setDisable(true);
                }

                vb1.getChildren().add(deleteButton);


                HBox eachMusicHBox = new HBox(title, artist);
                eachMusicHBox.setAlignment(Pos.CENTER_LEFT);
                eachMusicHBox.setSpacing(10);
                eachMusicHBox.setCache(true);
                eachMusicHBox.setCacheHint(CacheHint.SPEED);
                eachMusicHBox.setId(i2+"");
                HBox.setHgrow(eachMusicHBox,Priority.ALWAYS);

                eachMusicHBox.setOnMouseClicked(event -> {

                    if(player.isActive)
                    {
                        player.stop();
                    }

                    addToRecents(cachedPlaylist.get(playlistName).get(Integer.parseInt(((Node)event.getSource()).getId())));
                    player = new Player(playlistName,Integer.parseInt(((Node)event.getSource()).getId()), dc,isUnix);


                });

                HBox mainHBox = new HBox(eachMusicHBox,vb1);
                mainHBox.setId(eachSong.get("title").toString() + eachSong.getOrDefault("artist","") + eachSong.getOrDefault("channelTitle",""));

                playlistListView.getItems().add(mainHBox);
                i2++;
            });

        }
    }

    ArrayList<JFXProgressBar> yy = new ArrayList<>();
    ArrayList<Label> yyx = new ArrayList<>();

    private void addToDownloads(String watchID, JFXButton originalButton, String thumbnailURL, String title, String channelTitle)
    {

        String refinedTitle = title.replace("|","&").replace("\\","&").replace("/","&");
        int thisIndex = yy.size();
        try
        {
            Platform.runLater(()->{
                originalButton.setDisable(true);
                ImageView downloadThumbnailImageView = new ImageView(thumbnailURL);

                Label titleLabel = new Label(title);
                titleLabel.setFont(new Font("Roboto Regular",25));
                titleLabel.setPadding(new Insets(0,0,10,0));
                Label channelTitleLabel = new Label(channelTitle);
                channelTitleLabel.setFont(robotoRegular15);
                Label statusLabel = new Label("Downloading ...");
                JFXProgressBar progressBar = new JFXProgressBar(-1);
                VBox next = new VBox(titleLabel,channelTitleLabel,statusLabel,progressBar);
                next.setSpacing(5);

                HBox eachDownloadNode = new HBox(downloadThumbnailImageView,next);
                eachDownloadNode.setSpacing(10);
                eachDownloadNode.setPadding(new Insets(25));
                eachDownloadNode.setId(watchID);
                eachDownloadNode.getStyleClass().add("card");

                downloadsVBox.getChildren().add(eachDownloadNode);
            });

            Thread.sleep(500);

            for(Node eachNode : downloadsVBox.getChildren())
            {
                if(eachNode.getId().equals(watchID))
                {
                    HBox c = (HBox) eachNode;
                    VBox v = (VBox) c.getChildren().get(1);
                    yyx.add((Label) v.getChildren().get(2));
                    yy.add((JFXProgressBar) v.getChildren().get(3));
                }
            }

            String udlExec = "youtube-dl.exe";
            if(isUnix) udlExec = "./youtube-dl";

            String cmd = udlExec+" -o audioDownload.%(ext)s --extract-audio --audio-format mp3 https://www.youtube.com/watch?v="+watchID;

            System.out.println(cmd);

            Process p = Runtime.getRuntime().exec(cmd);

            BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while(true)
            {
                String newLine = bf.readLine();
                System.out.println("new"+newLine);
                if(newLine == null) break;
                //[download] 100.0%

                if(newLine.startsWith("[download]"))
                {
                    String progressStr = newLine.substring(10,16).replace(" ","");
                    if(!progressStr.contains("Desti") && !progressStr.contains("%") && !progressStr.contains("Resum"))
                    {
                        double d = Double.parseDouble(progressStr);
                        Platform.runLater(()->{
                            yy.get(thisIndex).setProgress(d/100);
                        });
                    }
                }
                else if(newLine.startsWith("[ffmpeg]"))
                {
                    Platform.runLater(()->{
                        yyx.get(thisIndex).setText("Converting ...");
                    });
                }
            }

            Platform.runLater(()->{
                yyx.get(thisIndex).setText("Finishing Up ...");
            });

            ID3v2 id3v2Tag = new ID3v24Tag();
            Mp3File mp3File = new Mp3File(new File("audioDownload.mp3"));
            mp3File.setId3v2Tag(id3v2Tag);

            Image img = new Image(thumbnailURL);
            BufferedImage ix = SwingFXUtils.fromFXImage(img,null);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(ix, "png", bos );

            id3v2Tag.setTitle(title);
            id3v2Tag.setArtist(channelTitle);
            id3v2Tag.setAlbumArtist(channelTitle);
            id3v2Tag.setComment("Downloaded via github.com/dubbadhar/gramophy. Made with Love by Debayan. Youtube Watch ID : "+watchID);
            id3v2Tag.setAlbumImage(bos.toByteArray(),"LOL");

            mp3File.save(config.get("music_lib_path")+"/"+refinedTitle+".mp3");

            loadLibrary();

            Platform.runLater(()->{
                yy.get(thisIndex).setProgress(1);
                yyx.get(thisIndex).setText("Downloaded!");
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Platform.runLater(()->{
                yyx.get(thisIndex).setText("Unable to download! Check Stacktrace!");
                yy.get(thisIndex).setProgress(1);
            });
        }
        finally {
            new File(refinedTitle+"_pass_.mp3").delete();
            new File(refinedTitle+"_pass_.webm").delete();
        }
    }



    private void refreshPlaylistsUI()
    {
        Platform.runLater(()->playlistsMasonryPane.getChildren().clear());

        for(String eachPlaylistName : cachedPlaylist.keySet())
        {
            if(!eachPlaylistName.equals("YouTube") && !eachPlaylistName.endsWith("_youtube_playlist"))
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
                eachPlaylistVBox.setOnMouseClicked(event -> {
                    new Thread(new Task<Void>() {
                        @Override
                        protected Void call(){
                            try
                            {
                                String playlistName = ((Node) event.getSource()).getId();
                                loadPlaylist(playlistName);
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
                eachPlaylistVBox.setId(eachPlaylistName);
                Platform.runLater(()->playlistsMasonryPane.getChildren().add(eachPlaylistVBox));
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
        r.setCache(true);
        r.setCacheHint(CacheHint.SPEED);
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
                isYouTubeSearching = true;
                Platform.runLater(()->{
                    youtubeSearchButton.setDisable(true);
                    youtubeSearchField.setDisable(true);
                    youtubeLoadingSpinner.setVisible(true);
                    youtubeAutoComplete.hide();
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
                        youtubeQueryURLStr = "https://www.googleapis.com/youtube/v3/search?part=snippet&q="+youtubeSearchQuery+"&maxResults=10&key="+config.get("youtube_api_key");
                    else
                        youtubeQueryURLStr = "https://www.googleapis.com/youtube/v3/search?part=snippet&q="+youtubeSearchQuery+"&maxResults=10&pageToken="+youtubeNextPageToken+"&key="+config.get("youtube_api_key");


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

                    if(youtubeResponse.has("nextPageToken"))
                        youtubeNextPageToken = youtubeResponse.getString("nextPageToken");
                    else
                        youtubeNextPageToken = "";

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
                                if(player.isActive)
                                {
                                    try
                                    {
                                        player.stop();
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }

                                addToRecents(cachedPlaylist.get("YouTube").get(Integer.parseInt(((Node)event.getSource()).getId())));
                                player = new Player("YouTube", Integer.parseInt(((Node)event.getSource()).getId()), dc, isUnix);

                            });

                            JFXButton saveToPlaylistButton = new JFXButton("");
                            saveToPlaylistButton.setGraphic(new ImageView(saveToPlaylistIcon));
                            saveToPlaylistButton.setId(x+"");
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

                            JFXButton downloadButton = new JFXButton();
                            downloadButton.setCache(true);
                            downloadButton.setCacheHint(CacheHint.SPEED);
                            downloadButton.setGraphic(new ImageView(downloadIcon));
                            downloadButton.setTextFill(PAINT_GREEN);
                            downloadButton.setId(x+"");
                            downloadButton.setOnMouseClicked(event -> {
                                new Thread(new Task<Void>() {
                                    @Override
                                    protected Void call(){
                                        JFXButton b = (JFXButton) event.getSource();
                                        try
                                        {
                                            String watchID = cachedPlaylist.get("YouTube").get(Integer.parseInt(((Node)event.getSource()).getId())).get("videoID").toString();

                                            addToDownloads(watchID,b,defaultThumbnailURL,title,channelTitle);
                                        }
                                        catch (Exception e)
                                        {
                                            e.printStackTrace();

                                            showErrorAlert("Error!","Unable to download! Check Stacktrace!");
                                            Platform.runLater(()->b.setDisable(false));
                                        }
                                        return null;
                                    }
                                }).start();
                            });

                            VBox vv = new VBox(downloadButton, saveToPlaylistButton);

                            if(isSongPresent(title,channelTitle,"My Music"))
                                downloadButton.setDisable(true);

                            HBox mainHBox = new HBox(videoHBox,vv);
                            mainHBox.setAlignment(Pos.CENTER_LEFT);


                            Platform.runLater(()-> youtubeListView.getItems().add(mainHBox));

                            x++;
                        }
                        else if(kindObj.equals("youtube#playlist"))
                        {
                            JSONObject snippet = eachItem.getJSONObject("snippet");
                            if(snippet.has("thumbnails"))
                            {
                                String title = snippet.getString("title");
                                String channelTitle = snippet.getString("channelTitle");

                                String playlistID = idObj.getString("playlistId");

                                /*
                                For some reason the YouTube API (tested 16/01/2020)
                                doesn't return thumnails of unavailable videos, so its better to omit them...
                                 */
                                JSONObject thumbnail = snippet.getJSONObject("thumbnails");
                                String defaultThumbnailURL = thumbnail.getJSONObject("default").getString("url");

                                Image ix = new Image(defaultThumbnailURL);
                                ImageView thumbnailImgView = new ImageView(ix);
                                Label titleLabel = new Label("Playlist : "+title);
                                titleLabel.setFont(robotoRegular15);
                                Label channelTitleLabel = new Label(channelTitle);
                                channelTitleLabel.setFont(robotoRegular15);
                                VBox vbox = new VBox(titleLabel,channelTitleLabel);
                                vbox.setId(title);
                                vbox.setSpacing(5);
                                HBox playlistHBox = new HBox(thumbnailImgView,vbox);
                                playlistHBox.setSpacing(10);
                                playlistHBox.setId(playlistID);
                                HBox.setHgrow(playlistHBox, Priority.ALWAYS);

                                playlistHBox.setOnMouseClicked(event -> {
                                    new Thread(new Task<Void>() {
                                        @Override
                                        protected Void call()
                                        {
                                            try
                                            {
                                                HBox h = (HBox) event.getSource();
                                                Node n1 = h.getChildren().get(1);
                                                String thisPlaylistID = h.getId();
                                                String playlistName = n1.getId();

                                                ArrayList<HashMap<String,Object>> songs = getSongsInYouTubePublicPlaylist(thisPlaylistID);

                                                cachedPlaylist.put(playlistName+"_youtube_playlist",songs);
                                                loadPlaylist(playlistName+"_youtube_playlist");

                                                if(player.isActive)
                                                {
                                                    player.stop();
                                                }

                                                player = new Player(playlistName+"_youtube_playlist",0,dc,isUnix);
                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                            }
                                            return null;
                                        }
                                    }).start();
                                });

                                JFXButton saveToPlaylistButton = new JFXButton("");
                                saveToPlaylistButton.setGraphic(new ImageView(saveToPlaylistIcon));
                                saveToPlaylistButton.setTextFill(PAINT_GREEN);
                                saveToPlaylistButton.setId(playlistID);
                                saveToPlaylistButton.setOnMouseClicked(event -> {
                                    new Thread(new Task<Void>() {
                                        @Override
                                        protected Void call() {
                                            try
                                            {
                                                Node n = (Node) event.getSource();
                                                String playlistID = n.getId();
                                                saveYouTubePlaylistSongsToLocalPlaylist(playlistID);
                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                            }
                                            return null;
                                        }
                                    }).start();
                                });


                                VBox vv = new VBox(saveToPlaylistButton);
                                HBox mainHBox = new HBox(playlistHBox,vv);
                                mainHBox.setAlignment(Pos.CENTER_LEFT);

                                Platform.runLater(()-> youtubeListView.getItems().add(mainHBox));
                            }
                        }
                    }

                    cachedPlaylist.put("YouTube",songs);
                    loadPlaylist("YouTube");


                    if(youtubePageNo>1)
                    {
                        if(player.currentPlaylistName.equals("YouTube"))
                            loadPlaylist("YouTube");
                    }

                    if(youtubeListView.getItems().size()>0 && !youtubeNextPageToken.equals(""))
                    {
                        JFXButton loadMoreButton = new JFXButton("Load More");
                        loadMoreButton.setCache(true);
                        loadMoreButton.setCacheHint(CacheHint.SPEED);
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
                    if(e.getLocalizedMessage().contains("403 for URL"))
                    {
                        showErrorAlert("Error 403","It seems Gramophy was unable to connect to YouTube with your given API Key.\nGo to the following link for more info : \n"+e.getLocalizedMessage().replace("Server returned HTTP response code: 403 for URL: ",""));
                    }
                    else
                    {
                        showErrorAlert("Uh Oh!","Unable to connect to YouTube. Make sure you're connected to the internet. For more info, check stacktrace");
                    }
                    e.printStackTrace();
                }
                finally {
                    Platform.runLater(()->{
                        youtubeListView.setDisable(false);
                        youtubeSearchButton.setDisable(false);
                        youtubeSearchField.setDisable(false);
                        youtubeLoadingSpinner.setVisible(false);
                        youtubeAutoComplete.hide();
                    });
                }

                isYouTubeSearching = false;
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
                downloadsButton.setTextFill(PAINT_WHITE);
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
                downloadsButton.setTextFill(PAINT_WHITE);
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
                downloadsButton.setTextFill(PAINT_WHITE);
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
                settingsButton.setTextFill(PAINT_WHITE);
                downloadsButton.setTextFill(PAINT_GREEN);
                downloadsPane.toFront();
                FadeInUp fiu = new FadeInUp(downloadsPane);
                fiu.setSpeed(2.0);
                fiu.play();
            }
            else if(paneNo == 5)
            {
                browseButton.setTextFill(PAINT_WHITE);
                libraryButton.setTextFill(PAINT_WHITE);
                playlistButton.setTextFill(PAINT_WHITE);
                settingsButton.setTextFill(PAINT_GREEN);
                downloadsButton.setTextFill(PAINT_WHITE);
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
        System.out.println(System.getProperty("user.dir"));
        String[] configArr = io.readFileArranged("config","::");

        String musicLibDir = configArr[0];
        String youtubeAPIKey = configArr[1];
        maxRecentsLimit = Integer.parseInt(configArr[2]);

        recentsPlaylistMaxLimitField.setText(configArr[2]);

        if(musicLibDir.equals("NULL"))
            musicLibDir = System.getProperty("user.home")+"/Music/";

        config.put("music_lib_path",musicLibDir);
        config.put("youtube_api_key",youtubeAPIKey);
        config.put("max_recents_limit",configArr[2]);
        selectMusicLibraryField.setText(musicLibDir);
        youtubeAPIKeyField.setText(youtubeAPIKey);
    }


    int i2 = 0;

    private void loadLibrary() throws Exception
    {
        io.log("Loading music library ...");

        File[] songsFiles = io.getFilesInFolder(config.get("music_lib_path"));

        ArrayList<HashMap<String,Object>> thisPlaylist = new ArrayList<>();

        for(File eachSong : songsFiles)
        {
            if(!eachSong.getName().endsWith(".mp3")) continue;

            try
            {
                Mp3File mp3File = new Mp3File(eachSong);

                ID3v1 id3v1 = mp3File.getId3v1Tag();
                HashMap<String, Object> songDetails = new HashMap<>();
                songDetails.put("location","local");
                songDetails.put("source",eachSong.toURI().toString());

                if(id3v1 == null)
                {
                    ID3v2 id3v2 = mp3File.getId3v2Tag();

                    if(id3v2.getTitle() == null)
                    {
                        songDetails.put("title", eachSong.getName());
                    }
                    else
                    {
                        songDetails.put("title", id3v2.getTitle());
                    }

                    if(id3v2.getArtist() == null)
                    {
                        songDetails.put("artist", "Unknown");
                    }
                    else
                    {
                        songDetails.put("artist", id3v2.getArtist());
                    }

                    if(id3v2.getAlbumImage() != null)
                    {
                        songDetails.put("album_art", id3v2.getAlbumImage());
                    }
                }
                else
                {
                    if(id3v1.getTitle() == null)
                    {
                        songDetails.put("title", eachSong.getName());
                    }
                    else
                    {
                        songDetails.put("title", id3v1.getTitle());
                    }

                    if(id3v1.getArtist() == null)
                    {
                        songDetails.put("artist", "Unknown");
                    }
                    else
                    {
                        songDetails.put("artist", id3v1.getArtist());
                    }
                }
                thisPlaylist.add(songDetails);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        cachedPlaylist.put("My Music",thisPlaylist);

    }

    private void loadRecents() throws Exception
    {
        System.out.println("xzcas");
        String[] recentsArr = io.readFileArranged("playlists/Recents","::");

        ArrayList<HashMap<String,Object>> newSongs = new ArrayList<>();

        for(String eachChunk : recentsArr)
        {
            if(eachChunk.equals("empty")) break;
            String[] eachSongArr = eachChunk.split("<>");

            HashMap<String,Object> eachSongDetails = new HashMap<>();

            eachSongDetails.put("location",eachSongArr[0]);
            eachSongDetails.put("source",eachSongArr[1]);

            if(eachSongArr[0].equals("local"))
            {
                System.out.println(URLDecoder.decode(eachSongArr[1],StandardCharsets.UTF_8).replace("file:/",""));
                File f = new File(URLDecoder.decode(eachSongArr[1],StandardCharsets.UTF_8).replace("file:/",""));
                if(f.exists())
                {
                    Mp3File mp3File = new Mp3File(f.getPath());

                    ID3v1 id3v1 = mp3File.getId3v1Tag();

                    if(id3v1 == null)
                    {
                        ID3v2 id3v2 = mp3File.getId3v2Tag();
                        eachSongDetails.put("title", id3v2.getTitle());
                        eachSongDetails.put("artist", id3v2.getArtist());
                        eachSongDetails.put("album_art", id3v2.getAlbumImage());
                    }
                    else
                    {
                        eachSongDetails.put("title", id3v1.getTitle());
                        eachSongDetails.put("artist", id3v1.getArtist());
                    }
                }
                else
                {
                    System.out.println("zxca2");
                    continue;
                }

                System.out.println(eachSongDetails.get("title"));
            }
            else
            {
                eachSongDetails.put("videoID",eachSongArr[1]);
                eachSongDetails.put("thumbnail",eachSongArr[2]);
                eachSongDetails.put("title",eachSongArr[3]);
                eachSongDetails.put("channelTitle",eachSongArr[4]);
            }

            newSongs.add(eachSongDetails);
        }

        cachedPlaylist.put("Recents",newSongs);
    }

    int maxRecentsLimit;

    public void addToRecents(HashMap<String,Object> song)
    {
        Thread xx = new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                boolean found = false;
                for(HashMap<String,Object> eachSongs : cachedPlaylist.get("Recents"))
                {

                    System.out.println("eachSongs : "+eachSongs.get("location"));
                    System.out.println("song : "+song.get("location"));

                    if(eachSongs.get("location").toString().equals(song.get("location").toString()))
                    {
                        if(eachSongs.get("location").toString().equals("youtube"))
                        {
                            System.out.println("eachSongs : "+eachSongs.get("videoID"));
                            System.out.println("song : "+song.get("videoID"));

                            if(eachSongs.get("videoID").toString().equals(song.get("videoID").toString()))
                            {
                                found = true;
                                break;
                            }
                        }
                        else if(eachSongs.get("location").toString().equals("local"))
                        {
                            System.out.println("eachSongs : "+eachSongs.get("source"));
                            System.out.println("song : "+song.get("source"));

                            if(eachSongs.get("source").toString().equals(song.get("source").toString()))
                            {
                                found = true;
                                break;
                            }
                        }
                    }
                }

                System.out.println(found);

                if(!found)
                {
                    if(cachedPlaylist.get("Recents").size() > maxRecentsLimit)
                    {
                        for(int i = 0; i<cachedPlaylist.get("Recents").size();i++)
                        {
                            if(cachedPlaylist.get("Recents").size() >= maxRecentsLimit)
                            {
                                cachedPlaylist.get("Recents").remove(i);
                            }
                        }
                    }

                    cachedPlaylist.get("Recents").add(song);
                    updatePlaylistsFiles();
                    try
                    {
                        System.out.println("Axcased");
                        loadRecents();
                        if(currentPlaylist.equals("Recents"))
                        {
                            loadPlaylist("Recents");
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        });
        xx.setPriority(1);
        xx.start();
    }

    private void loadOtherPlaylists() throws Exception
    {
        File[] playlistFiles = io.getFilesInFolder("playlists/");
        for(File eachPlaylistFile : playlistFiles)
        {
            if(eachPlaylistFile.getName().equals("Recents")) continue;

            String contentRaw = io.readFileRaw("playlists/"+eachPlaylistFile.getName());
            String[] contentArr = contentRaw.split("::");

            String playlistName = eachPlaylistFile.getName();
            ArrayList<HashMap<String,Object>> songs = new ArrayList<>();

            if(!contentRaw.equals("empty"))
            {
                for(int i =0;i<contentArr.length;i++)
                {
                    String[] eachSongContentArr = contentArr[i].split("<>");
                    HashMap<String,Object> sd = new HashMap<>();
                    sd.put("location",eachSongContentArr[0]);
                    sd.put("source",eachSongContentArr[1]);

                    if(eachSongContentArr[0].equals("local"))
                    {
                        File f = new File(URLDecoder.decode(eachSongContentArr[1],StandardCharsets.UTF_8).replace("file:/",""));
                        if(f.exists())
                        {
                            Mp3File mp3File = new Mp3File(URLDecoder.decode(eachSongContentArr[1],StandardCharsets.UTF_8).replace("file:/",""));

                            ID3v1 id3v1 = mp3File.getId3v1Tag();

                            if(id3v1 == null)
                            {
                                ID3v2 id3v2 = mp3File.getId3v2Tag();
                                sd.put("title", id3v2.getTitle());
                                sd.put("artist", id3v2.getArtist());
                                sd.put("album_art", id3v2.getAlbumImage());
                            }
                            else
                            {
                                sd.put("title", id3v1.getTitle());
                                sd.put("artist", id3v1.getArtist());
                            }
                        }
                        else
                        {
                            System.out.println("skipping "+eachSongContentArr[1]+" cuz not found ...");
                        }

                    }
                    else if(eachSongContentArr[0].equals("youtube"))
                    {
                        sd.put("videoID",eachSongContentArr[1]);
                        sd.put("thumbnail",eachSongContentArr[2]);
                        sd.put("title",eachSongContentArr[3]);
                        sd.put("channelTitle",eachSongContentArr[4]);

                    }

                    songs.add(sd);
                }
            }


            cachedPlaylist.put(playlistName,songs);
        }
    }

    @FXML
    public void playSongsFromCurrentPlaylist()
    {
        if(cachedPlaylist.get(currentPlaylist).size() > 0)
        {
            if(player.isActive)
            {
                player.stop();
            }

            addToRecents(cachedPlaylist.get(currentPlaylist).get(0));
            player = new Player(currentPlaylist,0, dc, isUnix);
        }
    }

    public void saveYouTubePlaylistSongsToLocalPlaylist(String playlistID)
    {
        VBox vb = new VBox();
        vb.setSpacing(10);

        int s = cachedPlaylist.size() - 2;
        if(cachedPlaylist.containsKey("YouTube")) s--;

        for(String eachPlaylistName : cachedPlaylist.keySet())
        {
            if(eachPlaylistName.endsWith("_youtube_playlist")) s--;
        }

        if(s==0)
        {
            showErrorAlert("No Local Playlists found","Create one to save to!");
        }
        else
        {
            JFXDialogLayout l = new JFXDialogLayout();
            l.getStyleClass().add("dialog_style");
            Label headingLabel = new Label("Select a Playlist to save to");
            headingLabel.setTextFill(WHITE_PAINT);
            headingLabel.setFont(Font.font("Roboto Regular",25));
            l.setHeading(headingLabel);


            JFXDialog popupDialog = new JFXDialog(importSongsFromYouTubePopupStackPane,l, JFXDialog.DialogTransition.CENTER);
            popupDialog.setOverlayClose(false);
            popupDialog.getStyleClass().add("dialog_box");

            for(String playlistName : cachedPlaylist.keySet())
            {
                if(playlistName.endsWith("_youtube_playlist") || playlistName.equals("YouTube") || playlistName.equals("Recents") || playlistName.equals("My Music")) continue;

                JFXButton playlistToSaveToButton = new JFXButton(playlistName);

                playlistToSaveToButton.setTextFill(WHITE_PAINT);
                playlistToSaveToButton.setId(playlistID);
                Platform.runLater(()->vb.getChildren().add(playlistToSaveToButton));


                playlistToSaveToButton.setOnMouseClicked(event -> {
                    new Thread(new Task<Void>() {
                        @Override
                        protected Void call()
                        {
                            try
                            {
                                JFXButton b = (JFXButton) event.getSource();
                                String playlistID = b.getId();
                                String playlistToSaveTo = b.getText();

                                ArrayList<HashMap<String,Object>> songs = getSongsInYouTubePublicPlaylist(playlistID);

                                int songsImported = 0;
                                for(HashMap<String,Object> eachSong : songs)
                                {
                                    if(!isSongPresent(eachSong,playlistToSaveTo))
                                    {
                                        cachedPlaylist.get(playlistToSaveTo).add(eachSong);
                                        songsImported++;
                                    }
                                    else
                                        System.out.println("Song already present. Skipping ...");
                                }

                                updatePlaylistsFiles();
                                refreshPlaylistsUI();

                                Platform.runLater(()->{
                                    popupDialog.close();
                                    importSongsFromYouTubePopupStackPane.toBack();
                                });

                                showErrorAlert("Done!","Successfully imported "+songsImported+" songs to "+playlistName);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }).start();
                });
            }

            l.setBody(vb);

            JFXButton cancelButton = new JFXButton("CANCEL");
            cancelButton.setTextFill(Color.RED);

            l.setActions(cancelButton);

            Platform.runLater(() -> importSongsFromYouTubePopupStackPane.getChildren().clear());


            cancelButton.setOnMouseClicked(event->{
                popupDialog.close();
                popupDialog.setOnDialogClosed(event1 -> importSongsFromYouTubePopupStackPane.toBack());
            });

            Platform.runLater(()->{
                importSongsFromYouTubePopupStackPane.toFront();
                popupDialog.show();
            });
        }
    }


    public void customisePlaylist(HashMap<String,Object> newSong)
    {
        VBox vb = new VBox();
        vb.setSpacing(10);

        int s = cachedPlaylist.size() - 2;
        if(cachedPlaylist.containsKey("YouTube")) s--;

        for(String eachPlaylistName : cachedPlaylist.keySet())
        {
            if(eachPlaylistName.endsWith("_youtube_playlist")) s--;
        }

        if(s==0)
        {
            showErrorAlert("No Local Playlists found","Create one to save to!");
        }
        else
        {
            for(String playlistName : cachedPlaylist.keySet())
            {
                if(playlistName.endsWith("_youtube_playlist") || playlistName.equals("YouTube") || playlistName.equals("Recents") || playlistName.equals("My Music")) continue;

                JFXRadioButton rb = new JFXRadioButton(playlistName);

                boolean found = false;
                for(HashMap<String,Object> sd : cachedPlaylist.get(playlistName))
                {
                    if(sd.get("location").toString().equals(newSong.get("location").toString()))
                    {
                        if(sd.get("location").toString().equals("youtube"))
                        {
                            if(sd.get("videoID").toString().equals(newSong.get("videoID").toString()))
                            {
                                found = true;
                                break;
                            }
                        }
                        else if(sd.get("location").toString().equals("local"))
                        {
                            if(sd.get("title").toString().equals(newSong.get("title").toString()))
                            {
                                found = true;
                                break;
                            }
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

            Platform.runLater(() -> importSongsFromYouTubePopupStackPane.getChildren().clear());

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
                                            if(sd.get("videoID").toString().equals(newSong.get("videoID").toString()))
                                            {
                                                found = true;
                                                foundSong = sd;
                                                break;
                                            }
                                        }
                                        else if(sd.get("location").toString().equals("local"))
                                        {
                                            if(sd.get("title").toString().equals(newSong.get("title").toString()))
                                            {
                                                found = true;
                                                foundSong = sd;
                                                break;
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
                            loadPlaylist(currentPlaylist);

                            popupDialog.close();
                            popupDialog.setOnDialogClosed(event12 -> importSongsFromYouTubePopupStackPane.toBack());
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }).start();
            });

            cancelButton.setOnMouseClicked(event -> {
                popupDialog.close();
                popupDialog.setOnDialogClosed(event1 -> importSongsFromYouTubePopupStackPane.toBack());
            });

            Platform.runLater(()->{
                importSongsFromYouTubePopupStackPane.toFront();
                popupDialog.show();
            });
        }
    }

    public void updatePlaylistsFiles()
    {
        for(String playlistName : cachedPlaylist.keySet())
        {
            if(playlistName.equals("My Music") || playlistName.equals("YouTube")) continue;
            if(playlistName.endsWith("_youtube_playlist")) continue;

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

        Platform.runLater(()->alertStackPane.getChildren().clear());

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

        Label l1 = new Label("Enter YouTube Playlist ID below (Must be PUBLIC)");
        l1.setFont(robotoRegular15);
        l1.setTextFill(WHITE_PAINT);
        l1.setWrapText(true);

        JFXTextField l2 = new JFXTextField();
        l2.setFont(robotoRegular15);
        l2.setStyle("-fx-inner-text: WHITE;");

        VBox bodyVBox = new VBox(l1,l2);

        l.setBody(bodyVBox);

        JFXSpinner sp = new JFXSpinner();
        sp.setProgress(-1);
        sp.setCache(true);
        sp.setCacheHint(CacheHint.SPEED);
        sp.setPrefSize(24,24);
        sp.setVisible(false);
        JFXButton confirmButton = new JFXButton("IMPORT");
        confirmButton.setTextFill(WHITE_PAINT);
        JFXButton cancelButton = new JFXButton("CANCEL");
        cancelButton.setTextFill(Color.RED);

        l.setActions(sp,confirmButton,cancelButton);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                importSongsFromYouTubePopupStackPane.getChildren().clear();
            }
        });

        JFXDialog popupDialog = new JFXDialog(importSongsFromYouTubePopupStackPane,l, JFXDialog.DialogTransition.CENTER);
        popupDialog.setOverlayClose(false);
        popupDialog.getStyleClass().add("dialog_box");
        cancelButton.setOnMouseClicked(event -> {
            popupDialog.close();
            popupDialog.setOnDialogClosed(event1 -> importSongsFromYouTubePopupStackPane.toBack());
        });

        confirmButton.setOnMouseClicked(event -> {
            if(l2.getText().length()==0)
            {
                showErrorAlert("Uh Oh!","Please enter a valid YouTube Playlist ID!");
            }
            else
            {
                new Thread(new Task<Void>() {
                    @Override
                    protected Void call() {
                        try
                        {
                            Platform.runLater(()->{
                                l2.setDisable(true);
                                confirmButton.setDisable(true);
                                cancelButton.setDisable(true);

                                sp.setVisible(true);
                            });

                            ArrayList<HashMap<String,Object>> playlistSongs = getSongsInYouTubePublicPlaylist(l2.getText());

                            for(HashMap<String,Object> eachSongDetails : playlistSongs)
                                if(!isSongPresent(eachSongDetails, currentPlaylist)) cachedPlaylist.get(currentPlaylist).add(eachSongDetails);

                            loadPlaylist(currentPlaylist);
                            updatePlaylistsFiles();
                        }
                        catch (Exception e)
                        {
                            showErrorAlert("Error!","Unable to import!\nCheck Stacktrace");
                            e.printStackTrace();
                        }
                        finally
                        {
                            Platform.runLater(()->{
                                sp.setVisible(false);
                                popupDialog.close();
                                popupDialog.setOnDialogClosed(event12 -> importSongsFromYouTubePopupStackPane.toBack());
                            });
                        }
                        return null;
                    }
                }).start();
            }
        });


        Platform.runLater(()->{
            importSongsFromYouTubePopupStackPane.toFront();
            popupDialog.show();
        });
    }

    public ArrayList<HashMap<String,Object>> getSongsInYouTubePublicPlaylist(String playlistID) throws Exception
    {
        ArrayList<HashMap<String,Object>> toReturn = new ArrayList<>();

        String youtubeURL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId="+playlistID+"&key="+config.get("youtube_api_key");
        String nextPageToken = "";
        while(!nextPageToken.equals("done"))
        {
            if(!nextPageToken.equals(""))
                youtubeURL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&pageToken="+nextPageToken+"&playlistId="+playlistID+"&key="+config.get("youtube_api_key");

            InputStreamReader bf = new InputStreamReader(new URL(youtubeURL).openStream());

            StringBuilder response = new StringBuilder();
            while(true)
            {
                int c = bf.read();
                if(c == -1) break;
                response.append((char) c);
            }

            JSONObject responseJSON = new JSONObject(response.toString());

            if(responseJSON.has("nextPageToken"))
                nextPageToken = responseJSON.getString("nextPageToken");
            else
                nextPageToken = "done";

            JSONArray items = responseJSON.getJSONArray("items");

            for(int i = 0;i<items.length();i++)
            {
                JSONObject eachSong = items.getJSONObject(i);
                JSONObject snippet = eachSong.getJSONObject("snippet");
                JSONObject idObj = snippet.getJSONObject("resourceId");
                if(eachSong.getString("kind").equals("youtube#playlistItem"))
                {
                    String title = snippet.getString("title");
                    String channelTitle = snippet.getString("channelTitle");

                    if(!snippet.has("thumbnails")) continue;

                    JSONObject thumbnail = snippet.getJSONObject("thumbnails");
                    String defaultThumbnailURL = thumbnail.getJSONObject("default").getString("url");

                    String videoId = idObj.getString("videoId");

                    HashMap<String,Object> songDetails = new HashMap<>();
                    songDetails.put("location","youtube");
                    songDetails.put("videoID",videoId);
                    songDetails.put("title",title);
                    songDetails.put("channelTitle",channelTitle);
                    songDetails.put("thumbnail",defaultThumbnailURL);

                    toReturn.add(songDetails);
                }
            }
        }

        return toReturn;
    }

    @FXML
    public void selectMusicLibraryFolderButtonClicked(ActionEvent event)
    {
        Node e = (Node) event.getSource();
        Window ps = e.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File newMusicFolder = directoryChooser.showDialog(ps);
        File presentFolder = new File(config.get("music_lib_path"));

        if(newMusicFolder == null) return;

        if(!newMusicFolder.getAbsolutePath().equals(presentFolder.getAbsolutePath()))
        {
            selectMusicLibraryField.setText(newMusicFolder.getAbsolutePath());
        }
    }

    @FXML
    public void applySettingsButtonClicked()
    {
        String errs = "";

        if(youtubeAPIKeyField.getText().length() == 0)
        {
            errs += "* Invalid Youtube API Key Entered.\n";
        }

        if(recentsPlaylistMaxLimitField.getText().length() == 0)
        {
            errs += "* Invalid Limit for Recents Playlist. Must be in figures.\n";
        }
        else
        {
            try
            {
                Integer.parseInt(recentsPlaylistMaxLimitField.getText());
            }
            catch (Exception e)
            {
                errs += "* 0 cant be a limit for Recents Playlist!\n";
            }
        }

        if(errs.length() > 0)
        {
            showErrorAlert("Uh Oh!","Please correct the following errors and try again :\n"+errs);
            return;
        }

        Thread s = new Thread(new Task<Void>() {
            @Override
            protected Void call(){
                Platform.runLater(()->applySettingsButton.setDisable(true));
                try
                {
                    if(!config.get("music_lib_path").equals(selectMusicLibraryField.getText()))
                        loadLibrary();
                    updateConfig("music_lib_path",selectMusicLibraryField.getText());
                    updateConfig("youtube_api_key",youtubeAPIKeyField.getText());
                    updateConfig("max_recents_limit",recentsPlaylistMaxLimitField.getText());
                    cachedPlaylist.get("Recents").clear();
                    maxRecentsLimit = Integer.parseInt(recentsPlaylistMaxLimitField.getText());
                    if(player.isActive)
                    {
                        player.stop();
                        player.hide();
                    }
                    loadLibrary();

                    showErrorAlert("Settings Applied","New Settings have been applied!");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                Platform.runLater(()->applySettingsButton.setDisable(false));
                return null;
            }
        });
        s.setPriority(4);
        s.start();
    }

    public void updateConfig(String key, String newValue)
    {
        config.replace(key,newValue);
        io.writeToFile(config.get("music_lib_path")+"::"+config.get("youtube_api_key")+"::"+config.get("max_recents_limit")+"::","config");
    }

    public void showInternetConnectionError()
    {
        showErrorAlert("Uh Oh!","Unable to connect!\nMake sure you're connected to the internet, and try again!");
    }
}
