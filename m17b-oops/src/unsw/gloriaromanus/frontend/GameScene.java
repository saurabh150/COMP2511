package unsw.gloriaromanus.frontend;

import java.io.IOException;

import javafx.scene.Scene;

/**
 * Represents a generic game scene
 */
public interface GameScene {

	Scene getScene() throws IOException;

	void terminate();

}
