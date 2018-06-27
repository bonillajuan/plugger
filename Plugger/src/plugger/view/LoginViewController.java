package plugger.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import plugger.ClientThread;
import plugger.MainPlugger;
import plugger.model.Context;

import java.io.IOException;
import java.net.InetAddress;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

public class LoginViewController {

	@FXML public JFXTextField usernameField;
	@FXML public JFXPasswordField passwordField;
	@FXML public JFXButton loginButton;
	@FXML public JFXButton registerButton;

	private final static String LINK_REGISTRAZIONE = "http://fw.admiralkirov.net:8080/regi.html";

	@FXML
	public void initialize(){
		Context.getInstance().setLoginViewController(this);
	}

	public void startConnection() throws IOException{
		@SuppressWarnings("unused")
		InetAddress localHost = InetAddress.getLocalHost();
		@SuppressWarnings("unused")
		String ipRemote = "192.168.30.233";
		String remoteHost = "fw.admiralkirov.net";

		System.out.println("TENTANDO COLLEGAMENTO AL SERVER...");
		Runnable clientThread = new ClientThread(remoteHost,9090);


		if(Context.getInstance().getClientThread().getClientSocket()!=null){
			System.out.println("AVVIO CLIENT THREAD...");
			new Thread(clientThread).start();
			checkLogin();
		}else{
			System.out.println("SOCKET CHIUSA ::: IMPOSSIBILE AVVIARE THREAD");
		}
	}

	public void checkLogin(){
		String username = usernameField.getText();
		String password = passwordField.getText();
		Context.getInstance().getClientThread().login(username, password);
		usernameField.clear();
		passwordField.clear();
	}

	public void openRegisterLink(){
		System.out.println("OPENING REGISTER LINK.");
		if(Context.getInstance().getHostServices()!=null){
			Context.getInstance().getHostServices().showDocument(LINK_REGISTRAZIONE);
		}else{
			System.out.println("HOST SERVICE NULL: "+Context.getInstance().getHostServices());
		}
	}

	public void showMainView(){
		try{
		Context.getInstance().getLoginStage().close();
		FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainPlugger.class.getResource(VistaNavigator.MAIN));
        BorderPane mainBorderPane = loader.load();
        MainViewController mainViewController = loader.getController();
        VistaNavigator.setMainController(mainViewController);
        Context.getInstance().setMainViewController(mainViewController);
        Stage mainStage = new Stage();
        mainStage.setTitle("Plugger");
        mainStage.getIcons().add(new Image("plugger/view/immagini/icona.jpg"));
        mainStage.setMinWidth(1000);
        mainStage.setMinHeight(650);
        mainStage.setScene(new Scene(mainBorderPane));
        mainStage.show();
        mainStage.setOnCloseRequest(event -> Context.getInstance().getClientThread().disconnectClient());
        Context.getInstance().setMainStage(mainStage);
        VistaNavigator.loadVista(VistaNavigator.LIBRARY_DISPLAY);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
