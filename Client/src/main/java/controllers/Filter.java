package controllers;

import content.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import util.MovieProp;
import util.Window;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Filter implements Initializable {

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
    private TextField movieOwner;

    @FXML
    private DatePicker movieDate;

    @FXML
    private TextField movieNameInput;

    @FXML
    private TextField movieOCInput;

    @FXML
    private TextField movieID;

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

        btnSave.setOnAction((e) -> {
            MovieProp mp = new MovieProp(
                    movieKey.getText(),
                    movieOwner.getText(),
                    movieNameInput.getText(),
                    movieID.getText().length() == 0 ? -1 : Long.parseLong(movieID.getText()),
                    movieXInput.getText().length() == 0 ? -1 : Float.parseFloat(movieXInput.getText()),
                    movieYInput.getText().length() == 0 ? -1 : Integer.parseInt(movieYInput.getText()),
                    movieOCInput.getText().length() == 0 ? -1 : Integer.parseInt(movieOCInput.getText()),
                    movieDate.getValue(),
                    movieMpaaEnum.getValue(),
                    movieGenreEnum.getValue(),
                    operatorNameInput.getText(),
                    operatorHeightInput.getText().length() == 0 ? -1 : Long.parseLong(operatorHeightInput.getText()),
                    operatorColorEnum.getValue(),
                    operatorNationalityEnum.getValue(),
                    operatorPassportInput.getText()
            );
           Main.setFilter(mp);
            try {
                Window.setScene("main", resources.getString("main.title"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        MovieProp def = Main.getFilter();
        if (def != null) {
            movieKey.setText(def.getKey());
            movieOwner.setText(def.getOwner());
            movieNameInput.setText(def.getName());
            movieID.setText(def.getID() == -1 ? "" : String.valueOf(def.getID()));
            movieXInput.setText(def.getX() == -1 ? "" : String.valueOf(def.getX()));
            movieYInput.setText(def.getY() == -1 ? "" : String.valueOf(def.getY()));
            movieOCInput.setText(def.getOscars() == -1 ? "" : String.valueOf(def.getOscars()));
            movieDate.setValue(def.getDate());
            movieMpaaEnum.setValue(def.getMpaa());
            movieGenreEnum.setValue(def.getGenre());
            operatorNameInput.setText(def.getOperatorName());
            operatorHeightInput.setText(def.getOperatorHeight() == -1 ? "" : String.valueOf(def.getOperatorHeight()));
            operatorColorEnum.setValue(def.getOperatorHColor());
            operatorNationalityEnum.setValue(def.getOperatorNationality());
            operatorPassportInput.setText(def.getOperatorPassport());
        }
    }

    private void goBack() {
        try {
            Window.setScene("main", Main.getResources().getString("main.title"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
