package plugger;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import javafx.collections.ObservableList;
import pluggerserver.Brano;
import plugger.model.Context;

public class ClientThread implements Runnable{

    private Socket clientSocket;
    private DataOutputStream outToServer;
    private DataInputStream inFromServer;
    private int idUser;
    private String username;
    private byte[] byteBuffer;
    private String status;

    private static final String CONNECTED = "connected";
    private static final String DISCONNECTED = "disconnected";

    private OutputStream outputStream;
    private InputStream inputStream;

    private Path staticPathPluggerMusic;
    private Path tempPathPluggerMusic;

    public ClientThread(String hostAddress, int port){
    	System.out.println("CLIENT THREAD WITH STRING.");
    	Context.getInstance().setClientThread(this);
        Socket clientSocket = null;
		try {
			clientSocket = new Socket(hostAddress, port);
		} catch (UnknownHostException e) {
			setStatusSocket(DISCONNECTED);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			setStatusSocket(DISCONNECTED);
		}
		if(clientSocket!=null && clientSocket.isConnected()){
			setClientSocket(clientSocket);
	        setStatusSocket(CONNECTED);
	        System.out.println("HOST ADDRESS: "+hostAddress);
	        System.out.println("PORT: "+port);
	        initiateConnection();
		}else{
			System.out.println("SOCKET CHIUSA ::: IMPOSSIBILE COLLEGARSI AL SERVER");
		}
    }

