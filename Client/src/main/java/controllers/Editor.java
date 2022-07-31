package controllers;

import content.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import util.MovieProp;
import util.NetManager;
import util.Request;
import util.Window;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Editor implements Initializable {
    private static MovieProp context;
    private static String type = "";
    private static String gb = "main";

    public static void setType(String type) {
        Editor.type = type;
    }

    public static MovieProp getContext() {
        return context;
    }

    public static void setContext(MovieProp context) {
        Editor.context = context;
    }

    @FXML
    private Button btnBack;

    @FXML
    private Button btnSave;

    @FXML
    private ChoiceBox<MovieGenre> movieGenreEnum;

    @FXML
    private ChoiceBox<MpaaRating> movieMpaaEnum;

    @FXML
    private TextField movieKey;
    @FXML
    private TextField movieNameInput;

    @FXML
    private TextField movieOCInput;

    @FXML
    private TextField movieXInput;

    @FXML
    private TextField movieYInput;

    @FXML
    private ChoiceBox<Color> operatorColorEnum;

    @FXML
    private TextField operatorHeightInput;

    @FXML
    private TextField operatorNameInput;

    @FXML
    private ChoiceBox<Country> operatorNationalityEnum;

    @FXML
    private TextField operatorPassportInput;

    public static void setGoBack(String gb) {
        Editor.gb = gb;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnBack.setOnAction((e) -> goBack());

        ObservableList mge = FXCollections.observableArrayList(
                null,
                MovieGenre.ADVENTURE,
                MovieGenre.COMEDY,
                MovieGenre.TRAGEDY
        );
        movieGenreEnum.setItems(mge);

        ObservableList mme = FXCollections.observableArrayList(
                null,
                MpaaRating.PG_13,
                MpaaRating.R,
                MpaaRating.NC_17
        );
        movieMpaaEnum.setItems(mme);

        ObservableList oc = FXCollections.observableArrayList(
                null,
                Color.BLUE,
                Color.GREEN,
                Color.YELLOW,
                Color.BROWN,
                Color.ORANGE
        );
        operatorColorEnum.setItems(oc);

        ObservableList on = FXCollections.observableArrayList(
                null,
                Country.GERMANY,
                Country.SPAIN,
                Country.UNITED_KINGDOM
        );
        operatorNationalityEnum.setItems(on);

        if (type.equals("info")) {
            btnSave.setVisible(false);
            movieKey.setEditable(false);
            movieNameInput.setEditable(false);
            movieXInput.setEditable(false);
            movieYInput.setEditable(false);
            movieOCInput.setEditable(false);
            operatorHeightInput.setEditable(false);
            operatorNameInput.setEditable(false);
            operatorPassportInput.setEditable(false);

            movieMpaaEnum.setOnAction((e) -> {
                movieMpaaEnum.setValue(context.getMpaa());
            });
            movieGenreEnum.setOnAction((e) -> {
                movieGenreEnum.setValue(context.getGenre());
            });
            operatorColorEnum.setOnAction((e) -> {
                operatorColorEnum.setValue(context.getOperatorHColor());
            });
            operatorNationalityEnum.setOnAction((e) -> {
                operatorNationalityEnum.setValue(context.getOperatorNationality());
            });

        } else {
            btnSave.setVisible(true);
        }

        btnSave.setOnAction((e) -> {
            Movie movie;
            try {
                movie = new Movie(
                        movieNameInput.getText(),
                        new Coordinates(
                                new Float(movieXInput.getText()),
                                new Integer(movieYInput.getText())
                        ),
                        new Integer(movieOCInput.getText()),
                        movieGenreEnum.getValue(),
                        movieMpaaEnum.getValue(),
                        new Person(
                                operatorNameInput.getText(),
                                new Long(operatorHeightInput.getText()),
                                operatorPassportInput.getText(),
                                operatorColorEnum.getValue(),
                                operatorNationalityEnum.getValue()
                        )
                );
            } catch (NumberFormatException ee) {
                Window.showAlert(Main.getResources().getString("text.error"), Main.getResources().getString("err.input_error") + ee.getMessage(), Alert.AlertType.ERROR);
                return;
            }
            if (type != "insert") movie.setID(context.getID());

            NetManager.asyncExchange(new Request(type, type == "update" ? String.valueOf(context.getID()) : movieKey.getText(), movie), (res) ->
                    {
                        int sc = res.getCode();
                        switch (sc) {
                            case 200:
                                Window.showAlert(Main.getResources().getString("text.result_title"), Main.getResources().getString("text.success_edit"), Alert.AlertType.INFORMATION);
                                break;
                            case 201:
                                Window.showAlert(Main.getResources().getString("text.result_title"), Main.getResources().getString("text.success_insert"), Alert.AlertType.INFORMATION);
                                break;
                            case 403:
                                Window.showAlert(Main.getResources().getString("text.result_title"), Main.getResources().getString("err.not_your_movie"), Alert.AlertType.ERROR);
                                break;
                            case 409:
                                Window.showAlert(Main.getResources().getString("text.result_title"), Main.getResources().getString("err.duplicate_key"), Alert.AlertType.ERROR);
                                break;
                            case 500:
                                Window.showAlert(Main.getResources().getString("text.result_title"), Main.getResources().getString("err.server_error"), Alert.AlertType.ERROR);
                                break;
                            default:
                                Window.showAlert(Main.getResources().getString("text.result_title"), "" + sc, Alert.AlertType.ERROR);
                                break;
                        }
                        Platform.runLater(() -> {
                            Main.getMovies();
                            goBack();
                        });
                    }
            );


        });

        if (context != null) {
            movieGenreEnum.setValue(context.getGenre());
            movieMpaaEnum.setValue(context.getMpaa());
            movieKey.setText(context.getKey());
            movieKey.setDisable(true);
            movieNameInput.setText(context.getName());
            movieOCInput.setText(String.valueOf(context.getOscars()));
            movieXInput.setText(String.valueOf(context.getX()));
            movieYInput.setText(String.valueOf(context.getY()));
            operatorColorEnum.setValue(context.getOperatorHColor());
            operatorHeightInput.setText(String.valueOf(context.getOperatorHeight()));
            operatorNameInput.setText(context.getOperatorName());
            operatorNationalityEnum.setValue(context.getOperatorNationality());
            operatorPassportInput.setText(context.getOperatorPassport());
        }
    }

    private void goBack() {
        switch (gb) {
            case "main":
                try {
                    Window.setScene("main", Main.getResources().getString("main.title"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "visualize":
                try {
                    Window.setScene("visualize", Main.getResources().getString("visualize.title"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
