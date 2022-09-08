package org.dashbuilder.desktop;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DashboardEditor extends VBox {
    
    
    public DashboardEditor(StringProperty spContent) {
        var btnClose = new Button("Close");
        var txtContent = new TextArea();        
        txtContent.textProperty().bindBidirectional(spContent);
        super.getChildren().addAll(btnClose,txtContent);        
        btnClose.setOnAction(e -> this.setVisible(false));
        VBox.setVgrow(txtContent, Priority.ALWAYS);
    }
    

}
