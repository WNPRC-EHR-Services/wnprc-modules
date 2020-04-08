#Author: Daniel Nicolalde

# print the starting time for the transform script
writeLines(paste("Processing start time:",Sys.time(),"\n",sep=" "));

suppressMessages(library(Rlabkey));
rVersion = paste(R.version$major, R.version$minor, R.version$arch, R.version$os, sep=".");

########################################## FUNCTIONS ##########################################

getCurveFitInputCol <- function(runProps, fiRunCol, defaultFiCol)
{
    runCol = runProps$val1[runProps$name == fiRunCol];
    if (runCol == "FI") {
        runCol = "fi"
    } else if (runCol == "FI-Bkgd") {
    	runCol = "fiBackground"
    } else if (runCol == "FI-Bkgd-Blank" | runCol == "FI-Bkgd-Neg") {
    	runCol = "FIBackgroundNegative"
    } else {
        runCol = defaultFiCol
    }
    runCol;
}

getFiDisplayName <- function(fiCol)
{
    displayVal = fiCol;
    if (fiCol == "fi") {
        displayVal = "FI"
    } else if (fiCol == "fiBackground") {
        displayVal = "FI-Bkgd"
    } else if (fiCol == "FIBackgroundNegative") {
        displayVal = "FI-Bkgd-Neg"
    }
    displayVal;
}

fiConversion <- function(val)
{
    1 + max(val,0);
}

# fix for Issue 14070 - capp the values at something that can be stored in the DB
maxValueConversion <- function(val)
{
    min(val, 10e37);
}

populateTitrationData <- function(rundata, titrationdata)
{
    rundata$isStandard = FALSE;
    rundata$isQCControl = FALSE;
    rundata$isUnknown = FALSE;
    rundata$isOtherControl = FALSE;

    # apply the titration data to the rundata object
    if (nrow(titrationdata) > 0)
    {
        for (tIndex in 1:nrow(titrationdata))
        {
            titrationName = as.character(titrationdata[tIndex,]$Name);
            titrationRows = rundata$titration == "true" & rundata$description == titrationName;
            rundata$isStandard[titrationRows] = (titrationdata[tIndex,]$Standard == "true");
            rundata$isQCControl[titrationRows] = (titrationdata[tIndex,]$QCControl == "true");
            rundata$isOtherControl[titrationRows] = (titrationdata[tIndex,]$OtherControl == "true");
        }
    }

    # Issue 20316: incorrectly labeling unselected titrated unknowns as not "isUnknown"
    rundata$isUnknown[!(rundata$isStandard | rundata$isQCControl | rundata$isOtherControl)] = TRUE;

    rundata
}

isNegativeControl <- function(analytedata, analyteVal)
{
    negControl = FALSE;
    if (!is.null(analytedata$NegativeControl))
    {
        if (!is.na(analytedata$NegativeControl[analytedata$Name == analyteVal]))
        {
            negControl = as.logical(analytedata$NegativeControl[analytedata$Name == analyteVal]);
        }
    }

    negControl
}

