import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;


public class Client extends Thread{
	int player;
	int score;
	boolean inReplay, p1WishToReplay, p2WishToReplay; 
	boolean roundHasEnded;
	Integer port;
	String ip; 
	Socket socketClient;
	
	ObjectOutputStream out;
	ObjectInputStream in;
	
	private Consumer<Serializable> callback;
	
	Client(Consumer<Serializable> call){
		inReplay = false;
		player = 0;
		roundHasEnded = false;
		score = 0;
		callback = call;
	}
	
	Client(){
		inReplay = false;
		player = 0;
		roundHasEnded = false;
		score = 0;
	}
	
	public void run() {
		try {
			socketClient= new Socket(ip,port);
		    out = new ObjectOutputStream(socketClient.getOutputStream());
		    in = new ObjectInputStream(socketClient.getInputStream());
		    socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {System.out.println("Something wrong with input/outputStream exception");}

		while(true) {
			try {
				MorraInfo temp = (MorraInfo)in.readObject();
				roundHasEnded = false;
				if(temp.hasScoreUpdate) {
					if(temp.playerNumber == 1) {
						score = temp.p1Points;					
				  }
					else if (temp.playerNumber ==2) {
						score = temp.p2Points;
						
					}
					callback.accept(temp.universalMessage );
					temp.hasScoreUpdate = false;
					roundHasEnded = true;
				}
				
				else if(temp.hasUniversalMessage) {
					System.out.println("message: " + temp.universalMessage);
					callback.accept(temp.universalMessage );
					temp.hasUniversalMessage = false;
				}
				
				
				if(temp.playerNumber == 1) {
					player = 1;
				}
				else if(temp.playerNumber == 2) {
					player = 2;
				}
				
			}
			catch(Exception e) {
				System.out.println("Except: Could not readObject from Instream!");
				break;
			}
		}
	
    }
	
	public void setPort(Integer data) {
		port = data;
	}
	
	public Integer getPort() {
		return port;
	}
	
	public String getIP() {
		return ip;
	}
	
	public void createConnection(String data) {
		ip = data;
	}
	public boolean startReplay() {
		if (p1WishToReplay && p2WishToReplay) {
			return true;
		}
		
		return false;
	}
	public boolean validPortNumber() {
		if((port > 65535) || (port < 0)){
			return false;
		}
		
		if(port <  1023)
			return false;
		
		return true;
	}
	
	public void send(MorraInfo data) {
		try {
			if (data.p1WishToReplay )
				p1WishToReplay = true;
			if(data.p2WishToReplay)
				p2WishToReplay = true;
			if(p1WishToReplay && p2WishToReplay) {
				inReplay = true;
			}
			out.writeObject(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
