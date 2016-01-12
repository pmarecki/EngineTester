CREATE TABLE category (
  catid   SERIAL,
  comment TEXT
);

CREATE TABLE engine (
  engineid SERIAL,
  catid    INT,
  filename TEXT
);

CREATE TABLE data (
  dataid   SERIAL,
  catid    INT,
  filename TEXT
);

CREATE TABLE result (
  resultid SERIAL,
  catid    INT,
  engineid INT,
  dataid   INT,
  filename TEXT
);

CREATE TABLE dataset (
  datasetid SERIAL,
  comment   TEXT
);

CREATE TABLE indataset (
  inid      SERIAL,
  datasetid INT,
  dataid    INT
);



