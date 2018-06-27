package plugger.model;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import javafx.application.HostServices;
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

public class Context {

	private final static Context instance = new Context();

    public static Context getInstance() {
        return instance;
    }

    public HostServices hostServices;

    public HostServices getHostServices(){
    	return this.hostServices;
    }

    public void setHostService(HostServices hostServices){
    	this.hostServices = hostServices;
    }

    public Stage loginStage;

    public void setLoginStage(Stage loginStage){
    	this.loginStage = loginStage;
    }

    public Stage getLoginStage(){
    	return this.loginStage;
    }

    public Stage mainStage;

    public void setMainStage(Stage mainStage){
    	this.mainStage = mainStage;
    }

    public Stage getMainStage(){
    	return this.mainStage;
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

    public void addBranoToListaBraniUpload(Brano brano){
		System.out.println("FILE ADDED TO LISTA BRANI UPLOAD: "+brano.getPathFile());
		brano.setStatus(UploadDetailsViewController.NOT_SAVED);
		getListaBraniUpload().add(brano);
	}

    public ObservableList<Brano> getListaBraniUpload(){
    	return this.listaBraniUpload;
    }

    public BidiMap<Brano, File> mapFileBrano = new DualHashBidiMap<Brano, File>();

    public BidiMap<Brano, File> getMapFileBrano(){
    	return this.mapFileBrano;
    }

    public void addBranoFileToMap(Brano brano, File file){
    	System.out.println("BRANO ADDED TO BRANO FILE MAP: "+brano.toString());
		System.out.println("FILE ADDED TO BRANO FILE MAP: "+file.toPath());
		getMapFileBrano().put(brano, file);
	}

    public List<Brano> listaBraniRicevuti;

    public void setListaBraniRicevuti(){
    	listaBraniRicevuti = new ArrayList<Brano>();
    }

    public List<Brano> getListaBraniRicevuti(){
    	return this.listaBraniRicevuti;
    }

    public void addBranoToListaBraniRicevuti(Brano brano){
    	System.out.println("BRANO RICEVUTO ADDED TO LIST: "+brano.toString());
		getListaBraniRicevuti().add(brano);
    }

    public void addBraniRicevutiToListaBrani(){
    	for(Brano brano : getListaBraniRicevuti()){
    		getListaBraniClient().add(brano);
    	}
    }

    public ObservableList<Brano> listaBraniClient = FXCollections.observableArrayList();

    public ObservableList<Brano> getListaBraniClient(){
    	return this.listaBraniClient;
    }

    public void addToListaBraniClient(Brano brano){
    	System.out.println("BRANO ADDED TO LISTA BRANI CLIENT: "+brano.toString());
    	getListaBraniClient().add(brano);
    }

    public Map<Brano, ArrayList<Path>> mapBraniFileCoverDownloaded = new HashMap<>();

    public Map<Brano, ArrayList<Path>> getMapBraniFileCoverDownloaded(){
    	return this.mapBraniFileCoverDownloaded;
    }

    public void addToMapBraniFileCoverDownloaded(Brano brano, ArrayList<Path> paths){
    	getMapBraniFileCoverDownloaded().put(brano, paths);
    }

}
