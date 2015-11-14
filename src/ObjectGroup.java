import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ObjectGroup {

    String alias;
    GroupMember propietario;
    int idg;
    ArrayList<GroupMember> Members = new ArrayList<>();
    ReentrantLock lock = new ReentrantLock();
    int auto_increment_id;
    boolean add_remove = true;
    Condition cond;
    int envios = 0;

    ObjectGroup(String aliasGrupo, int idGrupo, String aliasPropietario, String hostnamePropietaro, int puerto) {
        this.auto_increment_id = 0;//
        this.alias = aliasGrupo;
        this.idg = idGrupo;
        this.propietario = new GroupMember(aliasPropietario, hostnamePropietaro, auto_increment_id, idGrupo, puerto);
        this.Members.add(propietario);
    }

    public GroupMember isMember(String aliasmiembro) {
        lock.lock();

            int tam = this.Members.size();
            for (int i = 0; i < tam; i++) {
                if (this.Members.get(i).aliasMiembro.equals(aliasmiembro)) {
                    lock.unlock();
                    return this.Members.get(i);
                }
            }

            lock.unlock();

        return null;
    }

    public GroupMember addMember(String aliasMiembro, String host, int puerto) throws InterruptedException {
        lock.lock();

        if (add_remove) {
            for (int i = 0; i < this.Members.size(); i++) {
                if (this.Members.get(i).aliasMiembro.equals(aliasMiembro)) {
                    lock.unlock();
                    return null;
                }
            }
            auto_increment_id = auto_increment_id + 1;
            GroupMember nuevoMiembro = new GroupMember(aliasMiembro, host, auto_increment_id, idg, puerto);
            this.Members.add(nuevoMiembro);

            lock.unlock();
            return nuevoMiembro;
        } else {

            lock.unlock();
            return null;
        }
    }

    boolean removeMember(String aliasmiembro) throws InterruptedException {
        lock.lock();
        if (add_remove) {
            for (int i = 0; i < this.Members.size(); i++) {
                if (this.Members.get(i).aliasMiembro.equals(aliasmiembro)
                        && !propietario.aliasMiembro.equals(aliasmiembro)) {
                    this.Members.remove(i);
                    cond.signal();
                    lock.unlock();
                    return true;
                }
            }
            lock.unlock();
            return false;
        } else {

            lock.unlock();
            return false;
        }
    }

    void Sending(){
    add_remove= false;
    envios++;
    }
    
    void EndSending(GroupMember gm){
     envios--;
      if(envios == 0)
          add_remove= true;    
    }
    
    
    public boolean AllowMembers() {
        lock.lock();

            add_remove = true;

            lock.unlock();

        return true;
    }

    public boolean StopMembers() {
        lock.lock();

            add_remove = false;

            lock.unlock();

        return true;
    }
}
