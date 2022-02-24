package unsw.gloriaromanus.frontend;

import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class StartScene implements GameScene {

	private StartGameController controller;
	private SceneTransitioner sceneTransitioner;
	private Scene scene;
	private Parent root;

	public StartScene(SceneTransitioner sceneTransitioner) {
		this.sceneTransitioner = sceneTransitioner;
	}

	public void allocatePlayersScene(VBox vBox, int nPlayer, List<String> factions) {
		vBox.getChildren().clear();
		Button next = new Button("Next");
		Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/The_wastes_of_space.ttf"), 40);
		next.setFont(font);
		ComboBox<String> comboBox = new ComboBox<String>(FXCollections.observableList(factions));
		Label name = new Label("Player " + nPlayer);
		name.setFont(font);
        name.setStyle("-fx-text-fill: rgb(92, 79, 73); -fx-font-weight: bold; -fx-font-size: 25pt;");
		comboBox.getSelectionModel().selectFirst();
		vBox.getChildren().add(name);
		vBox.getChildren().add(comboBox);
		vBox.getChildren().add(next);
		next.setOnAction(event -> {
			vBox.getChildren().clear();
			controller.handleNext(comboBox.getValue());
		});
	}

	@Override
	public Scene getScene() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("startGame.fxml"));
		root = loader.load();
		controller = loader.getController();
		controller.setSceneTransitioner(sceneTransitioner);
		loader.setController(controller);
		scene = new Scene(root, sceneTransitioner.getWidth(), sceneTransitioner.getHeight());
		controller.setScene(this);
		return scene;
	}

	@Override
	public void terminate() {
		scene = null;
	}
}
