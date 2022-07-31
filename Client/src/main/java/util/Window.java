package util;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import com.labs.lab8fx.App;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class Window {
    public static Stage getStage() {
        return stage;
    }

    private static Stage stage;

    public static void setLocale(Locale locale) {
        Window.locale = locale;
    }

    private static Locale locale = new Locale("en", "ZA");

    public static void setStage(Stage stage) {
        Window.stage = stage;
    }

    public static void setScene(Stage st, String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(App.class.getResource(fxml + ".fxml"));
        fxmlLoader.setResources(ResourceBundle.getBundle("com/labs/lab8fx/l10n/l10n", locale));
        Scene sc = new Scene(fxmlLoader.load(), 1300, 700);
        st.setScene(sc);
    }

    public static void setScene(String fxml) throws IOException {
        setScene(stage, fxml);
    }

    public static void setScene(String fxml, String title) throws IOException {
        setScene(fxml);
        stage.setTitle(title);
    }

    public static void showAlert(String title, String msg, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert a = new Alert(type);
            a.setTitle(title);
            a.setContentText(msg);
            a.setHeaderText(null);
            if (type == Alert.AlertType.NONE) {
                a.getDialogPane().getButtonTypes().add(ButtonType.OK);
            }
            a.show();
        });
    }

    public static String prompt(String msg) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter value");
        dialog.setHeaderText(null);
        dialog.setContentText(msg);
        Optional<String> result = dialog.showAndWait();
        return result.isPresent() ? result.get() : null;
    }

    public static void setTitle(String title) {
        stage.setTitle(title);
    }

    public static void setMinDimensions(int width, int height) {
        stage.setMinWidth(width);
        stage.setMinHeight(height);
    }

    public static void show() {
        stage.show();
    }
}
