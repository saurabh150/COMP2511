package unsw.gloriaromanus.frontend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundController {
	/**
	 * Singleton
	 */
	private static SoundController instance = new SoundController();
	private Media media;
	private MediaPlayer mediaPlayer;
	private HBox mediaToggle;

	private Map<Sounds, AudioClip> sounds = new HashMap<>();
	private Map<Boolean, Image> soundImages = new HashMap<>();
	private Boolean isBGMOn;
	private Slider volumeSlider;
	private DoubleProperty volumeProperty;

	private SoundController() {
		media = new Media(new File("sounds/background.mp3").toURI().toString());

		// Instantiating MediaPlayer class
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

		sounds.put(Sounds.MARCHING, new AudioClip(new File("sounds/marching.mp3").toURI().toString()));
		sounds.put(Sounds.FIGHT, new AudioClip(new File("sounds/battle.mp3").toURI().toString()));
		sounds.put(Sounds.MONEY, new AudioClip(new File("sounds/money.mp3").toURI().toString()));
		sounds.put(Sounds.MENU, new AudioClip(new File("sounds/menu.mp3").toURI().toString()));

		// setting up images
		try {
			soundImages.put(Boolean.TRUE, new Image(new FileInputStream("images/sound_on.png")));
			soundImages.put(Boolean.FALSE, new Image(new FileInputStream("images/sound_off.png")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		initaliseMediaToggle();

	}

	private void initaliseMediaToggle() {
		volumeSlider = new Slider(0, 1, 0.2);
		volumeSlider.setOrientation(Orientation.VERTICAL);
		volumeProperty = mediaPlayer.volumeProperty();
		volumeProperty.bindBidirectional(volumeSlider.valueProperty());
		bindAllAudio();

		Button toggle = new Button();
		toggle.setBackground(Background.EMPTY);
		toggle.setCursor(Cursor.HAND);
		toggle.setGraphic(getSoundImageView(Boolean.TRUE));
		toggle.setOnAction(e -> {
			toggleMediaPlayer();
			toggle.setGraphic(getSoundImageView(isBGMOn));
		});
		mediaToggle = new HBox(10);
		mediaToggle.setAlignment(Pos.CENTER);
		mediaToggle.getChildren().addAll(toggle, volumeSlider);
	}

	public static SoundController getInstance() {
		return instance;
	}

	public void startBGM() {
		// by setting this property to true, the audio will be played
		stopAllAudio();
		mediaPlayer.play();
		isBGMOn = Boolean.TRUE;
	}

	public void stopBGM() {
		mediaPlayer.pause();
		isBGMOn = Boolean.FALSE;
	}

	private void stopAllAudio() {
		for (AudioClip audio: sounds.values()) {
			audio.stop();
		}
	}

	private void bindAllAudio() {
		for (AudioClip audio: sounds.values()) {
			audio.volumeProperty().bindBidirectional(volumeProperty);
		}
	}

	private void toggleMediaPlayer() {
		if (isBGMOn) {
			stopBGM();
			volumeSlider.setDisable(true);
		} else {
			startBGM();
			volumeSlider.setDisable(false);
		}
	}

	private ImageView getSoundImageView(Boolean on) {
		Image image = soundImages.get(on);
		ImageView imageView = new ImageView(image);
		imageView.setFitHeight(30);
		imageView.setFitWidth(30);
		return imageView;
	}

	public Pane getMediaToggle() {
		return mediaToggle;
	}

	public void playSoundEffect(Sounds type) {
		sounds.get(type).play();
	}
}
