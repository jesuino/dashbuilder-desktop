package org.dashbuilder.desktop;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Worker.State;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class MainApplication extends Application implements Runnable {

    private static final String DASHBUILDER_VERSION = "0.22.0";
    private static final String INDEX_FILE =
            "/META-INF/resources/webjars/kie-tools__dashbuilder-client/" + DASHBUILDER_VERSION + "/dist/index.html";

    private String INIT_SCRIPT = """
                let content = "";
                window.addEventListener("message", (e) => {
                  if (e.data === "ready") {
                    dbBridge.ready();
                  }
                });
            """;

    private WebEngine engine;
    private DashbuilderBridge dbBridge;
    private SimpleStringProperty spContent;

    @Override
    public void start(Stage s) {
        var webView = new WebView();

        spContent = new SimpleStringProperty();
        var editor = new TextArea();        
        var root =  new SplitPane(editor, webView);
        s.setScene(new Scene(root));
        s.setTitle("Dashbuilder");

        VBox.setVgrow(webView, Priority.ALWAYS);
        s.show();
        
        editor.textProperty().bindBidirectional(spContent);

        setupEngine(webView);
        spContent.addListener((obs, old, _new) -> dbBridge.sendContent(_new));
    }

    private void setupEngine(WebView webView) {
        var index = this.getClass().getResource(INDEX_FILE).toExternalForm();
        engine = webView.getEngine();
        engine.getLoadWorker().stateProperty().addListener((v, old, state) -> {
            if (state == State.SUCCEEDED) {
                var win = (JSObject) engine.executeScript("window");
                dbBridge = new DashbuilderBridge(engine, this);
                win.setMember("dbBridge", dbBridge);
                engine.executeScript(INIT_SCRIPT);
            }
        });
        engine.load(index);
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
            try {
                var resource = this.getClass().getResource("/dashboard.dash.yaml").openStream();
                var content = new String(resource.readAllBytes());
                spContent.set(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
