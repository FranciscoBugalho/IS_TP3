package database;

import data.Client;
import data.Currency;
import data.Manager;
import data.Person;
import database.dbUtils.DbConnectionConstants;
import database.dbUtils.InsertConstants;
import database.dbUtils.SelectConstants;

import java.sql.*;
import java.util.*;

public class Database implements DbConnectionConstants, SelectConstants, InsertConstants {

    private final Connection dbConnection;

    public Database() throws SQLException {
        dbConnection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
    }

    public void saveManager(Manager manager) throws SQLException {
        savePerson(new Person(manager.getEmail(), manager.getName()));

        PreparedStatement ps = dbConnection.prepareStatement(INSERT_MANAGER);
        ps.setString(1, manager.getEmail());

        ps.executeUpdate();
        ps.close();
    }

    public void saveClient(Client client) throws SQLException {
        savePerson(new Person(client.getEmail(), client.getName()));

        PreparedStatement ps = dbConnection.prepareStatement(INSERT_CLIENT);
        ps.setString(1, client.getEmail());
        ps.setString(2, client.getManager_person_email());

        ps.executeUpdate();
        ps.close();
    }

    private void savePerson(Person person) throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(INSERT_PERSON);
        ps.setString(1, person.getEmail());
        ps.setString(2, person.getName());

        ps.executeUpdate();
        ps.close();
    }

    public void saveCurrency(Currency currency) throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(INSERT_CURRENCY);
        ps.setString(1, currency.getName());
        ps.setFloat(2, currency.getTo_euro());

        ps.executeUpdate();
        ps.close();
    }

    public List<String> getAllManagers() throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(SELECT_ALL_MANAGERS);

        List<String> managerData = new ArrayList<>();
        ResultSet rSet = ps.executeQuery();
        while (rSet.next())
            managerData.add(rSet.getString(1) + " - " + rSet.getString(2));
        ps.close();

        return managerData;
    }

    public List<String> getAllClients() throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(SELECT_ALL_CLIENTS);

        List<String> clientData = new ArrayList<>();
        ResultSet rSet = ps.executeQuery();
        while (rSet.next())
            clientData.add(rSet.getString(1) + " - " + rSet.getString(2) + " (Manager: " + rSet.getString(3) + ")");
        ps.close();

        return clientData;
    }

    public List<String> getAllCurrencies() throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(SELECT_ALL_CURRENCIES);

        List<String> currencyData = new ArrayList<>();
        ResultSet rSet = ps.executeQuery();
        while (rSet.next())
            currencyData.add(rSet.getString(1) + " (" + rSet.getFloat(2) + "€)");
        ps.close();

        return currencyData;
    }

    public List<String> getCreditsPerClient() throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(SELECT_ALL_CREDITS_PER_CLIENT);

        List<String> creditsData = new ArrayList<>();
        ResultSet rSet = ps.executeQuery();
        String auxStr;
        while (rSet.next()) {
            if (rSet.getFloat(2) == 0)
                auxStr = " has no credits!";
            else
                auxStr = " (" + rSet.getFloat(2) + "€)";
            creditsData.add(rSet.getString(1) + auxStr);
        }
        ps.close();

        return creditsData;
    }

    public List<String> getPaymentsPerClient() throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(SELECT_ALL_PAYMENTS_PER_CLIENT);

        List<String> paymentsData = new ArrayList<>();
        ResultSet rSet = ps.executeQuery();
        String auxStr;
        while (rSet.next()) {
            if (rSet.getFloat(2) == 0)
                auxStr = " has no payments!";
            else
                auxStr = " (" + rSet.getFloat(2) + "€)";
            paymentsData.add(rSet.getString(1) + auxStr);
        }
        ps.close();

        return paymentsData;
    }

    private List<Client> getClientList() throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(SELECT_ALL_CLIENTS);

        List<Client> clients = new ArrayList<>();
        ResultSet rSet = ps.executeQuery();
        while (rSet.next())
            clients.add(new Client(rSet.getString(2), rSet.getString(1), rSet.getString(3)));
        ps.close();

        return clients;
    }

    public float getClientBalance(String clientEmail) throws SQLException {
        PreparedStatement ps = dbConnection.prepareStatement(SELECT_CLIENT_BALANCE_BY_EMAIL);
        ps.setString(1, clientEmail);

        float balance = 0;
        ResultSet rSet = ps.executeQuery();
        while (rSet.next())
            balance = rSet.getFloat(1);
        ps.close();

        return balance;
    }

    public String getTotalPurchaseInfo(int type) throws SQLException {
        float total = 0;
        PreparedStatement ps = null;

        switch (type) {
            case 0 -> ps = dbConnection.prepareStatement(SELECT_TOTAL_CREDITS);
            case 1 -> ps = dbConnection.prepareStatement(SELECT_TOTAL_PAYMENTS);
            case 2 -> ps = dbConnection.prepareStatement(SELECT_TOTAL_BALANCE);
        };

        if (ps != null) {
            ResultSet rSet = ps.executeQuery();
            rSet.next();
            total = rSet.getFloat(1);
        }

        return String.valueOf(total);
    }

    public List<String> getBillFromLastMonth() throws SQLException {
        List<String> bills = new ArrayList<>();

        PreparedStatement ps = dbConnection.prepareStatement(SELECT_BILL_PER_CLIENT);

        ResultSet rSet = ps.executeQuery();
        while (rSet.next())
            bills.add(rSet.getString(1) + " - " + rSet.getFloat(2) + "€ (" + ((rSet.getInt(3) == 1) ? "payed" : "not payed") + ")");
        ps.close();

        return bills;
    }

    public List<String> getClientsWithoutPayments() throws SQLException {
        List<String> paymentsLastMonths = new ArrayList<>();

        PreparedStatement ps = dbConnection.prepareStatement(SELECT_WHO_HAS_PAYMENTS_LAST_MONTHS);

        ResultSet rSet = ps.executeQuery();
        while (rSet.next())
            paymentsLastMonths.add(rSet.getString(1));
        ps.close();

        return paymentsLastMonths;
    }

    public String getClientWithLowestBalance() throws SQLException {
        List<Client> clients = getClientList();
        if (clients.isEmpty())
            return "There are no clients in the system!\n";

        PreparedStatement ps = dbConnection.prepareStatement(SELECT_CLIENT_WITH_LOWEST_BALANCE);

        StringBuilder info = new StringBuilder();
        ResultSet rSet = ps.executeQuery();
        while (rSet.next())
            info.append(rSet.getString(2)).append(" - ")
                    .append(rSet.getString(3)).append(" (Manager: ").append(rSet.getString(4))
                    .append(")\nBalance = ").append(rSet.getFloat(1)).append("€\n");
        ps.close();

        return info.toString();
    }

    public String getManagerWithTheHighestRevenue() throws SQLException {
        List<String> managers = getAllManagers();
        String str = "There are no managers in the system!\n";
        if (managers.isEmpty())
            return str;

        PreparedStatement ps = dbConnection.prepareStatement(SELECT_HIGHEST_REVENUE);
        ResultSet rSet = ps.executeQuery();
        while (rSet.next())
            str = rSet.getString(1) + " (" + rSet.getString(2) + ") \nRevenue: " + rSet.getString(3) + "€";
        ps.close();

        return str;
    }
}
