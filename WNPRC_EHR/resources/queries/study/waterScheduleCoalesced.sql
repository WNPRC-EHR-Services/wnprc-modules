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
COALESCE (WA.objectid, WS.objectid) AS objectIdCoalesced,
COALESCE (WA.project, WS.project) AS projectCoalesced,



FROM waterSchedule WS

LEFT JOIN  study.waterAmount WA ON ( WS.animalid = WA.id AND WS.origDate = WA.date)
