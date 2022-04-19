/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notas.programacionavanzada2022;

/**
 *
 * @author sergi
 */
public class Child extends Thread{
    private String childName; 
    private int totalActivities; 
    private int nonSnackActivies; 
    private Camp childCamp; 
    
    public Child (String cName, Camp camp)
    {
        this.childName = cName; 
        this.totalActivities = 0; 
        this.nonSnackActivies = 0;
        this.childCamp = camp; 
    }

    public String getChildName() {
        return childName;
    }

    public int getTotalActivities() {
        return totalActivities;
    }

    public int getNonSnackActivies() {
        return nonSnackActivies;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public void setTotalActivities(int totalActivities) {
        this.totalActivities = totalActivities;
    }

    public void setNonSnackActivies(int nonSnackActivies) {
        this.nonSnackActivies = nonSnackActivies;
    }
    
    public void run ()
    {
        childCamp.enterCamp(this);
        try 
        {
            sleep(10000); //Activities (ZIPLINE, ROPE, SNACK)
        }
        catch (InterruptedException e)
        { 
            System.out.println(e.toString());
        }
        childCamp.leaveCamp(this);
    }
}
