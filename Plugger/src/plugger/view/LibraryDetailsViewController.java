package plugger.view;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;

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

		ArrayList<Path> paths = Context.getInstance().getMapBraniFileCoverDownloaded().get(brano);

		InputStream imageStream = null;
		try {
			imageStream = new FileInputStream(paths.get(1).toFile());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Image coverBrano = new Image(imageStream);
		setImageDetailsBrano(coverBrano);

		getImageView().setImage(getImageDetailsBrano());
    }

	public ImageView getImageView(){
		return this.coverImageView;
	}

	public Image getImageDetailsBrano(){
		return this.coverBrano;
	}

	public void setImageDetailsBrano(Image coverBrano){
		if(getImageDetailsBrano()!=null)getImageDetailsBrano().cancel();
		this.coverBrano = coverBrano;
	}

	public void removeImagePlayer(){
		getImageDetailsBrano().cancel();
		coverBrano = null;
		getImageView().setImage(null);
		System.gc();
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
