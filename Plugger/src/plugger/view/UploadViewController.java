package plugger.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.jfoenix.controls.JFXButton;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import plugger.MainPlugger;

import plugger.model.Context;
import pluggerserver.Brano;

public class UploadViewController {

	@FXML
	public JFXButton uploadButton;
	@FXML
	public Label uploadImageLabel;
	@FXML
	public JFXButton searchFileButton;
	@FXML
	public JFXButton searchImageButton;
	@FXML
	public AnchorPane uploadFileView;
	@FXML
	public VBox uploadImageView;
	@FXML
	public AnchorPane holderUploadFileDetails;
	@FXML
	public TableView<Brano> uploadTable;
	@FXML
	public TableColumn<Brano, String> titoloCol;
	@FXML
	public TableColumn<Brano, String> artistaCol;
	@FXML
	public TableColumn<Brano, String> albumCol;
	@FXML
	public TableColumn<Brano, Boolean> checkCol;
	@FXML
	public TableColumn<Brano,String> statusCol;

	public ImageView imageView = new ImageView();
	public List<File> imageList = new ArrayList<File>();


	@FXML
	public void initialize(){
		showUploadDetailsView();
		setUploadItems();
		setDragAndDropFile(uploadFileView);
		setDragAndDropImage(uploadImageView);
		if(Context.getInstance().getUploadDetailsViewController().getSelectedBrano()!=null){
			setImage(Context.getInstance().getUploadDetailsViewController().getSelectedBrano());
		}
	}

	public void uploadFile(){
		Brano brano = Context.getInstance().getUploadDetailsViewController().getSelectedBrano();
		if(brano.hasCover()){
			File file = Context.getInstance().getMapFileBrano().get(brano);
			System.out.println("FILE PRESO DA MAPPA CON CHIAVE "+brano.toString()+" - "+file.toString());
			if(file!=null){
				Context.getInstance().getUploadDetailsViewController().updateDataBrano(brano, file);
				uploadTable.refresh();
				Context.getInstance().getUploadDetailsViewController().setStatus(brano, UploadDetailsViewController.SAVED);
				changeStatusUploadButton(brano);

				System.out.println("BRANO SALVATO: "+brano.getPathFile());
				System.out.println("BRANO DA INVIARE: "+brano);

				Context.getInstance().getClientThread().uploadBrano(brano);
			}
		}else{
			System.out.println("IL BRANO DEVE AVERE UNA COVER.");
		}
	}

	public TableView<Brano> getTableView(){
		return this.uploadTable;
	}

	public void setDragAndDropFile(Node node){
		// Dragging over surface
		node.setOnDragOver(new EventHandler<DragEvent>() {
	        @Override
	        public void handle(DragEvent event) {
	            Dragboard db = event.getDragboard();
		            boolean isAccepted = FilenameUtils.getExtension(db.getFiles().get(0).getPath().toString()).equals("mp3");
			            if (db.hasFiles() && isAccepted) {
			            	node.setStyle("-fx-border-color: 66ccff;"
			                        + "-fx-border-width: 5;"
			                        + "-fx-background-color: #C6C6C6;"
			                        + "-fx-border-style: solid;");
			                event.acceptTransferModes(TransferMode.COPY);
			            }else{
			            	event.consume();
			            }
	        }
	    });

		node.setOnDragExited(new EventHandler<DragEvent>() {
	        @Override
	        public void handle(DragEvent event) {
            	node.setStyle(null);
	        }
	    });

	    // Dropping over surface
		node.setOnDragDropped(new EventHandler<DragEvent>() {
	        @Override
	        public void handle(DragEvent event) {
	            Dragboard db = event.getDragboard();
	            boolean success = false;
	            if (db.hasFiles()) {
	                success = true;
	                node.setStyle(null);
	                for (File file:db.getFiles()) {
	                	if(FilenameUtils.getExtension(file.getPath().toString()).equals("mp3")){
	                		Brano brano = new Brano(file);
	                		Context.getInstance().addBranoFileToMap(brano, file);
	                		System.out.println("MAP UPDATED - Brano: "+Context.getInstance().getMapFileBrano().getKey(file).toString());
	            			System.out.println("MAP UPDATED - File: "+Context.getInstance().getMapFileBrano().get(brano).toPath());
	                		Context.getInstance().addBranoToListaBraniUpload(brano);
	                	}else{
	                		System.out.println("FILE NOT VALID: "+file.toString());
	                	}
	                }
	            }
	            event.setDropCompleted(success);
	            event.consume();
	        }
	    });
	}

