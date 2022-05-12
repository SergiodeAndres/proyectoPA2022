
package Part2;

import Part1.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Instructor extends Thread
{
    
    private String instructorName;
    private Camp instructorCamp;
    private String instructorActivity; 
    private int instructorActivitiesDone;
    private ArrayList<String> possibleActivities; 

    //Constructor
    public Instructor(String paramInstructorName, 
            Camp paramInstructorCamp, 
            ArrayList<String> paramInstructorActivities) 
    {
        this.instructorName = paramInstructorName;
        this.instructorCamp = paramInstructorCamp;
        this.instructorActivitiesDone = 0; 
        this.possibleActivities = paramInstructorActivities; 
    }
    
    public void run()
    {
        //Synchronized block to choose which instructor gets which activity
        synchronized(possibleActivities)
        {
            /**Each instructor shuffles the list of activities like cards and 
               takes the first activity**/
            Collections.shuffle(possibleActivities); 
            instructorActivity = possibleActivities.get(0); 
            possibleActivities.remove(0);
        } 
        /**Instructors enter the camp; even instructors through the left
           Odds through the right**/
        if (instructorName.charAt(1) % 2 == 0)
        {
            instructorCamp.enterCampLeft(this);
        }
        else 
        {
            instructorCamp.enterCampRight(this);
        }
        //Instructors now begin their activity cycle
        while(true)
        {
            while (instructorActivitiesDone < 10)
            {
                /**They repeat their assigned activity 10 times
                   Then go to the common area and repeat**/
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
                    instructorCamp.SnackClean(this);
                }
            }
            instructorActivitiesDone = 0; 
            instructorCamp.commonArea(this);
        }
    }

    //Getters and Setters
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
