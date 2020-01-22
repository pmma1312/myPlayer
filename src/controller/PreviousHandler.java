package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ressource.Data;
import ressource.Icons;
import ressource.References;
import util.Util;

public class PreviousHandler implements EventHandler<ActionEvent> {

	@Override
	public void handle(ActionEvent event) {
		if(References.mediaPlayer != null) {
			if(Data.SONG_QUEUE_POSITION > 0) {
				Util.removePlayingIcon();
				Data.SONG_QUEUE_POSITION--;
			}
			
			PlayActionHandler ah = new PlayActionHandler();
			ah.playMethod();
		} else {
			Alert alert = new Alert(AlertType.INFORMATION, "Please play a song first", ButtonType.OK);
			alert.show();
		}
		
	}

}
