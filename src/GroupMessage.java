
import java.io.Serializable;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author usuario
 */
public class GroupMessage implements Serializable{
    
    ArrayList<Byte> mensaje;
    GroupMember emisor;
    
    public GroupMessage(GroupMember emisor, ArrayList<Byte> mensaje){
        
        this.emisor = new GroupMember(emisor.aliasMiembro, emisor.hostname, emisor.idMiembro, emisor.idGrupo, emisor.puerto);
        this.mensaje = mensaje;
        
    }
    
}
