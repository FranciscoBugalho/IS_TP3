package database.dbUtils;

public interface InsertConstants {
    public static final String INSERT_PERSON = "INSERT INTO person (email, name) VALUES (?, ?)";
    public static final String INSERT_MANAGER = "INSERT INTO manager (person_email) VALUES (?)";
    public static final String INSERT_CLIENT = "INSERT INTO client (person_email, manager_person_email) VALUES (?, ?)";
    public static final String INSERT_CURRENCY = "INSERT INTO currency (name, to_euro) VALUES (?, ?)";
}