    public ClientThread(InetAddress hostAddress, int port){
    	System.out.println("CLIENT THREAD WITH INET ADDRESS.");
    	Context.getInstance().setClientThread(this);
        Socket clientSocket = null;
		try {
			clientSocket = new Socket(hostAddress, port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			setStatusSocket(DISCONNECTED);
		}
		if(clientSocket!=null && clientSocket.isConnected()){
			setClientSocket(clientSocket);
	        setStatusSocket(CONNECTED);
	        System.out.println("HOST ADDRESS: "+hostAddress);
	        System.out.println("PORT: "+port);
	        initiateConnection();
		}else{
			System.out.println("SOCKET CHIUSA ::: IMPOSSIBILE COLLEGARSI AL SERVER");
		}
    }

    public void setClientSocket(Socket clientSocket){
    	this.clientSocket = clientSocket;
    }

    public Socket getClientSocket(){
    	return this.clientSocket;
    }

    public void setStatusSocket(String status){
    	this.status = status;
    }

    public String getStatusSocket(){
    	return this.status;
    }

    public void initiateConnection(){
        //inFromUser = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("ISTANZIO GLI STREAMS...");

            outputStream = getClientSocket().getOutputStream();
            //System.out.println("OPS ISTANZIATO: "+outputStream);

            inputStream = getClientSocket().getInputStream();
            //System.out.println("IPS ISTANZIATO: "+inputStream);

            outToServer = new DataOutputStream(outputStream);
            //System.out.println("DOS ISTANZIATO: "+outToServer);

            inFromServer = new DataInputStream(inputStream);
            //System.out.println("DIS ISTANZIATO: "+inFromServer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        System.out.println("CLIENT THREAD IMPOSTATO.");

        String userPath = System.getProperty("user.home") + File.separator + "Music";
        System.out.println("USER PATH: "+userPath.toString());

        userPath += File.separator + "LocalMusicaPlugger";
        System.out.println("USER FINAL PATH: "+userPath.toString());

        staticPathPluggerMusic = createUserDirectory(userPath);

        String tempUserPath = userPath += File.separator + "_tmp";
        System.out.println("TEMP USER PATH: "+userPath.toString());

        tempPathPluggerMusic = createUserDirectory(tempUserPath);

        if(staticPathPluggerMusic==null||tempPathPluggerMusic==null){
        	System.out.println("ERROR CREATING DIRECTORIES");
        	System.out.println("ENDING CLIENT.");
        	disconnectClient();
        }

        try {
			Files.setAttribute(tempPathPluggerMusic, "dos:hidden", true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public Path createUserDirectory(String path){
    	File dirToBeCreated = new File(path);
        System.out.println("FINAL USER PATH: "+dirToBeCreated.toString());

        if (dirToBeCreated.exists()) {
            System.out.println(dirToBeCreated + " already exists");
            return dirToBeCreated.toPath();
        } else if (dirToBeCreated.mkdirs()) {
            System.out.println(dirToBeCreated + " was created");
            return dirToBeCreated.toPath();
        } else {
            System.out.println(dirToBeCreated + " was not created");
            return null;
        }
    }

    public boolean containsIdBrano(final ObservableList<Brano> list, final int idBrano){
    	return list.stream().anyMatch(brano -> brano.getIdBrano()==idBrano);
    }

    public Path createTempFile(Path parent, String children, String type){
		Path newPath = null;
		try {
			newPath = Files.createTempFile(parent, children, type);
			System.out.println("TEMP PATH FILE: "+newPath.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newPath;
	}

    public Path createTempDirectory(Path parent, String children){
		System.out.println("DIR PARENT: "+parent.toString());
		System.out.println("DIR CHILD: "+children);
		Path newPath = null;
		try {
			newPath = Files.createTempDirectory(parent, children);
			System.out.println("TEMP PATH DIR: "+newPath.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newPath;
	}

    public void selectBrano(Brano brano){
    	System.out.println("SELECT FILE METHOD ACTIVATED.");

    	if(!Context.getInstance().getMapBraniFileCoverDownloaded().containsKey(brano)){
    		System.out.println("BRANO NON SCARICATO.");

    		if(!getClientSocket().isClosed()){

            	writeToServer("SELECT_FILE");

            	readFromServer();

            	writeToServer(String.valueOf(brano.getIdBrano()));

            	String mp3FileExtension = "."+FilenameUtils.getExtension(brano.getPathFile());
        		String coverFileExtension = "."+FilenameUtils.getExtension(brano.getPathCover());
        		System.out.println("MP3 File Ext: "+mp3FileExtension);
        		System.out.println("Cover File Ext: "+coverFileExtension);

            	//CREATING TEMP DIRS AND FILE
        		Path tempArtistPath = createTempDirectory(tempPathPluggerMusic, brano.getArtista());
        		Path tempAlbumPath = createTempDirectory(tempArtistPath, brano.getAlbum());
        		Path tempMp3File = createTempFile(tempAlbumPath, brano.getTitolo(), mp3FileExtension);
        		Path tempCoverFile = createTempFile(tempAlbumPath, brano.getTitolo(), coverFileExtension);

            	tempMp3File = receiveFile(tempMp3File);

            	tempCoverFile = receiveFile(tempCoverFile);

            	String outcome = readFromServer();

            	if(outcome.contentEquals("CASE_SELECT_BRANO/TRANSFER_COMPLETE")){
            		brano.setPathFile(tempMp3File.toString());
                	brano.setPathCover(tempCoverFile.toString());

                	ArrayList<Path> pathFiles = new ArrayList<Path>();
                	System.out.println("ADDING PATH TO PATH FILES: "+tempMp3File.toString());
                	pathFiles.add(tempMp3File);
                	System.out.println("ADDING PATH TO PATH FILES: "+tempCoverFile.toString());
                	pathFiles.add(tempCoverFile);

                	Context.getInstance().addToMapBraniFileCoverDownloaded(brano, pathFiles);
                	Context.getInstance().getMainViewController().setMediaPlayer(brano);
                	Context.getInstance().getLibraryDetailsViewController().setDetailsBrano(brano);

                	try {
    					Files.setAttribute(tempArtistPath, "dos:hidden", true);
    					Files.setAttribute(tempAlbumPath, "dos:hidden", true);
    					Files.setAttribute(tempMp3File, "dos:hidden", true);
    					Files.setAttribute(tempCoverFile, "dos:hidden", true);
    				} catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}

            	}else if(outcome.contentEquals("CASE_SELECT_BRANO/TRANSFER_ERROR")){
            		System.out.println("ERROR IN TRANSFER.");
            		System.out.println("DELETING FILES.");
            		tempMp3File.toFile().delete();
            		tempCoverFile.toFile().delete();
            	}
        	}else{
        		System.out.println("SOCKET CHIUSA ::: IMPOSSIBILE COLLEGARSI AL SERVER");
        	}

    	}else{
    		System.out.println("BRANO GIA' SCARICATO.");
    		Context.getInstance().getMainViewController().setMediaPlayer(brano);
        	Context.getInstance().getLibraryDetailsViewController().setDetailsBrano(brano);
    	}

    }

	public Path receiveFile(Path tempPathFile){
    	String fileSize = readFromServer();
    	List<String> splitFileSize = Arrays.asList(fileSize.split(","));
        long fileLength= Long.parseLong(splitFileSize.get(1));
        System.out.println("LENGTH FILE: "+fileLength);

        byte[] fileArr = new byte[(int)fileLength];

        FileOutputStream fileOutputStream = null;
        try {
			fileOutputStream = new FileOutputStream(tempPathFile.toFile());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        writeToServer("CLIENT_READY/SEND_FILE");
        int readFromServer = 0;
    	int counter = 0;
        try{
	        do{
	        	readFromServer = inFromServer.read(fileArr);
	        	System.out.println("DATA READ: "+readFromServer);
	        	if(readFromServer>0){
	        		fileOutputStream.write(fileArr, 0, readFromServer);
	        		counter += readFromServer;
	        		System.out.println("COUNTER: "+counter);
		        	System.out.println("FILE LENTGH: "+fileLength);
	        	}
	        	writeToServer(String.valueOf(readFromServer));
	        }while(counter!=fileLength);
	        System.out.println("FILE RICEVUTO CORRETTAMENTE.");
        }catch(IOException e){
        	e.printStackTrace();
        }

        if(counter == tempPathFile.toFile().length()){
        	System.out.println("FILE RECEIVED CORRECTLY.");
        	writeToServer("TRANSFER_COMPLETED_SUCCESSFULLY,"+counter);
        	return tempPathFile;
        }else{
        	writeToServer("ERROR_IN_TRANSFER/WRONG_FILE_SIZE");
        	return null;
        }
    }

    public void updateHomepage(){
        System.out.println("UPDATE HOMEPAGE METHOD ACTIVATED.");

        if(!getClientSocket().isClosed()){
        	//SEND TO THE SERVER OUR OPERATION COMMAND
            String requestOp = "UPDATE_HOMEPAGE";
            writeToServer(requestOp);

            //WAIT FOR THE SERVER TO ASK INFO
            String response = readFromServer();

            List<String> splitResponse = Arrays.asList(response.split(","));
            int nBrani = Integer.parseInt(splitResponse.get(1));
            System.out.println("BRANI TOTALI DA RICEVERE: "+nBrani);
            Context.getInstance().setListaBraniRicevuti();

            int braniRicevuti = 0;
            for(int i = 1; i<=nBrani; i++){
            	String length = readFromServer();
            	writeToServer("LENGTH_BRANO,"+length);
                Brano branoRicevuto = receiveBrano(Integer.parseInt(length));
                if(!containsIdBrano(Context.getInstance().getListaBraniClient(), branoRicevuto.getIdBrano())){
                	Context.getInstance().addBranoToListaBraniRicevuti(branoRicevuto);
                	System.out.println("BRANO RICEVUTO NON PRESENTE NEL CLIENT.");
                }else{
                	System.out.println("BRANO RICEVUTO GIA' PRESENTE NEL CLIENT.");
                }
                System.out.println("BRANO RICEVUTI: "+i);
                System.out.println("BRANI DA RICEVERE: "+ String.valueOf(nBrani-i));
                braniRicevuti = i;
            }

            System.out.println("BRANI RICEVUTI: "+braniRicevuti);
            System.out.println("N DA RICEVERE BRANI: "+nBrani);
            if(braniRicevuti==nBrani){
            	System.out.println("BRANO RICEVUTI = BRANI TOTALI DA RICEVERE.");
                writeToServer("BRANI_RECEIVED,"+braniRicevuti);
            }

            readFromServer();

            Context.getInstance().addBraniRicevutiToListaBrani();
        }else{
        	System.out.println("SOCKET CHIUSA ::: IMPOSSIBILE COLLEGARSI AL SERVER");
        }


    }

    private Brano receiveBrano(int length){
        System.out.println("RECEIVE BRANO METHOD ACTIVE.");
        Brano brano = null;
        byteBuffer = new byte[length];
        int read = 0;
        while(read != length){
            try {
                read = inFromServer.read(byteBuffer);
            } catch (IOException e) {
                disconnectClient();
                e.printStackTrace();
            }
        }
        byte[] containerBrano = Arrays.copyOf(byteBuffer, read);
        ByteArrayInputStream byteArrInStream = new ByteArrayInputStream(containerBrano);
        try {
            ObjectInputStream inObjectFromServer = new ObjectInputStream(byteArrInStream);
            brano = (Brano) inObjectFromServer.readObject();
            System.out.println("BYTES READ: "+read);
            System.out.println("BRANO RICEVUTO: "+brano.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException c) {
            // TODO: handle exception
            c.printStackTrace();
        }

        return brano;
    }

    public void login(String user, String psw){
        System.out.println("LOGIN METHOD ACTIVATED.");

        if(!getClientSocket().isClosed()){

            //SEND TO THE SERVER OUR OPERATION COMMAND
            String requestOp = "LOGIN";
            writeToServer(requestOp);

            //WAIT FOR THE SERVER TO ASK INFO
            String status = readFromServer();
            System.out.println("SERVER RESPONSE: "+status);
            if(status.contentEquals("CASE_LOGIN/SEND_VALUES")){
                //SEND TO THE SERVER THE USER CREDENTIALS
                String credentials = user +","+psw;
                writeToServer(credentials);
            }

            //WAIT FOR THE SERVER TO CHECK CREDENTIALS
            String confirmLogin = readFromServer();
            if(!confirmLogin.contentEquals("CASE_LOGIN/INVALID_VALUES")){
            	System.out.println("SERVER RESPONSE: "+confirmLogin);
            	 List<String> splitConfirmLogin = Arrays.asList(confirmLogin.split(","));
                if(splitConfirmLogin.get(0).contentEquals("CASE_LOGIN/VALUES_CONFIRMED")){
                    setIdUser(Integer.parseInt(splitConfirmLogin.get(1)));
                    setUsername(splitConfirmLogin.get(2));
                    Context.getInstance().getLoginViewController().showMainView();
                }else if(splitConfirmLogin.get(0).contentEquals("CASE_LOGIN/VALUES_INVALID")){
                    System.out.println("CREDENZIALI ERRATE.");
                }
            }else{
            	System.out.println(confirmLogin);
            }
        }else{
        	System.out.println("SOCKET CHIUSA ::: IMPOSSIBILE COLLEGARSI AL SERVER");
        }

    }

    public void logout(){

    	if(!getClientSocket().isClosed()){
    		System.out.println("LOGIN METHOD ACTIVATED.");

        	//SEND TO THE SERVER OUR OPERATION COMMAND
            String requestOp = "LOGOUT";
            writeToServer(requestOp);

            //WAIT FOR THE SERVER TO ASK INFO
            readFromServer();

            setIdUser(0);
           // Context.getInstance().getMainViewController().getMediaPlayer().dispose();
            Context.getInstance().getMainStage().hide();
    	}else{
    		System.out.println("SOCKET CHIUSA ::: IMPOSSIBILE COLLEGARSI AL SERVER");
    	}

    }

    public void setIdUser(int id){
        this.idUser = id;
        System.out.println("ID USER: "+idUser);
    }

    public int getIdUser(){
        return this.idUser;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }

    public void disconnectClient(){
        System.out.println("CLOSING SOCKET CONNECTION.");
        try {
            getClientSocket().close();
            setStatusSocket(DISCONNECTED);
            deleteTempFiles();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void deleteTempFiles(){
    	//Context.getInstance().getMainViewController().getMediaPlayer().dispose();
    	//Context.getInstance().getMainViewController().removeImagePlayer();
    	//Context.getInstance().getLibraryDetailsViewController().getImageDetailsBrano().cancel();

    	for(Brano brano : Context.getInstance().getMapBraniFileCoverDownloaded().keySet()){
        	ArrayList<Path> paths = Context.getInstance().getMapBraniFileCoverDownloaded().get(brano);
        	for(Path path : paths){
        		File fileToDelete = path.toFile();
        		System.out.println("TEMP FILE TO DELETE: "+fileToDelete.toString());

        		File tempAlbumDir = fileToDelete.getParentFile();
        		System.out.println("TEMP ALBUM DIR: "+tempAlbumDir.toString());

        		File tempArtistDir = tempAlbumDir.getParentFile();
        		System.out.println("TEMP ARTIST DIR: "+tempArtistDir.toString());

        		if(fileToDelete.delete()){
        			if(!paths.get(1).toFile().exists()){
        				deleteDirectory(tempArtistDir);
            		}
        		}else{
        			System.out.println("ERRORE ELIMINAZIONE TEMP FILE.");
        		}

        	}
        }
    }

    public void deleteDirectory(File file){

		for (File childFile : file.listFiles()) {

			if (childFile.isDirectory()) {
				deleteDirectory(childFile);
			} else {
				System.out.println("CHILD FILE: "+childFile.getPath().toString());
				/*if (!childFile.delete()) {
					throw new IOException();
				}*/
			}
		}

		if (!file.delete()) {
			System.out.println("ERROR DELETING FILE/DIRECTORY");
			try {
				throw new IOException();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			System.out.println("DELETED DIRECTORY: "+file.toString());
		}
	}

    public void uploadBrano(Brano brano){
        System.out.println("SEND FILE METHOD ACTIVATED.");

        if(!getClientSocket().isClosed()){
        	boolean transferBranoComplete = false;
            boolean transferFileComplete = false;
            boolean transferCoverComplete = false;

            String pathCover = brano.getPathCover();

            //SEND TO THE SERVER OUR OPERATION COMMAND
            String requestOp = "UPLOAD";
            writeToServer(requestOp);

            //WAIT FOR THE SERVER TO ASK INFO
            String requestBrano = readFromServer();
            if(requestBrano.contentEquals("CASE_UPLOAD/SEND_BRANO")){
            	System.out.println("SENDING BRANO.");
            	transferBranoComplete = sendBrano(brano);
            }

            //PREPARING COVER FILE
            File coverFile = new File(pathCover);

            //WAIT FOR THE SERVER
            readFromServer();

            //NOTIFY THE SERVER WE ARE READY TO SEND THE FILE
            writeToServer("CLIENT_READY_TO_SEND_FILE");

            //WAIT FOR THE SERVER TO SEND THE FILE
            String requestFile = readFromServer();
            if(requestFile.contentEquals("CASE_UPLOAD/SEND_FILE")){
                transferFileComplete = sendMp3File(new File(brano.getPathFile()));
            }else{
                if(requestFile.contentEquals("CASE_UPLOAD/ERROR_DIRECTORIES")){
                    System.out.println("ERROR WHILE CREATING DIRECTORIES.");
                }
            }

            String requestCover = readFromServer();

            if(requestCover.contentEquals("CASE_UPLOAD/SEND_COVER")){
                transferCoverComplete = sendCoverFile(coverFile);
            }else{
                if(requestFile.contentEquals("CASE_UPLOAD/ERROR_DIRECTORIES")){
                    System.out.println("ERROR WHILE CREATING DIRECTORIES.");
                }
            }

            System.out.println("BRANO: "+transferBranoComplete+", FILE: "+transferFileComplete+"; COVER: "+transferCoverComplete);
            if(transferBranoComplete && transferFileComplete && transferCoverComplete){
                writeToServer("TRANSFER_COMPLETE");
            }else{
                writeToServer("TRANSFER_ERROR");
            }
            readFromServer();
        }else{
        	System.out.println("SOCKET CHIUSA ::: IMPOSSIBILE COLLEGARSI AL SERVER");
        }

    }

    public boolean sendBrano(Brano brano){
        System.out.println("SEND BRANO METHOD ACTIVE.");
        int branoLength = 0;

        ByteArrayOutputStream byteArrOutStream;
        try {
            byteArrOutStream = new ByteArrayOutputStream();
            ObjectOutputStream outObjectToServer = new ObjectOutputStream(byteArrOutStream);
            outObjectToServer.writeObject(brano);
            outObjectToServer.flush();
            outObjectToServer.close();

            branoLength = byteArrOutStream.size();
            System.out.println("TOTAL DATA SENDING: "+branoLength);
            writeToServer(Integer.toString(branoLength));

            readFromServer();

            System.out.println("SENDING BRANO TO SERVER: "+brano.toString());
            outToServer.write(byteArrOutStream.toByteArray());
            outToServer.flush();
        } catch (IOException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }
        String confirmTransfer = readFromServer();
        List<String> splitConfirmTransfer = Arrays.asList(confirmTransfer.split(","));
        String serverBranoLength = splitConfirmTransfer.get(1);
        System.out.println("BRANO LENGHT: "+branoLength);
        System.out.println("BRANO LENGTH READ BY THE SERVER: "+serverBranoLength);

        if(branoLength==Integer.parseInt(serverBranoLength)){
        	System.out.println("SEND BRANO METHOD OUTCOME: "+true);
        	System.out.println("END OF SENDING BRANO METHOD.");
            return true;
        }else{
        	System.out.println("SEND BRANO METHOD OUTCOME: "+false);
        	System.out.println("END OF SENDING BRANO METHOD.");
        	return false;
        }
    }

    public boolean sendMp3File(File mp3File){
        System.out.println("SEND MP3 FILE METHOD ACTIVE.");
        long mp3FileLength = mp3File.length();
        writeToServer(Long.toString(mp3FileLength));
        byte[] bytes = new byte[(int)mp3File.length()];
        readFromServer();
        int counter = 0;
        int res = 0;
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(mp3File))){
            do{
                try {
                    res = bis.read(bytes);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                System.out.println("RES: "+res);
                System.out.println("SENDING FILE DATA TO SERVER...");
                if(res > 0){
                	outToServer.write(bytes,0,res);
                    outToServer.flush();
                    counter += res;
                    System.out.println("COUNT: "+counter);
                }
            }while(counter!=mp3FileLength);
            System.out.println("LENGTH TOTAL DATA SENT: "+counter);
        } catch (FileNotFoundException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        String confirmTransfer = readFromServer();
        List<String> splitConfirmTransfer = Arrays.asList(confirmTransfer.split(","));
        String serverFileLength = splitConfirmTransfer.get(1);
        System.out.println("FILE LENGHT: "+mp3FileLength);
        System.out.println("FILE LENGTH READ BY THE SERVER: "+serverFileLength);
        System.out.println("END OF SENDING FILE METHOD.");

        if(mp3FileLength==Integer.parseInt(serverFileLength)){
        	System.out.println("SEND FILE METHOD OUTCOME: "+true);
        	System.out.println("END OF SENDING FILE METHOD.");
            return true;
        }else{
        	System.out.println("SEND FILE METHOD OUTCOME: "+false);
        	System.out.println("END OF SENDING FILE METHOD.");
        	return false;
        }
    }

    public boolean sendCoverFile(File coverFile){
        System.out.println("SEND COVER METHOD ACTIVE.");
        String ext = FilenameUtils.getExtension(coverFile.getPath().toString().toLowerCase());
        BufferedImage image = null;
        int coverFileLength = 0;
        try {
            image = ImageIO.read(coverFile);
            ByteArrayOutputStream byteArrOutStream = new ByteArrayOutputStream();
            ImageIO.write(image, ext, byteArrOutStream);

            coverFileLength = byteArrOutStream.size();
            System.out.println("TOTAL DATA SENDING: "+coverFileLength);
            writeToServer(Integer.toString(coverFileLength));

            readFromServer();

            outToServer.write(byteArrOutStream.toByteArray());
            outToServer.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String confirmTransfer = readFromServer();
        List<String> splitConfirmTransfer = Arrays.asList(confirmTransfer.split(","));
        String serverCoverLength = splitConfirmTransfer.get(1);
        System.out.println("COVER LENGHT: "+coverFileLength);
        System.out.println("COVER LENGTH READ BY THE SERVER: "+serverCoverLength);

        if(coverFileLength==Integer.parseInt(serverCoverLength)){
        	System.out.println("SEND COVER METHOD OUTCOME: "+true);
        	System.out.println("END OF SENDING COVER METHOD.");
            return true;
        }else{
        	System.out.println("SEND COVER METHOD OUTCOME: "+false);
        	System.out.println("END OF SENDING COVER METHOD.");
        	return false;
        }
    }

    public String readFromServer(){
        String serverResponse = null;
        if(getStatusSocket().contentEquals(CONNECTED)){
            int read = 0;
            byteBuffer = new byte[8*1024];
            while(read == 0){
                try {
                    read = inFromServer.read(byteBuffer);
                } catch (IOException e) {
                    disconnectClient();
                    e.printStackTrace();
                }
            }
            System.out.println("BYTES READ: "+read);
            serverResponse = new String(byteBuffer).trim();
            System.out.println("SERVER RESPONSE: "+serverResponse);
        }else{
            System.out.println("SOCKET IS CLOSED.");
        }
        return serverResponse;
    }

    public void writeToServer(String str){
            String request = str;
            byte[] reqArray = request.getBytes();
            System.out.println("SENDING TO SERVER: "+new String(reqArray));
            System.out.println("LENGHT SENDING: "+reqArray.length);
            try {
                outToServer.write(reqArray);
                outToServer.flush();
            } catch (IOException e) {
                disconnectClient();
                e.printStackTrace();
            }
    }

}
