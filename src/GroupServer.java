
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author YO
 */
public class GroupServer extends UnicastRemoteObject implements GroupServerInterface{
    LinkedList<ObjectGroup> grupos = new <ObjectGroup>LinkedList();
    ObjectGroup grupo;
    ReentrantLock lock = new ReentrantLock();
    static int numGrupo = 0;

    GroupServer() throws RemoteException {
        super();
    }
    
    public int createGroup(String galias, String oalias, String ohostname, int puerto) {
        lock.lock();

        for (int i = 0; i < grupos.size(); i++) {
            if (grupos.get(i).alias.equals(galias)) {
                lock.unlock();
                return -1;
            }
        }

        //if(numGrupo!=0);
        numGrupo++;
        grupo = new ObjectGroup(galias, numGrupo, oalias, ohostname, puerto);
        grupos.add(grupo);

        lock.unlock();

        return grupo.idg;
    }
    
    public int findGroup(String galias) {
        lock.lock();

        for (int i = 0; i < grupos.size(); i++) {
            if (grupos.get(i).alias.equals(galias)) {
                lock.unlock();
                return grupos.get(i).idg;
            }
        }

        lock.unlock();

        return -1;
    }
    
    public boolean removeGroup(String galias, String oalias) {
        lock.lock();

        for (int i = 0; i < grupos.size(); i++) {
            if (grupos.get(i).alias.equals(galias)
                    && grupos.get(i).propietario.aliasMiembro.equals(oalias)) {
                grupos.remove(i);
                lock.unlock();
                return true;
            }
        }

        return false;
    }
    
    public boolean removeGroup(int gid, String oalias) {
        lock.lock();
        for (int i = 0; i < grupos.size(); i++) {
            if (grupos.get(i).idg == gid
                    && grupos.get(i).propietario.aliasMiembro.equals(oalias)) {
                grupos.remove(i);
                lock.unlock();
                return true;
            }
        }

        lock.unlock();

        return false;
    }
    
    
    public GroupMember addMember(int gid, String alias, String hostname, int puerto) {
        lock.lock();

        for (int i = 0; i < grupos.size(); i++) {
            
            ObjectGroup gru = grupos.get(i);
            int idgru = gru.idg;
            if (gid == idgru) {
                try {
                    lock.unlock();
                    return grupos.get(i).addMember(alias, hostname, puerto);

                } catch (InterruptedException ex) {
                    Logger.getLogger(GroupServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        lock.unlock();
        return null;

    }
    
    public GroupMember isMember(int gid, String alias) throws RemoteException{
        lock.lock();

        for (int i = 0; i < grupos.size(); i++) {
            if (grupos.get(i).idg == gid) {
                lock.unlock();
                return grupos.get(i).isMember(alias);
            }
        }

        lock.unlock();

        return null;
    }
    
    
    
    public boolean sendGroupMessage (GroupMember gm, ArrayList<Byte> msg) throws RemoteException{
        
        
        GroupMessage mensaje;
        for (int i = 0; i < grupos.size(); i++) {
            if(grupos.get(i).isMember(gm.aliasMiembro) != null){
                
            mensaje = new GroupMessage(gm,msg);
            grupos.get(i).Sending();
            SendingMessage hilo = new SendingMessage(mensaje, grupos.get(i));
            hilo.start();
            
            //Se termina de enviar los mensajes de grupo
            return true;
            }
        }
        return false;
            
    }
//    public boolean StopMembers(int gid) {
//        lock.lock();
//
//        for (int i = 0; i < grupos.size(); i++) {
//            if (grupos.get(i).idg == gid) {
//                lock.unlock();
//                return grupos.get(i).StopMembers();
//            }
//        }
//
//        lock.unlock();
//
//        return false;
//    }
//    
//    public boolean AllowMembers(int gid) {
//        lock.lock();
//
//        for (int i = 0; i < grupos.size(); i++) {
//            if (grupos.get(i).idg == gid) {
//                lock.unlock();
//                return grupos.get(i).AllowMembers();
//            }
//        }
//
//        lock.unlock();
//
//        return false;
//    }
    
    public static void main(String[] args) throws UnknownHostException {
        System.setProperty("java.security.policy", "C:\\Users\\usuario\\Desktop\\CentralizedGroups\\src\\PolicyServidor");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            LocateRegistry.createRegistry(1099);
            GroupServer servidor = new GroupServer();
            Naming.rebind("//localhost/GroupServer", servidor);

        } catch (RemoteException | MalformedURLException ex) {
            Logger.getLogger(GroupServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
