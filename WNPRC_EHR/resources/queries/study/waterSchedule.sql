/*
 * Copyright (c) 2010-2014 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */

SELECT
d.id AS wanimalid,
d.calculated_status,
s.*,
s.objectid as treatmentid

FROM study.demographics d

JOIN (

SELECT
  s.*,
  timestampadd('SQL_TSI_MINUTE', ((s.hours * 60) + s.minutes), s.origDate) as datemin,
  CASE
    WHEN (hours >= 6 AND hours < 20) THEN 'AM'
    WHEN (hours < 6 OR hours >= 20) THEN 'PM'
    ELSE 'Other'
  END as timeOfDay,

  ((s.hours * 60) + s.minutes) as timeOffset

FROM (

    SELECT

      t1.objectid,
      t1.dataset,
      t1.id as animalid,

      coalesce( ft.hourofday, ((hour(t1.date) * 100) + minute(t1.date))) as time,
      (coalesce(ft.hourofday, (hour(t1.date) * 100))/100 ) as hours,
      CASE
        WHEN ( ft.hourofday IS NOT NULL) THEN (((ft.hourofday / 100.0) - floor( ft.hourofday / 100)) * 100)
        ELSE minute(t1.date)
      END as minutes,
      dr.date as origDate,
      dr.dateOnly AS dateOnly,      --debug column
      t1.dateOnly AS dateOnlyT1,    --debug column
      t1.frequency.meaning as frequency,
      t1.date as startDate,
      timestampdiff('SQL_TSI_DAY', cast(t1.dateOnly as timestamp), dr.dateOnly)  as daysElapsed,
      t1.enddate,
      t1.enddateCoalescedFuture,    --debug column
      t1.project,
      t1.volume,
      t1.qcstate

    FROM ehr_lookups.dateRange dr

    JOIN study.waterOrders t1
      --NOTE: should the enddate consider date/time?
      ON dr.dateOnly >= t1.dateOnly and dr.dateOnly <= t1.enddateCoalescedFuture --AND
          --technically the first day of the treatment is day 1, not day 0
      --(  (mod(CAST(timestampdiff('SQL_TSI_DAY', CAST(t1.dateOnly as timestamp), dr.dateOnly) as integer), t1.frequency.intervalindays) = 0 And t1.frequency.intervalindays is not null And t1.frequency.dayofweek is null )

      -- OR (t1.frequency.dayofweek is not null And t1.frequency.intervalindays is null And dr.DayOfWeek in (select k.value from onprc_ehr.Frequency_DayofWeek k where k.FreqKey = t1.frequency.rowid ) )
        --)
        --  )

    --LEFT JOIN ehr.treatment_times tt ON (tt.treatmentid = t1.objectid)
    LEFT JOIN ehr_lookups.treatment_frequency_times ft ON (ft.frequency = t1.frequency.meaning )

    /*LEFT JOIN (
        SELECT
          sc.code
        from ehr_lookups.snomed_subset_codes sc
        WHERE sc.primaryCategory = 'Diet'
        GROUP BY sc.code
    ) snomed ON snomed.code = t1.code*/

    --NOTE: if we run this report on a future interval, we want to include those treatments
    WHERE t1.date is not null
    --NOTE: they have decided to include non-public data
    --AND t1.qcstate.publicdata = true --and t1.dateOnly <= curdate()

    ) s

) s ON (s.animalid = d.id)

WHERE (d.lastDayatCenter Is Null or d.lastDayAtCenter >= s.enddateCoalescedFuture)


--account for date/time in schedule
and s.datemin >= s.startDate and s.datemin < s.enddateCoalescedFuture