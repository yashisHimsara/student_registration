package Ijse.Controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LodingController implements Initializable {

    @FXML
    private AnchorPane lodingform;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Timeline timeline = new Timeline();
        KeyFrame keyFrame1 = new KeyFrame(Duration.millis(4000), actionEvent -> {
            System.out.println("Initializing Application....");
            System.out.println("Welcome to Culinary System v1.0.0");
        });

        timeline.getKeyFrames().addAll(keyFrame1);
        timeline.playFromStart();

        timeline.setOnFinished(actionEvent -> {
            try {
                lodingform.getScene().getWindow().hide();
                Parent root = FXMLLoader.load(getClass().getResource("/view/Dashboard.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.centerOnScreen();
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
