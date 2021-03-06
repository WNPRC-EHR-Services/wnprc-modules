-- noinspection SqlNoDataSourceInspectionForFile

SELECT
personid,
first_name,
middle_name,
last_name,
notes,
hold,
measles_required,
tbResults.tbid as id,
to_char(tbResults.tbdate,'MM/DD/YYYY') as date,
'tb_clearances' as table_name

FROM persons

LEFT JOIN (

  SELECT
  p_tb_map.person_id,
  tb.id as tbid,
  CAST(tb.date as DATE) as tbdate

  FROM tb_clearances tb

  LEFT JOIN persons_tb_clearances p_tb_map
  ON (
    tb.id = p_tb_map.clearance_id
  )

  ORDER BY tbdate DESC

) tbResults

ON tbResults.person_id = persons.personid



