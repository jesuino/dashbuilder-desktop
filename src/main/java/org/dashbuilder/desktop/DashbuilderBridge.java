package org.dashbuilder.desktop;

import java.util.regex.Matcher;

import javafx.scene.web.WebEngine;

public class DashbuilderBridge {

    WebEngine engine;
    private Runnable readyCallback;

    boolean ready;

    public DashbuilderBridge(WebEngine engine, Runnable readyCallback) {
        this.engine = engine;
        this.readyCallback = readyCallback;
    }

    public void ready() {
        ready = true;
        readyCallback.run();
    }

    public void sendContent(String content) {
        var lines = content.split("\\n");
        StringBuffer sbScript = new StringBuffer();
        sbScript.append("content =");
        
        for (var line: lines) {
            sbScript.append("'");
            sbScript.append(line.replaceAll("'", Matcher.quoteReplacement("\\'")));
            sbScript.append("\\n'");
            sbScript.append("+");            
        }
        sbScript.replace(sbScript.length() - 1, sbScript.length(), ";");
        sbScript.append("window.postMessage(content, null);");
        if (ready) {
            engine.executeScript(sbScript.toString());
        } else {
            throw new IllegalArgumentException("Dashbuilder is not ready to receive content");
        }
    }
}