package unsw.gloriaromanus.frontend;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class GloriaRomanusScene implements GameScene{

	private GloriaRomanusController controller;
	private Scene scene;
	private Parent root;
	private SceneTransitioner sceneTransitioner;

	public GloriaRomanusScene(SceneTransitioner sceneTransitioner) {
		this.sceneTransitioner = sceneTransitioner;
	}

	public void showProvinceStats() {
		// will fill out with province stats and allow buying?
	}

	@Override
	public Scene getScene() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
		root = loader.load();
		controller = loader.getController();
		loader.setController(controller);
		controller.initData(sceneTransitioner.getGame());
		controller.setSceneTransitioner(sceneTransitioner);
		scene = new Scene(root, sceneTransitioner.getWidth(), sceneTransitioner.getHeight());
		return scene;
	}

	public void updateScene() {
		controller.refreshFaction();
	}

	public void terminate() {
		controller.terminate();
	}

}
