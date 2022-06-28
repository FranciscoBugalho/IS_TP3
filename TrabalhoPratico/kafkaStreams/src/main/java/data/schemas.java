package data;

public interface schemas {
    String PERSON_SCHEMA = "{\"schema\":{\"type\":\"struct\",\"fields\":[{\"type\":\"string\",\"optional\":false,\"field\":\"email\"},{\"type\":\"string\",\"optional\":false,\"field\":\"name\"}],\"optional\":false}}";
    // "payload":{"email":"teste1@teste.com","name":"teste1"}
    String CLIENT_SCHEMA = "{\"schema\":{\"type\":\"struct\",\"fields\":[{\"type\":\"float\",\"optional\":true,\"field\":\"payments\"},{\"type\":\"float\",\"optional\":true,\"field\":\"credits\"},{\"type\":\"float\",\"optional\":true,\"field\":\"balance\"},{\"type\":\"float\",\"optional\":true,\"field\":\"bill\"},{\"type\":\"int8\",\"optional\":true,\"field\":\"payed\"},{\"type\":\"int8\",\"optional\":true,\"field\":\"payments_last_months\"},{\"type\":\"string\",\"optional\":true,\"field\":\"manager_person_email\"},{\"type\":\"string\",\"optional\":false,\"field\":\"person_email\"}],\"optional\":false}}";
    // "payload":{"payments":0.0,"credits":0.0,"balance":0.0,"bill":0.0,"payed":1,"payments_last_months":0,"manager_person_email":"m2@mail.com","person_email":"c2@mail.com"}
    String CLIENT_SCHEMA_CREDITS = "{\"schema\":{\"type\":\"struct\",\"fields\":[{\"type\":\"float\",\"optional\":false,\"field\":\"credits\"},{\"type\":\"float\",\"optional\":false,\"field\":\"balance\"},{\"type\":\"string\",\"optional\":false,\"field\":\"manager_person_email\"},{\"type\":\"string\",\"optional\":false,\"field\":\"person_email\"}],\"optional\":false}}";
    String MANAGER_SCHEMA = "{\"schema\":{\"type\":\"struct\",\"fields\":[{\"type\":\"float\",\"optional\":false,\"field\":\"revenues\"},{\"type\":\"string\",\"optional\":false,\"field\":\"person_email\"}],\"optional\":false}}";
    // "payload":{"revenues":0.0,"person_email":"m1@mail.com"}
    String CURRENCY_SCHEMA = "{\"schema\":{\"type\":\"struct\",\"fields\":[{\"type\":\"string\",\"optional\":false,\"field\":\"name\"},{\"type\":\"float\",\"optional\":false,\"field\":\"to_euro\"}],\"optional\":false}}";
    // "payload":{"name":"Real","to_euro":0.16}
}
