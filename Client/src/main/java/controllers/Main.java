package controllers;

import content.*;
import javafx.application.Platform;
import javafx.scene.control.*;
import util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class Main implements Initializable {
    private static ResourceBundle resources;
    private static MovieProp filter = new MovieProp("", "", "", -1, -1, -1, -1, null, null, null, "", -1, null, null, "");

    @FXML
    private TableColumn<MovieProp, Long> ID;

    @FXML
    private TableColumn<MovieProp, MpaaRating> Mpaa;

    @FXML
    private TableColumn<MovieProp, Float> X;

    @FXML
    private TableColumn<MovieProp, Integer> Y;

    @FXML
    private Button btnVisualize;

    @FXML
    private TableColumn<MovieProp, LocalDate> date;

    @FXML
    private TableColumn<MovieProp, MovieGenre> genre;

    @FXML
    private TableColumn<MovieProp, String> key;

    @FXML
    private TableColumn<MovieProp, String> name;

    @FXML
    private TableColumn<MovieProp, Color> operatorHColor;

    @FXML
    private TableColumn<MovieProp, Long> operatorHeight;

    @FXML
    private TableColumn<MovieProp, String> operatorName;

    @FXML
    private TableColumn<MovieProp, Country> operatorNationality;

    @FXML
    private TableColumn<MovieProp, String> operatorPassport;

    @FXML
    private TableColumn<MovieProp, Integer> oscars;

    @FXML
    private TableColumn<MovieProp, String> owner;

    @FXML
    private TableView<MovieProp> table;

    @FXML
    private Text textUser;

    public static ObservableList<MovieProp> movies = FXCollections.observableArrayList();
    private static TreeMap<String, Movie> moviesRes = new TreeMap<>();

    public static ResourceBundle getResources() {
        return resources;
    }

    public static MovieProp getFilter() {
        return filter;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getMovies();
        Main.resources = resources;

        textUser.setText(Auth.getLogin());

        btnVisualize.setOnAction((e) -> {
            System.out.println(e.toString());
            try {
                Window.setScene("visualize", resources.getString("visualize.title"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        table.setPlaceholder(new Text(resources.getString("main.table.placeholder")));

        ID.setCellValueFactory((cellData) -> cellData.getValue().IDProperty().asObject());
        Mpaa.setCellValueFactory((cellData) -> cellData.getValue().mpaaProperty());
        X.setCellValueFactory((cellData) -> cellData.getValue().xProperty().asObject());
        Y.setCellValueFactory((cellData) -> cellData.getValue().yProperty().asObject());
        date.setCellValueFactory((cellData) -> cellData.getValue().dateProperty());
        genre.setCellValueFactory((cellData) -> cellData.getValue().genreProperty());
        key.setCellValueFactory((cellData) -> cellData.getValue().keyProperty());
        name.setCellValueFactory((cellData) -> cellData.getValue().nameProperty());
        operatorHColor.setCellValueFactory((cellData) -> cellData.getValue().operatorHColorProperty());
        operatorHeight.setCellValueFactory((cellData) -> cellData.getValue().operatorHeightProperty().asObject());
        operatorName.setCellValueFactory((cellData) -> cellData.getValue().operatorNameProperty());
        operatorNationality.setCellValueFactory((cellData) -> cellData.getValue().operatorNationalityProperty());
        operatorPassport.setCellValueFactory((cellData) -> cellData.getValue().operatorPassportProperty());
        oscars.setCellValueFactory((cellData) -> cellData.getValue().oscarsProperty().asObject());
        owner.setCellValueFactory((cellData) -> cellData.getValue().ownerProperty());

        table.setItems(movies);

        ContextMenu cm = new ContextMenu();

        MenuItem info = new MenuItem(resources.getString("context_menu.info"));
        info.setOnAction((e) -> {
            NetManager.asyncExchange(new Request("info"), (resp) -> {
                if (resp.getCode() == 200) {
                    Window.showAlert(resources.getString("text.result_title"), resp.getMsg(), Alert.AlertType.INFORMATION);
                } else {
                    Window.showAlert(resources.getString("text.error"), "" + resp.getCode(), Alert.AlertType.ERROR);
                }
            });
        });

        MenuItem insert = new MenuItem(resources.getString("context_menu.insert"));
        insert.setOnAction((e) -> {
            try {
                Editor.setContext(null);
                Editor.setType("insert");
                Editor.setGoBack("main");
                Window.setScene("editor", resources.getString("text.insert"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        MenuItem update = new MenuItem(resources.getString("context_menu.update"));
        update.setOnAction((e) -> {
                    if (table.getSelectionModel().getSelectedItem() == null) {
                        Window.showAlert(resources.getString("text.error"), resources.getString("err.need_select_movie"), Alert.AlertType.ERROR);
                        return;
                    }
                    if (!table.getSelectionModel().getSelectedItem().getOwner().equals(Auth.getLogin())) {
                        Window.showAlert(resources.getString("text.error"), resources.getString("err.not_your_movie"), Alert.AlertType.ERROR);
                        return;
                    }
                    try {
                        Editor.setContext(table.getSelectionModel().getSelectedItem());
                        Editor.setType("update");
                        Window.setScene("editor", resources.getString("editor.title"));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
        );

        MenuItem clear = new MenuItem(resources.getString("context_menu.clear"));
        clear.setOnAction((e) -> {
            NetManager.asyncExchange(new Request("clear"), (resp) -> {
                int code = resp.getCode();
                switch (code) {
                    case 200:
                        Window.showAlert(resources.getString("text.result_title"), resources.getString("text.success_clear"), Alert.AlertType.INFORMATION);
                        break;
                    case 404:
                        Window.showAlert(resources.getString("text.result_title"), resources.getString("err.empty_clear"), Alert.AlertType.ERROR);
                        break;
                    default:
                        Window.showAlert(resources.getString("text.error"), "" + code, Alert.AlertType.ERROR);
                        break;
                }
                getMovies();
            });
        });

        MenuItem exit = new MenuItem(resources.getString("context_menu.exit"));
        exit.setOnAction((e) -> {
            Platform.exit();
        });

        MenuItem removeGreater = new MenuItem(resources.getString("context_menu.remove_greater"));
        removeGreater.setOnAction((e) -> {
            MovieProp movie = table.getSelectionModel().getSelectedItem();
            if (movie != null) {
                NetManager.asyncExchange(new Request("remove_greater", Integer.toString(movie.getOscars())), (resp) -> {
                    int code = resp.getCode();
                    switch (code) {
                        case 200:
                            Window.showAlert(resources.getString("text.result_title"), resources.getString("text.remove_success"), Alert.AlertType.INFORMATION);
                            break;
                        //400
                        default:
                            Window.showAlert(resources.getString("text.error"), "" + code, Alert.AlertType.ERROR);
                            break;
                    }
                    getMovies();
                });
            } else {
                Window.showAlert(Main.getResources().getString("text.error"), resources.getString("err.movie_not_selected"), Alert.AlertType.ERROR);
            }
        });

        MenuItem replaceIfGreater = new MenuItem(resources.getString("context_menu.replace_if_greater"));
        replaceIfGreater.setOnAction((e) -> {
            try {
                Editor.setContext(table.getSelectionModel().getSelectedItem());
                Editor.setType("replace_if_greater");
                Window.setScene("editor", resources.getString("text.update"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        MenuItem removeGreaterKey = new MenuItem(resources.getString("context_menu.remove_greater_key"));
        removeGreaterKey.setOnAction((e) -> {
            MovieProp movie = table.getSelectionModel().getSelectedItem();
            if (movie != null) {
                NetManager.asyncExchange(new Request("remove_greater_key", movie.getKey()), (resp) -> {
                    int code = resp.getCode();
                    switch (code) {
                        case 200:
                            Window.showAlert(resources.getString("text.result_title"), resources.getString("text.remove_success"), Alert.AlertType.INFORMATION);
                            break;
                        //400
                        default:
                            Window.showAlert(resources.getString("text.error"), "" + code, Alert.AlertType.ERROR);
                            break;
                    }
                    getMovies();
                });
            } else {
                Window.showAlert(Main.getResources().getString("text.error"), Main.getResources().getString("err.movie_not_selected"), Alert.AlertType.ERROR);
            }
        });

        MenuItem sumOfOscars = new MenuItem(resources.getString("context_menu.sum_of_oscars"));
        sumOfOscars.setOnAction((e) -> {
            NetManager.asyncExchange(new Request("sum_of_oscars_count"), (resp) -> {
                int code = resp.getCode();
                switch (code) {
                    case 200:
                        Window.showAlert(resources.getString("text.result_title"), resources.getString("text.sum_of_oscars_count") + ": " + resp.getMsg(), Alert.AlertType.INFORMATION);
                        break;
                    default:
                        Window.showAlert(resources.getString("text.error"), "" + code, Alert.AlertType.ERROR);
                        break;
                }
            });
        });

        MenuItem filter = new MenuItem(resources.getString("context_menu.filter"));
        filter.setOnAction((e) -> {
            try {
                Window.setScene("filter", resources.getString("context_menu.filter"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        MenuItem remove = new MenuItem(resources.getString("context_menu.remove"));
        remove.setOnAction((e) -> {
            MovieProp movie = table.getSelectionModel().getSelectedItem();
            if (movie == null) {
                Window.showAlert(Main.getResources().getString("text.error"), Main.getResources().getString("err.movie_not_selected"), Alert.AlertType.ERROR);
                return;
            }
            if (!movie.getOwner().equals(Auth.getLogin())) {
                Window.showAlert(Main.getResources().getString("text.error"), Main.getResources().getString("err.not_your_movie"), Alert.AlertType.ERROR);
                return;
            }

            NetManager.asyncExchange(new Request("remove_key", movie.getKey()), (resp) -> {
                int code = resp.getCode();
                switch (code) {
                    case 200:
                        Window.showAlert(resources.getString("text.result_title"), resources.getString("text.remove_success"), Alert.AlertType.INFORMATION);
                        break;
                    default:
                        Window.showAlert(resources.getString("text.error"), "" + code, Alert.AlertType.ERROR);
                        break;
                }
                getMovies();
            });

        });

        cm.getItems().addAll(info, insert, update, clear, exit, removeGreater, replaceIfGreater, removeGreaterKey, sumOfOscars, filter, remove);

        table.setContextMenu(cm);
    }

    public static void getMovies() {
        new Thread(() -> {

            Response resp = NetManager.exchange(new Request("show"));
            if (resp.getMovies() != null) {
                Main.movies.clear();
                resp.getMovies().entrySet().stream().map(e -> {
                                    MovieProp newMP = new MovieProp(
                                            e.getKey(),
                                            e.getValue().getOwner(),
                                            e.getValue().getName(),
                                            e.getValue().getId(),
                                            e.getValue().getCoords().getX(),
                                            e.getValue().getCoords().getY(),
                                            e.getValue().getOscarsCount(),
                                            e.getValue().getDate(),
                                            e.getValue().getMpaa(),
                                            e.getValue().getGenre(),
                                            e.getValue().getOperator().getName(),
                                            e.getValue().getOperator().getHeight(),
                                            e.getValue().getOperator().getHairColor(),
                                            e.getValue().getOperator().getNationality(),
                                            e.getValue().getOperator().getPassportID()
                                    );
                                    return newMP;
                                }
                        )
                        .filter(m -> filter.getKey().length() == 0 || m.getKey().contains(filter.getKey()))
                        .filter(m -> filter.getOwner().length() == 0 || m.getOwner().contains(filter.getOwner()))
                        .filter(m -> filter.getName().length() == 0 || m.getName().contains(filter.getName()))
                        .filter(m -> filter.getID() == -1 || m.getID() == filter.getID())
                        .filter(m -> filter.getOscars() == -1 || m.getOscars() == filter.getOscars())
                        .filter(m -> filter.getDate() == null || m.getDate().equals(filter.getDate()))
                        .filter(m -> filter.getMpaa() == null || ((m.getMpaa() != null) && m.getMpaa().equals(filter.getMpaa())))
                        .filter(m -> filter.getGenre() == null || ((m.getGenre() != null) && m.getGenre().equals(filter.getGenre())))
                        .filter(m -> filter.getOperatorName().length() == 0 || m.getOperatorName().contains(filter.getOperatorName()))
                        .filter(m -> filter.getOperatorHeight() == -1 || m.getOperatorHeight() == filter.getOperatorHeight())
                        .filter(m -> filter.getOperatorHColor() == null || ((m.getOperatorHColor() != null) && m.getOperatorHColor().equals(filter.getOperatorHColor())))
                        .filter(m -> filter.getOperatorNationality() == null || ((m.getOperatorNationality() != null) && m.getOperatorNationality().equals(filter.getOperatorNationality())))
                        .filter(m -> filter.getOperatorPassport().length() == 0 || m.getOperatorPassport().contains(filter.getOperatorPassport()))
                        .forEach(Main.movies::add);
            }

        }).start();
    }

    public static void setFilter(MovieProp filter) {
        Main.filter = filter;
    }
}
