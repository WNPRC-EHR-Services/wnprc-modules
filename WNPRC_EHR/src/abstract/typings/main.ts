export interface ContextProps {
  animalInfo: AnimalInfoProps;
  setAnimalInfoExternal: (animalInfo: AnimalInfoProps) => void;
  animalInfoState: AnimalInfoStates;
  setAnimalInfoStateExternal: (string) => void;
  updateAnimalInfoCacheExternal: (animalInfo: AnimalInfoProps) => void;
  animalInfoCache: object;
}

export interface AnimalInfoProps {
  Id: string;
  _labkeyurl_Id: string;
  calculated_status: string;
  _labkeyurl_calculated_status: string;
  gender: string;
  _labkeyurl_genderd: string;
  dam: string;
  _labkeyurl_dam: string;
  birth: string;
  _labkeyurl_birth: string;
}

export type AnimalInfoStates =
    | "waiting"
    | "loading"
    | "loading-unsuccess"
    | "loading-success";
