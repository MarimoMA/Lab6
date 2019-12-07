package com.example.test.ds;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Task implements Serializable {
    private static int idCounter = 1;
    private int id;
    private String title;
    private Project mainProject = null;
    private ArrayList<Task> subTask;
    private String responsibleUser;
    private Date dateStarted = null;
    private Date dateFinished = null;

    public Task(Project project, String title, String creator){
        Calendar calendar = Calendar.getInstance();
        this.dateStarted = calendar.getTime();
        this.title = title;
        this.responsibleUser = creator;
        this.mainProject = project;
        this.id = idCounter;
        idCounter++;
    }


    public Task() {}

    public void setId(int id) {
        this.id = id;
    }

    public void setMainProject(Project mainProject) {
        this.mainProject = mainProject;
    }



    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }



    public Date getDateStarted() {
        return dateStarted;
    }

    public int getId() {
        return id;
    }

    public Project getMainProject() {
        return mainProject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResponsibleUser() {
        return responsibleUser;
    }

    public void setResponsibleUser(String responsibleUser) {
        this.responsibleUser = responsibleUser;
    }

    public Date getDateFinished() {
        return dateFinished;
    }

    public void setDateFinished(Date dateFinished) {
        this.dateFinished = dateFinished;
    }

    @Override
    public String toString() {
        return "Task " +
                "Title = " + title +
                ", dateStarted =" + dateStarted +
                ", dateFinished =" + dateFinished;
    }
}