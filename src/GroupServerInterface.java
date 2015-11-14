
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author usuario
 */
public interface GroupServerInterface extends Remote{
    
    public int createGroup(String galias, String oalias, String ohostname, int puerto) throws RemoteException;
    public int findGroup (String galias) throws RemoteException;
    public boolean removeGroup (int gid, String oalias) throws RemoteException;
    public boolean removeGroup (String galias, String oalias) throws RemoteException;
    public GroupMember addMember (int gid, String alias, String hostname, int puerto) throws RemoteException;
    public GroupMember isMember (int gid, String alias) throws RemoteException;
    public boolean sendGroupMessage (GroupMember gm, ArrayList<Byte> msg) throws RemoteException;
//    public boolean StopMembers (int gid) throws RemoteException;
//    public boolean AllowMembers (int gid) throws RemoteException;

    
}
