import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;

public class Server{
	int count = 1;	
	int port;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;
	
	ClientThread player1, player2;
	boolean areTwoPlayers; 
	boolean hasPlayerOneMove;
	boolean hasPlayerTwoMove;
	boolean player1WantsReplay, player2WantsReplay;
	TheGameOfMorra g;
	Integer p1Move, p1Guess, p1Score, p2Move, p2Guess, p2Score, numberOfClients;

	Server(Consumer<Serializable> call, int portVal){
		player1WantsReplay = false;
		player2WantsReplay = false;
		p1Score = 0;
		p2Score = 0;
		numberOfClients = 0;
		areTwoPlayers = false;
		hasPlayerOneMove = false;
		hasPlayerTwoMove = false;
		
		port = portVal;
		callback = call;
		server = new TheServer();
		server.start();
	}
	
	Server(){
		server = new TheServer();
	}

	public int playGame(int p1Guess, int p1Hand, int p2Guess, int p2Hand) {
		int result = -1;
		
		if( (p1Hand > 5 || p2Hand > 5) ){
			return result;
		}
		
		int targetGuess = p1Hand + p2Hand;
		
		// Player 1 Wins
		if(( p1Guess == targetGuess) && ( p2Guess != targetGuess))
		{
			result = 1;
		}
		
		// Player 2 Wins
		else if(( p2Guess == targetGuess) && ( p1Guess != targetGuess))
		{
			result = 2;
		}
		
		// Tie
		else {
			result = 0;
		}
		
		return result;
	}
	