	public void setDragAndDropImage(Node node){
		// Dragging over surface
		node.setOnDragOver(new EventHandler<DragEvent>() {
	        @Override
	        public void handle(DragEvent event) {
	            Dragboard db = event.getDragboard();
	            	boolean isAccepted = FilenameUtils.getExtension(db.getFiles().get(0).getPath().toString()).toLowerCase().equals("png")
	            					|| FilenameUtils.getExtension(db.getFiles().get(0).getPath().toString()).toLowerCase().equals("jpg");
			            if (db.hasFiles() && isAccepted) {
			            	node.setStyle("-fx-border-color: 66ccff;"
			                        + "-fx-border-width: 5;"
			                        + "-fx-background-color: #C6C6C6;"
			                        + "-fx-border-style: solid;");
			                event.acceptTransferModes(TransferMode.COPY);
			            }else{
			            	event.consume();
			            }
	        }
	    });

		node.setOnDragExited(new EventHandler<DragEvent>() {
	        @Override
	        public void handle(DragEvent event) {
            	node.setStyle(null);
	        }
	    });

	    // Dropping over surface;
		node.setOnDragDropped(new EventHandler<DragEvent>() {
	        @Override
	        public void handle(DragEvent event) {
	            Dragboard db = event.getDragboard();
	            boolean success = false;
	            if (db.hasFiles()) {
	                success = true;
	                node.setStyle(null);
	                for (File file:db.getFiles()) {
                		if(FilenameUtils.getExtension(file.getPath().toString()).toLowerCase().equals("png")||
                			FilenameUtils.getExtension(file.getPath().toString()).toLowerCase().equals("jpg")){
            				String pathCover = file.getPath().toString();
            				System.out.println("PATH COVER: "+pathCover);
	                		Context.getInstance().getUploadDetailsViewController().getSelectedBrano().setPathCover(pathCover);
	                		setImage(Context.getInstance().getUploadDetailsViewController().getSelectedBrano());
	                	}else{
	                		System.out.println("FILE NOT VALID: "+file.getPath().toString());
	                	}
	                }
	            }
	            event.setDropCompleted(success);
	            event.consume();
	        }
	    });
	}

	public void filePicker(){
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilterMP3 = new  FileChooser.ExtensionFilter("MP3 files (*.mp3)", "*.MP3");
		    fileChooser.getExtensionFilters().addAll(extFilterMP3);
			File file = fileChooser.showOpenDialog(null);
			if(file != null){
				Brano brano = new Brano(file);
				Context.getInstance().addBranoFileToMap(brano, file);
				System.out.println("MAP UPDATED - Brano: "+Context.getInstance().getMapFileBrano().getKey(file).toString());
    			System.out.println("MAP UPDATED - File: "+Context.getInstance().getMapFileBrano().get(brano).toPath());
				Context.getInstance().addBranoToListaBraniUpload(brano);
			}
	}

