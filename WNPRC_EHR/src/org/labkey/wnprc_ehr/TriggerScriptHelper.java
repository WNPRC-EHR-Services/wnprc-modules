package org.labkey.wnprc_ehr;

import org.apache.commons.beanutils.ConversionException;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.labkey.api.collections.CaseInsensitiveHashMap;
import org.labkey.api.data.CompareType;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.data.ConvertHelper;
import org.labkey.api.data.Selector;
import org.labkey.api.data.SimpleFilter;
import org.labkey.api.data.Table;
import org.labkey.api.data.TableInfo;
import org.labkey.api.data.TableSelector;
import org.labkey.api.ehr.EHRService;
import org.labkey.api.ldk.notification.NotificationSection;
import org.labkey.api.ldk.notification.NotificationService;
import org.labkey.api.module.Module;
import org.labkey.api.module.ModuleLoader;
import org.labkey.api.query.FieldKey;
import org.labkey.api.query.QueryService;
import org.labkey.api.query.UserSchema;
import org.labkey.api.security.User;
import org.labkey.api.security.UserManager;
import org.labkey.api.util.PageFlowUtil;
import org.labkey.dbutils.api.SimpleQueryFactory;
import org.labkey.dbutils.api.SimplerFilter;
import org.labkey.ehr.EHRSchema;
import org.labkey.wnprc_ehr.notification.DeathNotification;
import org.labkey.wnprc_ehr.notification.VvcNotification;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by jon on 7/13/16.
 */
public class TriggerScriptHelper {
    protected final Container container;
    protected final User user;
    protected static final Logger _log = Logger.getLogger(TriggerScriptHelper.class);
    protected final SimpleQueryFactory queryFactory;

    private TriggerScriptHelper(int userId, String containerId) {
        user = UserManager.getUser(userId);
        if (user == null) {
            throw new RuntimeException("User does not exist: " + userId);
        }

        container = ContainerManager.getForId(containerId);
        if (container == null) {
            throw new RuntimeException("Container does not exist: " + containerId);
        }

        queryFactory = new SimpleQueryFactory(user, container);
    }

    public static TriggerScriptHelper create(int userId, String containerId) {
        return new TriggerScriptHelper(userId, containerId);
    }
    @NotNull
    private User getUser()
    {
        return user;
    }

    @NotNull
    private Container getContainer()
    {
        return container;
    }

    private TableInfo getTableInfo(String schema, String query)
    {
        return getTableInfo(schema, query, false);
    }

    private TableInfo getTableInfo(String schema, String query, boolean suppressError)
    {
        UserSchema us = QueryService.get().getUserSchema(getUser(), getContainer(), schema);
        if (us == null)
        {
            if (!suppressError)
                throw new IllegalArgumentException("Unable to find schema: " + schema);

            return null;
        }

        TableInfo ti = us.getTable(query);
        if (ti == null)
        {
            if (!suppressError)
                throw new IllegalArgumentException("Unable to find table: " + schema + "." + query);

            return null;
        }

        return ti;
    }

    public Map<String, Object> getExtraContext()
    {
        Map<String, Object> map = new HashMap<>();
        map.put("quickValidation", true);
        map.put("generatedByServer", true);

        return map;
    }

    public void sendDeathNotification(final List<String> ids) {

        if (!NotificationService.get().isServiceEnabled() && NotificationService.get().isActive(new DeathNotification(), container)){
            _log.info("Notification service is not enabled, will not send death notification");
            return;
        }
        for (String id : ids) {
            DeathNotification idNotification = new DeathNotification();
            idNotification.setParam(DeathNotification.idParamName, id);
            idNotification.sendManually(container, user);
        }
    }

    public String lookupValue(String key, String study, String queryName, String keyCol, String displayColumn) {
        JSONArray results = queryFactory.selectRows(study, queryName, new SimplerFilter(keyCol, CompareType.EQUAL, key));
        if (results.length() > 0) {
            return results.getJSONObject(0).getString(displayColumn);
        }
        else {
            return null;
        }
    }

    public String lookupGender(String code) {
        return lookupValue(code, "ehr_lookups", "gender_codes", "code", "meaning");
    }

    public void sendHusbandryNotification(String requestId, String id, String project){


    }

    //TODO: created methods to record daterequested
    public void updateVVC(){

    }
    //TODO: send notification once the vvc is requested
    public void sendVvcNotification(String requestid){
        Module ehr = ModuleLoader.getInstance().getModule("EHR");
        VvcNotification sendNotifcation = new VvcNotification(ehr, requestid);
    }

