import * as React from "react";
import DatePicker from "react-datepicker";
import "../../../theme/css/react-datepicker.css";
import "../../../theme/css/index.css";
import "../../../theme/css/tooltip.css";
import {
  enteredWeightIsGreaterThanPrevWeight,
  enteredWeightIsLessThanPrevWeight
} from "../../query/helpers";
import { useEffect, useState } from "react";
import { labkeyActionSelectWithPromise } from "../../query/actions";
import { useRef, useContext } from "react";
import DateInput from "../../../components/DateInput";
import { AppContext } from "../App/ContextProvider";
import DropdownOptions from "../../components/DropdownOptions";
import {Filter, ActionURL} from "@labkey/api"
import {AnimalInfoStates, AnimalInfoProps, ConfigProps, FormErrorLevels} from "../../../typings/main";
import {RowObj, WeightFormProps} from "../../typings/main";

/**
 * Main modal which holds the values for the fields in the weight dataset,
 * and the restraints  dataset.
 */

const EnterWeightForm: React.FunctionComponent<WeightFormProps> = props => {
  const {
    animalid,
    weight,
    date,
    remark,
    restraint,
    index,
    infoState,
    liftUpVal,
    liftUpErrorLevel,
    liftUpValidation,
    liftUpCurrentIndex,
    getAnimalInfo
  } = props;
  const [prevweight, setPrevWeight] = useState<number>(null);
  const [animalInfo, setAnimalInfo] = useState<AnimalInfoProps>(null);
  const [weightWarning, setWeightWarning] = useState<string>("");
  const [animalError, setAnimalError] = useState<string>("");
  const [weightError, setWeightError] = useState<string>("");
  const [anyErrors, setAnyErrors] = useState<boolean>(true);
  const [errorLevel, setErrorLevel] = useState<FormErrorLevels>("no-action");
  const [animalInfoState, setAnimalInfoState] = useState<AnimalInfoStates>("waiting");

  const { submit, submitted, setRestraintsInAppContext, restraints, setEndTimeInAppContext, setStartTimeInAppContext, setFormFrameworkTypesInAppContext, wasSaved, isRecording, setIsRecordingInAppContext, setAnyErrorsEverInAppContext, formdata, setFormDataInAppContext } = useContext(
    AppContext
  );

  useEffect(() => {
    liftUpErrorLevel(errorLevel);
  }, []);

  //always fire this once
  useEffect(() => {
    infoState(animalInfoState);
  }, []);

  useEffect(() => {
    //validateItems();
  },[]);

  useEffect(() => {
    infoState(animalInfoState);
  }, [animalInfoState]);

  useEffect(() => {
    /*let copyformdata: Array<RowObj> = [...formdata];
    copyformdata[index].animal_info.value = animalInfo;*/
    //liftUpAnimalInfo(animalInfo, index);

  }, [animalInfo]);

  useEffect(() => {
    liftUpErrorLevel(errorLevel);
  }, [errorLevel]);

  useEffect(() => {
    liftUpValidation("validated", !anyErrors, index);
  },[anyErrors]);

  /*setPrevWeight(data["rows"][0]["Id/MostRecentWeight/MostRecentWeight"]);
  validateItems("animalid", animalid);
  setAnimalError("");*/
  //TODO propagate up animal not found issue?
  //setInfoState("loading-unsuccess");
  //TODO
  /*setAnimalError("Animal Not Found");
  validateItems("animalid", animalid)*/

  //validate items to set error levels which determine which buttons are disabled
  const validateItems = (name: string, value: string | number | object) => {
    if (value == "" && name == "animalid") {
      setAnyErrors(true);
      setAnyErrorsEverInAppContext();
      setErrorLevel("no-action");
      return;
    }
    if (value == "" && name == "weight" && animalError == "") {
      setAnyErrors(true);
      setAnyErrorsEverInAppContext();
      setErrorLevel("saveable");
      return;
    }
    if (value != "" && name == "weight" && animalError == "") {
      setAnyErrors(false);
      setErrorLevel("submittable");
      return;
    }
  };



  const startSessionTimer = () => {
    if (isRecording)
      return;
    setIsRecordingInAppContext(true);
    setStartTimeInAppContext(new Date());
  }

  const handleChange = (e: any) => {
    let target = e.target as HTMLInputElement;
    liftUpVal(target.name, target.value, index);
    startSessionTimer();
    validateItems(e.target.name, e.target.value);
  };

  const handleRestraintChange = (val: number) => {
    liftUpVal("restraint", val, index);
    startSessionTimer();
  };

  const handleDateChange = (date: object) => {
    liftUpVal("date", date, index);
    startSessionTimer();
  };

  const handleRawDateChange = (e: any) => {
    if (e.currentTarget.value instanceof Date && !isNaN(e.currentTarget.value)){
      liftUpVal("date", new Date(e.currentTarget.value), index);
      startSessionTimer();
    }
  };


  const checkWeights = (e: React.FormEvent<HTMLInputElement>) => {
    let target = e.target as HTMLInputElement;
    if (enteredWeightIsGreaterThanPrevWeight(weight, prevweight, 10)) {
      setWeightWarning("Weight is greater than 10% of previous weight.");
    } else if (enteredWeightIsLessThanPrevWeight(weight, prevweight, 10)) {
      setWeightWarning("Weight is less than 10% of previous weight.");
    } else {
      setWeightWarning("");
    }

    if (target.value.length == 0) {
      setWeightError("Required");
    } else {
      setWeightError("");
      setErrorLevel("submittable");
    }
  };

  let calendarEl = useRef(null);

  const openDatepicker = () => {
    //@ts-ignore
    calendarEl.setOpen(true);
  };
  return (
    <div>
      <div className="row">
        <div className="col-xs-2">
          <label htmlFor="animalid">Id:</label>
        </div>
        <div className="col-xs-9">
          <input
            type="text"
            name="animalid"
            id={`animalid_${index}`}
            className="form-control"
            value={animalid.toLowerCase()}
            onChange={handleChange}
            onBlur={(e) => {
              //validate some shit, which should happen in handleChange
              getAnimalInfo(index, animalid);
              liftUpCurrentIndex(index)
            }}
            onFocus={(e) => {
              //validate some shit, which should happen in handleChange
              getAnimalInfo(index, animalid);
              liftUpCurrentIndex(index)
            }}
            required
          />
          {formdata[index].animal_info.error != "" && <span data-tooltip={formdata[index].animal_info.error}>❗</span>}
        </div>
      </div>
      <div className="row">
        <div className="col-xs-2">
          <label htmlFor="weight">Weight: </label>
        </div>
        <div className="col-xs-9">
          <input
            type="number"
            step="any"
            name="weight"
            id={`weight_${index}`}
            className="form-control"
            value={weight || ""}
            onChange={handleChange}
            onFocus={() => {
              //validate some shit, which should happen in handleChange
              getAnimalInfo(index, animalid);
              liftUpCurrentIndex(index)
            }}
            onBlur={e => {
              checkWeights(e);
            }}
            required
          />
          {weightWarning && (
            <span data-tooltip={weightWarning} id="weight-warning">
              ⚠️
            </span>
          )}
          {weightError && <span data-tooltip={weightError}>❗</span>}
        </div>
      </div>
      <div className="row">
        <div className="col-xs-2">
          <label htmlFor="date">Date: </label>
        </div>
        <div className="col-xs-9">
          <DatePicker
            ref={r => (calendarEl = r)}
            showTimeSelect
            onChangeRaw={handleRawDateChange}
            dateFormat="yyyy-MM-dd HH:mm"
            todayButton="Today"
            selected={date}
            className="form-control"
            name="date"
            id={`date_${index}`}
            onChange={handleDateChange}
            onFocus={() => {
              getAnimalInfo(index, animalid);
              liftUpCurrentIndex(index)
              }
            }
            customInput={<DateInput
              opendate={openDatepicker}
              iconpath={`${ActionURL.getContextPath()}/wnprc_ehr/static/images/icons8-calendar-24.png`}
              />}
          />
        </div>
      </div>
      <div className="row">
        <div className="col-xs-2">
          <label htmlFor="restraints">Restraint: </label>
        </div>
        <div className="col-xs-9">
          <DropdownOptions
            options={restraints}
            initialvalue={restraint}
            value={handleRestraintChange}
            name="restraints"
            id={`restraint_${index}`}
            classname="form-control"
            valuekey="type"
            displaykey="type"
          />
        </div>
      </div>
      <div className="row">
        <div className="col-xs-2">
          <label htmlFor="remark">Remark: </label>
        </div>
        <div className="col-xs-9">
          <textarea
            name="remark"
            id={`remark_${index}`}
            className="form-control"
            rows={3}
            value={remark}
            onChange={handleChange}
            onFocus={() => {
              getAnimalInfo(index, animalid);
              liftUpCurrentIndex(index)
              }
            }
          />
        </div>
      </div>
    </div>
  );
};

export default EnterWeightForm;
