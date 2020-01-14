SELECT
    alias,
    investigatorId,
    (CASE WHEN investigatorId.lastName IS NOT NULL AND investigatorId.firstName IS NOT NULL
          THEN (investigatorId.lastName ||', '|| investigatorId.firstName)
          WHEN investigatorId.lastName IS NOT NULL AND investigatorId.firstName IS NULL
          THEN investigatorId.lastName
          WHEN investigatorId.lastName IS NULL AND investigatorId.firstName IS NULL
          THEN investigatorId.userid.displayName
    END) AS investigatorName
FROM ehr_billing_public.aliases
WHERE investigatorId IS NOT NULL