package com.example.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    public ListView<String> clientView;
    @FXML
    public ListView<String> serverView;
    @FXML
    public TextField pathField;

    public Network network;
    private String homeDir;
    private byte[] buf;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            buf = new byte[256];
            homeDir = System.getProperty("user.home");
            clientView.getItems().clear();
            clientView.getItems().addAll(getFiles(homeDir));
            network = new Network(7172);
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private List<String> getFiles(String dir) {
        String[] list = new File(dir).list();
        assert list != null;
        return Arrays.asList(list);
    }

    private void readLoop() {
        try {
            while (true) {
                String command = network.readString();
                System.out.println("command: " + command);
                if (command.equals("#list#")) {
                    Platform.runLater(() -> serverView.getItems().clear());
                    int len = network.readInt();
                    for (int i = 0; i < len; i++) {
                        String file = network.readString();
                        Platform.runLater(() -> serverView.getItems().add(file));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Connection lost");
        }
    }

    public void pushFail(ActionEvent actionEvent) throws IOException {
        network.getOs().writeUTF("#file#");
        String file = clientView.getSelectionModel().getSelectedItem();
        network.getOs().writeUTF(file);
        File toSend = Path.of(homeDir).resolve(file).toFile();
        network.getOs().writeLong(toSend.length());
        try (FileInputStream fis = new FileInputStream(toSend)) {
            while (fis.available() > 0) {
                int read = fis.read(buf);
                network.getOs().write(buf, 0, read);
            }
        }
        network.getOs().flush();
    }

    public void downFail(ActionEvent actionEvent) {

    }

    public void upPath(ActionEvent actionEvent) {

    }
}