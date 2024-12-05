import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class WelcomePageController {

    @FXML
    private TextField serverIpField;

    @FXML
    private TextField portField;

    @FXML
    public void connectToServer() {
        String serverIp = serverIpField.getText().trim();
        String portString = portField.getText().trim();

        // Check if the IP and port are valid
        if (serverIp.isEmpty() || portString.isEmpty()) {
            System.out.println("Please enter both IP and port.");
            return;
        }

        try {
            int port = Integer.parseInt(portString);
            // Implement connection logic here (e.g., creating a socket connection)

            // If connection is successful, switch to the game scene
            switchToGameScene();
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number.");
        } catch (Exception e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }

    private void switchToGameScene() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gameplay.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) serverIpField.getScene().getWindow();
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
}