	public void setUploadItems(){

		;
		    // Add observable list data to the table
			titoloCol.setCellValueFactory(new PropertyValueFactory<Brano, String>("titolo"));
			artistaCol.setCellValueFactory(new PropertyValueFactory<Brano, String>("artista"));
			albumCol.setCellValueFactory(new PropertyValueFactory<Brano, String>("album"));
			checkCol.setCellValueFactory(new PropertyValueFactory<Brano,Boolean>("hasPathCover"));
			statusCol.setCellValueFactory(new PropertyValueFactory<Brano, String>("status"));

			checkCol.setCellFactory(column -> new CheckBoxTableCell<Brano, Boolean>());

			uploadTable.setItems(Context.getInstance().getListaBraniUpload());

			uploadTable.setRowFactory(tv -> {
	            TableRow<Brano> row = new TableRow<>();
	            row.setOnMouseClicked(event -> {
	                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
	                    Brano brano = uploadTable.getSelectionModel().getSelectedItem();
	                    System.out.println("BRANO SELEZIONATO: "+brano.getPathFile());
	                    Context.getInstance().getUploadDetailsViewController().setUploadDetailsBrano(brano);
	                    setImage(brano);
	                    changeStatusUploadButton(brano);
	                }
	            });
	            return row ;
	        });
	}

	public void changeStatusUploadButton(Brano brano){
		if(Context.getInstance().getUploadDetailsViewController().isSaved(brano)){
        	uploadButton.setDisable(true);
        }else
        if(!Context.getInstance().getUploadDetailsViewController().isSaved(brano)){
        	uploadButton.setDisable(false);
        }
	}

	public void setImage(Brano brano){
		System.out.println("BOOLEAN PATH COVER BRANO: "+brano.hasCover());
		String pathCover = brano.getPathCover();
		System.out.println("SETTING IMAGE FROM BRANO: "+pathCover);
		InputStream imageStream = null;
		if(pathCover!=null){
			System.out.println("PATH COVER NOT NULL.");
			if(uploadImageView.getChildren().removeAll(uploadImageLabel, searchImageButton)){
					uploadImageView.getChildren().add(imageView);
					imageView.setFitHeight(180);
					imageView.setFitWidth(180);
					try {
						imageStream = new FileInputStream(pathCover);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        Image coverBrano = new Image(imageStream);
			        imageView.setImage(coverBrano);
				}else{
					if(uploadImageView.getChildren().contains(imageView)){
						try {
							imageStream = new FileInputStream(pathCover);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				        Image coverBrano = new Image(imageStream);
				        imageView.setImage(coverBrano);
					}
				}
		}else if(pathCover == null){
			System.out.println("PATH COVER NULL.");
			boolean removed;
			if(removed = uploadImageView.getChildren().remove(imageView)){
				System.out.println("REMOVED: "+removed);
				uploadImageView.getChildren().addAll(uploadImageLabel, searchImageButton);
			}
		}
	}

	public void imagePicker(){
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilterImage = new  FileChooser.ExtensionFilter("Image files (*.jpg,*.png)", "*.JPG", "*.PNG");
	    //FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
	    fileChooser.getExtensionFilters().addAll(extFilterImage);
		File selectedFile = fileChooser.showOpenDialog(null);
		if (selectedFile != null) {
			String pathCover = selectedFile.getPath().toString();
			System.out.println("PATH COVER: "+pathCover);
			Context.getInstance().getUploadDetailsViewController().getSelectedBrano().setPathCover(pathCover);
			setImage(Context.getInstance().getUploadDetailsViewController().getSelectedBrano());
	    }
	}

	public void showUploadDetailsView(){
		System.out.println("SHOW UPLOAD DETAILS VIEW");
		try{
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainPlugger.class.getResource(VistaNavigator.UPLOAD_DETAILS_VIEW));
	        VBox uploadDetailsView = loader.load();
	        UploadDetailsViewController uploadDetailsViewController = loader.getController();
	        Context.getInstance().setUploadDetailsViewController(uploadDetailsViewController);
	        holderUploadFileDetails.getChildren().add(uploadDetailsView);
			MainPlugger.setNode(holderUploadFileDetails, uploadDetailsView);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
