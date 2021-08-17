import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;



class MorraTest {
	Client client1;
	Integer Port;
	boolean  P1WantsReplay, P2WantsReplay;
	String IP;
	
	@BeforeEach
	void init() {
		client1 = new Client();
	}
	
	@Test
	void IPChecker() {
		IP = "127.0.0.1";
		
		client1.createConnection(IP);
		
		assertEquals(IP, client1.getIP(), "IP was not properly given to client");	
	}
	
	@Test
	void validPortChecker() {
		Port = 100000;
		client1.setPort(Port);
		
		assertFalse(client1.validPortNumber());
	}
	
	@Test
	void failedToRestart() {
		P1WantsReplay = false;
		P2WantsReplay = true;
		
		client1.p1WishToReplay = P1WantsReplay;
		client1.p2WishToReplay = P2WantsReplay;
		
		assertFalse(client1.startReplay());
	}
	
	@Test
	void SuccessfulRestart() {
		P1WantsReplay = true;
		P2WantsReplay = true;
		
		client1.p1WishToReplay = P1WantsReplay;
		client1.p2WishToReplay = P2WantsReplay;
		
		assertTrue(client1.startReplay());
		
	}
	
	@Test
	void PortChecker() {
		Port = 5556;
		
		client1.setPort(Port);
		
		assertEquals(Port, client1.getPort(), "Port was not properly given to client");	
	}
	
	

}
