/*
 * Lemonade Stand - A port of the Apple II game "Lemonade Stand"
 */
package com.maehem.lemonade;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/*
 *  TODO:
 *       - music as intended from original BASIC data and code.
 *       - 
*/
/**
 *
 * @author mark
 */
public class Lemonade extends Application {

    LemonadeController gameState = new LemonadeController();

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT %1$tL] [%4$-7s] %5$s%n");
    }

    @Override
    public void start(Stage stage) throws Exception {
        StackPane root = new StackPane();
        LemonadeBaseView baseView = new LemonadeBaseView(gameState);
        LemonadeDialogManager dialogManager = new LemonadeDialogManager(gameState);
        
        root.getChildren().add(baseView);
        
        stage.setOnCloseRequest(e -> {
            baseView.doQuit();            
            Platform.exit();
        });
        
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Lemonade Stand");
        stage.setScene(scene);
        stage.show();
        
        stage.setX(stage.getX()-300); // Scoot window left.

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
