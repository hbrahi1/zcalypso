DROP TABLE vet_specialties IF EXISTS;
DROP TABLE vets IF EXISTS;
DROP TABLE specialties IF EXISTS;
DROP TABLE visits IF EXISTS;
DROP TABLE contacts IF EXISTS;
DROP TABLE types IF EXISTS;
DROP TABLE businessPartners IF EXISTS;


CREATE TABLE vets (
  id         INTEGER IDENTITY PRIMARY KEY,
  first_name VARCHAR(30),
  last_name  VARCHAR(30),
  profile_description VARCHAR(255)
);
CREATE INDEX vets_last_name ON vets (last_name);

CREATE TABLE specialties (
  id   INTEGER IDENTITY PRIMARY KEY,
  name VARCHAR(80)
);
CREATE INDEX specialties_name ON specialties (name);

CREATE TABLE vet_specialties (
  vet_id       INTEGER NOT NULL,
  specialty_id INTEGER NOT NULL
);
ALTER TABLE vet_specialties ADD CONSTRAINT fk_vet_specialties_vets FOREIGN KEY (vet_id) REFERENCES vets (id);
ALTER TABLE vet_specialties ADD CONSTRAINT fk_vet_specialties_specialties FOREIGN KEY (specialty_id) REFERENCES specialties (id);

CREATE TABLE types (
  id   INTEGER IDENTITY PRIMARY KEY,
  name VARCHAR(80)
);
CREATE INDEX types_name ON types (name);

CREATE TABLE businessPartners (
  id         INTEGER IDENTITY PRIMARY KEY,
  first_name VARCHAR(30),
  last_name  VARCHAR(30),
  address    VARCHAR(255),
  city       VARCHAR(80),
  telephone  VARCHAR(20),
  profile_description VARCHAR(255)
);
CREATE INDEX businessPartners_last_name ON businessPartners (last_name);

CREATE TABLE contacts (
  id         INTEGER IDENTITY PRIMARY KEY,
  name       VARCHAR(30),
  birth_date DATE,
  type_id    INTEGER NOT NULL,
  businessPartner_id   INTEGER NOT NULL,
  profile_description VARCHAR(255)
);
ALTER TABLE contacts ADD CONSTRAINT fk_contacts_businessPartners FOREIGN KEY (businessPartner_id) REFERENCES businessPartners (id);
ALTER TABLE contacts ADD CONSTRAINT fk_contacts_types FOREIGN KEY (type_id) REFERENCES types (id);
CREATE INDEX contacts_name ON contacts (name);

CREATE TABLE visits (
  id          INTEGER IDENTITY PRIMARY KEY,
  contact_id      INTEGER NOT NULL,
  visit_date  DATE,
  description VARCHAR(255)
);
ALTER TABLE visits ADD CONSTRAINT fk_visits_contacts FOREIGN KEY (contact_id) REFERENCES contacts (id);
CREATE INDEX visits_contact_id ON visits (contact_id);
