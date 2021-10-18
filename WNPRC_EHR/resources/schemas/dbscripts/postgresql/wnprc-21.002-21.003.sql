DROP TABLE IF EXISTS wnprc.protocol_species_max;
CREATE TABLE wnprc.protocol_species_max
(   
    rowid                   serial NOT NULL,
    protocol_id             varchar(255),
    pi_name                 varchar(255),
    date_approved           TIMESTAMP,
    arrow_common_name       varchar(255),
    max_three_year          integer,

    
    -- Default fields for LabKey.
    container         entityid NOT NULL,
    createdby         userid,
    created           TIMESTAMP,
    modifiedby        userid,
    modified          TIMESTAMP,
    
    CONSTRAINT pk_protocol_species_max_rowid PRIMARY KEY (rowid)
);
