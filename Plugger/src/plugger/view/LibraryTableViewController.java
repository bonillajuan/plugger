package plugger.view;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pluggerserver.Brano;
import plugger.model.Context;

public class LibraryTableViewController {

	@FXML
	public TableView<Brano> libraryTable;
	@FXML
	public TableColumn<Brano, String> titoloCol;
	@FXML
	public TableColumn<Brano, String> artistaCol;
	@FXML
	public TableColumn<Brano, String> albumCol;
	/*@FXML
	public TableColumn<Brano, String> pathFileCol;
	@FXML
	public TableColumn<Brano, String> pathCoverCol;*/
	@FXML
	public TableColumn<Brano, String> durataCol;

	@FXML
	public void initialize(){
		setItems();
	}

	public void setItems() {
	    // Add observable list data to the table
		titoloCol.setCellValueFactory(new PropertyValueFactory<>("titolo"));
		artistaCol.setCellValueFactory(new PropertyValueFactory<>("artista"));
		albumCol.setCellValueFactory(new PropertyValueFactory<>("album"));
		durataCol.setCellValueFactory(new PropertyValueFactory<>("durata"));
		//pathFileCol.setCellValueFactory(new PropertyValueFactory<>("pathFile"));
		//pathCoverCol.setCellValueFactory(new PropertyValueFactory<>("pathCover"));

		// The predicate here displays all files
        FilteredList<Brano> filteredFiles = new FilteredList<>(Context.getInstance().getListaBraniClient(), mp3 -> true);

        Context.getInstance().getMainViewController().getSearchField().textProperty().addListener((observable, oldValue, newValue) -> {
            filteredFiles.setPredicate(brano -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowercaseFilter = newValue.toLowerCase();
                String[] filterFields = new String[] {
                		brano.getTitolo(), brano.getArtista(), brano.getAlbum(),
                };

                for (String field : filterFields) {
                    if (field == null) {
                        continue;
                    }

                    if (field.toLowerCase().contains(lowercaseFilter)) {
                        return true;
                    }
                }

                return false;
            });
        });

        SortedList<Brano> sortedData = new SortedList<>(filteredFiles);
        sortedData.comparatorProperty().bind(libraryTable.comparatorProperty());

		libraryTable.setItems(sortedData);

		libraryTable.setRowFactory(tv -> {
            TableRow<Brano> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Brano brano = row.getItem();
                    Context.getInstance().getClientThread().selectBrano(brano);
                }
            });
            return row ;
        });
	}
}
