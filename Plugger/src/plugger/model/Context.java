package plugger.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import plugger.ClientThread;
import plugger.view.LibraryDetailsViewController;
import plugger.view.LoginViewController;
import plugger.view.MainViewController;
import plugger.view.UploadDetailsViewController;
import plugger.view.UploadViewController;
import pluggerserver.Brano;
import pluggerserver.UtilityBrano;

public class Context {

	private final static Context instance = new Context();

    public static Context getInstance() {
        return instance;
    }

    public Stage loginStage;

    public void setLoginStage(Stage loginStage){
    	this.loginStage = loginStage;
    }

    public Stage getLoginStage(){
    	return loginStage;
    }

    public MainViewController mainViewController;

    public void setMainViewController(MainViewController mainViewController){
    	this.mainViewController = mainViewController;
    }

    public MainViewController getMainViewController() {
        return mainViewController;
    }

    public LoginViewController loginViewController;

    public void setLoginViewController(LoginViewController loginViewController){
    	this.loginViewController = loginViewController;
    }

    public LoginViewController getLoginViewController() {
        return loginViewController;
    }

    public LibraryDetailsViewController libraryDetailsViewController;

    public void setLibraryDetailsViewController(LibraryDetailsViewController libraryDetailsViewController){
    	this.libraryDetailsViewController = libraryDetailsViewController;
    }

    public LibraryDetailsViewController getLibraryDetailsViewController() {
        return libraryDetailsViewController;
    }

    public UploadDetailsViewController uploadDetailsViewController;

    public void setUploadDetailsViewController(UploadDetailsViewController uploadDetailsViewController){
    	this.uploadDetailsViewController = uploadDetailsViewController;
    }

    public UploadDetailsViewController getUploadDetailsViewController() {
        return uploadDetailsViewController;
    }

    public UploadViewController uploadViewController;

    public void setUploadViewController(UploadViewController uploadViewController){
    	this.uploadViewController = uploadViewController;
    }

    public UploadViewController getUploadViewController() {
        return uploadViewController;
    }

    public Playlist playlist;

    public void setPlaylist(Playlist playlist){
    	this.playlist = playlist;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public ClientThread clientThread;

    public void setClientThread(ClientThread clientThread){
    	this.clientThread = clientThread;
    }

    public ClientThread getClientThread(){
    	return this.clientThread;
    }

    public ObservableList<Brano> listaBraniUpload = FXCollections.observableArrayList();

    public void addFileToListaBraniUpload(Brano brano){
		System.out.println("FILE ADDED: "+brano.getPathFile());
		getListaBraniUpload().add(brano);
	}

    public ObservableList<Brano> getListaBraniUpload(){
    	return this.listaBraniUpload;
    }

    public void addBranoFileToMap(Brano brano, File file){
    	System.out.println("BRANO ADDED: "+brano.toString());
		System.out.println("FILE ADDED: "+file.toPath());
		getMapFileBrano().put(brano, file);
	}

    public BidiMap<Brano, File> mapFileBrano = new DualHashBidiMap<Brano, File>();

    public BidiMap<Brano, File> getMapFileBrano(){
    	return this.mapFileBrano;
    }

}
