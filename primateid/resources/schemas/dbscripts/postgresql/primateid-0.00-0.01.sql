DROP SCHEMA IF EXISTS primateid;
CREATE SCHEMA primateid;

DROP TABLE IF EXISTS primateid.unique_ids;
CREATE TABLE primateid.unique_ids (
  container ENTITYID NOT NULL,
  participantid VARCHAR(32) NOT NULL,
  primateid VARCHAR(10) NOT NULL,

  CONSTRAINT pk_unique_ids PRIMARY KEY (container, participantid),
  CONSTRAINT primateid_unique UNIQUE (container, primateid)
);