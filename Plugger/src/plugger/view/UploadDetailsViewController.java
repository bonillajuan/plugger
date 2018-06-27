package plugger.view;

import java.io.File;

import com.jfoenix.controls.JFXTextField;

import javafx.fxml.FXML;

import plugger.model.Context;
import pluggerserver.UtilityBrano;
import pluggerserver.Brano;

public class UploadDetailsViewController {

	@FXML public JFXTextField titoloField;

    @FXML public JFXTextField artistaField;

    @FXML public JFXTextField albumField;

    @FXML public JFXTextField genereField;

    public Brano selectedBrano = null;

    public String status;
    public static final String SAVED = "SENT";
    public static final String NOT_SAVED = "NOT_SENT";

    @FXML
    public void initialize(){
    	setUploadDetailsBrano(Context.getInstance().getListaBraniUpload().get(0));
    }

    public void setUploadDetailsBrano(Brano brano){
    	setSelectedBrano(brano);
    	setTitoloField(brano.getTitolo());
		setArtistaField(brano.getArtista());
		setAlbumField(brano.getAlbum());
    }

    public void updateDataBrano(Brano brano, File file){
    	System.out.println("BRANO: "+brano.toString()+", FILE: "+file);
    	UtilityBrano utilityBrano = new UtilityBrano(brano, file);

    	brano.setTitolo(titoloField.getText());
    	utilityBrano.setTagTitolo(titoloField.getText());
    	System.out.println("NUOVO TITOLO BRANO: "+brano.getTitolo());

    	brano.setArtista(artistaField.getText());
    	utilityBrano.setTagArtista(artistaField.getText());
    	System.out.println("NUOVO ARTISTA BRANO: "+brano.getArtista());

    	brano.setAlbum(albumField.getText());
    	utilityBrano.setTagAlbum(albumField.getText());
    	System.out.println("NUOVO ALBUM BRANO: "+brano.getAlbum());
    }

	public void setAlbumField(String album) {
		// TODO Auto-generated method stub
		albumField.setText(album);
	}

	public void setArtistaField(String artista) {
		// TODO Auto-generated method stub
		artistaField.setText(artista);
	}

	public void setTitoloField(String titolo) {
		// TODO Auto-generated method stub
		titoloField.setText(titolo);
	}

	public void setSelectedBrano(Brano brano){
		selectedBrano = brano;
	}

	public Brano getSelectedBrano(){
		return selectedBrano;
	}

	public void setStatus(Brano brano, String status){
		getSelectedBrano().setStatus(status);
	}

	public String getStatus(Brano brano){
		return brano.getStatus();
	}

	public boolean isSaved(Brano brano){
		boolean isSaved = false;
		if(brano.getStatus().contentEquals((UploadDetailsViewController.SAVED))){
			isSaved = true;
		}else if(brano.getStatus().contentEquals((UploadDetailsViewController.NOT_SAVED))){
			isSaved = false;
		}
		return isSaved;
	}

	public void updateTableView(){
		Context.getInstance().getUploadViewController().getTableView().refresh();
	}

}
