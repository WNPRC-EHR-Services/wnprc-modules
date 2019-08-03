/*
 * Copyright (c) 2010-2014 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */

SELECT
d.id AS animalId,
d.calculated_status,
s.*,
s.objectid as treatmentid

FROM study.demographics d

JOIN (

    SELECT

      t1.objectid,
      t1.taskid,
      t1.dataset,
      t1.id as wanimalid,
      dr.date as origDate,
      t1.date as startDate,
      timestampdiff('SQL_TSI_DAY', cast(t1.dateOnly as timestamp), dr.dateOnly) + 1  as daysElapsed,
      t1.enddate,
      t1.enddateCoalescedFuture,    --debug column
      t1.assignedTo,
      t1.project,
      t1.volume,
      t1.qcstate

    FROM ehr_lookups.dateRange dr

    JOIN study.waterOrders t1
      --NOTE: should the enddate consider date/time?
      ON dr.dateOnly >= t1.dateOnly AND dr.dateOnly <= t1.enddateCoalescedFuture --AND


    --NOTE: if we run this report on a future interval, we want to include those treatments
    WHERE t1.date is not null

) s ON (s.wanimalid = d.id)

WHERE (d.lastDayatCenter Is Null or d.lastDayAtCenter >= s.enddateCoalescedFuture)


--account for date/time in schedule
--origDate has to have 00:00 a time otherwise it does not include the first day of the order
and s.origDate >= s.startDate and s.origDate <= s.enddateCoalescedFuture