	public class TheServer extends Thread{
		
		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(port);)
			{
				
		    System.out.println("Server is waiting for a client!");

		     while(true) 
		     {
		
				ClientThread c = new ClientThread(mysocket.accept(), count);
				++numberOfClients;
				callback.accept("client has connected to server: " + "client #" + count);
				clients.add(c);
				c.start();
				if (clients.size() >= 2)
					areTwoPlayers = true;
				
				count++;
				
			 }
		    }//end of try
				catch(Exception e) {
					callback.accept("Server socket did not launch");
				}
			}//end of while
		
		public int getNumberClients() {
			return numberOfClients;
			}
		
		
		}// end TheServer
	

		class ClientThread extends Thread{
			Socket connection;
			int count,a;
			ObjectInputStream in;
			ObjectOutputStream out;
			
			ClientThread(Socket s, int count){				
				this.connection = s;
				this.count = count;		
				a=1;
			}
			
			public void sendClientMessage(String data) {
				for(int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					
					MorraInfo temp = new MorraInfo();
					temp.hasUniversalMessage = true;
					temp.roundHasEnded = false;
					temp.hasScoreUpdate = false;
					temp.universalMessage = data;
					System.out.println("Sending message (" + temp.universalMessage + ") to client " + (i+1));
					try {
					 t.out.writeObject(temp);
					}
					catch(Exception e) {System.out.println("Exception caught from writeObject");}
				}
			}
			
			public void sendClientMessageWithScoreUpdate(String data, int score, int playerNumber) {
				for(int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					
					MorraInfo temp = new MorraInfo();
					temp.hasUniversalMessage = true;				
					temp.hasScoreUpdate = true;
					temp.roundHasEnded = true;
					temp.hasTwoPlayers = true;
					temp.playerNumber = playerNumber;
					if(playerNumber == 1) {
						temp.p1Points = score;
					}
					else if (playerNumber == 2) {
						temp.p2Points = score;
					}
					temp.universalMessage = data;
					System.out.println("Sending message (" + temp.universalMessage + ") and score update: " + score + " to client " + (i+1));
					try {
					 t.out.writeObject(temp);
					}
					catch(Exception e) {System.out.println("Exception from writeObject");}
				}
			}
			
			
			public void restartGame() {
				count = 1;
				
				areTwoPlayers = true; 
				hasPlayerOneMove = false;
				hasPlayerTwoMove = false;
				player1WantsReplay = false;
				player2WantsReplay = false;
				
				p1Score = 0;
				p2Score = 0;
				
			}
			
			public void run(){
					
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);	
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}
				
		
				System.out.println("Count: " + count);
				sendClientMessage("new client on server: client #"+count);
				if(areTwoPlayers) {
					try {
						sendClientMessage("There are two players!");
						player1 = clients.get(0);
						player2 = clients.get(1);
						MorraInfo basic = new MorraInfo();
						basic.hasUniversalMessage = true;
						basic.universalMessage = "Player 1";
						player1.out.writeObject(basic);
						
						basic.universalMessage = "Player 2";
						player2.out.writeObject(basic);
						
					}
					
					catch (Exception e) {
						System.out.println("AreTwoPlayer Exception");
					}
				}
				else {
					sendClientMessage("Waiting for two players...");
					
				}
				 while(true) {
					    try { // GAME
					    	System.out.println("Server ready for input!");
					    	MorraInfo data = (MorraInfo) in.readObject();
					    	
					    	if(data.p1WishToReplay)
					    		player1WantsReplay = true;
					    	if(data.p2WishToReplay)
					    		player2WantsReplay = true;
					    	
					    	if(player1WantsReplay && player2WantsReplay) { // RESET GAME
					    		restartGame();
					    		sendClientMessage("Restarting game...");
					    	}
					    	else if (player1WantsReplay || player2WantsReplay) {
					    		// To skip other branch
					    	}
					    	
					    	else {
						    	callback.accept("Message: " + data.universalMessage);
						    	if(data.playerNumber == 1) {
						    		hasPlayerOneMove = true;
						    		p1Move = data.p1Hand;
						    		p1Guess = data.p1Guess;
						    	}
						    	else if(data.playerNumber == 2) {
						    		hasPlayerTwoMove = true;
						    		p2Move = data.p2Hand;
						    		p2Guess = data.p2Guess;
						    	}
						    	else {
						    		System.out.println("Should not get here.");
						    	}
						    	
						    	if(hasPlayerOneMove && hasPlayerTwoMove) {
						    		hasPlayerOneMove = false;
						    		hasPlayerTwoMove = false;
						    		int goalResult = p1Move + p2Move;
						    		
						    		// P1 WINS
						    		if((p1Guess == goalResult) && (p2Guess != goalResult)) {  			
						    			String P1WinMessage = "Player 1 won playing " + p1Move + " and \nguessing " + p1Guess;
						    			++p1Score;
						    			
						    			if(p1Score >= 2){ // Total Game Win
						    				callback.accept("Player 1 has won the Game!");
						    				sendClientMessage("Player 1 has won the Game!");
						    			}
						    			
						    			else { // Round Win
						    				callback.accept(P1WinMessage);
							    			sendClientMessageWithScoreUpdate(P1WinMessage, p1Score, 1);
						    			}	
						    		}
						    		
						    		// P2 WINS
						    		else if((p2Guess == goalResult) && (p1Guess != goalResult)) {
						    			String P2WinMessage = "Player 2 won playing " + p2Move + " and \nguessing " + p2Guess;
						    			++p2Score;
						    			
						    			if(p2Score >= 2){ // Total Game Win
						    				callback.accept("Player 2 has won the Game!");
						    				sendClientMessage("Player 2 has won the Game!");
						    			}
						    			
						    			else { // Regular Round Win
						    				callback.accept(P2WinMessage);
							    			sendClientMessageWithScoreUpdate(P2WinMessage, p2Score, 2);
						    			}	    			
						    		}
						    		
						    		// TIE
						    		else {					    			
						    			String tieMessage = "Game has ended in a tie! Restarting..";
						    			callback.accept(tieMessage);
						    			sendClientMessageWithScoreUpdate(tieMessage, p1Score, 0);
						    		}
						    	}
					     }
					    	
					    }// end try
					    
					    catch(Exception e) {
					    	callback.accept("OOOOPPs...Something wrong with the socket \nfrom client: " + count + "....closing down!");
					    	sendClientMessage("Client #"+count+" has left the server!");
					    	--numberOfClients;
					    	clients.remove(this);
					    	break;
					    }			    
					}// end of while
				}//end of run
		}//end of client thread
		
}


	
	

	
