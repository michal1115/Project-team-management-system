package pl.edu.agh.projectTeamManagementSystem.model.aggregations;

import javafx.collections.FXCollections;
import pl.edu.agh.projectTeamManagementSystem.model.Participant;
import pl.edu.agh.projectTeamManagementSystem.model.ProjectGroup;
import pl.edu.agh.projectTeamManagementSystem.model.aggregations.model_wrapper.ParticipantProjectGroup;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ParticipantProjectGroupList extends AbstractAggregatedList<ParticipantProjectGroup>{
    public ParticipantProjectGroupList(){
        super();
    }

    public void setWithParticipants(ParticipantList participantList){

        List<ParticipantProjectGroup> participantProjectGroups = new LinkedList<ParticipantProjectGroup>();
        for (Participant p : participantList.getElements()){
            participantProjectGroups.add(new ParticipantProjectGroup(p));
        }
        System.out.println(participantProjectGroups);
        this.setElements(FXCollections.observableList(participantProjectGroups));
    }
    public void setSelectedProjectGroup(Optional<ProjectGroup> selectedProjectGroup){
        for (ParticipantProjectGroup p : this.elements){
            System.out.println("OK");
            p.setSelectedProjectGroup(selectedProjectGroup);
        }
    }
}
