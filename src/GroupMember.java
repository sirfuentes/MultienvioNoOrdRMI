
import java.io.Serializable;

/**
 *
 * @author usuario
 */
public class GroupMember implements Serializable{
    String aliasMiembro;
    String hostname;
    int idMiembro;
    int idGrupo;
    int puerto;
    
    public GroupMember(String aliasMiembro, String hostname, int idMiembro, int idGrupo, int p){
        this.aliasMiembro = aliasMiembro;
        this.hostname = hostname;
        this.idGrupo = idGrupo;
        this.idMiembro = idMiembro;
        this.puerto = p;
        
        
    }
}
