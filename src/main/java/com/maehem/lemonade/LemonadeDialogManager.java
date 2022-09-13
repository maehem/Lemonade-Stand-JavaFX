/*
 * Lemonade View Manager
 * 
 * Manages game flow and presentation of dialogs for game state.
 */
package com.maehem.lemonade;

import java.text.DecimalFormat;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 *
 * @author Mark J. Koch
 */
public class LemonadeDialogManager implements LemonadeStateChangeListener {

    private static final Logger log = Logger.getLogger(LemonadeDialogManager.class.getName());

    LemonadeController game;

    public LemonadeDialogManager(LemonadeController gameState) {
        this.game = gameState;
        gameState.addListener(this);
    }

    @Override
    public void gameStateChanged() {
        switch (game.getState()) {
            case START:
                break;
            case SPLASH:
                log.config("View: Splash State.  Calling splashDialog()");
                splashDialog();
                break;
            case SETUP:
                log.config("View: Setup State.  Calling introDialog()");
                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(event -> introDialog());
                delay.play();
                break;
            case DO_LOAD:
                loadDialog();
                break;
            case NEW_BUSINESS:
                newBusinessDialog();
                break;
            case DAY_START:
                dayStartDialog();  // Presents Player settings entry.
                break;
            case SELLING_START:   // Base View animates day before moving forward.
                // Nothing to do here.  Base window is where the party is.
                break;
            case REPORT:          // Selling day ended. Fesults Ready.  Present report.
                dailyReportDialog();
                break;
        }
    }

    private void splashDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Lemonade Stand");
        
