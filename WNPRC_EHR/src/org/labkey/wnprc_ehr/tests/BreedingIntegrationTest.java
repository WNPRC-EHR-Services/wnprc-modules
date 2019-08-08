package org.labkey.wnprc_ehr.tests;

import org.apache.xmlbeans.XmlException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.labkey.api.action.NullSafeBindException;
import org.labkey.api.admin.ImportException;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.module.FolderTypeManager;
import org.labkey.api.module.ModuleLoader;
import org.labkey.api.security.User;
import org.labkey.api.study.Dataset;
import org.labkey.api.study.Study;
import org.labkey.api.study.TimepointType;
import org.labkey.api.test.TestWhen;
import org.labkey.api.util.GUID;
import org.labkey.api.util.JunitUtil;
import org.labkey.api.util.TestContext;
import org.labkey.study.importer.DatasetImportUtils;
import org.labkey.study.model.StudyImpl;
import org.labkey.study.model.StudyManager;
import org.labkey.wnprc_ehr.DatasetImportHelper;
import org.labkey.wnprc_ehr.TriggerScriptHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TestWhen(TestWhen.When.DRT)
public class BreedingIntegrationTest extends Assert
{
    private static final String TEST_CONTAINER_NAME = GUID.makeHash();

    private Container _container;
    private User _user;

    @BeforeClass
    public void createContainer() throws XmlException, SQLException, ImportException, DatasetImportUtils.DatasetLockExistsException, IOException
    {
        _container = ContainerManager.ensureContainer(JunitUtil.getTestContainer(), TEST_CONTAINER_NAME);
        _user = TestContext.get().getUser();


        // ARRANGE: create the study in the breeding test container
        ContainerManager.setFolderType(_container, FolderTypeManager.get().getFolderType("Study"), _user,
                new NullSafeBindException(_container, "test-import"));
        StudyImpl s = new StudyImpl(_container, "Breeding Test Study");
        s.setTimepointType(TimepointType.DATE); // must be set to avoid NullPointerException
        s.setSubjectColumnName("SubjectID");    // must be set due to non-null constraint
        s.setSubjectNounPlural("Subjects");     // must be set due to non-null constraint
        s.setSubjectNounSingular("Subject");    // must be set due to non-null constraint
        Study study = StudyManager.getInstance().createStudy(_user, s);

        // ACT: load the dataset metadata from the reference study in the resources
        File file = new File(Paths.get(ModuleLoader.getInstance().getModule("WNPRC_EHR")
                .getExplodedPath().getAbsolutePath(), "referenceStudy", "study").toFile(), "study.xml");
        DatasetImportHelper.importDatasetMetadata(_user, _container, file);

        // ASSERT: make sure that the datasets we are expecting got created
        List<? extends Dataset> datasets = study.getDatasets();
        Assert.assertArrayEquals(new String[]{"breeding_encounters", "pregnancies", "pregnancy_outcomes", "ultrasounds"},
                datasets.stream().map(Dataset::getName).sorted().toArray());
    }

    @AfterClass
    public void deleteContainer()
    {
        if (_container != null)
            ContainerManager.delete(_container, _user);
    }

