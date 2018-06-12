package plugger.view;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import pluggerserver.Brano;
import plugger.model.Context;

public class FirstUploadViewController {

	@FXML public AnchorPane firstUploadView;

	@FXML
	public void initialize(){
		setDragAndDropFile(firstUploadView);
	}

	public void setDragAndDropFile(Node node){
		// Dragging over surface
		node.setOnDragOver(new EventHandler<DragEvent>() {
	        @Override
	        public void handle(DragEvent event) {
	            Dragboard db = event.getDragboard();
		            //boolean isAccepted = db.getFiles().get(0).toPath().toString().endsWith(".mp3");
		            boolean isAccepted = FilenameUtils.getExtension(db.getFiles().get(0).getPath().toString()).equals("mp3");
			            if (db.hasFiles() && isAccepted) {
			            	node.setStyle("-fx-border-color: red;"
			                        + "-fx-border-width: 5;"
			                        + "-fx-background-color: #C6C6C6;"
			                        + "-fx-border-style: solid;");
			                event.acceptTransferModes(TransferMode.COPY);
			            }else{
			            	event.consume();
			            }
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
	                		Context.getInstance().addFileToListaBraniUpload(brano);
	                		System.out.println("ADDING TO LIST FIRST BRANO: "+Context.getInstance().getListaBraniUpload().get(0).getTitolo());
	                	}else{
	                		System.out.println("FILE NOT VALID: "+file.toString());
	                	}
	                }
	                showUploadView();
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
			Context.getInstance().addFileToListaBraniUpload(brano);
			showUploadView();
		}
	}

	public void showUploadView() {
        VistaNavigator.loadVista(VistaNavigator.UPLOAD_VIEW);
    }



}
