package book;

import data.Currency;
import data.Manager;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class ManageData {
    public ManageData() { }

    public String addManager(String managerName, String managerEmail) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/rest/services/meiBanking/addManager");
        Manager m = new Manager(managerEmail, managerName);
        Entity<Manager> input = Entity.entity(m, MediaType.APPLICATION_JSON);
        Response response = target.request().post(input);
        String value = response.readEntity(String.class);
        response.close();

        return value;
    }

    public String addClient(String clientName, String clientEmail, String managerData) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/rest/services/meiBanking/addClient");
        String[] managerDataSplit = managerData.split(" - ");
        data.Client c = new data.Client(clientEmail, clientName, managerDataSplit[1]);
        Entity<data.Client> input = Entity.entity(c, MediaType.APPLICATION_JSON);
        Response response = target.request().post(input);
        String value = response.readEntity(String.class);
        response.close();

        return value;
    }

    public String addCurrency(String currencyName, float currencyInEuros) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/rest/services/meiBanking/addCurrency");
        Currency c = new Currency(currencyName, currencyInEuros);
        Entity<Currency> input = Entity.entity(c, MediaType.APPLICATION_JSON);
        Response response = target.request().post(input);
        String value = response.readEntity(String.class);
        response.close();

        return value;
    }

    public List<String> getAllInformation(String type) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/rest/services/meiBanking/getAllInformation");
        target = target.queryParam("name", type);
        Response response = target.request().get();
        List<String> value = response.readEntity(List.class);
        response.close();

        return value;
    }

    public List<String> getAllPurchaseInformation(String type) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/rest/services/meiBanking/getAllPurchaseInformation");
        target = target.queryParam("name", type);
        Response response = target.request().get();
        List<String> value = response.readEntity(List.class);
        response.close();

        return value;
    }

    public String getClientBalance(String clientInfo) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/rest/services/meiBanking/getClientBalance");
        String[] clientDataSplit = clientInfo.split(" ");
        target = target.queryParam("name", clientDataSplit[2]);
        Response response = target.request().get();
        String value = response.readEntity(String.class);
        response.close();

        return value;
    }

    public String getTotalPurchaseInfo(String type) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/rest/services/meiBanking/getTotalPurchaseInfo");
        target = target.queryParam("name", type);
        Response response = target.request().get();
        String value = response.readEntity(String.class);
        response.close();

        return value;
    }

    public List<String> getBillFromLastMonth() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/rest/services/meiBanking/getBillFromLastMonth");
        Response response = target.request().get();
        List<String> value = response.readEntity(List.class);
        response.close();

        return value;
    }

    public List<String> getClientsWithoutPayments() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/rest/services/meiBanking/getClientsWithoutPayments");
        Response response = target.request().get();
        List<String> value = response.readEntity(List.class);
        response.close();

        return value;
    }

    public String getTheLowestBalance() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/rest/services/meiBanking/getTheLowestBalance");
        Response response = target.request().get();
        String value = response.readEntity(String.class);
        response.close();

        return value;
    }

    public String getManagerWithTheHighestRevenue() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/rest/services/meiBanking/getManagerWithTheHighestRevenue");
        Response response = target.request().get();
        String value = response.readEntity(String.class);
        response.close();

        return value;
    }
}
