package Part2;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceCampData extends Remote
{
    int getZipLineQueue() throws RemoteException;
    
    int getZipLineUses() throws RemoteException;
    
    int getRopeQueue() throws RemoteException;
    
    int getDirtyTrays() throws RemoteException;
    
    int getCleanTrays() throws RemoteException;
    
    int getChildrenEating() throws RemoteException;
    
    int getActivitiesDone(String childName) throws RemoteException;
}
