/*
 * Merging water Schedule with water amount
 *
 *
*/

SELECT
WS.*,
WS.origDate AS date,
/*WS.animalid,
WS.origDate,
WS.startDate,*/
COALESCE (WA.volume, CAST (WS.volume AS DOUBLE))AS volumeCoalesced,
COALESCE (WA.assignedTo, WS.assignedTo) AS assignedToCoalesced,
COALESCE (WA.dataset.label, WS.dataset.label) AS datasetCoalesced,

CASE
WHEN WA.volume IS NOT NULL THEN 'waterAmount'
WHEN WS.volume IS NOT NULL THEN 'waterOrder'
END AS dataSource


FROM waterSchedule WS

LEFT JOIN  study.waterAmount WA ON ( WS.animalid = WA.id AND WS.origDate = WA.date)
