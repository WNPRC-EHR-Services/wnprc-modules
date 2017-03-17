import * as api from "WebUtils/API";
import * as React from "react";
import * as ReactDOM from "react-dom";
import * as $ from 'jquery';
import * as moment from 'moment';
import {YearSelector} from "../lib/DatePicker";
import {Table} from "../../../lkpm/modules/WebUtils/src/ts/WebUtils/model/Table";
import {FilterableTable} from "../../../lkpm/modules/WebUtils/src/ts/WebUtils/component/lk-table";
import {
    TableRow, ReactTableColumn,
    SimpleStringColumn, SimpleLinkColumn
} from "../../../lkpm/modules/WebUtils/src/ts/WebUtils/model/TableRow";
import * as s from "underscore.string";
import * as _ from "underscore";
import Moment = moment.Moment;
import {
    getLinkToAnimal, buildURLWithParams,
    getCurrentContainer
} from "../../../lkpm/modules/WebUtils/src/ts/WebUtils/LabKey";



let values = new Table({rows: []});
values.rowHeaders(['Order', 'Date', "Type", "Case Number", "Animal Id", "Status", "View Report"]);

interface PathCase {
    status:   string;
    date:     Moment;
    type:     PathCaseType;
    caseno:   string;
    order:    number | null;
    taskid:   string;
    animalid: string;
}

let getOrder = function(caseno: string): number | null{
    let order: null | number = null;

    if (_.isString(caseno)) {
        let matches = caseno.match(/\d+[a-zA-Z](\d+)/);

        if (_.isArray(matches)) {
            order = parseInt(matches[1]);
        }
    }

    return (_.isNull(order) || _.isNaN(order)) ? null : order;
};

let getCaseURL = function(animalid: string, query: string): string {
    return buildURLWithParams('query', 'executeQuery', getCurrentContainer(), {
        schemaName: 'study',
        'query.queryName': query,
        'query.Id~in': animalid
    });
};

let getTaskURL = function(taskid: string, type: string): string {
    return buildURLWithParams('ehr', 'taskDetails', getCurrentContainer(), {
        formtype: type,
        taskid:   taskid
    });
};

interface URLData {
    controller: string,
    action: string
}

let makeRow = function(pathCase: PathCase): TableRow {
    let linksColumn: ReactTableColumn = {
        getReactElement() {
            if (pathCase.type != "Necropsy") {
                return (<span></span>);
            }

            let necropsyReport = pageLoadData.urlData.necropsyReport as URLData;
            let collectionList = pageLoadData.urlData.collectionList as URLData;

            let reportURL = buildURLWithParams(necropsyReport.controller, necropsyReport.action, getCurrentContainer(), {
                taskid: pathCase.taskid,
                reportMode: "true"
            });

            let collectionListURL = buildURLWithParams(collectionList.controller, collectionList.action, getCurrentContainer(), {
                taskid: pathCase.taskid,
                reportMode: "true"
            });

            return (
                <div>
                    <a href={reportURL}>View Report</a>
                    <br/>
                    <a href={collectionListURL}>View Collection List</a>
                </div>
            );
        },

        getValue(): string {
            return "";
        }
    };

    let type = pathCase.type;
    let typeColumn: ReactTableColumn = {
        getReactElement(): JSX.Element {
            if (s.isBlank(pathCase.taskid)) {
                let query = (type == 'Necropsy') ? "Necropsies" : "Biopsies";
                return (
                    <div>
                        <a href={getCaseURL(pathCase.animalid, query)}>{type}</a>
                        <p style={{color: 'red'}}>! No Task Assigned to Record</p>
                    </div>
                );
            }
            else {
                return (
                    <div>
                        <a href={getTaskURL(pathCase.taskid, type)}>{type}</a>
                    </div>
                )
            }
        },

        getValue(): string {
            return pathCase.type;
        }
    };

    return new TableRow({
        columns: [
            {
                getHTML: (): string => {
                    return (pathCase.order == null) ? '' : pathCase.order.toString();
                },
                getValue: (): string => {
                    return s.lpad(this.getHTML(), 10, '0');
                }
            },
            new SimpleStringColumn(pathCase.date.format('YYYY/MM/DD HH:mm')),
            typeColumn,
            new SimpleStringColumn(pathCase.caseno),
            new SimpleLinkColumn(pathCase.animalid, getLinkToAnimal(pathCase.animalid)),
            new SimpleStringColumn(pathCase.status),
            linksColumn
        ],
        otherData: pathCase
    })
};

type PathCaseType = "Biopsy" | "Necropsy";

let parsePathCase = function(object: any, type: PathCaseType): PathCase {
    console.log(object);
    return {
        status:   object['QCState/label'] || object['QCState/label'.toLowerCase().replace(/\//, "_fs_")],
        date:     api.parseDateFromDB(object['date']),
        type:     type,
        caseno:   object['caseno'],
        order:    getOrder(object['caseno']),
        taskid:   _.isString(object['taskid']) ? object['taskid'] : '',
        animalid: object['Id'] || object['id']
    }
};

let pageLoadData: any = (window as any)['PageLoadData'];
let cases = [].concat(
    pageLoadData.biopsies.map((row: any) => {return parsePathCase(row, "Biopsy"); }),
    pageLoadData.necropsies.map((row: any) => {return parsePathCase(row, "Necropsy"); })
);

values.rows(_.sortBy(cases, (row: PathCase) => {
    return (row.order == null) ? -1000 : -row.order;
}).map(makeRow));

export class PathCaseList extends React.Component<{}, {}> {
    render() {
        return (
        <div className="panel panel-primary">
            <div className="panel-heading"><span className="panel-title">List of Biopsy and Necropsy Cases</span></div>

            <div className="panel-body">
                <form className="form-horizontal">
                    <div className="form-group">
                        <label className="col-xs-3 control-label">Year: </label>
                            <div className="col-xs-9">
                                <YearSelector/>
                            </div>
                    </div>
                </form>

                <div className="row">
                    <FilterableTable table={values} />
                </div>
            </div>
        </div>
        )
    }
}

export class Page extends React.Component<any, any> {
    render() {
        return <PathCaseList />;
    }
}

ReactDOM.render(
    <Page />,
    $("#react-page").get(0)
);