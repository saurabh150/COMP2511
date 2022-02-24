package unsw.gloriaromanus.frontend;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import unsw.gloriaromanus.backend.Faction;
import unsw.gloriaromanus.backend.Province;

public class ProvinceScene {
    private Stage stage;
	private String title;
	private ProvinceController controller;
	private Scene scene;
    private Parent root;

    public void start() {
		stage.setTitle(title);
		stage.setScene(scene);
		stage.showAndWait();
	}
    public ProvinceController getController() {
        return controller;
    }
    // GloriaRomanusApplication application
    public ProvinceScene(SceneTransitioner st, Stage s, Faction faction,
    Province start) throws IOException {
        this.stage = s;
		//this.stage = application.getStage();
        title = "GloriaRomanus";

		FXMLLoader loader = new FXMLLoader(getClass().getResource("province.fxml"));
		root = loader.load();
		controller = loader.getController();
        //controller.attach((Observer) application);
        controller.init(st, s, faction, start);
        loader.setController(controller);

        controller.initSceneDefault();
        controller.initInfrastructureScene();
        //scene = new Scene(root, application.getWidth(), application.getHeight());
        scene = new Scene(root, 750, 673);
		controller.setScene(this);
    }

    public ProvinceScene(SceneTransitioner st, Stage s, Faction faction,
    Province start, Province dest) throws IOException {
        this.stage = s;
		//this.stage = application.getStage();
		title = "GloriaRomanus";

		FXMLLoader loader = new FXMLLoader(getClass().getResource("province.fxml"));
		root = loader.load();
		controller = loader.getController();
        //controller.attach((Observer) application);
        controller.init(st, s, faction, start, dest);
        
        loader.setController(controller);

        controller.initSceneMove();
        controller.initInfrastructureScene();

        //scene = new Scene(root, application.getWidth(), application.getHeight());
        scene = new Scene(root, 750, 673);
		controller.setScene(this);
    }


}
