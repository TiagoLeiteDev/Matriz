package pt.matriz.projects;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import pt.matriz.identity.AccountRepository;
import pt.matriz.identity.CurrentIdentity;
import pt.matriz.identity.UserProfile;
import pt.matriz.tasks.TaskFilter;
import pt.matriz.tasks.TaskQueries;

@RestController
public class DashboardController {
    private final CurrentIdentity identity;
    private final AccountRepository accounts;
    private final ProjectQueries projects;
    private final TaskQueries tasks;
    public DashboardController(CurrentIdentity identity,AccountRepository accounts,ProjectQueries projects,TaskQueries tasks) {
        this.identity=identity;this.accounts=accounts;this.projects=projects;this.tasks=tasks;
    }
    public record Dashboard(UserProfile utilizador,List<ProjectQueries.ProjectView> projetos,Map<String,Long> contagens,boolean incluiIndependentes) {}
    @GetMapping("/api/painel")
    @Transactional(readOnly=true,isolation=Isolation.REPEATABLE_READ)
    public Dashboard get(@RequestParam(required=false) String incluirIndependentes) {
        if(incluirIndependentes==null)throw TaskFilter.bad();
        boolean include=TaskFilter.bool(incluirIndependentes);
        var profile=accounts.profile(identity.id());
        var visible=projects.list("",false);
        Map<String,Long> counts=new LinkedHashMap<>();
        for(String state:List.of("TODO","IN_PROGRESS","DONE"))counts.put(state,0L);
        for(var task:tasks.list(new TaskFilter("",null,false,null,null,false,null,null)))
            if(include||task.projetoId()!=null)counts.computeIfPresent(task.estado(),(k,v)->v+1);
        return new Dashboard(profile,visible,counts,include);
    }
}
