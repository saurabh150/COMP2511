package unsw.gloriaromanus.frontend;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import unsw.gloriaromanus.backend.Game;

public class StartGameController {
	@FXML
	private StackPane stackPane;

	@FXML
	private Button startGameButton;

	@FXML
	private TextField numberOfPlayers;

	@FXML
	private Label numberOfPlayersError;

	@FXML
	private VBox outerContainer;

	private StartScene scene;

	private Game game;
	private SceneTransitioner sceneTransitioner;

	private int nPlayers = 0;
	private int currentPlayer = 0;

	@FXML
	private void initialize() throws FileNotFoundException {
		Image im = new Image(new FileInputStream("images/war_background.jpg"));
		stackPane.setBackground(new Background(new BackgroundImage(im, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1.0, 1.0, true, true, false, false))));
		Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/The_wastes_of_space.ttf"), 75);
		startGameButton.setFont(font);
		outerContainer.setSpacing(20);
		numberOfPlayers.setAlignment(Pos.CENTER);
	}

	@FXML
	public void handleStartGameBtn(ActionEvent event) {
		attemptToStartGame();
	}

	private void attemptToStartGame() {
		String returnMsg = "";
		try {
			if (numberOfPlayers.getText().length() > 0) {
				nPlayers = Integer.parseInt(numberOfPlayers.getText());
				returnMsg = Game.checkNPlayers(nPlayers);
			}
		} catch (Exception e) {
			returnMsg = "Invalid number of players!";
		}
		numberOfPlayersError.setText(returnMsg);
		if (returnMsg.equals("Success")) {
			clearScreen();
			game = new Game();
			allocatePlayers();
		}
	}

	/**
	 * Handles creating the allocating player scene
	 */
	private void allocatePlayers() {
		currentPlayer++;
		scene.allocatePlayersScene(outerContainer, currentPlayer, game.getUnusedFactionStrings());
	}

	/**
	 * Handles the next player allocation after the next button is clicked
	 *
	 * @param faction
	 */
	public void handleNext(String faction) {
		game.selectedFaction(faction);

		if (currentPlayer < nPlayers) {
			allocatePlayers();
		} else {
			sceneTransitioner.setGame(game);
			Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to use an auto-generated goal?");
			((Button) confirm.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");
			Optional<ButtonType> closeResponse = confirm.showAndWait();;
        	if (!ButtonType.OK.equals(closeResponse.get())) {
            	sceneTransitioner.transitionToGoal();
        	} else if (ButtonType.OK.equals(closeResponse.get())) {
            	sceneTransitioner.transitionToGame();
       	 	}
			//sceneTransitioner.transitionToGoal();
			//sceneTransitioner.transitionToGame(game);
		}
	}

	private void clearScreen() {
		outerContainer.getChildren().clear();
	}

	public void setScene(StartScene scene) {
		this.scene = scene;
	}

	public Game getGame() {
		return this.game;
	}

	public void setSceneTransitioner(SceneTransitioner sceneTransitioner) {
		this.sceneTransitioner = sceneTransitioner;
	}


}
