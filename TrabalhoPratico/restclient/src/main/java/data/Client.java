package data;

public class Client extends Person{

    private String manager_person_email;

    public Client(String email, String name, String manager_person_email) {
        super(email, name);
        this.manager_person_email = manager_person_email;
    }

    public String getManager_person_email() {
        return manager_person_email;
    }

    public void setManager_person_email(String manager_person_email) {
        this.manager_person_email = manager_person_email;
    }
}
