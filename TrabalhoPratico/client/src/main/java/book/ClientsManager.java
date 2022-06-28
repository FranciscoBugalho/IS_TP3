package book;

import data.Client;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONObject;

import java.util.*;

public class ClientsManager {

    private static boolean containsDuplicateClient(List<Client> clientsList, String clientEmail){
        for(Client i : clientsList){
            if(i.getEmail().equals(clientEmail))
                return true;
        }
        return false;
    }

    private static boolean containsDuplicateCurrency(List<String> currenciesList, String currencyName){
        for(String i : currenciesList){
            if(i.equals(currencyName))
                return true;
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        //Assign topicNames to string variable
        String topicNameGetInfo = "DB_Info_Topics_Client";
        String topicNameCredits = "Credits_Topic";
        String topicNamePayments = "Payments_Topic";

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

        // Producer (Sends Payments and Credits)
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Consumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList(topicNameGetInfo));

        Producer<String, String> producer = new KafkaProducer<>(props);

        // Auxiliar Variables
        List<Client> clientsList = new ArrayList<>();
        List<String> currenciesList = new ArrayList<>();
        Client auxClient;
        String auxCurrency;
        Random rand = new Random();
        float creditAmount;
        float paymentAmount;
        int i = 0;

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);    // Receives Clients and Currencies from DB
            for (ConsumerRecord<String, String> record : records) {                     // Processes Clients/Currencies received from DB
                JSONObject key = new JSONObject(record.key());
                System.out.printf("offset = %d, key = %s, value = %s \n", record.offset(), record.key(), record.value());
                if(key.has("manager_person_email")){    // It's a client
                    JSONObject clientJSON = new JSONObject(record.value());
                    if(!containsDuplicateClient(clientsList, clientJSON.getJSONObject("payload").getString("person_email"))){
                        auxClient = new Client(clientJSON.getJSONObject("payload").getString("person_email"),null,clientJSON.getJSONObject("payload").getString("manager_person_email"));
                        clientsList.add(auxClient);
                        //System.out.println("Added: " + clientJSON.getJSONObject("payload").getString("person_email"));
                    }
                }
                else if(key.has("name")){  // It's a currency
                    JSONObject currencyJSON = new JSONObject(record.value());
                    if(!containsDuplicateCurrency(currenciesList, currencyJSON.getJSONObject("payload").getString("name"))){
                        currenciesList.add(currencyJSON.getJSONObject("payload").getString("name"));
                        //System.out.println("Added: " + currencyJSON.getJSONObject("payload").getString("name"));
                    }
                }
                //System.out.printf("offset = %d, key = %s, value = %s \n", record.offset(), record.key(), record.value());
            }
            if(clientsList.isEmpty() || currenciesList.isEmpty()){   // Verifies it there is at least one currency and one client
                continue;
            }
            // Selects a random client
            auxClient = clientsList.get(rand.nextInt(clientsList.size()));

            // Selects a random currency
            auxCurrency = currenciesList.get(rand.nextInt(currenciesList.size()));

            // Selects a random amount
            paymentAmount = rand.nextFloat() * (1000 - 100) + 100;
            creditAmount = rand.nextFloat() * (1000 - 100) + 100;

            // Sends Payment to kafkaStreams
            JSONObject paymentPayload =  new JSONObject()
                    .put("payment", true)
                    .put("price", paymentAmount)
                    .put("currency_name", auxCurrency)
                    .put("manager_person_email", auxClient.getManager_person_email())
                    //.put("person_email", auxClient.getEmail()) //OLD
                    .put("payments", 0)
                    .put("credits", 0)
                    .put("balance", 0)
                    .put("bill", 0)
                    .put("payed", 1)
                    .put("payments_last_months", 0);
            producer.send(new ProducerRecord<String, String>(topicNamePayments, new JSONObject().put("manager", false).put("email", auxClient.getEmail()).toString(), paymentPayload.toString()));
            System.out.println("New Payment ("+ auxClient.getEmail() +"):" + paymentPayload.toString());
            paymentPayload =  new JSONObject()
                    .put("price", paymentAmount)
                    .put("currency_name", auxCurrency)
                    .put("revenues", 0);
            producer.send(new ProducerRecord<String, String>(topicNamePayments, new JSONObject().put("manager", true).put("email", auxClient.getManager_person_email()).toString(), paymentPayload.toString()));
            i++;

            // Sends Credit to KafkaStreams
            JSONObject creditPayload =  new JSONObject()
                    .put("payment", false)
                    .put("price", creditAmount)
                    .put("currency_name", auxCurrency)
                    .put("manager_person_email", auxClient.getManager_person_email())
                    //.put("person_email", auxClient.getEmail())
                    .put("payments", 0)
                    .put("credits", 0)
                    .put("balance", 0)
                    .put("bill", 0)
                    .put("payed", 1)
                    .put("payments_last_months", 0);

            producer.send(new ProducerRecord<String, String>(topicNameCredits, new JSONObject().put("manager", false).put("email", auxClient.getEmail()).toString(), creditPayload.toString()));
            System.out.println("New Credit ("+ auxClient.getEmail() +"):" + creditPayload.toString());
            i++;
        }
    }
}
