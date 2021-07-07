import * as React from "react";
import {
  useContext,
  useEffect,
  useRef,
  useState
} from "react";
import {
  InfoProps,
  ConfigProps,
  RowObj,
  UserEditableWeightFormValues,
} from "../../typings/main";
import {
  getQCStateByLabel,
  getQCStateByRowId,
  getQCStateMap,
  labkeyActionSelectWithPromise
} from "../../query/actions";
import {groupCages, saveRowsDirect} from "../../../query/helpers";
import { Button } from "react-bootstrap";
import AnimalInfoPane from "../../components/AnimalInfoPane";
import EnterWeightForm from "./EnterWeightForm";
import {
  getlocations,
  groupCommands,
  setupJsonData
} from "../../query/helpers";
//import {setupJsonData} from "../../../query/helpers";
import BatchModal from "../../components/BatchModal";
import SubmitModal from "../../components/SubmitModal";
import BulkEditModal from "../../components/BulkEditModal";
import Spinner from "../../../components/Spinner";
import SubmitForReviewModal from "../../components/SubmitForReviewModal";
import { AppContext } from "../App/ContextProvider";
import CustomAlert from "../../components/CustomAlert";
import * as dayjs from "dayjs";
import {ActionURL, Utils, Security, Filter} from "@labkey/api";
import {AnimalInfoStates, FormErrorLevels} from "../../../typings/main";
import { Command, CommandType } from "@labkey/api/dist/labkey/query/Rows";
import Collapse from "react-bootstrap/Collapse";

const sleep = ms => new Promise(resolve => setTimeout(resolve, ms));

/**
 * Main parent component that holds most of the state.
 */
