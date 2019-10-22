/*
 * Merging water Schedule with water amount
 *
 *
*/
SELECT *,
    dateCoalesce AS dateOutCoalesce,
    WeightAtTime.weight AS WeightAtTime,
    WeightAtTime.id AS IdFromWeight,
    WeightAtTime.Weightdate AS dateFromWeight

FROM(
    SELECT
    WS.*,
    COALESCE (WA.date, WS.origDate) AS dateCoalesce,
    /*WS.animalid,
    WS.origDate,
    WS.startDate,*/
    COALESCE (WA.volume, CAST (WS.volume AS DOUBLE))AS volumeCoalesced,
    COALESCE (WA.assignedTo, WS.assignedTo) AS assignedToCoalesced,
    COALESCE (WA.assignedTo.title, WS.assignedToTittle) AS assignedToTitleCoalesced,
    COALESCE (WA.dataset.name, WS.dataset.name) AS dataSource,
    COALESCE (WA.objectid, WS.objectid) AS objectIdCoalesced,
    COALESCE (WA.lsid, WS.lsid) AS lsidCoalesced,
    COALESCE (WA.project, WS.project) AS projectCoalesced,
    COALESCE (WA.frequency, WS.frequency) AS frequencyCoalesced,
    COALESCE (WA.frequency.meaning, WS.freqMeaning) AS frequencyMeaningCoalesced,





    FROM waterSchedule WS

    FULL JOIN  study.waterAmount WA ON ( WS.animalid = WA.id AND WS.origDate = WA.date)
) WSOUT

LEFT OUTER JOIN
    (SELECT w.id, w.date as Weightdate, w.weight AS weight FROM study.weight w
    WHERE w.qcstate.publicdata = true --AND w.date <= WSOUT.dateOutCoalesce
    ORDER BY w.date DESC LIMIT 1
    --GROUP BY w.id
    ) WeightAtTime ON WSOUT.animalId = WeightAtTime.id

-- TODO: Outer join should look something like this:
/*SELECT id, date as Weightdate, weight AS weight
FROM study.weight
WHERE id = 'r18023' AND date <= '2018-09-14'
--GROUP BY id
ORDER BY date DESC LIMIT 1*/
