package org.labkey.wnprc_ehr.notification;
import org.labkey.api.collections.CaseInsensitiveHashMap;
import org.labkey.api.data.ColumnInfo;
import org.labkey.api.data.CompareType;
import org.labkey.api.data.Container;
import org.labkey.api.data.ConvertHelper;
import org.labkey.api.data.SimpleFilter;
import org.labkey.api.data.TableInfo;
import org.labkey.api.data.TableSelector;
import org.labkey.api.module.Module;
import org.labkey.api.query.FieldKey;
import org.labkey.api.query.QueryService;
import org.labkey.api.security.User;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: Daniel Nicolalde
 * Date: 2021-07-28
 */
public class WaterOrdersAlertNotification extends AbstractEHRNotification
{
    public WaterOrdersAlertNotification(Module owner){
        super(owner);
    }
    public String getName(){return "WNPRC Waters Orders Not Completed (ACT)";}

    public String getDescription(){
        return  "Send notification of any eater order not completed by the corresponding frequency.";
    }

    @Override
    public String getEmailSubject(Container c)
    {
        return "Daily Water Alerts: " + _dateTimeFormat.format(new Date());
    }

    @Override
    public String getCronString()
    {
        return "0 0 10,13,15,17,19 * * ?";
    }

    public String getCategory(){
        return "Husbandry";
    }

    public String getScheduleDescription()
    {
        return "daily at 1000, 1300, 1500, 1700, 1900";
    }

    @Override
    public String getMessageBodyHTML(Container c, User u)
    {
        StringBuilder msg = new StringBuilder();

        final Date now = new Date();

        msg.append("This email contains any water orders not marked as completed.  It was run on: " + _dateFormat.format(now) + " at " + _timeFormat.format(now) + ".<p>");
        findWaterOrdersNotCompleted(c,u,msg,LocalDateTime.now(), false);
        findWaterOrdersNotCompleted(c,u,msg,LocalDateTime.now(), true);

        return msg.toString();

    }

