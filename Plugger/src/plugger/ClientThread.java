package plugger;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import pluggerserver.Brano;
import plugger.model.Context;

public class ClientThread implements Runnable{

    public Socket clientSocket;
    //public BufferedReader inFromUser;
    public DataOutputStream outToServer;
    public DataInputStream inFromServer;
    //public CharBuffer charBuffer;
    public int idUser;
    public byte[] byteBuffer;

    public OutputStream outputStream;
    public InputStream inputStream;

    public ObjectOutputStream outObjectToServer;
	public ObjectInputStream inObjectFromServer;


    public ClientThread(String hostAddress, int port) throws IOException {
        clientSocket = new Socket(hostAddress, port);
        System.out.println("HOST ADDRESS: "+hostAddress);
        System.out.println("PORT: "+port);
        //charBuffer = CharBuffer.allocate(8*1024);
        initiateConnection();
    }

    public ClientThread(InetAddress hostAddress, int port) throws IOException {
        clientSocket = new Socket(hostAddress, port);
        //charBuffer = CharBuffer.allocate(8*1024);
        System.out.println("HOST ADDRESS: "+hostAddress);
        System.out.println("PORT: "+port);
        initiateConnection();
    }

    public void initiateConnection(){
        //inFromUser = new BufferedReader(new InputStreamReader(System.in));
        try {
        	System.out.println("ISTANZIO GLI STREAMS...");

        	outputStream = clientSocket.getOutputStream();
        	System.out.println("OPS ISTANZIATO: "+outputStream);

        	inputStream = clientSocket.getInputStream();
        	System.out.println("IPS ISTANZIATO: "+inputStream);

			outToServer = new DataOutputStream(outputStream);
			System.out.println("DOS ISTANZIATO: "+outToServer);

			inFromServer = new DataInputStream(inputStream);
			System.out.println("DIS ISTANZIATO: "+inFromServer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public void run(){
    	System.out.println("CLIENT THREAD IMPOSTATO.");
    	Context.getInstance().setClientThread(this);
    }

    public void updateHomepage(){
    	System.out.println("UPDATE HOMEPAGE METHOD ACTIVATED.");

    	//SEND TO THE SERVER OUR OPERATION COMMAND
        String requestOp = "UPDATE_HOMEPAGE";
        writeToServer(requestOp);

        //WAIT FOR THE SERVER TO ASK INFO
        String response = readFromServer();
        System.out.println("SERVER RESPONSE: "+response);

        List<String> splitResponse = Arrays.asList(response.split(","));
        int nBrani = Integer.parseInt(splitResponse.get(1));
        do{

        }while(nBrani>0);
    }

    public void login(String user, String psw){
        System.out.println("LOGIN METHOD ACTIVATED.");

        String username = user;
        String password = psw;

        //SEND TO THE SERVER OUR OPERATION COMMAND
        String requestOp = "LOGIN";
        writeToServer(requestOp);

        //WAIT FOR THE SERVER TO ASK INFO
        String status = readFromServer();
        System.out.println("SERVER RESPONSE: "+status);
        if(status.contentEquals("CASE_LOGIN/SEND_VALUES")){
            //SEND TO THE SERVER THE USER CREDENTIALS
            String credentials = username+","+password;
            writeToServer(credentials);
        }

        //WAIT FOR THE SERVER TO CHECK CREDENTIALS
        String confirmLogin = readFromServer();
        System.out.println("SERVER RESPONSE: "+confirmLogin);
        List<String> splitConfirmLogin = Arrays.asList(confirmLogin.split(","));

        if(splitConfirmLogin.get(0).contentEquals("CASE_LOGIN/VALUES_CONFIRMED")){
        	setIdUser(splitConfirmLogin.get(1));
            Context.getInstance().getLoginViewController().showMainView();
        }else if(splitConfirmLogin.get(0).contentEquals("CASE_LOGIN/VALUES_INVALID")){
            System.out.println("CREDENZIALI ERRATE.");
        }

    }

    public void setIdUser(String id){
    	this.idUser = Integer.parseInt(id);
    	System.out.println("ID USER: "+idUser);
    }

    public int getIdUser(){
    	return this.idUser;
    }

    public void disconnectClient(){
    	System.out.println("CLOSING SOCKET CONNECTION.");
    	try {
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void sendFile(Brano brano){

    	String titolo = brano.getTitolo();
		String artista = brano.getArtista();
		String album = brano.getAlbum();
		String pathFile = brano.getPathFile();

        System.out.println("SEND FILE METHOD ACTIVATED.");

        //SEND TO THE SERVER OUR OPERATION COMMAND
        String requestOp = "UPLOAD";
        writeToServer(requestOp);

        //WAIT FOR THE SERVER TO ASK INFO
        String status = readFromServer();
        System.out.println("SERVER RESPONSE: "+status);

        File file = new File(pathFile);
        byte[] bytes = new byte[(int)file.length()];

        //SEND THE PROPERTIES OF THE FILE
        String fileLength = Long.toString(file.length());
        writeToServer(fileLength);

        String waitingBrano = readFromServer();
        System.out.println("SERVER RESPONSE: "+waitingBrano);

        //SENDING BRANO OBJECT TO SERVER
        if(waitingBrano.contentEquals("CASE_UPLOAD/SEND_BRANO")){
        	try {
            	ByteArrayOutputStream byteArrOutStream = new ByteArrayOutputStream();
                ObjectOutputStream outObjectToServer = new ObjectOutputStream(byteArrOutStream);
    			outObjectToServer.writeObject(brano);
    			outObjectToServer.flush();
    			System.out.println("OBJ_OUT_STR: "+outObjectToServer.toString());
    			System.out.println("SENDING BRANO TO SERVER: "+brano.toString());
    			System.out.println("SENDING DATA: "+byteArrOutStream.toByteArray());
    			outToServer.write(byteArrOutStream.toByteArray());
    			outToServer.flush();
    		} catch (IOException e3) {
    			// TODO Auto-generated catch block
    			e3.printStackTrace();
    		}
        }

        /*String details = fileLength+","+artista+","+album+","+titolo;
        writeToServer(details);*/

        //WAIT FOR THE SERVER
        String response = readFromServer();
        System.out.println("SERVER RESPONSE: "+response);

        //WAIT FOR THE SERVER TO CONFIRM IT'S READY
        String confirm = readFromServer();
        System.out.println("SERVER RESPONSE: "+confirm);

        if(confirm.contentEquals("CASE_UPLOAD/READY")){
            int counter = 0;
            int res = 0;
            try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))){
                do{
                    try {
						res = bis.read(bytes);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                    System.out.println("RES: "+res);
                    if(res>0){
                        try {
                        	System.out.println("SENDING FILE DATA TO SERVER...");
							outToServer.write(bytes,0,res);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        counter += res;
                        System.out.println("COUNT: "+counter);
                    }
                }while(res > 0);
            } catch (FileNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
        }else{
            if(confirm.contentEquals("CASE_UPLOAD/ERROR_DIRECTORIES")){
                System.out.println("ERROR WHILE CREATING DIRECTORIES.");
            }
        }

        //READ LENGTH FILE FROM THE SERVER
        String transferTerminated = readFromServer();
        List<String> splitConfirmTransfer = Arrays.asList(transferTerminated.split(","));
        System.out.println("SERVER RESPONSE: "+splitConfirmTransfer.get(0));
        String serverFileLength = splitConfirmTransfer.get(1);
        System.out.println("FILE LENGHT: "+fileLength);
        System.out.println("FILE LENGTH READ BY THE SERVER: "+serverFileLength);

        if(!fileLength.contentEquals(serverFileLength)){
            System.out.println("SERVER ERROR DURING READING FILE.");
            String notifyError = "TRANSFER_ERROR";
            writeToServer(notifyError);
        }else{
            System.out.println("TRANSFER COMPLETE.");
            String notifySuccess = "TRANSFER_COMPLETE";
            writeToServer(notifySuccess);
        }

        String finalOutcome = readFromServer();
        System.out.println(finalOutcome);
    }

    public String readFromServer(){
    	String serverResponse = null;
    	if(clientSocket.isClosed()==false){
    		//System.out.println("CLIENT IS CLOSED: "+clientSocket.isClosed());
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
            //String serverResponse = bufferToString(charBuffer);
            serverResponse = new String(byteBuffer).trim();
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
    		//.out.println("SENDING TO SERVER: "+str);
    		//System.out.println("LENGHT SENDING: "+str.length());
            try {
				outToServer.write(reqArray);
            	//outToServer.writeBytes(str);
				outToServer.flush();
			} catch (IOException e) {
				disconnectClient();
				e.printStackTrace();
			}
    }

    /*public String bufferToString(CharBuffer buffer) {
        buffer.flip();
        char[] bytes = new char [buffer.remaining()];
        buffer.get(bytes);
        String converted = new String(bytes);
        System.out.println("CONVERTED LENGHT: "+converted.length());
        return converted;
    }*/

}
