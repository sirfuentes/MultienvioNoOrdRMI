
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author YO
 */
public class Client extends UnicastRemoteObject implements ClientInterface{
    
    final ReentrantLock lock = new ReentrantLock(true);
    final Condition condicion = lock.newCondition();
    LinkedList<GroupMessage> cola = new LinkedList<>();
    static GroupServerInterface stub;
    
    Client() throws RemoteException {
        super();
    }

    public static void main(String[] args) throws UnknownHostException {
        String host = "//localhost/GroupServer";
        String alias, s1,s2,s3,s4;
        int i1,i2, sc;
        boolean menu = true;
        Scanner scan= new Scanner(System.in);
        
        
        
        

        //seguridad
        System.setProperty("java.security.policy", "C:\\Users\\usuario\\Desktop\\CentralizedGroups\\src\\PolicyServidor");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {

            Client client = new Client();
            GroupServerInterface cliente = (GroupServerInterface) Naming.lookup("GroupServer");

            System.out.println("Alias?");

            //Scanner alias = new Scanner(System.in);
            alias = scan.next();

            while (menu) {
                System.out.println("Usuario:  " + alias);
                System.out.println("1. Crear nuevo grupo\n"
                        + "2. Buscar grupo\n"
                        + "3. Eliminar grupo por ID\n"
                        + "4. Eliminar grupo por alias\n"
                        + "5. Bloquear altas\n"
                        + "6. Desbloquear altas\n"
                        + "7. Añadir miembro a un grupo\n"
                        + "8. Comprobar si miembro esta en grupo\n"
                        + "9. Salir\n");

                //Scanner opcion = new Scanner(System.in);
                switch (scan.nextInt()) {

                    case 1:
                        System.out.println("Introduce alias del nuevo grupo");
                        s1=scan.next();
                        System.out.println("Introduce el puerto");
                        i2=scan.nextInt();
                        
                        if (cliente.createGroup(s1, alias, java.net.Inet4Address.getLocalHost().getHostName(), i2) > 0) {
                            System.out.println("El grupo se ha creado correctamnte");
                        } else {
                            System.out.println("Error al crear el grupo");
                        }
                        break;
                        
                        case 2:
                      System.out.println("Introduce el alias del grupo a buscar");
                       Scanner a2=new Scanner(System.in);
                    int grupo=cliente.findGroup(a2.next());
                  
                    if(grupo>=0)
                        System.out.println("ID de grupo encontrado: "+grupo);
                    else
                        System.out.println("No existe el ese grupo");
                       
                      break; 
                            
                    case 3:
                        System.out.println("Introduce ID de grupo a eliminar");
                        i1= scan.nextInt();
                        System.out.println("Introduce alias del propetario");
                        s1= scan.next();
                        
                        if (cliente.removeGroup(i1, s1)) {
                            System.out.println("El grupo se ha eliminado correctamente");
                        } else {
                            System.out.println("Error al eliminar el grupo");
                        }
                        break;

                    case 4:

                        System.out.println("Introduce alias del grupo a eliminar");
                        s1= scan.next();
                        System.out.println("Introduce alias del propetario");
                        s2= scan.next();
                        
                        if (cliente.removeGroup(s1, s2)) {
                            System.out.println("El grupo se ha eliminado correctamente");
                        } else {
                            System.out.println("Error eliminando el grupo");
                        }
                        break;

//                    case 5:
//                        System.out.println("Introduce ID del grupo");
//                        i1= scan.nextInt();
//                        
//                        if (cliente.StopMembers(i1)) {
//                            System.out.println("Las altas fueron bloqueadas");
//                        } else {
//                            System.out.println("error cambiando permisos");
//                        }
//
//                        break;

//                    case 6:
//                        System.out.println("Introduce ID de grupo");
//                        i1= scan.nextInt();
//                        
//                        if (cliente.AllowMembers(i1)) {
//                            System.out.println("Las altas fueron desbloqueadas");
//                        } else {
//                            System.out.println("error cambiando permisos");
//                        }
//                        break;

                    case 7:

                        System.out.println("Introduce el ID del grupo");
                        i1= scan.nextInt();
                        System.out.println("Introduce el alias del nuevo miembro");
                        s1= scan.next();
                        System.out.println("Introduce el ID del hostname");
                        s2= scan.next();
                        System.out.println("Inserte un número de puerto");
                        sc = scan.nextInt();
                        
                        
                        if (cliente.addMember(i1, s1, s2, sc) != null) {
                            System.out.println("El nuevo miembro se ha añadido correctamente");
                        } else {
                            System.out.println("Error al añadir el nuevo miembro");
                        }

                        break;
                    
                  

                    case 8:
                        System.out.println("Introduce el ID del grupo");
                        i1= scan.nextInt();
                        System.out.println("Introduce el alias del miembro");
                        s1= scan.next();
                        
                        if (cliente.isMember(i1, s1) != null) {
                            System.out.println("Este miembro pertenece a este grupo");
                        } else {
                            System.out.println("Este miembro no pertenece a este grupo");
                        }
                        break;

                    case 9:
                        System.out.println("Saliendo... " + alias);
                        menu = false;
                        UnicastRemoteObject.unexportObject(client, true);
                        scan.next();
                        break;
                }

            }
        } catch (RemoteException | NotBoundException | MalformedURLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
    public void DepositMessage(GroupMessage m) throws RemoteException {
        lock.lock();
        try {
            cola.push(m);
            
            //Desbloqueo condicion, porque ha llegado un mensaje
            condicion.signalAll();
            //condicion.
            
        } finally {
            lock.unlock();
        }
    }

    @Override
    public synchronized ArrayList<Byte> receiveGroupMessage(String galias) throws RemoteException {
        
        lock.lock();
        int id = -1;
         try {

      
        id = stub.findGroup(galias);
        //System.out.println("Llego aqui "+ id);
       if( id == -1){
       return null;
       }
       while(true){

       
       
        for (int i = 0; i < cola.size(); i++) {
            if(cola.get(i).emisor.idGrupo == id){
                ArrayList<Byte> array =(ArrayList<Byte>) cola.get(i).mensaje.clone();
              //  cola.get(i).mensaje.get();
                cola.remove(i);
                return array;
            }
        }
                    //Bloqueo porque no tiene ningún mensaje de ese grupo
           try{
                condicion.await();
               
            //return null;
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }

    }//Fin while
        } finally {
            lock.unlock();
        }
}
}
