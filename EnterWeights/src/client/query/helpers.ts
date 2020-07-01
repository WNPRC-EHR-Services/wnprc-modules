import {
  labkeyActionSelectWithPromise,
} from "./actions";

//TODO implement this...
export const getSchemaMetaData = (schemaName: string, queryName: string) => {};

export const setupWeightValues = (values: any[], QCStateLabel: string, taskId: string) => {
  let valuesToInsert = [];
  for (let value of values){
    valuesToInsert.push({
      Id: value.animalid.value,
      QCStateLabel: QCStateLabel,
      date: value.date.value,
      weight: value.weight.value,
      remark: value.remark.value,
      taskid: taskId,
      objectid: value.objectid.value
      //lsid: value.lsid.value || ""
    })
  }
  return valuesToInsert;
};

export const setupTaskValues = (taskId: string, dueDate: object, assignedTo: number, QCStateLabel: string) => {
  return [{
      taskId: taskId,
      duedate: dueDate,
      assignedTo: assignedTo,
      category: "task",
      title: "Weight",
      formType: "Weight",
      QCStateLabel: QCStateLabel
    }];
};

export const setupRestraintValues = (values: any[], taskId: string) => {
  let restraintValsToInsert = [];
  for (let value of values){
    //probably still need objectid here
    restraintValsToInsert.push({
      Id: value.animalid.value,
      restraintType: value.restraint.value,
      taskid: taskId,
      objectid: value.restraint.objectid,
      weight_objectid: value.objectid.value,
      date: value.date.value
    })
  }
  return restraintValsToInsert;
};

export const groupCommands = (values) => {
  return values.reduce((acc, item) => {
    if (!acc[item.command.value]) {
      acc[item.command.value] = [];
    }

    acc[item.command.value].push(item);
    return acc;
  }, {});
};

export const enteredWeightIsGreaterThanPrevWeight = (
  weight: number,
  prevWeight: number,
  percentChange: number
) => {
  return weight > prevWeight * (percentChange / 100) + prevWeight;
};

export const enteredWeightIsLessThanPrevWeight = (
  weight: number,
  prevWeight: number,
  percentChange: number
) => {
  return weight < prevWeight - (percentChange / 100) * prevWeight;
};

//given a set of ids, checks to see if all are unique
export const checkUniqueIds = (ids: Array<string>) => {
  let uniqueIds = [...new Set(ids)];
  return uniqueIds.length < ids.length ? false : true;
};

export const getlocations = (location:Array<any>): Array<Promise<any>> => {
  if (location.length == 0) {
    //alert('please set a location')
    return;
  }
  let promises = [];
  location.forEach(loc => {
    let config = {
      schemaName: "study",
      queryName: "demographicsCurLocation",
      columns: ["Id,location"],
      sort: "Id",
      filterArray: [
        LABKEY.Filter.create(
          "location",
          loc.value,
          LABKEY.Filter.Types.CONTAINS
        )
      ]
    };
    promises.push(labkeyActionSelectWithPromise(config));
  });

  return promises;

};

export const getQCStateByRowId = (id: number) => {};

export const generateUUID = () => {
  return LABKEY.Utils.generateUUID();
};