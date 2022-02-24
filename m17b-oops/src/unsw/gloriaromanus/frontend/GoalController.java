package unsw.gloriaromanus.frontend;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import unsw.gloriaromanus.victory.Goal;
import unsw.gloriaromanus.victory.GoalAND;
import unsw.gloriaromanus.victory.GoalComposite;
import unsw.gloriaromanus.victory.GoalConquest;
import unsw.gloriaromanus.victory.GoalOR;
import unsw.gloriaromanus.victory.GoalTreasury;
import unsw.gloriaromanus.victory.GoalWealth;
import unsw.gloriaromanus.victory.VictoryCondition;

public class GoalController {

    @FXML
    private VBox mainVBox;

    @FXML
    private Button nextButton;

    @FXML
    private Pane pane1;

    private SceneTransitioner sceneTransitioner;

    private List<String> conditionsLeft = new ArrayList<>();
    private List<String> logicalLeft = new ArrayList<>();
    private List<String> hierarchicy = new ArrayList<>();

    private boolean stage2 = false;
    private boolean stage3 = false;
    private boolean stage4 = true;
    private boolean stage5 = false;

    private GoalScene goalScene;

    @FXML
    public void initialize() {
        Image im = null;
        try {
            im = new Image(new FileInputStream("images/war_background.jpg"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
		pane1.setBackground(new Background(new BackgroundImage(im, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1.0, 1.0, true, true, false, false))));

        resetGoal();
    }

    public void startUP() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to use an auto-generated goal?");
        ((Button) confirm.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");
        Optional<ButtonType> closeResponse = confirm.showAndWait();;
        if (!ButtonType.OK.equals(closeResponse.get())) {
            setUp1();
        } else if (ButtonType.OK.equals(closeResponse.get())) {
            sceneTransitioner.transitionToGame();
        }
    }

    public void setUp1() {
        HBox hb1 = new HBox();
        hb1.setAlignment(Pos.CENTER);
        mainVBox.getChildren().clear();

        ComboBox<String> comboBox1 = new ComboBox<>(FXCollections.observableList(getLogicInString()));
        comboBox1.setOnAction(event -> {
            String val = comboBox1.getValue();
            if (val != null) {
                System.out.println("value from combobox1: "+ val);
                hierarchicy.add(val);
                comboBox1.setDisable(true);
                setUp2();
            }
        });
        hb1.getChildren().add(comboBox1);

        mainVBox.getChildren().addAll(new Label(" "), hb1, new Label(" "));

    }

    public void setUp2() {
        if (!stage2){
            System.out.println("In SetUp2");
            stage2 = true;
            HBox hb1 = new HBox();
            hb1.setAlignment(Pos.CENTER);

            Label l = new Label("    ");

            ComboBox<String> comboBox2 = new ComboBox<>(FXCollections.observableList(getConditionsAndLogicInString()));
            comboBox2.setOnAction(event -> {
                String val = comboBox2.getValue();
                if (val != null) {
                    System.out.println("value from combobox2: "+ val);
                    hierarchicy.add(val);
                    comboBox2.setEditable(false);
                    if (conditionsLeft.contains(val))
                        conditionsLeft.remove(val);
                    else stage4 = false;
                    comboBox2.setDisable(true);
                    setUp3(hb1);


                }
            });

            hb1.getChildren().addAll(comboBox2, l);
            mainVBox.getChildren().add(hb1);

        }

    }

	private void setUp3(HBox hb1) {
        if (!stage3){
            stage3 = true;
            ComboBox<String> comboBox3 = new ComboBox<>(FXCollections.observableList(getConditionsInString()));
            comboBox3.setOnAction(event -> {
                String val = comboBox3.getValue();
                if (val != null) {
                    System.out.println("value from combobox3: "+ val);
                    hierarchicy.add(val);
                    conditionsLeft.remove(val);
                    comboBox3.setEditable(false);
                    comboBox3.setDisable(true);
                    if (!stage4)
                        setUp4();
                }
            });
            hb1.getChildren().add(comboBox3);
        }
    }

    private void setUp4() {
        if (!stage4){
            stage4 = true;
            mainVBox.getChildren().add(new Label(" "));

            HBox hb1 = new HBox();
            hb1.setAlignment(Pos.CENTER_LEFT);
            ComboBox<String> comboBox4 = new ComboBox<>(FXCollections.observableList(getConditionsInString()));
            comboBox4.setOnAction(event -> {
                String val = comboBox4.getValue();
                if (val != null) {
                    conditionsLeft.remove(val);
                    System.out.println("value from combobox4: "+ val);
                    hierarchicy.add(val);
                    comboBox4.getEditor().setEditable(false);
                    comboBox4.setDisable(true);
                    stage5 = false;

                    setUp5(hb1);
                }
            });
            hb1.getChildren().addAll(comboBox4, new Label(" "), new Label(" "), new Label(" "), new Label(" "), new Label(" "));
            mainVBox.getChildren().addAll(hb1);
        }
    }

    private void setUp5(HBox hb1) {
        if (!stage5){
            stage5 = true;
            ComboBox<String> comboBox5 = new ComboBox<>(FXCollections.observableList(getConditionsInString()));
            comboBox5.setOnAction(event -> {
                String val = comboBox5.getValue();
                if (val != null) {
                    conditionsLeft.remove(val);
                    System.out.println("value from combobox5: "+ val);
                    hierarchicy.add(val);
                    comboBox5.setEditable(false);
                    comboBox5.setDisable(true);
                }
            });
            hb1.getChildren().add(comboBox5);
        }
    }

    @FXML
    public void resetGoal() {
        stage2 = false;
        stage3 = false;
        stage4 = true;
        stage5 = true;

        conditionsLeft = new ArrayList<>();
        logicalLeft = new ArrayList<>();
        hierarchicy = new ArrayList<>();
        conditionsLeft.add("WEALTH");
        conditionsLeft.add("CONQUEST");
        conditionsLeft.add( "TREASURY");

        logicalLeft.add("AND");
        logicalLeft.add("OR");
        setUp1();
    }

    private List<String> getConditionsAndLogicInString() {
        List<String> conditions = new ArrayList<>();
        for (String vc: logicalLeft) {
            conditions.add(vc);
        }

        for (String vc: conditionsLeft) {
            conditions.add(vc);
        }

        return conditions;
    }

    private List<String> getLogicInString() {
        List<String> conditions = new ArrayList<>();
        for (String vc: logicalLeft) {
            conditions.add(vc);
        }


        return conditions;
    }

    private List<String> getConditionsInString() {
        List<String> conditions = new ArrayList<>();
        for (String vc: conditionsLeft) {
            conditions.add(vc);
        }

        return conditions;
    }


    public void setSceneTransitioner(SceneTransitioner sceneTransitioner) {
        System.out.println("\n*********** This has ran **********\n");
        this.sceneTransitioner = sceneTransitioner;
	}

	public void setScene(GoalScene goalScene) {
        this.goalScene = goalScene;
	}

    @FXML
    public void handleNext(){

        if (!stage2 || !stage3 || !stage4 || !stage5) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Please complete the goal selection.");
            a.show();
        } else {
            VictoryCondition logic1 = stringToCondition(hierarchicy.get(0));
            GoalComposite gc1 = new GoalComposite(logic1);

            if (hierarchicy.size() == 3) {
                VictoryCondition condition1 = stringToCondition(hierarchicy.get(1));
                VictoryCondition condition2 = stringToCondition(hierarchicy.get(2));

                Goal g1 = new Goal(condition1);
                Goal g2 = new Goal(condition2);

                gc1.addSubgoal(g1);
                gc1.addSubgoal(g2);


            } else {
                VictoryCondition logic2 = stringToCondition(hierarchicy.get(1));
                VictoryCondition condition1 = stringToCondition(hierarchicy.get(2));
                VictoryCondition condition2 = stringToCondition(hierarchicy.get(3));
                VictoryCondition condition3 = stringToCondition(hierarchicy.get(4));

                GoalComposite gc2 = new GoalComposite(logic2);
                Goal g1 = new Goal(condition1);
                Goal g2 = new Goal(condition2);
                Goal g3 = new Goal(condition3);

                gc2.addSubgoal(g2);
                gc2.addSubgoal(g3);

                gc1.addSubgoal(g1);
                gc1.addSubgoal(gc2);

            }

            sceneTransitioner.getGame().setGameGoal(gc1);
            sceneTransitioner.transitionToGame();
        }
    }

    public VictoryCondition stringToCondition(String s) {
        switch(s) {
            case "AND":
                return new GoalAND();
            case "OR":
                return new GoalOR();
            case "CONQUEST":
                return new GoalConquest();
            case "WEALTH":
                return new GoalWealth();
            case "TREASURY":
                return new GoalTreasury();
            default:
                return null;
        }
    }
}
