package unsw.gloriaromanus.frontend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol.HorizontalAlignment;
import com.esri.arcgisruntime.symbology.TextSymbol.VerticalAlignment;
import com.esri.arcgisruntime.data.Feature;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.geojson.FeatureCollection;
import org.geojson.LngLatAlt;

import org.json.JSONArray;
import org.json.JSONObject;
import unsw.gloriaromanus.backend.Faction;
import unsw.gloriaromanus.backend.Game;
import unsw.gloriaromanus.backend.Observer;
import unsw.gloriaromanus.backend.Province;
import unsw.gloriaromanus.victory.Goal;

public class GloriaRomanusController implements Observer {
    private final String INVADE = "INVADE";
    private final String MOVE = "MOVE";
    private final String SETTINGS = "SETTINGS";
    private final String GOAL = "goal";
    private final String WHITE_TEXT_STYLE = "whiteText";
    private final int NUM_COLOURS = 7;
    private List<Integer> colours = Arrays.asList(0xFFFF0000, 0xFF109C48, 0xFF1570B9, 0xFF6643BD, 0xFFBC2AC0, 0xFFD47708, 0xFFA79C4B);
    @FXML
    private StackPane gameBackground;
    @FXML
    private VBox sideBar;
    @FXML
    private MapView mapView;

    private TextField sourceProvince;
    private TextField destProvince;
    private TextArea outputTerminal;

    private ArcGISMap map;

    private Map<String, String> provinceToOwningFactionMap;

    private Map<String, Integer> provinceToNumberTroopsMap;
    private Map<String, Integer> factionToColourMap;

    private Feature currentlySelectedSourceProvince = null;
    private Feature currentlySelectedDestProvince = null;

    private FeatureLayer featureLayer_provinces;

    private Game game;
    private SceneTransitioner sceneTransitioner;
    private Font dramaticFont;
    private Faction currentFaction;
    private StringProperty sourceProvinceLabel;
    private StringProperty destProvinceLabel;
    private StringProperty currentFactionName;
    private IntegerProperty currentFactionWealth;
    private IntegerProperty currentFactionTreasury;
    private IntegerProperty currentYear;
    private StringProperty battleOrMoveProperty;
    private List<String> disabledAttackProvinces;
    private List<String> disabledMoveProvinces;
    private List<String> factions;
    private Map<String, Image> iconImageMap;
    private Map<String, Image> factionImagesMap;
    private boolean firstInitializer = false;
    @FXML
    private void initialize() throws FileNotFoundException {
        currentFactionName = new SimpleStringProperty();
        currentFactionWealth = new SimpleIntegerProperty();
        currentFactionTreasury = new SimpleIntegerProperty();
        currentYear = new SimpleIntegerProperty();
        battleOrMoveProperty = new SimpleStringProperty(INVADE);
        sourceProvinceLabel = new SimpleStringProperty("Your Province");
        destProvinceLabel = new SimpleStringProperty("Enemy Province");
        disabledAttackProvinces = new ArrayList<>();
        disabledMoveProvinces = new ArrayList<>();
        dramaticFont = Font.loadFont(getClass().getResourceAsStream("/fonts/LovesauceRegular.ttf"), 30);
        initImageMap();
        mapFactionImages();
        setupPermanentSidebar();
    }

    public void initData(Game game) throws JsonParseException, JsonMappingException, IOException {
        this.game = game;
        game.init();
        game.attach(this);
        nextPlayer();
        game.hardCodeUnitValues();

        provinceToOwningFactionMap = new HashMap<String, String>();
        provinceToNumberTroopsMap = new HashMap<String, Integer>();
        game.mapProvinceToFaction(provinceToOwningFactionMap);
        game.mapProvinceToNumberTroops(provinceToNumberTroopsMap);
        factions = game.getFactionStrings();
        mapFactionsToColours();

        currentlySelectedSourceProvince = null;
        currentlySelectedDestProvince = null;

        initializeProvinceLayers();
        setupGoals();
    }

