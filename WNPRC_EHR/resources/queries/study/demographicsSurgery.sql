/*
 * Copyright (c) 2013 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
SELECT

s.id,

CASE
WHEN (group_concat(s.major) like '%Yes%') THEN
    'Yes'
      ELSE
    null
END AS MajorSurgery,
SUM (
  CASE WHEN s.major = 'Yes'
    THEN 1 ELSE 0
  END
  ) AS NumberOfMajorSurgeries,
true As AnySurgery,

count(*) as NumberOfSurgeries

FROM study.encounters s
WHERE s.qcstate.publicdata = true AND type = 'Surgery'

GROUP BY s.id