populateNegativeBeadSubtraction <- function(rundata, analytedata)
{
    # initialize the FI-Bkgd-Neg variable
    rundata$FIBackgroundNegative = NA;

    # read the run property from user to determine if we are to subtract the negative control bead from unks only
    unksIndex = !(rundata$isStandard | rundata$isQCControl | rundata$isOtherControl);
    unksOnly = TRUE;
    if (any(run.props$name == "SubtNegativeFromAll"))
    {
        if (labkey.transform.getRunPropertyValue(run.props, "SubtNegativeFromAll") == "1")
        {
            unksOnly = FALSE;
        }
    }

    # loop through each analyte and subtract the negative control bead as specified in the analytedata
    for (index in 1:nrow(analytedata))
    {
       analyteName = analytedata$Name[index];
       negativeBeadName = as.character(analytedata$NegativeBead[index]);
       negativeControl = isNegativeControl(analytedata, analyteName);

       # store a boolean vector of indices for negControls and analyte unknowns
       analyteIndex = rundata$name == analyteName;
       negControlIndex = rundata$name == negativeBeadName;

       if (!negativeControl & !is.na(negativeBeadName) & any(negControlIndex) & any(analyteIndex))
       {
           # loop through the unique dataFile/description/excpConc/dilution combos and subtract the mean
           # negative control fiBackground from the fiBackground of the given analyte
           negControlData = rundata[negControlIndex,];
           combos = unique(subset(negControlData, select=c("dataFile", "description", "dilution", "expConc")));

           for (index in 1:nrow(combos))
           {
                dataFile = combos$dataFile[index];
                description = combos$description[index];
                dilution = combos$dilution[index];
                expConc = combos$expConc[index];

                # only standards have expConc, the rest are NA
                combo = rundata$dataFile == dataFile & rundata$description == description & rundata$dilution == dilution & !is.na(rundata$expConc) & rundata$expConc == expConc;
                if (is.na(expConc))
                {
                    combo = rundata$dataFile == dataFile & rundata$description == description & rundata$dilution == dilution & is.na(rundata$expConc);
                }

                # get the mean negative bead FI-Bkgrd values for the given description/dilution
                # issue 20457: convert negative "negative control" mean to zero to prevent subtracting a negative
                negControlMean = max(mean(rundata$fiBackground[negControlIndex & combo]), 0);

                # calc the FIBackgroundNegative for all of the non-"Negative Control" analytes for this combo
                if (unksOnly) {
                    rundata$FIBackgroundNegative[unksIndex & analyteIndex & combo] = rundata$fiBackground[unksIndex & analyteIndex & combo] - negControlMean;
                } else{
                    rundata$FIBackgroundNegative[analyteIndex & combo] = rundata$fiBackground[analyteIndex & combo] - negControlMean;
                }
           }
       }
    }

    rundata
}

writeErrorOrWarning <- function(type, msg)
{
    write(paste(type, type, msg, sep="\t"), file=error.file, append=TRUE);
    if (type == "error") {
        quit("no", 0, FALSE);
    }
}

convertToFileName <- function(name)
{
    # Issue 23230: slashes in the file name cause issues creating the PDFs, for now convert "/" and " " to "_"
    gsub("[/ ]", "_", name);
}

######################## STEP 0: READ IN THE RUN PROPERTIES AND RUN DATA #######################

run.props = labkey.transform.readRunPropertiesFile("${runInfo}");

# save the important run.props as separate variables
run.data.file = labkey.transform.getRunPropertyValue(run.props, "runDataFile");
run.output.file = run.props$val3[run.props$name == "runDataFile"];
error.file = labkey.transform.getRunPropertyValue(run.props, "errorsFile");

# read in the run data file content
run.data = read.delim(run.data.file, header=TRUE, sep="\t");

# read in the analyte information (to get the mapping from analyte to standard/titration)
analyte.data.file = labkey.transform.getRunPropertyValue(run.props, "analyteData");
analyte.data = read.delim(analyte.data.file, header=TRUE, sep="\t");

# read in the titration information
titration.data.file = labkey.transform.getRunPropertyValue(run.props, "titrationData");
titration.data = data.frame();
if (file.exists(titration.data.file)) {
    titration.data = read.delim(titration.data.file, header=TRUE, sep="\t");
}
run.data <- populateTitrationData(run.data, titration.data);

# determine if the data contains both raw and summary data
# if both exists, only the raw data will be used for the calculations
bothRawAndSummary = any(run.data$summary == "true") & any(run.data$summary == "false");

######################## STEP 1: SET THE VERSION NUMBERS ################################

runprop.output.file = labkey.transform.getRunPropertyValue(run.props, "transformedRunPropertiesFile");
fileConn<-file(runprop.output.file);
writeLines(c(paste("TransformVersion",transformVersion,sep="\t"),
paste("RuminexVersion",ruminexVersion,sep="\t"),
paste("RVersion",rVersion,sep="\t")), fileConn);
close(fileConn);
