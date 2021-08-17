import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class TheGameOfMorra extends Application {
	Button clientConnectButton, rulesNextButton, sendButton, replayButton, exitButton;
	Text clientPort, clientIP, clientGuessHeader, gameRules, gameRulesHeader, gameOfMoraMessage,
	clientPlayerOneHeader,clientPlayerOneScoreText, clientPlayerTwoHeader, clientPlayerTwoScoreText;
	
	TextField portText, ipText, clientGuessText;
	EventHandler<ActionEvent> goToClient, goToRules, replayGame, exitGame;
	Pane clientIntroPane, clientPane, rulesPane;
	Client clientConnection;
	Image rulesImage, clientImage, fingerZero, fingerOne, fingerTwo, fingerThree, fingerFour, fingerFive, clientImageStart;
	ImageView ivZero, ivOne, ivTwo, ivThree, ivFour, ivFive;
	BackgroundImage rulesImageHolder, clientImageHolder, clientImageStartHolder;
	Background backGround, clientBackGround, clientStartBackGround;
	VBox clientPlayerOneVBox, clientPlayerTwoVBox, clientGuessBox;
	HBox imageBox, ipBox, portBox, clientScoreHBox;
	ListView<String> clientListItems;
	int lastIndexEndPoint, lastRoundEnd, playerNumber, winner, clientPlayerOneScore, clientPlayerTwoScore, hand, guess;
	boolean roundHasEnded, playerOneWantsReplay, playerTwoWantsReplay, inReplay;
	ArrayList<Integer>previousIndex, previousIndexWin, previousIndexRestart, previousIndexAssigned;
	
	PauseTransition PauseT = new PauseTransition(Duration.seconds(3));

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		launch(args);
	}
	

	public Scene createIntroClientScene() {
		clientConnectButton = new Button();
		clientConnectButton.setText("Connect");
		clientConnectButton.setPrefSize(150, 50);
		clientConnectButton.setStyle("-fx-font-size: 30 ");
		clientConnectButton.setLayoutX(240);
		clientConnectButton.setLayoutY(250);
		clientConnectButton.setOnAction(goToRules);

		gameOfMoraMessage = new Text("Welcome to the Game of Morra!");
		gameOfMoraMessage.setStyle("-fx-font-size: 35;-fx-font-weight: bold");
		gameOfMoraMessage.setLayoutX(40);
		gameOfMoraMessage.setLayoutY(80);
				
				
		clientPort = new Text("Enter Your Port Number: ");
		portText = new TextField("5555");
		
		clientIP = new Text("Enter Your IP Number: ");
		ipText = new TextField("127.0.0.1");
		
		ipBox = new HBox(15, clientIP, ipText);
		ipBox.setLayoutX(150);
		ipBox.setLayoutY(150);
		
		portBox = new HBox(clientPort, portText);
		portBox.setLayoutX(150);
		portBox.setLayoutY(200);
	
		clientIntroPane = new Pane();
		clientIntroPane.getChildren().addAll(ipBox, portBox, clientConnectButton,gameOfMoraMessage);
		
		clientImageStart = new Image("backgroundClient1.png");
		clientImageStartHolder = new BackgroundImage(clientImageStart,BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false ,false)); 
		clientStartBackGround = new Background(clientImageStartHolder);
		
		clientIntroPane.setBackground(clientStartBackGround);
		
		return new Scene(clientIntroPane, 600,400);
	}
	
	public Scene createRulesScene() {
		rulesNextButton = new Button();
		rulesNextButton.setText("Next");
		rulesNextButton.setPrefSize(150, 50);
		rulesNextButton.setStyle("-fx-font-size: 30 ");
		rulesNextButton.setLayoutX(340);
		rulesNextButton.setLayoutY(500);
		rulesNextButton.setOnAction(goToClient);
		
		gameRulesHeader = new Text("GAME RULES");
		gameRulesHeader.setStyle("-fx-font-size: 40;-fx-font-weight: bold;");
		gameRulesHeader.setLayoutX(310);
		gameRulesHeader.setLayoutY(50);

		gameRules = new Text(" - Morra is a hand game that dates back thousands of years. \n\n"
				+ " - The rules are simple. At the same time, each player"
				+ " must\n    reveal their hand holding out zero to five fingers.\n\n"
				+ " - At the same time, they must call out their guess about\n   how many fingers "
				+ "total will be revealed by both players.\n\n - If a player guesses correctly, "
				+ "they win a point.\n\n"
				+ " - If both players guess correctly, no points are awarded.\n\n - If no one guesses "
				+ "correctly, no points are awarded.\n\n" 
				+ " - First to 2 points wins!");
		gameRules.setStyle("-fx-font-size: 20;-fx-font-weight: bold;");
		gameRules.setLayoutY(100);
		

		rulesPane = new Pane();
		rulesPane.getChildren().addAll(rulesNextButton, gameRules, gameRulesHeader);
		
		
		rulesImage = new Image("rulesBackground.png");
		rulesImageHolder = new BackgroundImage(rulesImage,BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false ,false)); 
		backGround = new Background(rulesImageHolder);
		rulesPane.setBackground(backGround);
		return new Scene(rulesPane, 800,600);
		
		//primaryStage.show();
		
		
		
	}
	
	public int findPlayerNumber(ObservableList<String> list) {
		for(String v: list) {
			System.out.println("V IS: " + v);
			if (v.equals("Player 1"))
				return 1;
			else if (v.equals("Player 2"))
				return 2;
		}
		
		return 0;
	}
	
	public void blur() {		
		ivZero.setStyle("-fx-opacity: 0.1");
		ivOne.setStyle("-fx-opacity: 0.1");
		ivTwo.setStyle("-fx-opacity: 0.1");
		ivThree.setStyle("-fx-opacity: 0.1");
		ivFour.setStyle("-fx-opacity: 0.1");
		ivFive.setStyle("-fx-opacity: 0.1");
	}
	
	public void unblur() {		
		ivZero.setStyle("-fx-opacity: 1");
		ivOne.setStyle("-fx-opacity: 1");
		ivTwo.setStyle("-fx-opacity: 1");
		ivThree.setStyle("-fx-opacity: 1");
		ivFour.setStyle("-fx-opacity: 1");
		ivFive.setStyle("-fx-opacity: 1");
	}
	
	public Scene createClientScene() {
		    // Create Client Connection 
			clientConnection = new Client(data->{
				Platform.runLater(
						()->{clientListItems.getItems().add(data.toString());
								});
				});
			
			clientListItems.setPrefSize(250, 500);
			
			// Attempting to connect and start from port/ip given
			Integer port = Integer.parseInt(portText.getText());
			clientConnection.setPort(port);
			clientConnection.createConnection(ipText.getText());
			if(!inReplay) {
			clientConnection.start();
		}	
			
		fingerZero = new Image("fist4.png");
		fingerOne = new Image("finger1.png");
		fingerTwo = new Image("finger2.png");
		fingerThree = new Image("finger3.png");
		fingerFour = new Image("finger4.png");
		fingerFive = new Image("finger5.png");												
		
		ivZero = new ImageView(fingerZero);
		ivZero.setFitHeight(100);
		ivZero.setFitWidth(100);
		ivZero.setPreserveRatio(true);
		
		ivOne = new ImageView(fingerOne);
		ivOne.setFitHeight(100);
		ivOne.setFitWidth(100);
		ivOne.setPreserveRatio(true);
		
		ivTwo = new ImageView(fingerTwo);
		ivTwo.setFitHeight(100);
		ivTwo.setFitWidth(100);
		ivTwo.setPreserveRatio(true);
		
		ivThree = new ImageView(fingerThree);
		ivThree.setFitHeight(100);
		ivThree.setFitWidth(100);
		ivThree.setPreserveRatio(true);
		
		ivFour = new ImageView(fingerFour);
		ivFour.setFitHeight(100);
		ivFour.setFitWidth(100);
		ivFour.setPreserveRatio(true);
		
		ivFive = new ImageView(fingerFive);
		ivFive.setFitHeight(100);
		ivFive.setFitWidth(100);
		ivFive.setPreserveRatio(true);
	
		ivZero.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
			hand = 0;
			blur(); 
		    ivZero.setStyle("-fx-opacity: 1");
		});
		
		ivOne.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
		    hand = 1;
		    blur();
		    ivOne.setStyle("-fx-opacity: 1");
		});
		
		ivTwo.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
		    hand = 2;
		    blur();
		    ivTwo.setStyle("-fx-opacity: 1");
		});
		
		ivThree.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
		    hand = 3;
		    blur();
		    ivThree.setStyle("-fx-opacity: 1");
		});
		
		ivFour.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
		    hand = 4;
		    blur();
		    ivFour.setStyle("-fx-opacity: 1");
		});
			
		ivFive.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
		    hand = 5;
		    blur();
		    ivFive.setStyle("-fx-opacity: 1");
		});
			
		imageBox = new HBox(15, ivZero, ivOne, ivTwo, ivThree, ivFour, ivFive);
		imageBox.setLayoutX(240);
		imageBox.setLayoutY(20);
		
		// Create Game GUI 		
		clientGuessHeader = new Text("Enter Your Guess (0-10)");
		clientGuessHeader.setStyle("-fx-font-size: 30;-fx-font-weight: bold");
		clientGuessText = new TextField("0");
		
		clientGuessBox = new VBox(20, clientGuessHeader, clientGuessText);
		clientGuessBox.setLayoutX(390);
		clientGuessBox.setLayoutY(200);
		
		exitButton = new Button("Exit");
		exitButton.setDisable(true);
		exitButton.setPrefSize(150, 50);
		exitButton.setOnAction(exitGame);
		
		replayButton = new Button("Replay");
		replayButton.setDisable(true);
		replayButton.setPrefSize(150, 50);
		replayButton.setOnAction(replayGame);
		
		HBox extraButtons = new HBox(20, replayButton, exitButton);
		extraButtons.setLayoutX(460);
		extraButtons.setLayoutY(630);
		
		// Receive function
		sendButton = new Button("Send");
		sendButton.setPrefSize(150, 50);
		sendButton.setLayoutX(468);
		sendButton.setLayoutY(350);
		
		// Disable button and only enable once server sends Player Assignment
		sendButton.setOnAction(e->{
			if(clientGuessText.getText().trim().isEmpty()) {
				clientListItems.getItems().add("You must put something in the Guess Box!!");
			}
			else if(!gameHasStarted()) {
				clientListItems.getItems().add("You can't play with less than two players!!");
			}
			else {
				sendButton.setDisable(true);
						
				MorraInfo box = new MorraInfo();
				box.playerOneMoveMade = false;
				box.playerTwoMoveMade = false;
				playerNumber = findPlayerNumber(clientListItems.getItems());
				
				box.playerNumber = playerNumber;
				
				if(playerNumber == 1) {
					box.p1Hand = hand;
					box.p1Guess = Integer.parseInt(clientGuessText.getText());
					box.universalMessage = "You have received Player 1s";
				}
				else if(playerNumber == 2) {
					box.p2Hand = hand;
					box.p2Guess = Integer.parseInt(clientGuessText.getText());
					box.universalMessage = "You have received Player 2s";
				}
				else {
					box.universalMessage = "You have received from an unknown player.";
				}
				
				clientConnection.send(box); 
				clientGuessText.clear();
	
				PauseT.play();
			}	
			});
		
		clientPlayerOneScore = 0;
		clientPlayerOneHeader = new Text("Player 1");
		clientPlayerOneHeader.setStyle("-fx-font-size: 30;-fx-font-weight: bold");
		clientPlayerOneScoreText = new Text("0");
		clientPlayerOneScoreText.setTranslateX(45);
		clientPlayerOneScoreText.setStyle("-fx-font-size: 30;-fx-font-weight: bold");
		
		clientPlayerTwoScore = 0;
		clientPlayerTwoHeader = new Text("Player 2");
		clientPlayerTwoHeader.setStyle("-fx-font-size: 30;-fx-font-weight: bold");
		clientPlayerTwoScoreText = new Text("0");
		clientPlayerTwoScoreText.setTranslateX(45);
		clientPlayerTwoScoreText.setStyle("-fx-font-size: 30;-fx-font-weight: bold");
		
		clientPlayerOneVBox = new VBox(20, clientPlayerOneHeader, clientPlayerOneScoreText);
		clientPlayerTwoVBox = new VBox(20, clientPlayerTwoHeader, clientPlayerTwoScoreText);
		
		clientScoreHBox = new HBox(60, clientPlayerOneVBox, clientPlayerTwoVBox);
		clientScoreHBox.setLayoutX(410);
		clientScoreHBox.setLayoutY(460);
		
		clientImage = new Image("clientBackground.png");
		clientImageHolder = new BackgroundImage(clientImage,BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false ,false)); 
		clientBackGround = new Background(clientImageHolder);
		
		clientPane = new Pane(clientListItems, clientGuessBox, sendButton, imageBox, extraButtons,clientScoreHBox);
		clientPane.setBackground(clientBackGround);
		
		return new Scene(clientPane , 800, 700);
	}
	
	public boolean doesntExist(int i ) {
		for(int v: previousIndex) {
			if( i == v) {
				return false;
			}
		}

		return true;
	}
	public boolean doesntExistWin(int i ) {
		for(int v: previousIndexWin) {
			if( i == v) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean doesntExistRestart(int i ) {
		for(int v: previousIndexRestart) {
			if( i == v) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean doesntExistAssigned(int i ) {
		for(int v: previousIndexAssigned) {
			if( i == v) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean gameHasStarted() {
		boolean result = false;
		for (int i=0; i < clientListItems.getItems().size(); i++) {
			String data = clientListItems.getItems().get(i);
			
			if((data.equals("Player 1") || data.equals("Player 2") ) ) {
				result = true;
			}
		}
	
		return result;
	}
	public boolean roundHasEnded() {
		boolean result = false;
		for (int i=0; i < clientListItems.getItems().size(); i++) {
			String data = clientListItems.getItems().get(i);
			
			if((data.contains("Player 1 won playing ") || data.contains("Player 2 won playing ")
					|| data.equals("Game has ended in a tie! Restarting..") ) && (doesntExist(i))) {
				previousIndex.add(i);
				result = true;
				
				if(data.contains("Player 1 won playing ")) {
					clientPlayerOneScore++;
					Integer updatedScore = clientPlayerOneScore;
					clientPlayerOneScoreText.setText(updatedScore.toString());
				}
				
				else if(data.contains("Player 2 won playing ")) {
					clientPlayerTwoScore++;
					Integer updatedScore = clientPlayerTwoScore;
					clientPlayerTwoScoreText.setText(updatedScore.toString());
				}
				
			}
		}
	
		return result;
	}
	
	public boolean gameHasEnded() {
		boolean result = false;
		for (int i=0; i < clientListItems.getItems().size(); i++) {
			String data = clientListItems.getItems().get(i);
			if((data.equals("Player 1 has won the Game!") || data.equals("Player 2 has won the Game!") ) && (doesntExistWin(i))) {
				previousIndexWin.add(i);
				result = true;
				
				if(data.equals("Player 1 has won the Game!")) {
					clientPlayerOneScore++;
					Integer updatedScore = clientPlayerOneScore;
					clientPlayerOneScoreText.setText(updatedScore.toString());
				}
				
				else if(data.equals("Player 2 has won the Game!")) {
					clientPlayerTwoScore++;
					Integer updatedScore = clientPlayerTwoScore;
					clientPlayerTwoScoreText.setText(updatedScore.toString());
				}
			}
			
		}
	
		return result;
	}
	
	public boolean gameNeedsRestart() {
		boolean result = false;
		for (int i=0; i < clientListItems.getItems().size(); i++) {
			String data = clientListItems.getItems().get(i);
			if((data.equals("Restarting game...")) && (doesntExistRestart(i))) {
				previousIndexRestart.add(i);
				result = true;
			}
		}
	
		return result;
	}
	
	public boolean youGotAssigned() {
		// Loop past fist assignments of Player 1 and Player 2
		int j;
		for(j=0; j<clientListItems.getItems().size(); j++) {
			if( (clientListItems.getItems().get(j).equals("Player 2")) || (clientListItems.getItems().get(j).equals("Player 1")) ){
				break;
			}
		}
		
		boolean result = false;
		for (int i=j+1; i < clientListItems.getItems().size(); i++) {
			String data = clientListItems.getItems().get(i);
			if( (data.equals("Player 1") || (data.equals("Player 2") ) && (doesntExistAssigned(i) ) ) ) {
				previousIndexAssigned.add(i);
				result = true;
				break;
			}
		}
	
		return result;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// Start the game
		primaryStage.setTitle("(Server) Let's Play Morra!!!");
		
		// Game State holders
		lastIndexEndPoint = 0;
		lastRoundEnd = 0;
		previousIndex = new ArrayList<Integer>();
		previousIndexWin = new ArrayList<Integer>();
		previousIndexRestart = new ArrayList<Integer>();
		previousIndexAssigned = new ArrayList<Integer>();
		inReplay = false;
			
		// Pauses
		PauseT.setOnFinished(e -> 
		{
			System.out.println("Pause Begin:");
			if(gameNeedsRestart()) {
				inReplay = true;
				clientPlayerOneScore = 0;
				clientPlayerOneScoreText.setText("0");
				
				clientPlayerTwoScore = 0;
				clientPlayerTwoScoreText.setText("0");
				
				sendButton.setDisable(false);
				exitButton.setDisable(true);
				
			}
			else {
				if(gameHasEnded()) {
					clientListItems.getItems().add("Would you like to replay or exit?");
					replayButton.setDisable(false);
					exitButton.setDisable(false);
					unblur();
				}
				else if(roundHasEnded()) {
					unblur();
					sendButton.setDisable(false);
				}
				else if(youGotAssigned()) {
					exitButton.setDisable(true);
					sendButton.setDisable(false);
					
					clientPlayerOneScore = 0;
					clientPlayerOneScoreText.setText("0");
					
					clientPlayerTwoScore = 0;
					clientPlayerTwoScoreText.setText("0");
				}
				
				else {
					System.out.println("Pause End.");
					PauseT.play();
				}
			}
			
		});
		
		// Event Handlers
		goToClient = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent action) {
				primaryStage.setScene(createClientScene());		
			}			
		};
				
		goToRules = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent action) {
				primaryStage.setScene(createRulesScene());
			}			
		}; 
		 
		exitGame = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent action) {
                Platform.exit();
                System.exit(0);
            }
        };
        
        replayGame = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent action) {
                //reset game scores and game
            	replayButton.setDisable(true);
    			
    			MorraInfo box = new MorraInfo();
    			box.p1WishToReplay = false;
    			box.p2WishToReplay = false;
    			box.playerOneMoveMade = false;
    			box.playerTwoMoveMade = false;
    			box.playerNumber = playerNumber;
    			if(playerNumber == 1) {
    				box.p1WishToReplay = true;
    			}
    			else if(playerNumber == 2) {
    				box.p2WishToReplay = true;
    			}
    			else {
    				box.universalMessage = "You have received from an unknown player";
    			}
    			
    			clientConnection.send(box); 
    			
    			PauseT.play();
            }
        };
        
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
		
		clientListItems = new ListView<String>();
		primaryStage.setScene(createIntroClientScene());
		primaryStage.show();
	}

}


