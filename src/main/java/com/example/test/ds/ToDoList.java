package com.example.test.ds;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ToDoList implements Serializable {

    private final ArrayList<User> users = new ArrayList<>();
    private User loggedIn = null;
    private Connection con = null;

    public void connectToDB(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/lab3", "root", "");
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void disconect(){
        try{
            if(con!=null)
                con.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public Person registerPerson(String login, String password, String name, String lastname) {
        if(isLoginOcupied(login)){
            return null;
        }
        Person newPerson = new Person(login, password, name, lastname);
        users.add(newPerson);
        try{
            PreparedStatement ps = con.prepareStatement("INSERT INTO `user` (`id`, `activated`, `login`, `password`, `rating`) "
                    + "VALUES (NULL, ?, ?, MD5(?), ?)", Statement.RETURN_GENERATED_KEYS);

            ps.setBoolean(1, true);
            ps.setString(2, login);
            ps.setString(3, password);
            ps.setInt(4, 0);

            ps.executeUpdate();
            ResultSet ids = ps.getGeneratedKeys();

            ids.next();
            int ID = ids.getInt(1);

            ps = con.prepareStatement("INSERT INTO `person` (`id`, `name`, `lastname`) "
                    + "VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, ID);
            ps.setString(2, name);
            ps.setString(3, lastname);

            ps.executeUpdate();
            ids.close();
            ps.close();
        }
        catch(Exception ex){

        }
        return newPerson;
    }

    public Company registerCompany(String login, String password, String title, String contactPerson) {
        if(isLoginOcupied(login)){
            return null;
        }
        Company newCompany = new Company(login, password, title, contactPerson);
        users.add(newCompany);
        try{
            PreparedStatement prepState = con.prepareStatement("INSERT INTO `user` (`id`, `activated`, `login`, `password`, `rating`)"
                    + "VALUES (NULL, ?, ?, MD5(?), ?)", Statement.RETURN_GENERATED_KEYS);

            prepState.setBoolean(1, true);
            prepState.setString(2, login);
            prepState.setString(3, password);
            prepState.setInt(4, 0);

            prepState.executeUpdate();
            ResultSet ids = prepState.getGeneratedKeys();

            ids.next();
            int ID = ids.getInt(1);

            prepState = con.prepareStatement("INSERT INTO `company` (`id`, `title`, `contact_person`)"
                    + "VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            prepState.setInt(1, ID);
            prepState.setString(2, title);
            prepState.setString(3, contactPerson);

            prepState.executeUpdate();
            ids.close();
            prepState.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return newCompany;
    }

    public User loginUser(String login, String password) {
        User user = null;
        try{
            PreparedStatement prep = con.prepareStatement("SELECT * FROM user WHERE login=? AND password=MD5(?)", Statement.RETURN_GENERATED_KEYS);
            prep.setString(1, login);
            prep.setString(2, password);
            ResultSet duom = prep.executeQuery();

            while(duom.next()){
                user = new User();
                user.setId(duom.getInt("id"));
                user.setLogin(duom.getString("login"));
                user.setActive(duom.getBoolean("activated"));
                user.setRating(duom.getInt("rating"));
                int random = (int)(Math.random()*10000 + 325432);
                prep = con.prepareStatement("UPDATE user SET token = ? WHERE id = ?");
                prep.setInt(1, random);
                prep.setInt(2, user.getId());
                prep.executeUpdate();
                user.setToken(random);
                return user;
            }

            duom.close();
            prep.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public String getLoggedUsersName() {
        if(loggedIn.getClass() == Person.class)
            return ((Person)loggedIn).getName();
        else
            return ((Company)loggedIn).getTitle();
    }

    public boolean isLogged(int userId, int token){
        try{
            PreparedStatement prep = con.prepareStatement("SELECT * FROM user WHERE id=? AND token=?");
            prep.setInt(1, userId);
            prep.setInt(2, token);
            ResultSet duom = prep.executeQuery();

            while(duom.next()){
                return true;
            }

            prep.close();
            duom.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    public void createProject(String title) {
        try{
            PreparedStatement prepState = con.prepareStatement("INSERT INTO `project` (`id`, `title`, `project_creator`, `start_date`,"
                    + " `end_date`, `status`) VALUES (NULL, ?, ?)");
            prepState.setString(1, title);
            prepState.setString(2, loggedIn.getLogin());
            prepState.executeUpdate();
            ResultSet ids = prepState.getGeneratedKeys();
            ids.next();
            int projectID = ids.getInt(1);

            prepState = con.prepareStatement("INSERT INTO `assigned_projects` (`user_id`, `project_id`) VALUES (?, ?)");
            prepState.setInt(1, loggedIn.getId());
            prepState.setInt(2, projectID);
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void createProjectAdmin(String userLogin, String title){
        try{
            PreparedStatement prep = con.prepareStatement("SELECT * FROM user WHERE login=?");
            prep.setString(1, userLogin);
            ResultSet duom = prep.executeQuery();
            duom.next();
            int userID = duom.getInt("id");
            duom.close();

            prep = con.prepareStatement("INSERT INTO `project` (`id`, `title`, `project_creator`)"
                    + " VALUES (NULL, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            prep.setString(1, title);
            prep.setString(2, userLogin);
            prep.executeUpdate();

            ResultSet ids = prep.getGeneratedKeys();
            ids.next();
            int projectID = ids.getInt(1);

            prep = con.prepareStatement("INSERT INTO `assigned_projects` (`user_id`, `project_id`) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            prep.setInt(1, userID);
            prep.setInt(2, projectID);
            prep.executeUpdate();

            ids.close();
            prep.close();
        }catch(SQLException ex){
            throw null;
        }
    }

    public boolean addUserToProject(String userLogin, Project createdProject) {
        try{
            PreparedStatement prep = con.prepareStatement("SELECT * FROM user WHERE login=?", Statement.RETURN_GENERATED_KEYS);
            prep.setString(1, userLogin);
            ResultSet data = prep.executeQuery();
            data.next();
            int userID = data.getInt("id");
            data.close();

            prep = con.prepareStatement("INSERT INTO `assigned_projects` (`user_id`, `project_id`) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            prep.setInt(1, userID);
            prep.setInt(2, createdProject.getId());
            prep.executeQuery();
            prep.close();

            return true;
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }

    public Project getProject(String projectTitle) {
        for(User user : users){
            if(user == loggedIn)
                return user.getCurrentProject(projectTitle);
        }
        return  null;
    }

    public String getCurrentUserLogin() {
        return loggedIn.getLogin();
    }

    public ArrayList<Project> getAllLoggedUsersProjects() {
        ArrayList<Project> allProjects = new ArrayList<>();
        ResultSet data = null;
        try{
            PreparedStatement prep = con.prepareStatement("SELECT * FROM `project` INNER JOIN `assigned_projects` "
                    + "ON `project`.`id` = `assigned_projects`.`user_id` WHERE `user_id` = ?", Statement.RETURN_GENERATED_KEYS);
            prep.setInt(1, loggedIn.getId());
            data = prep.executeQuery();

            while(data.next()){
                Project project = new Project();
                project.setId(data.getInt("id"));
                project.setTitle(data.getString("title"));
                project.setProjectCreator(data.getString("project_creator"));
                project.setStartDate(data.getDate("start_date"));

                allProjects.add(project);
            }
            data.close();
            prep.close();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return allProjects;
    }

    public ArrayList<Project> getAllLoggedUsersActiveProjects() {
        ArrayList<Project> activeProjects = new ArrayList<>();
        try{
            PreparedStatement prep = con.prepareStatement("SELECT * FROM `project` INNER JOIN `assigned_projects` "
                    + "ON `project`.`id` = `assigned_projects`.`user_id` WHERE `user_id` = ? AND `status` = 'InProgress'", Statement.RETURN_GENERATED_KEYS);
            prep.setInt(1, loggedIn.getId());
            ResultSet data = prep.executeQuery();

            while(data.next()){
                Project project = new Project();
                project.setId(data.getInt("id"));
                project.setTitle(data.getString("title"));
                project.setProjectCreator(data.getString("project_creator"));
                project.setStartDate(data.getDate("start_date"));

                activeProjects.add(project);
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return activeProjects;
    }

    public ArrayList<User> getAllUsers(){
        ArrayList<User> allUsers = new ArrayList<>();
        try{
            PreparedStatement prep = con.prepareStatement("SELECT * FROM user", Statement.RETURN_GENERATED_KEYS);
            ResultSet duom = prep.executeQuery();
            while(duom.next()){
                User user = new User();
                user.setId(duom.getInt("id"));
                user.setLogin(duom.getString("login"));
                user.setActive(duom.getBoolean("activated"));
                user.setRating(duom.getInt("rating"));
                allUsers.add(user);
            }
            duom.close();
            prep.close();
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
        return allUsers;
    }

    public boolean isUserLogged() {
        if(loggedIn != null)
            return true;
        return  false;
    }

    public boolean isLoggedInUserCreator(Project createdProject) {
        return createdProject.getProjectCreator().equals(loggedIn.getLogin());
    }

    public boolean isCreatorRemovingProject(Project createdProject, String userName) {
        return (createdProject.getProjectCreator() == userName);
    }

    public void changeProjectCreator(Project createdProject) {
        ArrayList<User> otherUsers = createdProject.getAssignedUsers();
        if(otherUsers.size() > 1)
            createdProject.setProjectCreator(otherUsers.get(0).getLogin());
        else
            throw null;
    }

    public void deleteProject(Project currentProject) {
        try{
            PreparedStatement prep = con.prepareStatement("DELETE FROM `task` WHERE `task`.`project_id` = ?");
            prep.setInt(1, currentProject.getId());
            prep.executeUpdate();

            prep = con.prepareStatement("DELETE FROM `assigned_projects` WHERE `project_id` = ?");
            prep.setInt(1, currentProject.getId());
            prep.executeUpdate();

            prep = con.prepareStatement("DELETE FROM `project` WHERE `id` = ?");
            prep.setInt(1, currentProject.getId());
            prep.executeUpdate();

            prep.close();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void endTask(Project currentProject, Task task, User user) {
        try {
            PreparedStatement prep = con.prepareStatement("UPDATE `task` SET `date_finished` = ? WHERE `task`.`id` = ?", Statement.RETURN_GENERATED_KEYS);
            java.util.Date today = new java.util.Date();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(today.getTime());
            prep.setTimestamp(1, timestamp);
            prep.setInt(2, task.getId());
            prep.executeQuery();

            prep = con.prepareStatement("UPDATE `user` SET `rating` = ? WHERE `user`.`id` = ?", Statement.RETURN_GENERATED_KEYS);
            prep.setInt(1, user.getRating() + 1);
            prep.setInt(2, user.getId());
            prep.executeQuery();

            ArrayList<Task> allTasks = getAllTasks(currentProject);

            int taskFinnished = 0;

            for(Task tsk : allTasks){
                if(tsk.getDateFinished() != null)
                    taskFinnished ++;
            }

            if(taskFinnished == allTasks.size())
                changeProjectStatus(currentProject);

            prep.close();
        }catch(SQLException ex) {
            throw null;
        }
    }

    public ArrayList<Task> getAllTasks(Project project){
        ArrayList<Task> tasks = new ArrayList();
        try{
            PreparedStatement prep = con.prepareStatement("Select * FROM `task` WHERE `project_id` = ?", Statement.RETURN_GENERATED_KEYS);
            prep.setInt(1, project.getId());
            ResultSet data = prep.executeQuery();

            while(data.next()){
                Task task = new Task();
                task.setId(data.getInt("id"));
                task.setTitle(data.getString("title"));
                task.setMainProject(project);
                task.setDateStarted(data.getDate("date_started"));
                task.setDateFinished(data.getDate("date_finished"));
                task.setResponsibleUser(data.getString("responsible_user"));
                tasks.add(task);
            }

            data.close();
            prep.close();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return tasks;
    }

    public void endTaskAdmin(Project currentProject, String endTask) {
        try {
            currentProject.endTask(endTask);
            Task task = currentProject.getTaskByTitle(endTask);
            String login = task.getResponsibleUser();
            User respUser = getUserByLogin(login);
            respUser.setRating(respUser.getRating() + 1);
        }catch(NullPointerException ex) { throw null;}
    }

    @Override
    public String toString() {
        return "ToDoList: " +
                "users=" + users.toString();
    }

    public void logoutUser() {
        loggedIn = null;
    }

    public boolean isProjectCreator(Project currentProject) {
        return currentProject.getProjectCreator() == loggedIn.getLogin();
    }

    public void changeProjectStatus(Project currentProject) {
        try{
            PreparedStatement prep = con.prepareStatement("UPDATE `project` SET `status` = ? WHERE `project`.`id` = ?", Statement.RETURN_GENERATED_KEYS);
            prep.setInt(2, currentProject.getId());
            if(currentProject.getStatus() == Project.State.InProgress)
                prep.setString(1, "Finnished");
            else
                prep.setString(1, "InProgress");
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public String showUserInfo(User user) {
        if(user.getClass() == Person.class)
            return ((Person)user).toString();
        else
            return ((Company)user).toString();
    }

    public void changeUserName(String newName) {
        if(loggedIn.getClass() == Person.class)
            ((Person)loggedIn).setName(newName);
        else
            ((Company)loggedIn).setTitle(newName);
    }

    public void changeUserLastName(String lastName) {
        if(loggedIn.getClass() == Person.class)
            ((Person)loggedIn).setLastname(lastName);
        else
            ((Company)loggedIn).setContactPerson(lastName);
    }

    public void deactivateAccount() {
        loggedIn.setActive(false);
    }

    public ArrayList<Person> getAllPeople(){
        ArrayList<Person> people = new ArrayList<>();
        try{
            PreparedStatement prep = con.prepareStatement("SELECT * FROM `user` INNER JOIN `person` ON `user`.`id` = `person`.`id`", Statement.RETURN_GENERATED_KEYS);
            ResultSet duom = prep.executeQuery();

            while(duom.next()){
                Person person = new Person(duom.getString("login"), duom.getString("password"),
                        duom.getString("name"), duom.getString("lastname"));
                people.add(person);
            }
            duom.close();
            prep.close();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return people;
    }

    public ArrayList<Company> getAllCompanies(){
        ArrayList<Company> companies = new ArrayList<>();
        try{
            PreparedStatement prep = con.prepareStatement("SELECT * FROM `user` INNER JOIN `company` ON `user`.`id` = `company`.`id`", Statement.RETURN_GENERATED_KEYS);
            ResultSet duom = prep.executeQuery();

            while(duom.next()){
                Company company = new Company(duom.getString("login"), duom.getString("password"),
                        duom.getString("title"), duom.getString("contact_person"));
                companies.add(company);
            }
            duom.close();
            prep.close();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return companies;
    }

    public int[] getUserCount(){
        int [] amount = new int [2];
        ArrayList<Person> people = getAllPeople();
        ArrayList<Company> companies = getAllCompanies();
        amount[0] = people.size();
        amount[1] = companies.size();
        return amount;
    }

    public ArrayList<Project> getAllProjects(){
        ArrayList<Project> projects = new ArrayList<>();
        for(User u : users){
            projects.addAll(u.getAllProjects());
        }
        return projects;
    }

    public int[][] getProjectNumbers(){
        ArrayList<Project> allProjects = getAllProjects();
        int[][] array = new int[allProjects.size()][2];
        int id = 0;

        for(Project p:getAllProjects()){
            array[id][0] = p.getId();
            array[id][1] = p.getAllTasks().size();
            id++;
        }

        return array;
    }

    public void createTask(Project project, String title, String creator) {
        try{
            PreparedStatement prep = con.prepareStatement("INSERT INTO `task` (`id`, `title`, `project_id`, `responsible_user`) "
                    + "VALUES(NULL, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            prep.setString(1, title);
            prep.setInt(2, project.getId());
            prep.setString(3, creator);
            prep.executeUpdate();
            prep.close();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public ArrayList<User> getDeactivatedUsers() {
        ArrayList<User> deactivatedUsers = new ArrayList();
        for(User user:users){
            if(!user.isActive())
                deactivatedUsers.add(user);
        }
        return deactivatedUsers;
    }

    public ArrayList<User>  getActiveUsers() {
        ArrayList<User> activaUsers = new ArrayList();
        for(User user:users){
            if(user.isActive())
                activaUsers.add(user);
        }
        return activaUsers;
    }

    public void deactivateAccount(User user) {
        if(user.isActive())
            user.setActive(false);
        else
            user.setActive(true);
    }

    public void removeUser(User user){
        users.remove(user);
    }

    public User getUserByLogin(String login) {
        User user = new User();
        try{
            PreparedStatement prep = con.prepareStatement("SELECT * FROM `user` WHERE `login` = ?", Statement.RETURN_GENERATED_KEYS);
            prep.setString(1, login);
            ResultSet data = prep.executeQuery();
            while(data.next()){
                user.setId(data.getInt("id"));
                user.setLogin(data.getString("login"));
                user.setPassword(data.getString("password"));
                user.setRating(data.getInt("rating"));
                user.setActive(data.getBoolean("activated"));
            }
            data.close();
            prep.close();
        }catch(Exception ex){
            throw null;
        }
        return user;
    }

    public boolean isLoginOcupied(String login) {
        try{
            PreparedStatement ps = con.prepareStatement("SELECT * FROM user WHERE login=?", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, login);
            ResultSet duom = ps.executeQuery();
            while(duom.next()){
                duom.close();
                ps.close();
                return true;
            }
            duom.close();
            ps.close();
            return false;

        }catch(Exception ex){
            ex.printStackTrace();
        }
        return true;
    }

    public ArrayList<Project> getAllUsersProjects(User user) {
        ArrayList<Project> allProjects = new ArrayList();
        try{
            PreparedStatement prep = con.prepareStatement("SELECT * FROM `project` INNER JOIN `assigned_projects` "
                    + "ON `project`.`id` = `assigned_projects`.`project_id` WHERE `assigned_projects`.`user_id` = ?", Statement.RETURN_GENERATED_KEYS);
            prep.setInt(1, user.getId());
            ResultSet data = prep.executeQuery();
            while(data.next()){
                Project project = new Project();
                project.setId(data.getInt("id"));
                project.setTitle(data.getString("title"));
                project.setProjectCreator(data.getString("project_creator"));
                project.setStartDate(data.getDate("start_date"));
                project.setEndDate(data.getDate("end_date"));
                String status = data.getString("status");
                if("InProgress".equals(status))
                    project.setStatus(Project.State.InProgress);
                else
                    project.setStatus(Project.State.Finished);
                allProjects.add(project);
            }
            data.close();
            prep.close();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return allProjects;
    }

    public String getUserClass(User user) {
        try{
            PreparedStatement prep = con.prepareStatement("SELECT * FROM `user` INNER JOIN `person` "
                    + "ON `user`.`id` = `person`.`id` WHERE `user`.`id` = ?", Statement.RETURN_GENERATED_KEYS);
            prep.setInt(1, user.getId());
            ResultSet data = prep.executeQuery();
            int counter = 0;

            while(data.next())
                counter ++;

            if(counter == 1){
                return "Person";
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return "Company";
    }

    public Person getPerson(User user) {
        Person person = new Person();
        try{
            PreparedStatement prep = con.prepareStatement("SELECT * FROM `person` WHERE `id` = ?", Statement.RETURN_GENERATED_KEYS);
            prep.setInt(1, user.getId());
            ResultSet data = prep.executeQuery();

            while(data.next()){
                person.setId(data.getInt("id"));
                person.setName(data.getString("name"));
                person.setLastname(data.getString("lastname"));
            }
            data.close();
            prep.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return person;
    }

    public Company getCompany(User user) {
        Company company = new Company();
        try{
            PreparedStatement prep = con.prepareStatement("SELECT * FROM `company` WHERE `id` = ?", Statement.RETURN_GENERATED_KEYS);
            prep.setInt(1, user.getId());
            ResultSet data = prep.executeQuery();

            while(data.next()){
                company.setId(data.getInt("id"));
                company.setTitle(data.getString("title"));
                company.setContactPerson(data.getString("contact_person"));
            }
            data.close();
            prep.close();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return company;
    }

    public void updatePersonData(Person person, String name, String lastname) {
        try{
            PreparedStatement prep = con.prepareStatement("UPDATE `person` SET `name` = ?, `lastname` = ? WHERE `id` = ?");
            prep.setString(1, name);
            prep.setString(2, lastname);
            prep.setInt(3, person.getId());
            prep.executeUpdate();
            prep.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void updateCompany(Company company, String title, String contactPerson) {
        try{
            PreparedStatement prep = con.prepareStatement("UPDATE `company` SET `title` = ?, `contact_person` = ? "
                    + "WHERE `id` = ?");
            prep.setString(1, title);
            prep.setString(2, contactPerson);
            prep.setInt(3, company.getId());
            prep.executeUpdate();
            prep.close();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void updateUser(User user, String login, String password, boolean active, int rating) {
        try{
            PreparedStatement prep = con.prepareStatement("UPDATE `user` SET `login` = ?, `password` = ?, `activated` = ?, `rating` = ? WHERE `user`.`id` = ?");
            prep.setString(1, login);
            prep.setString(2, password);
            prep.setBoolean(3, active);
            prep.setInt(4, rating);
            prep.setInt(5, user.getId());
            prep.executeUpdate();
            prep.close();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public User getUserById(int userId) {
        User user = null;
        try{
            PreparedStatement prep = con.prepareStatement("SELECT * FROM `user` WHERE `id` = ?");
            prep.setInt(1, userId);
            ResultSet data = prep.executeQuery();
            while(data.next()){
                user = new User(data.getString("login"), data.getString("password"));
                user.setId(data.getInt("id"));
                user.setRating(data.getInt("rating"));
                user.setActive(data.getBoolean("activated"));
            }
            data.close();
            prep.close();
        }catch(Exception ex){
            return null;
        }
        return user;
    }
}