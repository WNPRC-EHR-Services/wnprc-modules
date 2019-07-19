SELECT
       rowId,
       Id,
       transactionNumber,
       item,
       comment,
       chargeId.departmentCode AS groupName,
       quantity,
       unitCostDirect AS unitCost,
       totalCostDirect AS totalCost,
       date,
       invoiceDate,
       category,
       servicecenter,
       invoiceNumber
FROM ehr_billing.invoicedItems