    public String checkScheduledWaterTask(List<Map<String, Object>> recordsInTransaction)
    {
        int i=0;

        if (recordsInTransaction != null)
        {
            Calendar currentTime = Calendar.getInstance();
            Calendar limitTime = Calendar.getInstance();
            limitTime.set(Calendar.HOUR_OF_DAY, 13);
            limitTime.set(Calendar.MINUTE, 30);

            for (Map<String, Object> origMap : recordsInTransaction)
            {
                Map<String, Object> map = new CaseInsensitiveHashMap<>(origMap);
                if (!map.containsKey("date"))
                {
                    _log.warn("TriggerScriptHelper.checkScheduledWaterTask was passed a previous record lacking a date");
                    continue;
                }

                try
                {
                    String objectId = ConvertHelper.convert(map.get("objectid"), String.class);
                    if (objectId != null)
                    {
                        Date waterClientDate=ConvertHelper.convert(map.get("date"), Date.class);
                        int qcState = ConvertHelper.convert(map.get("qcstate"), Integer.class);
                        Calendar waterDate = Calendar.getInstance();
                        waterDate.setTime(waterClientDate);
                        //ConvertHelper.convert(map.get("qcstate"), Number.class);
                        if (waterDate.after(limitTime) || currentTime.after(limitTime))
                        {

                            //Check the value for the schedule qcstate
                            int tempOrdinal = EHRService.QCSTATES.Scheduled.ordinal();
                            if (ConvertHelper.convert(map.get("assignto"), String.class).equals("animalcare") && (qcState-1) == EHRService.QCSTATES.Scheduled.ordinal())
                                i++;
                        }
                    }
                }
                catch (ConversionException e)
                {
                    _log.error("TriggerScriptHelper.checkScheduleWaterTask was unable to parse date or qcstate", e);
                    throw e;
                }
            }
        }
        if (i>0)
        {
            return "At least one water is schedule after than 1:30 PM" ;
        }

        return null;
    }

    public void updateWaterRow(String performedby, String parentId) throws Exception{

        if (parentId != null)
        {
            TableInfo waterTable = getTableInfo("study", "waterGiven");

            List<Map<String, Object>> toUpdate = new ArrayList<>();
            List<Map<String, Object>> oldKeys = new ArrayList<>();
            List<String> taskIds = new ArrayList<>();

            //This will always return only one row from the water table show I changed the map on volumeObject
            SimpleFilter schedulewater = new SimpleFilter(FieldKey.fromString("lsid"), parentId);
            TableSelector volumes = new TableSelector(waterTable, PageFlowUtil.set("lsid", "volume", "taskid", "objectid"), schedulewater, null);
            Map <String,Object>[] volumeObject = volumes.getMapArray();

            if (volumeObject.length > 0)
            {
                for (Map<String,Object> volumeMap : volumeObject)
                {
                    String lsid = ConvertHelper.convert(volumeMap.get("lsid"), String.class);
                    Double oldVolume = ConvertHelper.convert(volumeMap.get("volume"), Double.class);

                    taskIds.add(ConvertHelper.convert(volumeMap.get("taskid"), String.class));

                    Map<String, Object> r = new CaseInsensitiveHashMap<>();

                    r.put("lsid", lsid);
                    r.put("volume", 0);
                    r.put("qcstate",EHRService.QCSTATES.Completed.getQCState(getContainer()).getRowId());
                    r.put("parentid", null);
                    r.put("remarks", "Request completed by:" + performedby+" Volume given: "+oldVolume);
                    r.put("scheduledVolume",oldVolume);
                    //r.put("enddate", row.g);
                    toUpdate.add(r);

                    Map<String, Object> keyMap = new CaseInsensitiveHashMap<>();
                    keyMap.put("lsid", lsid);
                    oldKeys.add(keyMap);
                }

                if (!toUpdate.isEmpty())
                {
                    Container container = getContainer();
                    User user = getUser();
                    Map<String, Object> context = getExtraContext();
                    List<Map<String, Object>> updatedRows = waterTable.getUpdateService().updateRows(user, container, toUpdate, oldKeys, null, context);
                    if (!updatedRows.isEmpty())
                    {
                        completeWaterGivenTask(taskIds, waterTable);
                    }
                }
            }
        }
    }

