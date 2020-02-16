package pl.edu.agh.projectTeamManagementSystem.model.reports;

public interface ReportGenerator<T> {

    Report<T> generate();

}
