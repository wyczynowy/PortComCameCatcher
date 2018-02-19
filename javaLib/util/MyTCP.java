package util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MyTCP {
	private static BufferedReader inFromClient;
//	private BufferedReader inFromUser;
	private BufferedReader inFromServer;
	private DataOutputStream outToServer;
	private static Socket clientSocket;
	private static ServerSocket welcomeSocket;
	private static Socket connectionSocket;
	private boolean isConnectedToServer = false;			// Zmienna przechowujaca informacje o stanie polaczenia Hosta do serwera (1 - polaczenie aktywne, 0 - polaczenie nieaktywne)
	private boolean isConnectedSomeClient = false;
	private static String IpAddress;
	private static int port;
	
	private static final long MILLIS_TO_WAIT = 3 * 1000L;	// Timeout proby polaczenia z serwerem
	
	public int createServer(int port) {
		try {
			welcomeSocket = new ServerSocket(port);
		} catch (IOException e) {
			return 1;
		} return 0;
	}
	
	public boolean waitForClient() {
		try {
			connectionSocket = welcomeSocket.accept();
			isConnectedSomeClient = !connectionSocket.isClosed();
			return connectionSocket.isClosed();
		} catch (IOException e) {
			return true;
		}
	}
	
/**********************************************************************/
//
//	 				FUNCTION RETURNS CONNECTION WITH SOME CLIENT STATUS
//					@ return false - connection closed
//							 true - connection opened
//
/**********************************************************************/

		public boolean getConnectionToSomeClientStatus() {
			return isConnectedSomeClient;
		}
		
/**********************************END*********************************/
		
/**********************************************************************/
//
// 				FUNCTION CLOSE CONNECTION WITH CLIENTS
// 				@ return 0 - connection close successful
// 				1 - connection close error
//
/**********************************************************************/

	public int closeConnectionWithClients() {
		try {
			if (isConnectedSomeClient) {
				connectionSocket.close();
				isConnectedSomeClient = !connectionSocket.isClosed();
			}

			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 1;
		}
	}

/********************************** END *********************************/
	
	private static String receiveLineFromClient() {
		try {
//			Socket connectionSocket = welcomeSocket.accept();
			inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			return inFromClient.readLine();
		} catch (IOException e) {
			return "Read Error";
		}
	}
	
	public String receiveOneLineFromClient(int timeout) {
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<String> future = executor.submit(MyTCP::receiveLineFromClient);
		String result = "";
		try {
			result = future.get(timeout, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			result = "received timed out";
			future.cancel(true /* mayInterruptIfRunning */ );
		} catch (InterruptedException e) {
			result = "task interrupted";
		} catch (ExecutionException e) {
			result = "task aborted";
		} catch (Exception e) {
			result = "other error";
		}
		executor.shutdownNow();
		return result;
	}
	
	private static Integer cts() throws UnknownHostException, IOException {	
		clientSocket = new Socket(IpAddress, port);
		return 0;
	}

/**********************************************************************/
//
//	 				FUNCTION CONNECTs TO SERVER WITH:
//					@ IpAddress - server address ip
//					@ port - server port
//
/**********************************************************************/	
	
	public int connectToServer(String IpAddress, int port) {
		MyTCP.IpAddress = IpAddress;
		MyTCP.port = port;

		try {
			final ExecutorService executor = Executors.newSingleThreadExecutor();
			final Future<Integer> future = executor.submit(MyTCP::cts);
			int result = future.get(MILLIS_TO_WAIT, TimeUnit.MILLISECONDS);
//			clientSocket = new Socket(IpAddress, port);
			isConnectedToServer = clientSocket.isConnected();
			// inFromUser = new BufferedReader(new
			// InputStreamReader(System.in));
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			return result;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return 1;
		} catch (IOException e) {
			e.printStackTrace();
			return 2;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return 3;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return 4;
		} catch (TimeoutException e) {
			e.printStackTrace();
			return 5;
		}
	}
	
/**********************************END*********************************/
	
/**********************************************************************/
//
// 				FUNCTION RETURNS CONNECTION TO SERVER STATUS
//				@ return false - connection closed
//						 true - connection opened
//
/**********************************************************************/

	public boolean getConnectionToServerStatus() {
		return isConnectedToServer;
	}
	
/**********************************END*********************************/
	
/**********************************************************************/
//
//					FUNCTION CLOSE CONNECTION WITH SERVER
//					@ return 0 - connection close successful
//							 1 - connection close error
//	
/**********************************************************************/
	
	public int closeConnectionWithServer() {
		try {
			if (isConnectedToServer) {
				clientSocket.close();
				isConnectedToServer = !clientSocket.isClosed();
			}

			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 1;
		}
	}
	
/**********************************END*********************************/
	
/**********************************************************************/
//
//					FUNCTION CLOSE CONNECTION WITH HOSTS
//					@ return 0 - connection close successful
//							 1 - connection close error
//	
/**********************************************************************/

	public int closeConnectionWithHost() {
		try {
			connectionSocket.close();
			return 0;
		} catch (IOException e) {
			// e.printStackTrace();
			return 1;
		}
	}

/**********************************END*********************************/
	
	public int sendToServer(char oneByte) {
		try {
			outToServer.writeByte(oneByte);
		} catch (IOException e) {
			return 1;
		} return 0;
	}
	
	public int sendToServer(String string) {
		try {
			outToServer.writeBytes(string);
		} catch (IOException e) {
			return 1;
		} catch(NullPointerException e2) {
			return 2;
		} return 0;
	}
	
	public int sendToServer(int value) {

		try {
			outToServer.writeByte(value);
			return 0;
		} catch (IOException e) {
			return 1;
		}

	}
	
	public int receiveFromServer() {
		try {
			return inFromServer.read();
		} catch (IOException e) {
			return 1;
		}
	}
		
}
