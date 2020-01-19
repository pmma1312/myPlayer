package controller;

import java.io.File;

import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import ressource.Data;
import ressource.References;
import util.Util;
import view.FileTreeItem;

public class PlayActionHandler implements EventHandler<ActionEvent> {

	@Override
	public void handle(ActionEvent e) {
		this.playMethod();
	}

	public void playMethod() {
		if (Data.SONG_QUEUE.size() == 0 || Util.checkIfPlaylistOrDirChanged()) {
			Util.generateSongQueue();
		}

		if (Data.SONG_QUEUE.size() > 0) {
			FileTreeItem selectedItem = Data.SONG_QUEUE.get(Data.SONG_QUEUE_POSITION);
			File file = new File(selectedItem.getPath());

			if (!file.isDirectory()) {
				// Change play button
				ImageView imageView = new ImageView(
						new Image(PlayActionHandler.class.getResourceAsStream("../ressource/img/pause.png")));
				imageView.setFitHeight(50);
				imageView.setFitWidth(50);

				References.bPlay.setGraphic(imageView);
				References.bPlay.setOnAction(new PauseActionHandler());

				// Stop the current playing media
				if (References.mediaPlayer != null) {
					if (References.mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED)) {
						References.mediaPlayer.play();
						return;
					} else {
						References.mediaPlayer.stop();
					}
				}

				Media audioFile = new Media(file.toURI().toString());
				References.songPlayingTitleLabel.setText(selectedItem.getValue());
				References.songPlayingArtistLabel.setText("Unknown Artist");
				References.songPlayingAlbum.setText("Unknown Album");
				References.coverImage
						.setImage(new Image(getClass().getResourceAsStream("../ressource/img/defaultcover.jpg")));

				audioFile.getMetadata().addListener(new MetaDataChangeListener());

				MediaPlayer player = new MediaPlayer(audioFile);
				
				player.currentTimeProperty().addListener(
						(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
							References.labelTimeIndicator
									.setText(Util.formatDecimalToMinutes(player.getCurrentTime().toSeconds()) + " / "
											+ Util.formatDecimalToMinutes(player.getTotalDuration().toSeconds()));
							References.mediaProgressBar.setProgress(
									player.getCurrentTime().toMillis() / player.getTotalDuration().toMillis());
						});

				player.setVolume(References.volumeSlider.getValue() / 100);
				player.play();

				player.setOnEndOfMedia(() -> {
					if (References.checkBoxRepeat.isSelected()) {
						player.seek(Duration.ZERO);
					} else {
						if(Data.SONG_QUEUE_POSITION < (Data.SONG_QUEUE.size() - 1)) {
							Data.SONG_QUEUE_POSITION++;
							this.playMethod();
						} else {
							this.reset();
						}
					}
				});

				References.mediaPlayer = player;
			} else {
				this.reset();
			}
		}

	}
	
	private void reset() {
		References.mediaProgressBar.setProgress(0);
		References.labelTimeIndicator.setText("00:00 / 00:00");
		References.songPlayingTitleLabel.setText("No song playing");
		References.songPlayingAlbum.setText("");
		References.songPlayingArtistLabel.setText("");
	}

}
