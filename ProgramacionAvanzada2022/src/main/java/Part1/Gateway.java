//  The Gateway class defines a Conditional Critical Region for a
//  boolean variable closed to be checked by a process
//  When closed == false (open) the process can continue, otherwise
//  the process must stop.

package Part1;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Gateway
{
    private boolean close=false;
    private Lock lock = new ReentrantLock();
    private Condition stop = lock.newCondition();

    public void look()
    {
        try
        {
            lock.lock();
            while(close)
            {
                try
                {
                    stop.await();
                } catch(InterruptedException ie){ }
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
        finally
        {
            lock.unlock();
        }
    }
}
