package database.dbUtils;

public interface SelectConstants {
    public static final String SELECT_ALL_MANAGERS = "SELECT p.name, m.person_email FROM manager m JOIN person p ON p.email = m.person_email";

    public static final String SELECT_ALL_CLIENTS = "SELECT p.name, c.person_email, c.manager_person_email FROM client c JOIN person p ON p.email = c.person_email";

    public static final String SELECT_ALL_CURRENCIES = "SELECT name, to_euro FROM currency";

    public static final String SELECT_ALL_CREDITS_PER_CLIENT = "SELECT p.name, credits FROM client JOIN person p ON p.email = person_email";

    public static final String SELECT_ALL_PAYMENTS_PER_CLIENT = "SELECT p.name, payments FROM client JOIN person p ON p.email = person_email";

    public static final String SELECT_CLIENT_BALANCE_BY_EMAIL = "SELECT balance FROM client WHERE person_email = ?";

    public static final String SELECT_TOTAL_CREDITS = "SELECT SUM(credits) FROM client";

    public static final String SELECT_TOTAL_PAYMENTS = "SELECT SUM(payments) FROM client";

    public static final String SELECT_TOTAL_BALANCE = "SELECT SUM(balance) FROM client";

    public static final String SELECT_BILL_PER_CLIENT = "SELECT p.name, bill, payed FROM client JOIN person p ON p.email = person_email";

    public static final String SELECT_WHO_HAS_PAYMENTS_LAST_MONTHS = "SELECT p.name FROM client JOIN person p ON p.email = person_email WHERE payments_last_months = 0";

    public static final String SELECT_CLIENT_WITH_LOWEST_BALANCE = "SELECT MIN(c.balance), p.name, c.person_email, c.manager_person_email FROM client c JOIN person p ON p.email = c.person_email";

    public static final String SELECT_HIGHEST_REVENUE = "SELECT p.name, m.person_email, MAX(revenues) FROM manager m JOIN person p ON p.email = m.person_email";
}
