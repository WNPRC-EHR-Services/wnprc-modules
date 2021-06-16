import * as React from "react";
import { Table } from "react-bootstrap";
import Spinner from "../../components/Spinner";
import {PaneProps} from "../typings/main";


/**
 * Displays animal info given a successful state.
 */
const AnimalInfoPane: React.FunctionComponent<PaneProps> = props => {
  const { animalInfo, infoState } = props;

  if (infoState == "waiting") {
    return <div id="animal-info-empty">Select a record.</div>;
  }

  if (infoState == "loading-unsuccess") {
    return <div id="animal-info-empty">Animal not found.</div>;
  }

  if (infoState == "loading") {
    return (
      <div id="animal-info-empty">
        <Spinner text={"Loading..."} />
      </div>
    );
  }

  if (infoState == "loading-success") {
    return (
      <div>
        <Table responsive="sm" className="animal-info-table">
          <tbody>
            <tr>
              <td>Id</td>
              <td>
                <a href={animalInfo._labkeyurl_Id} target={"_blank"}>{animalInfo.Id}</a>
              </td>
            </tr>
            <tr>
              <td>Gender</td>
              <td>
                <a href={animalInfo._labkeyurl_gender} target={"_blank"}>{animalInfo.gender}</a>
              </td>
            </tr>
            <tr>
              <td>Current Weight</td>
              <td>
                <a
                  href={
                    animalInfo[
                      "_labkeyurl_Id/MostRecentWeight/MostRecentWeight"
                      ]
                  }
                  target={"_blank"}
                >
                  {animalInfo["Id/MostRecentWeight/MostRecentWeight"]}
                </a>
              </td>
            </tr>
            <tr>
              <td>Weight Date</td>
              <td>
                <a
                  href={
                    animalInfo[
                      "_labkeyurl_Id/MostRecentWeight/MostRecentWeightDate"
                      ]
                  }
                  target={"_blank"}
                >
                  {animalInfo["Id/MostRecentWeight/MostRecentWeightDate"]}
                </a>
              </td>
            </tr>
            <tr>
              <td>Medical</td>
              <td>
                {animalInfo.medical}
              </td>
            </tr>
            <tr>
              <td>Dam</td>
              <td>
                <a href={animalInfo._labkeyurl_dam} target={"_blank"}>{animalInfo.dam}</a>
              </td>
            </tr>
            <tr>
              <td>Age</td>
              <td>
                {animalInfo["Id/age/AgeFriendly"]}
              </td>
            </tr>
            <tr>
              <td>Current Behavior(s)</td>
              <td>
                <a
                  href={
                    animalInfo[
                      "_labkeyurl_Id/CurrentBehavior/currentBehaviors"
                      ]
                  }
                  target={"_blank"}
                >
                  {animalInfo["Id/CurrentBehavior/currentBehaviors"]}
                </a>
              </td>
            </tr>
          </tbody>
        </Table>
      </div>
    );
  }
};

export default AnimalInfoPane;