    private void completeWaterGivenTask(List<String> taskIds, TableInfo waterTable)
    {
        TableInfo taskTable = getTableInfo("ehr", "tasks");

        SimpleFilter waterTasks = new SimpleFilter(FieldKey.fromString("taskid"), taskIds, CompareType.IN);
        waterTasks.addCondition(FieldKey.fromString("qcstate/label"), EHRService.QCSTATES.Scheduled.getLabel(),CompareType.EQUAL);
        TableSelector wt = new TableSelector(waterTable, Collections.singleton("taskid"),waterTasks,null);

        List<String> remainingTask = wt.getArrayList(String.class);
        if (remainingTask.isEmpty())
        {
            SimpleFilter waterTasksToCompleted = new SimpleFilter(FieldKey.fromString("taskid"), taskIds, CompareType.IN);
            TableSelector completedWaterTask = new TableSelector(waterTable,Collections.singleton("taskid"),waterTasksToCompleted,null);
            List<String> lwt = completedWaterTask.getArrayList(String.class);
            //Look for task in Task table to complete task

            //note: allow draft records to count
            SimpleFilter filtertask = new SimpleFilter(FieldKey.fromString("taskid"), lwt, CompareType.IN);
            TableSelector tasks = new TableSelector(taskTable, filtertask, null);
            tasks.forEach(new Selector.ForEachBlock<ResultSet>()
            {
                @Override
                public void exec(ResultSet rs) throws SQLException
                {
                    String taskid = rs.getString("taskid");

                    Map<String, Object> toUpdate = new CaseInsensitiveHashMap<>();
                    toUpdate.put("qcstate", EHRService.QCSTATES.Completed.getQCState(getContainer()).getRowId());
                    toUpdate.put("taskid", taskid);
                    Table.update(getUser(), EHRSchema.getInstance().getSchema().getTable(EHRSchema.TABLE_TASKS), toUpdate, taskid);
                }
            });
        }
    }

    public String waterLastThreeDays(String id, Date date, List<Map<String, Object>> recordsInTransaction){

        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("This animal has not received the required water for the last three days, requires exception from Vet staff <br>");
        int waterdays =0;

        if (recordsInTransaction != null)
        {
            Calendar waterTime = Calendar.getInstance();
            Calendar limitTime = Calendar.getInstance();
            waterTime.setTime(date);
            limitTime.setTime(date);
            limitTime.add(Calendar.DATE, -2);
            //limitTime.set(Calendar.MINUTE, 30);

            try
            {
                String animalId = id;


                //Using two hashtable for keeping track of unique waters and one for the sum of waters for the last three days
                Hashtable<Calendar, WaterInfo> waterAmount = new Hashtable<Calendar, WaterInfo>();
                Hashtable<String, WaterInfo> uniqueWater = new Hashtable<String, WaterInfo>();

                //have to zero out the time for all the time too match exactly, the db does not return times with the date from waterPrePivot
                limitTime.set(Calendar.MILLISECOND, 0);
                limitTime.set(Calendar.SECOND, 0);
                limitTime.set(Calendar.MINUTE, 0);
                limitTime.set(Calendar.HOUR, 0);
                limitTime.set(Calendar.HOUR_OF_DAY, 0);
                limitTime.set(Calendar.AM_PM, 0);
                for (int i = 0; i <= 2; i++)
                {
                    //Calendar tempCalendar = Calendar.getInstance();
                    Calendar tempCalendar = (Calendar) limitTime.clone();


                    tempCalendar.add(Calendar.DATE, i);
                    //initiating the total amount of water for a day with -1.0, this allow for including animals in the project
                    WaterInfo tempWater = new WaterInfo(tempCalendar.getTime(), -1.0);
                    waterAmount.put(tempCalendar, tempWater);
                }
                Double avWeight = new Double (0);

                TableInfo waterGiven = getTableInfo("study","WaterRecentWeight");
                SimpleFilter filter = new SimpleFilter(FieldKey.fromString("Id"), animalId);
                filter.addCondition(FieldKey.fromString("date"), limitTime.getTime(),CompareType.DATE_GTE);
                TableSelector rawPreviousWater = new TableSelector(waterGiven, PageFlowUtil.set("id", "date", "volume", "InnerWeight", "objectid"),filter, null);
                Map<String, Object>[] waterFromServer = rawPreviousWater.getMapArray();

                //updating and adding waters from server, objectid will take are of any duplicates

                if (waterFromServer.length>0){
                    for (Map<String,Object> saveWater : waterFromServer){
                        WaterInfo waters = new WaterInfo(ConvertHelper.convert(saveWater.get("date"), Date.class), ConvertHelper.convert(saveWater.get("volume"), Double.class),ConvertHelper.convert(saveWater.get("InnerWeight"), Double.class));
                        String waterId = new String(ConvertHelper.convert(saveWater.get("objectid"), String.class));
                        uniqueWater.put(waterId, waters);

                    }
                }

                for (Map<String, Object> origMap : recordsInTransaction)
                {
                    Map<String, Object> map = new CaseInsensitiveHashMap<>(origMap);
                    if (!map.containsKey("date"))
                    {
                        _log.warn("TriggerScriptHelper.checkScheduledWaterTask was passed a previous record lacking a date");
                        continue;
                    }

                    Date rowDate = ConvertHelper.convert(map.get("date"), Date.class);
//                    Calendar zeroMills = Calendar.getInstance();
//                    zeroMills.setTime(rowDate);
//                    zeroMills.set(Calendar.MILLISECOND, 0);
//                    rowDate=zeroMills.getTime();

                    //only one row does not go to add the water amount uploaded

                    if (date.compareTo(rowDate)>=0)
                    {
                        Double weightDate = ConvertHelper.convert(map.get("weight"), Double.class);
                        if (weightDate == null){
                            TableInfo animalWeight = getTableInfo("study","demographicsMostRecentWeight");
                            SimpleFilter filterWeight = new SimpleFilter(FieldKey.fromString("Id"), animalId);
                            TableSelector ts = new TableSelector(animalWeight, Collections.singleton("MostRecentWeight"), filterWeight, null);
                            weightDate = ts.getObject(Double.class);
                        }
                        WaterInfo clientWater = new WaterInfo(ConvertHelper.convert(map.get("date"), Date.class), ConvertHelper.convert(map.get("volume"), Double.class),weightDate);
                        String clientObjectid = ConvertHelper.convert(map.get("objectid"), String.class);
                        uniqueWater.put(clientObjectid, clientWater);
                    }
                }

                if (!uniqueWater.isEmpty()){

                    Collection waterCollection = uniqueWater.values();

                    for (Object wi : waterCollection){
                        WaterInfo tempWater = (WaterInfo)wi;
                        Calendar tempCal = Calendar.getInstance();
                        tempCal.setTime(tempWater.getDate());


                        addWaterToDate(waterAmount, tempCal, tempWater.getQuantity(), tempWater.getWeigth());
                    }
                }

                if (!waterAmount.isEmpty()){
                    int maxDays  = 0;
                    int initialDays = 0;
                    Collection waterLastThreedays = waterAmount.values();

                    for (Object wl : waterLastThreedays){
                        WaterInfo waterCheck = (WaterInfo)wl;

                        if (waterCheck.getQuantity()==-1){
                            initialDays ++;
                        }
                        if (waterCheck.getQuantity()/waterCheck.getWeigth()<20){
                            maxDays++;
                        }
                    }
                    if (maxDays>2 && initialDays == 0){
                        return errorMsg.toString();
                    }
                }


            }catch(ConversionException e)
            {
                _log.error("TriggerScriptHelper.verifyBloodVolume was unable to parse date or qcstate", e);
                throw e;

            }






            if (waterdays>=2){

                return errorMsg.toString();
            }else
            {
                return null;
            }
        }
        return null;

    }

