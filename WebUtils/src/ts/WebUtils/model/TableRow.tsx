import * as _ from "underscore";
import * as React from "react";
import { v4 as uuidv4 } from "uuid";

export interface HTMLTableColumn {
    getHTML(): string;
    getValue(): string;
}

export function isHTMLTableColumn(variable: TableColumn): variable is HTMLTableColumn {
    return _.isFunction((variable as HTMLTableColumn).getHTML);
}

export interface ReactTableColumn {
    getReactElement(): JSX.Element;
    getValue(): string;
}

export class SimpleStringColumn implements ReactTableColumn {
    constructor(public value: string) {

    }

    getReactElement(): JSX.Element {
        return <span>{this.value}</span>;
    }

    getValue(): string {
        return this.value;
    }
}

export class SimpleLinkColumn implements ReactTableColumn {
    constructor(public display: string, public address: string) {}

    getReactElement(): JSX.Element {
        return (
            <span>
                <a href={this.address}>{this.display}</a>
            </span>
        )
    }

    getValue(): string {
        return this.display;
    }
}

export type TableColumn = HTMLTableColumn | ReactTableColumn

export interface TableRowConfig {
    columns: TableColumn[],
    otherData?: any,
    warn?: KnockoutObservable<boolean> | boolean,
    err?:  KnockoutObservable<boolean> | boolean
}

export class TableRow {
    isSelected: KnockoutObservable<boolean> = ko.observable(false);
    isEven:     KnockoutObservable<boolean> = ko.observable(false);
    isHidden:   KnockoutObservable<boolean> = ko.observable(false);

    key: string = uuidv4();

    warn: KnockoutComputed<boolean>;
    err:  KnockoutComputed<boolean>;

    columns: TableColumn[];
    otherData: any = {};

    constructor(config: TableRowConfig) {
        this.warn = ko.computed(() => {
            return (ko.isObservable(config.warn)) ? config.warn() : (_.isBoolean(config.warn)) ? config.warn : false;
        });

        this.err = ko.computed(() => {
            return (ko.isObservable(config.err)) ? config.err() : (_.isBoolean(config.err)) ? config.err : false;
        });

        this.columns = config.columns;

        if (config.otherData) {
            this.otherData = config.otherData;
        }
    }
}