CREATE TABLE person (
	email varchar(64) UNIQUE NOT NULL,
	name	 varchar(64) NOT NULL,
	PRIMARY KEY(email)
);

CREATE TABLE client (
	payments		 float(8) NOT NULL DEFAULT 0,
	credits		 float(8) NOT NULL DEFAULT 0,
	balance		 float(8) NOT NULL DEFAULT 0,
	bill		 float(8) NOT NULL DEFAULT 0,
	payed		 boolean NOT NULL DEFAULT 1,
	payments_last_months boolean NOT NULL DEFAULT 0,
	manager_person_email varchar(64) NOT NULL,
	person_email	 varchar(64) UNIQUE NOT NULL,
	PRIMARY KEY(person_email)
);

CREATE TABLE manager (
	revenues	 float(8) NOT NULL DEFAULT 0,
	person_email varchar(64) UNIQUE NOT NULL,
	PRIMARY KEY(person_email)
);

CREATE TABLE currency (
	name	 varchar(64) NOT NULL,
	to_euro float(8) NOT NULL,
	PRIMARY KEY(name)
);

ALTER TABLE client ADD CONSTRAINT client_fk1 FOREIGN KEY (manager_person_email) REFERENCES manager(person_email);
ALTER TABLE client ADD CONSTRAINT client_fk2 FOREIGN KEY (person_email) REFERENCES person(email);
ALTER TABLE manager ADD CONSTRAINT manager_fk1 FOREIGN KEY (person_email) REFERENCES person(email);

