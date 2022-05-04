/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notas.programacionavanzada2022;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sergi
 */
public class Instructor extends Thread{
    
    private String instructorName;
    private Camp instructorCamp;
    private String instructorActivity; 
    private int instructorActivitiesDone;
    private ArrayList<String> possibleActivities; 

    public Instructor(String instructorName, Camp instructorCamp, ArrayList<String> instructorActivities) 
    {
        this.instructorName = instructorName;
        this.instructorCamp = instructorCamp;
        this.instructorActivitiesDone = 0; 
        this.possibleActivities = instructorActivities; 
    }
    
    public void run()
    {
        synchronized(possibleActivities)
        {
            Collections.shuffle(possibleActivities); 
            instructorActivity = possibleActivities.get(0); 
            possibleActivities.remove(0);
            System.out.println("Instructor " + instructorName + " got activity " + instructorActivity);
        } 
        if (instructorName.charAt(1) % 2 == 0)
        {
            instructorCamp.enterCampLeft(this);
        }
        else 
        {
            instructorCamp.enterCampRight(this);
        }
        while(true)
        {
            while (instructorActivitiesDone < 3)
            {
                if (instructorActivity.equals("RopeActivity"))
                {
                    instructorCamp.activityRope(this);
                }
                else if (instructorActivity.equals("Zipline"))
                {
                    instructorCamp.activityZipLine(this);
                }
                else 
                {
                    try {
                        instructorCamp.SnackClean(this);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Instructor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            instructorActivitiesDone = 0; 
            instructorCamp.commonArea(this);
        }
    }

    public String getInstructorName() 
    {
        return instructorName;
    }

    public void setInstructorName(String instructorName) 
    {
        this.instructorName = instructorName;
    }

    public Camp getInstructorCamp() 
    {
        return instructorCamp;
    }

    public void setInstructorCamp(Camp instructorCamp) 
    {
        this.instructorCamp = instructorCamp;
    }

    public String getInstructorActivity() 
    {
        return instructorActivity;
    }

    public void setInstructorActivity(String instructorActivity) 
    {
        this.instructorActivity = instructorActivity;
    }

    public int getInstructorActivitiesDone() 
    {
        return instructorActivitiesDone;
    }

    public void setInstructorActivitiesDone(int instructorActivitiesDone) 
    {
        this.instructorActivitiesDone = instructorActivitiesDone;
    }  
}
