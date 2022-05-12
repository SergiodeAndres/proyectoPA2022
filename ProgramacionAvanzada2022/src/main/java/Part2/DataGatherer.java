package Part2;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class DataGatherer extends UnicastRemoteObject
                         implements InterfaceCampData
{
    private Camp dataCamp;
    public DataGatherer(Camp paramCamp) throws RemoteException 
    {
        this.dataCamp = paramCamp;
    }
    
    public int getZipLineQueue() throws RemoteException 
    {
        int zipQueue = dataCamp.getChildZipLineQueue().getThreadList().size();
        return zipQueue;
    }
    
    public int getZipLineUses() throws RemoteException 
    {
        int zipUses = dataCamp.getZipLineUses();
        return zipUses;
    }
    
    public int getRopeQueue() throws RemoteException 
    {
        int ropeQueue = dataCamp.getChildRopeQueue().getThreadList().size();
        return ropeQueue;
    }
    
    public int getDirtyTrays() throws RemoteException 
    {
        int dirtyTrays = dataCamp.getDirtyTrays().get();
        return dirtyTrays;
    }
    
    public int getCleanTrays() throws RemoteException 
    {
        int cleanTrays = dataCamp.getCleanTrays().get();
        return cleanTrays;
    }
    
    public int getChildrenEating() throws RemoteException 
    {
        int snackRoom = dataCamp.getChildrenSnack().getThreadList().size();
        return snackRoom;
    }
    
    public int getActivitiesDone(String childName) throws RemoteException 
    {
        int activitiesDone = -1; 
        ArrayList<Child> childList = dataCamp.getChildrenInCamp();
        for (int i = 0; i < childList.size(); i++)
        {
            if (childList.get(i).getChildName().equals(childName))
            {
                activitiesDone = childList.get(i).getTotalActivities();
            }
        }
        return activitiesDone;
    }
}
