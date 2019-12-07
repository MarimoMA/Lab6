package com.example.test.ds;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class User implements Serializable {
    private static int idCounter = 1;
    private int id;
    private boolean active = true;
    private int rating = 0;
    private String login, password;
    private final ArrayList<Project> projects = new ArrayList();
    private int token;

    public User(String login, String password) {
        this.id = idCounter;
        idCounter++;
        this.login = login;
        this.password = password;
    }

    public User(){}

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public static void setIdCounter(int idCounter) {
        User.idCounter = idCounter;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getRating() {
        return rating;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Project getCurrentProject(String title) {
        for(Project project : projects)
            if(project.getTitle().equals(title))
                return project;
        return null;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Collection<? extends Project> getAllProjects() {
        return projects;
    }

    public ArrayList<Project> getAllActiveProjects() {
        ArrayList<Project> allActiveProjects = new ArrayList();
        for(Project project : projects){
            if(project.getStatus() == Project.State.InProgress){
                allActiveProjects.add(project);
            }
        }
        return allActiveProjects;
    }

    public void removeTask(Project createdProject, String taskTitle) {
        for(Project project : projects)
        {
            if(project == createdProject)
                project.removeTask(taskTitle);
        }
    }

    public void removeProject(Project projectToRemove) {
        for(Project project : projects)
            if(project == projectToRemove) {
                project.removeAllTasks();
                project.removeAllUsersFromProject(projectToRemove);
                projects.remove(project);
            }
    }

    public void addProject(Project project) {
        projects.add(project);
    }
}
