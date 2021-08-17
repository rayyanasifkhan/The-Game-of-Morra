import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class TheGameOfMorra extends Application {
	TextField s1,s2,s3,s4, c1;
	TextField portText;
	Text introPortMessage, numberOfClients, p1PointHeader, p1PointShower, p2PointHeader, p2PointShower;
	Text numberOfClientsHeader, numberOfClientsShower, gameReadyStartHeader, gameReadyStartShower;
	Button serverPower,b1;
	EventHandler<ActionEvent> goToServer;
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	VBox serverIntroBox;
	Scene introScene, serverScene;
	Pane startPane, serverPane;
	Server serverConnection;
	String portNumber;
	PauseTransition PauseServer = new PauseTransition(Duration.seconds(1));
	
	ListView<String> listItems;
	
	public TheGameOfMorra sendServerGUI() {
		return this;
	}
	public static void main(String[] args) {
		launch(args);
	}

	public Scene createServerScene() {
		// Create Pane to place things on
		Pane serverPane = new Pane();
		serverPane.setPadding(new Insets(20));
		serverPane.setStyle("-fx-background-color: coral");
		
		// Display state of game and game changes with listView
		listItems.setPrefSize(400, 660);
		listItems.setLayoutX(10);
		listItems.setLayoutY(20);

		int portVal = Integer.parseInt(portText.getText());
		serverConnection = new Server(data -> {
			Platform.runLater(()->{
				listItems.getItems().add(data.toString());
			});

		}, portVal);
		
		Integer x = serverConnection.numberOfClients;
		
		//Other GUI implementations
		// Player Points
		p1PointHeader = new Text("Player 1 Points");
		p1PointHeader.setFill(Color.WHITE);
		p1PointHeader.setFont(Font.font("Arial", FontWeight.BOLD, 25));
		p1PointHeader.setLayoutX(500);
		p1PointHeader.setLayoutY(50);
		
		p1PointShower = new Text("0");
		p1PointShower.setFill(Color.WHITE);
		p1PointShower.setFont(Font.font("Arial", FontWeight.BOLD, 25));
		p1PointShower.setLayoutX(590);
		p1PointShower.setLayoutY(80);
		
		p2PointHeader = new Text("Player 2 Points");
		p2PointHeader.setFill(Color.WHITE);
		p2PointHeader.setFont(Font.font("Arial", FontWeight.BOLD, 25));
		p2PointHeader.setLayoutX(750);
		p2PointHeader.setLayoutY(50);
		
		p2PointShower = new Text("0");
		p2PointShower.setFill(Color.WHITE);
		p2PointShower.setFont(Font.font("Arial", FontWeight.BOLD, 25));
		p2PointShower.setLayoutX(840);
		p2PointShower.setLayoutY(80);
		
		// Game Start Status and Number of Clients
		numberOfClientsHeader = new Text("Number of Clients: ");
		numberOfClientsHeader.setFill(Color.WHITE);
		numberOfClientsHeader.setFont(Font.font("Arial", FontWeight.BOLD, 25));
		numberOfClientsHeader.setLayoutX(500);
		numberOfClientsHeader.setLayoutY(500);
		
		numberOfClientsShower = new Text(x.toString());
		numberOfClientsShower.setFill(Color.WHITE);
		numberOfClientsShower.setFont(Font.font("Arial", FontWeight.BOLD, 25));
		numberOfClientsShower.setLayoutX(730);
		numberOfClientsShower.setLayoutY(500);
		
		gameReadyStartHeader = new Text("Ready to Start?: ");
		gameReadyStartHeader.setFill(Color.WHITE);
		gameReadyStartHeader.setFont(Font.font("Arial", FontWeight.BOLD, 25));
		gameReadyStartHeader.setLayoutX(500);
		gameReadyStartHeader.setLayoutY(540);
		
		gameReadyStartShower = new Text("NO ");
		gameReadyStartShower.setFill(Color.RED);
		gameReadyStartShower.setFont(Font.font("Arial", FontWeight.BOLD, 25));
		gameReadyStartShower.setLayoutX(700);
		gameReadyStartShower.setLayoutY(540);
	

		// Place nodes on pane, return scene with pane
		serverPane.getChildren().addAll(listItems, p1PointHeader, p1PointShower, p2PointHeader, p2PointShower,
										numberOfClientsHeader, numberOfClientsShower,gameReadyStartHeader, gameReadyStartShower);
		serverPane.setStyle("-fx-background-color: #432e59");
		
		
		PauseServer.play();
			
		return new Scene(serverPane, 1000, 700);
		
	}
	
	public Scene createIntroServerScene() {
		serverPower = new Button();
		serverPower.setText("POWER ON");
		serverPower.setPrefSize(200, 100);
		serverPower.setStyle("-fx-font-size: 30 ");
		
		serverPower.setOnAction(goToServer);
		serverPower.setTranslateX(50);
		
		introPortMessage = new Text("Enter Your Port Number Below: ");
		introPortMessage.setStyle("-fx-font-size: 20;-fx-font-weight: bold");
		introPortMessage.setFill(Color.WHITE);
		portText = new TextField("5555");
		
		serverIntroBox = new VBox(10, introPortMessage, portText, serverPower);
		serverIntroBox.setLayoutX(160);
		serverIntroBox.setLayoutY(125);
		
		startPane = new Pane();
		startPane.getChildren().addAll(serverIntroBox);
		
		startPane.setStyle("-fx-background-color: #422e59");
		return new Scene(startPane, 600,400);
		
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Game of Morra Server Side");

		listItems = new ListView<String>();
		goToServer = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent action) {
				primaryStage.setScene(createServerScene());	
			}			
		};
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
	
		primaryStage.setScene(createIntroServerScene());
		primaryStage.show();

		PauseServer.setOnFinished(e ->
		{
			if (serverConnection.numberOfClients == 1) {
				numberOfClientsShower.setText("1");
				gameReadyStartShower.setText("NO");
				gameReadyStartShower.setFill(Color.RED);
			}
			else if (serverConnection.numberOfClients == 2) {
				gameReadyStartShower.setText("YES");
				numberOfClientsShower.setText("2");
				gameReadyStartShower.setFill(Color.GREEN);
			}
			
			else if (serverConnection.numberOfClients == 0) {
				numberOfClientsShower.setText("0");
				gameReadyStartShower.setText("NO");
				gameReadyStartShower.setFill(Color.RED);
			}
			
			p1PointShower.setText(serverConnection.p1Score.toString());
			p2PointShower.setText(serverConnection.p2Score.toString());
			PauseServer.play();	
		});
	
	}
	
	public Scene createServerGui() {	
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: #422e59");
		
		pane.setCenter(listItems);
	
		return new Scene(pane, 500, 400);
			
	}

}
