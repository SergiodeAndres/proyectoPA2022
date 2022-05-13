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
    private Semaphore cleaned;
    private Semaphore dirtied;
    //Log elements
    private Semaphore fileSem;
    private File archivo;
    private FileWriter escribir;
    private PrintWriter line;
    //Stop button
    private Gateway remoteControl;
    
    //Constructor
    public Camp(JTextField paramDoorA, JTextField paramDoorB, 
            JTextField paramInstRope, JTextField paramInstZip, 
            JTextField paramInstSnack, JTextField paramChildZipQueue, 
            JTextField paramChildZipPrep, JTextField paramChildZipExec, 
            JTextField paramChildZipEnd,JTextField paramInstCommonArea, 
            JTextField paramCRopeQueue, JTextField paramCRopeA,
            JTextField paramCRopeB, JTextField paramChildCommonArea, 
            JTextField paramCleanTrays, JTextField paramDirtyTrays,
            JTextField paramChildSnack, JTextField paramChildrenSnack,
            Gateway paramGateway)
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
        cleaned = new Semaphore(0);
        dirtied = new Semaphore(25);
        cleanTraysList.push(cleanTrays.toString());
        dirtyTraysList.push(dirtyTrays.toString());
        //Log set-up
        archivo = new File("campEvolution.txt");
        fileSem = new Semaphore(1,true);
        //Stop button
        this.remoteControl = paramGateway;
    }
    
    public void enterCampLeft(Instructor instruct)
    {
        remoteControl.look();
        entranceA.push(instruct.getInstructorName());
        remoteControl.look();
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
            remoteControl.look();
            //The instructor enters the camp
            entryLock.unlock();
            entranceA.pop(instruct.getInstructorName());
            remoteControl.look();
        }
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp + ":" +" Instructor "+instruct.getInstructorName()+" enter through DOOR 1";
        writeFile(s);
    }
    
    public void enterCampRight(Instructor instruct)
    {
        remoteControl.look();
        entranceB.push(instruct.getInstructorName());
        remoteControl.look();
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
            remoteControl.look();
            entranceB.pop(instruct.getInstructorName());
            remoteControl.look();
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
        remoteControl.look();
        instructorRope.push(instruct.getInstructorName());
        remoteControl.look();
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
                    remoteControl.look();
                    ropeTeamA.add(ropeQueue.get(0));
                    childRopeQueue.pop(ropeQueue.get(0).getChildName());
                    childRopeTeamA.push(ropeQueue.get(0).getChildName());
                    ropeQueue.remove(0);
                    remoteControl.look();
                }
                else if (team == 1 && ropeTeamB.size()<5)
                {
                    remoteControl.look();
                    ropeTeamB.add(ropeQueue.get(0));
                    childRopeQueue.pop(ropeQueue.get(0).getChildName());
                    childRopeTeamB.push(ropeQueue.get(0).getChildName());
                    ropeQueue.remove(0);
                    remoteControl.look();
                }
            }
            sleep(7000);
            //The instructor decides who wins
            remoteControl.look();
            int winningTeam = (int)Math.floor(Math.random()*(1-0+1)+0);
            String s2children = "Children ";
            if (winningTeam == 0)
            {
                //If Team A wins, they receive 2 activities done, B only get 1
                while(ropeTeamA.size()>0)
                {
                    remoteControl.look();
                    s2children += ropeTeamA.get(0).getChildName()+" ";
                    ropeTeamA.get(0).setTotalActivities(ropeTeamA.get(0).getTotalActivities() + 2);
                    ropeTeamA.get(0).setNonSnackActivies(ropeTeamA.get(0).getNonSnackActivies() + 2);
                    childRopeTeamA.pop(ropeTeamA.get(0).getChildName());
                    ropeTeamA.remove(0);
                    remoteControl.look();
                }
                while(ropeTeamB.size()>0)
                {
                    remoteControl.look();
                    ropeTeamB.get(0).setTotalActivities(ropeTeamB.get(0).getTotalActivities() + 1);
                    ropeTeamB.get(0).setNonSnackActivies(ropeTeamB.get(0).getNonSnackActivies() + 2);
                    childRopeTeamB.pop(ropeTeamB.get(0).getChildName());
                    ropeTeamB.remove(0);
                    remoteControl.look();
                }
            }
            else
            {
                //If Team B wins, they receive 2 activities done, A only get 1
                while(ropeTeamA.size()>0)
                {
                    remoteControl.look();
                    ropeTeamA.get(0).setTotalActivities(ropeTeamA.get(0).getTotalActivities() + 1);
                    ropeTeamA.get(0).setNonSnackActivies(ropeTeamA.get(0).getNonSnackActivies() + 1);
                    childRopeTeamA.pop(ropeTeamA.get(0).getChildName());
                    ropeTeamA.remove(0);
                    remoteControl.look();
                }
                while(ropeTeamB.size()>0)
                {
                    remoteControl.look();
                    s2children += ropeTeamB.get(0).getChildName()+" ";
                    ropeTeamB.get(0).setTotalActivities(ropeTeamB.get(0).getTotalActivities() + 2);
                    ropeTeamB.get(0).setNonSnackActivies(ropeTeamB.get(0).getNonSnackActivies() + 2);
                    childRopeTeamB.pop(ropeTeamB.get(0).getChildName());
                    ropeTeamB.remove(0);
                    remoteControl.look();
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
        remoteControl.look();
        instructorRope.pop(instruct.getInstructorName());
        remoteControl.look();
        instruct.setInstructorActivitiesDone(instruct.getInstructorActivitiesDone()+1);
    }
    
    public void activityZipLine (Instructor instruct)
    {
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp+ ":" + " Instructor "+instruct.getInstructorName()+" enters ZIPLINE";
        writeFile(s);
        //The instructor accesses the activity
        remoteControl.look();
        instructorZipLine.push(instruct.getInstructorName());
        remoteControl.look();
        try
        {
            //The instructor waits for a child to arrive
            remoteControl.look();
            zipLineBarrier.await();
            remoteControl.look();
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
            remoteControl.look();
            zipLineBarrier.await();
            remoteControl.look();
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        //The instructor leaves the activity
        remoteControl.look();
        instruct.setInstructorActivitiesDone(instruct.getInstructorActivitiesDone()+1);
        instructorZipLine.pop(instruct.getInstructorName());
        remoteControl.look();
        SimpleDateFormat date2 = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp2 = date2.format(new Date());
        String s2 = timesStamp2+ ":" + " Instructor "+instruct.getInstructorName()+" leaves ZIPLINE";
    }
    
    
    public void commonArea (Instructor instruct)
    {
        remoteControl.look();
        //Instructor enters the common area
        instructorCommonArea.push(instruct.getInstructorName());
        remoteControl.look();
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
        remoteControl.look();
        instructorCommonArea.pop(instruct.getInstructorName());
        remoteControl.look();
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp+ ":" + " Instructor "+instruct.getInstructorName()+" finishes his BREAK";
        writeFile(s);
    }
    
    
    
    public void enterCampLeft(Child c)
    {
        //The child reaches the entrance
        remoteControl.look();
        entranceA.push(c.getChildName());
        remoteControl.look();
        try 
        {
            remoteControl.look();
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
            remoteControl.look();
            /**The child enters the camp, if the capacity is now full,
               the doors will close**/
            remoteControl.look();
            currentCapacity = currentCapacity + 1; 
            if (currentCapacity == totalCapacity)
            {
                doorAopen = false;
                doorBopen = false;
            }
            remoteControl.look();
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }
        finally 
        {
            remoteControl.look();
            entryLock.unlock();
            entranceA.pop(c.getChildName());
            remoteControl.look();
        }
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp + ":" + " Child " + c.getChildName() + "enters through DOOR 1";
        writeFile(s);
    }
    
    public void enterCampRight(Child c)
    {
        //The child reaches the entrance
        remoteControl.look();
        entranceB.push(c.getChildName());
        remoteControl.look();
        try 
        {
            //The child tries to enter the camp
            remoteControl.look();
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
            remoteControl.look();
            /**The child enters the camp, if the capacity is now full,
               the doors will close**/
            remoteControl.look();
            currentCapacity = currentCapacity + 1; 
            if (currentCapacity == totalCapacity)
            {
                doorAopen = false;
                doorBopen = false; 
            }
            remoteControl.look();
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }
        finally 
        {
            remoteControl.look();
            //System.out.println("Entered through 1");
            entryLock.unlock();
            entranceB.pop(c.getChildName());
            remoteControl.look();
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
            remoteControl.look();
           entryLock.lock(); 
           //The capacity of the camp decreases by 1
           currentCapacity = currentCapacity - 1; 
           remoteControl.look();
           /**
              If both entrances have childs waiting, the next door to open
              will depend on the turn, so they will open alternatively
           **/
           if (entranceA.getThreadList().size() > 0 && entranceB.getThreadList().size() > 0)
           {
               //Door turn is 0: door A opens, turn switches to 1
               if (doorTurn == 0)
                {
                    remoteControl.look();
                    doorTurn = 1;
                    doorAopen = true; 
                    doorAclosed.signalAll();
                    remoteControl.look();
                }
               //Door turn is 1: door B opens, turn switches to 1
                else 
                {
                    remoteControl.look();
                    doorTurn = 0; 
                    doorBopen = true;
                    doorBclosed.signalAll();
                    remoteControl.look();
                }
           }
           //If there's only people waiting in one door, it will open
           else if (entranceA.getThreadList().size() > 0)
           {
               remoteControl.look();
               doorAopen = true; 
               doorAclosed.signalAll();
               remoteControl.look();
           }
           else 
           {
               remoteControl.look();
               doorBopen = true; 
               doorBclosed.signalAll();
               remoteControl.look();
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
        remoteControl.look();
        childZipLineQueue.push(c.getChildName());
        remoteControl.look();
        
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp + ":" + " Child " + c.getChildName() + "enters the ZIPLINE";
        writeFile(s);
        
        try
        {   
            //The child will try to get on the zipline
            remoteControl.look();
            childZipLineSem.acquire();
            childZipLineQueue.pop(c.getChildName());
            childZipLinePrep.push(c.getChildName());
            remoteControl.look();
            try
            {
                //The child waits for the instructor to prepare it
                remoteControl.look();
                zipLineBarrier.await();
                remoteControl.look();
                //The child waits for the instructor to give the go signal
                remoteControl.look();
                zipLineBarrier.await();
                childZipLinePrep.pop(c.getChildName());
                childZipLineExec.push(c.getChildName());
                remoteControl.look();
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
                remoteControl.look();
                childZipLineExec.pop(c.getChildName());
                childZipLineEnd.push(c.getChildName());
                remoteControl.look();
                try 
                {
                    sleep(500); 
                }
                catch (InterruptedException e)
                { 
                    System.out.println(e.toString());
                }
                //The child signals the instructor it is done
                remoteControl.look();
                childZipLineEnd.pop(c.getChildName());
                remoteControl.look();
                
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
            remoteControl.look();
            childZipLineSem.release();
            c.setTotalActivities(c.getTotalActivities()+1);
            c.setNonSnackActivies(c.getNonSnackActivies()+1);
            remoteControl.look();
            SimpleDateFormat date2 = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
            String timesStamp2 = date2.format(new Date());
            String s2 = timesStamp2 + ":" + " Child " + c.getChildName() + "leaves the ZIPLINE";
        }
    }
    
    public void activityRope (Child c)
    {   remoteControl.look();
        //The child will try to enter the rope activity, if it can't it will leave
        if(childRopeSem.tryAcquire())
        {
            SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
            String timesStamp = date.format(new Date());
            String s = timesStamp + ":" + " Child " + c.getChildName() + "enters the ROPE";
            writeFile(s);
            remoteControl.look();
            childRopeQueue.push(c.getChildName());
            remoteControl.look();
            try
            {
                remoteControl.look();
                ropeQueue.add(c);
                remoteControl.look();
                //The child waits for other children to arrive
                ropeBarrier.await();
                remoteControl.look();
                /**
                   The child waits for the instructor to make the teams, 
                   do the activity and declare a winner
                 **/
                ropeBarrier.await();
                remoteControl.look();
            }
            catch (Exception e)
            {
                System.out.println(e.toString());
            }
            finally
            {
                //The child leaves
                childRopeSem.release(); 
                remoteControl.look();
                SimpleDateFormat date2 = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
                String timesStamp2 = date2.format(new Date());
                String s2 = timesStamp2 + ":" + " Child " + c.getChildName() + "leaves the ROPE";
                writeFile(s2);
            }
        }
    }
    
    public void commonArea (Child c)
    {
        remoteControl.look();
        //The child enters the common area
        childCommonArea.push(c.getChildName());
        remoteControl.look();
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
        remoteControl.look();
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp + ":" + " Child " + c.getChildName() + "leaves COMMON AREA";
        writeFile(s);
        childCommonArea.pop(c.getChildName());
        remoteControl.look();
    }
    
    
    public void SnackEat(Child c) 
    {
        remoteControl.look();
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp + ":" + " Child " + c.getChildName() + "enters SNACK AREA";
        writeFile(s);
        try 
        {
            remoteControl.look();
            //Children reaches activity, tries to get a clean tray and space to sit
            childSnackQueue.push(c.getChildName());
            maxChildren.acquire();
            cleaned.acquire();
            remoteControl.look();
            childSnackQueue.pop(c.getChildName());
            childrenSnack.push(c.getChildName());
            remoteControl.look();
            //Child gets a clean tray
            lockSnack.lock();
            remoteControl.look();
            cleanTraysList.pop(Integer.toString(cleanTrays.get()));
            cleanTraysList.push(Integer.toString(cleanTrays.decrementAndGet()));
            lockSnack.unlock();
            remoteControl.look();
            SimpleDateFormat date2 = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
            String timesStamp2 = date2.format(new Date());
            String s2 = timesStamp2 + ":" + " Child " + c.getChildName() + "grabs CLEAN TRAY";
            writeFile(s2);
            //Child eats    
            sleep(7000);
            //Child leaves a dirty tray    
            remoteControl.look();
            lockSnack.lock();
            childrenSnack.pop(c.getChildName());
            dirtyTraysList.pop(Integer.toString(dirtyTrays.get()));
            dirtyTraysList.push(Integer.toString(dirtyTrays.incrementAndGet()));
            lockSnack.unlock();
            dirtied.release();
            remoteControl.look();
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
            remoteControl.look();
            //Child leaves snack area
            maxChildren.release();
            SimpleDateFormat date4 = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
            String timesStamp4 = date4.format(new Date());
            String s4 = timesStamp4 + ":" + " Child " + c.getChildName() + "leaves SNACK AREA";
            writeFile(s4);
            remoteControl.look();
        } 
    }
    
    public void SnackClean(Instructor i) 
    {
        remoteControl.look();
        //instructor reaches activity
        instructorSnack.push(i.getInstructorName());
        remoteControl.look();
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
        String timesStamp = date.format(new Date());
        String s = timesStamp+ ":" + " Instructor "+i.getInstructorName()+" enters SNACK AREA";
        writeFile(s);
        try 
        {
            remoteControl.look();
            //Instructor checks if there's dirty trays
            dirtied.acquire();
            //Instructor cleans a dirty tray and puts it in the clean tray pile
            sleep((int)(2000*Math.random() + 3000));
            lockSnack.lock();
            remoteControl.look();
            dirtyTraysList.pop(Integer.toString(dirtyTrays.get()));
            dirtyTraysList.push(Integer.toString(dirtyTrays.decrementAndGet())); 
            remoteControl.look();
            cleanTraysList.pop(Integer.toString(cleanTrays.get()));
            cleanTraysList.push(Integer.toString(cleanTrays.incrementAndGet()));
            lockSnack.unlock();    
            cleaned.release();
            remoteControl.look();
            SimpleDateFormat date2 = new SimpleDateFormat("dd.MM.yyyy.HH:mm:ss.SS");
            String timesStamp2 = date2.format(new Date());
            String s2 = timesStamp2+ ":" + " Instructor "+i.getInstructorName()+" CLEANS TRAY";
            writeFile(s);
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        } 
        remoteControl.look();
        //Instructor leaves activity
        instructorSnack.pop(i.getInstructorName());
        i.setInstructorActivitiesDone(i.getInstructorActivitiesDone()+1);
        remoteControl.look();
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

    public ThreadList getEntranceA() 
    {
        return entranceA;
    }

    public void setEntranceA(ThreadList entranceA) 
    {
        this.entranceA = entranceA;
    }

    public ThreadList getEntranceB() 
    {
        return entranceB;
    }

    public void setEntranceB(ThreadList entranceB) 
    {
        this.entranceB = entranceB;
    }

    public ThreadList getInstructorRope() 
    {
        return instructorRope;
    }

    public void setInstructorRope(ThreadList instructorRope) 
    {
        this.instructorRope = instructorRope;
    }

    public ThreadList getChildRopeQueue() 
    {
        return childRopeQueue;
    }

    public void setChildRopeQueue(ThreadList childRopeQueue) 
    {
        this.childRopeQueue = childRopeQueue;
    }

    public ThreadList getChildRopeTeamA() 
    {
        return childRopeTeamA;
    }

    public void setChildRopeTeamA(ThreadList childRopeTeamA) 
    {
        this.childRopeTeamA = childRopeTeamA;
    }

    public ThreadList getChildRopeTeamB() 
    {
        return childRopeTeamB;
    }

    public void setChildRopeTeamB(ThreadList childRopeTeamB) 
    {
        this.childRopeTeamB = childRopeTeamB;
    }

    public ThreadList getInstructorZipLine() {
        return instructorZipLine;
    }

    public void setInstructorZipLine(ThreadList instructorZipLine) 
    {
        this.instructorZipLine = instructorZipLine;
    }

    public ThreadList getChildZipLineQueue() 
    {
        return childZipLineQueue;
    }

    public void setChildZipLineQueue(ThreadList childZipLineQueue) 
    {
        this.childZipLineQueue = childZipLineQueue;
    }

    public ThreadList getChildZipLinePrep() 
    {
        return childZipLinePrep;
    }

    public void setChildZipLinePrep(ThreadList childZipLinePrep) 
    {
        this.childZipLinePrep = childZipLinePrep;
    }

    public ThreadList getChildZipLineExec() 
    {
        return childZipLineExec;
    }

    public void setChildZipLineExec(ThreadList childZipLineExec) 
    {
        this.childZipLineExec = childZipLineExec;
    }

    public ThreadList getChildZipLineEnd() 
    {
        return childZipLineEnd;
    }

    public void setChildZipLineEnd(ThreadList childZipLineEnd) 
    {
        this.childZipLineEnd = childZipLineEnd;
    }

    public ThreadList getInstructorSnack() 
    {
        return instructorSnack;
    }

    public void setInstructorSnack(ThreadList instructorSnack) 
    {
        this.instructorSnack = instructorSnack;
    }

    public ThreadList getCleanTraysList() 
    {
        return cleanTraysList;
    }

    public void setCleanTraysList(ThreadList cleanTraysList) 
    {
        this.cleanTraysList = cleanTraysList;
    }

    public ThreadList getDirtyTraysList() 
    {
        return dirtyTraysList;
    }

    public void setDirtyTraysList(ThreadList dirtyTraysList) 
    {
        this.dirtyTraysList = dirtyTraysList;
    }

    public ThreadList getChildSnackQueue() 
    {
        return childSnackQueue;
    }

    public void setChildSnackQueue(ThreadList childSnackQueue) 
    {
        this.childSnackQueue = childSnackQueue;
    }

    public ThreadList getChildrenSnack() 
    {
        return childrenSnack;
    }

    public void setChildrenSnack(ThreadList childrenSnack) 
    {
        this.childrenSnack = childrenSnack;
    }

    public ThreadList getInstructorCommonArea() 
    {
        return instructorCommonArea;
    }

    public void setInstructorCommonArea(ThreadList instructorCommonArea) 
    {
        this.instructorCommonArea = instructorCommonArea;
    }

    public ThreadList getChildCommonArea() 
    {
        return childCommonArea;
    }

    public void setChildCommonArea(ThreadList childCommonArea) 
    {
        this.childCommonArea = childCommonArea;
    }

    public int getTotalCapacity() 
    {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) 
    {
        this.totalCapacity = totalCapacity;
    }

    public int getCurrentCapacity() 
    {
        return currentCapacity;
    }

    public void setCurrentCapacity(int currentCapacity) 
    {
        this.currentCapacity = currentCapacity;
    }

    public int getDoorTurn() 
    {
        return doorTurn;
    }

    public void setDoorTurn(int doorTurn) 
    {
        this.doorTurn = doorTurn;
    }

    public Lock getEntryLock() 
    {
        return entryLock;
    }

    public void setEntryLock(Lock entryLock) 
    {
        this.entryLock = entryLock;
    }

    public Lock getExitLock() 
    {
        return exitLock;
    }

    public void setExitLock(Lock exitLock) 
    {
        this.exitLock = exitLock;
    }

    public Condition getDoorAclosed() 
    {
        return doorAclosed;
    }

    public void setDoorAclosed(Condition doorAclosed) 
    {
        this.doorAclosed = doorAclosed;
    }

    public Condition getDoorBclosed() 
    {
        return doorBclosed;
    }

    public void setDoorBclosed(Condition doorBclosed) 
    {
        this.doorBclosed = doorBclosed;
    }

    public Boolean getDoorAopen() 
    {
        return doorAopen;
    }

    public void setDoorAopen(Boolean doorAopen) 
    {
        this.doorAopen = doorAopen;
    }

    public Boolean getDoorBopen() 
    {
        return doorBopen;
    }

    public void setDoorBopen(Boolean doorBopen) 
    {
        this.doorBopen = doorBopen;
    }

    public Semaphore getChildZipLineSem() 
    {
        return childZipLineSem;
    }

    public void setChildZipLineSem(Semaphore childZipLineSem) 
    {
        this.childZipLineSem = childZipLineSem;
    }

    public CyclicBarrier getZipLineBarrier() 
    {
        return zipLineBarrier;
    }

    public void setZipLineBarrier(CyclicBarrier zipLineBarrier) 
    {
        this.zipLineBarrier = zipLineBarrier;
    }

    public CyclicBarrier getRopeBarrier() 
    {
        return ropeBarrier;
    }

    public void setRopeBarrier(CyclicBarrier ropeBarrier) 
    {
        this.ropeBarrier = ropeBarrier;
    }

    public ArrayList<Child> getRopeQueue() 
    {
        return ropeQueue;
    }

    public void setRopeQueue(ArrayList<Child> ropeQueue) 
    {
        this.ropeQueue = ropeQueue;
    }

    public ArrayList<Child> getRopeTeamA() 
    {
        return ropeTeamA;
    }

    public void setRopeTeamA(ArrayList<Child> ropeTeamA) 
    {
        this.ropeTeamA = ropeTeamA;
    }

    public ArrayList<Child> getRopeTeamB() 
    {
        return ropeTeamB;
    }

    public void setRopeTeamB(ArrayList<Child> ropeTeamB) 
    {
        this.ropeTeamB = ropeTeamB;
    }

    public Semaphore getChildRopeSem() 
    {
        return childRopeSem;
    }

    public void setChildRopeSem(Semaphore childRopeSem) 
    {
        this.childRopeSem = childRopeSem;
    }

    public AtomicInteger getCleanTrays() 
    {
        return cleanTrays;
    }

    public void setCleanTrays(AtomicInteger cleanTrays) 
    {
        this.cleanTrays = cleanTrays;
    }

    public AtomicInteger getDirtyTrays() 
    {
        return dirtyTrays;
    }

    public void setDirtyTrays(AtomicInteger dirtyTrays) 
    {
        this.dirtyTrays = dirtyTrays;
    }

    public Semaphore getMaxChildren() 
    {
        return maxChildren;
    }

    public void setMaxChildren(Semaphore maxChildren) 
    {
        this.maxChildren = maxChildren;
    }

    public Lock getLockSnack() 
    {
        return lockSnack;
    }

    public void setLockSnack(Lock lockSnack) 
    {
        this.lockSnack = lockSnack;
    }

    public Semaphore getCleaned() 
    {
        return cleaned;
    }

    public void setCleaned(Semaphore cleaned) 
    {
        this.cleaned = cleaned;
    }

    public Semaphore getDirtied() 
    {
        return dirtied;
    }

    public void setDirtied(Semaphore dirtied) 
    {
        this.dirtied = dirtied;
    }

    public Semaphore getFileSem() 
    {
        return fileSem;
    }

    public void setFileSem(Semaphore fileSem) 
    {
        this.fileSem = fileSem;
    }

    public File getArchivo() 
    {
        return archivo;
    }

    public void setArchivo(File archivo) 
    {
        this.archivo = archivo;
    }

    public FileWriter getEscribir() 
    {
        return escribir;
    }

    public void setEscribir(FileWriter escribir) 
    {
        this.escribir = escribir;
    }

    public PrintWriter getLine() 
    {
        return line;
    }

    public void setLine(PrintWriter line) 
    {
        this.line = line;
    }

    public Gateway getRemoteControl() 
    {
        return remoteControl;
    }

    public void setRemoteControl(Gateway remoteControl) 
    {
        this.remoteControl = remoteControl;
    }
    
    

}
