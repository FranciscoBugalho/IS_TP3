package book;

import data.Client;
import data.Currency;
import data.Manager;
import database.Database;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
@Path("/meiBanking")
@Produces(MediaType.APPLICATION_JSON)
public class MEIBanking {

    @POST
    @Path("/addManager")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addManager(Manager manager) {
        System.out.println("Trying to add a new manager (" + manager.getEmail() + ")...");
        String str;

        try {
            Database db = new Database();
            db.saveManager(manager);
            str = "Manager successfully added!\n";
        } catch (SQLException e) {
            str = "Error adding manager!";
            System.out.println(e);
            Response.status(Response.Status.EXPECTATION_FAILED).entity(str).build();
        }
        System.out.println(str);
        return Response.status(Response.Status.OK).entity(str).build();
    }

    @POST
    @Path("/addClient")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addClient(Client client) {
        System.out.println("Trying to add a new client (" + client.getEmail() + ")...");
        String str;

        try {
            Database db = new Database();
            db.saveClient(client);
            str = "Client successfully added!\n";
        } catch (SQLException e) {
            str = "Error adding client!";
            Response.status(Response.Status.EXPECTATION_FAILED).entity(str).build();
        }
        System.out.println(str);
        return Response.status(Response.Status.OK).entity(str).build();
    }

    @POST
    @Path("/addCurrency")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addClient(Currency currency) {
        System.out.println("Trying to add a new currency (" + currency.getName() + ")...");
        String str;

        try {
            Database db = new Database();
            db.saveCurrency(currency);
            str = "Currency successfully added!\n";
        } catch (SQLException e) {
            str = "Error adding currency!";
            Response.status(Response.Status.EXPECTATION_FAILED).entity(str).build();
        }
        System.out.println(str);
        return Response.status(Response.Status.OK).entity(str).build();
    }

    @GET
    @Path("/getAllInformation")
    public List<String> getAllInformation(@QueryParam("name") String type) {
        System.out.println("Getting all " + type + " information...");
        List<String> data = new ArrayList<>();
        try {
            Database db = new Database();
            switch (type) {
                case RestConstants.GET_ALL_MANAGERS_INFORMATION -> data = db.getAllManagers();
                case RestConstants.GET_ALL_CLIENTS_INFORMATION -> data = db.getAllClients();
                case RestConstants.GET_ALL_CURRENCIES_INFORMATION -> data = db.getAllCurrencies();
                default -> { }
            }
            if (data.isEmpty())
                return null;
        } catch (SQLException e) {
            return null;
        }
        return data;
    }

    @GET
    @Path("/getAllPurchaseInformation")
    public List<String> getAllPurchaseInformation(@QueryParam("name") String type) {
        System.out.println("Getting all " + type + " information...");
        List<String> data = new ArrayList<>();
        try {
            Database db = new Database();
            switch (type) {
                case RestConstants.GET_ALL_CREDIT_INFORMATION -> data = db.getCreditsPerClient();
                case RestConstants.GET_ALL_PAYMENT_INFORMATION -> data = db.getPaymentsPerClient();
                default -> { }
            }
            if (data.isEmpty())
                return null;
        } catch (SQLException e) {
            return null;
        }
        return data;
    }

    @GET
    @Path("/getClientBalance")
    public Response getClientBalance(@QueryParam("name") String clientEmail) {
        System.out.println("Getting balance from " + clientEmail + "...");
        String str;
        try {
            Database db = new Database();
            float balance = db.getClientBalance(clientEmail);
            str = "Client (" + clientEmail + ") balance is " + balance + "€";
        } catch (SQLException e) {
            str = "Error getting client balance!";
            Response.status(Response.Status.EXPECTATION_FAILED).entity(str).build();
        }
        System.out.println(str);
        return Response.status(Response.Status.OK).entity(str).build();
    }

    @GET
    @Path("/getTotalPurchaseInfo")
    public String getTotalPurchaseInfo(@QueryParam("name") String type) {
        System.out.println("Getting total " + type + "...");
        String data = null;
        try {
            Database db = new Database();
            switch (type) {
                case RestConstants.GET_ALL_CREDIT_INFORMATION -> data = db.getTotalPurchaseInfo(0);
                case RestConstants.GET_ALL_PAYMENT_INFORMATION -> data = db.getTotalPurchaseInfo(1);
                case RestConstants.GET_ALL_BALANCE_INFORMATION -> data = db.getTotalPurchaseInfo(2);
            }
        } catch (SQLException e) {
            return null;
        }
        return data;
    }

    @GET
    @Path("/getBillFromLastMonth")
    public List<String> getBillFromLastMonth() {
        System.out.println("Getting the bill for all clients from the last month...");
        List<String> data = new ArrayList<>();
        try {
            Database db = new Database();
            data = db.getBillFromLastMonth();
            if (data.isEmpty())
                return null;
        } catch (SQLException e) {
            return null;
        }
        return data;
    }

    @GET
    @Path("/getClientsWithoutPayments")
    public List<String> getClientsWithoutPayments() {
        System.out.println("Getting clients without payments...");
        List<String> data = new ArrayList<>();
        try {
            Database db = new Database();
            data = db.getClientsWithoutPayments();
            if (data.isEmpty())
                return null;
        } catch (SQLException e) {
            return null;
        }
        return data;
    }

    @GET
    @Path("/getTheLowestBalance")
    public Response getTheLowestBalance() {
        System.out.println("Getting the client with the lowest balance...");
        String str;
        try {
            Database db = new Database();
            str = db.getClientWithLowestBalance();
        } catch (SQLException e) {
            str = "Error getting client balance!";
            Response.status(Response.Status.EXPECTATION_FAILED).entity(str).build();
        }
        System.out.println(str);
        return Response.status(Response.Status.OK).entity(str).build();
    }

    @GET
    @Path("/getManagerWithTheHighestRevenue")
    public Response getManagerWithTheHighestRevenue() {
        System.out.println("Getting the manager with the highest revenue...");
        String str;
        try {
            Database db = new Database();
            str = db.getManagerWithTheHighestRevenue();
        } catch (SQLException e) {
            str = "Error getting client balance!";
            Response.status(Response.Status.EXPECTATION_FAILED).entity(str).build();
        }
        System.out.println(str);
        return Response.status(Response.Status.OK).entity(str).build();
    }
}
