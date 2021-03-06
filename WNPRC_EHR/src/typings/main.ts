export interface ConfigProps {
    schemaName: string;
    queryName: string;
    columns?: any;
    sort?: string;
    containerPath?: string;
    filterArray?: Array<any>;
}

export type AnimalInfoStates =
  | "waiting"
  | "loading"
  | "loading-unsuccess"
  | "loading-success";


export interface AnimalInfoProps {
    Id: string;
    _labkeyurl_Id: string;
    calculated_status: string;
    _labkeyurl_calculated_status: string;
    gender: string;
    _labkeyurl_gender: string;
    dam: string;
    _labkeyurl_dam: string;
    birth: string;
    _labkeyurl_birth: string;
    age: string;
    avail:string
    sire: string;
    _labkeyurl_sire: string;
    hold: string;
    death: string;
    _labkeyurl_death: string;
    medical: string;
    _labkeyurl_medical: string;
    origin: string;
    _labkeyurl_origin: string;
}

export type FormErrorLevels = "no-action" | "saveable" | "submittable";
