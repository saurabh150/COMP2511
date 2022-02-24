package unsw.gloriaromanus.frontend;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import unsw.gloriaromanus.backend.Faction;
import unsw.gloriaromanus.backend.Province;
import unsw.gloriaromanus.infratructures.Farm;
import unsw.gloriaromanus.infratructures.Market;
import unsw.gloriaromanus.units.SoldierType;
import unsw.gloriaromanus.units.Unit;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class ProvinceController {

    @FXML
    private TextField taxRateTextField;

    @FXML
    private TextField townWealthTextField;

    @FXML
    private TextField factionTreasuryTextField;

    @FXML
    private Label provinceName;

    @FXML
    private VBox outerContainer1;

    @FXML
    private VBox outerContainer2;

    @FXML
    private VBox editableVBox;

    @FXML
    private Button taxRateButton;

    @FXML
    private Button buttonClose;

    @FXML
    private ImageView farmImageView;

    @FXML
    private VBox farmVBox;

    @FXML
    private ImageView marketImageView;

    @FXML
    private VBox marketVBox;



    private Stage provinceStage;

    private ProvinceScene scene;
    private List<Unit> unitsToMove = new ArrayList<>();
    private Map<String, Image> imageMap;

    private Province start;
    private Province dest;
    private Faction faction;
    private SceneTransitioner sceneTransitioner;
    private boolean farmBuilt = false;
    private boolean marketBuilt = false;
    private static final String PATH1 = "src/unsw/gloriaromanus/config/infrastructure_stats.json";

// ****************************** Init Functions *********************************
    public void init(SceneTransitioner st, Stage s, Faction faction, Province start) {
        this.start = start;
        this.faction = faction;
        this.provinceStage = s;
        this.sceneTransitioner = st;
        try{
            initImageMap();
        } catch(FileNotFoundException f) {
            f.printStackTrace();
        }
    }

    public void init(SceneTransitioner st, Stage s, Faction faction, Province start, Province dest) {
        init(st, s, faction, start);
        this.dest = dest;
    }

    public void initSceneDefault() {
        initStatBox();
        defaultScene();
    }

    public void initSceneMove() {
        initStatBox();
        movingScene();
    }

    private void initStatBox() {
        provinceName.setText(start.getName());
        townWealthTextField.setText(""+start.getTownWealth());
        taxRateTextField.setText(""+start.getTaxRate()+"%");
        factionTreasuryTextField.setText(""+faction.getTreasuryAmount());
    }

    public void initImageMap() throws FileNotFoundException {
        imageMap = new HashMap<>();
        imageMap.put(SoldierType.ARCHERS.toString(), new Image(new FileInputStream("images/CS2511Sprites_No_Background/ArcherMan/Archer_Man_NB.png")));
        imageMap.put(SoldierType.BALLISTA.toString(), new Image(new FileInputStream("images/CS2511Sprites_No_Background/Ballista/Ballista_NB.png")));
        imageMap.put(SoldierType.BERSERKERS.toString(), new Image(new FileInputStream("images/CS2511Sprites_No_Background/Berserker/Berserker_NB.png")));
        imageMap.put(SoldierType.CHARIOTS.toString(), new Image(new FileInputStream("images/CS2511Sprites_No_Background/Chariot/Chariot_NB.png")));
        imageMap.put(SoldierType.DRUID.toString(), new Image(new FileInputStream("images/CS2511Sprites_No_Background/Druid/Celtic_Druid_NB.png")));
        imageMap.put(SoldierType.ELEPHANTS.toString(), new Image(new FileInputStream("images/CS2511Sprites_No_Background/Elephant/Elephant_Archers/Elephant_Archers_NB.png")));
        imageMap.put(SoldierType.HEAVY_CAVALRY.toString(), new Image(new FileInputStream("images/CS2511Sprites_No_Background/Horse/Horse_Heavy_Cavalry/Horse_Heavy_Cavalry_NB.png")));
        imageMap.put(SoldierType.HOPLITE.toString(), new Image(new FileInputStream("images/CS2511Sprites_No_Background/Hoplite/Hoplite_NB.png")));
        imageMap.put(SoldierType.HORSE_ARCHERS.toString(), new Image(new FileInputStream("images/CS2511Sprites_No_Background/Horse/Horse_Archer/Horse_Archer_NB.png")));
        imageMap.put(SoldierType.JAVELIN_SKIRMISHERS.toString(), new Image(new FileInputStream("images/CS2511Sprites_No_Background/ArcherMan/Archer_Man_NB.png")));
        imageMap.put(SoldierType.LANCERS.toString(), new Image(new FileInputStream("images/CS2511Sprites_No_Background/JavelinSkirmisher/Javelin_Skirmisher_NB.png")));
        imageMap.put(SoldierType.LEGIONARY.toString(), new Image(new FileInputStream("images/legionary.png")));
        imageMap.put(SoldierType.MISSILE_INFANTRY.toString(), new Image(new FileInputStream("images/CS2511Sprites_No_Background/Crossbowman/Crossbowman_NB.png")));
        imageMap.put(SoldierType.ONAGER.toString(), new Image(new FileInputStream("images/CS2511Sprites_No_Background/Trebuchet/Trebuchet_NB.png")));
        imageMap.put(SoldierType.PIKEMEN.toString(), new Image(new FileInputStream("images/CS2511Sprites_No_Background/Pikeman/Pikeman_NB.png")));
        imageMap.put(SoldierType.SPEARMEN.toString(), new Image(new FileInputStream("images/CS2511Sprites_No_Background/Spearman/Spearman_NB.png")));
    }

// *********************************** Tax Functions *****************************

    private void handleTaxRate(String value) {
        switch (value) {
            case "10%":
                start.setLowTaxRate();
                break;
            case "15%":
                start.setNormalTaxRate();
                break;
            case "20%":
                start.setHighTaxRate();
                break;
            case "25%":
                start.setVeryHighTaxRate();
                break;
            default:
        }
    }

    @FXML
    public void changeTaxRate(ActionEvent e) {
        Stage popupwindow = new Stage();

        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Change Tax Rate");

        VBox layout= new VBox(10);

        List<String> rates = new ArrayList<>();
        rates.add("10%");
        rates.add("15%");
        rates.add("20%");
        rates.add("25%");

        ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableList(rates));
        comboBox.setOnAction(event -> {
            handleTaxRate(comboBox.getValue());
            popupwindow.close();
            initStatBox();
        });

        comboBox.setMinWidth(158);
        layout.getChildren().add(comboBox);
        layout.setAlignment(Pos.CENTER);
        Scene scene1= new Scene(layout, 200, 150);
        popupwindow.setScene(scene1);
        popupwindow.showAndWait();
    }

    // ******************************* Move Functions *********************************

    private void removeUnitFromMove(String selectedItem) {
        int id = Integer.parseInt(selectedItem);
        Unit unit = null;
        for (Unit u: unitsToMove) {
            if (u.getUnitId() == id) {
                unit = u;
                break;
            }
        }

        if (unit != null) {
            unitsToMove.remove(unit);
            start.addUnit(unit);
        }
    }

    public void addUnitToMove(String unit) {
        //Unit u = start.getUnitByType(unitType);
        Unit u = start.getUnitByID(Integer.parseInt(unit));
        unitsToMove.add(u);
    }

    @FXML
    public void moveUnits(ActionEvent event) {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to move unit(s)?");
        Optional<ButtonType> closeResponse = confirm.showAndWait();
		if (!ButtonType.OK.equals(closeResponse.get())) {
            event.consume();
            for (Unit u: unitsToMove) {
                start.addUnit(u);
            }
            unitsToMove = new ArrayList<>();
            initSceneMove();
		} else if (ButtonType.OK.equals(closeResponse.get())) {
			event.consume();
            Province p = faction.move(unitsToMove, start, dest);

            if (p == null) {
                for (Unit u: unitsToMove) {
                    start.addUnit(u);
                }
                Alert error = new Alert(Alert.AlertType.ERROR, "Not enough movement points");
                error.showAndWait();
            } else {
                SoundController.getInstance().playSoundEffect(Sounds.MARCHING);
                unitsToMove = new ArrayList<>();
                sceneTransitioner.updateGloriaRomanusScene();
            }

            provinceStage.close();
        }

    }

    // ************************* Modifying Scenes *********************************
    public void initInfrastructureScene() {
        farmScene();
        marketScene();
        initStatBox();
    }

    public void farmScene() {

        Image farmImage = null;
        try {
            farmImage = new Image(new FileInputStream(
                    "images/CS2511Sprites_No_Background/TerrainAndBuildings/Buildings/Farm/Farm_NB.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        farmImageView.setImage(farmImage);
        Farm farm = start.getFarm();
        farmVBox.getChildren().clear();

        if (!start.farmExist()) {
            Button farmButton = new Button("Build");
            farmButton.setOnAction(event -> {
                handleBuildFarm(event);
                initInfrastructureScene();
                farmBuilt = true;
            });
            farmVBox.getChildren().add(farmButton);
        }
        if (farm != null) {
            ListView<String> listView = new ListView<>();
            ObservableList<String> items = FXCollections.observableArrayList();
            items.addAll(
                "Infrastructure Type: Farm",
                "Cost: " + farm.getCost(),
                "Cost To Upgrade: " + farm.getCostToUpgrade(),
                "Level: " + farm.getLevel(),
                "TownWealthGeneration: " + farm.getTownWealthGeneration()
            );

            listView.setItems(items);
            farmVBox.getChildren().add(listView);

            if (farm.isUpgradable()) {
                Button farmButton = new Button("Upgrade");
                farmButton.setOnAction(event -> {
                    handleUpgradeFarm(event);
                    initInfrastructureScene();

                });
                farmVBox.getChildren().add(farmButton);
            }
        }
    }

    public void marketScene() {
        Image marketImage = null;
        try {
            marketImage = new Image(new FileInputStream(
                    "images/CS2511Sprites_No_Background/TerrainAndBuildings/Buildings/Market/Market_NB.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        marketImageView.setImage(marketImage);
        Market market = start.getMarket();
        marketVBox.getChildren().clear();

        if (!start.marketExist()) {
            Button marketButton = new Button("Build");
            marketButton.setOnAction(event -> {
                handleBuildMarket(event);
                initInfrastructureScene();
            });
            marketVBox.getChildren().add(marketButton);
        }
        if (market != null) {
            ListView<String> listView = new ListView<>();
            ObservableList<String> items = FXCollections.observableArrayList();
            items.addAll(
                "Infrastructure Type: Farm",
                "Cost: " + market.getCost(),
                "Cost To Upgrade: " + market.getCostToUpgrade(),
                "Level: " + market.getLevel(),
                "TownWealthGeneration: " + market.getTownWealthGeneration()
            );

            listView.setItems(items);
            marketVBox.getChildren().add(listView);

            if (market.isUpgradable()) {
                Button marketButton = new Button("Upgrade");
                marketButton.setOnAction(event -> {
                    handleUpgradeMarket(event);
                    initInfrastructureScene();
                });
                marketVBox.getChildren().add(marketButton);
            }
        }
    }

    public void defaultScene() {
        outerContainer1.getChildren().clear();
        outerContainer2.getChildren().clear();
        outerContainer2.setVisible(false);
        editableVBox.getChildren().clear();

        for (Unit u: unitsToMove) {
            start.addUnit(u);
        }
        unitsToMove = new ArrayList<>();

        ListView<String> listView = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Unit u: start.getUnits()) {
            items.add(u.getUnitType().toString()+" #"+u.getUnitId());
        }
        listView.setItems(items);
        listView.setCellFactory(param -> new ListCell<String>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    imageView.setFitHeight(100);
                    imageView.setFitWidth(100);
                    imageView.setImage(imageMap.get(name.split(" ")[0]));
                    setText(name);
                    setGraphic(imageView);
                }
            }
        });
        listView.setOnMouseClicked(event -> {
            String selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showUnitStats(selected.split(" #")[1]);
            }
        });
        listView.setMinHeight(555);
        outerContainer1.getChildren().addAll(new Label("Units At Province"),listView);
    }

    // Show unit stats on side
    private void showUnitStats(String string) {
        int unitId = Integer.parseInt(string);
        Unit unit = null;
        for (Unit u: start.getUnits()) {
            if (u.getUnitId() == unitId) {
                unit = u;
                break;
            }
        }
        if (unit != null) {
            editableVBox.getChildren().clear();
            editableVBox.setAlignment(Pos.TOP_LEFT);

            ListView<String> listView = new ListView<>();
            ObservableList<String> items = FXCollections.observableArrayList();
            items.addAll(
                "Type: " + unit.getUnitCategory(),
                "Range: "+ unit.isRange(),
                "#Troops: " + unit.getNumSoldiers(),
                "Movement Points: " + unit.getCurrentMovementPoints(),
                "Armour: " + unit.getArmour(),
                "Morale: " + unit.getMorale(),
                "Speed: " + unit.getSpeed(),
                "Missile Attack: " + unit.getMissileAttack(),
                "Melee Attack: " + unit.getMeleeAttack(),
                "Defense Skill: " + unit.getDefenseSkill(),
                "Shield Defense: " + unit.getShieldDefense(),
                "Charge: " + unit.getCharge(),
                "Strength: " + unit.getStrength()
            );

            for (int i = 0; i < unit.getSpecialAbilities().size(); i++) {
                items.add(
                    "Special #"+i+1+": " + unit.getSpecialAbilities().get(i).toString()
                );
            }


            listView.setItems(items);
            editableVBox.getChildren().add(listView);
            //editableVBox.getChildren().addAll(label1, textField1);
        }
    }

    /**
     *
     * Initializing moving scene
     */
    public void movingScene() {
        outerContainer1.getChildren().clear();
        outerContainer2.getChildren().clear();
        outerContainer2.setVisible(true);
        editableVBox.getChildren().clear();

        // Table for units in province
        ListView<String> listView1 = new ListView<>();
        ObservableList<String> items1 = FXCollections.observableArrayList();
        for (Unit u: start.getUnits()) {
            items1.add(u.getUnitType().toString()+" #"+u.getUnitId());
        }
        listView1.setItems(items1);
        listView1.setCellFactory(param -> new ListCell<String>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    imageView.setImage(imageMap.get(name.split(" ")[0]));
                    imageView.setFitHeight(100);
                    imageView.setFitWidth(100);
                    setText(name);
                    setGraphic(imageView);
                }
            }
        });

        listView1.setOnMouseClicked(event -> {
            String selected = listView1.getSelectionModel().getSelectedItem();
            if (selected != null) {
                addUnitToMove(selected.split(" #")[1]);
                movingScene();
            }
        });

        listView1.setMinHeight(555);
        outerContainer1.getChildren().addAll(new Label("Units Remaining at Province "),listView1);

        // Table for units to move from province
        ListView<String> listView2 = new ListView<>();
        ObservableList<String> items2 = FXCollections.observableArrayList();
        for (Unit u: unitsToMove) {
            items2.add(u.getUnitType().toString()+" #"+u.getUnitId());
        }
        listView2.setItems(items2);
        listView2.setCellFactory(param -> new ListCell<String>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    imageView.setFitHeight(100);
                    imageView.setFitWidth(100);
                    imageView.setImage(imageMap.get(name.split(" ")[0]));
                    setText(name);
                    setGraphic(imageView);
                }
            }
        });
        listView2.setMinHeight(555);
        listView2.setOnMouseClicked(event -> {
            String selected = listView2.getSelectionModel().getSelectedItem();
            if (selected != null) {
                removeUnitFromMove(selected.split(" #")[1]);
                movingScene();
            }
        });
        outerContainer2.getChildren().addAll(new Label("Units To Move "),listView2);

        // Move Button
        Button moveButton = new Button("Move");
        moveButton.setOnAction(event -> {
            moveUnits(event);
        });
        editableVBox.setAlignment(Pos.BOTTOM_CENTER);
        editableVBox.getChildren().add(moveButton);
    }

    // ************* Training Units *************
    public void trainUnitScene() {
        defaultScene();
        List<String> units = faction.getTrainableUnits();
        outerContainer2.getChildren().clear();
        outerContainer2.setVisible(true);
        editableVBox.getChildren().clear();

        // Current Units in Province
        ListView<String> listView1 = new ListView<>();
        ObservableList<String> items1 = FXCollections.observableArrayList();
        for (String u: faction.getAffordableUnits()) {
            items1.add(u);
        }
        listView1.setItems(items1);
        listView1.setCellFactory(param -> new ListCell<String>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    imageView.setImage(imageMap.get(name));
                    imageView.setFitHeight(100);
                    imageView.setFitWidth(100);
                    setText(name);
                    setGraphic(imageView);
                }
            }
        });
        listView1.setOnMouseClicked(event -> {
            String selected = listView1.getSelectionModel().getSelectedItem();
            if (selected != null) {
                //faction.createUnitInProvince(start, selected);
                handleTraining(event, selected);
                //initSceneDefault();
            }
        });

        listView1.setMinHeight(555);
        outerContainer2.getChildren().addAll(new Label("Affordable Units "),listView1);


    }

    // ******************************* Infrastructure Functions ************************************

    public void handleBuildFarm(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to build a Farm?");
        Optional<ButtonType> closeResponse = confirm.showAndWait();
        if (!ButtonType.OK.equals(closeResponse.get())) {
            event.consume();
        } else if (ButtonType.OK.equals(closeResponse.get())) {
            event.consume();

            if (affordable("farm", "cost")) {
                buildFarm();
            } else {
                Alert err = new Alert(Alert.AlertType.ERROR, "Cannot afford a farm )): ");
                err.show();
            }
        }
    }

    public boolean affordable(String type, String key) {
        try {
            String content = Files.readString(Paths.get(PATH1));
            JSONObject infrastructure = new JSONObject(content);
            JSONObject json = infrastructure.getJSONObject(type);
            int cost = json.getInt(key);
            if (faction.getTreasuryAmount() >= cost) return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void buildFarm() {
        if (start.buildFarm()) {
            faction.addTreasury(-1 * start.getUnderConsCost());
            Alert info = new Alert(Alert.AlertType.INFORMATION, "Farm is being built, will be ready in " + start.getUnderConsTime() + " turn(s).");
            farmBuilt = true;
            info.show();
            SoundController.getInstance().playSoundEffect(Sounds.MONEY);
            sceneTransitioner.updateGloriaRomanusScene();
        } else {
            Alert err = new Alert(Alert.AlertType.ERROR, "Failed to build farm ):");
            err.show();
        }
    }

    public void handleBuildMarket(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to build a Market?");
        Optional<ButtonType> closeResponse = confirm.showAndWait();
        if (!ButtonType.OK.equals(closeResponse.get())) {
            event.consume();
        } else if (ButtonType.OK.equals(closeResponse.get())) {
            event.consume();
            if (affordable("market", "cost")) {
                buildMarket();
            } else {
                Alert err = new Alert(Alert.AlertType.ERROR, "Cannot afford a market )): ");
                err.show();
            }
        }
    }

    public void buildMarket() {
        if (start.buildMarket()) {
            faction.addTreasury(-1 * start.getUnderConsCost());
            Alert info = new Alert(Alert.AlertType.INFORMATION, "Market is being built, will be ready in " + start.getUnderConsTime() + " turn(s).");
            marketBuilt = true;
            info.show();
            SoundController.getInstance().playSoundEffect(Sounds.MONEY);
            sceneTransitioner.updateGloriaRomanusScene();
        } else {
            Alert err = new Alert(Alert.AlertType.ERROR, "Failed to build market ):");
            err.show();
        }
    }

    public void handleUpgradeFarm(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to upgrade a Farm?");
        Optional<ButtonType> closeResponse = confirm.showAndWait();
        if (!ButtonType.OK.equals(closeResponse.get())) {
            event.consume();
        } else if (ButtonType.OK.equals(closeResponse.get())) {
            event.consume();
            if (affordable("farm", "costToUpgrade")) {
                upgradeFarm();
            } else {
                Alert err = new Alert(Alert.AlertType.ERROR, "Cannot afford to upgrade farm )): ");
                err.show();
            }
        }
    }

    public void handleUpgradeMarket(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to upgrade a Market?");
        Optional<ButtonType> closeResponse = confirm.showAndWait();
        if (!ButtonType.OK.equals(closeResponse.get())) {
            event.consume();
        } else if (ButtonType.OK.equals(closeResponse.get())) {
            event.consume();
            if (affordable("market", "costToUpgrade")) {
                upgradeMarket();
            } else {
                Alert err = new Alert(Alert.AlertType.ERROR, "Cannot afford to upgrade farm )): ");
                err.show();
            }
        }
    }

    private void upgradeFarm() {
        start.upgradeFarm();
        faction.addTreasury(-1 * start.getFarm().getCostToUpgrade());
        Alert info = new Alert(Alert.AlertType.INFORMATION, "Farm is upgraded.");
        info.show();
        SoundController.getInstance().playSoundEffect(Sounds.MONEY);
        sceneTransitioner.updateGloriaRomanusScene();
    }

    private void upgradeMarket() {
        start.upgradeaMarket();
        faction.addTreasury(-1 * start.getMarket().getCostToUpgrade());
        Alert info = new Alert(Alert.AlertType.INFORMATION, "Market is upgraded.");
        info.show();
        SoundController.getInstance().playSoundEffect(Sounds.MONEY);
        sceneTransitioner.updateGloriaRomanusScene();
    }

    // ******************************* Training Function ************************************
    public void handleTraining(MouseEvent event, String type) {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to train "+ type +"?");
        Optional<ButtonType> closeResponse = confirm.showAndWait();
        if (!ButtonType.OK.equals(closeResponse.get())) {
            event.consume();
            initSceneDefault();
        } else if (ButtonType.OK.equals(closeResponse.get())) {
            event.consume();
            Alert info;
            if (faction.createUnitInProvince(start, type)){
                info = new Alert(Alert.AlertType.INFORMATION, "Selected unit is in training, will be ready in " + start.getLastTraineeTurns() + " turn(s).");
                SoundController.getInstance().playSoundEffect(Sounds.MONEY);
            }
            else info = new Alert(Alert.AlertType.ERROR, "Can't train more than 2 units at a time");
            info.show();
            initSceneDefault();
            sceneTransitioner.updateGloriaRomanusScene();
        }
    }

    // ***************************** Scene Functions ***************************************

    @FXML
    public void handleClose(ActionEvent e) {
        for (Unit u: unitsToMove) {
            start.addUnit(u);
        }
        provinceStage.close();
    }

    public void setScene(ProvinceScene provinceScene) {
        this.scene = provinceScene;
    }


}
