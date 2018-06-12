package plugger;

import java.io.IOException;
import java.net.InetAddress;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import plugger.model.Context;
import plugger.view.VistaNavigator;
import javafx.scene.*;
import javafx.scene.layout.*;

public class MainPlugger extends Application {

	@Override
	public void start(Stage loginStage){
		try {
			loginStage.setTitle("Plugger");
			loginStage.setMinWidth(1000);
			loginStage.setMinHeight(650);
			loginStage.setScene(createScene(loadLoginPane()));
			loginStage.show();
			Context.getInstance().setLoginStage(loginStage);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try{
			InetAddress localHost = InetAddress.getLocalHost();
			String remoteHost = "plugger.zapto.org";
			Runnable clientThread = new ClientThread(localHost,9090);
			System.out.println("AVVIO CLIENT THREAD...");
			new Thread(clientThread).start();
		}catch(Throwable t){
			t.printStackTrace();
		}
		System.out.println("AVVIO PIATTAFORMA...");
		launch(args);
	}

	/**
     * Loads the main fxml layout.
     * Sets up the vista switching VistaNavigator.
     * Loads the first vista into the fxml layout.
     *
     * @return the loaded pane.
     * @throws IOException if the pane could not be loaded.
     */
    private VBox loadLoginPane() throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainPlugger.class.getResource(VistaNavigator.LOGIN));
        VBox loginVBox = loader.load();
        //LoginViewController loginViewController = loader.getController();
        //VistaNavigator.setMainController(mainViewController);
        //Context.getInstance().setMainViewController(mainViewController);
        //VistaNavigator.loadVista(VistaNavigator.LIBRARY_DISPLAY);
		return loginVBox;
    }

    /**
    * Creates the main application scene.
    *
    * @param mainPane the main application layout.
    *
    * @return the created scene.
    */
	private Scene createScene(Pane mainBorderPane) {
        Scene scene = new Scene(mainBorderPane);
        return scene;
    }

	@SuppressWarnings("static-access")
	public static void setNode(AnchorPane parent, Node children){
		parent.setBottomAnchor(children, 0.0);
		parent.setTopAnchor(children, 0.0);
		parent.setLeftAnchor(children, 0.0);
		parent.setRightAnchor(children, 0.0);
	}

}
