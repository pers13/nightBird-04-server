

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MyWoodServer {

	private static ObjectInputStream reader;
	private static ObjectOutputStream writer;
	private static final int port = 1234;
	
	public static void main (String[] args) throws IOException, ClassNotFoundException {

		File file = new File("simple forest.txt");
		Point start = new Point(1,1);
		Point finish = new Point(5,5);
		Action action = Action.Ok;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			Socket socket = serverSocket.accept();
			System.out.println("Hello!\n"+System.getProperty("line.separator"));
			FileInputStream stream = new FileInputStream(file);
			MyWoodLoader wood_loader = new MyWoodLoader();
			MyPrintableWood wood = (MyPrintableWood)wood_loader.Load(stream, System.out);
			Destroyter d = new Destroyter();
			d.close(stream);		
			reader = new ObjectInputStream(socket.getInputStream());;
			writer = new ObjectOutputStream(socket.getOutputStream());
			try {
				Chatbox messageToServer = (Chatbox)reader.readObject();
				if (messageToServer.getCommand().equals("create mau5")) {
					wood.createWoodman(messageToServer.getName(), start, finish);
				}
				while((action != Action.WoodmanNotFound) && (action != Action.Finish)) {
					messageToServer = (Chatbox)reader.readObject();
					if (messageToServer.getCommand().equals("move mau5")) {
						action = wood.move(messageToServer.getName(), messageToServer.getDirection());
						//writer = new ObjectOutputStream(socket.getOutputStream());
						Chatbox messageToClient = new Chatbox(action);
						writer.writeObject(messageToClient);
						writer.flush();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				//d.close(System.out);
				if (socket != null) d.close(socket);
				if (serverSocket != null) d.close(serverSocket);
				if (writer != null) d.close(writer);
				if (reader != null) d.close(reader);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Goodbye!\n"+System.getProperty("line.separator"));
	}
}