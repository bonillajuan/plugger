package plugger.view;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;

import com.jfoenix.controls.JFXButton;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import plugger.MainPlugger;
import pluggerserver.Brano;
import plugger.model.Context;
import plugger.model.Playlist;

public class MainViewController {

	/** Holder of a switchable vista. */
    @FXML public AnchorPane holderView;

    @FXML public JFXButton uploadViewButton;
    @FXML public JFXButton libraryViewButton;

    /** MediaPlayer instances. **/
    @FXML public Button playButton;
    @FXML public Button previousButton;
    @FXML public Button nextButton;
	@FXML public Label titoloLabel;
	@FXML public Label artistaLabel;
	@FXML public ImageView coverImageView;
	@FXML public Slider tempoSlider;
	@FXML public Slider volumeSlider;
	@FXML public Label playTime;
	@FXML public Label durationTime;
	@FXML public TextField searchField;
	@FXML public Label usernameLabel;
	@FXML public MenuItem logoutItemMenu;
	@FXML public JFXButton refreshButton;

	public Media musicFile;
	public MediaPlayer mediaPlayer;
	public boolean atEndOfMedia = false;
	public boolean repeat = false;
	public boolean stopRequested = false;
	public Image coverBrano;
	public Status status = Status.UNKNOWN;
	public Duration duration;
	public Playlist playlist;
	public Brano currentBrano;

    /**
     * Replaces the vista displayed in the vista holder with a new vista.
     *
     * @param node the vista node to be swapped in.
     */
    public void showUploadView() {
    	if(Context.getInstance().getListaBraniUpload().isEmpty()){
    		VistaNavigator.loadVista(VistaNavigator.FIRST_UPLOAD_VIEW);
		}else
		if(!Context.getInstance().getListaBraniUpload().isEmpty()){
			VistaNavigator.loadVista(VistaNavigator.UPLOAD_VIEW);
		}

    }

    public void showLibraryDisplayView() {
        VistaNavigator.loadVista(VistaNavigator.LIBRARY_DISPLAY);
    }

    @FXML
    public void initialize(){

    	usernameLabel.setText(Context.getInstance().getClientThread().getUsername());
    	this.musicFile = null;
    	this.mediaPlayer = null;

    	Context.getInstance().getListaBraniClient().addListener(new ListChangeListener<Brano>() {
            @Override
            public void onChanged(Change<? extends Brano> c) {
                playlist = new Playlist(Context.getInstance().getListaBraniClient());
                Context.getInstance().setPlaylist(playlist);
            }
        });

    }

    public void skipTrack(){
    	Context.getInstance().getPlaylist().skipTrack();

    	Context.getInstance().getClientThread().selectBrano(Context.getInstance().getPlaylist().getCurrentSong());
    }

    public void previousTrack(){
    	previousButton.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }

            if (event.getClickCount() == 1) {
                mediaPlayer.stop();
                mediaPlayer.play();
            }

