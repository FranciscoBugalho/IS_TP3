package book;

import Utils.Utils;
import Utils.ASCIIArtGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {

    public Client() { }

    public void runClient() {
        userInterface();
    }

    public void userInterface() {
        ManageData manageData = new ManageData();
        int opt = -1;
        String result = null;
        Scanner scanner = new Scanner(System.in);

        displayTitle();

        do {
            do {
                System.out.println("Do you wish to:");
                System.out.println(" 1. Add a Manager");
                System.out.println(" 2. Add a Client");
                System.out.println(" 3. Add a Currency");
                System.out.println(" 4. List all Managers");
                System.out.println(" 5. List all Clients");
                System.out.println(" 6. List all Currencies");
                System.out.println(" 7. Get the credit per client");
                System.out.println(" 8. Get the payments per client");
                System.out.println(" 9. Get the current balance of a client");
                System.out.println("10. Get the total credits");
                System.out.println("11. Get the total payments");
                System.out.println("12. Get the total balance");
                System.out.println("13. Get the bill for each client for the last month");
                System.out.println("14. Get the list of clients without payments for the last two months");
                System.out.println("15. Get the person with the highest outstanding debt");
                System.out.println("16. Get the manager who has made the highest revenue in payments from his/her clients");
                System.out.println(" 0. Exit\n");
                System.out.print("Opt: ");

                while (!scanner.hasNextInt()) {
                    System.out.print("Opt: ");
                    scanner.next();
                }
                opt = scanner.nextInt();

                if (opt < 0 || opt > 16)
                    System.out.println("\033[0;31mInvalid option!\033[0m\n");
            } while (opt < 0 || opt > 16);

            switch (opt) {
                case 0 -> {
                    System.out.println("See you next time! \033[1;33m\uD83D\uDC4B\033[0m");
                    return;
                }
                case 1 -> {
                    scanner.nextLine();
                    System.out.print("\nManager name: ");
                    String managerName = scanner.nextLine();
                    String managerEmail = null;
                    do {
                        System.out.print("Manager email: ");
                        managerEmail = scanner.nextLine();
                    } while (!Utils.isValid(managerEmail));
                    result = manageData.addManager(managerName, managerEmail);
                    System.out.println(result + "\n");
                }
                case 2 -> {
                    List<String> managersData = manageData.getAllInformation("managers");
                    if (managersData == null || managersData.isEmpty()) {
                        System.out.println("\n\033[0;31mTo add a client you have to add managers first!\033[0m\n");
                        break;
                    }
                    scanner.nextLine();
                    System.out.print("\nClient name: ");
                    String clientName = scanner.nextLine();
                    String clientEmail = null;
                    do {
                        System.out.print("Client email: ");
                        clientEmail = scanner.nextLine();
                    } while (!Utils.isValid(clientEmail));
                    int managerNumber = -1;
                    do {
                        System.out.println("Select the client manager: ");
                        int counter = 1;
                        for (String str : managersData) {
                            System.out.println(counter + ". " + str);
                            counter++;
                        }

                        System.out.print("Opt: ");
                        while (!scanner.hasNextInt()) {
                            System.out.print("Opt: ");
                            scanner.next();
                        }
                        managerNumber = scanner.nextInt();

                        if (managerNumber < 1 || managerNumber > managersData.size())
                            System.out.println("\033[0;31mInvalid option!\033[0m\n");
                    } while (managerNumber < 1 || managerNumber > managersData.size());
                    result = manageData.addClient(clientName, clientEmail, managersData.get(managerNumber - 1));
                    System.out.println(result + "\n");
                }
                case 3 -> {
                    scanner.nextLine();
                    System.out.print("\nCurrency name: ");
                    String currencyName = scanner.nextLine();
                    float currencyInEuros = -1;
                    do {
                        System.out.print("Currency value (in euros): ");
                        while (!scanner.hasNextFloat()) {
                            System.out.print("Currency value (in euros): ");
                            scanner.next();
                        }
                        currencyInEuros = scanner.nextFloat();
                        if (currencyInEuros < 0)
                            System.out.println("\033[0;31mCurrency value should be bigger than 0€!\033[0m\n");
                    } while (currencyInEuros < 0);
                    result = manageData.addCurrency(currencyName, currencyInEuros);
                    System.out.println(result + "\n");
                }
                case 4 -> {
                    List<String> managers = manageData.getAllInformation("managers");
                    displayDBInfo(managers, 4);
                    backToMenu(scanner);
                }
                case 5 -> {
                    List<String> clients = manageData.getAllInformation("clients");
                    displayDBInfo(clients, 5);
                    backToMenu(scanner);
                }
                case 6 -> {
                    List<String> currencies = manageData.getAllInformation("currencies");
                    displayDBInfo(currencies, 6);
                    backToMenu(scanner);
                }
                case 7 -> {
                    List<String> creditPerClient = manageData.getAllPurchaseInformation("credits");
                    displayDBInfo(creditPerClient, 7);
                    backToMenu(scanner);
                }
                case 8 -> {
                    List<String> paymentPerClient = manageData.getAllPurchaseInformation("payments");
                    displayDBInfo(paymentPerClient, 8);
                    backToMenu(scanner);
                }
                case 9 -> {
                    List<String> clientData = manageData.getAllInformation("clients");
                    if (clientData == null || clientData.isEmpty()) {
                        System.out.println("\033[0;31mThere are no clients in the system!\033[0m\n");
                        backToMenu(scanner);
                        break;
                    }
                    int clientNumber = -1;
                    do {
                        System.out.println("Select the client: ");
                        int counter = 1;
                        for (String str : clientData) {
                            System.out.println(counter + ". " + str);
                            counter++;
                        }

                        System.out.print("Opt: ");
                        while (!scanner.hasNextInt()) {
                            System.out.print("Opt: ");
                            scanner.next();
                        }
                        clientNumber = scanner.nextInt();

                        if (clientNumber < 1 || clientNumber > clientData.size())
                            System.out.println("\033[0;31mInvalid option!\033[0m\n");
                    } while (clientNumber < 1 || clientNumber > clientData.size());
                    result = manageData.getClientBalance(clientData.get(clientNumber - 1));
                    System.out.println(result + "\n");
                    backToMenu(scanner);
                }
                case 10 -> {
                    result = manageData.getTotalPurchaseInfo("credits");
                    System.out.println("Total credits: " + result + "€\n");
                    backToMenu(scanner);
                }
                case 11 -> {
                    result = manageData.getTotalPurchaseInfo("payments");
                    System.out.println("Total payments: " + result + "€\n");
                    backToMenu(scanner);
                }
                case 12 -> {
                    result = manageData.getTotalPurchaseInfo("balance");
                    System.out.println("Total balance: " + result + "€\n");
                    backToMenu(scanner);
                }
                case 13 -> {
                    List<String> billPerClient = manageData.getBillFromLastMonth();
                    displayDBInfo(billPerClient, 13);
                    backToMenu(scanner);
                }
                case 14 -> {
                    List<String> listOfClients = manageData.getClientsWithoutPayments();
                    displayDBInfo(listOfClients, 14);
                    backToMenu(scanner);
                }
                case 15 -> {
                    result = manageData.getTheLowestBalance();
                    if (result.equalsIgnoreCase("There are no clients in the system!\n"))
                        System.out.println("\033[0;31m" + result + "\033[0m");
                    else
                        System.out.println("The person with the lowest balance: \n" + result);
                    backToMenu(scanner);
                }
                case 16 -> {
                    result = manageData.getManagerWithTheHighestRevenue();
                    if (result.equalsIgnoreCase("There are no managers in the system!\n"))
                        System.out.println("\033[0;31m" + result + "\033[0m");
                    else
                        System.out.println("The manager with the highest revenue: \n" + result + "\n");
                    backToMenu(scanner);
                }
                default -> {
                    System.out.println("\033[0;31mError, try again later!\033[0m");
                    return;
                }
            }
        } while (true);
    }

    private void displayDBInfo(List<String> info, int type) {
        if (info == null || info.isEmpty()) {
            switch (type) {
                case 4 -> System.out.println("\033[0;31mThere are no managers in the system!\033[0m\n");
                case 5, 7, 8, 13, 14 -> System.out.println("\033[0;31mThere are no clients in the system which satisfy this condition!\033[0m\n");
                case 6 -> System.out.println("\033[0;31mThere are no currencies in the system!\033[0m\n");
            }
        } else {
            switch (type) {
                case 4 -> System.out.print("\nManager's Information: \n");
                case 5 -> System.out.print("\nClient's Information: \n");
                case 6 -> System.out.print("\nCurrencies Information: \n");
                case 7 -> System.out.print("\nCredit per client: \n");
                case 8 -> System.out.print("\nPayments per client: \n");
                case 13 -> System.out.print("\nBill per client (last month): \n");
                case 14 -> System.out.print("\nClients without payments: \n");
            }
            for (String str : info)
                System.out.println(str);
            System.out.println();
        }
    }

    private void backToMenu(Scanner scanner) {
        int exit;
        do {
            System.out.print("Press 0 to go back to menu... ");
            while (!scanner.hasNextInt()) {
                System.out.print("Press 0 to go back to menu... ");
                scanner.next();
            }
            exit = scanner.nextInt();
        } while (exit != 0);
        System.out.println();
    }

    private void displayTitle() {
        ASCIIArtGenerator asciiArtGenerator = new ASCIIArtGenerator();
        try {
            System.out.println("\033[0;32m");
            asciiArtGenerator.printTextArt("Welcome Admin!", ASCIIArtGenerator.ART_SIZE_MEDIUM);
            System.out.println("\033[0m\n");
        } catch (Exception e) {
            System.out.println("----------- \033[0;32mWelcome Admin!\033[0m -----------\n");
        }
    }
}
