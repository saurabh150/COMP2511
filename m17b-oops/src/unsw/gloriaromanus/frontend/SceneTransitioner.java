package unsw.gloriaromanus.frontend;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import unsw.gloriaromanus.backend.Faction;
import unsw.gloriaromanus.backend.Game;
import unsw.gloriaromanus.backend.Province;

public class SceneTransitioner {
    private final String SETTINGS = "SETTINGS";

	private Stage primaryStage;
	private Game game;
	private GameScene currentScene;
	private GloriaRomanusScene gloriaRomanusScene;
	private int height = 700;
	private int width = 800;
	private Font dramaticFont;

	public SceneTransitioner(Stage primaryStage) {
		this.primaryStage = primaryStage;

		primaryStage.setOnCloseRequest(confirmCloseGameHandler);
        dramaticFont = Font.loadFont(getClass().getResourceAsStream("/fonts/LovesauceRegular.ttf"), 30);
		loadMainMenu();
	}

	private void loadMainMenu() {
		SoundController.getInstance().playSoundEffect(Sounds.MENU);
		if (currentScene != null) currentScene.terminate();
		try {
			currentScene = new StartScene(this);
			setScene(currentScene.getScene());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void setScene(Scene scene) {
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public void transitionToGame() {
		currentScene.terminate();
		try {
			gloriaRomanusScene = new GloriaRomanusScene(this);
			currentScene = gloriaRomanusScene;
			setScene(currentScene.getScene());
			SoundController.getInstance().startBGM();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void transitionToMainMenu() {
		currentScene.terminate();
		loadMainMenu();
	}

	public void transitionToSettings() {
		Stage settingsWindow = new Stage();

        settingsWindow.initModality(Modality.APPLICATION_MODAL);
        settingsWindow.setTitle(SETTINGS);

        VBox layout= new VBox(10);
        layout.getStyleClass().add("settingsBg");
        Label title = new Label(SETTINGS);
        title.setFont(dramaticFont);
        title.setStyle("-fx-text-fill: rgb(92, 79, 73); -fx-font-weight: bold;");
        Pane soundControl = SoundController.getInstance().getMediaToggle();

        Button backButton = new Button("BACK");
        backButton.getStyleClass().add("settingsButton");
        backButton.setFont(dramaticFont);
        backButton.setOnAction(e -> {
            settingsWindow.close();
        });

        Button menuButton = new Button("MAIN MENU");
        menuButton.getStyleClass().add("settingsButton");
        menuButton.setFont(dramaticFont);
        menuButton.setOnAction(e -> {
			handleMainMenuTransition(settingsWindow, e);
        });

        layout.getChildren().addAll(title, soundControl, backButton, menuButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene1= new Scene(layout, 400, 400);
        scene1.getStylesheets().add("unsw/gloriaromanus/settings.css");
        settingsWindow.setScene(scene1);
        settingsWindow.showAndWait();
	}

	private void handleMainMenuTransition(Stage settingsWindow, ActionEvent event) {
		Alert closeConfirmation = createAlert("Are you sure you want to leave the game?", "Go to Main Menu", settingsWindow);

		Optional<ButtonType> closeResponse = closeConfirmation.showAndWait();
		if (!ButtonType.OK.equals(closeResponse.get())) {
			event.consume();
		} else if (ButtonType.OK.equals(closeResponse.get())) {
			event.consume();
			settingsWindow.close();
			transitionToMainMenu();
			SoundController.getInstance().stopBGM();
		}
	}

	// public void transitionToHelp() {

	// }

	public void transitionToGameEnd(boolean won, String faction) {
		Stage endWindow = new Stage();
		String gameEnd = (won) ? "YOU WON!" : "YOU LOST!";
		String imagePath = (won) ? "images/you_win.png" : "images/you_lose.png";

        endWindow.initModality(Modality.APPLICATION_MODAL);
        endWindow.setTitle(faction + " " + gameEnd);

		VBox layout= new VBox(10);
		try {
			Image result = new Image(new FileInputStream(imagePath));
			ImageView imageView = new ImageView(result);
			imageView.setFitHeight(150);
			imageView.setFitWidth(200);
			layout.getChildren().add(imageView);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (!won) {
			Button backButton = new Button("BACK");
			backButton.getStyleClass().add("settingsButton");
			backButton.setFont(dramaticFont);
			backButton.setOnAction(e -> {
				endWindow.close();
			});
			layout.getChildren().add(backButton);
		}

        Button menuButton = new Button("MAIN MENU");
        menuButton.getStyleClass().add("settingsButton");
        menuButton.setFont(dramaticFont);
        menuButton.setOnAction(e -> {
			handleMainMenuTransition(endWindow, e);
        });

        layout.getChildren().add(menuButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene1= new Scene(layout, 300, 300);
        scene1.getStylesheets().add("unsw/gloriaromanus/settings.css");
        endWindow.setScene(scene1);
        endWindow.showAndWait();
	}
	/**
	 * Handles exit confirmation
	 */
	private EventHandler<WindowEvent> confirmCloseGameHandler =  event -> {
		Alert closeConfirmation = createAlert("Don't leave now! You haven't conquered the world yet!", "Exit", primaryStage);

		Optional<ButtonType> closeResponse = closeConfirmation.showAndWait();
		if (!ButtonType.OK.equals(closeResponse.get())) {
			event.consume();
		}
	};

	public Alert createAlert(String message, String header, Stage stage) {
		Alert closeConfirmation = new Alert(Alert.AlertType.CONFIRMATION, message);
		Button exitButton = (Button) closeConfirmation.getDialogPane().lookupButton(ButtonType.OK);
		exitButton.setText("Quit");
		closeConfirmation.setHeaderText(header);
		closeConfirmation.initModality(Modality.APPLICATION_MODAL);
		closeConfirmation.initOwner(stage);
		return closeConfirmation;
	}

	public void updateGloriaRomanusScene() {
		gloriaRomanusScene.updateScene();
	}

	public void transitionToProvince(Faction f, Province start) throws IOException {
		Stage provinceStage = new Stage();
		ProvinceScene provinceScene = new ProvinceScene(this, provinceStage, f, start);
		provinceScene.start();
	}

	public void transitionToMove(Faction f, Province start, Province dest) throws IOException {
		Stage provinceStage = new Stage();
		ProvinceScene provinceScene = new ProvinceScene(this, provinceStage, f, start, dest);
		provinceScene.start();
	}

	public void transitionToGoal() {
		currentScene.terminate();
		try {
			currentScene = new GoalScene(this);
			setScene(currentScene.getScene());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

}
