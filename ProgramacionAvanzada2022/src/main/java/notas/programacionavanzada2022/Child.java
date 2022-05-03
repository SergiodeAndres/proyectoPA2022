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
    private int nonSnackActivities; 
    private Camp childCamp; 
    
    public Child (String cName, Camp camp)
    {
        this.childName = cName; 
        this.totalActivities = 0; 
        this.nonSnackActivities = 0;
        this.childCamp = camp; 
    }

    public String getChildName() {
        return childName;
    }

    public int getTotalActivities() {
        return totalActivities;
    }

    public int getNonSnackActivies() {
        return nonSnackActivities;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public void setTotalActivities(int totalActivities) {
        this.totalActivities = totalActivities;
    }

    public void setNonSnackActivies(int nonSnackActivies) {
        this.nonSnackActivities = nonSnackActivies;
    }
    
    public void run ()
    {
        int entrance = (int)Math.floor(Math.random()*(1-0+1)+0);
        if (entrance == 0)
        {
            childCamp.enterCampLeft(this);
        }
        else 
        {
            childCamp.enterCampRight(this);
        }
        try 
        {
            sleep(10000); //Activities (ZIPLINE, ROPE, SNACK)
            while(totalActivities < 15){
                int activity = 0; //numero aleatorio
                if (activity == 0 && nonSnackActivities >= 3){
                    totalActivities += 1; //SNACK
                }else{
                    if (activity == 1){
                        totalActivities += 1; //ROPE
                    }else{
                        if (activity == 2){
                            totalActivities += 1; //ZIPLINE
                        }
                    }
                }
            }
        }
        catch (InterruptedException e)
        { 
            System.out.println(e.toString());
        }
        childCamp.leaveCamp(this);
    }
}
