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
 *  TODO and possble enhancements:
 *       - music as intended from original BASIC data and code.
 *       - player bankrupt actions not tested, and probably don't work right.
 *       - more cloud layers and configurations
 *       - animate sun moving during sales state.
 *       - update cups shown throughout sales day.
 *       - add a basket of lemons next to stand.
 *       - bag of sugar, cutting board, pitcher shown at pre-setup phase.
 *       - Name each character.
 *       - Southpark-style character sitting behind stand. Random kid each run.
 *       - Lemonade price displayed during sales day.
 *       - fix issue where street crew buyout shows on visual too early.
 *       - enhance pre-selling "chance of rain" and post-selling "actual rain" visual
 *       - get save/load working
 *       - popup graph of sales results so far.
 *       - integrate UI into single window, no pop-up dialogs.
 *       - future:  stock ticker at bottom showing price of sugar & lemons + weather forecast.
 *       _ more progression of lemons, sugar and paper(for ad signs).
 *       - individual buffs and penalties (mom gave sugar/lemons)
 *       - better weather system: temp, humidity, rain chance, wind
 *       - freak snowstorm, windstorm(fall leaves in drinks), attacked by flock of miscevious cockatoos/bees
 *       - visual with up to 6 stands in a row.
 *       - resizable UI.
 *       - online play
 *       - menu of items for sale (multiple drinks to make).
 */

/**
 *
 * @author Mark J Koch
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