    @Test
    public void createBreedingEncountersFromHousingRecords()
    {
        TriggerScriptHelper tsh = TriggerScriptHelper.create(_user.getUserId(), _container.getId());

//        tsh.createBreedingRecordsFromHousingChanges();

        List<Map<String, Object>> housingRecords = new ArrayList<>();

        //female 1 - end
        JSONObject row4 = new JSONObject();
        row4.put("Id", "r09048");
        row4.put("date", new Timestamp(118, 10, 20, 13, 15, 0, 0));
        row4.put("room", "c420");
        row4.put("cage", "0039");
        row4.put("remark", "female 1 end");
        row4.put("ejacConfirmed", false);
        row4.put("reason", "Breeding ended");
        row4.put("performedby", "gg");
        row4.put("sex", "f");
        housingRecords.add(row4);

        //male 1a - end
        JSONObject row5 = new JSONObject();
        row5.put("Id", "r16080");
        row5.put("date", new Timestamp(118, 10, 20, 13, 15, 0, 0));
        row5.put("room", "c420");
        row5.put("cage", "0039");
        row5.put("remark", "male 1a end");
        row5.put("ejacConfirmed", true);
        row5.put("reason", "Breeding ended");
        row5.put("performedby", "gg");
        row5.put("sex", "m");
        housingRecords.add(row5);

        //male 1b - end
        JSONObject row6 = new JSONObject();
        row6.put("Id", "r15020");
        row6.put("date", new Timestamp(118, 10, 20, 13, 15, 0, 0));
        row6.put("room", "c420");
        row6.put("cage", "0039");
        row6.put("remark", "male 1b end");
        row6.put("ejacConfirmed", false);
        row6.put("reason", "Breeding ended");
        row6.put("performedby", "gg");
        row6.put("sex", "m");
        housingRecords.add(row6);

        //female 1 - start
        JSONObject row1 = new JSONObject();
        row1.put("Id", "r09048");
        row1.put("date", new Timestamp(118, 10, 20, 8, 15, 0, 0));
        row1.put("room", "c420");
        row1.put("cage", "0039");
        row1.put("remark", "female 1 start");
        row1.put("project", "20150801");
        row1.put("reason", "Breeding");
        row1.put("performedby", "gg");
        row1.put("sex", "f");
        housingRecords.add(row1);

        //male 1a - start
        JSONObject row2 = new JSONObject();
        row2.put("Id", "r16080");
        row2.put("date", new Timestamp(118, 10, 20, 8, 15, 0, 0));
        row2.put("room", "c420");
        row2.put("cage", "0039");
        row2.put("remark", "male 1a start");
        row2.put("project", "20150801");
        row2.put("reason", "Breeding");
        row2.put("performedby", "gg");
        row2.put("sex", "m");
        housingRecords.add(row2);

        //male 1b - start
        JSONObject row3 = new JSONObject();
        row3.put("Id", "r15020");
        row3.put("date", new Timestamp(118, 10, 20, 8, 15, 0, 0));
        row3.put("room", "c420");
        row3.put("cage", "0039");
        row3.put("remark", "male 1b start");
        row3.put("project", "20150801");
        row3.put("reason", "Breeding");
        row3.put("performedby", "gg");
        row3.put("sex", "m");
        housingRecords.add(row3);

        //female 2a - start
        JSONObject row7 = new JSONObject();
        row7.put("Id", "r15005");
        row7.put("date", new Timestamp(118, 10, 22, 8, 15, 0, 0));
        row7.put("room", "cb11");
        row7.put("cage", "0067");
        row7.put("remark", "female 2a start");
        row7.put("project", "20150902");
        row7.put("reason", "Breeding");
        row7.put("performedby", "gg");
        row7.put("sex", "f");
        housingRecords.add(row7);

        //female 2b - start
        JSONObject row8 = new JSONObject();
        row8.put("Id", "r14118");
        row8.put("date", new Timestamp(118, 10, 22, 8, 15, 0, 0));
        row8.put("room", "cb11");
        row8.put("cage", "0067");
        row8.put("remark", "female 2b start");
        row8.put("project", "20150902");
        row8.put("reason", "Breeding");
        row8.put("performedby", "gg");
        row8.put("sex", "f");
        housingRecords.add(row8);

        //male 2 - start
        JSONObject row9 = new JSONObject();
        row9.put("Id", "r14003");
        row9.put("date", new Timestamp(118, 10, 22, 8, 15, 0, 0));
        row9.put("room", "cb11");
        row9.put("cage", "0067");
        row9.put("remark", "male 2 start");
        row9.put("project", "20150902");
        row9.put("reason", "Breeding");
        row9.put("performedby", "gg");
        row9.put("sex", "m");
        housingRecords.add(row9);

        //male 3 - start
        JSONObject row13 = new JSONObject();
        row13.put("Id", "r14003");
        row13.put("date", new Timestamp(118, 10, 22, 11, 15, 0, 0));
        row13.put("room", "ab110");
        row13.put("cage", "0029");
        row13.put("remark", "male 3 start");
        row13.put("project", "20180109");
        row13.put("reason", "Breeding");
        row13.put("performedby", "gg");
        row13.put("sex", "m");
        housingRecords.add(row13);

        //female 2b - end
        JSONObject row11 = new JSONObject();
        row11.put("Id", "r14118");
        row11.put("date", new Timestamp(118, 10, 22, 11, 15, 0, 0));
        row11.put("room", "cb11");
        row11.put("cage", "0071");
        row11.put("remark", "female 2b end");
        row11.put("ejacConfirmed", true);
        row11.put("reason", "Breeding ended");
        row11.put("performedby", "gg");
        row11.put("sex", "f");
        housingRecords.add(row11);

        //male 2 - end
        JSONObject row12 = new JSONObject();
        row12.put("Id", "r14003");
        row12.put("date", new Timestamp(118, 10, 22, 11, 15, 0, 0));
        row12.put("room", "cb11");
        row12.put("cage", "0067");
        row12.put("remark", "male 2 end");
        row12.put("ejacConfirmed", false);
        row12.put("reason", "Breeding ended");
        row12.put("performedby", "gg");
        row12.put("sex", "m");
        housingRecords.add(row12);

        //female 2a/3 - end/start
        JSONObject row10 = new JSONObject();
        row10.put("Id", "r15005");
        row10.put("date", new Timestamp(118, 10, 22, 11, 15, 0, 0));
        row10.put("room", "ab110");
        row10.put("cage", "0029");
        row10.put("remark", "female 2a/3 start again");
        row10.put("project", "20150902");
        row10.put("reason", "Breeding");
        row10.put("performedby", "gg");
        row10.put("sex", "f");
        housingRecords.add(row10);
    }
}