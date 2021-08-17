import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;



class MorraTest {
	Server server1;
	Integer P1Move, P1Guess, P2Move, P2Guess;
	
	@BeforeEach
	void init() {
		server1 = new Server();
	}
	
	@Test
	void ZeroGameTest() {
		P1Move = 0;
		P1Guess = 0;
		
		P2Move = 0;
		P2Guess = 0;
		
		int result = server1.playGame(P1Guess, P1Move, P2Guess, P2Move);
		
		assertEquals(0, result, "Should have been tie in zero game");
	}
	
	@Test
	void P1WinGameTest() {
		P1Move = 3;
		P1Guess = 5;
		
		P2Move = 2;
		P2Guess = 2;
		
		int result = server1.playGame(P1Guess, P1Move, P2Guess, P2Move);
		
		assertEquals(1, result, "P1 should have won simple game");
	}
	
	@Test
	void P2WinGameTest() {
		P1Move = 0;
		P1Guess = 3;
		
		P2Move = 2;
		P2Guess = 2;
		
		int result = server1.playGame(P1Guess, P1Move, P2Guess, P2Move);
		
		assertEquals(2, result, "P2 should have won simple game");
	}
	
	@Test
	void P1P2MaxMoveTieGameTest() {
		P1Move = 5;
		P1Guess = 5;
		
		P2Move = 5;
		P2Guess = 5;
		
		int result = server1.playGame(P1Guess, P1Move, P2Guess, P2Move);
		
		assertEquals(0, result, "Should have been tie in max game");
	}
	
	
	@Test
	void P1P2MaxMoveP1WinTest() {
		P1Move = 5;
		P1Guess = 10;
		
		P2Move = 5;
		P2Guess = 3;
		
		int result = server1.playGame(P1Guess, P1Move, P2Guess, P2Move);
		
		assertEquals(1, result, "P1 should have won in MaxMoveGame");
	}
	
	@Test
	void P1P2MaxMoveP2WinTest() {
		P1Move = 5;
		P1Guess = 3;
		
		P2Move = 5;
		P2Guess = 10;
		
		int result = server1.playGame(P1Guess, P1Move, P2Guess, P2Move);
		
		assertEquals(2, result, "P2 should have won in MaxMoveGame");
	}
	
	@Test
	void BothCorrectGuessesTie() {
		P1Move = 2;
		P1Guess = 5;
		
		P2Move = 3;
		P2Guess = 5;
		
		int result = server1.playGame(P1Guess, P1Move, P2Guess, P2Move);
		
		assertEquals(0, result, "Should have been tie in both correct guess game");
	}
	
	@Test
	void InvalidFingerNumber() {
		P1Move = 6;
		P1Guess = 4;
		
		P2Move = 2;
		P2Guess = 4;
		
		int result = server1.playGame(P1Guess, P1Move, P2Guess, P2Move);
		
		assertEquals(-1, result, "Should have been -1 in invalid game");
	}

}
