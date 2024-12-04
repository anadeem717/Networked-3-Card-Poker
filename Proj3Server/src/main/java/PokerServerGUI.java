
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PokerServerGUI extends Application {

	private Server server;
	private ListView<String> serverLog;
	private int portNumber;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {

		primaryStage.setTitle("Poker Server");
		primaryStage.show();
	}


}