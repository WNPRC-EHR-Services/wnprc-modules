/*
 * Merging water Schedule with water amount
 *
 *
*/
/*SELECT
WSO.animalId,
COALESCE (WA.date, WSO.origDate) AS date,
COALESCE (WA.volume, CAST (WSO.vol AS DOUBLE)) AS volumeCoalesced,
COALESCE (WA.assignedTo,WSO.assignedTo) AS assignedToCoalesced,
COALESCE (WA.assignedTo.title, WSO.assignedToTitle) AS assignedToTitleCoalesced,
COALESCE (WA.dataset.name, WSO.dataSource) AS dataSource,
COALESCE (WA.objectid, WSO.objectId) AS objectIdCoalesced,
COALESCE (WA.lsid, WSO.lsid) AS lsid,
COALESCE (WA.project, WSO.project) AS projectCoalesced,
COALESCE (WA.frequency, WSO.frequency) AS frequencyCoalesced,
COALESCE (WA.frequency.meaning, WSO.frequencyMeaning) AS frequencyMeaningCoalesced,

(SELECT wg.weight AS weightAtDate
    FROM study.weight wg
    WHERE wg.id = WSO.animalId AND CAST(substring(CAST(wg.date AS VARCHAR) , 1, 10) AS DATE) <= WSO.origDate
    ORDER BY wg.date DESC
    LIMIT 1
) AS weightAtDate,

(SELECT wg.date AS weightDate
    FROM study.weight wg
    WHERE wg.id = WSO.animalId AND CAST(substring(CAST(wg.date AS VARCHAR) , 1, 10) AS DATE) <= WSO.origDate
    ORDER BY wg.date DESC
    LIMIT 1
) AS weightDate

FROM
        (
            SELECT
                WS.animalId AS animalId,
                WS.origDate AS origDate,
                WS.volume AS Vol,
                WS.assignedTo AS assignedTo,
                WS.assignedToTittle AS assignedToTitle,
                'waterOrders' AS dataSource,
                WS.objectid AS objectid,
                WS.lsid AS lsid,
                WS.project AS project,
                WS.frequency AS frequency,
                WS.freqMeaning AS frequencyMeaning


            FROM study.waterSchedule WS
            WHERE WS.volume = (SELECT MAX(WSI.volume) FROM study.waterSchedule WSI WHERE WSI.animalId = WS.animalId AND WSI.origDate = WS.origDate)
    ) WSO

FULL JOIN  study.waterAmount WA
    ON ( WSO.animalId = WA.id AND WSO.origDate = WA.date)*/

-- Alternative with UNION ALL faster query

(SELECT
    WA.id AS animalId,
    WA.date AS date,
    CAST (WA.volume AS DOUBLE) AS volume,
    WA.assignedTo AS assignedTo,
    WA.assignedTo.title AS assignedToTitle,
    'waterAmount' AS dataSource,
    WA.objectid AS objectid,
    WA.lsid AS lsid,
    WA.project AS project,
    WA.frequency AS frequency,
    WA.frequency.meaning AS frequencyMeaning


FROM study.waterAmount WA
)

UNION ALL

(SELECT
    WS.animalId AS animalId,
    WS.origDate AS date,
    CAST (WS.volume AS DOUBLE) AS volume,
    WS.assignedTo AS assignedTo,
    WS.assignedToTittle AS assignedToTitle,
    'waterOrders' AS dataSource,
    WS.objectid AS objectid,
    WS.lsid AS lsid,
    WS.project AS project,
    WS.frequency AS frequency,
    WS.freqMeaning AS frequencyMeaning


FROM study.waterSchedule WS
WHERE NOT EXISTS (SELECT 1
                    FROM study.waterAmount WAI
                    WHERE WAI.id = WS.animalId AND WAI.date = WS.origDate
                    )


)


