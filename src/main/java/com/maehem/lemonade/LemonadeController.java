/*
 * Lemonade Controller - main logic for game

        10 REM    <<< LEMONADE STAND >>>
        15 REM 
        20 REM   FROM AN ORIGINAL PROGRAM
        30 REM    BY BOB JAMISON, OF THE
        40 REM    MINNESOTA  EDUCATIONAL
        50 REM     COMPUTING CONSORTIUM
        60 REM           *  *  *
        70 REM    MODIFIED FOR THE APPLE
        80 REM        FEBRUARY, 1979
        90 REM      BY CHARLIE KELLNER
        92 REM      V.3 BY DREW LYNCH
        94 REM    V.4 BY BRUCE TOGNAZZINI
        99 REM          *  *  *
        ?? REM   Ported to Java/JavaFX 
        ?? REM       September, 2022
        ?? REM      V.5 by Mark J Koch
 * 
 * Original BASIC source code reference for Apple II game located at end of
 * this file and in the base level of this repository.
 */
package com.maehem.lemonade;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark J. Koch
 */
public class LemonadeController {
    private static final Logger log = Logger.getLogger(LemonadeController.class.getName());
    
    public enum State  { START, SPLASH, SETUP, NEW_BUSINESS, DO_LOAD, DAY_START, SELLING_START, SELLING_END, REPORT, QUIT };
    public enum Type { LOAD, NEW };
    
    private State state = State.START;
    
    private Type type = Type.NEW;
    
    private final ArrayList<LemonadeStateChangeListener> listeners = new ArrayList<>();
    
    private final LemonadeModel model = new LemonadeModel();
    
    // Constants
    static final int    P9 = 10;   //150 P9 = 10    // effects formula if price set is above or below this number.
    static final double S3 = 0.15; //160 S3 = .15   // Price of advertizing signs. (dollars)
    static final int    S2 = 30;   //170 S2 = 30    // Effectiveness of each sign?
    static final double A2 = 2.00; //175 A2 = 2.00  // Starting funds (dollars).
    static final double C9 = 0.5;  //194 C9 = .5    // part of computation math for glasses sold.
    static final double C2 = 1.0;  //195 C2 = 1     // part of computation math for glass sold.

    
//    double r1 = 0;              // Dryness, effects demand. Normal 1, Hot Day:2, RainyDay: <1
//    int r2 = 0;                 // Street Worker buyout.  0=no-buyout or 2=buyout.  Could be boolean.
//    int sC = 0;                 // Sunny/Cloudy.  2 = sunny, 5 = thunderstorm, 7 = heatwave, 10 = rain w/ chance of thunderstorm.
    double c1 = 0;                // Cost in pennies.

    //int c5 = 0;                 // Set to 1 by prompt if user wants to make changes.  We won't use this in Java.
    
    //double sti = 0.0;           // Player funds. Copy of a[i] but gets manipulated during player round.
    
    //char newGame = 'N';         // Var 'A'

    // Player vars.  
    // TODO: Change to Player object.
                                //: GOTO 135
                                //135 DIM A(30),L(30),H(30),B(30),S(30),P(30),G(30)
    //double a[] = new double[30];// Available funds
    //int l[] = new int[30];      // Glasses to make
    //int h[] = new int[30];      // Freak thunderstorm. Per player. Not working.
    //int b[] = new int[30];      // Bankrupt Flag. 1=bankrupt, 0=normal  Could be boolean or check (a[] > 0).
    //int s[] = new int[30];      // Signs for advertising.
    //int p[] = new int[30];      // Price to charge for lemonate (cents)
    //double g[] = new double[30];// Gain. Effects Glasses Sold. Always  = 1. No game effect. Maybe for game hardness/progression?

    //10200 REM  INIT VARIABLES
    //10210 I = K1 = X = Y  // i not global, k1 defined below.
    
    // POKE vectors not used in Java version.
    //int x = 0;          // Pitch and duration vars for READ DATA chunk.
    //int y = 0;
    
//    // None of these variables are ever used. Some are in unreachable code.
//    int k1 = 1;         //    : K1 = 1      // Old code?  Part of unreachable code at 7004 and 17104.
//    float kP = 0.5f;    //    : KP = .5     // Old code?
//    int k5 = 5;         //    : K5 = 5      // Old code?
//    int kB = 25;        //    : KB = 25     // Never used.
//    int k2 = 3;         //    : K2 = 2      // Old code?
//    int k3 = 4;         //    : K3 = 3      // Old code?
//    int k4 = 4;         //    : K4 = 4      // Never used.
//    int k7 = 7;         //    : K7 = 7      // Never used.
//    int kR = 27;        //    : KR = 27     // Never used.
//    int k8 = 8;         //    : K8 = 8      // Never used.
//    int kT = 13;        //    : KT = 13     // never used.
//    int kL = 14;        //    : KL = 14     // Old code?
//    int k6 = 6;         //    : K6 = 6      // Never used.
//    int k9 = 9;         //    : K9 = 9      // Never used.
    
    /**
     * Call this to start things going.
     */
    public void gameInit() {
        log.log(Level.CONFIG, "Game Init");

        initialize();               //  5 GOSUB 10000       
        
        // Wait a second after window comes up and then pop the splash screen.
        new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    setState(State.START);
                } catch (InterruptedException ex) {
                    log.log(Level.SEVERE, null, ex);
                }
            }
        }.run();
    }
    
    /**
     * Call to cause game to quit.
     */
    public void setQuit() {
        setState(State.QUIT );
    }
    
    /**
     * Called by Dialog Manager to tell us it's finished with START.
     */
    public void setStartFinished() {
        setState(State.SPLASH);
    }
    
    // This state flow moved to ViewManager
