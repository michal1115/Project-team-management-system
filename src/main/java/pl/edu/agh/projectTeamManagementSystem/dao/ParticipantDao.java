package pl.edu.agh.projectTeamManagementSystem.dao;

import org.hibernate.Session;
import pl.edu.agh.projectTeamManagementSystem.model.Participant;
import pl.edu.agh.projectTeamManagementSystem.model.ProjectGroup;
import pl.edu.agh.projectTeamManagementSystem.session.SessionService;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ParticipantDao extends GenericDao<Participant> {

    public ParticipantDao() {
        super();
    }

    public Optional<Participant> findParticipantByEmail(String email){
        final Session session = SessionService.getSession();

        TypedQuery<Participant> participantsByEmailQuery = session.createQuery("from Participant as participant " +
                "where participant.email = :email", Participant.class);
        participantsByEmailQuery.setParameter("email", email);

        return participantsByEmailQuery.getResultStream().findFirst();
    }

    public List<Participant> findParticipantsAssignedTo(ProjectGroup projectGroup) {
        return findAll().stream()
                .filter(participant -> participant.isParticipantIn(projectGroup))
                .collect(Collectors.toList());
    }

    public List<Participant> findParticipantsNotAssignedTo(ProjectGroup projectGroup) {
        return findAll().stream()
                .filter(participant -> !participant.isParticipantIn(projectGroup))
                .collect(Collectors.toList());
    }

}
