DROP TABLE IF EXISTS wnprc.husbandry_frequency;
CREATE TABLE wnprc.husbandry_frequency
(
    rowid      serial                    NOT NULL,
    meaning    varchar(100) DEFAULT NULL NOT NULL,
    dayofweek  integer,
    sort_order integer,
    active     bool         DEFAULT true,
    altmeaning varchar(30),
    CONSTRAINT PK_husbandry_frequency PRIMARY KEY (rowid)
)
    WITH (OIDS= FALSE);

INSERT INTO wnprc.husbandry_frequency
(rowid, meaning,dayofweek, sort_order,active,altmeaning)
VALUES
(1, 'Daily - AM',       NULL,   1,'TRUE', 'AM'),
(2, 'Daily - PM',       NULL,   2,'TRUE','PM'),
(3, 'Daily - AM/PM',    NULL,   3,'TRUE',''),
(4, 'Daily - Any Time', NULL,   4,'TRUE','Any Time'),
(5, 'Sunday - PM',           '1',    5,'TRUE',''),
(6, 'Monday - PM',           '2',    6,'TRUE',''),
(7, 'Tuesday - PM',          '3',    7,'TRUE',''),
(8, 'Wednesday - PM',        '4',    8,'TRUE',''),
(9, 'Thursday - PM',         '5',    9,'TRUE',''),
(10, 'Friday - PM',          '6',    10,'TRUE',''),
(11, 'Saturday - PM',        '7',    11,'TRUE','');