    public void addWaterToDate (Hashtable<Calendar, WaterInfo> waterAmount, Calendar HashDate, Double volume, Double weight){

        HashDate.set(Calendar.MILLISECOND, 0);
        HashDate.set(Calendar.SECOND, 0);
        HashDate.set(Calendar.MINUTE, 0);
        HashDate.set(Calendar.HOUR, 0);
        HashDate.set(Calendar.HOUR_OF_DAY, 0);
        HashDate.set(Calendar.AM_PM, 0);

        WaterInfo waterDate = waterAmount.get(HashDate);
        Double tempVolume = waterDate.getQuantity();
        if (tempVolume >= 0){
            tempVolume += volume;
            WaterInfo tempWater= new WaterInfo(tempVolume,weight);
            waterAmount.put(HashDate,tempWater);

        }else if (tempVolume == -1){

            WaterInfo tempWater= new WaterInfo(volume,weight);
            waterAmount.put(HashDate,tempWater);

        }


    }



    public static class WaterInfo implements Comparable<org.labkey.ehr.utils.TriggerScriptHelper.BloodInfo>
    {
        //private String _objectId;
        private Date _date;
        private double _quantity;
        private double _weight;

        public WaterInfo() {}

        public WaterInfo( Double quantity, Double weight)
        {
            //_objectId = objectId;
            //setDate(date);
            _quantity = quantity;
            setWeight(weight);
        }
        public WaterInfo( Date date, Double quantity)
        {
            //_objectId = objectId;
            setDate(date);
            _quantity = quantity;
            //setWeight(weight);
        }
        public WaterInfo(Date date, Double quantity, Double weight)
        {
            //_objectId = objectId;
            setDate(date);
            _quantity = quantity;
            setWeight(weight);
        }

        @Override
        public int compareTo(@NotNull org.labkey.ehr.utils.TriggerScriptHelper.BloodInfo o)
        {
            return getDate().compareTo(o.getDate());
        }

        public Date getDate()
        {
            return _date;
        }

        public double getQuantity()
        {
            return _quantity;
        }

        public double getWeigth()
        {
            return _weight;
        }

        public void setDate(Date date)
        {
            _date = date;
        }

        public void setQuantity(double quantity)
        {
            _quantity = quantity;
        }

        public void setWeight(double weight)
        {
            _weight = weight;
        }


    }



}
