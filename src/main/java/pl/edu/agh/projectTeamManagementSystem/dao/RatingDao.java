package pl.edu.agh.projectTeamManagementSystem.dao;

import org.hibernate.Session;
import pl.edu.agh.projectTeamManagementSystem.model.ProjectGroup;
import pl.edu.agh.projectTeamManagementSystem.model.Rating;
import pl.edu.agh.projectTeamManagementSystem.session.SessionService;

import javax.persistence.TypedQuery;
import java.util.List;

public class RatingDao extends GenericDao<Rating> {

    public RatingDao() {
        super();
    }

    public List<Rating> findByProjectGroup(ProjectGroup projectGroup) {
        final Session session = SessionService.getSession();
        TypedQuery<Rating> ratingQuery = session.createQuery(
                "from Rating r where r.assessedGroup = :projectGroup", Rating.class
        );
        ratingQuery.setParameter("projectGroup", projectGroup);
        return ratingQuery.getResultList();
    }

}