const EnterWeightFormContainer: React.FunctionComponent<any> = props => {
  const { submit, submitted, setRestraintsInAppContext, restraints, setStartTimeInAppContext, setEndTimeInAppContext, setFormDataInAppContext, formdata, setCageDataInAppContext, cagedata, setTaskIdInAppContext, taskId, setFormFrameworkTypesInAppContext, wasSaved, setWasSavedInAppContext, setBulkEditUsedInAppContext, setBatchAddUsedInAppContext } = useContext(AppContext);
  const [animalId, setAnimalId] = useState<string>("");
  const [animalInfo, setAnimalInfo] = useState<InfoProps>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [locloading, setLocLoading] = useState<boolean>(false);
  const [loadingData, setLoadingData] = useState<boolean>(false);
  const [infoState, setInfoState] = useState<AnimalInfoStates>("waiting");
  const [location, setLocation] = useState<Array<string>>([]);
  const [ids, setIds] = useState<Array<string>>([]);
  const [submitBoxText, setSubmitBoxText] = useState<string>("Submit values?");
  const [current, setCurrent] = useState<number>(0);
  const [wasError, setWasError] = useState<boolean>(false);
  const [errorText, setErrorText] = useState<string>("");
  const [editMode, setEditMode] = useState<boolean>(false);
  const [QCMap, setQCMap] = useState<any>();
  const [reviewer, setReviewer] = useState<any>( Security.currentUser.id);
  const [showAlert, setShowAlert] = useState<boolean>(true);
  const [saving, setSaving] = useState<boolean>(false);
  const [showModal, setShowModal] = useState<string>();
  const [errorLevel, setErrorLevel] = useState<FormErrorLevels>("no-action");
  const [singleEditMode, setSingleEditMode] = useState(false);
  const [zoom, setZoom] = useState(100);

  const formEl = useRef(null);


  // we want to return the index where the animal exists ( could be multiple )
  const lookUpAnimalIndices = (formData: Array<RowObj>, animalid: string): Array<number> => {
    let formDataAnimalIndices: Array<number> = [];
    formData.forEach((item, idx) => {
      if (item.animalid.value === animalid) {
        formDataAnimalIndices.push(idx);
      }
    })
    return formDataAnimalIndices;
  }

  const getAnimalInfo = (index: number, animalid: any): void => {
    if (!formdata[index] || animalid == "")
      return;
    //check cache
    if (formdata[index].animal_info.value) {
      setAnimalInfo(formdata[index].animal_info.value);
      setInfoState("loading-success");
      return;
    }
    //need to show loading spinner
    setInfoState("loading");

    let config: ConfigProps = {
      schemaName: "study",
      queryName: "demographics",
      sort: "-date",
      filterArray: [
        Filter.create("Id", animalid, Filter.Types.EQUAL)
      ]
    };
    labkeyActionSelectWithPromise(config).then(data => {
      //cache animal info
      if (data["rows"][0]) {
        let animalInfo = data["rows"][0];
        //why is this formdata outdated


        setFormDataInAppContext((prevState: Array<RowObj>) => {
          let copyFormData: Array<RowObj> = [...prevState];
          let indices = lookUpAnimalIndices(copyFormData, animalInfo.Id);
          indices.forEach((index) => {
            copyFormData[index].animal_info.value = animalInfo;
            copyFormData[index].animal_info.error = "";
          })
          // Object.assign would also work
          return [...copyFormData];
        });
        setAnimalInfo(animalInfo);
        setInfoState("loading-success");
        /*setPrevWeight(data["rows"][0]["Id/MostRecentWeight/MostRecentWeight"]);
        validateItems("animalid", animalid);
        setAnimalError("");*/
      } else {
        setInfoState("loading-unsuccess");
        setFormDataInAppContext((prevState: Array<RowObj>) => {
          let copyFormData: Array<RowObj> = [...prevState];
          let indices = lookUpAnimalIndices(copyFormData, animalInfo.Id);
          indices.forEach((index) => {
            copyFormData[index].animal_info.error = "Animal Not Found";
          })
          // Object.assign would also work
          return [...copyFormData];
        });
        //TODO
        /*setAnimalError("Animal Not Found");
        validateItems("animalid", animalid)*/
      }
    });
  };

  const triggerLocation = (loc: Array<any>) => {
    setLocation(loc);
    setBatchAddUsedInAppContext();
  };

  //here we lifted up state from the BulkEditFields - > BatchEditModal -> this component
  const updateValues = (vals: UserEditableWeightFormValues): void => {

    setFormDataInAppContext((prevState: Array<RowObj>) => {
      let copyformdata: Array<RowObj> = [...prevState];

      copyformdata.forEach(item => {
        if (vals["weight"]["value"]){
          item["weight"]["value"] = vals["weight"]["value"];
        }
        if (vals["date"]["value"]){
          item["date"]["value"] = vals["date"]["value"];
        }
        if (vals["restraint"]["value"]){
          item["restraint"]["value"] = vals["restraint"]["value"];
        }
        if (vals["remark"]["value"]){
          item["remark"]["value"] = vals["remark"]["value"];
        }
      });
      return [...copyformdata];

    });

    onValidate();
    setBulkEditUsedInAppContext();
  };
  const triggerIds = (ids: any) => {
    setIds(ids);
    setBatchAddUsedInAppContext();
  };

  useEffect(() => {
    if (location.length == 0) {
      return;
    }
    let animaldata: Array<RowObj>;
    if (formdata[0] && formdata[0].animalid.value != ""){
      animaldata = [...formdata];
    } else {
      animaldata = [];
    }
    setLocLoading(true);
    Promise.all(getlocations(location)).then(d => {
      d.forEach((promise, i) => {
        if (promise["rows"]) {
          promise["rows"].forEach((row, i) => {
            let restraintObjectId: string = Utils.generateUUID().toUpperCase();
            animaldata.push({
              animalid: { value: row.Id, error: "" },
              date: { value: new Date(), error: "" },
              weight: { value: undefined, error: "" },
              remark: { value: "", error: "" },
              animal_location: {value: row.location, error: ""},
              objectid: {
                value: Utils.generateUUID().toUpperCase(),
                error: ""
              },
              lsid: { value: "", error: "" },
              restraint_objectid: { value: restraintObjectId, error: ""},
              QCState: {
                value: getQCStateByLabel(QCMap, "In Progress"),
                error: ""
              },
              command: { value: "insert", error: "" },
              collapsed: { value: true, error: "" },
              visibility: { value: "visible", error: "" },
              restraint: {value: "", error: "", objectid: restraintObjectId},
              validated: {value: false, error: ""},
              animal_info: {value: null, error: ""}
            });
          });
        }
        setFormDataInAppContext((prevState: Array<RowObj>) => {
          return [...animaldata, ...prevState]
        })
      });
      animaldata.forEach((item:RowObj) => {
        item["cage"] = {value: "", error: ""};
        if (item.animal_location.value != ""){
          item["cage"]["value"] = getRoomCage(item.animal_location.value as string)[1] ? getRoomCage(item.animal_location.value as string)[1] : "no_cage";
        }
      })
      setLocLoading(false);
      setErrorLevel("saveable");
    });
  }, [location]);

  useEffect(() => {
    document.body.style.zoom = zoom + "%";
  },[zoom])

  useEffect(() => {
    setFormIds();
  }, [ids]);

  //if we are in edit mode
  useEffect(() => {
    if (
      ActionURL.getParameter("taskid") == null &&
      ActionURL.getParameter("lsid") == null
    ) {
      return;
    }
    //set the ask id
    setEditMode(true);
    setLoadingData(true);

    //if there's no task id, then we don't have to update the task table... just update the records
    //or more generally if we are in edit mode, we don't have ot update the task id...
    setTaskIdInAppContext(ActionURL.getParameter("taskid"));

    //TODO clean this up
    let filter: Array<any> = [];
    if (ActionURL.getParameter("taskid")) {
      filter.push(
        Filter.create(
          "taskid",
          ActionURL.getParameter("taskid"),
          Filter.Types.EQUAL
        )
      );
    }
    if (ActionURL.getParameter("lsid")) {
      filter.push(
        Filter.create(
          "lsid",
          ActionURL.getParameter("lsid"),
          Filter.Types.EQUAL
        )
      );
    }

    //if we have an lsid but no taskid, this probably means we are editing an individual record,
    //we should not try to insert into taskid table in this case
    if (ActionURL.getParameter("taskid") == null &&
      ActionURL.getParameter("lsid") != null){
      setSingleEditMode(true);
    }

    //TODO what if the url is complete rubbish?

    let config: ConfigProps = {
      schemaName: "study",
      queryName: ActionURL.getParameter("formtype"),
      filterArray: filter,
      columns: "Id,date,weight,remark,objectid,lsid,QCState,taskid,restraint_objectid"
    };

    let newformdata: Array<RowObj> = [];
    let newformdatarestraints: Array<RowObj> = [];
    let restraintobjectids: Array<string> = [];
    labkeyActionSelectWithPromise(config).then(data => {

      if (data["rows"].length == 0){
        setWasError(true);
        setErrorText("Cannot find a record with that taskid/lsid.");
        setLoadingData(false);
        return;
      }

      data["rows"].forEach((row, i) => {

        newformdata.push({
          animalid: { value: row.Id, error: "" },
          date: { value: new Date(row.date), error: "" },
          weight: { value: row.weight, error: "" },
          remark: { value: row.remark || "", error: "" },
          animal_location: {value: "", error: ""},
          objectid: { value: row.objectid, error: "" },
          lsid: { value: row.lsid, error: "" },
          restraint_objectid: { value: row.restraint_objectid, error: "" },
          QCState: { value: row.QCState, error: "" },
          command: { value: "update", error: "" },
          collapsed: { value: i != 0, error: "" },
          visibility: { value: "visible", error: "" },
          restraint: {value: "", error: "", objectid: ""},
          validated: {value: false, error: ""},
          animal_info: {value: null, error: ""}
        });
        setTaskIdInAppContext(row.taskid);
        restraintobjectids.push(row.restraint_objectid);
      });
    }).then(()=> {

      //if theres no lsid/taskid, then we have to get the restraint record using weight objectid
      let filter: Array<any> = [];
      let restraintobjectidssplit: string = restraintobjectids.join(";")
      if (ActionURL.getParameter("taskid") == null){
        filter.push(
          Filter.create(
            "objectid",
            restraintobjectidssplit,
            Filter.Types.IN
          )
        )
      }

      let config: ConfigProps = {
        schemaName: "study",
        queryName: "restraints",
        filterArray: filter,
        columns: "objectid,restraintType"
      };

      labkeyActionSelectWithPromise(config).then((data)=> {
        //load up the restraints type into state
        newformdatarestraints = [...newformdata];
          for (var key of Object.keys(newformdatarestraints)) {
            for (var key2 of Object.keys(data["rows"])) {
              if (data["rows"][key2]["objectid"] == newformdatarestraints[key]["restraint_objectid"]["value"]){
                newformdatarestraints[key].restraint.objectid = data["rows"][key2]["objectid"];
                newformdatarestraints[key].restraint.value = data["rows"][key2]["restraintType"];
              }
          }
        }
      }).then(() => {
        setErrorLevel("submittable");
        setFormDataInAppContext((prevState: Array<RowObj>) => {
          return [...newformdatarestraints];
        });
        setLoadingData(false);
      }).catch((data) => {
        setWasError(true);
        setErrorText("There was an error loading the record.");
      });
    });
  }, []);

  //load up lookupdata
  useEffect( () => {
    let config:ConfigProps = {
      schemaName: "ehr_lookups",
      queryName: "restraint_type",
      filterArray: [Filter.create("type", "Table-Top", Filter.Types.EQUAL)]
    };
    labkeyActionSelectWithPromise(config).then((data) => {
      setRestraintsInAppContext(data.rows);
    })
    let config_fff:ConfigProps = {
      schemaName: "ehr",
      queryName: "form_framework_types",
      filterArray: [Filter.create("schemaName", "study", Filter.Types.EQUAL), Filter.create("queryName", "weight", Filter.Types.EQUAL) ]
    };
    labkeyActionSelectWithPromise(config_fff).then((data) => {
      setFormFrameworkTypesInAppContext(data.rows);
    })
  },[]);

  //attempting to set the focus to the id input after the 'next' button has been hit
  useEffect( () => {

    if (formdata[0] && formdata[0].cage){
      //console.log(groupCages(formdata))
      setCageDataInAppContext(groupCages(formdata));

    }
  },[formdata])

  //mutate the array
  const liftUpVal = (name: string, value: string | number | object | boolean, index: number) => {
    setFormDataInAppContext((prevState: Array<RowObj>) => {
      let newValues = [...prevState];
      newValues[index][name]["value"] = value;
      return [...newValues];
    });
  };
  const liftUpValidationState = (name: string, value: string | number | object | boolean, index: number) => {
    setFormDataInAppContext((prevState: Array<RowObj>) => {
      let newValues = [...prevState];
      newValues[index][name]["value"] = value;
      return [...newValues];
    });
  };

  const liftUpIndexState = (index: number) => {
    setCurrent(index);
    console.log(index)
  }

  const liftUpInfoState = (state: AnimalInfoStates) => {
    setInfoState(state);
  };

  const onSubmit = (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    e.preventDefault();
    if (formEl.current.checkValidity()) {
      handleShowModal(e);
    } else {
      formEl.current.reportValidity();
    }
  };

  const onSubmitForReview = (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    e.preventDefault();
    if (formEl.current.checkValidity()) {
      handleShowModal(e);
    } else {
      formEl.current.reportValidity();
    }
  };

  const onValidate = (): void => {
    let copyformdata: Array<RowObj> = [...formdata];
    copyformdata.forEach(item => {
      //don't validate against deleted / hidden items
      if (item["visibility"]["value"] != "hide-record"){
        if ((typeof item["weight"]["value"] == 'undefined' ||  item["weight"]["value"] == '') && item["animalid"]["value"] == ""){
          setErrorLevel("no-action")
        }
        if ((typeof item["weight"]["value"] == 'undefined' ||  item["weight"]["value"] == '' ) && item["animalid"]["value"] != ""){
          setErrorLevel("saveable")
        }
        if ((typeof item["weight"]["value"] != 'undefined' &&  item["weight"]["value"] != '') && item["animalid"]["value"] != ""){
          setErrorLevel("submittable")
        }
      }
    });

  };

  const changeActionToUpdate = (): void => {
    setFormDataInAppContext((prevState: Array<RowObj>) => {
      let copyformdata: Array<RowObj> = [...prevState];
      copyformdata.forEach(item => {
        if (item["command"]["value"] != "delete") {
          item["command"]["value"] = "update";
        }
      });
      return [...copyformdata]
    });
  };

  const onSave = (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    e.preventDefault();
    //to animate the popup
    setSaving(true);
    let command: CommandType = wasSaved || editMode ? "update" : "insert";
    //if we are in edit mode, just grab current QCState of a record...
    //TODO think about this qcstate implementation a bit more since the qc state gets saved "globally"
    // (see saveWeights function)...
    let QCState: string = "";
    if (editMode) {
      QCState = getQCStateByRowId(QCMap, formdata[0]["QCState"]["value"]);
    } else {
      QCState = "In Progress";
    }

    let itemsToInsert = groupCommands(formdata);
    let currentDate: string = dayjs(new Date()).format();
    let jsonData = setupJsonData(itemsToInsert, QCState, taskId, reviewer, currentDate, command);

    saveRowsDirect(jsonData).then(()=> {
      setWasSavedInAppContext(true);
      setSaving(false);
      //need to set each record to update
      changeActionToUpdate();
      window.scrollTo(0,0);
      setEndTimeInAppContext(new Date());
    }).catch(()=>{
      setWasError(true);
      setErrorText("Error during operation.");
      window.scrollTo(0,0);
    });

  };

  const wait = (time: number): Promise<any> => {
    return new Promise(resolve => {
      let countdown: number = time;
      setInterval(() => {
        setSubmitBoxText("Success! Redirecting in..." + countdown);
        countdown--;
        if (countdown == 0) {
          resolve();
        }
      }, 1000);
    });
  };

  const waitWithInterval = (time: number, interval: number): Promise<any> => {
    return new Promise(resolve => {
      let countdown: number = time;
      setInterval(() => {
        countdown--;
        if (countdown == 0) {
          resolve();
        }
      }, interval);
    });
  };

  const triggerSubmit = () => {
    let command: CommandType = wasSaved || editMode ? "update" : "insert";
    setSubmitBoxText("Submitting...");

    let itemsToInsert = groupCommands(formdata);
    let currentDate: string = dayjs(new Date()).format();
    let jsonData = setupJsonData(itemsToInsert, "Completed", taskId, reviewer, currentDate, command);

    saveRowsDirect(jsonData).then(()=> {
      //for now just on submit, need to revisit other submit buttons
      setEndTimeInAppContext(new Date());

      wait(2).then(() => {
        window.location.href = ActionURL.buildURL(
          "wnprc_ehr",
          "dataEntry.view",
          ActionURL.getContainer()
        );
      });
      submit();
    }).catch(e => {
      setSubmitBoxText(e.exception);
    });

  };

  useEffect(() => {
    setFormDataInAppContext((prevState: Array<RowObj>) => {
      let initialdata: Array<RowObj> = [];
      let restraintObjectId: string = Utils.generateUUID().toUpperCase();
      initialdata.push({
        animalid: { value: "", error: "" },
        date: { value: new Date(), error: "" },
        weight: { value: undefined, error: "" },
        remark: { value: "", error: "" },
        animal_location: {value: "", error: ""},
        objectid: { value: Utils.generateUUID().toUpperCase(), error: "" },
        lsid: { value: "", error: "" },
        restraint_objectid: { value: restraintObjectId, error: "" },
        QCState: {
          value: getQCStateByLabel(QCMap, "In Progress") || 2,
          error: ""
        },
        command: { value: "insert", error: "" },
        collapsed: { value: false, error: "" },
        visibility: { value: "visible", error: "" },
        restraint: {value: "", error: "", objectid: restraintObjectId },
        validated: {value: false, error: ""},
        animal_info: {value: null, error: ""}
      });
      return [...initialdata];
    });
    getQCStateMap().then(map => {
      setQCMap(map);
    });
  }, []);

  const setFormIds = () => {
    setFormDataInAppContext((prevState: Array<RowObj>) => {
      let copyformdata: Array<RowObj>;
      if (formdata[0] && formdata[0].animalid.value != "")
      {
        copyformdata = [...prevState]
      }
      else
      {
        copyformdata = []
      }
      ids.forEach((id, i) => {
        let restraintObjectId = Utils.generateUUID().toUpperCase();
        copyformdata.push({
          animalid: {value: id, error: ""},
          date: {value: new Date(), error: ""},
          weight: {value: undefined, error: ""},
          remark: {value: "", error: ""},
          animal_location: {value: "", error: ""},
          objectid: {
            value: Utils.generateUUID().toUpperCase(),
            error: ""
          },
          lsid: {value: "", error: ""},
          restraint_objectid: {value: restraintObjectId, error: ""},
          QCState: {
            value: getQCStateByLabel(QCMap, "In Progress") || 2,
            error: ""
          },
          command: {value: "insert", error: ""},
          collapsed: {value: false, error: ""},
          visibility: {value: "visible", error: ""},
          restraint: {value: "", error: "", objectid: restraintObjectId},
          validated: {value: false, error: ""},
          animal_info: {value: null, error: ""}
        });
      });
      return [...copyformdata];
    });
  };

  const toggleOpen = (index) => {
    return new Promise( (resolve, reject) => {
      setFormDataInAppContext((prevState: Array<RowObj>) => {
        let copyformdata: Array<RowObj>;
        copyformdata = [...prevState];
        copyformdata[index]["collapsed"]["value"] = false;
        return [...copyformdata];
      });
      resolve();
      //return true;
    })

  }

  const toggleClose = (index) => {
    setFormDataInAppContext((prevState: Array<RowObj>) => {
      let copyformdata: Array<RowObj>;
      copyformdata = [...prevState];
      copyformdata[index]["collapsed"]["value"] = true;
      return [...copyformdata];
    });
    return true;
  }

  const toggleCollapse = (i: number) => {
    setFormDataInAppContext((prevState: Array<RowObj>) => {
      let copyformdata: Array<RowObj>;
      copyformdata = [...prevState];
      copyformdata[i]["collapsed"]["value"] = copyformdata[i]["collapsed"]["value"] ? false : true;
      return [...copyformdata];
    });
    return true;
  };

  //what state is the toggleOpen dependent on?
  //dependent on the whichButton was clicked.

  const addRecord = (): RowObj => {
    let restraintObjectId: string = Utils.generateUUID().toUpperCase();
    return {
      animalid: { value: "", error: "" },
      date: { value: new Date(), error: "" },
      weight: { value: undefined, error: "" },
      remark: { value: "", error: "" },
      animal_location: {value: "", error: ""},
      objectid: { value: Utils.generateUUID().toUpperCase(), error: "" },
      lsid: { value: "", error: "" },
      restraint_objectid: {value: restraintObjectId, error: ""},
      QCState: {
        value: getQCStateByLabel(QCMap, "In Progress") || 2,
        error: ""
      },
      command: { value: "insert", error: "" },
      collapsed: { value: false, error: "" },
      visibility: { value: "visible", error: "" },
      restraint: {value: "", error: "", objectid: restraintObjectId},
      validated: {value: false, error: ""},
      animal_info: {value: null, error: ""}
    };
  };
  const removeRecord = (copyformdata, i) => {
    //the validity of this record is no longer valid, so set the error level to whatever it was
      //only need to do this part if we are in wasSaved or editMode, otherwise we can splice.
      if (wasSaved || editMode) {
        copyformdata[i]["command"]["value"] = "delete";
      }
      else {
        copyformdata.splice(i, 1);
      }

      return [...copyformdata];
  }

  const getRoomCage = (location: string) => {
    if (location.indexOf("-") == -1){
      return null;
    }
    let room = location.substring(0,location.indexOf("-"));
    let cage = location.substring(location.indexOf("-")+1);
    return [room, cage];
  }

  const triggerSubmitForReview = () => {
    let command: CommandType = wasSaved || editMode ? "update" : "insert";

    let itemsToInsert = groupCommands(formdata);
    let currentDate: string = dayjs(new Date()).format();
    let jsonData = setupJsonData(itemsToInsert, "In Progress", taskId, reviewer, currentDate, command);

    saveRowsDirect(jsonData)
      .then(data => {
        //TODO wait til this is done rather than sleep
        submit();
        setEndTimeInAppContext(new Date());
        wait(2).then(() => {
          window.location.href = ActionURL.buildURL(
            "wnprc_ehr",
            "dataEntry.view",
            ActionURL.getContainer()
          )
        });
      })
      .catch(e => {
        setSubmitBoxText(e);
      });
  };

  const triggerReviewer = (id: number): void => {
    setReviewer(id);
  };

  const onAlertClose = (): void => {
    setShowAlert(false);
  };

  //when this is called, just get the id and set which modal to show
  const handleShowModal = (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    let target = e.target as HTMLInputElement
    setShowModal(target.id);
  };

  const flipModalState = () => {
    setShowModal("none");
  };

  const triggerUpAnyErrors = e => {
    setErrorLevel(e);
  };

  return (
    <div className={`content-wrapper-body ${saving ? "saving" : ""}`}>
      {wasSaved && (
        <CustomAlert
          body="Saved!"
          variant="success"
          show={showAlert}
          onClose={onAlertClose}
          dismissable={true}
        />
      )}
      {wasError && (
        <CustomAlert
          body={errorText}
          variant="danger"
          show={showAlert}
          onClose={onAlertClose}
          dismissable={true}
        />
      )}
      <div className="col-xs-6 panel panel-portal panel-portal-left">
        <div className="panel-heading">
          <h3>Data Entry</h3>
        </div>
        <Button
          variant="primary"
          className="wnprc-secondary-btn"
          id="add-record"
          disabled={singleEditMode}
          onClick={() => {
            let newRecord = addRecord();
            console.log('adding record, setting data.')
            setFormDataInAppContext((prevState: Array<RowObj>) => {
              let copyformdata = [...prevState];
              copyformdata.unshift(newRecord);
              return [...copyformdata];

            });
            document.getElementById("animalid_0").focus();
          }}
        >
          Add record
        </Button>
        <Button
          variant="primary"
          className="wnprc-secondary-btn"
          id="add-batch"
          disabled={singleEditMode}
          onClick={handleShowModal}
        >
          Add Batch
        </Button>
        <Button
          variant="primary"
          className="wnprc-secondary-btn"
          id="edit-batch"
          onClick={handleShowModal}
        >
          Bulk Edit
        </Button>
        <Button
          variant="primary"
          className="wnprc-secondary-btn"
          id="delete-record"
          disabled={singleEditMode}
          onClick={() => {
            setFormDataInAppContext((prevState: Array<RowObj>) => {
              return [];
            });
            setCageDataInAppContext([]);
            setAnimalInfo(null);
            setInfoState("waiting");
          }}
        >
          Delete All
        </Button>
        <Button
          variant="primary"
          className="wnprc-secondary-btn"
          onClick={(d) => {
            for (let i = 0; i < formdata.length; i++) {
              toggleOpen(i);
            }
        }}>Expand All</Button>
        <Button
          variant="primary"
          className="wnprc-secondary-btn"
          onClick={(d) => {
            for (let i = 0; i < formdata.length; i++) {
              toggleClose(i);
            }
        }}>Collapse All</Button>
        {showModal == "submit-all-btn" && (
          <SubmitModal
            name="final"
            title="Submit All"
            submitAction={triggerSubmit}
            bodyText={submitBoxText}
            submitText="Submit final"
            enabled={true}
            flipState={flipModalState}
          />
        )}
        {showModal == "submit-review-btn" && (
          <SubmitForReviewModal
            action={triggerSubmitForReview}
            setreviewer={triggerReviewer}
            flipState={flipModalState}
          />
        )}
        {showModal == "edit-batch" && (
          <BulkEditModal
            liftUpBulkValues={updateValues}
            flipState={flipModalState}
            restraints={restraints}
          />
        )}
        {showModal == "add-batch" && (
          <BatchModal
            setLocation={triggerLocation}
            setIds={triggerIds}
            flipState={flipModalState}
          />
        )}
        <form className="weights-form" ref={formEl}>

          {locloading || loadingData ? (
            <Spinner text={"Loading..."} />
          ) : (
            <div>
              {formdata
                .filter(item => item.command.value != "delete")
                .map((item, i) => (
                  <div className={`row ${item.visibility.value}`} key={i}>
                    <div className="col-xs-10">
                      <div className="row card" key={i}>
                        <div
                          className="card-header"
                            aria-controls={`${formdata[i]["collapsed"]["value"]}-${i}`}
                            aria-expanded={formdata[i]["collapsed"]["value"]}
                            onClick={e => {
                              if (
                                e.currentTarget.className.localeCompare(
                                  "remove-record-button"
                                ) != 0
                              ) {
                                //toggleCollapse(item, i);
                                //setTestCollapse(!testCollapse)
                                toggleCollapse(i);
                              }
                              setAnimalId(item.animalid.value);
                            }}
                          >
                            <div className="card-title h4">
                              {item.animalid.value}
                              {item.animal_info.value && `, ${item.animal_info.value["Id/curLocation/room"]}-`}
                              {item.animal_info.value && `${item.animal_info.value["Id/curLocation/cage"]}`}
                              {item.animal_info.value && `, ${item.animal_info.value["Id/curLocation/cond"]}`}
                            </div>
                        </div>
                        <Collapse in={!formdata[i]["collapsed"]["value"]}>
                        <div id={`${formdata[i]["collapsed"]["value"]}-${i}`}
                        >
                          <div className="card-body">
                            <EnterWeightForm
                              key={i}
                              index={i}
                              animalid={item.animalid.value}
                              weight={item.weight.value}
                              date={item.date.value}
                              remark={item.remark.value}
                              restraint={item.restraint.value}
                              liftUpVal={liftUpVal}
                              infoState={liftUpInfoState}
                              liftUpErrorLevel={triggerUpAnyErrors}
                              liftUpValidation={liftUpValidationState}
                              liftUpCurrentIndex={liftUpIndexState}
                              getAnimalInfo={getAnimalInfo}
                            />
                          </div>
                        </div>
                        </Collapse>
                      </div>
                    </div>
                    <div className="col-xs-2">
                      <button
                        className="remove-record-button"
                        id={`remove-record-btn_${i}`}
                        type="button"
                        title="Remove Record"
                        aria-label="Close"
                        onClick={e => {
                          setFormDataInAppContext((prevState: Array<RowObj>) =>{
                            let copyformdata: Array<RowObj> = [...prevState];
                            copyformdata[i]["visibility"]["value"] =
                              "hide-record";
                            return copyformdata;
                          });
                          sleep(750).then(() => {
                            setFormDataInAppContext((prevState: Array<RowObj>) => {
                              let copyformdata: Array<RowObj> = [...prevState];
                              let recordRemoved = removeRecord(copyformdata, i)
                              return [...recordRemoved];
                            })
                            onValidate();
                          });
                        }}
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          width="48"
                          height="48"
                          viewBox="0 0 24 24"
                        >
                          <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" />
                        </svg>
                      </button>
                    </div>
                  </div>
                ))}
            </div>
          )}
          <div className="row">
            <button
              className={`btn btn-primary submit-btn ${saving ? "saving" : ""}`}
              id="submit-all-btn"
              onClick={e => onSubmit(e)}
              disabled={errorLevel !== "submittable"}
            >
              Submit all
            </button>
            <button
              className={`btn btn-primary submit-btn ${saving ? "saving" : ""}`}
              id="save-draft-btn"
              onClick={e => onSave(e)}
              disabled={errorLevel == "no-action"}
            >
              Save Draft
            </button>
            <button
              className={`btn btn-primary submit-btn ${saving ? "saving" : ""}`}
              id="submit-review-btn"
              onClick={e => onSubmitForReview(e)}
              disabled={errorLevel !== "submittable"}
            >
              Submit for Review
            </button>
            {errorLevel !== "submittable" &&
            <div>
              <span id="invalid-tooltip" data-tooltip={"Fill in missing information for boxes outlined in red."}>⚠️</span>
            </div>
            }
          </div>
        </form>
      </div>
      <div className="col-xs-5 animal-info-pane">
        <div className="row panel panel-portal ">
          <div className="panel-heading">
            <h3>Navigation</h3>
          </div>
        <div className="panel-body">
          <div className="cage-container">
            {cagedata && (Object.values(cagedata) as Array<any>).map((cage, cageIndex) => (
              <div className="cage-group" key={cageIndex}>
                {cage.map((animal, animalIndex) => (
                  <button onClick={(b) => {
                    for (let i = 0; i < formdata.length; i++){
                      if (animal.animalid == formdata[i].animalid){
                        toggleOpen(i);
                        toggleOpen(i).then((d)=> {
                          waitWithInterval(1,500).then(() => {

                            document.getElementById("weight_" + (i)).focus();
                          })
                        });
                      }
                    }
                  }} key={animalIndex}>{animal && animal.animalid && animal.animalid.value}</button>
                  ))}
              </div>
            ))}
          </div>

          <div>

          </div>

          </div>
        </div>

        <div className="row panel panel-portal panel-body">
          <div className="panel-heading">
            <h3>Animal Info</h3>
          </div>
            <AnimalInfoPane animalInfo={animalInfo} infoState={infoState} />
        </div>
      </div>
      <div className="clear"></div>
    </div>
  );
};

export default EnterWeightFormContainer;