    private void setupGoals() {
        Label goalLabel = new Label("GOALS");
        goalLabel.getStyleClass().add(WHITE_TEXT_STYLE);

        Goal goal = game.getGamegoal();
        JSONObject json = goal.toReadable();
        // Deconstructing goal
        Label firstOperator = new Label(json.getString(GOAL));
        firstOperator.getStyleClass().add(WHITE_TEXT_STYLE);
        JSONArray jsonGoals = json.getJSONArray("subgoals");
        JSONObject firstGoal = jsonGoals.getJSONObject(0);
        JSONObject secondGoal = jsonGoals.getJSONObject(1);
        HBox secondGoalBox;

        ImageView goal1 = getGoalImageView(firstGoal.getString(GOAL));

        if (secondGoal.has("subgoals")) {
            Label secondOperator = new Label(secondGoal.getString(GOAL));
            secondOperator.getStyleClass().add(WHITE_TEXT_STYLE);

            JSONArray secondJsonGoals = secondGoal.getJSONArray("subgoals");
            ImageView goal2 = getGoalImageView(secondJsonGoals.getJSONObject(0).getString(GOAL));
            ImageView goal3 = getGoalImageView(secondJsonGoals.getJSONObject(1).getString(GOAL));
            secondGoalBox = new HBox(goal2, secondOperator, goal3);
        } else {
            ImageView goal2 = getGoalImageView(secondGoal.getString(GOAL));
            secondGoalBox = new HBox(goal2);
        }
        secondGoalBox.setAlignment(Pos.CENTER);
        VBox goalVbox = new VBox(goalLabel, goal1, firstOperator, secondGoalBox);
        goalVbox.setAlignment(Pos.CENTER);
        sideBar.getChildren().add(goalVbox);
    }

    private ImageView getGoalImageView(String goal) {
        Image goalImage = iconImageMap.get(goal);
        ImageView imageView = new ImageView();
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        imageView.setImage(goalImage);
        return imageView;
    }

    private void initImageMap() throws FileNotFoundException {
        iconImageMap = new HashMap<>();
        iconImageMap.put(GoalType.TREASURY.toString(), new Image(new FileInputStream("images/treasure.png")));
        iconImageMap.put(GoalType.WEALTH.toString(), new Image(new FileInputStream("images/gold.png")));
        iconImageMap.put(GoalType.CONQUEST.toString(), new Image(new FileInputStream("images/flag.png")));
        iconImageMap.put(SETTINGS, new Image(new FileInputStream("images/settings.png")));
    }

    private void mapFactionsToColours() {
        factionToColourMap = new HashMap<>();
        for (String faction: factions) {
            factionToColourMap.put(faction, colours.get(factions.indexOf(faction) % NUM_COLOURS));
        }
    }

