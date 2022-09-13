/*
 * Player Data Model
 */
package com.maehem.lemonade;

/**
 *
 * @author Mark J. Koch
 */
public class LemonadePlayer {

    private double sti = 0; // Daily Funds Cache
    private double a = LemonadeController.A2;//  Available funds
    private int l = 0;      // Glasses to make
    private int s = 0;      // Signs for advertising.
    private int p = 0;      // Price to charge for lemonate (cents)
    private int b = 0;      // Bankrupt Flag. 1=bankrupt, 0=normal  Could be boolean or check (a[] > 0).
    private int h = 0;      // Freak thunderstorm. Per player. Not working. TODO: Boolean
    private double g = 1.0;   // Gain. Effects Glasses Sold.  Normal = 1. Thunderstorm = 0; Maybe for game hardness/progression? 
    private int glassesSold = 0; // Glasses sold the previous day.
    private double profit = 0; // Profit of the previous day.
    private double income = 0;  // Income of the previous day.
    private double expenses = 0; // Expenses of the previous day.
    
    /**
     * Get available funds (balance).
     * 
     * @return amount in dollars.
     */
    public double getA() {
        return a;
    }
    
    /**
     * Withdraw amount from player balance in Dollars.
     * 
     * @param amt amount to take in dollars.
     * @return false if withdraw amount is larger than balance.
     */
    public boolean takeA( double amt ) {
        if ( amt > a ) return false;
        a -= amt;
        return true;
    }
    
    /**
     * Deposit amount into player balance.
     * 
     * @param amt
     * @return false if amount is negative.
     */
    public boolean depositA( double amt ) {
        if ( amt < 0.0 ) return false;
        a += amt;
        return true;
    }
    
    /**
     * Set player as bankrupt and zero balance.
     */
    public void setBankrupt() {
        a = 0;
        b = 1;
    }
    
    /**
     * Get number of glasses to make.
     * 
     * @return number of glasses.
     */
    public int getL() {
        return l;
    }
    
    /**
     * Number of glasses to make.
     * 
     * @param num of glasses to make. 0 - 1000.
     * @return false if number out of range.
     */
    public boolean setL( int num ) {
        if ( num < 0 || num > 1000 )  return false;
        l = num;
        return true;
    }
    
    /**
     * Get number of signs to make.
     * 
     * @return number of signs.
     */
    public int getS() {
        return s;
    }
    
    /**
     * Set number of signs to make.
     * 
     * @param num of signs to make.  0 - 50.
     * @return false if invalid number of signs.
     */
    public boolean setS( int num ) {
        if ( num < 0 || num > 50 )  return false;
        s = num;
        return true;
    }
    
    /**
     * Price per glass.
     * 
     * @return price in cents.
     */
    public int getP() {
        return p;
    }
    
    /**
     * Set price per glass
     * 
     * @param num in cents 0 - 100
     * @return false on value out of range.
     */
    public boolean setP( int num ) {
        if ( num < 0 || num > 100 )  return false;
        p = num;
        return true;
    }
   
    /**
     * Bankrupt
     * 
     * @return greater than zero if bankrupt.
     */
    public int getB() {
        return b;
    }
    
    /**
     * Set if player bankrupt.  1=bankrupt, 0=not-bankrupt.
     * 
     * @param val 
     */
    public void setB( int val ) {
        b = val;
    }
    
    /**
     * Per player freak thunderstorm/catastrophy.  Not Used.
     * @return greater than zero if thunderstorm.
     */
    public int getH() {
        return h;
    }
    
    /**
     * Per player freak thunderstorm/catastrophy. Not used.
     * @param val 0=no thunderstorm,  1=thunderstorm.
     */
    public void setH( int val ) {
        h = val;
    }
    
    /**
     * Gain.  Sales multiplier. Usually=1.  Thunderstorm=0. 
     * @return gain multiplier
     */
    public double getG() {
        return g;
    }
    
    /**
     * Gain multiplier for sales. Normal=1,  Thunderstorm=0
     * 
     * @param amt value to set.
     * @return 
     */
    public boolean setG( double amt ) {
        if ( amt < 0.0 || amt > 5.0 ) return false;
        g = amt;
        return true;
    }
    
    /**
     * Day (or previous day) glasses sold.
     * @return number of glasses sold
     */
    public int getGlassesSold() {
        return glassesSold;
    }
    
    /**
     * Set day or (previous day) glasses sold.
     * @param n number of glasses.
     */
    public void setGlassesSold( int n) {
        glassesSold = n;
    }
    
    /**
     * Copy of available balance that is manipulated during daily sales tabulation.
     * 
     * @return current amount.
     */
    public double getSTI() {
        return sti;
    }
    
    /**
     * Load this amount into STI. Overwrites any previous amount.
     * @param amt in dollars.
     */
    public void setSTI(double amt) {
        this.sti = amt;
    }
    
    /**
     * Remember profit for previous day.
     * 
     * @param amt amount to set
     * @return false if amount is less than zero.
     */
    public boolean setProfit( double amt ) {
        
        this.profit = amt;
        return true;
    }
    
    /**
     * Get previous run profits.
     * 
     * @return 
     */
    public double getProfit() {
        return this.profit;
    }
    
    /**
     * @return the income
     */
    public double getIncome() {
        return income;
    }

    /**
     * @param amt the income to set
     * @return true if amount to set was greater than zero.
     */
    public boolean setIncome(double amt) {
        if ( amt < 0 ) return false;
        this.income = amt;
        return true;
    }

    /**
     * @return the expenses
     */
    public double getExpenses() {
        return expenses;
    }

    /**
     * @param amt the expenses to set
     * @return  true if amt is greater than zero.
     */
    public boolean setExpenses(double amt) {
        if ( amt < 0 ) return false;
        this.expenses = amt;
        return true;
    }
}
