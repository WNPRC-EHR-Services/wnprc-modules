package org.labkey.wnprc_ehr.updates;

import org.apache.log4j.Logger;
import org.labkey.api.collections.CaseInsensitiveMapWrapper;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.data.DbScope;
import org.labkey.api.data.Results;
import org.labkey.api.data.SimpleFilter;
import org.labkey.api.data.TableInfo;
import org.labkey.api.ehr.EHRService;
import org.labkey.api.module.Module;
import org.labkey.api.module.ModuleContext;
import org.labkey.api.query.BatchValidationException;
import org.labkey.api.query.FieldKey;
import org.labkey.api.query.QueryService;
import org.labkey.api.query.QueryUpdateService;
import org.labkey.api.query.UserSchema;
import org.labkey.api.security.User;
import org.labkey.dbutils.api.SimpleQueryFactory;
import org.labkey.wnprc_ehr.DatasetImportHelper;

import java.io.File;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused") // reflection
public class UpdateTo15_15 implements ModuleUpdate.Updater
{
    /**
     * Logger for logging the logs
     */
    private static Logger LOG = Logger.getLogger(UpdateTo15_15.class);

    @Override
    public boolean applies(ModuleContext ctx)
    {
        return ctx.getOriginalVersion() < 15.15;
    }

    @Override
    public void doAfterUpdate(ModuleContext ctx)
    {
        // no-op
    }

    @Override
    public void doBeforeUpdate(ModuleContext ctx)
    {
        // no-op
    }

    @Override
    public void doVersionUpdate(ModuleContext ctx)
    {
        // no-op
    }

    @Override
    public void onStartup(ModuleContext ctx, Module module)
    {
        EHRService es = EHRService.get();
        File file = new File(Paths.get(module.getExplodedPath().getAbsolutePath(), "referenceStudy", "study").toFile(), "study.xml");
        getEHRContainers(module, es).forEach(c -> updateContainer(es.getEHRUser(c), c, file));
    }

    /**
     * Returns a new JavaScript animal history report row built from the passed name, title, query, and description as a
     * {@link CaseInsensitiveMapWrapper} (which is required by LabKey). The category will default to "Colony Management"
     *
     * @param reportName  Name of the report
     * @param reportTitle Title to show on the animal history tab
     * @param queryName   Name of the JavaScript report to load
     * @param description Description of the report
     * @return Case-insensitive map of fields to data to insert into the database
     */
    private CaseInsensitiveMapWrapper<Object> createJavaScriptReportRow(String reportName, String reportTitle, String queryName, String description)
    {
        return new CaseInsensitiveMapWrapper<>(Stream.of(new AbstractMap.SimpleEntry<>("reportname", reportName)
                , new AbstractMap.SimpleEntry<>("category", "Colony Management")
                , new AbstractMap.SimpleEntry<>("reporttype", "js")
                , new AbstractMap.SimpleEntry<>("reporttitle", reportTitle)
                , new AbstractMap.SimpleEntry<>("schemaname", "study")
                , new AbstractMap.SimpleEntry<>("queryname", queryName)
                , new AbstractMap.SimpleEntry<>("description", description)
                , new AbstractMap.SimpleEntry<>("visible", true)
                , new AbstractMap.SimpleEntry<>("todayonly", false)
                , new AbstractMap.SimpleEntry<>("queryhaslocation", false)
        ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
    }

    /**
     * Returns all EHR module containers
     *
     * @param module Module being updated
     * @param es     EHR service used to get the EHR containers
     * @return Stream of all containers with EHR and the passed module enabled
     */
    private Stream<Container> getEHRContainers(Module module, EHRService es)
    {
        return ContainerManager.getAllChildrenWithModule(ContainerManager.getRoot(), module).stream()
                .map(es::getEHRStudyContainer).distinct();
    }

    /**
     * Executes the update on the passed container as the passed user, using the passed file to retrieve the metdata
     *
     * @param u User executing the update
     * @param c Container to update
     * @param f File containing the dataset metdata to import
     */
    private void updateContainer(User u, Container c, File f)
    {
        LOG.debug(String.format("importing WNPRC 15.15 study metadata and updating EHR reports in container: %s", c.getName()));
        DatasetImportHelper.safeImportDatasetMetadata(u, c, f);
        updateReports(u, c);
    }

    /**
     * Replaces the old "pregnancies" report in animal history with three new JavaScript reports for pregnancies,
     * ultrasounds, and breeding encounters
     *
     * @param user      User executing the update
     * @param container Container to update
     */
    private void updateReports(User user, Container container)
    {
        UserSchema schema = QueryService.get().getUserSchema(user, container, "ehr");
        assert schema != null;

        try (DbScope.Transaction tx = schema.getDbSchema().getScope().ensureTransaction())
        {
            TableInfo table = schema.getTable("reports");
            assert table != null;

            QueryUpdateService qus = table.getUpdateService();
            assert qus != null;

            SimpleQueryFactory factory = new SimpleQueryFactory(user, container);
            try (Results results = factory.makeQuery("ehr", "reports").select(new SimpleFilter(FieldKey.fromString("reportname"), "pregnancies")))
            {
                ArrayList<Map<String, Object>> toDelete = new ArrayList<>();
                results.iterator().forEachRemaining(toDelete::add);

                if (toDelete.size() > 0)
                {
                    LOG.debug("deleting old pregnancies report, replacing with new one");
                    qus.deleteRows(user, container, toDelete, null, null);
                }
            }

            LOG.debug("inserting new pregnancies, breeding_encounters, and ultrasounds reports to animal history");
            BatchValidationException bve = new BatchValidationException();
            qus.insertRows(user, container, Arrays.asList(
                    createJavaScriptReportRow("pregnancies", "Pregnancies", "PregnancyReport", "This report contains a list of known pregnancies, including conception dates and sire (where available)"),
                    createJavaScriptReportRow("breeding_encounters", "Breeding Encounters", "BreedingReport", "This report contains a list of encounters between a breeding dam and a possible sire"),
                    createJavaScriptReportRow("ultrasounds", "Ultrasounds", "UltrasoundReport", "This report details the ultrasounds performed on breeding dams")),
                    bve, null, null);

            if (bve.hasErrors())
                throw bve;

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.warn("unable to update EHR animal history reports, will need to be addressed manually", e);
        }
    }
}
