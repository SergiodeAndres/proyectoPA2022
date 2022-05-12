package Part2;

import Part1.*;


public class Child extends Thread
{
    private String childName; 
    private int totalActivities; 
    private int nonSnackActivities; 
    private Camp childCamp; 
    
    //Constructor
    public Child (String paramChildName, Camp paramCamp)
    {
        this.childName = paramChildName; 
        this.totalActivities = 0; 
        this.nonSnackActivities = 0;
        this.childCamp = paramCamp; 
    }
    
    public void run ()
    {
        //Choose a random entrace for the child
        int entrance = (int)Math.floor(Math.random()*(1-0+1)+0);
        if (entrance == 0)
        {
            childCamp.enterCampLeft(this);
        }
        else 
        {
            childCamp.enterCampRight(this);
        }
            while(totalActivities < 15)
            {
                //Choose random activity for the child
                int activity = (int)Math.floor(Math.random()*(1-0+2)+0); 
                if (activity == 0 && nonSnackActivities >= 3)
                {
                    /**Go to the snack if valid, add one activity
                    set nonSnackActivities to 0, the child won't be able 
                    to return to the snack until 3 other activities have been 
                    done, then go to the common area**/
                    childCamp.SnackEat(this);
                    totalActivities += 1; 
                    nonSnackActivities = 0;
                    childCamp.commonArea(this);
                }
                else if (activity == 1)
                {
                    /**Go to the rope activity, then add 1 
                     to nonSnackActivities then go to the common area**/
                    childCamp.activityZipLine(this);
                    childCamp.commonArea(this);
                }
                else
                {
                    /**Go to the zipline activity, then add 1 
                     to nonSnackActivities then go to the common area**/
                    childCamp.activityRope(this);
                    childCamp.commonArea(this);
                }
            }
        childCamp.leaveCamp(this);
    }
    
    //Getters and Setters
    public String getChildName() 
    {
        return childName;
    }

    public int getTotalActivities() 
    {
        return totalActivities;
    }

    public int getNonSnackActivies() 
    {
        return nonSnackActivities;
    }

    public void setChildName(String childName) 
    {
        this.childName = childName;
    }

    public void setTotalActivities(int totalActivities) 
    {
        this.totalActivities = totalActivities;
    }

    public void setNonSnackActivies(int nonSnackActivies) 
    {
        this.nonSnackActivities = nonSnackActivies;
    }
}
