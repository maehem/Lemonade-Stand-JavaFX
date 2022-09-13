/*
 * Data Model for Lemonade Stand
 */
package com.maehem.lemonade;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark J. Koch
 */
public class LemonadeModel {
    private static final Logger log = Logger.getLogger(LemonadeModel.class.getName());
    static final int    MAX_PLAYERS = 6;

    private boolean  gameOver = false;
    
    private int numPlayers=0; // var 'N'.  Num players. Input in titlePage()
    private int day = 0;
    private double currentCost = 0;
    
    private boolean streetWork = false;
    
    private double r1 = 0;              // Dryness, effects demand. Normal 1, Hot Day:2, RainyDay: <1
    private int r2 = 0;                 // Street Worker buyout.  0=no-buyout or 2=buyout.  Could be boolean.
    private int sC = 0;                 // Sunny/Cloudy.  2 = sunny, 5 = thunderstorm, 7 = heatwave, 10 = rain w/ chance of thunderstorm.

    private LemonadePlayer player[] = new LemonadePlayer[MAX_PLAYERS];
    
    private String currentEventMessage = "";
    private String dailyReport = "";
    
    protected LemonadePlayer[] getPlayers() {
        return player;
    }
    
    LemonadePlayer getPlayer(int i) {
        return player[i];
    }
    
    public int getNumPlayers() {
        return numPlayers;
    }
    
    public void setNumPlayers(int numPlayers) {
        log.log(Level.CONFIG, "Number of players set to: " + numPlayers);
        for ( int i=0; i<numPlayers; i++ ) {
            player[i] = new LemonadePlayer();
        }
        this.numPlayers = numPlayers;
    }
    
    public int getDay() {
        return day;
    }
    
    public void newDay() {        
        setDryness(1.0 - Math.random()*0.7);  //r1 = 1;  // Reset dryness. Random helps with clouds.
        r2 = 0;  // Reset buy-out
        streetWork = false;
        
        for ( int i=0; i< numPlayers; i++ ) {
            getPlayer(i).setG(1); // Could have been set to 0 by thunderstorm.
        }
        day++;
    }
    
    public void setCost( double c ) {
        log.log(Level.CONFIG, "Cost set to: {0}", c);
        currentCost = c;
    }
    
    public double getCost() {
        return currentCost;
    }
    
    public int getStreetBuyout() {
        return r2;
    }
    
    public void setStreetBuyout() {
        log.log(Level.CONFIG, "Buyout set to: {0}", 2);
        r2 = 2;
    }
    
    public int getSunnyCloudy() {
        return sC;
    }
    
    public void setSunnyCloudy(  int val ) {
        log.log(Level.CONFIG, "Sunny/Cloudy set to: {0}", val);
        sC = val;
    }
    
    public double getDryness() {
        return r1;
    }
    
    public void setDryness( double dry ) {
        log.log(Level.CONFIG, "Dryness set to: {0}", dry);
        r1 = dry;
    }
    
    protected void setCurrentEventsMessage(String mes) {
        currentEventMessage = mes;
    }
    
    protected String getCurrentEventsMessage() {
        return currentEventMessage;
    }
    
    protected void setGameOver() {
        log.log(Level.CONFIG, "GameOver set to: {0}", true);
        gameOver = true;
    }
    
    protected boolean isGameOver() {
        return gameOver;
    }
    
    protected void setDailyReportMessage( String message ) {
        dailyReport = message;
    }
    
    protected String getDailyReportMessage() {
        return dailyReport;
    }
    
    protected boolean getStreetWork() {
        return streetWork;
    }
    
    protected void setStreetWork( boolean val ) {
        log.log(Level.CONFIG, "StreetWork set to: {0}", val);
        streetWork = val;
    }
}
