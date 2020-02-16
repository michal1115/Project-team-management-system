package pl.edu.agh.projectTeamManagementSystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainPane.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Project Team Management System");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}
