import {URLForAction} from "../../../build/generated-ts/GeneratedFromJava";
declare const LABKEY: {
    CSRF: string,
    ActionURL: any,
    getModuleContext(moduleName: string): any;
};

export function getCSRF(): string {
    return LABKEY.CSRF || "";
}

export function getCurrentController(): string {
    return LABKEY.ActionURL.getController();
}

export function getCurrentContainer(): string {
    return LABKEY.ActionURL.getContainer();
}

export function getBaseURL(): string {
    return LABKEY.ActionURL.getBaseURL();
}

export function urlFromAction(actionURL: URLForAction, params?: {[name: string]: string}): string {
    if (!params) {
        return buildURL(actionURL.controller, actionURL.actionName, getCurrentContainer());
    }
    else {
        return buildURLWithParams(actionURL.controller, actionURL.actionName, getCurrentContainer(), params);
    }
}

export function buildURL(controller: string, action: string, container: string): string {
    return LABKEY.ActionURL.buildURL(controller, action, container);
}

export function buildURLWithParams(controller: string, action: string, container: string, queryParams: {[name: string]: string}): string {
    return LABKEY.ActionURL.buildURL(controller, action, container, queryParams);
}

declare const __WebUtilsPageLoadData: any;

export function getPageLoadData(): any {
    return __WebUtilsPageLoadData;
}

export function getModuleContext(moduleName: string): any {
    return LABKEY.getModuleContext(moduleName) || {};
}

export function getLinkToAnimal(id: string): string {
    return LABKEY.ActionURL.buildURL('ehr', 'participantView', null, {participantId: id});
}