    private void setupPermanentSidebar() {
        //Settings
        Image image = iconImageMap.get(SETTINGS);
        ImageView settingsView = new ImageView(image);
        settingsView.setFitHeight(40);
        settingsView.setFitWidth(40);
        Button settings = new Button();
        settings.setBackground(Background.EMPTY);
        settings.setGraphic(settingsView);
        settings.setOnAction(e -> handleSettings(e));

        // Setup Year
        Label yearLabel = new Label("Year: ");
        Label year = new Label();
        year.textProperty().bind(currentYear.asString());
        year.getStyleClass().add(WHITE_TEXT_STYLE);
        yearLabel.getStyleClass().add(WHITE_TEXT_STYLE);
        HBox yearBox = new HBox(yearLabel, year, settings);
        yearBox.setPadding(new Insets(10));
        BorderPane yearHeader = new BorderPane();
        yearHeader.setRight(yearBox);
        yearHeader.setStyle("-fx-max-width: 70px; -fx-max-height: 30px;");
        StackPane.setAlignment(yearHeader, Pos.TOP_RIGHT);
        gameBackground.getChildren().add(yearHeader);

        // Setup sidebar
        sideBar.setPadding(new Insets(10));
        sideBar.setSpacing(5);

        // Setup faction
        Label faction = new Label("Faction: ");
        Label factionLabel = new Label();
        factionLabel.getStyleClass().add("factionLabel");
        factionLabel.textProperty().bind(currentFactionName);
        HBox factionBar = new HBox(faction, factionLabel);

        // Setup wealth
        Label wealth = new Label("Wealth: ");
        Label currentWealth = new Label();
        currentWealth.textProperty().bind(currentFactionWealth.asString());
        HBox wealthBar = new HBox(wealth, currentWealth);

        // Setup treasury
        Label treasury = new Label("Treasury: ");
        Label currentTreasury = new Label();
        currentTreasury.textProperty().bind(currentFactionTreasury.asString());
        HBox treasuryBar = new HBox(treasury, currentTreasury);

        // Setup battle or move buttons
        Button battleButton = new Button("Battle");
        battleButton.getStyleClass().add("battleButton");
        Button moveButton = new Button("Move");
        moveButton.getStyleClass().add("moveButton");
        HBox actionButtons = new HBox(battleButton, moveButton);
        actionButtons.setSpacing(10);

        // Setup Battle/Move dialogues
        Label sourceProv = new Label("Your province");
        Label destProv = new Label("Enemy province");
        sourceProv.textProperty().bind(sourceProvinceLabel);
        destProv.textProperty().bind(destProvinceLabel);
        sourceProvince = new TextField();
        destProvince = new TextField();
        sourceProvince.setEditable(false);
        destProvince.setEditable(false);
        VBox yourProv = new VBox(sourceProv, sourceProvince);
        yourProv.setSpacing(3);
        VBox oppProv = new VBox(destProv, destProvince);
        oppProv.setSpacing(3);
        HBox battleBox = new HBox(yourProv, oppProv);
        battleBox.setSpacing(5);

        // Invade or move button and dialogue
        Button invadeOrMove = new Button();
        invadeOrMove.getStyleClass().add("invadeOrMove");
        invadeOrMove.setFont(dramaticFont);
        invadeOrMove.textProperty().bind(battleOrMoveProperty);
        invadeOrMove.setOnAction((event) -> {
            try {
                if (battleOrMoveProperty.getValue().equals(INVADE))
                    clickedInvadeButton(event);
                // handle move case
                if (battleOrMoveProperty.getValue().equals(MOVE)) {
                    clickedMoveButton(event);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Label outputLabel = new Label("Output");
        VBox outputBox = new VBox(outputLabel);
        outputTerminal = new TextArea();
        outputTerminal.setEditable(false);
        Button clearTerminal = new Button("Clear");

        clearTerminal.setOnAction((event) -> {
            clearTerminal();
        });

        // Buttons actions
        battleButton.setOnAction((event) -> {
            updateBattleButton();
        });
        moveButton.setOnAction((event) -> {
            updateMoveButton();
        });

        // Next Player action
        Button nextPlayer = new Button("Next Player");
        nextPlayer.getStyleClass().add("nextPlayer");
        nextPlayer.setOnAction((event) -> {
            nextPlayer();
        });
        BorderPane nextPlayerPane = new BorderPane();
        nextPlayerPane.setPadding(new Insets(10));
        nextPlayerPane.setRight(nextPlayer);
        nextPlayerPane.setStyle("-fx-max-width: 70px; -fx-max-height: 30px;");
        StackPane.setAlignment(nextPlayerPane, Pos.BOTTOM_RIGHT);
        gameBackground.getChildren().add(nextPlayerPane);

        sideBar.getChildren().addAll(factionBar, wealthBar, treasuryBar, actionButtons, battleBox, invadeOrMove, outputBox,
                outputTerminal, clearTerminal);
    }

    // changes to next player
    private void nextPlayer() {
        currentYear.setValue(game.getYear());
        Faction faction = game.getFactionTurn();
        if (faction.gameLost()) {
            sceneTransitioner.transitionToGameEnd(false, faction.getName());
            nextPlayer();
            return;
        }
        checkCurrentVictory(faction.getName());

        this.currentFaction = faction;
        refreshFaction();
        disabledAttackProvinces.clear();
        disabledMoveProvinces.clear();
    }

    /**
     * Checks victory of current faction
     * @param factionName
     */
    private void checkCurrentVictory(String factionName) {
        if (game.checkVictory(factionName))
            sceneTransitioner.transitionToGameEnd(true, factionName);
    }

    public void refreshFaction() {
        currentFactionName.setValue(currentFaction.getName());
        currentFactionWealth.setValue(currentFaction.getWealth());
        currentFactionTreasury.setValue(currentFaction.getTreasuryAmount());
        try {
            if (firstInitializer) {
                game.mapProvinceToNumberTroops(provinceToNumberTroopsMap);
                addAllPointGraphics();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        firstInitializer = true;
    }

    // Handles the battle
    private void clickedInvadeButton(ActionEvent e) throws IOException {
        if (currentlySelectedSourceProvince != null && currentlySelectedDestProvince != null) {
            String currentProvince = (String) currentlySelectedSourceProvince.getAttributes().get("name");
            String enemyProvince = (String) currentlySelectedDestProvince.getAttributes().get("name");
            if (confirmIfProvincesConnected(currentProvince, enemyProvince)) {
                SoundController.getInstance().playSoundEffect(Sounds.FIGHT);
                if (game.startBattle(currentProvince, enemyProvince)) {
                    disableProvince(enemyProvince);
                    printMessageToTerminal("Won battle!");
                } else {
                    disableProvince(currentProvince);
                    printMessageToTerminal("Lost battle!");
                }
                game.mapProvinceToNumberTroops(provinceToNumberTroopsMap);
                game.mapProvinceToFaction(provinceToOwningFactionMap);
                resetSelections(); // reset selections in UI
                addAllPointGraphics(); // reset graphics
                checkCurrentVictory(currentFactionName.get());
            } else {
                printMessageToTerminal("Provinces not adjacent, cannot invade!");
            }

        }
    }

    // Handles the Move
    public void clickedMoveButton(ActionEvent e) throws IOException {
        if (currentlySelectedSourceProvince != null && currentlySelectedDestProvince != null) {
            String s = (String) currentlySelectedSourceProvince.getAttributes().get("name");
            String d = (String) currentlySelectedDestProvince.getAttributes().get("name");

            Province start = game.getProvinceFromString(s);
            Province dest = game.getProvinceFromString(d);
            if (game.getMoveUnit().checkPathExists(currentFaction, start, dest)) {
                sceneTransitioner.transitionToMove(currentFaction, start, dest);
            } else {
                printMessageToTerminal("Cannot pass through enemy teritory");
            }

        }
    }

    /**
     * run this initially to update province owner, change feature in each
     * FeatureLayer to be visible/invisible depending on owner. Can also update
     * graphics initially
     */
    private void initializeProvinceLayers() throws JsonParseException, JsonMappingException, IOException {

        Basemap myBasemap = Basemap.createImagery();
        // myBasemap.getReferenceLayers().remove(0);
        map = new ArcGISMap(myBasemap);
        mapView.setMap(map);

        // note - tried having different FeatureLayers for AI and human provinces to
        // allow different selection colors, but deprecated setSelectionColor method
        // does nothing
        // so forced to only have 1 selection color (unless construct graphics overlays
        // to give color highlighting)
        GeoPackage gpkg_provinces = new GeoPackage("src/unsw/gloriaromanus/provinces_right_hand_fixed.gpkg");
        gpkg_provinces.loadAsync();
        gpkg_provinces.addDoneLoadingListener(() -> {
            if (gpkg_provinces.getLoadStatus() == LoadStatus.LOADED) {
                // create province border feature
                featureLayer_provinces = createFeatureLayer(gpkg_provinces);
                map.getOperationalLayers().add(featureLayer_provinces);

            } else {
                System.out.println("load failure");
            }
        });
        addAllPointGraphics();
    }

    // ------------------------------------------------------------------------------
    // Setup unit images

    private void addAllPointGraphics() throws JsonParseException, JsonMappingException, IOException {
        mapView.getGraphicsOverlays().clear();

        InputStream inputStream = new FileInputStream(new File("src/unsw/gloriaromanus/provinces_label.geojson"));
        FeatureCollection fc = new ObjectMapper().readValue(inputStream, FeatureCollection.class);

        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();

        for (org.geojson.Feature f : fc.getFeatures()) {
            if (f.getGeometry() instanceof org.geojson.Point) {
                org.geojson.Point p = (org.geojson.Point) f.getGeometry();
                LngLatAlt coor = p.getCoordinates();
                Point curPoint = new Point(coor.getLongitude(), coor.getLatitude(), SpatialReferences.getWgs84());
                PictureMarkerSymbol s = null;
                String province = (String) f.getProperty("name");
                String faction = provinceToOwningFactionMap.get(province);

                TextSymbol t = new TextSymbol(10,
                        faction + "\n" + province + "\n" + "Strength " + game.getProvinceStrength(province) + "\n" + provinceToNumberTroopsMap.get(province),
                        factionToColourMap.get(faction), HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
                s = new PictureMarkerSymbol(factionImagesMap.get(faction));
                t.setHaloColor(0xFFFFFFFF);
                t.setHaloWidth(2);
                Graphic gPic = new Graphic(curPoint, s);
                Graphic gText = new Graphic(curPoint, t);
                graphicsOverlay.getGraphics().add(gPic);
                graphicsOverlay.getGraphics().add(gText);
            } else {
                System.out.println("Non-point geo json object in file");
            }

        }

        inputStream.close();
        mapView.getGraphicsOverlays().add(graphicsOverlay);
    }

    private void mapFactionImages() throws FileNotFoundException {
        factionImagesMap = new HashMap<>();
        factionImagesMap.put("Romans", new Image(new File("images/CS2511Sprites_No_Background/Flags/Roman/RomanFlag.png").toURI().toString(), 40, 30, false, false));
        factionImagesMap.put("Carthaginians", new Image(new File("images/CS2511Sprites_No_Background/Flags/Carthage/CarthageFlag.png").toURI().toString(), 40, 30, false, false));
        factionImagesMap.put("Gauls", new Image(new File("images/CS2511Sprites_No_Background/Flags/Gallic/GallicFlag.png").toURI().toString(), 40, 30, false, false));
        factionImagesMap.put("Celtic Britons", new Image(new File("images/CS2511Sprites_No_Background/Flags/Celtic/CelticFlag.png").toURI().toString(), 40, 30, false, false));
        factionImagesMap.put("Spanish", new Image(new File("images/CS2511Sprites_No_Background/Flags/Spanish/SpanishFlag.png").toURI().toString(), 40, 30, false, false));
        factionImagesMap.put("Numidians", new Image(new File("images/CS2511Sprites_No_Background/Flags/Numidian/NumidianFlag.png").toURI().toString(), 40, 30, false, false));
        factionImagesMap.put("Egyptians", new Image(new File("images/CS2511Sprites_No_Background/Flags/Egyptian/EgyptianFlag.png").toURI().toString(), 40, 30, false, false));
        factionImagesMap.put("Seleucid Empire", new Image(new File("images/CS2511Sprites_No_Background/Flags/Seleucid/SeleucidFlag.png").toURI().toString(), 40, 30, false, false));
        factionImagesMap.put("Pontus", new Image(new File("images/CS2511Sprites_No_Background/Flags/Pontus/PontusFlag.png").toURI().toString(), 40, 30, false, false));
        factionImagesMap.put("Amenians", new Image(new File("images/CS2511Sprites_No_Background/Flags/Amenian/AmenianFlag.png").toURI().toString(), 40, 30, false, false));
        factionImagesMap.put("Parthians", new Image(new File("images/CS2511Sprites_No_Background/Flags/Parthian/ParthianFlag.png").toURI().toString(), 40, 30, false, false));
        factionImagesMap.put("Germanics", new Image(new File("images/CS2511Sprites_No_Background/Flags/Germanic/GermanicFlag.png").toURI().toString(), 40, 30, false, false));
        factionImagesMap.put("Greek City States", new Image(new File("images/CS2511Sprites_No_Background/Flags/Greek/GreekFlag.png").toURI().toString(), 40, 30, false, false));
        factionImagesMap.put("Macedonians", new Image(new File("images/CS2511Sprites_No_Background/Flags/Macedonian/MacedonianFlag.png").toURI().toString(), 40, 30, false, false));
        factionImagesMap.put("Thracians", new Image(new File("images/CS2511Sprites_No_Background/Flags/Thrace/ThracianFlag.png").toURI().toString(), 40, 30, false, false));
        factionImagesMap.put("Dacians", new Image(new File("images/CS2511Sprites_No_Background/Flags/Dacian/DacianFlag.png").toURI().toString(), 40, 30, false, false));
    }

    // ------------------------------------------------------------------------------
    // Setup ARCGIS map

    private FeatureLayer createFeatureLayer(GeoPackage gpkg_provinces) {
        FeatureTable geoPackageTable_provinces = gpkg_provinces.getGeoPackageFeatureTables().get(0);

        // Make sure a feature table was found in the package
        if (geoPackageTable_provinces == null) {
            System.out.println("no geoPackageTable found");
            return null;
        }

        // Create a layer to show the feature table
        FeatureLayer flp = new FeatureLayer(geoPackageTable_provinces);

        // https://developers.arcgis.com/java/latest/guide/identify-features.htm
        // listen to the mouse clicked event on the map view
        mapView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (battleOrMoveProperty.get().equals(INVADE)) {
                    handleInvadeAction(event, flp);
                } else {
                    handleMoveAction(event, flp);
                }
            } else if (event.getButton() == MouseButton.SECONDARY) {
                handleProvinceClick(event, flp);
            }
        });
        return flp;
    }

    // ------------------------------------------------------------------------------
    // Checks Province stats
    private void handleProvinceClick(MouseEvent event, FeatureLayer flp) {
        // get the screen point where the user clicked or tapped
        Point2D screenPoint = new Point2D(event.getX(), event.getY());

        // specifying the layer to identify, where to identify, tolerance around point,
        // to return pop-ups only, and
        // maximum results
        // note - if select right on border, even with 0 tolerance, can select multiple
        // features - so have to check length of result when handling it
        final ListenableFuture<IdentifyLayerResult> identifyFuture = mapView.identifyLayerAsync(flp, screenPoint, 0,
                false, 25);

        // add a listener to the future
        identifyFuture.addDoneListener(() -> {
            try {
                // get the identify results from the future - returns when the operation is
                // complete
                IdentifyLayerResult identifyLayerResult = identifyFuture.get();
                // a reference to the feature layer can be used, for example, to select
                // identified features
                if (identifyLayerResult.getLayerContent() instanceof FeatureLayer) {
                    FeatureLayer featureLayer = (FeatureLayer) identifyLayerResult.getLayerContent();
                    // select all features that were identified
                    List<Feature> features = identifyLayerResult.getElements().stream().map(f -> (Feature) f)
                            .collect(Collectors.toList());
                    handleProvinceSelection(features, featureLayer);
                }
            } catch (InterruptedException | ExecutionException | IOException ex) {
                // ... must deal with checked exceptions thrown from the async identify
                // operation
                System.out.println("InterruptedException occurred");
            }
        });
    }

    private void handleProvinceSelection(List<Feature> features, FeatureLayer featureLayer) throws IOException {
        if (features.size() > 1) {
            printMessageToTerminal("Have more than 1 element - you might have clicked on boundary!");
        } else if (features.size() == 1) {
            // note maybe best to track whether selected...
            Feature f = features.get(0);
            String province = (String) f.getAttributes().get("name");

            if (provinceToOwningFactionMap.get(province).equals(currentFactionName.get())) {
                Province clickedProvince = game.getProvinceFromString(province);
                sceneTransitioner.transitionToProvince(currentFaction, clickedProvince);
            }
        }
    }

    // -----------------------------------------------------------------------------
    // Battle

    private void handleInvadeAction(MouseEvent event, FeatureLayer flp) {
        // get the screen point where the user clicked or tapped
        Point2D screenPoint = new Point2D(event.getX(), event.getY());

        // specifying the layer to identify, where to identify, tolerance around point,
        // to return pop-ups only, and
        // maximum results
        // note - if select right on border, even with 0 tolerance, can select multiple
        // features - so have to check length of result when handling it
        final ListenableFuture<IdentifyLayerResult> identifyFuture = mapView.identifyLayerAsync(flp, screenPoint, 0,
                false, 25);

        // add a listener to the future
        identifyFuture.addDoneListener(() -> {
            try {
                // get the identify results from the future - returns when the operation is
                // complete
                IdentifyLayerResult identifyLayerResult = identifyFuture.get();
                // a reference to the feature layer can be used, for example, to select
                // identified features
                if (identifyLayerResult.getLayerContent() instanceof FeatureLayer) {
                    FeatureLayer featureLayer = (FeatureLayer) identifyLayerResult.getLayerContent();
                    // select all features that were identified
                    List<Feature> features = identifyLayerResult.getElements().stream().map(f -> (Feature) f)
                            .collect(Collectors.toList());
                    handleInvadeFeatureSelection(features, featureLayer);
                }
            } catch (InterruptedException | ExecutionException ex) {
                // ... must deal with checked exceptions thrown from the async identify
                // operation
                System.out.println("InterruptedException occurred");
            }
        });
    }

    private void handleInvadeFeatureSelection(List<Feature> features, FeatureLayer featureLayer) {
        if (features.size() > 1) {
            printMessageToTerminal("Have more than 1 element - you might have clicked on boundary!");
        } else if (features.size() == 1) {
            // note maybe best to track whether selected...
            Feature f = features.get(0);
            String province = (String) f.getAttributes().get("name");

            if (provinceToOwningFactionMap.get(province).equals(currentFactionName.get())) {
                // province owned by human
                // this disables the player from trying to attack again from the same province
                if (disabledAttackProvinces.contains(province))
                    return;
                if (currentlySelectedSourceProvince != null) {
                    featureLayer.unselectFeature(currentlySelectedSourceProvince);
                }
                currentlySelectedSourceProvince = f;
                sourceProvince.setText(province);
            } else {
                if (currentlySelectedDestProvince != null) {
                    featureLayer.unselectFeature(currentlySelectedDestProvince);
                }
                currentlySelectedDestProvince = f;
                destProvince.setText(province);
            }
            featureLayer.selectFeature(f);
        }
    }
    // -----------------------------------------------------------------------------
    // Moving Units

    // Checks whetehr both provinces selected are from the same province
    private void handleMoveAction(MouseEvent event, FeatureLayer flp) {
        // get the screen point where the user clicked or tapped
        Point2D screenPoint = new Point2D(event.getX(), event.getY());

        // specifying the layer to identify, where to identify, tolerance around point,
        // to return pop-ups only, and
        // maximum results
        // note - if select right on border, even with 0 tolerance, can select multiple
        // features - so have to check length of result when handling it
        final ListenableFuture<IdentifyLayerResult> identifyFuture = mapView.identifyLayerAsync(flp, screenPoint, 0,
                false, 25);

        // add a listener to the future
        identifyFuture.addDoneListener(() -> {
            try {
                // get the identify results from the future - returns when the operation is
                // complete
                IdentifyLayerResult identifyLayerResult = identifyFuture.get();
                // a reference to the feature layer can be used, for example, to select
                // identified features
                if (identifyLayerResult.getLayerContent() instanceof FeatureLayer) {
                    FeatureLayer featureLayer = (FeatureLayer) identifyLayerResult.getLayerContent();
                    // select all features that were identified
                    List<Feature> features = identifyLayerResult.getElements().stream().map(f -> (Feature) f)
                            .collect(Collectors.toList());
                    handleMoveFeatureSelection(features, featureLayer);
                }
            } catch (InterruptedException | ExecutionException ex) {
                // ... must deal with checked exceptions thrown from the async identify
                // operation
                System.out.println("InterruptedException occurred");
            }
        });
    }

    private void handleMoveFeatureSelection(List<Feature> features, FeatureLayer featureLayer) {
        if (features.size() > 1) {
            printMessageToTerminal("Have more than 1 element - you might have clicked on boundary!");
        } else if (features.size() == 1) {
            // note maybe best to track whether selected...
            Feature f = features.get(0);
            String province = (String) f.getAttributes().get("name");
            if (provinceToOwningFactionMap.get(province).equals(currentFactionName.get())) {
                // province owned by current player
                if (currentlySelectedSourceProvince == null && !checkIfSameFeatures(f, currentlySelectedDestProvince)) {
                    currentlySelectedSourceProvince = f;
                    sourceProvince.setText(province);
                    featureLayer.selectFeature(f);
                } else if (currentlySelectedDestProvince == null
                        && !checkIfSameFeatures(f, currentlySelectedSourceProvince)) {
                    currentlySelectedDestProvince = f;
                    destProvince.setText(province);
                    featureLayer.selectFeature(f);
                } else if (checkIfSameFeatures(f, currentlySelectedSourceProvince)) {
                    featureLayer.unselectFeature(currentlySelectedSourceProvince);
                    currentlySelectedSourceProvince = null;
                    sourceProvince.setText("");
                } else if (checkIfSameFeatures(f, currentlySelectedDestProvince)) {
                    featureLayer.unselectFeature(currentlySelectedDestProvince);
                    currentlySelectedDestProvince = null;
                    destProvince.setText("");
                }
            }
        }
    }

    private boolean checkIfSameFeatures(Feature a, Feature b) {
        if (a == null || b == null)
            return false;
        String first = (String) a.getAttributes().get("name");
        String second = (String) b.getAttributes().get("name");
        return first.equals(second);
    }

    private boolean confirmIfProvincesConnected(String province1, String province2) throws IOException {
        String content = Files
                .readString(Paths.get("src/unsw/gloriaromanus/province_adjacency_matrix_fully_connected.json"));
        JSONObject provinceAdjacencyMatrix = new JSONObject(content);
        return provinceAdjacencyMatrix.getJSONObject(province1).getBoolean(province2);
    }

    private void resetSelections() {
        if (currentlySelectedDestProvince != null) {
            featureLayer_provinces.unselectFeature(currentlySelectedDestProvince);
        }
        if (currentlySelectedSourceProvince != null) {
            featureLayer_provinces.unselectFeature(currentlySelectedSourceProvince);
        }
        currentlySelectedDestProvince = null;
        currentlySelectedSourceProvince = null;
        sourceProvince.setText("");
        destProvince.setText("");
    }

    private void printMessageToTerminal(String message) {
        outputTerminal.appendText(message + "\n");
    }

    private void clearTerminal() {
        outputTerminal.setText("");
    }

    private void disableProvince(String province) {
        disabledMoveProvinces.add(province);
        disabledAttackProvinces.add(province);
    }

    //-------------------------------------------------------------------------------
    // Settings
    private void handleSettings(ActionEvent event) {
        sceneTransitioner.transitionToSettings();
    }

    // ------------------------------------------------------------------------------
    // Button update functions
    private void updateBattleButton() {
        resetSelections();
        battleOrMoveProperty.setValue(INVADE);
        sourceProvinceLabel.setValue("Your Prov");
        destProvinceLabel.setValue("Enemy Prov");
    }

    private void updateMoveButton() {
        resetSelections();
        battleOrMoveProperty.setValue(MOVE);
        sourceProvinceLabel.setValue("Start");
        destProvinceLabel.setValue("Destination");
    }

    /**
     * Stops and releases all resources used in application.
     */
    void terminate() {

        if (mapView != null) {
            mapView.dispose();
        }
    }

    public Game getGame() {
        return game;
    }

    public void setSceneTransitioner(SceneTransitioner sceneTransitioner) {
        this.sceneTransitioner = sceneTransitioner;
    }

	@Override
	public void update(String message) {
		printMessageToTerminal(message);
	}


}