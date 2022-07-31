package controllers;

import content.Coordinates;
import exceptions.NetException;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import util.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Visualize implements Initializable {
    private static MovieProp selectedMovie;
    private static HashMap<String, Group> groups = new HashMap<>();
    private static boolean isPlaying = false;
    private static boolean isRecording = false;
    private static LinkedList<HistoryValue> history = new LinkedList<>();
    private static HashMap<String, Translate> translates = new HashMap<>();
    private static HashMap<String, Coordinates> recPoint = new HashMap<>();

    @FXML
    private Button btnBack;

    @FXML
    private Pane pane;

    @FXML
    private Button play_btn;

    @FXML
    private Button rec_btn;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getHistory();
        history.add(new HistoryValue("", new Coordinates(0, 0), Duration.ZERO));
        render();

        Main.movies.addListener((ListChangeListener<MovieProp>) c -> {
            Platform.runLater(() -> render());
        });

        btnBack.setOnAction((e) -> {
            try {
                Window.setScene("main", resources.getString("main.title"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        play_btn.setOnAction((e) -> {
            System.out.println(recPoint);
            if (!isPlaying) {
                play_btn.setText("⏸");
                isPlaying = true;
                rec_btn.setText("⏺");
                isRecording = false;

                Timeline timeline = new Timeline();
                translates.clear();
                try {
                    recPoint.entrySet().stream().forEachOrdered(r -> {
                        System.out.println(Main.movies.stream().filter(m -> m.getKey().equals(r.getKey())).findFirst().get().getX());
                        groups.get(r.getKey()).getTransforms().add(new Translate(
                                r.getValue().getX() - Main.movies.stream().filter(m -> m.getKey().equals(r.getKey())).findFirst().get().getX(),
                                r.getValue().getY() - Main.movies.stream().filter(m -> m.getKey().equals(r.getKey())).findFirst().get().getY())
                        );
                    });

                    history.stream().forEachOrdered(h -> {
                        Translate translate = new Translate();
                        if (h.getKey().length() == 0) return;
                        groups.get(h.getKey()).getTransforms().add(translate);
                        timeline.getKeyFrames().add(new KeyFrame(h.getDuration(),
                                new KeyValue(
                                        translate.xProperty(),
                                        0),
                                new KeyValue(
                                        translate.yProperty(),
                                        0
                                )));
                        timeline.getKeyFrames().add(new KeyFrame(h.getDuration().add(Duration.millis(500)),
                                new KeyValue(
                                        translate.xProperty(),
                                        h.getValue().getX() / 2),
                                new KeyValue(
                                        translate.yProperty(),
                                        h.getValue().getY() / 2
                                )));

                        groups.get(h.getKey()).getTransforms().add(translate);
                    });
                } catch (NoSuchElementException | NullPointerException ex) {
                    //for filtered
                }
                timeline.setOnFinished(e1 -> {
                    play_btn.setText("▶");
                    isPlaying = false;
                });
                timeline.play();

            } else {
                play_btn.setText("▶");
                isPlaying = false;
            }
        });

        rec_btn.setOnAction((e) -> {
            if (!isRecording) {
                history.clear();
                NetManager.asyncExchange(new Request("clear_history"));
                history.add(new HistoryValue("", new Coordinates(0, 0), Duration.ZERO));
                recPoint.clear();

                rec_btn.setText("◼");
                isRecording = true;
                play_btn.setText("▶");
                isPlaying = false;
            } else {
                rec_btn.setText("⏺");
                isRecording = false;
            }
        });
    }

    private void getHistory() {
        new Thread(() -> {
            Response h = NetManager.exchange(new Request("get_history"));
            if (h.getHistory() != null) {
                history = h.getHistory();
            }
            Response r = NetManager.exchange(new Request("get_rec_points"));
            if (h.getHistory() != null) {
                r.getHistory().forEach(h1 -> {
                    recPoint.put(h1.getKey(), h1.getValue());
                });
            }

        }).start();

    }

    private void render() {
        pane.getChildren().clear();
        groups.clear();
        try {
            Main.movies.stream().forEach((e) -> {
                add(e);
            });
        } catch (ConcurrentModificationException ex) {
        }
    }

    private void add(MovieProp e) {
        long size = e.getOscars();
        Group yt = new Group();
        Rectangle rect = new Rectangle(e.getX(), e.getY(), size, size / 1.7);
        rect.setArcHeight(size / 4);
        rect.setArcWidth(size / 4);
        rect.setFill(Color.hsb(e.getOwner().hashCode(), 1, 1));
        yt.getChildren().add(rect);
        long tSize = size / 5;
        Polygon tr = new Polygon(
                e.getX() + size / 2 + tSize, e.getY() + size / 1.7 / 2,
                e.getX() + size / 2 - tSize / 2, e.getY() + size / 1.7 / 2 + tSize / 2 * Math.sqrt(3),
                e.getX() + size / 2 - tSize / 2, e.getY() + size / 1.7 / 2 - tSize / 2 * Math.sqrt(3)
        );

        tr.setFill(Color.WHITE);
        yt.getChildren().add(tr);

        Rotate rotation = new Rotate();
        rotation.setAxis(Rotate.Z_AXIS);
        rotation.setPivotX(e.getX() + size / 2);
        rotation.setPivotY(e.getY() + size / 1.7 / 2);
        Timeline rotate = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(rotation.angleProperty(), 0)),
                new KeyFrame(Duration.seconds(1), new KeyValue(rotation.angleProperty(), 360))
        );
        tr.getTransforms().add(rotation);
        rotate.play();

        yt.setOnMouseClicked((e1) -> {
            if (e1.getButton() == MouseButton.SECONDARY) {
                if (!e.getOwner().equals(Auth.getLogin())) {
                    Window.showAlert(Main.getResources().getString("text.error"), Main.getResources().getString("err.move_others_movie"), Alert.AlertType.ERROR);

                    return;
                }
                Coordinates c = new Coordinates((float) (Math.random() * 200 - 100), (int) (Math.random() * 200 - 100));

                if (isRecording) {
                    if (!recPoint.containsKey(e.getKey())) {
                        recPoint.put(e.getKey(), new Coordinates(e.getX(), e.getY()));
                        NetManager.asyncExchange(new Request("add_rec_point", e.getKey() + ";" + e.getX() + ";" + e.getY()));
                    }
                    history.add(new HistoryValue(e.getKey(), c, history.getLast().getDuration().add(Duration.millis(500))));
                    NetManager.asyncExchange(new Request("add_history", e.getKey() + ";" + c.getX() + ";" + c.getY()));
                }

                Translate translate = new Translate();
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(translate.xProperty(), 0), new KeyValue(translate.yProperty(), 0)),
                        new KeyFrame(Duration.seconds(1), new KeyValue(translate.xProperty(), c.getX()), new KeyValue(translate.yProperty(), c.getY()))
                );
                yt.getTransforms().add(translate);
                timeline.play();

                timeline.setOnFinished(e2 -> {
                    NetManager.asyncExchange(new Request("move", e.getKey() + ";" + (c.getX() + e.getX()) + ";" + (c.getY() + e.getY())));
                    Main.movies.stream().forEach((m) -> {
                        if (m.getKey().equals(e.getKey())) {
                            m.setX(c.getX() + e.getX());
                            m.setY(c.getY() + e.getY());
                        }
                    });
                });

            } else {
                Editor.setContext(e);
                Editor.setType("update");
                Editor.setGoBack("visualize");
                try {
                    Window.setScene("editor", Main.getResources().getString("editor.title"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        pane.getChildren().add(yt);
        groups.put(e.getKey(), yt);
    }
}
