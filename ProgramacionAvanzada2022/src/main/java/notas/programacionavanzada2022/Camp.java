/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notas.programacionavanzada2022;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import javax.swing.JTextField;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 *
 * @author sergi
 */
public class Camp {
    //GUI Text Boxes
    private ThreadList entranceA;
    private ThreadList entranceB;
    private ThreadList camp;
    private ThreadList instructorRope; 
    private ThreadList instructorZipLine; 
    private ThreadList instructorSnack;
    private ThreadList childZipLineQueue;
    private ThreadList childZipLinePrep;
    private ThreadList childZipLineExec;
    private ThreadList childZipLineEnd;
    private ThreadList instructorCommonArea;
    private ThreadList childCommonArea;
    private ThreadList childRopeQueue;
    private ThreadList childRopeTeamA;
    private ThreadList childRopeTeamB;
    
    private ThreadList cleanTraysList;
    private ThreadList dirtyTraysList;
    private ThreadList childSnackQueue;
    private ThreadList childrenSnack;
    
    //Number variables used for concurrency
    private int totalCapacity;
    private int currentCapacity; 
    private int doorTurn;
    //Concurrent elements
    private Lock entryLock; 
    private Lock exitLock; 
    private Condition doorAclosed; 
    private Condition doorBclosed;
    private Boolean doorAopen;
    private Boolean doorBopen;
    private Semaphore childZipLineSem; 
    private CyclicBarrier zipLineBarrier;
    private CyclicBarrier ropeBarrier;
    private ArrayList<Child> ropeQueue;
    private ArrayList<Child> ropeTeamA; 
    private ArrayList<Child> ropeTeamB;
    private Semaphore childRopeSem;
    
    private AtomicInteger cleanTrays = new AtomicInteger();
    private AtomicInteger dirtyTrays = new AtomicInteger();
    private int clean;
    private int dirty;
    private Semaphore maxChildren;
    private Lock lockSnack;
    private Condition noClean;
    private Condition noDirty;
    
    
    public Camp(JTextField doorA, JTextField doorB, JTextField pCamp, JTextField instRope,
            JTextField instZip, JTextField instSnack, JTextField childZipQueue, 
            JTextField childZipPrep, JTextField childZipExec, JTextField childZipEnd,
            JTextField instCommonArea, JTextField cRopeQueue, JTextField cRopeA,
            JTextField cRopeB, JTextField cCommonArea, JTextField cCleanTrays, JTextField cDirtyTrays,
            JTextField cChildSnack, JTextField cChildrenSnack)
    {
        //GUI Text boxes set-up
        entranceA = new ThreadList(doorA); 
        entranceB = new ThreadList(doorB);
        camp = new ThreadList(pCamp); 
        instructorRope = new ThreadList(instRope);
        instructorZipLine = new ThreadList(instZip);
        instructorSnack = new ThreadList(instSnack);       
        childZipLineQueue = new ThreadList(childZipQueue); 
        childZipLinePrep = new ThreadList(childZipPrep); 
        childZipLineExec = new ThreadList(childZipExec); 
        childZipLineEnd = new ThreadList(childZipEnd); 
        instructorCommonArea = new ThreadList(instCommonArea); 
        childRopeQueue = new ThreadList(cRopeQueue); 
        childRopeTeamA = new ThreadList(cRopeA); 
        childRopeTeamB = new ThreadList(cRopeB); 
        childCommonArea = new ThreadList(cCommonArea);
        
        cleanTraysList = new ThreadList(cCleanTrays);
        dirtyTraysList = new ThreadList(cDirtyTrays);
        childSnackQueue = new ThreadList(cChildSnack);
        childrenSnack = new ThreadList(cChildrenSnack);
        //Number variables set-up
        this.totalCapacity = 50; 
        this.currentCapacity = 0; 
        this.doorTurn = 0; 
        //Concurrent elements set-up
        entryLock = new ReentrantLock(); 
        exitLock = new ReentrantLock();
        doorAclosed = entryLock.newCondition(); 
        doorBclosed = entryLock.newCondition();
        doorAopen = false; 
        doorBopen = false;
        childRopeSem = new Semaphore(10); 
        zipLineBarrier = new CyclicBarrier(2); 
        ropeBarrier = new CyclicBarrier(11); 
        childZipLineSem = new Semaphore(1, true);
        ropeTeamA = new ArrayList<Child>();
        ropeTeamB = new ArrayList<Child>();
        ropeQueue = new ArrayList<Child>();
        
        cleanTrays = new AtomicInteger(0);
        dirtyTrays = new AtomicInteger(25);
        maxChildren = new Semaphore(20);
        clean = 0;
        dirty = 25;
        lockSnack = new ReentrantLock();
        noClean = lockSnack.newCondition();
        noDirty = lockSnack.newCondition();
    }
    
