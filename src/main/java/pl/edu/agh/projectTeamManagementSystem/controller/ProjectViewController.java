package pl.edu.agh.projectTeamManagementSystem.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import pl.edu.agh.projectTeamManagementSystem.dao.ProjectDao;
import pl.edu.agh.projectTeamManagementSystem.dao.ProjectGroupDao;
import pl.edu.agh.projectTeamManagementSystem.model.Participant;
import pl.edu.agh.projectTeamManagementSystem.model.Project;
import pl.edu.agh.projectTeamManagementSystem.model.ProjectGroup;
import pl.edu.agh.projectTeamManagementSystem.model.aggregations.ProjectList;
import pl.edu.agh.projectTeamManagementSystem.model.interactions.ItemInputType;
import pl.edu.agh.projectTeamManagementSystem.model.mail.MailMessage;
import pl.edu.agh.projectTeamManagementSystem.model.mail.SendMailService;
import pl.edu.agh.projectTeamManagementSystem.presenter.ProjectEditPanePresenter;
import pl.edu.agh.projectTeamManagementSystem.presenter.TasksPresenter;

import java.io.IOException;
import java.time.LocalDate;

public class ProjectViewController {
    private ProjectList projectList;
    private ProjectDao projectDao;
    private ProjectGroupDao projectGroupDao;
    private SendMailService mailService;

    @FXML private TableView<Project> tableView;

    @FXML private Button addNewButton;
    @FXML private Button editButton;
    @FXML private Button removeButton;
    @FXML private Button tasksButton;

    @FXML private Label projectGroupNameLabel;
    @FXML private Label activeLabel;
    @FXML private Label creationDateLabel;
    @FXML private Label leaderLabel;
    @FXML private Label participantCountLabel;
    @FXML private Label averageScoreLabel;

    @FXML
    private void initialize() {
        projectList = new ProjectList();
        projectDao = new ProjectDao();
        projectGroupDao = new ProjectGroupDao();
        mailService = new SendMailService();

        bindTableProperties();
        bindButtonProperties();
        bindProjectGroupProperties();

        loadAll();
    }

    private void bindTableProperties() {
        tableView.itemsProperty().bind(projectList.getProperty());
    }

    private void bindButtonProperties() {
        BooleanBinding disableBinding = tableView.getSelectionModel().selectedItemProperty().isNull();
        editButton.disableProperty().bind(disableBinding);
        removeButton.disableProperty().bind(disableBinding);
        tasksButton.disableProperty().bind(disableBinding);
    }

    private void bindProjectGroupProperties() {
        // TODO: fix warnings
        ObjectBinding<String> groupNameBinding = Bindings.select(tableView.getSelectionModel().selectedItemProperty(), "projectGroup", "groupName");
        projectGroupNameLabel.textProperty().bind(groupNameBinding);

        ObjectBinding<Boolean> activeBinding = Bindings.select(tableView.getSelectionModel().selectedItemProperty(), "projectGroup", "active");
        StringBinding activeStringBinding = Bindings
                .when(activeBinding.isNotNull())
                .then(activeBinding.asString())
                .otherwise("");
        activeLabel.textProperty().bind(activeStringBinding);

        ObjectBinding<LocalDate> creationDateBinding = Bindings.select(tableView.getSelectionModel().selectedItemProperty(), "projectGroup", "creationDate");
        StringBinding creationDateStringBinding = Bindings
                .when(creationDateBinding.isNotNull())
                .then(creationDateBinding.asString())
                .otherwise("");
        creationDateLabel.textProperty().bind(creationDateStringBinding);
    }

    @FXML
    private void loadAll() {
        projectList.setElements(FXCollections.observableList(projectDao.findAll()));
    }

    @FXML
    private void loadActive() {
        projectList.setElements(FXCollections.observableList(projectDao.findActiveProjects()));
    }

    @FXML
    private void loadArchival() {
        projectList.setElements(FXCollections.observableList(projectDao.findArchivalProjects()));
    }

    @FXML
    private void loadFuture() {
        projectList.setElements(FXCollections.observableList(projectDao.findFutureProjects()));
    }

    @FXML
    void showTasks(){
        Project selectedProject = tableView.getSelectionModel().getSelectedItem();
        try {
            Dialog editDialog = new Dialog();
            FXMLLoader loader = new FXMLLoader();
            Parent parent = loader.load(getClass().getResourceAsStream("/fxml/ProjectTasksView.fxml"));
            TasksPresenter presenter = loader.getController();
            presenter.setItemInputType(ItemInputType.EDIT_ITEM);
            presenter.setProject(selectedProject);
            presenter.setWindow(editDialog.getDialogPane().getScene().getWindow());
            editDialog.getDialogPane().setContent(parent);
            editDialog.showAndWait();
            if (presenter.isAccepted()) {
                projectDao.save(selectedProject);
            }
        }
        catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    @FXML
    void addNewProject() {
        try {
            Dialog editDialog = new Dialog();
            FXMLLoader loader = new FXMLLoader();
            Parent parent = loader.load(getClass().getResourceAsStream("/fxml/ProjectEditPane.fxml"));
            ProjectEditPanePresenter presenter = loader.getController();
            presenter.setItemInputType(ItemInputType.NEW_ITEM);
            presenter.setWindow(editDialog.getDialogPane().getScene().getWindow());
            editDialog.getDialogPane().setContent(parent);
            editDialog.showAndWait();

            if (presenter.isAccepted()) {
                if (presenter.getProject().getProjectGroup() != null) {
                    projectGroupDao.save(presenter.getProject().getProjectGroup());
                }
                projectDao.save(presenter.getProject());
                projectList.getElements().add(presenter.getProject());
            }
        }
        catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    @FXML
    void editSelectedProject() {
        Project selectedProject = tableView.getSelectionModel().getSelectedItem();
        try {
            Dialog editDialog = new Dialog();
            FXMLLoader loader = new FXMLLoader();
            Parent parent = loader.load(getClass().getResourceAsStream("/fxml/ProjectEditPane.fxml"));
            ProjectEditPanePresenter presenter = loader.getController();
            presenter.setItemInputType(ItemInputType.EDIT_ITEM);
            presenter.setProject(selectedProject);
            presenter.setWindow(editDialog.getDialogPane().getScene().getWindow());
            editDialog.getDialogPane().setContent(parent);
            editDialog.showAndWait();
            if (presenter.isAccepted()) {
                projectGroupDao.save(selectedProject.getProjectGroup());
                projectDao.save(selectedProject);
            }
        }
        catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    @FXML
    void removeSelectedProject() {
        Project selectedProject = tableView.getSelectionModel().getSelectedItem();
        sendRemovedEmail(selectedProject);
        projectDao.delete(selectedProject);
        projectList.getElements().remove(selectedProject);
    }

    void sendRemovedEmail(Project project) {
        ProjectGroup group = project.getProjectGroup();
        for (Participant participant : group.getParticipants()) {
            if (!participant.isSubscribed())
                continue;
            MailMessage message = new MailMessage();
            message.setReceiver(participant.getEmail());
            message.setSubject("Gastro update");
            message.setText("Project " + project.getName() + "was removed");
            mailService.sendEmail(message);
        }
    }
}