    private void findWaterOrdersNotCompleted(Container c,User u, StringBuilder msg, final LocalDateTime maxDate, boolean includeFuture){
        LocalDateTime currentTime = maxDate;
        LocalDateTime amThreshold = LocalDateTime.now().withHour(10).withMinute(30);

        //Setting interval to start the water schedule, the system generates the calendar thirty days before
        LocalDateTime roundedMax = maxDate;
        roundedMax = roundedMax.plusDays(-5).truncatedTo(ChronoUnit.DAYS);

        //final String intervalLength = "10";

        //Sending parameters to the query that generates the water schedule from water orders and water amounts
        Map<String, Object> parameters = new CaseInsensitiveHashMap<>();
        parameters.put("NumDays", 6);
        //static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String dateFormat = roundedMax.format(DateTimeFormatter.ISO_DATE_TIME);
        parameters.put("StartDate", roundedMax.toString());

        TableInfo ti = QueryService.get().getUserSchema(u,c,"study").getTable("waterScheduleCoalesced");
        SimpleFilter filter = new SimpleFilter(FieldKey.fromString("QCState/label"),"Scheduled",CompareType.EQUAL);

        if (includeFuture){
            LocalDateTime futureDate = maxDate;
            futureDate = futureDate.plusDays(1).truncatedTo(ChronoUnit.DAYS);

            filter.addCondition(FieldKey.fromString("dateOrdered"),futureDate, CompareType.LTE);
            filter.addCondition(FieldKey.fromString("dateOrdered"),currentTime, CompareType.GT);

        }else{
            filter.addCondition(FieldKey.fromString("dateOrdered"),currentTime, CompareType.LTE);
        }

        filter.addCondition(FieldKey.fromString("dateOrdered"), roundedMax,CompareType.DATE_GTE);
       // filter.addCondition(FieldKey.fromString("assignedTo"), "animalcare");
        filter.addCondition(FieldKey.fromString("actionRequired"),true,CompareType.EQUAL);

        Set<FieldKey> columns = new HashSet<>();
        columns.add(FieldKey.fromString("animalId"));
        columns.add(FieldKey.fromString("dateOrdered"));
        columns.add(FieldKey.fromString("volume"));
        columns.add(FieldKey.fromString("frequencyMeaning"));
        columns.add(FieldKey.fromString("displaytimeofday"));
        columns.add(FieldKey.fromString("assignedTo"));



        final Map<FieldKey, ColumnInfo> colMap = QueryService.get().getColumns(ti, columns);


        TableSelector ts = new TableSelector(ti,colMap.values(), filter,null);
        ts.setNamedParameters(parameters);
        long total = ts.getRowCount();

        String timeofday = "AM";
        if (currentTime.isAfter(amThreshold)){
            timeofday = "PM";
        }

        if (total == 0 && !includeFuture){
            msg.append("All " + timeofday + " water orders are completed");
        }else if(total > 0){
            if(includeFuture && timeofday.equals("PM")){
                msg.append("<p><b>There are "+total+ " water orders that are scheduled for today and have not been completed</b><br>");
            }else{
                msg.append("<p><b>WARNING: There are "+total+ " water orders that have not been completed</b><br>");
            }

            Map<String,Object>[] waterOrdersScheduled = ts.getMapArray();
            List <Map<String,Object>> animalCareWaters = new ArrayList<>();
            List<Map<String,Object>> researchStaffWaters = new ArrayList<>();
            List<Map<String,Object>> spiWaters = new ArrayList<>();


            if(waterOrdersScheduled.length >0){
                for(Map<String,Object> waterSchedule : waterOrdersScheduled){
                    String assignedTo = ConvertHelper.convert(waterSchedule.get("assignedTo"),String.class);
                    if (assignedTo.equals("animalcare")){
                        animalCareWaters.add(waterSchedule);
                    }else if (assignedTo.equals("researchstaff")){
                        researchStaffWaters.add(waterSchedule);
                    }else if (assignedTo.equals("spi")){
                        spiWaters.add(waterSchedule);
                    }
                }
                StringBuilder htmlTable;
                if (!animalCareWaters.isEmpty())
                {
                    htmlTable = createTable(animalCareWaters, "Animal Care");
                    msg.append(htmlTable);
                }
                if (!researchStaffWaters.isEmpty())
                {
                    htmlTable = createTable(researchStaffWaters, "Research Staff");
                    msg.append(htmlTable);
                }
                if (!spiWaters.isEmpty())
                {
                    htmlTable = createTable(spiWaters, "SPI");
                    msg.append(htmlTable);
                }


            }
            msg.append("<br><a href='" + getExecuteQueryUrl(c,"study","waterScheduleCoalesced","Scheduled")+"&query.param.StartDate="+roundedMax+"&query.param.NumDays=" + 1 +
                    "&query.dateOrdered~lte="+currentTime+ "&query.dateOrdered~dategte="+roundedMax+"'>Click here to view schedule waters</a></p>");
        }



    }
    private StringBuilder createTable (List<Map<String,Object>> listOfDBObjects, String assignedTo){
        StringBuilder returnTable = new StringBuilder();
        returnTable.append("<br><strong>"+ assignedTo +"</strong>");
        returnTable.append("<table border=1 style='border-collapse: collapse;'>");
        returnTable.append("<tr><td style='padding: 5px; text-align: center;'><strong>Id</strong></td>" +
                "<td style='padding: 5px; text-align: center;'><strong>Date Ordered</strong></td>" +
                "<td style='padding: 5px; text-align: center;'><strong>Volume</strong></td>" +
                "<td style='padding: 5px; text-align: center;'><strong>Time of Day</strong></td>" +
                "<td style='padding: 5px; text-align: center;'><strong>Frequency</strong></td>" +
                "<td style='padding: 5px; text-align: center;'><strong>Assigned To</strong></td></tr>\n");
        for (Map<String,Object> dbObject : listOfDBObjects){
            LocalDateTime objectDateTime = ConvertHelper.convert(dbObject.get("dateOrdered"),Date.class).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            returnTable.append("<tr><td style='padding: 5px;'>" + ConvertHelper.convert(dbObject.get("animalId"),String.class)
                    + "</td><td style='padding: 5px; text-align: center;'> " + objectDateTime.format(formatter)
                    + "</td><td style='padding: 5px; text-align: center;'> " + ConvertHelper.convert(dbObject.get("volume"),String.class)
                    + "</td><td style='padding: 5px; text-align: center;'> " + ConvertHelper.convert(dbObject.get("displaytimeofday"),String.class)
                    + "</td><td style='padding: 5px; text-align: center;'> " + ConvertHelper.convert(dbObject.get("frequencyMeaning"),String.class)
                    + "</td><td style='padding: 5px; text-align: center;'> " + ConvertHelper.convert(dbObject.get("assignedTo"),String.class) +"</td></tr>" );
        }
        returnTable.append("</table>");

        return returnTable;

    }
}