    public void enterCampLeft(Instructor instruct)
    {
        entranceA.push(instruct.getInstructorName());
        try 
        {
            entryLock.lock();
            if (!doorAopen)
            {
                try 
                {
                  sleep((int)(500*Math.random() + 500));
                  doorAopen = true;
                }
                catch (InterruptedException e)
                { 
                    System.out.println(e.toString());
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }
        finally 
        {
            entryLock.unlock();
            entranceA.pop(instruct.getInstructorName());
        }  
    }
    
    public void activityRope (Instructor instruct)
    {
        instructorRope.push(instruct.getInstructorName());
        try 
        {
            ropeBarrier.await();
            while(ropeQueue.size() > 0)
            {
                int team = (int)Math.floor(Math.random()*(1-0+1)+0);
                if (team == 0 && ropeTeamA.size()<5)
                {
                    ropeTeamA.add(ropeQueue.get(0));
                    childRopeQueue.pop(ropeQueue.get(0).getChildName());
                    childRopeTeamA.push(ropeQueue.get(0).getChildName());
                    ropeQueue.remove(0);
                }
                else
                {
                    ropeTeamB.add(ropeQueue.get(0));
                    childRopeQueue.pop(ropeQueue.get(0).getChildName());
                    childRopeTeamB.push(ropeQueue.get(0).getChildName());
                    ropeQueue.remove(0);
                }
            }
            System.out.println("Team A: " + ropeTeamA.size());
            System.out.println("Team B: " + ropeTeamB.size());
            sleep(3000);
            int winningTeam = (int)Math.floor(Math.random()*(1-0+1)+0);
            if (winningTeam == 0)
            {
                System.out.println("Team A wins");
                while(ropeTeamA.size()>0)
                {
                    ropeTeamA.get(0).setTotalActivities(ropeTeamA.get(0).getTotalActivities() + 2);
                    childRopeTeamA.pop(ropeTeamA.get(0).getChildName());
                    ropeTeamA.remove(0);
                }
                while(ropeTeamB.size()>0)
                {
                    ropeTeamB.get(0).setTotalActivities(ropeTeamB.get(0).getTotalActivities() + 1);
                    childRopeTeamB.pop(ropeTeamB.get(0).getChildName());
                    ropeTeamB.remove(0);
                }
            }
            else
            {
                System.out.println("Team B wins");
                while(ropeTeamA.size()>0)
                {
                    ropeTeamA.get(0).setTotalActivities(ropeTeamA.get(0).getTotalActivities() + 1);
                    childRopeTeamA.pop(ropeTeamA.get(0).getChildName());
                    ropeTeamA.remove(0);
                }
                while(ropeTeamB.size()>0)
                {
                    ropeTeamB.get(0).setTotalActivities(ropeTeamB.get(0).getTotalActivities() + 2);
                    childRopeTeamB.pop(ropeTeamB.get(0).getChildName());
                    ropeTeamB.remove(0);
                }
            }
            ropeBarrier.await();
        }
        catch (Exception e)
        { 
            System.out.println(e.toString());
        }
        instructorRope.pop(instruct.getInstructorName());
    }
    
    public void activityZipLine (Instructor instruct)
    {
        instructorZipLine.push(instruct.getInstructorName());
        try
        {
            zipLineBarrier.await();
            try 
            {
                sleep(1000); //Activities (ZIPLINE, ROPE, SNACK)
            }
            catch (InterruptedException e)
            { 
                System.out.println(e.toString());
            }
            zipLineBarrier.await();
            zipLineBarrier.await();
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        instruct.setInstructorActivitiesDone(instruct.getInstructorActivitiesDone()+1);
        instructorZipLine.pop(instruct.getInstructorName());
    }
    /*
    public void activitySnack (Instructor instruct)
    {
        instructorSnack.push(instruct.getInstructorName());
        try 
        {
            sleep(2000); //Activities (ZIPLINE, ROPE, SNACK)
        }
        catch (InterruptedException e)
        { 
            System.out.println(e.toString());
        }
        instructorSnack.pop(instruct.getInstructorName());
    }
    */
    public void commonArea (Instructor instruct)
    {
        instructorCommonArea.push(instruct.getInstructorName());
        try 
        {
            sleep((int)(1000*Math.random() + 1000));
        }
        catch (InterruptedException e)
        { 
            System.out.println(e.toString());
        }
        instructorCommonArea.pop(instruct.getInstructorName());
    }
    
    public void enterCampRight(Instructor instruct)
    {
        entranceB.push(instruct.getInstructorName());
        try 
        {
            entryLock.lock();
            if (!doorBopen)
            {
                try 
                {
                  sleep((int)(500*Math.random() + 500));
                  doorBopen = true;
                }
                catch (InterruptedException e)
                { 
                    System.out.println(e.toString());
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }
        finally 
        {
            entryLock.unlock();
            entranceB.pop(instruct.getInstructorName());
        }  
    }
    
    public void enterCampLeft(Child c)
    {
        entranceA.push(c.getChildName());
        try 
        {
            entryLock.lock();
            while (!doorAopen)
            {
                try 
                {
                    doorAclosed.await();
                }
                catch (Exception e)
                {
            System.out.println(e.toString());
                }  
            }
            currentCapacity = currentCapacity + 1; 
            if (currentCapacity == totalCapacity)
            {
                doorAopen = false;
                doorBopen = false;
            }
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }
        finally 
        {
            //System.out.println("Entered through 0");
            entryLock.unlock();
            entranceA.pop(c.getChildName());
        }  
    }
    
    public void enterCampRight(Child c)
    {
        entranceB.push(c.getChildName());
        try 
        {
            entryLock.lock();
            while (!doorBopen)
            {
                try 
                {
                    doorBclosed.await();
                }
                catch (Exception e)
                {
            System.out.println(e.toString());
                }  
            }
            currentCapacity = currentCapacity + 1; 
            if (currentCapacity == totalCapacity)
            {
                doorAopen = false;
                doorBopen = false; 
            }
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }
        finally 
        {
            //System.out.println("Entered through 1");
            entryLock.unlock();
            entranceB.pop(c.getChildName());
        }  
    }
    
    public void leaveCamp (Child c)
    {
        try 
        {
           entryLock.lock(); 
           currentCapacity = currentCapacity - 1; 
           if (entranceA.getThreadList().size() > 0 && entranceB.getThreadList().size() > 0)
           {
               if (doorTurn == 0)
                {
                    doorTurn = 1;
                    doorAopen = true; 
                    doorAclosed.signalAll();
                }
                else 
                {
                    doorTurn = 0; 
                    doorBopen = true;
                    doorBclosed.signalAll();
                }
           }
           else if (entranceA.getThreadList().size() > 0)
           {
               doorAopen = true; 
               doorAclosed.signalAll();
           }
           else 
           {
               doorBopen = true; 
               doorBclosed.signalAll();
           }
           
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }
        finally 
        {
            entryLock.unlock(); 
        }
    }
    
    public void activityZipLine (Child c)
    {
        childZipLineQueue.push(c.getChildName());
        try
        {
            childZipLineSem.acquire();
            childZipLineQueue.pop(c.getChildName());
            childZipLinePrep.push(c.getChildName());
            try
            {
                zipLineBarrier.await();
                zipLineBarrier.await();
                childZipLinePrep.pop(c.getChildName());
                childZipLineExec.push(c.getChildName());
                try 
                {
                    sleep(3000);
                }
                catch (InterruptedException e)
                { 
                    System.out.println(e.toString());
                }
                childZipLineExec.pop(c.getChildName());
                childZipLineEnd.push(c.getChildName());
                try 
                {
                    sleep(500); //Activities (ZIPLINE, ROPE, SNACK)
                }
                catch (InterruptedException e)
                { 
                    System.out.println(e.toString());
                }
                zipLineBarrier.await();
                childZipLineEnd.pop(c.getChildName());
                
            }
            catch(Exception e)
            {
                System.out.println(e.toString());
            }
        }
        catch(InterruptedException ie)
        {
             System.out.println(ie.toString());       
        }
        finally
        {
            childZipLineSem.release();
            c.setTotalActivities(c.getTotalActivities()+1);
        }
    }
    
    public void activityRope (Child c)
    {
        if(childRopeSem.tryAcquire())
        {
            childRopeQueue.push(c.getChildName());
            try
            {
                ropeQueue.add(c);
                ropeBarrier.await();
                ropeBarrier.await();
            }
            catch (Exception e)
            {
                System.out.println(e.toString());
            }
            finally
            {
                childRopeSem.release(); 
            }
        }
    }
    
    public void commonArea (Child c)
    {
        childCommonArea.push(c.getChildName());
        try 
        {
            sleep((int)(2000*Math.random() + 2000));
        }
        catch (InterruptedException e)
        { 
            System.out.println(e.toString());
        }
        childCommonArea.pop(c.getChildName());
    }
    
    public void SnackEat(Child i) throws InterruptedException{
        
        try {
            lockSnack.lock();
            while (cleanTrays.get() < 1) {
                childSnackQueue.push(i.getChildName());
                noClean.await(); //waits for a signal
            }try {
                maxChildren.acquire();
                
                childSnackQueue.pop(i.getChildName());
                childrenSnack.push(i.getChildName());
                
                cleanTraysList.pop(Integer.toString(cleanTrays.get()));
                //clean = previousClean();
                cleanTraysList.push(Integer.toString(cleanTrays.decrementAndGet()));
                
                sleep(7000);
                
                childrenSnack.pop(i.getChildName());
                dirtyTraysList.pop(Integer.toString(dirtyTrays.get()));
                //dirty = nextDirty();
                dirtyTraysList.push(Integer.toString(dirtyTrays.incrementAndGet()));
                
                noDirty.signalAll();
                
            } catch (Exception e){}
        } finally {
            lockSnack.unlock();
            maxChildren.release();
        } 
    }
    public void SnackClean(Instructor i) throws InterruptedException{
        instructorSnack.push(i.getInstructorName());
        
        try {
            lockSnack.lock(); // mutual exclusion code that changes state
            while(dirtyTrays.get() < 1){
                noDirty.await();
            }try{
                int n = (int)Math.floor(Math.random()*(1-0+2)+3); //Random number between 3-5
                sleep(n*1000);
                
                dirtyTraysList.pop(Integer.toString(dirtyTrays.get()));
                //dirty = previousDirty();
                dirtyTraysList.push(Integer.toString(dirtyTrays.decrementAndGet()));
                
                cleanTraysList.pop(Integer.toString(cleanTrays.get()));
                //clean = nextClean();
                cleanTraysList.push(Integer.toString(cleanTrays.incrementAndGet()));
                
                noClean.signalAll();
                
            } catch (Exception e){}
        }finally {
            lockSnack.unlock();
        } 
        instructorSnack.pop(i.getInstructorName());
    }
    
    public int nextClean(){
        return cleanTrays.getAndIncrement();
    }
    
    public int nextDirty(){
        return dirtyTrays.getAndIncrement();
    }
    
    public int previousClean(){
        return cleanTrays.getAndDecrement();
    }
    
    public int previousDirty(){
        return dirtyTrays.getAndDecrement();
    }
}
