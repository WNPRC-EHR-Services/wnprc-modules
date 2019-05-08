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
COALESCE (WA.volume, CAST (WS.volume AS DOUBLE))AS volumeCoalesced

FROM waterSchedule WS

LEFT JOIN  study.waterAmount WA ON ( WS.animalid = WA.id AND WS.origDate = WA.date)
