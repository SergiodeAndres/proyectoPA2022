/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notas.programacionavanzada2022;
import static java.lang.Thread.sleep;
import javax.swing.JTextField;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 *
 * @author sergi
 */
public class Camp {
    private ThreadList entranceA; 
    private ThreadList entranceB; 
    private ThreadList camp; 
    private int totalCapacity;
    private int currentCapacity; 
    private int doorTurn;
    private Lock entryLock; 
    private Lock exitLock; 
    private Condition doorAclosed; 
    private Condition doorBclosed;
    
    private Semaphore cleanTrays = new Semaphore(0);
    private Semaphore dirtyTrays = new Semaphore(25);
    private Semaphore em = new Semaphore(1);
    private Semaphore maxChildren = new Semaphore(20);
    
    
    public Camp(JTextField doorA, JTextField doorB, JTextField pCamp)
    {
        entranceA = new ThreadList(doorA); 
        entranceB = new ThreadList(doorB);
        camp = new ThreadList(pCamp); 
        this.totalCapacity = 50; 
        this.currentCapacity = 0; 
        this.doorTurn = 0; 
        entryLock = new ReentrantLock(); 
        exitLock = new ReentrantLock();
        doorAclosed = entryLock.newCondition(); 
        doorBclosed = entryLock.newCondition();
        
        cleanTrays = new Semaphore(0);
        dirtyTrays = new Semaphore(25);
    }
    
    public void enterCampLeft(Child c)
    {
        System.out.println("0");
        entranceA.push(c);
        try 
        {
            entryLock.lock();
            while (currentCapacity == totalCapacity)
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
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }
        finally 
        {
            System.out.println("Entered through 0");
            entryLock.unlock();
            entranceA.pop(c);
            camp.push(c);
        }  
    }
    
    public void enterCampRight(Child c)
    {
        System.out.println("1");
        entranceB.push(c);
        try 
        {
            entryLock.lock();
            while (currentCapacity == totalCapacity)
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
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }
        finally 
        {
            System.out.println("Entered through 1");
            entryLock.unlock();
            entranceB.pop(c);
            camp.push(c);
        }  
    }
    
    public void leaveCamp (Child c)
    {
        try 
        {
           entryLock.lock();
           camp.pop(c); 
           currentCapacity = currentCapacity - 1; 
           if (entranceA.getThreadList().size() > 0 && entranceB.getThreadList().size() > 0)
           {
               if (doorTurn == 0)
                {
                    doorTurn = 1; 
                    doorAclosed.signalAll();
                }
                else 
                {
                    doorTurn = 0; 
                    doorBclosed.signalAll();
                }
           }
           else if (entranceA.getThreadList().size() > 0)
           {
               doorAclosed.signalAll();
           }
           else 
           {
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
    
    public void SnackEat(Child i) throws InterruptedException{
        cleanTrays.acquire();
        maxChildren.acquire();
        em.acquire(); // Block: SC start
        sleep(7000);
        em.release(); // Unblock: SC end
        maxChildren.release();
        dirtyTrays.release();
    }
    public void SnackClean(Instructor i) throws InterruptedException{
        dirtyTrays.acquire();
        em.acquire(); // Block: SC start
        int n = (int)Math.floor(Math.random()*(1-0+2)+3); //Random number between 3-5
        sleep(n*1000);
        em.release(); // Unblock: SC end
        cleanTrays.release();

    }
}
