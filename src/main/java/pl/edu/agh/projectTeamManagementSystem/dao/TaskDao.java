package pl.edu.agh.projectTeamManagementSystem.dao;

import org.hibernate.Session;
import pl.edu.agh.projectTeamManagementSystem.model.Task;
import pl.edu.agh.projectTeamManagementSystem.session.SessionService;

import javax.persistence.TypedQuery;
import java.util.List;

public class TaskDao extends GenericDao<Task>{
    public TaskDao() {
        super();
    }

    public List<Task> findRatingsByProjectName(String name) {
        final Session session = SessionService.getSession();
        TypedQuery<Task> taskQuery = session.createQuery(
                "from Task r where r.assessedProject.name = :name", Task.class
        );
        taskQuery.setParameter("name", name);
        return taskQuery.getResultList();
    }
}
