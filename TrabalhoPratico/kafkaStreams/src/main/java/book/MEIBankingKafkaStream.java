package book;

import data.Client;
import data.Currency;
import data.Manager;
import data.schemas;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class MEIBankingKafkaStream {

    static private List<Client> clientsList = new ArrayList<>();
    static private List<Currency> currenciesList = new ArrayList<>();
    static private List<Manager> managersList = new ArrayList<>();

    private static boolean containsDuplicateClient(List<Client> clientsList, String clientEmail){
        for(Client i : clientsList){
            if(i.getEmail().equals(clientEmail))
                return true;
        }
        return false;
    }

    private static boolean containsDuplicateCurrency(List<Currency> currenciesList, String currencyName){
        for(Currency i : currenciesList){
            if(i.getName().equals(currencyName))
                return true;
        }
        return false;
    }

    private static boolean containsDuplicateManager(List<Manager> managersList, String managerEmail){
        for(Manager i : managersList){
            if(i.getEmail().equals(managerEmail))
                return true;
        }
        return false;
    }

    public static void kafkaConsumer(){
        //Assign topicNames to string variable
        String topicNameGetClients = "DB_Info_Topics_KafkaStreams";

        // create instance for properties to access producer configs
        Properties props = new Properties();

        //Assign localhost id
        props.put("bootstrap.servers", "localhost:9092");

        //Set acknowledgements for producer requests.
        props.put("acks", "all");

        //If the request fails, the producer can automatically retry,
        props.put("retries", 0);

        //Specify buffer size in config
        props.put("batch.size", 16384);

        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);

        // Consumer (Receives Clients and Currencies)
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaDBClientConsumer");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        Consumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList(topicNameGetClients));

        // Auxiliar Variables
        Client auxClient;
        Currency auxCurrency;
        Manager auxManager;

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);    // Receives Clients and Currencies from DB
            for (ConsumerRecord<String, String> record : records) {                     // Processes Clients/Currencies received from DB
                JSONObject key = new JSONObject(record.key());
                if(key.has("manager_person_email")){    // It's a client
                    JSONObject clientJSON = new JSONObject(record.value());
                    if(!containsDuplicateClient(clientsList, clientJSON.getJSONObject("payload").getString("person_email"))){
                        auxClient = new Client(clientJSON.getJSONObject("payload").getString("person_email"),null,clientJSON.getJSONObject("payload").getString("manager_person_email"));
                        clientsList.add(auxClient);
                        System.out.println("Added Client: " + clientJSON.getJSONObject("payload").getString("person_email"));
                    }
                }
                else if(key.has("name")){  // It's a currency
                    JSONObject currencyJSON = new JSONObject(record.value());
                    if(!containsDuplicateCurrency(currenciesList, currencyJSON.getJSONObject("payload").getString("name"))){
                        auxCurrency = new Currency(currencyJSON.getJSONObject("payload").getString("name"), currencyJSON.getJSONObject("payload").getFloat("to_euro"));
                        currenciesList.add(auxCurrency);
                        System.out.println("Added Currency: " + currencyJSON.getJSONObject("payload").getString("name"));
                    }
                }
                else if(key.has("revenues")){  // It's a manager
                    JSONObject managerJSON = new JSONObject(record.value());
                    if(!containsDuplicateManager(managersList, managerJSON.getJSONObject("payload").getString("person_email"))){
                        auxManager = new Manager(managerJSON.getJSONObject("payload").getString("person_email"), null, managerJSON.getJSONObject("payload").getFloat("revenues"));
                        managersList.add(auxManager);
                        System.out.println("Added Manager: " + managerJSON.getJSONObject("payload").getString("person_email"));
                    }
                }
                //System.out.printf("offset = %d, key = %s, value = %s \n", record.offset(), record.key(), record.value());
            }

        }
    }

    public static Currency getCurrencyByName(String currencyName) {
        for(Currency i : currenciesList){
            if(i.getName().equals(currencyName)){
                return i;
            }
        }
        return null;
    }

    public static Client updateClientCredits(JSONObject value) {
        //System.out.println(value);
        Currency moeda = getCurrencyByName(value.getString("currency_name"));
        if(moeda == null || clientsList.isEmpty())
            return null;

        for(Client i : clientsList){
            if(i.getEmail().equals(value.getString("person_email"))){
                float entradaCredits = value.getFloat("price") * moeda.getTo_euro();
                i.setCredits(i.getCredits() + entradaCredits);
                i.setBalance(i.getBalance() - entradaCredits);
                System.out.println("Updated Client " + value.getString("person_email") +" credits to " + i.getCredits());
                return i;
            }
        }
        return null;
    }

    public static Client updateClientPayment(JSONObject value) {
        //System.out.println(value);
        Currency moeda = getCurrencyByName(value.getString("currency_name"));
        if(moeda == null || clientsList.isEmpty())
            return null;

        for(Client i : clientsList){
            if(i.getEmail().equals(value.getString("person_email"))){
                float saidaPayments = value.getFloat("price") * moeda.getTo_euro();
                i.setPayments(i.getPayments() + saidaPayments);
                i.setBalance(i.getBalance() + saidaPayments);
                System.out.println("Updated Client " + value.getString("person_email") +" payments to " + i.getPayments());
                return i;
            }
        }
        return null;
    }

    public static Manager updateManagerRevenue(JSONObject value) {
        //System.out.println(value);
        Currency moeda = getCurrencyByName(value.getString("currency_name"));
        if(moeda == null || managersList.isEmpty())
            return null;

        for(Manager i : managersList){
            if(i.getEmail().equals(value.getString("manager_email"))){
                i.setRevenues(i.getRevenues() + (value.getFloat("price") * moeda.getTo_euro()));
                System.out.println("Updated Manager " + value.getString("manager_email") +" revenue to " + i.getRevenues());
                return i;
            }
        }
        return null;
    }

    public static KTable<Windowed<String>, String> computeBill(KStream<String, String> creditLines, KStream<String, String> paymentLines){
        KTable<Windowed<String>, String> outWindowCreditLines = creditLines.groupByKey().windowedBy(TimeWindows.of(TimeUnit.SECONDS.toMillis(60)))
                        .reduce((oldval, newval) -> {        // New credit, update balance and credits
                            JSONObject oldvalJSON = new JSONObject(oldval);
                            JSONObject newvalJSON = new JSONObject(newval);
                            Currency newvalCurrency = getCurrencyByName(newvalJSON.getString("currency_name"));
                            Currency oldvalCurrency = getCurrencyByName(oldvalJSON.getString("currency_name"));

                            while(newvalCurrency == null || oldvalCurrency == null){
                                try {
                                    Thread.sleep(10000);
                                    newvalCurrency = getCurrencyByName(newvalJSON.getString("currency_name"));
                                    oldvalCurrency = getCurrencyByName(oldvalJSON.getString("currency_name"));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            //System.out.println("OLD Credit Win:" + oldval);
                            //System.out.println("NEW Credit Win:" + newval);

                            float newCreditsEuro = newvalJSON.getFloat("price") * newvalCurrency.getTo_euro();
                            if(oldvalJSON.getFloat("bill") == 0){
                                float oldCreditsEuro = oldvalJSON.getFloat("price") * oldvalCurrency.getTo_euro();
                                oldvalJSON.put("bill", oldCreditsEuro);
                            }

                            oldvalJSON.put("bill", oldvalJSON.getFloat("bill") + newCreditsEuro);    // Updates bill
                            return oldvalJSON.toString();
                        }
                );
        KTable<Windowed<String>, String> outWindowPaymentClientLines = paymentLines.filterNot((k,v) -> new JSONObject(k).getBoolean("manager"))
                .groupByKey().windowedBy(TimeWindows.of(TimeUnit.SECONDS.toMillis(60))).reduce((oldval, newval) -> { // New credit, update balance and credits

                            JSONObject oldvalJSON = new JSONObject(oldval);
                            JSONObject newvalJSON = new JSONObject(newval);
                            Currency newvalCurrency = getCurrencyByName(newvalJSON.getString("currency_name"));
                            Currency oldvalCurrency = getCurrencyByName(oldvalJSON.getString("currency_name"));

                            while(newvalCurrency == null || oldvalCurrency == null){
                                try {
                                    Thread.sleep(10000);
                                    newvalCurrency = getCurrencyByName(newvalJSON.getString("currency_name"));
                                    oldvalCurrency = getCurrencyByName(oldvalJSON.getString("currency_name"));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            //System.out.println("OLD Payment Win:" + oldval);
                            //System.out.println("NEW Payment Win:" + newval);

                            float newPaymentsEuro = newvalJSON.getFloat("price") * newvalCurrency.getTo_euro();
                            // Client
                            if(oldvalJSON.getFloat("bill") == 0){
                                float oldPaymentsEuro = oldvalJSON.getFloat("price") * oldvalCurrency.getTo_euro();
                                oldvalJSON.put("bill",  -oldPaymentsEuro);
                            }

                            oldvalJSON.put("bill", oldvalJSON.getFloat("bill") - newPaymentsEuro);        // Updates bill
                            return oldvalJSON.toString();
                        }
                );
        KTable<Windowed<String>, String> clientsBill = outWindowCreditLines.leftJoin(outWindowPaymentClientLines, (leftValueCredits, rightValuePayments) ->
                {
                    if(leftValueCredits == null && rightValuePayments == null){
                        return null;
                    } else if(leftValueCredits == null) {
                        JSONObject paymentsBillJSON = new JSONObject(rightValuePayments);

                        if(paymentsBillJSON.getFloat("bill") < 0){
                            paymentsBillJSON.put("payed", 0);
                        } else {  paymentsBillJSON.put("payed", 1); }
                        return paymentsBillJSON.toString();

                    } else if(rightValuePayments == null) {
                        JSONObject creditsBillJSON = new JSONObject(leftValueCredits);
                        if(creditsBillJSON.getFloat("bill") < 0){
                            creditsBillJSON.put("payed", 0);
                        } else {  creditsBillJSON.put("payed", 1); }
                        return creditsBillJSON.toString();
                    }
                    JSONObject creditsBillJSON = new JSONObject(leftValueCredits);
                    JSONObject paymentsBillJSON = new JSONObject(rightValuePayments);

                    creditsBillJSON.put("bill", creditsBillJSON.getFloat("bill") + paymentsBillJSON.getFloat("bill"));

                    if(creditsBillJSON.getFloat("bill") < 0){
                        creditsBillJSON.put("payed", 0);
                    } else {  creditsBillJSON.put("payed", 1); }

                    //System.out.println(creditsBillJSON.toString());

                    return creditsBillJSON.toString();
                }
        );
        return clientsBill;
    }

    public static KTable<Windowed<String>, String> computeNoPayments(KStream<String, String> paymentLines){
        KTable<Windowed<String>, Long> outWindowPaymentClientLines = paymentLines.filterNot((k,v) -> new JSONObject(k).getBoolean("manager"))
                .groupByKey().windowedBy(TimeWindows.of(TimeUnit.SECONDS.toMillis(60))).count();
        //outWindowPaymentClientLines.mapValues((k, v) -> k + " => " + v).toStream().map((key, value) -> KeyValue.pair(key.key(), value)).to("debugClient", Produced.with(Serdes.String(), Serdes.String()));
        return outWindowPaymentClientLines.mapValues((k, v) -> "" + v);
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        String topicNameCredits = "Credits_Topic";
        String topicNamePayments = "Payments_Topic";
        String outTopicClient = "client";
        String outTopicManager = "manager";

        //outTopicClient = "debugClient";
        //outTopicManager = "debugManager";

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "exercises-application");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();


        // Credits
        KStream<String, String> creditLines = builder.stream(topicNameCredits);
        KTable<String, String> outCreditLines = creditLines.groupByKey().
                reduce((oldval, newval) -> {        // New credit, update balance and credits
                            JSONObject oldvalJSON = new JSONObject(oldval);
                            JSONObject newvalJSON = new JSONObject(newval);
                            Currency newvalCurrency = getCurrencyByName(newvalJSON.getString("currency_name"));
                            Currency oldvalCurrency = getCurrencyByName(oldvalJSON.getString("currency_name"));

                            while(newvalCurrency == null || oldvalCurrency == null){
                                try {
                                    Thread.sleep(10000);
                                    newvalCurrency = getCurrencyByName(newvalJSON.getString("currency_name"));
                                    oldvalCurrency = getCurrencyByName(oldvalJSON.getString("currency_name"));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            //System.out.println("OLD Credit:" + oldval);
                            //System.out.println("NEW Credit:" + newval);

                            if(oldvalJSON.has("revenues")){
                                return newvalJSON.toString();
                            }

                            float newCreditsEuro = newvalJSON.getFloat("price") * newvalCurrency.getTo_euro();

                            if(oldvalJSON.getFloat("credits") == 0){
                                float oldCreditsEuro = oldvalJSON.getFloat("price") * oldvalCurrency.getTo_euro();
                                oldvalJSON.put("credits", oldCreditsEuro);
                                oldvalJSON.put("balance", oldCreditsEuro);
                            }

                            oldvalJSON.put("balance", oldvalJSON.getFloat("balance") + newCreditsEuro);    // Updates balance
                            oldvalJSON.put("credits", oldvalJSON.getFloat("credits") + newCreditsEuro);    // Updates credits

                            return oldvalJSON.toString();

                        }
                );

        // Payments
        KStream<String, String> paymentLines = builder.stream(topicNamePayments);

        // Updates Managers
        KTable<String, String> outPaymentManagersLines = paymentLines.filter((k,v) -> new JSONObject(k).getBoolean("manager")).groupByKey().
                reduce((oldval, newval) -> {
                            JSONObject oldvalJSON = new JSONObject(oldval);
                            JSONObject newvalJSON = new JSONObject(newval);
                            Currency newvalCurrency = getCurrencyByName(newvalJSON.getString("currency_name"));
                            Currency oldvalCurrency = getCurrencyByName(oldvalJSON.getString("currency_name"));

                            //System.out.println("OLD REVENUE:" + oldval);
                            //System.out.println("NEW REVENUE:" + newval);

                            while(newvalCurrency == null || oldvalCurrency == null){
                                try {
                                    Thread.sleep(10000);
                                    newvalCurrency = getCurrencyByName(newvalJSON.getString("currency_name"));
                                    oldvalCurrency = getCurrencyByName(oldvalJSON.getString("currency_name"));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            float newPaymentsEuro = newvalJSON.getFloat("price") * newvalCurrency.getTo_euro();

                            oldvalJSON.put("revenues", oldvalJSON.getFloat("revenues") + newPaymentsEuro);        // Updates balance

                            return oldvalJSON.toString();
                        }
                );
        outPaymentManagersLines.mapValues((k, v) ->{        // Atualiza managers na base de dados
                    JSONObject vJSON = new JSONObject(v);
                    JSONObject kJSON = new JSONObject(k);
                    String returnVal = new JSONObject(schemas.MANAGER_SCHEMA).put("payload", new JSONObject()
                            .put("person_email", kJSON.getString("email"))
                            .put("revenues", vJSON.getFloat("revenues"))
                    ).toString();
                    //System.out.println("Updated manager: " + returnVal);
                    return returnVal;
                }
        ).toStream().to(outTopicManager, Produced.with(Serdes.String(), Serdes.String()));

        // Updates Clients
        KTable<String, String> outPaymentClientLines = paymentLines.filterNot((k,v) -> new JSONObject(k).getBoolean("manager")).groupByKey().
                reduce((oldval, newval) -> {        // New credit, update balance and credits

                            JSONObject oldvalJSON = new JSONObject(oldval);
                            JSONObject newvalJSON = new JSONObject(newval);
                            Currency newvalCurrency = getCurrencyByName(newvalJSON.getString("currency_name"));
                            Currency oldvalCurrency = getCurrencyByName(oldvalJSON.getString("currency_name"));

                            //System.out.println("OLD Payment:" + oldval);
                            //System.out.println("NEW Payment:" + newval);

                            while(newvalCurrency == null || oldvalCurrency == null){
                                try {
                                    Thread.sleep(10000);
                                    newvalCurrency = getCurrencyByName(newvalJSON.getString("currency_name"));
                                    oldvalCurrency = getCurrencyByName(oldvalJSON.getString("currency_name"));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            float newPaymentsEuro = newvalJSON.getFloat("price") * newvalCurrency.getTo_euro();

                            //System.out.println("OLD:" + oldval);
                            //System.out.println("NeW:" + newval);

                            // Client
                            if(oldvalJSON.getFloat("payments") == 0){
                                float oldPaymentsEuro = oldvalJSON.getFloat("price") * oldvalCurrency.getTo_euro();
                                oldvalJSON.put("payments", oldPaymentsEuro);
                                oldvalJSON.put("balance",  -oldPaymentsEuro);
                            }

                            oldvalJSON.put("balance", oldvalJSON.getFloat("balance") - newPaymentsEuro);        // Updates balance
                            oldvalJSON.put("payments", oldvalJSON.getFloat("payments") + newPaymentsEuro);      // Updates payments

                            return oldvalJSON.toString();

                        }
                );

        // KTable com informacoes de Payments e Credits de clients
        KTable<String, String> clients = outCreditLines.leftJoin(outPaymentClientLines, (leftValueCredits, rightValuePayments) ->
                {
                    if(leftValueCredits == null && rightValuePayments == null){
                        return null;
                    } else if(rightValuePayments == null){
                        return leftValueCredits;
                    } else if(leftValueCredits == null){
                        return rightValuePayments;
                    }
                    JSONObject leftValueCreditsJSON = new JSONObject(leftValueCredits);
                    JSONObject rightValuePaymentsJSON = new JSONObject(rightValuePayments);

                    leftValueCreditsJSON.put("payments", rightValuePaymentsJSON.getFloat("payments"));
                    leftValueCreditsJSON.put("balance", leftValueCreditsJSON.getFloat("balance") + rightValuePaymentsJSON.getFloat("balance"));

                    return leftValueCreditsJSON.toString();
                }
        );

        // KTable com Client's bills
        KTable<Windowed<String>, String> clientsBill = computeBill(creditLines, paymentLines);
        KStream<String, String> clientsBillStream = clientsBill.toStream().map((key, value) -> KeyValue.pair(key.key(), value));
        KTable<String, String> clientsBillString = clientsBillStream.groupByKey().reduce((oldval, newval) -> newval);
        clients = clients.leftJoin(clientsBillString, (leftValueClients, rightValueBills) ->
                {
                    if(leftValueClients == null && rightValueBills == null){
                        return null;
                    } else if(rightValueBills == null){
                        return leftValueClients;
                    }
                    JSONObject leftValueClientsJSON = new JSONObject(leftValueClients);
                    JSONObject rightValueBillsJSON = new JSONObject(rightValueBills);

                    leftValueClientsJSON.put("bill", rightValueBillsJSON.getFloat("bill"));
                    leftValueClientsJSON.put("payed", rightValueBillsJSON.getFloat("payed"));

                    //System.out.println(leftValueCreditsJSON.toString());

                    return leftValueClientsJSON.toString();
                }
        );

        // KTable com Client's que fizeram payments nos ultimos meses
        KTable<Windowed<String>, String> clientsPaymentsFlag = computeNoPayments(paymentLines);
        KStream<String, String> clientsPaymentsFlagStream = clientsPaymentsFlag.toStream().map((key, value) -> KeyValue.pair(key.key(), value));
        KTable<String, String> clientsPaymentsFlagString = clientsPaymentsFlagStream.groupByKey().reduce((oldval, newval) -> newval);
        clients = clients.leftJoin(clientsPaymentsFlagString, (leftValueClients, rightValueLastMonths) ->
                {
                    if(rightValueLastMonths == null){
                        JSONObject leftValueClientsJSON = new JSONObject(leftValueClients);

                        leftValueClientsJSON.put("payments_last_months", 0);
                        return leftValueClients;
                    }
                    JSONObject leftValueClientsJSON = new JSONObject(leftValueClients);

                    leftValueClientsJSON.put("payments_last_months", Integer.parseInt(rightValueLastMonths) );

                    //System.out.println("Nr Payments Ultimo Meses:" + rightValueLastMonths);
                    //System.out.println("Payments Last Months: " + leftValueClientsJSON);

                    return leftValueClientsJSON.toString();
                }
        );

        // Update Client's balance, credits, payments
        clients.mapValues((k, v) ->{
                    JSONObject vJSON = new JSONObject(v);
                    String returnVal = new JSONObject(schemas.CLIENT_SCHEMA).put("payload", new JSONObject()
                            .put("payments", vJSON.getFloat("payments"))
                            .put("credits", vJSON.getFloat("credits"))
                            .put("balance", vJSON.getFloat("balance"))
                            .put("bill", vJSON.getFloat("bill"))
                            .put("payed", vJSON.getFloat("payed"))
                            .put("payments_last_months", vJSON.getFloat("payments_last_months"))
                            .put("person_email", new JSONObject(k).getString("email"))
                            .put("manager_person_email", vJSON.getString("manager_person_email"))
                    ).toString();
                    System.out.println("Update client("+ new JSONObject(k).getString("email") +"): " + returnVal);
                    return returnVal;
                }
        ).toStream().to(outTopicClient, Produced.with(Serdes.String(), Serdes.String()));


        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.cleanUp();
        streams.start();

        kafkaConsumer(); // Lista de Currencies
    }
}