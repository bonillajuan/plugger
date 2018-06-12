package plugger.view;

import java.io.FileInputStream;
import java.io.InputStream;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pluggerserver.Brano;
import plugger.model.Context;

public class LibraryDetailsViewController {

	@FXML public Label titoloLabel;
	@FXML public Label artistaLabel;
	@FXML public Label albumLabel;
	@FXML public ImageView coverImageView;

	public Image coverBrano;

	@FXML
	public void initialize(){
		if(Context.getInstance().getPlaylist() != null && Context.getInstance().getMainViewController().getCurrentSong()!=null){
			setDetailsBrano(Context.getInstance().getMainViewController().getCurrentSong());
		}
	}

	public void setDetailsBrano(Brano brano){
    	setTitoloLabel(brano.getTitolo());
		setArtistaLabel(brano.getArtista());
		setAlbumLabel(brano.getAlbum());
		InputStream imageStream = null;
		try {
			imageStream = new FileInputStream(brano.getPathCover());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		coverBrano = new Image(imageStream);
		coverImageView.setImage(coverBrano);
    }

	public String getTitoloLabel() {
		return textTitoloProperty().get();
	}

	public void setTitoloLabel(String text) {
		//System.out.println("TITOLO:" +text);
		textTitoloProperty().set(text);
	}

	public StringProperty textTitoloProperty(){
		return titoloLabel.textProperty();
	}

	public String getArtistaLabel() {
		return textArtistaProperty().get();
	}

	public void setArtistaLabel(String text) {
		//System.out.println("ARTISTA:" +text);
		textArtistaProperty().set(text);
	}

	public StringProperty textArtistaProperty(){
		return artistaLabel.textProperty();
	}

	public String getAlbumLabel() {
		return textArtistaProperty().get();
	}

	public void setAlbumLabel(String text) {
		//System.out.println("ALBUM:" +text);
		textAlbumProperty().set(text);
	}

	public StringProperty textAlbumProperty(){
		return albumLabel.textProperty();
	}
}
