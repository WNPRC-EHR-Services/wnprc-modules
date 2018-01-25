package org.labkey.breeding;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.jetbrains.annotations.NotNull;
import org.labkey.api.action.NullSafeBindException;
import org.labkey.api.admin.ImportException;
import org.labkey.api.admin.StaticLoggerGetter;
import org.labkey.api.data.Container;
import org.labkey.api.security.User;
import org.labkey.api.writer.FileSystemFile;
import org.labkey.study.importer.DatasetDefinitionImporter;
import org.labkey.study.importer.DatasetImportUtils;
import org.labkey.study.importer.StudyImportContext;
import org.labkey.study.writer.StudyArchiveDataTypes;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

/**
 * Helper class to initiate the dataset metadata import
 */
final class DatasetImportHelper
{
    /**
     * Logger for logging the logs
     */
    private static final Logger LOG = Logger.getLogger(DatasetImportHelper.class);

    /**
     * Executes the dataset metadata import utilizing LabKey's predefined dataset definition importer.
     *
     * @param user         User executing the import
     * @param container    Container into which to import the dataset. Expected to have an active 'study' module
     * @param studyXmlFile XML file defining the study, including the manifest and the metadata.
     */
    static void importDatasetMetadata(@NotNull User user, @NotNull Container container, @NotNull File studyXmlFile)
            throws IOException, SQLException, DatasetImportUtils.DatasetLockExistsException, XmlException, ImportException
    {
        assert container.hasActiveModuleByName("study");
        assert studyXmlFile.exists();

        LOG.debug(String.format("[import] loading dataset metadata... [from=%s,to=%s]",
                studyXmlFile.getAbsolutePath(), container.getName()));
        FileSystemFile root = new FileSystemFile(studyXmlFile.getParentFile());
        StudyImportContext ctx = new StudyImportContext(user, container, studyXmlFile,
                Collections.singleton(StudyArchiveDataTypes.DATASET_DEFINITIONS), new StaticLoggerGetter(LOG), root);
        DatasetDefinitionImporter ddi = new DatasetDefinitionImporter();
        ddi.process(ctx, root, new NullSafeBindException(container, "import"));
        LOG.debug("[import] ...done.");
    }
}
