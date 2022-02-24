package unsw.gloriaromanus.frontend;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class GoalScene implements GameScene{
	private GoalController controller;
	private Scene scene;
    private Parent root;
    private SceneTransitioner sceneTransitioner;

    public GoalScene(SceneTransitioner sceneTransitioner) {
        this.sceneTransitioner = sceneTransitioner;
    }

    @Override
	public Scene getScene() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("goal.fxml"));
		root = loader.load();
		controller = loader.getController();
		controller.setSceneTransitioner(sceneTransitioner);
		loader.setController(controller);
		scene = new Scene(root, sceneTransitioner.getWidth(), sceneTransitioner.getHeight());
        controller.setScene(this);
        //controller.startUP();
		return scene;
	}

    @Override
    public void terminate() {
        scene = null;
    }

    public SceneTransitioner getSceneTransitioner() {
        return sceneTransitioner;
    }

}