        alert.setHeaderText("Welcome to Lemonade Stand");
        alert.getDialogPane().setPrefWidth(400);
        alert.setContentText(game.getSplashPageText());
        alert.setOnCloseRequest((t) -> {
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    //try {
                    //Thread.sleep(2000);
                    game.setSplashShown();  // tells game to move to next state.
                    //} catch (InterruptedException ex) {
                    //    Logger.getLogger(LemonadeViewManager.class.getName()).log(Level.SEVERE, null, ex);
                    //}
                }
            };
            tt.run();
        });
        
        alert.show();
        alert.setX(alert.getX()+300); // Scoot to the right.
        //amgTrack();

    }


    private void introDialog() {
        Dialog<LemonadeController.Type> dialog = new Dialog<>();
        dialog.setTitle("Set Up Game");

        Image glyphImage = new Image(getClass().getResourceAsStream(
                "/glyphs/houses-on-street.png"
        ));

        ImageView im = new ImageView(glyphImage);
        im.setFitHeight(96);
        im.setPreserveRatio(true);
        dialog.setGraphic(im);

        dialog.setHeaderText(game.getIntroPageHeaderText());
        dialog.setContentText(game.getIntroPageText());
        ButtonType buttonTypeLoad = new ButtonType("Load Saved Game", ButtonData.OK_DONE);
        
        ButtonType buttonTypeNew = new ButtonType("New Game", ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().addAll(/*buttonTypeLoad, */ buttonTypeNew);

        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeLoad) {
                game.setLoadSave();
                
                return LemonadeController.Type.LOAD;
            } else {
                return LemonadeController.Type.NEW;
            }
        });

        dialog.setOnCloseRequest((t) -> {
            log.log(Level.FINER, "Intro dialog close request.");
            new TimerTask() {
                @Override
                public void run() {
                    game.finishSetup();  // tells game to move to next state.
                }
            }.run();
        });
        dialog.show();
        dialog.setX(dialog.getX()+300); // Scoot to the right.
    }

    private void loadDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Game Load");
        Image glyphImage = new Image(getClass().getResourceAsStream(
                "/glyphs/floppy-disk.png"
        ));

        ImageView im = new ImageView(glyphImage);
        im.setFitHeight(64);
        im.setPreserveRatio(true);
        dialog.setGraphic(im);

        dialog.setHeaderText(game.getIntroPageHeaderText());
        Label comingSoon = new Label("Coming Soon");

        GridPane grid = new GridPane();
        grid.add(comingSoon, 1, 1);
        dialog.getDialogPane().setContent(grid);

        //  TODO: Cancel button that allows user to bail and start as new.
        ButtonType buttonTypeLoad = new ButtonType("Load From File", ButtonData.OK_DONE);
        ButtonType buttonTypeNew = new ButtonType("I changed my mind. Start New Game", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeLoad, buttonTypeNew);

        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeLoad) {
                log.config("User selected load file.");
                game.loadData("filePath");
                // start game at progress report.
                return "Path to file";
            } else {
                log.config("User bailed, start a new game.");
                game.clearLoadSave();
                game.finishSetup();
            }
            
            return null;
        });

        dialog.show();
        dialog.setX(dialog.getX()+300); // Scoot to the right.
    }

    private void newBusinessDialog() {
        Dialog dialog = new Dialog();
        //alert.setResizable(true);
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        dialog.getDialogPane().setPrefWidth(600);

        Image glyphImage = new Image(getClass().getResourceAsStream(
                "/glyphs/lemons.png"
        ));

        ImageView im = new ImageView(glyphImage);
        im.setFitHeight(64);
        im.setPreserveRatio(true);
        dialog.setGraphic(im);

        ButtonType buttonTypeOK = new ButtonType("Let's Do This!", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOK);
        dialog.setTitle("Lemonade Stand");
        dialog.setHeaderText(game.getNewBusinessPageHeader());
        VBox content = new VBox();
        Label pageText = new Label(game.getNewBusinessPageText());
        Label nPlayersLabel = new Label("Number of Players: ");
        Spinner nPlayersSpinner = new Spinner(1, 6, 1);

        GridPane grid = new GridPane();
        grid.add(nPlayersLabel, 1, 1);
        grid.add(nPlayersSpinner, 2, 1);

        content.getChildren().addAll(pageText, grid);

        dialog.getDialogPane().setContent(content);
        dialog.setOnCloseRequest((t) -> {
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    game.setNewBusinessComplete((Integer) nPlayersSpinner.getValue());  // tells game to move to next state.
                }
            };
            tt.run();
        });
        
        dialog.show();
        dialog.setX(dialog.getX()+300); // Scoot to the right.
    }

    private void dayStartDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Good Morning!");
        dialog.setHeaderText("Let's get our stand set up...");

        Image glyphImage = new Image(getClass().getResourceAsStream(
                "/glyphs/lemons.png"
        ));

        ImageView im = new ImageView(glyphImage);
        im.setFitHeight(64);
        im.setPreserveRatio(true);
        dialog.setGraphic(im);

        VBox contentArea = new VBox();

        Label currentEvents = new Label(game.getModel().getCurrentEventsMessage());

        GridPane grid = new GridPane();

        contentArea.getChildren().addAll(currentEvents, grid);
        grid.setPrefWidth(600);

        Label pLabel = new Label("Player:");
        pLabel.setAlignment(Pos.BASELINE_RIGHT);
        pLabel.setMinWidth(150);
        grid.add(pLabel, 1, 1);

        Label label1 = new Label("Number of Glasses: ");
        label1.setAlignment(Pos.BASELINE_RIGHT);
        label1.setMinWidth(150);
        Label label2 = new Label("Price per Glass: ");
        label2.setAlignment(Pos.BASELINE_RIGHT);
        label2.setMinWidth(150);
        Label label3 = new Label("Number of Signs: ");
        label3.setAlignment(Pos.BASELINE_RIGHT);
        label3.setMinWidth(150);
        grid.add(label1, 1, 2);
        grid.add(label2, 1, 3);
        grid.add(label3, 1, 4);

        for (int i = 0; i < game.getModel().getNumPlayers(); i++) {
            LemonadePlayer p = game.getModel().getPlayer(i);
            Label l = new Label(Integer.toString(i + 1));
            l.setAlignment(Pos.BASELINE_CENTER);
            l.setMaxWidth(60);
            grid.add(l, i + 2, 1);

            Node t1 = getNumericField(p.getL(), "NG:" + i);
            Node t2 = getNumericField(p.getP(), "PG:" + i);
            Node t3 = getNumericField(p.getS(), "NS:" + i);

            grid.add(t1, i + 2, 2);
            grid.add(t2, i + 2, 3);
            grid.add(t3, i + 2, 4);
        }

        dialog.getDialogPane().setContent(contentArea);

        ButtonType buttonTypeStart = new ButtonType("Start Selling!", ButtonData.OK_DONE);
        ButtonType buttonTypeQuit = new ButtonType("Quit Game", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeStart, buttonTypeQuit );

        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeStart) {
                log.config("User started day run.");
                for (Node n : grid.getChildren()) {
                    LemonadePlayer[] player = game.getModel().getPlayers();
                    
                    if (n instanceof TextField) {
                        TextField tf = (TextField) n;
                        String id = tf.getId();  // NG:n - glass made,  PG:n - price,  NS:n - signs made
                        log.log(Level.FINER, 
                                "    node: {0} val: {1}",
                                new Object[]{tf.getId(), tf.getText()}
                        );
                        String[] split = id.split(":");
                        int pix = Integer.valueOf(split[1]);
                        LemonadePlayer p = player[pix];
                        switch (split[0]) {
                            case "NG":
                                p.setL(Integer.parseInt(tf.getText()));
                                break;
                            case "PG":
                                p.setP(Integer.parseInt(tf.getText()));
                                break;
                            case "NS":
                                p.setS(Integer.parseInt(tf.getText()));
                                break;
                        }
                    }
                }
                // TODO: run it.
                game.setBeginSelling();
                
                return "OK";
            } else if ( b == buttonTypeQuit ) {
                game.setQuit();
            }
            return null;
        });

        dialog.show();
        dialog.setX(dialog.getX()+360); // Scoot to the right.
    }

    private void dailyReportDialog() {
        Dialog<String> dialog = new Dialog();
        dialog.setTitle("Lemonade Stand");
        dialog.setHeaderText("Daily Report");
        Image glyphImage = new Image(getClass().getResourceAsStream(
                "/glyphs/money-bag.png"
        ));

        ImageView im = new ImageView(glyphImage);
        im.setFitHeight(64);
        im.setPreserveRatio(true);
        dialog.setGraphic(im);

        VBox contentArea = new VBox();
        contentArea.getChildren().addAll(
                new Label(game.getModel().getDailyReportMessage()),
                getPlayerReportGrid()
        );
        dialog.getDialogPane().setContent(contentArea);

        ButtonType buttonTypeStart = new ButtonType("OK", ButtonData.OK_DONE);
        ButtonType buttonTypeQuit = new ButtonType("Quit Game", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeStart, buttonTypeQuit );

        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeStart) {
                log.config("User finised report.");
                new TimerTask() {
                    @Override
                    public void run() {
                        game.setReportShown();  // tells game to move to next state.
                    }
                }.run();
                return "OK";
            } else if (b == buttonTypeQuit) {
                game.setQuit();
                return "OK";
            }
            return null;
        });
                
        dialog.show();
        dialog.setX(dialog.getX()+360); // Scoot to the right.
    }

    private GridPane getPlayerReportGrid() {
        GridPane grid = new GridPane();
        grid.setPrefWidth(600);

        Label pLabel = new Label("Player: ");
        pLabel.setAlignment(Pos.BASELINE_RIGHT);
        pLabel.setMinWidth(150);

        Label label1 = new Label("Glasses Made: ");
        label1.setAlignment(Pos.BASELINE_RIGHT);
        label1.setMinWidth(150);
        Label label2 = new Label("Price per Glass: ");
        label2.setAlignment(Pos.BASELINE_RIGHT);
        label2.setMinWidth(150);
        Label label3 = new Label("Glasses Sold: ");
        label3.setAlignment(Pos.BASELINE_RIGHT);
        label3.setMinWidth(150);
        Label label4 = new Label("Signs Made: ");
        label4.setAlignment(Pos.BASELINE_RIGHT);
        label4.setMinWidth(150);
        Label label5 = new Label("Income: ");
        label5.setAlignment(Pos.BASELINE_RIGHT);
        label5.setMinWidth(150);
        Label label6 = new Label("Expenses: ");
        label6.setAlignment(Pos.BASELINE_RIGHT);
        label6.setMinWidth(150);
        Label label7 = new Label("Profit: ");
        label7.setAlignment(Pos.BASELINE_RIGHT);
        label7.setMinWidth(150);
        Label label8 = new Label("Balance: ");
        label8.setAlignment(Pos.BASELINE_RIGHT);
        label8.setMinWidth(150);

        grid.add(pLabel, 1, 1);
        grid.add(label1, 1, 2);
        grid.add(label2, 1, 3);
        grid.add(label3, 1, 4);
        grid.add(label4, 1, 5);
        grid.add(label5, 1, 6);
        grid.add(label6, 1, 7);
        grid.add(label7, 1, 8);
        grid.add(label8, 1, 9);

        DecimalFormat df = new DecimalFormat("0.00");

        for (int i = 0; i < game.getModel().getNumPlayers(); i++) {
            LemonadePlayer player = game.getModel().getPlayer(i);
            Label l = new Label(Integer.toString(i + 1));
            l.setAlignment(Pos.BASELINE_CENTER);
            l.setMaxWidth(60);
            grid.add(l, i + 2, 1); // Player number heading            

            grid.add(getROCell(Integer.toString(player.getL())), i + 2, 2); // Glasses made            
            grid.add(getROCell(Integer.toString(player.getP())), i + 2, 3); // Price 
            grid.add(getROCell(Integer.toString(player.getGlassesSold())), i + 2, 4); // Glasses sold
            grid.add(getROCell(Integer.toString(player.getS())), i + 2, 5); // Signs Made 
            grid.add(getROCell("$" + df.format(player.getIncome())), i + 2, 6); // Income
            grid.add(getROCell("$" + df.format(player.getExpenses())), i + 2, 7); // Expenses
            grid.add(getROCell("$" + df.format(player.getProfit())), i + 2, 8); // Profit
            grid.add(getROCell("$" + df.format(player.getA())), i + 2, 9); // Balance
        }

        return grid;
    }

    private Node getROCell(String val) {
        Label cell = new Label(val);
        cell.setAlignment(Pos.BASELINE_RIGHT);
        cell.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        cell.setMinWidth(80);
        cell.setMaxWidth(80);

        return cell;
    }

    private Node getNumericField(int val, String id) {
        TextField cell = new TextField(Integer.toString(val));
        cell.setId(id);
        cell.setMinWidth(60);
        cell.setMaxWidth(60);

        // Don't let user enter non digits.
        cell.textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\d*")) {
                cell.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        return cell;
    }
    

}
