package pl.edu.agh.projectTeamManagementSystem.model.importer;

import pl.edu.agh.projectTeamManagementSystem.model.Participant;

import java.nio.file.Path;

public class ParticipantCsvImporter extends AbstractCsvImporter<Participant> {
    public ParticipantCsvImporter(Path importedFile) {
        super(importedFile);
    }
}
