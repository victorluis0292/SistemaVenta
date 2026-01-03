/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

/**
 *
 * @author vic
 */
public class CashInBox {
    private int id;
    private double amount;
    
    //constructor
    //public  CashInBox(){}
    //constructor
    public CashInBox(double amount){
    this.amount=amount;
    }
    //Getters and Setters
    
    public int getId(){
    return id;
    }
    public void setid(){
    this.id=id;
    }
    public double getAmount(){
    return amount;
    }
    public void setAmount(double amount){
    this.amount=amount;
    }
}