//    private void gameDo() {          //0 REM  Run a lemonade stand.
//        // Present as splash screen for 8 seconds.
//        // start a timer then call:
//        getSplashPageText();               //  : GOSUB 11000
//        
//        // Present as dialog        //300 REM    START OF GAME
//        introPage();                //310 GOSUB 12000
//    
//        for ( int i=0; i<model.getNumPlayers(); i++ ) {   //  : FOR I = 1 TO N
//            LemonadePlayer player = model.getPlayer(i);
//            //player.setBankrupt(false);         //  : B(I) = 0 // 0 by default.
//            player.depositA(A2);           //  : A(I) = A2  // TODO make this the default.
//        }                                       //  : NEXT 
//        
//        if ( newGame == 'Y' ) {                 //320 IF A$ = "Y" THEN  GOSUB 13000
//            newBusiness();                                                //  : GOTO 400
//        } else {
//            continueOldGame();                  //330 GOSUB 14000
//        }
//        
//        while (true ) {
//            runDay();
//        }
//    }
    
    /**
     * Start a new day.   Called after user sets number of players and after
     * each daily report is finished.
     * 
     */
    private void startNewDay() {
        computeWeather();
        model.newDay();
        model.setCost( computeCost() );    //540 ...
        computeCurrentEvents();
        
        setState(State.DAY_START); // Notifies listeners, cause player entry to display.
    }
    
    /**
     * Trigger selling.  BaseView will animate for a bit (based on conditions).
     */
    public void setBeginSelling() {
        setState(State.SELLING_START);
    }
    
    private void computeReport() {
        StringBuilder sb = new StringBuilder("Daily Messages:\n");

        //c5 = 0;                       //1110 C5 = 0  // Not sued in Java.
                                        //   : TEXT 
                                        //   : HOME 
                                        //1120 PRINT 
                                        //   : IF SC = 10 AND  RND (1) < .25 THEN 2300
        if ( model.getSunnyCloudy() == 10 && Math.random() < 0.25 ) {
            sb.append(eventThunderstorm());
        }

        // Assemble Finance Report Dialog.

                                            //1130 PRINT "$$ LEMONSVILLE DAILY FINANCIAL REPORT $$"
                                            //1135 PRINT 

                                            // Play a little melody
                                            //1140 POKE 768,152
                                            //   : POKE 769,80
                                            //   : CALL 770   // Call $0302
                                            //1142 POKE 768,128
                                            //   : POKE 769,160
                                            //   : CALL 770
                                            //1144 POKE 768,152
                                            //   : POKE 769,40
                                            //   : CALL 770
                                            //1146 POKE 768,144
                                            //   : POKE 769,80
                                            //   : CALL 770
                                            //1148 POKE 768,128
                                            //   : POKE 769,200
                                            //   : CALL 770

                                            //1180 REM    CALCULATE PROFITS
        
        if ( model.getStreetBuyout() == 2 ) { //1182 IF R2 = 2 THEN 2290
            sb.append(eventStreetCrewBuyout());
        }

        // R3 is never changed from 0. So this never triggers.
                                                                    //1183 IF R3 = 3 THEN 2350

        for ( int i=0; i<model.getNumPlayers(); i++) {              //1185 FOR I = 1 TO N
            LemonadePlayer player = model.getPlayer(i);
            
            if ( player.getA() < 0 ) {                               //1186 IF A(I) < 0 THEN A(I) = 0
                player.setBankrupt();
            }
            boolean n2Do = true;
            if ( model.getStreetBuyout() != 2 ) {                    //1187 IF R2 = 2 THEN 1260 // Street Buyout when R2 = 2
                double n1;
                if ( player.getP() < P9 ) {                          //1190 IF P(I) >  = P9 THEN 1220
                    n1 = (P9-player.getP())/P9 * 0.8 * S2 + S2;    //1200 N1 = (P9 - P(I)) / P9 * .8 * S2 + S2
                    //1210 GOTO 1230
                } else {
                    n1 = ((P9^2) * S2/ player.getP()^2);             //1220 N1 = ((P9 ^ 2) * S2 / P(I) ^ 2)
                }
                double w = -player.getS() * C9;                      //1230 W =  - S(I) * C9
                double v = 1 - (Math.exp(w) * C2);                //1232 V = 1 - ( EXP (W) * C2)
                player.setGlassesSold( (int) (model.getDryness() * (n1 +(n1* v))) );   //1234 N2 = R1 * (N1 + (N1 * V))
                player.setGlassesSold( (int) (player.getGlassesSold() * player.getG()) );   //1240 N2 =  INT (N2 * G(I))  // Not used, G[i] is always 1.

                if ( player.getGlassesSold() < player.getL() ) {     //1250 IF N2 <  = L(I) THEN 1270
                    n2Do = false;
                }
            }

            if ( n2Do) {             //1260 N2 = L(I)                
                player.setGlassesSold( player.getL() );
            }

            double m = player.getGlassesSold() * player.getP() * 0.01; //1270 M = N2 * P(I) * .01 // Income
            player.setIncome(m);
            double e = player.getS() * S3 + player.getL() * c1;//1280 E = S(I) * S3 + L(I) * C1  // Expenses
            player.setExpenses(e);
            double p1 = m - e;                                  //1290 P1 = M - E                // Net Profit
            player.setProfit(p1);
            player.depositA(p1);                            //1300 A(I) = A(I) + P1

            // TODO:  Need to fix this so that random players have freak thunderstorm?
            // It never worked in the original game.
            // Never used. The H(I) var is always 0.
//                if ( h[i] == 1 ) { //1310 IF H(I) = 1 THEN 2300
//                    thunderstorm();
//                    // Do we restart the loop?
//                    
//                    continue;   // Next player.
//                }

                                                    //1320 PRINT 
            if ( player.getB() == 1 ) {             //1321 IF B(I) <  > 1 THEN 1330
                                                    //1326 PRINT "STAND ";I;
                //   : PRINT "   BANKRUPT"
                sb.append(MessageFormat.format("Player {0} went bankrupt.\n", i));
                    //continueOrEnd();              //   : GOSUB 18000
                                                    //1327 VTAB 5
                                                    //   : CALL  - 958 // Clears text from cursor to bottom of screen (including text on same line as cursor) (same as <ESC> <F>)
                continue;                           //   : GOTO 1390
            }
            //dailyReport();                        //1330 GOSUB 5000  // handled by ViewManager
            
            // Check if player went bankrupt.
            if (player.getA() < model.getCost()/100.0f) { //1350 IF A(I) > C / 100 THEN 1390

                //sb.append(MessageFormat.format("Stand: {0}\n", i));  //1360 PRINT "STAND ";I

                                                    //1365 HOME 
                                                    //   : PRINT "  ...YOU DON'T HAVE ENOUGH MONEY LEFT"
                                                    //1370 PRINT " TO STAY IN BUSINESS  YOU'RE BANKRUPT!"
                //sb.append("You don't have any money left to stay in business.\nYou're bankrupt.");
                player.setBankrupt();  //1380 B(I) = 1

                //continueOrEnd();                  //1382 GOSUB 18000 // get a value for N
                                                    //   : HOME 
                                                    //1385 IF N = 1 AND B(1) = 1 THEN 31111
                
                // Was this the final player standing?
                if ( model.getNumPlayers()==1 && player.getB()==1 ) { // Quit if only one player and bankrupt.
                    model.setGameOver();
                    //quit();   TODO: New state GAME_OVER
                }
            }
        }                                       //1390 NEXT I

    // handled in model    r1 = 1;              //1395 R1 = 1  // Reset dryness.
    // handled in model    r2 = 0;              //1396 R2 = 0
                                                //1400 GOTO 400  // weather report
        model.setDailyReportMessage(sb.toString());
    }  // end while(1)

    private void computeWeather() {
                                            //400 REM    WEATHER REPORT
        double weather = Math.random();    //410 SC =  RND (1)
        
        if ( weather < 0.6 ) {              //420 IF SC < .6 THEN SC = 2
            model.setSunnyCloudy(2);
                                            //  : GOTO 460
        } else if ( weather < 0.8 ) {       //430 IF SC < .8 THEN SC = 10
            model.setSunnyCloudy(10);   //  : GOTO 460
        } else {                            //440 SC = 7
            model.setSunnyCloudy(7);
        }

        if ( model.getDay() < 3 ) {         //460 IF D < 3 THEN SC = 2
            model.setSunnyCloudy(2);    // It's always sunny in Philadelphia (on days 1 and 2).
        }
    }
    
    private double computeCost() {
        double cost = 2.0;                  //540 C = 2
        if ( model.getDay() > 2 ) {         //  : IF D > 2 THEN C = 4
            cost = 4.0;  // No more free sugar from Mom.
        } else if ( model.getDay() > 6 ) {  //550 IF D > 6 THEN C = 5
            cost = 5.0;  // Lemons cost more now.
        }
        return cost;  // in pennies.
    }
    
    private void computeCurrentEvents() {
        StringBuilder sb = new StringBuilder();
                                        //520 PRINT "ON DAY ";D;", THE COST OF LEMONADE IS ";
                                        //560 PRINT "$.0";C
        sb.append(MessageFormat.format(
            "On day {0}, the cost of lemonade is {1} cents\n",
            new Object[]{ model.getDay(), model.getCost() }
        ));
        
                                        //  : PRINT 
        c1 = model.getCost() * 0.01;    //570 C1 = C * .01  // Convert cost in pennies to dollars.
        //r1 = 1;                       //580 R1 = 1  // TODO: Don't need as it is reset before each day?
        
                                        //600 REM    CURRENT EVENTS
        if ( model.getDay() == 3 ) {    //610 IF D <  > 3 THEN 650
                                        //620 PRINT "(YOUR MOTHER QUIT GIVING YOU FREE SHUGAR)"
            sb.append("(Your mother quit giving you free sugar.)\n");
        }
        if ( model.getDay() == 7 ) {               //650 IF D <  > 7 THEN 700
                                        //660 PRINT "(THE PRICE OF LEMONADE MIX JUST WENT UP)"
            sb.append("(The price of lemons just went up.)\n");
        }
                                        //700 REM    AFTER 2 DAYS THINGS CAN HAPPEN
        if ( model.getDay() > 2 ) sb.append(randomEvent());   //710 IF D > 2 THEN 2000

                                        //800 REM    INPUT VALUES
        sb.append("\n\n");            //805 PRINT

                                        //  : POKE 34, PEEK (37)  // Cursor position to top of screen. Not used in Java.
                                        //810 FOR I = 1 TO N
 
        model.setCurrentEventsMessage(sb.toString());
    }

    //2000 REM    RANDOM EVENTS
    private String randomEvent() {
        if ( model.getSunnyCloudy() == 10 ) { //2010 IF SC = 10 THEN 2110
            return eventLightRain();
        }
        if ( model.getSunnyCloudy() == 7 ) { //2030 IF SC = 7 THEN 2410  // Heat Wave
            return eventHeatwave();
        }
        if ( Math.random() < 0.25 ) {       //2040 IF  RND (1) < .25 THEN 2210
            return eventStreetWork();
        }


        return "";                          //2050 GOTO 805
            // Unreachable           
//            if ( x1 == 1) {               //2100 IF X1 = 1 THEN 805
//                return;
//            }        
    }
    
    private String eventLightRain() {
        int j = 30 + (int)(Math.random() * 5.0) * 10;  //2110 J = 30 +  INT ( RND (1) * 5) * 10
        
                                            //2112 PRINT "THERE IS A ";J;"% CHANCE OF LIGHT RAIN,"
                                            //2115 PRINT "AND THE WEATHER IS COOLER TODAY."
        
        model.setDryness( 1.0 - (j/100.0)); //2120 R1 = 1 - J / 100
                                            //2130 X1 = 1    // Not used.
        
        return MessageFormat.format(
                "\n          WEATHER REPORT\n" + 
                "          There is a {0}% chance of light rain, and the weather is cooler today.\n", 
                j
        );
    }                                       //2140 GOTO 805

                                            //2200 IF X2 = 1 THEN 805  // unreachable
    
    private String eventStreetWork() {
                                            //2210 PRINT "THE STREET DEPARTMENT IS WORKING TODAY."
                                            //2220 PRINT "THERE WILL BE NO TRAFFIC ON YOUR STREET."
        String message =
                "\nThe street department is working today. " +
                "There will be no traffic on your street.\n";
       
        model.setStreetWork(true);
        // 50% cnance that crew will buyout lemonade or day is bust.
       if ( Math.random() > 0.5 ) {         //2230 IF  RND ( - 1) < .5 THEN 2233
            model.setStreetBuyout();        //2231 R2 = 2
                                            //2232 GOTO 2250
       } else {
            model.setDryness(0.1);      //2233 R1 = .1   // Dryness(demand) set to very wet.  Causes fewer sales.
       }
       // Not used.                         //2250 X2 = 1
       
       return message;       
    }                                       //2260 GOTO 805
    
    private String eventStreetCrewBuyout() {
        String message = "The street crew bought all your" + //2290 PRINT "THE STREET CREWS BOUGHT ALL YOUR"
            "lemonade at lunchtime.\n";                     //2295 PRINT "LEMONADE AT LUNCHTIME!!"
                                                            //2296 FOR DE = 1 TO 1000  // Delay
                                                            //   : NEXT DE
                                                            //   : VTAB 3
                                                            //   : CALL  - 958
                                                            //2297 GOTO 1185
        
        return message;
    }
    
    private String eventThunderstorm() {                //2300 REM    THUNDERSTORM!
        //x3 = 1;                                       //2310 X3 = 1  // Not used.
        //r3 = 0;                                       //   : R3 = 0  // Not used.
        model.setSunnyCloudy(5);                    //2320 SC = 5  // thunderstorm
    
        weatherDisplay();                               //   : GOSUB 15000
                                                        //   : TEXT 
                                                        //   : HOME 
                                                        //2330 PRINT "WEATHER REPORT:  A SEVERE THUNDERSTORM"
                                                        //2340 PRINT "HIT LEMONSVILLE EARLIER TODAY, JUST AS"
                                                        //2350 PRINT "THE LEMONADE STANDS WERE BEING SET UP."
                                                        //2360 PRINT "UNFORTUNATELY, EVERYTHING WAS RUINED!!"
        String message = 
                "\n                  WEATHER REPORT\n" +
                "           A severe thunderstorm it Lemonsville\n" +
                "           earlier today, just as the lemonade\n" +
                "           stands were being set up.\n\n" +
                "           Unfortunately, everything was ruined!!\n\n";
        
        for ( int i=0; i<model.getNumPlayers(); i++ ) {  //2370 FOR J = 1 TO N
             model.getPlayer(i).setG(0);             //   : G(J) = 0
        }                                                //   : NEXT
        return message;                                 //2380 GOTO 1185                
                                                        //2400 IF X4 = 1 THEN 805  // Never reached.
    }
    
    // Heatwave
    private String eventHeatwave() {
                                                        // HEATWAVE
        // Never used.                                  //2410 X4 = 1
                                                        //2430 PRINT "A HEAT WAVE IS PREDICTED FOR TODAY!"
        String message =  
                "\n                  WEATHER REPORT\n" +
                "         A heat wave is predicted for today!\n";   
        model.setDryness(2.0);                      //2440 R1 = 2
        
        return message;
                                                        //2450 GOTO 805   // Restart
                                                        //3000 END 
    }
    
    // STI is tracked in pennies.
    // Java version handles this when formatting strings. Not used.
                                                        //4000 REM    STI => DOLLARS.CENTS
                                                        //        //4010 STI =  INT (STI * 100 + .5) / 100
                                                        //        //4020 STI$ = "$" +  STR$ (STI)
                                                        //        //4030 IF STI =  INT (STI) THEN STI$ = STI$ + ".0"
                                                        //        //4040 IF STI =  INT (STI * 10 + .5) / 10 THEN STI$ = STI$ + "0"
                                                        //4050 RETURN 

    // Daily report is handled in DialogManager and  uses UI code.
                                                        //5000 VTAB 6
                                                        //   : POKE 34,5
                                                        //5002 PRINT "   DAY ";D; TAB( 30);"STAND ";I
                                                        //   : PRINT 
                                                        //   : PRINT 
                                                        //5010 PRINT "  ";N2; TAB( 7);"GLASSES SOLD"
                                                        //   : PRINT 
                                                        //5012 STI = P(I) / 100
                                                        //stiToDollars(); //   : GOSUB 4000
                                                        //   : PRINT STI$; TAB( 7);"PER GLASS";
                                                        //5014 STI = M
                                                        //stiToDollars(); //   : GOSUB 4000
                                                        //   : PRINT  TAB( 27);"INCOME ";STI$
                                                        //5016 PRINT 
                                                        //   : PRINT 
                                                        //   : PRINT "  ";L(I); TAB( 7);"GLASSES MADE"
                                                        //   : PRINT 
                                                        //5020 STI = E
                                                        //stiToDollars(); //   : GOSUB 4000
                                                        //   : PRINT "  ";S(I); TAB( 7);"SIGNS MADE"; TAB( 25);"EXPENSES ";STI$
                                                        //   : PRINT 
                                                        //   : PRINT 
                                                        //5030 STI = P1
                                                        //stiToDollars(); //   : GOSUB 4000
                                                        //   : PRINT  TAB( 16);"PROFIT  ";STI$
                                                        //   : PRINT 
                                                        //5040 STI = A(I)
                                                        //stiToDollars(); //   : GOSUB 4000
                                                        //   : PRINT  TAB( 16);"ASSETS  ";STI$
                                                        //continueOrEnd(); //5060 GOSUB 18000
                                                        //5070 REM 
                                                        //5090 HOME 
                                                        //   : RETURN

                                                        // Never called
                                                        //7004 FOR J = 1 TO 6
                                                        //   : Y = I + K1
                                                        //   : I = I + K3 +  RND (K1) * K5
                                                        //   : X = X + K1 - K2 * ( RND (K1) > KP)
                                                        //   : IF I > KTP AND X > KL THEN X = X - K2
    
    
                                                        //10000 REM    INITIALIZE
    private void initialize() {
        // Place a small bit of machine code here and call it ($0302) 770 dec
        // Helpful 6502 Instructions Guide:
        //      https://www.masswerk.at/6502/6502_instruction_set.html
        // Apple II Music tone:
        // See: https://www.applefritter.com/appleii-box/APPLE2/MoreSubRoutines/MoreSubRoutines.pdf
        // Play a tone for pitch and time.
        // Preserved here for educational resons. Java version will use a modern method for
        // music tones.
        
                                                    // Pitch at 768 (0x0301)
                                                    // Time  at 769 (0x0302)
                                                    //10100 POKE 770,173  // 0xAD  -  LDA 0xC030    ; Load A with 0xC030 - Location of HW speaker toggle
                                                    //    : POKE 771,48   // 0x30
                                                    //    : POKE 772,192  // 0xC0  
                                                    //    : POKE 773,136  // 0x88 -   DEY           ; Decriment Y by 1
                                                    //    : POKE 774,208  // 0xD0  -  BNE rel(5)    ; Skip 5 bytes if Not Zero
                                                    //    : POKE 775,5    // 0x05
                                                    //    : POKE 776,206  // 0xCE -   DEC abs(1)    ; Decriment - 0x0301  (769) - time
                                                    //    : POKE 777,1    // 0x01
                                                    //    : POKE 778,3    // 0x03
                                                    //    : POKE 779,240  // 0xF0  -  BEQ rel(9)    ; Branch Equal rel (9)
                                                    //    : POKE 780,9    // 0x09
                                                    //    : POKE 781,202  // 0xCA  -  DEX           ; Decriment X
                                                    //10110 POKE 782,208  // 0xD0  -  BNE rel(0xF5) ; Branch not Zero -11 btyes to 773
                                                    //    : POKE 783,245  // 0xF5  
                                                    //    : POKE 784,174  // 0xAE  -  LDX 0x0300    ; Load pitch value at 0x0300 (768)
                                                    //    : POKE 785,0    // 0x00
                                                    //    : POKE 786,3    // 0x03
                                                    //    : POKE 787,76   // 0x4C -   JMP 0x0302    ; Jump 02L  03H (back to 770)
                                                    //    : POKE 788,2    // 0x02
                                                    //    : POKE 789,3    // 0x03
                                                    //    : POKE 790,96   // 0x60 -   RTS           ; Return from subroutine
                                                    //    : POKE 791,0    // 0x00 -   BRK           ; Break
                                                    //    : POKE 792,0    // 0x00 -   BRK           ; Break

                                                    // Moved these globals to opt of Class.
                                                    //10200 REM  INIT VARIABLES
                                                    //10210 I = K1 = X = Y
                                                    //    : K1 = 1
                                                    //    : KP = .5
                                                    //    : K5 = 5
                                                    //    : KB = 25
                                                    //    : K2 = 2
                                                    //    : K3 = 3
                                                    //    : K4 = 4
                                                    //    : K5 = 5
                                                    //    : K7 = 7
                                                    //    : KR = 27
                                                    //    : K8 = 8
                                                    //    : KT = 13
                                                    //    : KL = 14
                                                    //    : K6 = 6
                                                    //    : K9 = 9
                                                    //10999 RETURN 

    }
    
    public String getSplashPageText() {
        StringBuilder sb = new StringBuilder();
        
                                                                    ////11000 REM    INTRODUCTION
                                                                    //11000 REM    SPLASH SCREEN
                                                                    //11100 PRINT "LEMONADE STAND"
        sb.append("Lemonade Stand\n\n");
        
        //Why does it skip over this lovely splash screen?          //11105 GOTO 11280
                                                                    //11110 PRINT ";LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL;LLLLL";
                                                                    //11120 PRINT ";LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL;LLLLL";
                                                                    //11130 PRINT ";LLLL;;;;L;;;;;L;;;;L;;;;L;;;;L;;;;L;;;;";
                                                                    //11140 PRINT ";LLLL;LL;L;L;L;L;LL;L;LL;LLLL;L;LL;L;LL;";
                                                                    //11150 PRINT ";LLLL;;;;L;L;L;L;LL;L;LL;L;;;;L;LL;L;;;;";
                                                                    //11160 PRINT ";LLLL;LLLL;L;L;L;LL;L;LL;L;LL;L;LL;L;LLL";
                                                                    //11170 PRINT ";;;;L;;;;L;LLL;L;;;;L;LL;L;;;;L;;;;L;;;;";
                                                                    //11200 VTAB 11
                                                                    //11210 PRINT "LLLLLLLL;;;;;LL;LLLLLLLLLLLLLLL;LLLLLLLL";
                                                                    //11220 PRINT "LLLLLLLL;LLLLLL;LLLLLLLLLLLLLLL;LLLLLLLL";
                                                                    //11230 PRINT "LLLLLLLL;LLLLL;;;L;;;;L;;;;L;;;;LLLLLLLL";
                                                                    //11240 PRINT "LLLLLLLL;;;;;LL;LLLLL;L;LL;L;LL;LLLLLLLL";
                                                                    //11250 PRINT "LLLLLLLLLLLL;LL;LL;;;;L;LL;L;LL;LLLLLLLL";
                                                                    //11260 PRINT "LLLLLLLLLLLL;LL;LL;LL;L;LL;L;LL;LLLLLLLL";
                                                                    //11270 PRINT "LLLLLLLL;;;;;LL;LL;;;;L;LL;L;;;;LLLLLLLL";

                                                                    //11280 PRINT "COPYWRIGHT 1979: APPLE COMPUTER, INC."
                                                                    //11285 PRINT "USED BY PERMISSION OF APPLE COMPUTER, INC."
                                                                    //11290 PRINT "MODIFIED FOR USE BY THE BAUD LIBRARY BY:"
                                                                    //11292 PRINT "JOHN SHEETZ, K 2 A G I,  AND"
                                                                    //11294 PRINT "JOE GIOVANELLI, W 2 P V Y."
                                                                    //11296 PRINT "OCCASIONAL, MISSPELLED WORDS USED IN ORDER TO"
                                                                    //11298 PRINT "ENHANCE SPEECH RECOGNITION."
        sb.append(
            "Copyright 1979 by Apple Computer, Inc.\n" +
            "Not used by permission of Apple Computer, Inc.\n\n" +
            "Ported to Java/JavaFX by Mark J. Koch, @MarkJKoch, in 2022\n\n" +
            "Modified for use by the Baud Library by:\n" +
            "    John Sheetz, K2AGI  &\n" +
            "    Joe Giovanelli, W2PVY.\n\n" +
            "This Java/JavaFX version is offered as free software for\n" +
            "non-commercial or educational use only, yet may be subject\n" + 
            "to copyrights of Apple Computer, Inc.\n\n"
        );
        
        //   music();                                                       //11300 GOSUB 11700
        
        //  delay
                                                                            //    : FOR I = 1 TO 2000
                                                                            //    : NEXT 
        
        // MJK: I wonder what they were trying to acheive here? Color text?
                                                                            //11305 PRINT "BOY!!! 23P IS 25P THIS 24P GONNA 25P TAIST 22P GOOD!!!24P"
        
        // Delay
                                                                            //11307 FOR I = 1 TO 2000
                                                                            //    : NEXT 
        
                                                                            //11310 GOTO 11360  // Skip over the line drawing?
        
        // Nobody calls this.
                                                                            //11320 FOR I = 39 TO 7 STEP  - 1
                                                                            //    : COLOR= 15
                                                                            //    : VLIN 18,37 AT I
                                                                            //    : COLOR= 12
                                                                            //    : IF I < 39 THEN  VLIN 18,36 AT I + 1
                                                                            //11330 COLOR= 15
                                                                            //    : IF I < 30 THEN  VLIN 18,36 AT I + 10
                                                                            //11340 COLOR= 12
                                                                            //    : IF I < 29 THEN  VLIN 18,37 AT I + 11
                                                                            //    : POKE 32,I + 11
                                                                            //    : VTAB 11
                                                                            //    : HTAB I + 12
                                                                            //    : PRINT 
                                                                            //    : READ A$
                                                                            //    : PRINT A$;
                                                                            //11350 FOR J = 14 TO I + I
                                                                            //    : NEXT 
                                                                            //    : NEXT 
                                                                            //    : FOR I = 1 TO 1000
                                                                            //    : NEXT 

                                                                            // Play some tones
                                                                            //11360 FOR I = 36 TO 20 STEP  - 1
                                                                            //11370 FOR J = 1 TO 50
                                                                            //    : NEXT 
                                                                            //    : POKE 768,I * 3 - 12
                                                                            //    : POKE 769,12
                                                                            //    : CALL 770
                                                                            //    : NEXT 
                                                                            //11400 POKE 32,0
                                                                            //    : POKE 33,40
                                                                            //    : POKE 34,20
                                                                            //    : POKE 35,23
                                                                            //    : VTAB 22
                                                                            //11410 FOR I = 1 TO 4000
                                                                            //    : NEXT 
                                                                            //    : I =  FRE (0)
                                                                            //11490 RETURN
    
        // TODO:  Get this data moved to top.
        // There appears to be multiple songs here but are they used?
                                                                            //11500 REM    DATA
                                                                            //11510 DATA   96,180,128,60,114,60,128,120,144,60,152,60,128,255,128,60,114,60,85,120,96,60,102,60,114,120,102,60,96,255,0,0
                                                                            //11520 DATA   ,,,,,,L;LL;,L;LL;,;;LL;;,L;LL;,L;;;;,,,;L;;;;,;L;LL;,;L;LL;,;L;LL;,;;;LL;,,,,,*
                                                                            //11530 DATA   96,16,85,4,128,4,96,4,76,4,128,4,96,16,0,0
                                                                            //11540 DATA   114,120,144,60,114,255,1,120,128,120,144,60,128,120,114,60,144,120,171,255,228,255,0,0
                                                                            //11550 DATA   152,180,152,120,152,60,144,120,152,60,171,120,192,60,152,255,0,0
                                                                            //11560 DATA   0,160,128,255,152,40,171,80,192,40,228,255,1,40,0,160,192,255,192,40,171,80,152,40,128,255,0,0
        
        return sb.toString();
    }
    
    public int[] getSplashPageMusic() {
        // Pitch and Time
                                                    //11510 DATA   96,180,128,60,114,60,128,120,144,60,152,60,128,255,128,60,114,60,85,120,96,60,102,60,114,120,102,60,96,255,0,0
        
        // This will be a MIDI stream
        return new int[]{96,180,128,60,114,60,128,120,144,60,152,60,128,255,128,60,114,60,85,120,96,60,102,60,114,120,102,60,96,255,0,0 };
    }
    
                                                    //11700 REM    MUSIC
    private void music() {
        
        // Play a tune from DATA
                                                    //11710 READ I,J
                                                    //    : IF J = 0 THEN  RETURN 
                                                    //11720 POKE 768,I
                                                    //    : POKE 769,J
                                                    //    : CALL 770
                                                    //11730 GOTO 11710
    }
    
    // MJK: The labeling of Intro and Title seem swapped, so
    // I am switching them around.

    // Here for reference, but the dialog is actually presented in
    // LemonadeDialogManager based on this template and retrieved by:
    // getIntroPageText()  below.
        //12000 REM    TITLE PAGE
        //12000 REM    INTRO PAGE / SETUP
        //12100 TEXT 
        //    : HOME 
        //12110 PRINT "HI!  WELCOME TO LEMONSVILLE, CALIFORNIA!"
        //    : PRINT 
        
        // Play random music notes.
        //12115 FOR I = 1 TO 10
        //    : J =  RND (1) * 25 + 50
        //    : POKE 768,J
        //    : POKE 769,10
        //    : CALL 770
        //    : NEXT 
        
        //12120 PRINT "IN THIS SMALL TOUN, YOU ARE IN CHARGE OF"
        //12130 PRINT "RUNNING YOUR OWN LEMONADE STAND. YOU CAN"
        //12140 PRINT "COMPETE WITH AS MANY OTHER PEOPLE AS YOU"
        //12150 PRINT "WISH, BUT HOW MUCH PROFIT YOU MAKE IS UP"
        //12160 PRINT "TO YOU (THE OTHER STANDS' SALES WILL NOT"
        //12170 PRINT "AFFECT YOUR BUSINESS IN ANY WAY). IF YOU"
        //12180 PRINT "MAKE THE MOST MONEY, YOUR THE WINNER!!"
        //    : PRINT 
        //12190 PRINT "ARE YOU STARTING A NEW GAME? (YES OR NO)"
        //12200 VTAB 21
        //    : CALL  - 958
        //    : INPUT "TYPE YOUR ANSWER AND HIT RETURN ==> ";A$
        //12210 A$ =  LEFT$ (A$,1)
        //    : IF A$ <  > "Y" AND A$ <  > "N" THEN  PRINT  CHR$ (7);
        //    : GOTO 12200
        //12220 VTAB 23
        //    : CALL  - 958
        //    : INPUT "HOW MANY PEOPLE WILL BE PLAYING ==> ";N$
        //12230 N =  VAL (N$)
        //    : IF N < 1 OR N > 30 THEN  PRINT  CHR$ (7);
        //    : GOTO 12220
        //12240 RETURN
    
    public String getIntroPageHeaderText() {
        return "Hi!  Welcome to Lemonsville, California!";        
    }
 
    public String getIntroPageText() {
        return  
            "         In this small town, you are in charge of running your \n" + 
            "         own lemonade stand. You can compete with up to five \n" + 
            "         other people, but how much profit you make is up to \n" +
            "         you (the other stands' sales will not affect your business \n" + 
            "         in any way).\n\n" + 
            "         If you make the most money, You're the winner!!\n\n";
    }
    

    public String getNewBusinessPageHeader() {
        return "If life gives you lemons, make Lemonade.";
    }
   
    /**
     * Game Rules
     * Original BASIC source here:
     * 
        // Converted to getNewBusinessPageText() below.
        13000 REM    NEW BUSINESS
        13100 HOME 
        13110 PRINT "TO MANAGE YOUR LEMONADE STAND, YOU WILL "
        13120 PRINT "NEED TO MAKE THESE DECISIONS EVERY DAY: "
            : PRINT 
        13130 PRINT "1. HOW MANY GLASSES OF LEMONADE TO MAKE    (ONLY ONE BATCH IS MADE EACH MORNING)"
        13140 PRINT "2. HOW MANY ADVERTISING SIGNS TO MAKE      (THE SIGNS COST FIFTEEN CENTS EACH)  "
        13150 PRINT "3. WHAT PRICE TO CHARGE FOR EACH GLASS  "
            : PRINT 
        13160 PRINT "YOU WILL BEGIN WITH $2.00 CASH (ASSETS)."
        13170 PRINT "BECAUSE YOUR MOTHER GAVE YOU SOME SHUGAR,"
        13180 PRINT "YOUR COST TO MAKE LEMONADE IS TWO CENTS "
        13190 PRINT "A GLASS (THIS MAY CHANGE IN THE FUTURE)."
            : PRINT 
        13200 GOSUB 18000 // continueOrEnd(); 
        13202 HOME 
        13210 PRINT "YOUR EXPENSES ARE THE SUM OF THE COST OF"
        13220 PRINT "THE LEMONADE AND THE COST OF THE SIGNS. "
            : PRINT 
        13230 PRINT "YOUR PROFITS ARE THE DIFFERENCE BETWEEN "
        13240 PRINT "THE INCOME FROM SALES AND YOUR EXPENSES."
            : PRINT 
        13250 PRINT "THE NUMBER OF GLASSES YOU SELL EACH DAY "
        13260 PRINT "DEPENDS ON THE PRICE YOU CHARGE, AND ON "
        13270 PRINT "THE NUMBER OF ADVERTISING SIGNS YOU USE."
            : PRINT 
        13280 PRINT "KEEP TRACK OF YOUR ASSETS, BECAUSE YOU  "
        13290 PRINT "CAN'T SPEND MORE MONEY THAN YOU HAVE!   "
            : PRINT 
        13300 GOSUB 18000  // continueOrEnd(); 
        13302 HOME 
            : RETURN 
        
     * 
     * @return 
     */
    public String getNewBusinessPageText() {
        return 
        "To manage your Lemonade stand, you will " +
        "need to make these decisions every day: \n" +
        "\n" +
        "1. How many glasses of lemonade to make    (only one batch is made each morning)\n" +
        "2. How many advertising signs to make        (the signs cost fifteen cents each)  \n" +
        "3. What price to charge for each glass  \n" +
        "\n" +
       "You will begin with $2.00 cash (assets). " +
        "Because your mother gave you some sugar,\n" +
        "your cost to make lemonade is two cents " +
        "a glass (this may change in the future).\n" +
        "\n" +        
        "Your expenses are the sum of the cost of " +
        "the lemonade and the cost of your signs. \n" +
        "\n" +        
        "Your profits are the difference between " +
        "the income from sales and your expenses.\n" +
        "\n" +        
        "The number of glasses you sell each day " +
        "depends on the price you charge, and on\n" +
        "the number of advertising signs you use.\n" +
        "\n" +        
        "Keep track fo your assets, because you " +
        "can't spend more than you have!   \n\n\n\n";       
    }

    //  TODO: Read this from a save file instead of manual data entry.
    //14000 REM    CONTINUE OLD GAME
    private void continueOldGame() {
        // Calls around - 900 involve doing something to the screen.
        //14100 CALL  - 936  // Clear Screen
    

        //    : PRINT  CHR$ (7);
        //    : I = 0
        //14110 PRINT "HI AGAIN!  WELCOME BACK TO LEMONSVILLE! "
        //    : PRINT 
        //14120 PRINT "LET'S CONTINUE YOUR LAST GAME FROM WHERE"
        //14130 PRINT "YOU LEFT IT LAST TIME.  DO YOU REMEMBER "
        //14140 PRINT "WHAT DAY NUMBER IT WAS? ";

        //14150 INPUT "";A$
        //    : A =  VAL (A$)
        //    : PRINT 
        //    : IF A <  > 0 THEN 14200
        //14160 A$ =  LEFT$ (A$,1)
        //    : IF A$ = "Y" THEN  PRINT "GOOD!  WHAT DAY WAS IT? ";
        //    : I = I + 1
        //    : GOTO 14150

        //14170 IF A$ = "N" OR I > 0 THEN 14300
        //14180 PRINT  CHR$ (7);"YES OR NO? ";
        //    : I = I + 1
        //    : GOTO 14150
        //14200 IF A < 1 OR A > 99 OR A <  >  INT (A) THEN 14300
        //14210 D = A
        //14300 PRINT "OKAY - WE'LL START WITH DAY NO. ";D + 1
        //    : PRINT 
        //14400 FOR I = 1 TO N
        //    : PRINT 
        //    : PRINT 
        //14410 PRINT "PLAYER NO. ";I;", HOW MUCH MONEY (ASSETS)"
        //    : PRINT 
        //14420 PRINT "DID YOU HAVE? ";
        //14430 INPUT "";A$
        //    : A =  VAL (A$)
        //    : PRINT 
        //14440 IF A < 2 THEN  PRINT "O.K. - E'LL START YOU OUT WITH $2.00"
        //    : A = 2
        //    : GOTO 14490
        //14450 IF A > 40 THEN  PRINT "JUST TO BE FAIR, LET'S MAKE THAT $10.00"
        //    : A = 10
        //14490 A(I) =  INT (A * 100 + .5) / 100
        //    : NEXT 
        //14500 PRINT 
        //    : PRINT  CHR$ (7)
        //    : INPUT "...READY TO BEGIN? ";A$
        //14510 IF  LEFT$ (A$,1) = "N" THEN 13000
        //14520 RETURN 

    }
    
    // TODO: Move to LemonadeView
    //15000 REM    WEATHER DISPLAY
    private void weatherDisplay() {
        //15100 GOTO 15175
        //15110 COLOR= SC
        //    : FOR I = 0 TO 25
        //    : HLIN 0,39 AT I
        //    : NEXT I
        //15120 COLOR= 12
        //    : IF SC = 5 THEN  COLOR= 4
        //15125 FOR I = 26 TO 39
        //    : HLIN 0,39 AT I
        //    : NEXT I
        //15130 COLOR= 8
        //    : FOR I = 24 TO 32
        //    : HLIN 15,25 AT I
        //    : NEXT I
        //    : HLIN 15,25 AT 14
        //    : VLIN 14,23 AT 15
        //    : VLIN 14,23 AT 25
        //15150 COLOR= 13
        //    : FOR I = 17 TO 23 STEP 2
        //    : VLIN 22,23 AT I
        //    : NEXT I
        //15151 IF SC = 2 OR SC = 7 THEN 15160
        //15152 IF SC = 10 THEN  COLOR= 15
        //15157 IF SC = 5 THEN  COLOR= 0
        //15158 HLIN 6,10 AT 2
        //    : HLIN 4,14 AT 3
        //    : HLIN 7,12 AT 4
        //    : HLIN 22,30 AT 4
        //    : HLIN 20,36 AT 5
        //    : HLIN 23,33 AT 6
        //    : IF SC = 5 THEN  GOSUB 17000  // DO NOTHING
        //15159 GOTO 15170
        //15160 IF SC = 7 THEN  COLOR= 9
        //15162 HLIN 3,5 AT 1
        //    : HLIN 2,6 AT 2
        //    : FOR I = 3 TO 6
        //    : HLIN 1,7 AT I
        //    : NEXT 
        //    : HLIN 2,6 AT 7
        //    : HLIN 3,5 AT 8
        //15175 VTAB 22
        //    : HTAB 8
        //    : PRINT "LEMONSVILLE WETHER REPORT"
        //    : PRINT 
        //15180 IF SC = 2 THEN  HTAB 18
        //    : PRINT " SUNNY ";
        //15182 IF SC = 7 THEN  HTAB 15
        //    : PRINT " HOT AND DRY ";
        //15184 IF SC = 10 THEN  HTAB 17
        //    : PRINT " CLOUDY ";
        //15186 IF SC = 5 THEN  HTAB 14
        //    : PRINT " THUNDERSTORMS! ";
        //    : GOSUB 17000  // DO NOTHING
        //15200 RESTORE     // Restart DATA from beginning
        //15210 READ A$
        //    : IF A$ <  > "*" THEN 15210  // Skip over data to song after '*' character.
        //15220 IF SC <  > 2 THEN 15300   // Don't play current tune if not SC==2
        // Play this tune on Day 2?
        //15230 READ I,J
        //    : IF J = 0 THEN 15500
        //15240 FOR K = 1 TO J
        //    : POKE 768,I
        //    : POKE 769,10
        //    : CALL 770
        //15250 FOR L = K TO J
        //    : NEXT 
        //    : NEXT 
        
        //    : GOTO 15230
        
        //15300 READ I,J
        //    : IF J <  > 0 THEN 15300  // Skip this tune.
        //15310 IF SC = 7 THEN 15400
        //15320 READ I,J
        //    : IF J <  > 0 THEN 15320  // Skip this tune.
        //15330 IF SC = 10 THEN 15400
        //15340 READ I,J
        //    : IF J <  > 0 THEN 15340  // Skip this tune.
        //15400 READ I,J
        //    : IF J = 0 THEN 15500
        //15410 IF I = 1 THEN  FOR I = 1 TO J
        //    : NEXT 
        //    : GOTO 15400

        //15420 POKE 768,I
        //    : POKE 769,J
        //    : CALL 770
        //15430 GOTO 15400
        
        //15440 IF SC = 5 THEN  GOSUB 17000  // Do Nothing
        //15500 IF SC = 5 THEN  GOSUB 17000  // Do Nothing
        //15510 I =  FRE (0)
        //    : FOR I = 1 TO 200
        //    : NEXT 
        //    : RETURN

    } //17000 RETURN
    
    // Nothing calls this:
//17104 FOR J = 1 TO 6
//    : Y = I + K1
//    : I = I + K3 +  RND (K1) * K5
//    : X = X + K1 - K2 * ( RND (K1) > KP)
//    : IF I > KTP AND X > KL THEN X = X - K2
    
    
    // Pause and ask to continue game? - Not used in Java version
        //18000 VTAB 24
        //    : PRINT " PRESS SPACE TO CONTINUE, ESC TO END...";
        //18005 POKE  - 16368,0   // Clear Keyboard Strobe
        //18010 GET IN$
        //    : IF IN$ <  > " " AND  ASC (IN$) <  > 27 THEN 18010
        //18020 IF  ASC (IN$) <  > 27 THEN  RETURN 
        //18021 HOME 
        //    : VTAB 8
        //    : PRINT "ARE YOU SURE YOU WANT TO QUIT ?";
        //18022 GET IN$
        //18023 IF IN$ = "Y" THEN 31111 // quit();
        //18024 RETURN 
        //18030 RETURN 
    
    // Quitting   -  Not used in Java version
        //31111 HOME 
        //31113 IF  PEEK (994) +  PEEK (1001) = 192 THEN  CALL 1002
        //31116 SERIAL = 31416
        //31126 REM  SERIAL NUMBER OF DISK
        //31136 POKE  - 16368,0
        //    : IF  PEEK (1020) =  INT (SERIAL / 256) AND  PEEK (1021) = SERIAL - ( INT (SERIAL / 256) * 256) AND  PEEK (1019) = 0 THEN 31156
        //31146 TEXT 
        //    : CALL  - 936
        //    : TEXT 
        //    : END 
        //31156 POKE 1020,0
        //    : POKE 1021,0
        //    : PRINT 
        //    : PRINT "RUN INDEX"
        //    : END 
    
    public State getState() {
        return state;
    }
    
    private void setState(State s) {
        state = s;
        log.log(Level.CONFIG, "Game state changed to: {0}", s.name());
        notifyListeners();        
    }
    
    public void setSplashShown() {
        setState(State.SETUP);
    }
    
    public void setNewBusinessComplete(int val) {
        model.setNumPlayers(val);
        startNewDay();
    }
    
    /**
     * Triggered by base window animation of daily activity.
     * 
     * Gives base window a chance to update display if a disaster happened
     * then pause before presenting report.
     */
    public void setSellingEnd() {
        computeReport();
        setState(State.SELLING_END);
    }
    
    /**
     * Cause ViewManager to present the daily report.
     */
    public void setReport() {
        setState(State.REPORT);
    }
    
    /**
     * User has closed the report dialog.
     */
    public void setReportShown() {
        startNewDay();
    }
    
    public void setLoadSave() {
        type = Type.LOAD;
    }
    
    public void clearLoadSave() {
        type = Type.NEW;
    }
    
    public void loadData( String filePath ) {
        
    }
    
    public void finishSetup() {
        if ( type == Type.LOAD) {
            setState(State.DO_LOAD);
        } else {
            setState( State.NEW_BUSINESS);
        }
    }
    
    public void addListener( LemonadeStateChangeListener ll ) {
        listeners.add(ll);
    }
    
    private void notifyListeners() {
        log.log(Level.FINER, "Notify listeners of new state: {0}", getState().name());
        for( LemonadeStateChangeListener ll: listeners) {
            ll.gameStateChanged();
        }
    }
    
    protected LemonadeModel getModel() {
        return model;
    }
}

