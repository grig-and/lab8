package controllers;

import exceptions.NetException;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import util.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class Login implements Initializable {

    private static final String OK_AUTH_URL = "https://connect.ok.ru/oauth/authorize?client_id=512001334996&scope=&response_type=token&redirect_uri=https://connect.ok.ru/oauth/blank.html&layout=m";
    private static final String OK_REDIRECT_URL = "https://connect.ok.ru/oauth/blank.html";
    private static final String VK_AUTH_URL = "https://oauth.vk.com/authorize?client_id=8230542&display=page&redirect_uri=blank.html&scope=&response_type=token&v=5.133";
    private static final String VK_REDIRECT_URL = "https://oauth.vk.com/blank.html";

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnOK;

    @FXML
    private Button btnRegister;

    @FXML
    private Button btnVK;

    @FXML
    private TextField fldLogin;

    @FXML
    private PasswordField fldPassword;

    @FXML
    private Hyperlink by;

    @FXML
    private Hyperlink en;

    @FXML
    private Hyperlink gr;

    @FXML
    private Hyperlink ru;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnLogin.setOnAction((e) -> {
            try {
                int code = NetManager.checkPwd(fldLogin.getText(), fldPassword.getText());
                switch (code) {
                    case 200:
                        Window.setScene("main", resourceBundle.getString("main.title"));
                        break;
                    case 403:
                        Window.showAlert(resourceBundle.getString("text.error"), resourceBundle.getString("err.login_failed"), Alert.AlertType.ERROR);
                        break;
                    case 404:
                        Window.showAlert(resourceBundle.getString("text.error"), resourceBundle.getString("err.user_not_found"), Alert.AlertType.ERROR);
                        break;
                    default:
                        Window.showAlert(resourceBundle.getString("text.error"), "" + code, Alert.AlertType.ERROR);
                        break;
                }
            } catch (IOException | NetException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        });
        btnRegister.setOnAction((e) -> {
            try {
                if (NetManager.register(fldLogin.getText(), fldPassword.getText())) {
                    Window.setScene("main", resourceBundle.getString("main.title"));
                } else {
                    Window.showAlert(resourceBundle.getString("text.error"), resourceBundle.getString("err.reg_failed"), Alert.AlertType.ERROR);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        btnOK.setOnAction((e) -> {
            final WebView view = new WebView();
            final WebEngine engine = view.getEngine();
            engine.load(OK_AUTH_URL);
            Window.getStage().setScene(new Scene(view));
            Window.getStage().show();

            engine.locationProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.startsWith(OK_REDIRECT_URL)) {
                    String[] url1 = newValue.split("#");
                    String[] params = url1[1].split("&");
                    String token = params[0].split("=")[1];
                    try {
                        int code = NetManager.checkPwd("ok_", token);
                        switch (code) {
                            case 200:
                                Window.setScene("main", resourceBundle.getString("main.title"));
                                break;
                            case 500:
                                Window.setScene("login", resourceBundle.getString("login.title"));
                                Window.showAlert(resourceBundle.getString("text.error"), resourceBundle.getString("err.ss_login_failed"), Alert.AlertType.ERROR);
                                break;
                            default:
                                Window.showAlert(resourceBundle.getString("text.error"), "" + code, Alert.AlertType.ERROR);
                                break;
                        }
                    } catch (NetException | IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        });


        btnVK.setOnAction((e) -> {
            final WebView view = new WebView();
            final WebEngine engine = view.getEngine();
            engine.load(VK_AUTH_URL);

            Window.getStage().setScene(new Scene(view));
            Window.getStage().show();

            engine.locationProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.startsWith(VK_REDIRECT_URL)) {
                    String[] url12 = newValue.split("#");
                    String[] params = url12[1].split("&");
                    String token = params[0].split("=")[1];
                    try {
                        int code = NetManager.checkPwd("vk_", token);
                        switch (code) {
                            case 200:
                                Window.setScene("main", resourceBundle.getString("main.title"));
                                break;
                            case 500:
                                Window.setScene("login", resourceBundle.getString("login.title"));
                                Window.showAlert(resourceBundle.getString("text.error"), resourceBundle.getString("err.ss_login_failed"), Alert.AlertType.ERROR);
                                break;
                            default:
                                Window.showAlert(resourceBundle.getString("text.error"), "" + code, Alert.AlertType.ERROR);
                                break;
                        }
                    } catch (NetException | IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        });
        by.setOnAction((e) -> {
            Window.setLocale(new Locale("by", "BY"));
            try {
                Window.setScene("login", "Аўтарызацыя");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        en.setOnAction((e) -> {
            Window.setLocale(new Locale("en", "ZA"));
            try {
                Window.setScene("login", "Authorization");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        gr.setOnAction((e) -> {
            Window.setLocale(new Locale("gr", "GR"));
            try {
                Window.setScene("login", "Εξουσιοδότηση");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        ru.setOnAction((e) -> {
            Window.setLocale(new Locale("ru", "RU"));
            try {
                Window.setScene("login", "Авторизация");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

}
