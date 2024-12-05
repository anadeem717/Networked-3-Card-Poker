import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private ListView<String> serverLog; // List view to display server logs

    @FXML
    private Label clientCountLabel; // Label to show the number of connected clients

    private Server server; // The Server instance to handle server actions

    // Start the server with the given port number
    public void startServer(int portNumber) {
        try {
            // Create the server instance and set up the callback to update the log
            server = new Server(data -> Platform.runLater(() -> serverLog.getItems().add(data.toString())));

            // Start the server with the given port number
            server.start(portNumber);

            // Log the server start and update the UI
            serverLog.getItems().add("Server started on port: " + portNumber);
            clientCountLabel.setText("Clients Connected: 0");

        } catch (Exception e) {
            // Handle any other errors
            showAlert("Server Error", "An error occurred while starting the server.");
        }
    }

    // Stop the server when the button is clicked
    public void stopServer() {
        if (server != null) {
            server.stopServer();
            Platform.exit(); // Close the application
        }
    }

    // Show an alert with a given title and message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
