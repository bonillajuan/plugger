package plugger.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import plugger.MainPlugger;
import plugger.model.Context;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

public class LoginViewController {

	@FXML public JFXTextField usernameField;
	@FXML public JFXPasswordField passwordField;
	@FXML public JFXButton loginButton;
	@FXML public JFXButton registerButton;

	@FXML
	public void initialize(){
		Context.getInstance().setLoginViewController(this);
	}

	public void checkLogin() throws IOException{
		String username = usernameField.getText();
		String password = passwordField.getText();
		Context.getInstance().getClientThread().login(username, password);
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
        mainStage.setScene(new Scene(mainBorderPane));
        mainStage.show();
        VistaNavigator.loadVista(VistaNavigator.LIBRARY_DISPLAY);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