            if (event.getClickCount() == 2) {
            	Context.getInstance().getPlaylist().previousTrack();
            	Context.getInstance().getClientThread().selectBrano(Context.getInstance().getPlaylist().getCurrentSong());
            }
        });
    }

    public void setVista(Node node) {
    	holderView.getChildren().clear();
    	holderView.getChildren().setAll(node);
    	//System.out.println("HOLDER VIEW CHILDRENS: "+holderView.getChildren().size());
    	MainPlugger.setNode(holderView, node);
    }

    public void setDataBrano(Brano brano){
    	setTitoloLabel(brano.getTitolo());
		setArtistaLabel(brano.getArtista());
		InputStream imageStream = null;
		try {
			imageStream = new FileInputStream(brano.getPathCover());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Image coverBrano = new Image(imageStream);
		setImagePlayer(coverBrano);

		getImageView().setImage(getImagePlayer());
    }

    public ImageView getImageView(){
    	return this.coverImageView;
    }

    public Image getImagePlayer(){
		return this.coverBrano;
	}

	public void setImagePlayer(Image coverBrano){
		if(getImagePlayer()!=null){
			removeImagePlayer();
		}
		this.coverBrano = coverBrano;
	}

	public void removeImagePlayer(){
		getImagePlayer().cancel();
		coverBrano = null;
		getImageView().setImage(null);
		System.gc();
	}

    public void setCurrentSong(Brano brano){
    	this.currentBrano = brano;
    	Context.getInstance().getPlaylist().setCurrentSong(currentBrano);
    }

    public Brano getCurrentSong(){
    	return currentBrano;
    }

    public void refreshHomepage(){
    	Context.getInstance().getClientThread().updateHomepage();
    }

    public MediaPlayer getMediaPlayer(){
    	return this.mediaPlayer;
    }

    public synchronized void setMediaPlayer(Brano brano){
    	if(this.mediaPlayer != null){
    		mediaPlayer.stop();
    		mediaPlayer.dispose();
    	}

    	ArrayList<Path> paths = Context.getInstance().getMapBraniFileCoverDownloaded().get(brano);

		this.musicFile = new Media(paths.get(0).toFile().toURI().toString());
		this.mediaPlayer = new MediaPlayer(musicFile);

		setCurrentSong(brano);
		setDataBrano(brano);
		Context.getInstance().getLibraryDetailsViewController().setDetailsBrano(brano);

		mediaPlayer.setOnReady(new Runnable() {
			@Override
			public void run() {
				duration = mediaPlayer.getMedia().getDuration();
				//System.out.println(duration);
				updateValues();
			}
		});

		mediaPlayer.setOnEndOfMedia(() -> {
			getMediaPlayer().dispose();
			Context.getInstance().getPlaylist().skipTrack();
			Context.getInstance().getClientThread().selectBrano(Context.getInstance().getPlaylist().getCurrentSong());
        });

		mediaPlayer.setAutoPlay(true);
		status = Status.PLAYING;

		mediaPlayer.currentTimeProperty().addListener(new InvalidationListener()
        {
            public void invalidated(Observable ov) {
                updateValues();
            }
        });
		tempoSlider.valueProperty().addListener(new InvalidationListener() {
		    public void invalidated(Observable ov) {
		       if (tempoSlider.isValueChanging()) {
		       // multiply duration by percentage calculated by slider position
		    	   mediaPlayer.seek(duration.multiply(tempoSlider.getValue() / 100.0));
		       }
		    }
		});
	}

    public void setPlayAndPause(){
    	if (mediaPlayer == null) {
            return;
        }
    	switch(status){
    		case PAUSED:
    			mediaPlayer.play();
    			status = Status.PLAYING;
    			break;
    		case PLAYING:
    			mediaPlayer.pause();
    			status = Status.PAUSED;
		default:
			break;
    	}
    }

    @SuppressWarnings("deprecation")
	public void updateValues() {
		if (playTime != null && tempoSlider != null && volumeSlider != null) {
				Duration currentTime = mediaPlayer.getCurrentTime();
				Duration leftTime = duration.subtract(currentTime);
				formatTime(currentTime, leftTime);
				tempoSlider.setDisable(duration.isUnknown());
				if (!tempoSlider.isDisabled()
		            && duration.greaterThan(Duration.ZERO)
		            && !tempoSlider.isValueChanging()) {
		        	  tempoSlider.setValue(currentTime.divide(duration).toMillis()
		                  * 100.0);
		          }
				if (!volumeSlider.isValueChanging()) {
		            volumeSlider.setValue((int)Math.round(mediaPlayer.getVolume()
		                  * 100));
		          }
	        }
	  }

    public void setTempoSlider(){
	    tempoSlider.valueProperty().addListener(new InvalidationListener() {
		    public void invalidated(Observable ov) {
		       if (tempoSlider.isValueChanging()) {
		       // multiply duration by percentage calculated by slider position
		    	   mediaPlayer.seek(duration.multiply(tempoSlider.getValue() / 100.0));
		       }
		    }
		});
    }

    public void setVolumeSlider(){
		volumeSlider.valueProperty().addListener(new InvalidationListener() {
		    public void invalidated(Observable ov) {
		       if (volumeSlider.isValueChanging()) {
		    	   mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
		       }
		    }
		});
	}

    public void logout(){
    	Context.getInstance().getClientThread().logout();
    	Context.getInstance().getLoginStage().show();
    }

	public void formatTime(Duration elapsed, Duration duration) {
	   int intElapsed = (int)Math.floor(elapsed.toSeconds());
	   int elapsedHours = intElapsed / (60 * 60);
	   if (elapsedHours > 0) {
	       intElapsed -= elapsedHours * 60 * 60;
	   }
	   int elapsedMinutes = intElapsed / 60;
	   int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
	                           - elapsedMinutes * 60;

	   if (duration.greaterThan(Duration.ZERO)) {
	      int intDuration = (int)Math.floor(duration.toSeconds());
	      int durationHours = intDuration / (60 * 60);
	      if (durationHours > 0) {
	         intDuration -= durationHours * 60 * 60;
	      }
	      int durationMinutes = intDuration / 60;
	      int durationSeconds = intDuration - durationHours * 60 * 60 -
	          durationMinutes * 60;
	      if (durationHours > 0) {
	    	  setPlayTimeLabel(String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds));
	    	  setDurationLabel(String.format("%d:%02d:%02d", durationHours, durationMinutes, durationSeconds));
	         /*return String.format("%d:%02d:%02d/%d:%02d:%02d",
	            elapsedHours, elapsedMinutes, elapsedSeconds,
	            durationHours, durationMinutes, durationSeconds);*/
	      } else {
	    	  setPlayTimeLabel(String.format("%02d:%02d", elapsedMinutes, elapsedSeconds));
	    	  setDurationLabel(String.format("%02d:%02d", durationMinutes, durationSeconds));
	          /*return String.format("%02d:%02d/%02d:%02d",
	            elapsedMinutes, elapsedSeconds,durationMinutes,
	                durationSeconds);*/
	      }
	      } else {
	          if (elapsedHours > 0) {
	        	  setPlayTimeLabel(String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds));
	             /*return String.format("%d:%02d:%02d", elapsedHours,
	                    elapsedMinutes, elapsedSeconds);*/
	            } else {
	            	setPlayTimeLabel(String.format("%02d:%02d", elapsedMinutes, elapsedSeconds));
	                /*return String.format("%02d:%02d",elapsedMinutes,
	                    elapsedSeconds);*/
	            }
	        }
	    }

    public String getTitoloLabel() {
		return textTitoloProperty().get();
	}

	public void setTitoloLabel(String text) {
		textTitoloProperty().set(text);
	}

	public StringProperty textTitoloProperty(){
		return titoloLabel.textProperty();
	}

	public String getDurationLabel() {
		return textDurationProperty().get();
	}

	public void setDurationLabel(String text) {
		textDurationProperty().set(text);
	}

	public StringProperty textDurationProperty(){
		return durationTime.textProperty();
	}

	public String getArtistaLabel() {
		return textArtistaProperty().get();
	}

	public void setArtistaLabel(String text) {
		textArtistaProperty().set(text);
	}

	public StringProperty textArtistaProperty(){
		return artistaLabel.textProperty();
	}

	public String getPlayTimeLabel() {
		return textPlayTimeProperty().get();
	}

	public void setPlayTimeLabel(String text) {
		textPlayTimeProperty().set(text);
	}

	public StringProperty textPlayTimeProperty(){
		return playTime.textProperty();
	}

	public TextField getSearchField(){
		return searchField;
	}
}