/*
//--------------------------------------------------------------------------
//  REFERENCE:    Original Source Code from Apple II Game
//--------------------------------------------------------------------------

0 REM  Run a lemonade stand.
5 GOSUB 10000
: GOSUB 11000
: GOTO 135
10 REM    <<< LEMONADE STAND >>>
15 REM 
20 REM   FROM AN ORIGINAL PROGRAM
30 REM    BY BOB JAMISON, OF THE
40 REM    MINNESOTA  EDUCATIONAL
50 REM     COMPUTING CONSORTIUM
60 REM           *  *  *
70 REM    MODIFIED FOR THE APPLE
80 REM        FEBRUARY, 1979
90 REM      BY CHARLIE KELLNER
92 REM      V.3 BY DREW LYNCH
94 REM    V.4 BY BRUCE TOGNAZZINI
99 REM 
135 DIM A(30),L(30),H(30),B(30),S(30),P(30),G(30)
150 P9 = 10
160 S3 = .15
170 S2 = 30
175 A2 = 2.00
194 C9 = .5
195 C2 = 1
300 REM    START OF GAME
310 GOSUB 12000
  : FOR I = 1 TO N
  : B(I) = 0
  : A(I) = A2
  : NEXT 
320 IF A$ = "Y" THEN  GOSUB 13000
  : GOTO 400
330 GOSUB 14000
400 REM    WEATHER REPORT
410 SC =  RND (1)
420 IF SC < .6 THEN SC = 2
  : GOTO 460
430 IF SC < .8 THEN SC = 10
  : GOTO 460
440 SC = 7
460 IF D < 3 THEN SC = 2
470 GOSUB 15000
490 TEXT 
  : HOME 
500 REM    START OF NEW DAY
510 D = D + 1
520 PRINT "ON DAY ";D;", THE COST OF LEMONADE IS ";
540 C = 2
  : IF D > 2 THEN C = 4
550 IF D > 6 THEN C = 5
560 PRINT "$.0";C
  : PRINT 
570 C1 = C * .01
580 R1 = 1
600 REM    CURRENT EVENTS
610 IF D <  > 3 THEN 650
620 PRINT "(YOUR MOTHER QUIT GIVING YOU FREE SHUGAR)"
650 IF D <  > 7 THEN 700
660 PRINT "(THE PRICE OF LEMONADE MIX JUST WENT UP)"
700 REM    AFTER 2 DAYS THINGS CAN HAPPEN
710 IF D > 2 THEN 2000
800 REM    INPUT VALUES
805 PRINT 
  : POKE 34, PEEK (37)
810 FOR I = 1 TO N
815 A(I) = A(I) + .000000001
820 G(I) = 1
  : H(I) = 0
850 STI = A(I)
  : GOSUB 4000
  : PRINT "LEMONADE STAND ";I; TAB( 26);"ASSETS ";STI$
855 PRINT 
860 IF B(I) = 0 THEN 890
870 PRINT "YOU ARE BANKRUPT, NO DECISIONS"
875 PRINT "FOR YOU TO MAKE."
  : GOSUB 18000
  : GOTO 1100
890 PRINT "HOW MANY GLASSES OF LEMONADE DO YOU"
895 PRINT "WISH TO MAKE ";
900 INPUT IN$
  : L(I) =  VAL (IN$)
901 IF L(I) < 0 OR L(I) > 1000 THEN 903
902 GOTO 906
903 PRINT "COME ON, LET'S BE REASONABLE NOW!!!"
904 PRINT "TRY AGAIN"
905 GOTO 890
906 IF L(I) <  >  INT (L(I)) THEN 903
910 IF L(I) * C1 <  = A(I) THEN 950
920 PRINT "THINK AGAIN!!!  YOU HAVE ONLY ";STI$
930 PRINT "IN CASH AND TO MAKE ";L(I);" GLASSES OF"
932 PRINT "LEMONADE YOU NEED $";L(I) * C1;" IN CASH."
940 GOTO 890
950 PRINT 
951 PRINT "HOW MANY ADVERTISING SIGNS (";S3 * 100;" CENTS"
952 PRINT "EACH) DO YOU WANT TO MAKE ";
960 INPUT IN$
  : S(I) =  VAL (IN$)
961 IF S(I) < 0 OR S(I) > 50 THEN 963
962 GOTO 965
963 PRINT "COME ON, BE REASONABLE!!! TRY AGAIN."
964 GOTO 950
965 IF S(I) <  >  INT (S(I)) THEN 963
970 IF S(I) * S3 <  = A(I) - L(I) * C1 THEN 1010
975 PRINT 
980 STI = A(I) - L(I) * C1
  : GOSUB 4000
985 PRINT "THINK AGAIN, YOU HAVE ONLY ";STI$
990 PRINT "IN CASH LEFT AFTER MAKING YOUR LEMONADE."
1000 GOTO 950
1010 PRINT 
   : PRINT "WHAT PRICE (IN CENTS) DO YOU WISH TO"
1012 PRINT "CHARGE FOR LEMONADE ";
1015 INPUT IN$
   : P(I) =  VAL (IN$)
1020 IF P(I) < 0 OR P(I) > 100 THEN 1022
1021 GOTO 1024
1022 PRINT "COME ON, BE REASONABLE!!! TRY AGAIN."
1023 GOTO 1010
1024 IF P(I) <  >  INT (P(I)) THEN 1022
1025 IF C5 = 1 THEN 1050
1050 VTAB 23
   : INPUT "WOULD YOU LIKE TO CHANGE ANYTHING?";A$
1060 IF  LEFT$ (A$,1) = "Y" THEN  HOME 
   : C5 = 1
   : GOTO 820
1100 HOME 
1101 NEXT I
1110 C5 = 0
   : TEXT 
   : HOME 
1120 PRINT 
   : IF SC = 10 AND  RND (1) < .25 THEN 2300
1130 PRINT "$$ LEMONSVILLE DAILY FINANCIAL REPORT $$"
1135 PRINT 
1140 POKE 768,152
   : POKE 769,80
   : CALL 770
1142 POKE 768,128
   : POKE 769,160
   : CALL 770
1144 POKE 768,152
   : POKE 769,40
   : CALL 770
1146 POKE 768,144
   : POKE 769,80
   : CALL 770
1148 POKE 768,128
   : POKE 769,200
   : CALL 770
1180 REM    CALCULATE PROFITS
1182 IF R2 = 2 THEN 2290
1183 IF R3 = 3 THEN 2350
1185 FOR I = 1 TO N
1186 IF A(I) < 0 THEN A(I) = 0
1187 IF R2 = 2 THEN 1260
1190 IF P(I) >  = P9 THEN 1220
1200 N1 = (P9 - P(I)) / P9 * .8 * S2 + S2
1210 GOTO 1230
1220 N1 = ((P9 ^ 2) * S2 / P(I) ^ 2)
1230 W =  - S(I) * C9
1232 V = 1 - ( EXP (W) * C2)
1234 N2 = R1 * (N1 + (N1 * V))
1240 N2 =  INT (N2 * G(I))
1250 IF N2 <  = L(I) THEN 1270
1260 N2 = L(I)
1270 M = N2 * P(I) * .01
1280 E = S(I) * S3 + L(I) * C1
1290 P1 = M - E
1300 A(I) = A(I) + P1
1310 IF H(I) = 1 THEN 2300
1320 PRINT 
1321 IF B(I) <  > 1 THEN 1330
1326 PRINT "STAND ";I;
   : PRINT "   BANKRUPT"
   : GOSUB 18000
1327 VTAB 5
   : CALL  - 958
   : GOTO 1390
1330 GOSUB 5000
1350 IF A(I) > C / 100 THEN 1390
1360 PRINT "STAND ";I
1365 HOME 
   : PRINT "  ...YOU DON'T HAVE ENOUGH MONEY LEFT"
1370 PRINT " TO STAY IN BUSINESS  YOU'RE BANKRUPT!"
1380 B(I) = 1
1382 GOSUB 18000
   : HOME 
1385 IF N = 1 AND B(1) = 1 THEN 31111
1390 NEXT I
1395 R1 = 1
1396 R2 = 0
1400 GOTO 400
2000 REM    RANDOM EVENTS
2010 IF SC = 10 THEN 2110
2030 IF SC = 7 THEN 2410
2040 IF  RND (1) < .25 THEN 2210
2050 GOTO 805
2100 IF X1 = 1 THEN 805
2110 J = 30 +  INT ( RND (1) * 5) * 10
2112 PRINT "THERE IS A ";J;"% CHANCE OF LIGHT RAIN,"
2115 PRINT "AND THE WEATHER IS COOLER TODAY."
2120 R1 = 1 - J / 100
2130 X1 = 1
2140 GOTO 805
2200 IF X2 = 1 THEN 805
2210 PRINT "THE STREET DEPARTMENT IS WORKING TODAY."
2220 PRINT "THERE WILL BE NO TRAFFIC ON YOUR STREET."
2230 IF  RND ( - 1) < .5 THEN 2233
2231 R2 = 2
2232 GOTO 2250
2233 R1 = .1
2250 X2 = 1
2260 GOTO 805
2290 PRINT "THE STREET CREWS BOUGHT ALL YOUR"
2295 PRINT "LEMONADE AT LUNCHTIME!!"
2296 FOR DE = 1 TO 1000
   : NEXT DE
   : VTAB 3
   : CALL  - 958
2297 GOTO 1185
2300 REM    THUNDERSTORM!
2310 X3 = 1
   : R3 = 0
2320 SC = 5
   : GOSUB 15000
   : TEXT 
   : HOME 
2330 PRINT "WEATHER REPORT:  A SEVERE THUNDERSTORM"
2340 PRINT "HIT LEMONSVILLE EARLIER TODAY, JUST AS"
2350 PRINT "THE LEMONADE STANDS WERE BEING SET UP."
2360 PRINT "UNFORTUNATELY, EVERYTHING WAS RUINED!!"
2370 FOR J = 1 TO N
   : G(J) = 0
   : NEXT 
2380 GOTO 1185
2400 IF X4 = 1 THEN 805
2410 X4 = 1
2430 PRINT "A HEAT WAVE IS PREDICTED FOR TODAY!"
2440 R1 = 2
2450 GOTO 805
3000 END 
4000 REM    STI => DOLLARS.CENTS
4010 STI =  INT (STI * 100 + .5) / 100
4020 STI$ = "$" +  STR$ (STI)
4030 IF STI =  INT (STI) THEN STI$ = STI$ + ".0"
4040 IF STI =  INT (STI * 10 + .5) / 10 THEN STI$ = STI$ + "0"
4050 RETURN 
5000 VTAB 6
   : POKE 34,5
5002 PRINT "   DAY ";D; TAB( 30);"STAND ";I
   : PRINT 
   : PRINT 
5010 PRINT "  ";N2; TAB( 7);"GLASSES SOLD"
   : PRINT 
5012 STI = P(I) / 100
   : GOSUB 4000
   : PRINT STI$; TAB( 7);"PER GLASS";
5014 STI = M
   : GOSUB 4000
   : PRINT  TAB( 27);"INCOME ";STI$
5016 PRINT 
   : PRINT 
   : PRINT "  ";L(I); TAB( 7);"GLASSES MADE"
   : PRINT 
5020 STI = E
   : GOSUB 4000
   : PRINT "  ";S(I); TAB( 7);"SIGNS MADE"; TAB( 25);"EXPENSES ";STI$
   : PRINT 
   : PRINT 
5030 STI = P1
   : GOSUB 4000
   : PRINT  TAB( 16);"PROFIT  ";STI$
   : PRINT 
5040 STI = A(I)
   : GOSUB 4000
   : PRINT  TAB( 16);"ASSETS  ";STI$
5060 GOSUB 18000
5070 REM 
5090 HOME 
   : RETURN 
7004 FOR J = 1 TO 6
   : Y = I + K1
   : I = I + K3 +  RND (K1) * K5
   : X = X + K1 - K2 * ( RND (K1) > KP)
   : IF I > KTP AND X > KL THEN X = X - K2
10000 REM    INITIALIZE
10100 POKE 770,173
    : POKE 771,48
    : POKE 772,192
    : POKE 773,136
    : POKE 774,208
    : POKE 775,5
    : POKE 776,206
    : POKE 777,1
    : POKE 778,3
    : POKE 779,240
    : POKE 780,9
    : POKE 781,202
10110 POKE 782,208
    : POKE 783,245
    : POKE 784,174
    : POKE 785,0
    : POKE 786,3
    : POKE 787,76
    : POKE 788,2
    : POKE 789,3
    : POKE 790,96
    : POKE 791,0
    : POKE 792,0
10200 REM  INIT VARIABLES
10210 I = K1 = X = Y
    : K1 = 1
    : KP = .5
    : K5 = 5
    : KB = 25
    : K2 = 2
    : K3 = 3
    : K4 = 4
    : K5 = 5
    : K7 = 7
    : KR = 27
    : K8 = 8
    : KT = 13
    : KL = 14
    : K6 = 6
    : K9 = 9
10999 RETURN 
11000 REM    INTRODUCTION
11100 PRINT "LEMONADE STAND"
11105 GOTO 11280
11110 PRINT ";LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL;LLLLL";
11120 PRINT ";LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL;LLLLL";
11130 PRINT ";LLLL;;;;L;;;;;L;;;;L;;;;L;;;;L;;;;L;;;;";
11140 PRINT ";LLLL;LL;L;L;L;L;LL;L;LL;LLLL;L;LL;L;LL;";
11150 PRINT ";LLLL;;;;L;L;L;L;LL;L;LL;L;;;;L;LL;L;;;;";
11160 PRINT ";LLLL;LLLL;L;L;L;LL;L;LL;L;LL;L;LL;L;LLL";
11170 PRINT ";;;;L;;;;L;LLL;L;;;;L;LL;L;;;;L;;;;L;;;;";
11200 VTAB 11
11210 PRINT "LLLLLLLL;;;;;LL;LLLLLLLLLLLLLLL;LLLLLLLL";
11220 PRINT "LLLLLLLL;LLLLLL;LLLLLLLLLLLLLLL;LLLLLLLL";
11230 PRINT "LLLLLLLL;LLLLL;;;L;;;;L;;;;L;;;;LLLLLLLL";
11240 PRINT "LLLLLLLL;;;;;LL;LLLLL;L;LL;L;LL;LLLLLLLL";
11250 PRINT "LLLLLLLLLLLL;LL;LL;;;;L;LL;L;LL;LLLLLLLL";
11260 PRINT "LLLLLLLLLLLL;LL;LL;LL;L;LL;L;LL;LLLLLLLL";
11270 PRINT "LLLLLLLL;;;;;LL;LL;;;;L;LL;L;;;;LLLLLLLL";
11280 PRINT "COPYWRIGHT 1979: APPLE COMPUTER, INC."
11285 PRINT "USED BY PERMISSION OF APPLE COMPUTER, INC."
11290 PRINT "MODIFIED FOR USE BY THE BAUD LIBRARY BY:"
11292 PRINT "JOHN SHEETZ, K 2 A G I,  AND"
11294 PRINT "JOE GIOVANELLI, W 2 P V Y."
11296 PRINT "OCCASIONAL, MISSPELLED WORDS USED IN ORDER TO"
11298 PRINT "ENHANCE SPEECH RECOGNITION."
11300 GOSUB 11700
    : FOR I = 1 TO 2000
    : NEXT 
11305 PRINT "BOY!!! 23P IS 25P THIS 24P GONNA 25P TAIST 22P GOOD!!!24P"
11307 FOR I = 1 TO 2000
    : NEXT 
11310 GOTO 11360
11320 FOR I = 39 TO 7 STEP  - 1
    : COLOR= 15
    : VLIN 18,37 AT I
    : COLOR= 12
    : IF I < 39 THEN  VLIN 18,36 AT I + 1
11330 COLOR= 15
    : IF I < 30 THEN  VLIN 18,36 AT I + 10
11340 COLOR= 12
    : IF I < 29 THEN  VLIN 18,37 AT I + 11
    : POKE 32,I + 11
    : VTAB 11
    : HTAB I + 12
    : PRINT 
    : READ A$
    : PRINT A$;
11350 FOR J = 14 TO I + I
    : NEXT 
    : NEXT 
    : FOR I = 1 TO 1000
    : NEXT 
11360 FOR I = 36 TO 20 STEP  - 1
11370 FOR J = 1 TO 50
    : NEXT 
    : POKE 768,I * 3 - 12
    : POKE 769,12
    : CALL 770
    : NEXT 
11400 POKE 32,0
    : POKE 33,40
    : POKE 34,20
    : POKE 35,23
    : VTAB 22
11410 FOR I = 1 TO 4000
    : NEXT 
    : I =  FRE (0)
11490 RETURN 
11500 REM    DATA
11510 DATA   96,180,128,60,114,60,128,120,144,60,152,60,128,255,128,60,114,60,85,120,96,60,102,60,114,120,102,60,96,255,0,0
11520 DATA   ,,,,,,L;LL;,L;LL;,;;LL;;,L;LL;,L;;;;,,,;L;;;;,;L;LL;,;L;LL;,;L;LL;,;;;LL;,,,,,*
11530 DATA   96,16,85,4,128,4,96,4,76,4,128,4,96,16,0,0
11540 DATA   114,120,144,60,114,255,1,120,128,120,144,60,128,120,114,60,144,120,171,255,228,255,0,0
11550 DATA   152,180,152,120,152,60,144,120,152,60,171,120,192,60,152,255,0,0
11560 DATA   0,160,128,255,152,40,171,80,192,40,228,255,1,40,0,160,192,255,192,40,171,80,152,40,128,255,0,0
11700 REM    MUSIC
11710 READ I,J
    : IF J = 0 THEN  RETURN 
11720 POKE 768,I
    : POKE 769,J
    : CALL 770
11730 GOTO 11710
12000 REM    TITLE PAGE
12100 TEXT 
    : HOME 
12110 PRINT "HI!  WELCOME TO LEMONSVILLE, CALIFORNIA!"
    : PRINT 
12115 FOR I = 1 TO 10
    : J =  RND (1) * 25 + 50
    : POKE 768,J
    : POKE 769,10
    : CALL 770
    : NEXT 
12120 PRINT "IN THIS SMALL TOUN, YOU ARE IN CHARGE OF"
12130 PRINT "RUNNING YOUR OWN LEMONADE STAND. YOU CAN"
12140 PRINT "COMPETE WITH AS MANY OTHER PEOPLE AS YOU"
12150 PRINT "WISH, BUT HOW MUCH PROFIT YOU MAKE IS UP"
12160 PRINT "TO YOU (THE OTHER STANDS' SALES WILL NOT"
12170 PRINT "AFFECT YOUR BUSINESS IN ANY WAY). IF YOU"
12180 PRINT "MAKE THE MOST MONEY, YOUR THE WINNER!!"
    : PRINT 
12190 PRINT "ARE YOU STARTING A NEW GAME? (YES OR NO)"
12200 VTAB 21
    : CALL  - 958
    : INPUT "TYPE YOUR ANSWER AND HIT RETURN ==> ";A$
12210 A$ =  LEFT$ (A$,1)
    : IF A$ <  > "Y" AND A$ <  > "N" THEN  PRINT  CHR$ (7);
    : GOTO 12200
12220 VTAB 23
    : CALL  - 958
    : INPUT "HOW MANY PEOPLE WILL BE PLAYING ==> ";N$
12230 N =  VAL (N$)
    : IF N < 1 OR N > 30 THEN  PRINT  CHR$ (7);
    : GOTO 12220
12240 RETURN 
13000 REM    NEW BUSINESS
13100 HOME 
13110 PRINT "TO MANAGE YOUR LEMONADE STAND, YOU WILL "
13120 PRINT "NEED TO MAKE THESE DECISIONS EVERY DAY: "
    : PRINT 
13130 PRINT "1. HOW MANY GLASSES OF LEMONADE TO MAKE    (ONLY ONE BATCH IS MADE EACH MORNING)"
13140 PRINT "2. HOW MANY ADVERTISING SIGNS TO MAKE      (THE SIGNS COST FIFTEEN CENTS EACH)  "
13150 PRINT "3. WHAT PRICE TO CHARGE FOR EACH GLASS  "
    : PRINT 
13160 PRINT "YOU WILL BEGIN WITH $2.00 CASH (ASSETS)."
13170 PRINT "BECAUSE YOUR MOTHER GAVE YOU SOME SHUGAR,"
13180 PRINT "YOUR COST TO MAKE LEMONADE IS TWO CENTS "
13190 PRINT "A GLASS (THIS MAY CHANGE IN THE FUTURE)."
    : PRINT 
13200 GOSUB 18000
13202 HOME 
13210 PRINT "YOUR EXPENSES ARE THE SUM OF THE COST OF"
13220 PRINT "THE LEMONADE AND THE COST OF THE SIGNS. "
    : PRINT 
13230 PRINT "YOUR PROFITS ARE THE DIFFERENCE BETWEEN "
13240 PRINT "THE INCOME FROM SALES AND YOUR EXPENSES."
    : PRINT 
13250 PRINT "THE NUMBER OF GLASSES YOU SELL EACH DAY "
13260 PRINT "DEPENDS ON THE PRICE YOU CHARGE, AND ON "
13270 PRINT "THE NUMBER OF ADVERTISING SIGNS YOU USE."
    : PRINT 
13280 PRINT "KEEP TRACK OF YOUR ASSETS, BECAUSE YOU  "
13290 PRINT "CAN'T SPEND MORE MONEY THAN YOU HAVE!   "
    : PRINT 
13300 GOSUB 18000
13302 HOME 
    : RETURN 
14000 REM    CONTINUE OLD GAME
14100 CALL  - 936
    : PRINT  CHR$ (7);
    : I = 0
14110 PRINT "HI AGAIN!  WELCOME BACK TO LEMONSVILLE! "
    : PRINT 
14120 PRINT "LET'S CONTINUE YOUR LAST GAME FROM WHERE"
14130 PRINT "YOU LEFT IT LAST TIME.  DO YOU REMEMBER "
14140 PRINT "WHAT DAY NUMBER IT WAS? ";
14150 INPUT "";A$
    : A =  VAL (A$)
    : PRINT 
    : IF A <  > 0 THEN 14200
14160 A$ =  LEFT$ (A$,1)
    : IF A$ = "Y" THEN  PRINT "GOOD!  WHAT DAY WAS IT? ";
    : I = I + 1
    : GOTO 14150
14170 IF A$ = "N" OR I > 0 THEN 14300
14180 PRINT  CHR$ (7);"YES OR NO? ";
    : I = I + 1
    : GOTO 14150
14200 IF A < 1 OR A > 99 OR A <  >  INT (A) THEN 14300
14210 D = A
14300 PRINT "OKAY - WE'LL START WITH DAY NO. ";D + 1
    : PRINT 
14400 FOR I = 1 TO N
    : PRINT 
    : PRINT 
14410 PRINT "PLAYER NO. ";I;", HOW MUCH MONEY (ASSETS)"
    : PRINT 
14420 PRINT "DID YOU HAVE? ";
14430 INPUT "";A$
    : A =  VAL (A$)
    : PRINT 
14440 IF A < 2 THEN  PRINT "O.K. - E'LL START YOU OUT WITH $2.00"
    : A = 2
    : GOTO 14490
14450 IF A > 40 THEN  PRINT "JUST TO BE FAIR, LET'S MAKE THAT $10.00"
    : A = 10
14490 A(I) =  INT (A * 100 + .5) / 100
    : NEXT 
14500 PRINT 
    : PRINT  CHR$ (7)
    : INPUT "...READY TO BEGIN? ";A$
14510 IF  LEFT$ (A$,1) = "N" THEN 13000
14520 RETURN 
15000 REM    WEATHER DISPLAY
15100 GOTO 15175
15110 COLOR= SC
    : FOR I = 0 TO 25
    : HLIN 0,39 AT I
    : NEXT I
15120 COLOR= 12
    : IF SC = 5 THEN  COLOR= 4
15125 FOR I = 26 TO 39
    : HLIN 0,39 AT I
    : NEXT I
15130 COLOR= 8
    : FOR I = 24 TO 32
    : HLIN 15,25 AT I
    : NEXT I
    : HLIN 15,25 AT 14
    : VLIN 14,23 AT 15
    : VLIN 14,23 AT 25
15150 COLOR= 13
    : FOR I = 17 TO 23 STEP 2
    : VLIN 22,23 AT I
    : NEXT I
15151 IF SC = 2 OR SC = 7 THEN 15160
15152 IF SC = 10 THEN  COLOR= 15
15157 IF SC = 5 THEN  COLOR= 0
15158 HLIN 6,10 AT 2
    : HLIN 4,14 AT 3
    : HLIN 7,12 AT 4
    : HLIN 22,30 AT 4
    : HLIN 20,36 AT 5
    : HLIN 23,33 AT 6
    : IF SC = 5 THEN  GOSUB 17000
15159 GOTO 15170
15160 IF SC = 7 THEN  COLOR= 9
15162 HLIN 3,5 AT 1
    : HLIN 2,6 AT 2
    : FOR I = 3 TO 6
    : HLIN 1,7 AT I
    : NEXT 
    : HLIN 2,6 AT 7
    : HLIN 3,5 AT 8
15175 VTAB 22
    : HTAB 8
    : PRINT "LEMONSVILLE WETHER REPORT"
    : PRINT 
15180 IF SC = 2 THEN  HTAB 18
    : PRINT " SUNNY ";
15182 IF SC = 7 THEN  HTAB 15
    : PRINT " HOT AND DRY ";
15184 IF SC = 10 THEN  HTAB 17
    : PRINT " CLOUDY ";
15186 IF SC = 5 THEN  HTAB 14
    : PRINT " THUNDERSTORMS! ";
    : GOSUB 17000
15200 RESTORE
15210 READ A$
    : IF A$ <  > "*" THEN 15210
15220 IF SC <  > 2 THEN 15300
15230 READ I,J
    : IF J = 0 THEN 15500
15240 FOR K = 1 TO J
    : POKE 768,I
    : POKE 769,10
    : CALL 770
15250 FOR L = K TO J
    : NEXT 
    : NEXT 
    : GOTO 15230
15300 READ I,J
    : IF J <  > 0 THEN 15300
15310 IF SC = 7 THEN 15400
15320 READ I,J
    : IF J <  > 0 THEN 15320
15330 IF SC = 10 THEN 15400
15340 READ I,J
    : IF J <  > 0 THEN 15340
15400 READ I,J
    : IF J = 0 THEN 15500
15410 IF I = 1 THEN  FOR I = 1 TO J
    : NEXT 
    : GOTO 15400
15420 POKE 768,I
    : POKE 769,J
    : CALL 770
15430 GOTO 15400
15440 IF SC = 5 THEN  GOSUB 17000
15500 IF SC = 5 THEN  GOSUB 17000
15510 I =  FRE (0)
    : FOR I = 1 TO 200
    : NEXT 
    : RETURN 
17000 RETURN 
17104 FOR J = 1 TO 6
    : Y = I + K1
    : I = I + K3 +  RND (K1) * K5
    : X = X + K1 - K2 * ( RND (K1) > KP)
    : IF I > KTP AND X > KL THEN X = X - K2

18000 VTAB 24
    : PRINT " PRESS SPACE TO CONTINUE, ESC TO END...";
18005 POKE  - 16368,0
18010 GET IN$
    : IF IN$ <  > " " AND  ASC (IN$) <  > 27 THEN 18010
18020 IF  ASC (IN$) <  > 27 THEN  RETURN 
18021 HOME 
    : VTAB 8
    : PRINT "ARE YOU SURE YOU WANT TO QUIT ?";
18022 GET IN$
18023 IF IN$ = "Y" THEN 31111
18024 RETURN 
18030 RETURN 
31111 HOME 
31113 IF  PEEK (994) +  PEEK (1001) = 192 THEN  CALL 1002
31116 SERIAL = 31416
31126 REM  SERIAL NUMBER OF DISK
31136 POKE  - 16368,0
    : IF  PEEK (1020) =  INT (SERIAL / 256) AND  PEEK (1021) = SERIAL - ( INT (SERIAL / 256) * 256) AND  PEEK (1019) = 0 THEN 31156
31146 TEXT 
    : CALL  - 936
    : TEXT 
    : END 
31156 POKE 1020,0
    : POKE 1021,0
    : PRINT 
    : PRINT "RUN INDEX"
    : END 





*/