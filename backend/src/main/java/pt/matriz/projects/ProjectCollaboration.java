package pt.matriz.projects;

import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pt.matriz.identity.UserProfile;

@Service
public class ProjectCollaboration {
    private final JdbcTemplate jdbc;
    private final ProjectAccess access;
    private final ProjectCommands commands;
    public ProjectCollaboration(JdbcTemplate jdbc, ProjectAccess access, ProjectCommands commands) {
        this.jdbc=jdbc; this.access=access; this.commands=commands;
    }
    @Transactional(readOnly=true, isolation=Isolation.REPEATABLE_READ)
    public List<UserProfile> list(UUID project) {
        access.requireView(project);
        return jdbc.query("SELECT u.id,u.nome,u.email FROM colaboracao_projeto c JOIN utilizador u ON u.id=c.utilizador_id WHERE c.projeto_id=? ORDER BY u.id",
            (rs,n)->new UserProfile(rs.getObject("id",UUID.class),rs.getString("nome"),rs.getString("email")),project);
    }
    @Transactional
    public UserProfile add(UUID project, String email) {
        commands.lockOwner(project);
        var user=jdbc.query("SELECT id,nome,email FROM utilizador WHERE email=?",
            (rs,n)->new UserProfile(rs.getObject("id",UUID.class),rs.getString("nome"),rs.getString("email")),email)
            .stream().findFirst().orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(access.requireOwner(project).owner().equals(user.id())) throw new ResponseStatusException(HttpStatus.CONFLICT);
        if(jdbc.update("INSERT INTO colaboracao_projeto(projeto_id,utilizador_id) VALUES(?,?) ON CONFLICT DO NOTHING",project,user.id())==0)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        return user;
    }
    @Transactional
    public void remove(UUID project, UUID user) {
        commands.lockOwner(project);
        if(access.requireOwner(project).owner().equals(user)) throw new ResponseStatusException(HttpStatus.CONFLICT);
        if(jdbc.update("DELETE FROM colaboracao_projeto WHERE projeto_id=? AND utilizador_id=?",project,user)==0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        jdbc.update("DELETE FROM atribuicao_tarefa a USING tarefa t WHERE a.tarefa_id=t.id AND t.projeto_id=? AND a.utilizador_id=?",project,user);
    }
}
