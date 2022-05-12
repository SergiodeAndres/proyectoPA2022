package Part1;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import javax.swing.JTextField;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Camp {
    //GUI Text Boxes
    //-Entrances
    private ThreadList entranceA;
    private ThreadList entranceB;
    //-Rope Activity
    private ThreadList instructorRope; 
    private ThreadList childRopeQueue;
    private ThreadList childRopeTeamA;
    private ThreadList childRopeTeamB;
    //-Zipline Activity
    private ThreadList instructorZipLine; 
    private ThreadList childZipLineQueue;
    private ThreadList childZipLinePrep;
    private ThreadList childZipLineExec;
    private ThreadList childZipLineEnd;
    //-Snack
    private ThreadList instructorSnack;
    private ThreadList cleanTraysList;
    private ThreadList dirtyTraysList;
    private ThreadList childSnackQueue;
    private ThreadList childrenSnack;
    //-Common Area
    private ThreadList instructorCommonArea;
    private ThreadList childCommonArea;
    //Number variables used for concurrency
    private int totalCapacity;
    private int currentCapacity; 
    private int doorTurn;
    //Elements for synchronization and communication
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
    private Semaphore maxChildren;
    private Lock lockSnack;
    private Condition noClean;
    private Condition noDirty;
    private Semaphore cleaned;
    private Semaphore dirtied;
    //Log elements
    private Semaphore fileSem;
    private File archivo;
    private FileWriter escribir;
    private PrintWriter line;
    
    //Constructor
    public Camp(JTextField paramDoorA, JTextField paramDoorB, 
            JTextField paramInstRope, JTextField paramInstZip, 
            JTextField paramInstSnack, JTextField paramChildZipQueue, 
            JTextField paramChildZipPrep, JTextField paramChildZipExec, 
            JTextField paramChildZipEnd,JTextField paramInstCommonArea, 
            JTextField paramCRopeQueue, JTextField paramCRopeA,
            JTextField paramCRopeB, JTextField paramChildCommonArea, 
            JTextField paramCleanTrays, JTextField paramDirtyTrays,
            JTextField paramChildSnack, JTextField paramChildrenSnack)
    {
        //GUI Text boxes set-up
        entranceA = new ThreadList(paramDoorA); 
        entranceB = new ThreadList(paramDoorB);
        instructorRope = new ThreadList(paramInstRope);
        instructorZipLine = new ThreadList(paramInstZip);
        instructorSnack = new ThreadList(paramInstSnack);       
        childZipLineQueue = new ThreadList(paramChildZipQueue); 
        childZipLinePrep = new ThreadList(paramChildZipPrep); 
        childZipLineExec = new ThreadList(paramChildZipExec); 
        childZipLineEnd = new ThreadList(paramChildZipEnd); 
        instructorCommonArea = new ThreadList(paramInstCommonArea); 
        childRopeQueue = new ThreadList(paramCRopeQueue); 
        childRopeTeamA = new ThreadList(paramCRopeA); 
        childRopeTeamB = new ThreadList(paramCRopeB); 
        childCommonArea = new ThreadList(paramChildCommonArea);
        cleanTraysList = new ThreadList(paramCleanTrays);
        dirtyTraysList = new ThreadList(paramDirtyTrays);
        childSnackQueue = new ThreadList(paramChildSnack);
        childrenSnack = new ThreadList(paramChildrenSnack);
        //Number variables set-up
        this.totalCapacity = 50; 
        this.currentCapacity = 0; 
        this.doorTurn = 0; 
        //Synchronization elements set-up
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
        lockSnack = new ReentrantLock();
        noClean = lockSnack.newCondition();
        noDirty = lockSnack.newCondition();
        cleaned = new Semaphore(0);
        dirtied = new Semaphore(25);
        cleanTraysList.push(cleanTrays.toString());
        dirtyTraysList.push(dirtyTrays.toString());
        //Log set-up
        archivo = new File("campEvolution.txt");
        fileSem = new Semaphore(1,true);
    }
    
    public void enterCampLeft(Instructor instruct)
    {
        entranceA.push(instruct.getInstructorName());
        try 
        {
            /**The instructor tries to open the door
               The lock makes sure no one else tries to access at the same time
             **/
            entryLock.lock();
            if (!doorAopen)
            {
                //If the door is not open, it will open it (takes 500ms)
                try 
                {
                    sleep((int)(500*Math.random() + 500));
                    doorAopen = true;
                    SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
                    String timesStamp = date.format(new Date());
                    String s = timesStamp+ ":" + " Instructor "+instruct.getInstructorName()+" opens DOOR 1";
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
            //The instructor enters the camp
            entryLock.unlock();
            entranceA.pop(instruct.getInstructorName());
        }
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp + ":" +" Instructor "+instruct.getInstructorName()+" enter through DOOR 1";
        writeFile(s);
    }
    
    public void enterCampRight(Instructor instruct)
    {
        entranceB.push(instruct.getInstructorName());
        try 
        {
            /**The instructor tries to open the door
               The lock makes sure no one else tries to access at the same time
             **/
            entryLock.lock();
            if (!doorBopen)
            {
                //If the door is not open, it will open it (takes 500ms)
                try 
                {
                    sleep((int)(500*Math.random() + 500));
                    doorBopen = true;
                    SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
                    String timesStamp = date.format(new Date());
                    String s = timesStamp+ ":" + " Instructor "+instruct.getInstructorName()+" opens DOOR 2";
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
            //The instructor enters the camp
            entryLock.unlock();
            entranceB.pop(instruct.getInstructorName());
        }
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp + ":" + " Instructor "+instruct.getInstructorName()+" enters through DOOR 2";
        writeFile(s);
    }
    
    public void activityRope (Instructor instruct)
    {
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp+ ":" + " Instructor "+instruct.getInstructorName()+" accesses ROPE ACTIVITY";
        writeFile(s);
        instructorRope.push(instruct.getInstructorName());
        try 
        {
            //In the barrier, it waits for 10 children to arrive
            ropeBarrier.await();
            while(ropeQueue.size() > 0)
            {
                //The instructor randomly assigns a team to each child
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
            sleep(3000);
            //The instructor decides who wins
            int winningTeam = (int)Math.floor(Math.random()*(1-0+1)+0);
            String s2children = "Children ";
            if (winningTeam == 0)
            {
                //If Team A wins, they receive 2 activities done, B only get 1
                while(ropeTeamA.size()>0)
                {
                    s2children += ropeTeamA.get(0).getChildName()+" ";
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
                //If Team B wins, they receive 2 activities done, A only get 1
                while(ropeTeamA.size()>0)
                {
                    ropeTeamA.get(0).setTotalActivities(ropeTeamA.get(0).getTotalActivities() + 1);
                    childRopeTeamA.pop(ropeTeamA.get(0).getChildName());
                    ropeTeamA.remove(0);
                }
                while(ropeTeamB.size()>0)
                {
                    s2children += ropeTeamB.get(0).getChildName()+" ";
                    ropeTeamB.get(0).setTotalActivities(ropeTeamB.get(0).getTotalActivities() + 2);
                    childRopeTeamB.pop(ropeTeamB.get(0).getChildName());
                    ropeTeamB.remove(0);
                }
            }
            //The instructor waits for all the children
            ropeBarrier.await();
            SimpleDateFormat date2 = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
            String timesStamp2 = date2.format(new Date());
            String s2 = timesStamp2 + ": " + s2children + "win in the ROPE";
            writeFile(s2);
        }
        catch (Exception e)
        { 
            System.out.println(e.toString());
        }
        //The instructor leaves the activity
        SimpleDateFormat date3 = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp3 = date3.format(new Date());
        String s3 = timesStamp3+ ":" + " Instructor "+instruct.getInstructorName()+" leaves ROPE ACTIVITY";
        writeFile(s3);
        instructorRope.pop(instruct.getInstructorName());
        instruct.setInstructorActivitiesDone(instruct.getInstructorActivitiesDone()+1);
    }
    
    public void activityZipLine (Instructor instruct)
    {
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp+ ":" + " Instructor "+instruct.getInstructorName()+" enters ZIPLINE";
        writeFile(s);
        //The instructor accesses the activity
        instructorZipLine.push(instruct.getInstructorName());
        try
        {
            //The instructor waits for a child to arrive
            zipLineBarrier.await();
            //The instructor prepares the child
            try 
            {
                sleep(1000); //Activities (ZIPLINE, ROPE, SNACK)
            }
            catch (InterruptedException e)
            { 
                System.out.println(e.toString());
            }
            //The instructor waits for the child and then gives the signal to go
            zipLineBarrier.await();
            //The instructor waits for the child to finish
            zipLineBarrier.await();
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        //The instructor leaves the activity
        instruct.setInstructorActivitiesDone(instruct.getInstructorActivitiesDone()+1);
        instructorZipLine.pop(instruct.getInstructorName());
        SimpleDateFormat date2 = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp2 = date2.format(new Date());
        String s2 = timesStamp2+ ":" + " Instructor "+instruct.getInstructorName()+" leaves ZIPLINE";
    }
    
    
    public void commonArea (Instructor instruct)
    {
        //Instructor enters the common area
        instructorCommonArea.push(instruct.getInstructorName());
        try 
        {
            //The instructot takes its break
            SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
            String timesStamp = date.format(new Date());
            String s = timesStamp+ ":" + " Instructor "+instruct.getInstructorName()+" starts his BREAK";
            writeFile(s);
            sleep((int)(1000*Math.random() + 1000));
        }
        catch (InterruptedException e)
        { 
            System.out.println(e.toString());
        }
        //The instructor leaves the common area
        instructorCommonArea.pop(instruct.getInstructorName());
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp+ ":" + " Instructor "+instruct.getInstructorName()+" finishes his BREAK";
        writeFile(s);
    }
    
    
    
    public void enterCampLeft(Child c)
    {
        //The child reaches the entrance
        entranceA.push(c.getChildName());
        try 
        {
            //The child tries to enter the camp
            entryLock.lock();
            //If the door is closed, it will wait
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
            /**The child enters the camp, if the capacity is now full,
               the doors will close**/
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
            entryLock.unlock();
            entranceA.pop(c.getChildName());
        }
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp + ":" + " Child " + c.getChildName() + "enters through DOOR 1";
        writeFile(s);
    }
    
    public void enterCampRight(Child c)
    {
        //The child reaches the entrance
        entranceB.push(c.getChildName());
        try 
        {
            //The child tries to enter the camp
            entryLock.lock();
            //If the door is closed, it will wait
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
            /**The child enters the camp, if the capacity is now full,
               the doors will close**/
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
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp + ":" + " Child " + c.getChildName() + "enters through DOOR 2";
        writeFile(s);
    }
    
    public void leaveCamp (Child c)
    {
        //The child will leave the camp
        try 
        {
           entryLock.lock(); 
           //The capacity of the camp decreases by 1
           currentCapacity = currentCapacity - 1; 
           /**
              If both entrances have childs waiting, the next door to open
              will depend on the turn, so they will open alternatively
           **/
           if (entranceA.getThreadList().size() > 0 && entranceB.getThreadList().size() > 0)
           {
               //Door turn is 0: door A opens, turn switches to 1
               if (doorTurn == 0)
                {
                    doorTurn = 1;
                    doorAopen = true; 
                    doorAclosed.signalAll();
                }
               //Door turn is 1: door B opens, turn switches to 1
                else 
                {
                    doorTurn = 0; 
                    doorBopen = true;
                    doorBclosed.signalAll();
                }
           }
           //If there's only people waiting in one door, it will open
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
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp + ":" + " Child " + c.getChildName() + "EXITS the camp";
        writeFile(s);
    }
    
    public void activityZipLine (Child c)
    {
        //The child enters the queue for the Zipline
        childZipLineQueue.push(c.getChildName());
        
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp + ":" + " Child " + c.getChildName() + "enters the ZIPLINE";
        writeFile(s);
        
        try
        {   
            //The child will try to get on the zipline
            childZipLineSem.acquire();
            childZipLineQueue.pop(c.getChildName());
            childZipLinePrep.push(c.getChildName());
            try
            {
                //The child waits for the instructor to prepare it
                zipLineBarrier.await();
                //The child waits for the instructor to give the go signal
                zipLineBarrier.await();
                childZipLinePrep.pop(c.getChildName());
                childZipLineExec.push(c.getChildName());
                //The child rides the zipline (3 seconds)
                try 
                {
                    sleep(3000);
                }
                catch (InterruptedException e)
                { 
                    System.out.println(e.toString());
                }
                //The child lands and leaves (500ms)
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
                //The child signals the instructor it is done
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
            SimpleDateFormat date2 = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
            String timesStamp2 = date2.format(new Date());
            String s2 = timesStamp2 + ":" + " Child " + c.getChildName() + "leaves the ZIPLINE";
        }
    }
    
    public void activityRope (Child c)
    {
        //The child will try to enter the rope activity, if it can't it will leave
        if(childRopeSem.tryAcquire())
        {
            SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
            String timesStamp = date.format(new Date());
            String s = timesStamp + ":" + " Child " + c.getChildName() + "enters the ROPE";
            writeFile(s);
            childRopeQueue.push(c.getChildName());
            try
            {
                ropeQueue.add(c);
                //The child waits for other children to arrive
                ropeBarrier.await();
                /**
                   The child waits for the instructor to make the teams, 
                   do the activity and declare a winner
                 **/
                ropeBarrier.await();
            }
            catch (Exception e)
            {
                System.out.println(e.toString());
            }
            finally
            {
                //The child leaves
                childRopeSem.release(); 
                SimpleDateFormat date2 = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
                String timesStamp2 = date2.format(new Date());
                String s2 = timesStamp2 + ":" + " Child " + c.getChildName() + "leaves the ROPE";
                writeFile(s2);
            }
        }
    }
    
    public void commonArea (Child c)
    {
        //The child enters the common area
        childCommonArea.push(c.getChildName());
        try 
        {
            SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
            String timesStamp = date.format(new Date());
            String s = timesStamp + ":" + " Child " + c.getChildName() + "enters COMMON AREA";
            writeFile(s);
            //Child takes a break
            sleep((int)(2000*Math.random() + 2000));
        }
        catch (InterruptedException e)
        { 
            System.out.println(e.toString());
        }
        //Child leaves the common area
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp + ":" + " Child " + c.getChildName() + "leaves COMMON AREA";
        writeFile(s);
        childCommonArea.pop(c.getChildName());
    }
    
    
    public void SnackEat(Child c) 
    {
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp + ":" + " Child " + c.getChildName() + "enters SNACK AREA";
        writeFile(s);
        try 
        {
            //Children reaches activity, tries to get a clean tray and space to sit
            childSnackQueue.push(c.getChildName());
            maxChildren.acquire();
            cleaned.acquire();
            childSnackQueue.pop(c.getChildName());
            childrenSnack.push(c.getChildName());
            //Child gets a clean tray
            lockSnack.lock();
            cleanTraysList.pop(Integer.toString(cleanTrays.get()));
            cleanTraysList.push(Integer.toString(cleanTrays.decrementAndGet()));
            lockSnack.unlock();
            SimpleDateFormat date2 = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
            String timesStamp2 = date2.format(new Date());
            String s2 = timesStamp2 + ":" + " Child " + c.getChildName() + "grabs CLEAN TRAY";
            writeFile(s2);
            //Child eats    
            sleep(7000);
            //Child leaves a dirty tray    
            lockSnack.lock();
            childrenSnack.pop(c.getChildName());
            dirtyTraysList.pop(Integer.toString(dirtyTrays.get()));
            dirtyTraysList.push(Integer.toString(dirtyTrays.incrementAndGet()));
            lockSnack.unlock();
            dirtied.release();
            SimpleDateFormat date3 = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
            String timesStamp3 = date3.format(new Date());
            String s3 = timesStamp3 + ":" + " Child " + c.getChildName() + "leaves DIRTY TRAY";
            writeFile(s3);
                
        } 
        catch (Exception e)
        {
            System.out.println(e.toString());
        }
        finally 
        {
            //Child leaves snack area
            maxChildren.release();
            SimpleDateFormat date4 = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
            String timesStamp4 = date4.format(new Date());
            String s4 = timesStamp4 + ":" + " Child " + c.getChildName() + "leaves SNACK AREA";
            writeFile(s4);
        } 
    }
    
    public void SnackClean(Instructor i) 
    {
        //instructor reaches activity
        instructorSnack.push(i.getInstructorName());
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp+ ":" + " Instructor "+i.getInstructorName()+" enters SNACK AREA";
        writeFile(s);
        try 
        {
            //Instructor checks if there's dirty trays
            dirtied.acquire();
            //Instructor cleans a dirty tray and puts it in the clean tray pile
            int n = (int)Math.floor(Math.random()*(1-0+2)+3); //Random number between 3-5
            sleep(n*1000);
            lockSnack.lock();
            dirtyTraysList.pop(Integer.toString(dirtyTrays.get()));
            dirtyTraysList.push(Integer.toString(dirtyTrays.decrementAndGet())); 
            cleanTraysList.pop(Integer.toString(cleanTrays.get()));
            cleanTraysList.push(Integer.toString(cleanTrays.incrementAndGet()));
            lockSnack.unlock();    
            cleaned.release();
            SimpleDateFormat date2 = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
            String timesStamp2 = date2.format(new Date());
            String s2 = timesStamp2+ ":" + " Instructor "+i.getInstructorName()+" CLEANS TRAY";
            writeFile(s);
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        } 
        //Instructor leaves activity
        instructorSnack.pop(i.getInstructorName());
        i.setInstructorActivitiesDone(i.getInstructorActivitiesDone()+1);
        
        SimpleDateFormat date3 = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp3 = date3.format(new Date());
        String s3 = timesStamp3+ ":" + " Instructor "+i.getInstructorName()+" leaves SNACK AREA";
        writeFile(s);
    }
    
    public void writeFile(String s){
        try
        {
            fileSem.acquire();
            escribir = new FileWriter(archivo,true);
            line = new PrintWriter(escribir);
            line.println(s);
            line.close();
            escribir.close();
        }
        catch (Exception e) 
        {
            System.out.println(e.toString());
        }
        finally
        {
           fileSem.release(); 
        }
        
    }

}
