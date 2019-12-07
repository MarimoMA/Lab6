package com.example.test.ds;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Project implements Serializable {
    private int id;
    private int idCounter = 0;
    private ArrayList<Task> tasks = new ArrayList();
    private ArrayList<User> assignedUsers = new ArrayList();
    private String title, projectCreator;
    private Date startDate;
    private Date endDate = null;
    private State status;

    public ArrayList<User> getAllUsers() {
        return assignedUsers;
    }

    public Project(){}

    public enum State{
        InProgress, Finished
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public Project(User user, String title) {
        this.title = title;
        this.projectCreator = user.getLogin();
        this.id = idCounter;
        idCounter++;
        Calendar calendar = Calendar.getInstance();
        this.startDate = calendar.getTime();
        this.status = State.InProgress;
        assignedUsers.add(user);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void removeAllTasks() {
        tasks = null;
    }

    public void removeAllUsersFromProject(Project project) {
        for(User user : assignedUsers)
            user.removeProject(project);
        assignedUsers = null;
    }

    public void endTask(String endTask) {
        boolean found = false;
        for(Task task : tasks) {
            if (task.getTitle().equals(endTask)) {
                Calendar calendar = Calendar.getInstance();
                task.setDateFinished(calendar.getTime());
                found = true;
                break;
            }
        }
        if(!found) throw null;
    }

    public ArrayList<User> getAssignedUsers() {
        return assignedUsers;
    }

    public boolean checkIfUserAlreadyAdded(User user) {
        for(User assignedUser : assignedUsers){
            if(assignedUser == user)
                return true;
        }
        return false;
    }

    public ArrayList<Task> getAllTasks() {
        return tasks;
    }

    public void removeTask(String taskTitle) {
        Task task = getRequestedTask(taskTitle);
        if(task != null)
            tasks.remove(task);
        else
            throw null;
    }

    private Task getRequestedTask(String taskTitle) {
        for(Task task : tasks)
            if(task.getTitle().equals(taskTitle))
                return task;
        return  null;
    }

    public void removeUser(Project project, String removeUserLogin) {
        for(User user : assignedUsers)
            if(user.getLogin() == removeUserLogin) {
                user.removeProject(project);
                assignedUsers.remove(user);
            }

    }

    public void addUser(User user) {
        this.assignedUsers.add(user);
    }

    public void addTask(Project project, String title, String creator) {
        Task task = new Task(project, title, creator);
        tasks.add(task);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProjectCreator() {
        return projectCreator;
    }

    public void setProjectCreator(String projectCreator) {
        this.projectCreator = projectCreator;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public State getStatus() {
        return status;
    }

    public void setStatus(State status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Task getTaskByTitle(String title){
        for(Task t:tasks){
            if(t.getTitle().equals(title))
                return t;
        }
        return null;
    }

    @Override
    public String toString() {
        return "\tProject No " + id +":" +
                " \nTitle: " + title +
                " \nProjectCreator: " + projectCreator +
                " \nStartDate: " + startDate +
                " \nTasks: " + tasks.toString() +
                " \nAssignedUsers: " + assignedUsers.toString() +
                " \nEndDate: " + endDate +
                " \nStatus: " + status + "\n";
    }
}
