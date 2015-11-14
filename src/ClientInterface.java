
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author YO
 */
public interface ClientInterface extends Remote { 
    void DepositMessage(GroupMessage m) throws RemoteException;      
    ArrayList<Byte> receiveGroupMessage(String galias) throws RemoteException;
}
