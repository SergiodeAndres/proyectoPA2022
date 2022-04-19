/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notas.programacionavanzada2022;
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
    }
    
    public void enterCamp(Child c)
    {
        int entrance = (int)Math.floor(Math.random()*(1-0+1)+0);
        System.out.println(entrance);
        if (entrance == 0)
        {
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
                entryLock.unlock();
                entranceA.pop(c);
                camp.push(c);
            }
        }
        else 
        {
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
                entryLock.unlock();
                entranceB.pop(c);
                camp.push(c);
            }
        }  
    }
    
    public void leaveCamp (Child c)
    {
        try 
        {
           entryLock.lock();
           camp.pop(c); 
           currentCapacity = currentCapacity - 1; 
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
        catch (Exception e)
        {
            System.out.println(e.toString());
        }
        finally 
        {
            entryLock.unlock(); 
        }
    }
}