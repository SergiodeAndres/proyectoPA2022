package Part2;
import Part1.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Gateway
{
    private boolean close=false;
    private Lock lock = new ReentrantLock();
    private Condition stop = lock.newCondition();
    
    //Threads will check to see if they have to stop
    public void look()
    {
        try
        {
            lock.lock();
            while(close)
            {
                //They stop until the gateway changes
                try
                {
                    stop.await();
                } 
                catch(Exception e)
                {
                    System.out.println(e.toString());
                }
            }
        }
        finally
        {
            lock.unlock();
        }
    }
    
    public void open()
    {
        try
        {
            lock.lock();
            close=false;
            stop.signalAll();
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        finally
        {
            lock.unlock();
        }
    }
    
    public void close()
    {
        try
        {
            lock.lock();
            close=true;
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        finally
        {
            lock.unlock();
        }
    }
}
