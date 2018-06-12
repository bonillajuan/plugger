package plugger.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import plugger.MainPlugger;

public class VistaNavigator {

	/**
     * Convenience constants for fxml layouts managed by the navigator.
     */
    public static final String MAIN = "view/MainView.fxml";
    public static final String LOGIN = "view/LoginView.fxml";
    public static final String LIBRARY_DISPLAY = "view/LibraryDisplayView.fxml";
    public static final String UPLOAD_VIEW = "view/UploadView.fxml";
    public static final String FIRST_UPLOAD_VIEW = "view/FirstUploadView.fxml";
    public static final String TABLE_VIEW = "view/LibraryTableView.fxml";
    public static final String LIBRARY_DETAILS_VIEW = "view/LibraryDetailsView.fxml";
    public static final String UPLOAD_DETAILS_VIEW = "view/UploadDetailsView.fxml";

    /** The main application layout controller. */
    private static MainViewController mainViewController;

    /**
     * Stores the main controller for later use in navigation tasks.
     *
     * @param mainController the main application layout controller.
     */
    public static void setMainController(MainViewController mainViewController) {
        VistaNavigator.mainViewController = mainViewController;
    }

    /**
     * Loads the vista specified by the fxml file into the
     * vistaHolder pane of the main application layout.
     *
     * Previously loaded vista for the same fxml file are not cached.
     * The fxml is loaded anew and a new vista node hierarchy generated
     * every time this method is invoked.
     *
     * A more sophisticated load function could potentially add some
     * enhancements or optimizations, for example:
     *   cache FXMLLoaders
     *   cache loaded vista nodes, so they can be recalled or reused
     *   allow a user to specify vista node reuse or new creation
     *   allow back and forward history like a browser
     *
     * @param fxml the fxml file to be loaded.
     */
    public static void loadVista(String fxml) {
        try {
        	FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainPlugger.class.getResource(fxml));
			Node loaded = loader.load();
        	mainViewController.setVista(loaded);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
