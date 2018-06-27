package plugger.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import plugger.MainPlugger;
import plugger.model.Context;

public class LibraryDisplayViewController {

	@FXML
	public AnchorPane libraryDisplay;

	@FXML
	public AnchorPane detailsDisplay;


	@FXML
	public void initialize(){
		Context.getInstance().getClientThread().updateHomepage();
		setLibraryTableView();
		setLibraryDetailsView();
	}

	public void setLibraryTableView(){
		try{
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainPlugger.class.getResource(VistaNavigator.TABLE_VIEW));
	        AnchorPane tableView = loader.load();
			libraryDisplay.getChildren().add(tableView);
			MainPlugger.setNode(libraryDisplay, tableView);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void setLibraryDetailsView(){
		try{
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainPlugger.class.getResource(VistaNavigator.LIBRARY_DETAILS_VIEW));
	        VBox libraryDetailsView = loader.load();
	        LibraryDetailsViewController libraryDetailsViewController = loader.getController();
	        Context.getInstance().setLibraryDetailsViewController(libraryDetailsViewController);
	        detailsDisplay.getChildren().add(libraryDetailsView);
			MainPlugger.setNode(detailsDisplay, libraryDetailsView);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
