<%@ page import="org.labkey.dbutils.api.SimpleQueryFactory" %>
<%@ page import="org.labkey.dbutils.api.SimpleQuery" %>
<%@ page import="org.labkey.webutils.api.json.JsonUtils" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="java.util.List" %>
<%@ page import="org.json.JSONArray" %>
<%@ page import="java.util.UUID" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Comparator" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="org.labkey.api.view.ActionURL" %>
<%@ page import="org.labkey.wnprc_ehr.WNPRC_EHRController" %>
<%@ page import="org.labkey.api.security.Group" %>
<%@ page import="org.labkey.api.security.GroupManager" %>
<%@ page import="org.labkey.security.xml.GroupEnumType" %>
<%@ page extends="org.labkey.api.jsp.JspBase" %>

<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/fullcalendar/3.9.0/fullcalendar.min.css' />
<script src='https://cdnjs.cloudflare.com/ajax/libs/fullcalendar/3.9.0/fullcalendar.min.js'></script>
<script src= 'https://unpkg.com/popper.js/dist/umd/popper.min.js'></script>
<script src= 'https://unpkg.com/tooltip.js/dist/umd/tooltip.min.js'></script>

<%
    SimpleQueryFactory queryFactory = new SimpleQueryFactory(getUser(), getContainer());

    SimpleQuery assignedToOptions = queryFactory.makeQuery("ehr_lookups", "husbandry_assigned");
    List<JSONObject> assignedToList= JsonUtils.getSortedListFromJSONArray(assignedToOptions.getResults().getJSONArray("rows"),"title");

    //TODO: Query WaterCoalesce for all future water for the next 60 days
    SimpleQuery futureWaters = queryFactory.makeQuery("study", "waterScheduleCoalesced");
    List<JSONObject> waterList = JsonUtils.getListFromJSONArray(futureWaters.getResults().getJSONArray("rows"));

%>

<style type="text/css">
    /* Full Calendar heading */
    #calendar .fc-toolbar h1 {
        font-size: 20px;
        margin: 0;
    }
    #rapperDiv {
        max-width: 25%;
        float: left;
    }

    .toggle-check-input {
        width: 1px;
        height: 1px;
        position: absolute;
    }

    .toggle-check-text {
        display: inline-block;
        position: relative;
        text-transform: uppercase;
        background: #CCC;
        padding: 0.25em 0.5em 0.25em 2em;
        border-radius: 1em;
        min-width: 2em;
        color: #FFF;
        cursor: pointer;
        transition: background-color 0.15s;
    }

    .toggle-check-text:after {
        content: ' ';
        display: block;
        background: #FFF;
        width: 1.1em;
        height: 1.1em;
        border-radius: 1em;
        position: absolute;
        left: 0.3em;
        top: 0.25em;
        transition: left 0.15s, margin-left 0.15s;
    }

    .toggle-check-text:before {
        content: 'No';
    }

    .toggle-check-input:checked ~ .toggle-check-text {
        background: #333;
        padding-left: 0.5em;
        padding-right: 2em;
    }

    .toggle-check-input:checked ~ .toggle-check-text:before {
        content: 'Yes';
    }

    .toggle-check-input:checked ~ .toggle-check-text:after {
        left: 100%;
        margin-left: -1.4em;
    }
</style>

<%--<div class="col-xs-12 col-xl-8">--%>
<div class="row" >
    <div class="col-md-4">

        <div class="row">
            <div class="panel panel-primary">
                <div class="panel-heading"><span>Water Details</span></div>
                <div class="panel-body" id="waterInfoPanel" data-bind="with: taskDetails">


                    <dl class="dl-horizontal">
                        <dt>DataSource:         </dt> <dd>{{dataSource}}</dd>
                        <dt>Task ID:            </dt> <dd>{{taskid}}</dd>
                        <dt>Animal ID:          </dt> <dd><a href="{{animalLink}}">{{animalId}}</a></dd>
                        <dt>Assigned to:        </dt> <dd>{{assignedToCoalesced}}</dd>
                        <dt>Volume:             </dt> <dd>{{volumeCoalesced}}</dd>
                        <dt>Project (Account):  </dt> <dd>{{project}}</dd>
                        <dt>Date:               </dt> <dd>{{displayDate}}</dd>
                    </dl>

                    <button class="btn btn-default" data-bind="click: $root.requestTableClickAction" data-toggle="collapse" data-target="#waterExceptionPanel"
                                          id="waterInfo" params="" disabled>Enter Water Exception</button>

                    <button class="btn btn-default"  data-toggle="modal" data-target="#myModal"
                            id="enterWaterOrder" params="" disabled>Modify Water Order</button>

                    <div class="modal fade" id="myModal" role="dialog">
                        <div class="modal-dialog modal-lg">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                                    <h4 class="modal-title">Modify Water Order</h4>
                                </div>
                                <div class="modal-body">
                                    <p>Select an option from this window:</p>
                                    <p>End Date for Water Order: <b>{{date}}</b></p>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal" data-bind="click: $root.endWaterOrder">End Water Order</button>
                                    <button type="button" class="btn btn-default" data-dismiss="modal" data-bind="click: $root.enterNewWaterOrder">End and Star New Water Order</button>
                                    <button type="button" class="btn btn-default" data-dismiss="modal">Close Window</button>
                                </div>
                            </div>
                        </div>
                    </div>



                </div>
            </div>
        </div>

        <div class="row">
            <div class="collapse" id="waterExceptionPanel">

                <div class="panel panel-primary">
                    <div class="panel-heading"><span>Enter Water Exception</span></div>
                    <div class="panel-body" id="waterException" data-bind="with: taskDetails">
                        <!-- ko if: lsid() == '' -->
                        <p style="text-align: center">
                            <em>Please click on a pending request to schedule it.</em>
                        </p>
                        <!-- /ko -->

                        <form class="form-horizontal scheduleForm">
                            <!-- ko if: lsid() != '' -->
                            <div class="form-group">
                                <label class="col-xs-4 control-label">Animal ID</label>
                                <div class="col-xs-8">
                                    <p class="form-control-static">{{animalId}}</p>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-4 control-label">Date</label>
                                <div class="col-xs-8">
                                    <p class="form-control-static" type="date"> {{date}} </p>

                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-4 control-label">Volume</label>
                                <div class="col-xs-8">
                                    <%--<input type="hidden" class="hidden-assignedTo-field" data-bind="value: {{volumeCoalesced}}">--%>
                                    <input type="text" class="form-control" data-bind="value: volumeCoalesced"/>
                                    <%--<p class="form-control-static">{{volumeCoalesced}}</p>--%>
                                </div>

                            </div>
                            <!-- /ko -->

                            <div class="form-group">
                                <label class="col-xs-4 control-label">Project</label>
                                <div class="col-xs-8">
                                    <p class="form-control-static">{{project}}</p>

                                </div>
                            </div>


                            <div class="form-group">
                                <label class="col-xs-4 control-label">Assigned To</label>
                                <div class="col-xs-8">
                                    <select data-bind="value: assignedTo" class="form-control">
                                        <option value="">{{assignedToCoalesced}}</option>
                                        <%
                                            for(JSONObject assignedTo : assignedToList) {
                                                String value = assignedTo.getString("value");
                                                String title = assignedTo.getString("title");
                                        %>
                                        <option value="<%=value%>"><%=h(title)%></option>
                                        <%
                                            }
                                        %>
                                    </select>

                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-xs-4 control-label">Edit Multiple:</div>
                                <div class="col-xs-2 control-label">
                                    <label class="toggle-check">
                                        <input type="checkbox" class="toggle-check-input" data-bind="checked: $root.wantsSpam"  />
                                        <span class="toggle-check-text"></span>
                                    </label>
                                </div>
                            </div>

                            <div style="text-align: right;">
                                <button class="btn btn-default" data-bind="click: $root.clearForm">Cancel</button>
                                <button class="btn btn-primary" data-bind="click: $root.submitForm">Insert Water Exception</button>
                            </div>
                        </form>

                    </div>

                </div>
            </div>
        </div>
    </div>

    <div class="col-xs-12 col-md-8">
        <div class="panel panel-primary">
            <div class="panel-heading"><span>Calendar</span></div>
            <div class="panel-body">
                <div id="calendar"></div>


            </div>
        </div>
    </div>

</div>







<script>
    (function() {


        var $calendar = $('#calendar');
        $(document).ready(function () {
            $calendar.fullCalendar({
                header: {
                    left: 'prev,next today',
                    center: 'title',
                    right: 'month,basicWeek,basicDay'
                },
                eventSources:[
                    {events: function (startMoment, endMoment, timezone, callback) {
                        var date = new Date();
                        date.setDate(date.getDate()-60);

                        WebUtils.API.selectRows("study", "waterScheduleCoalesced", {
                            "date~gte": startMoment.format('Y-MM-DD'),
                            "date~lte": endMoment.format('Y-MM-DD'),
                            "parameters": {NumDays: 180,StartDate: date.format(LABKEY.extDefaultDateFormat)}
                            //  "wanimalid~eq": 'r18012'
                        }).then(function (data) {
                            var events = data.rows;

                                callback(events.map(function (row) {
                                    var volume;
                                    if(row.volumeCoalesced) {
                                        volume = row.volumeCoalesced;
                                    }else{
                                        volume = 0;
                                    }
                                    var eventObj = {
                                        title: row.animalId + ' ' + volume + 'ml',
                                        start: row.date,
                                        allDay: true,
                                        // vol: row.volume,
                                        rawRowData: row,
                                        color: '#00FFFF',
                                        description: 'Water for animal ' + row.animalId
                                    };


                                    return eventObj;
                                }))
                            })
                        }
                    },
                    {
                        events:[
                        {
                            title: 'Test123 this is a really long string to see what happens!',
                            start: '2019-04-22',
                            end:   '2019-04-23'
                        }
                            ],
                            color: 'yellow',
                            eventTextColor: 'red'
                        }
                ],
                eventClick: function (calEvent, jsEvent, view) {
                    $('#waterInfo').attr('disabled', 'disabled');
                    $('#enterWaterOrder').attr('disabled', 'disabled');
                    var momentDate;
                    jQuery.each(calEvent.rawRowData, function (key, value) {
                       //debugger;


                        if (key in WebUtils.VM.taskDetails) {
                            if (key == "date") {
                                momentDate = moment(value, "YYYY/MM/DD HH:mm:ss");
                                value = moment(value, "YYYY/MM/DD HH:mm:ss").format("MM/DD/YYYY");


                                // value = displayDate(value);
                               // webUtils.VM.taskDetails[rawDate](moment(value, "YYYY/MM/DD HH:mm:ss").format("MM/DD/YYYY"));
                                //value = value.toDate();
                            }

                            WebUtils.VM.taskDetails[key](value);
                         //   debugger;




                            $('#waterExceptionPanel').collapse('hide');

                            var today = moment();

                            if (key =="date" && (momentDate.diff(today, 'days'))>= 0){
                                $('#waterInfo').removeAttr('disabled');

                            }

                            if(key == "dataSource" && value == "waterOrder" && (momentDate.diff(today, 'days'))>= 0){
                                $('#enterWaterOrder').removeAttr('disabled');

                            }

                        }
                    });



                }

            })
        });

        //var viewModel =  new ViewModel();
        //ko.applyBindings(viewModel);

        var displayDate = function(dateString) {
            return moment(dateString, "YYYY/MM/DD HH:mm:ss").calendar(null, {
                sameElse: 'MMM D[,] YYYY'
            })
        };

        // For some reason, type-ahead makes this transparent.  In order to allow bootstrap to "disable" it
        // by greying it out, we need to remove that property.
        //$assignedToField.css('background-color', '');

        var $scheduleForm = $('.scheduleForm');

        _.extend(WebUtils.VM, {

            taskDetails: {
                lsid:                 ko.observable(),
                objectid:             ko.observable(),
                taskid:               ko.observable(),
                animalId:             ko.observable(),
                date:                 ko.observable(),
                volumeCoalesced:      ko.observable(),
                project:              ko.observable(),
                dataSource:           ko.observable(),
                assignedToCoalesced:  ko.observable(),
                rawDate:              ko.observable(),
                assignedTo:           ko.observable(),
                wantsSpam:            ko.observable(),

            },
            form: ko.mapping.fromJS({
                lsid:        '',
                animalId:    '',
                date:        new Date(),
                volume:      ''

            }),


            requestTableClickAction: function(row) {
                /*$('#waterExceptionPanel').block({
                    css: {
                        visibility: visible
                    }
                });*/
                WebUtils.VM.requestRowInForm = row;
                //WebUtils.VM.updateForm(row.animalId);

            },

            endWaterOrder: function (row){

                $('#waterInfoPanel').block({
                    message: '<img src="<%=getContextPath()%>/webutils/icons/loading.svg">Closing Water Order...',
                    css: {
                        border: 'none',
                        padding: '15px',
                        backgroundColor: '#000',
                        '-webkit-border-radius': '10px',
                        '-moz-border-radius': '10px',
                        opacity: .5,
                        color: '#fff'
                    }
                });

                WebUtils.VM.requestRowInForm = row;
                var waterOrder = ko.mapping.toJS(row);

                //TODO: escalate permission to close waterorder  older than 60 days or all ongoing water order
                //TODO: should have the QC Status of Started and not complete

                LABKEY.Ajax.request({
                    url: LABKEY.ActionURL.buildURL("wnprc_ehr", "CloseWaterOrder", null, {
                        lsid:           waterOrder.lsid,
                        taskId:         waterOrder.taskid,
                        objectId:       waterOrder.objectid,
                        animalId:       waterOrder.animalId,
                        endDate:        waterOrder.date,
                        dataSource:     waterOrder.dataSource

                    }),
                    success: LABKEY.Utils.getCallbackWrapper(function (response)
                    {
                        if (response.success){

                            // Refresh the calendar view.
                            $calendar.fullCalendar('refetchEvents');

                            $('#waterInfoPanel').unblock();

                        } else {
                            alert('Water cannot be closed')
                        }


                    }, this)

                });

            },

            enterNewWaterOrder: function (row){

                $('#waterInfoPanel').block({
                    message: '<img src="<%=getContextPath()%>/webutils/icons/loading.svg">Closing Water Order...',
                    css: {
                        border: 'none',
                        padding: '15px',
                        backgroundColor: '#000',
                        '-webkit-border-radius': '10px',
                        '-moz-border-radius': '10px',
                        opacity: .5,
                        color: '#fff'
                    }
                });

                WebUtils.VM.requestRowInForm = row;
                var waterOrder = ko.mapping.toJS(row);

                //TODO: escalate permission to close waterorder  older than 60 days or all ongoing water order
                //TODO: should have the QC Status of Started and not complete

                LABKEY.Ajax.request({
                    url: LABKEY.ActionURL.buildURL("wnprc_ehr", "EnterNewWaterOrder", null, {
                        lsid:           waterOrder.lsid,
                        taskId:         waterOrder.taskid,
                        objectId:       waterOrder.objectid,
                        animalId:       waterOrder.animalId,
                        endDate:        waterOrder.date,
                        dataSource:     waterOrder.dataSource

                    }),
                    success: LABKEY.Utils.getCallbackWrapper(function (response)
                    {
                        if (response.success){

                            // Refresh the calendar view.
                            $calendar.fullCalendar('refetchEvents');

                            $('#waterInfoPanel').unblock();
                            console.log(response.success);

                        } else {
                            alert('Water cannot be closed')
                        }


                    }, this)

                });













            },



            viewCollectionListURL: ko.pureComputed(function() {
                <% ActionURL collectionListURL = new ActionURL(WNPRC_EHRController.NecropsyCollectionListAction.class, getContainer()); %>

                return LABKEY.ActionURL.buildURL('<%= collectionListURL.getController() %>', '<%= collectionListURL.getAction() %>', null, {
                    reportMode: true,
                    taskid: WebUtils.VM.taskDetails.lsid()
                });
            })


        });

        WebUtils.VM.taskDetails.animalLink = ko.pureComputed(function() {
            var animalId = WebUtils.VM.taskDetails.animalId();

            return LABKEY.ActionURL.buildURL('ehr', 'participantView', null, {
                participantId: animalId
            });
        });

        WebUtils.VM.taskDetails.displayDate = ko.pureComputed(function(){
            var dateString = WebUtils.VM.taskDetails.date();

            if (dateString){
                return (moment(dateString, "MM/DD/YYYY").calendar(null, {
                    sameElse: 'MMM D[,] YYYY'
                }).split(" at"))[0]

            }else {
                return " ";
            }




        });

        _.extend(WebUtils.VM, {
            clearForm: function() {
                jQuery.each(WebUtils.VM.form, function(key, observable) {
                    if (ko.isObservable(observable) && !ko.isComputed(observable)) {
                        observable('');
                    }
                });

                $assignedToField.val('');
            },
           /* updateForm: function(animalId) {
                var request = pendingRequestsIndex[lsid];

                if (!_.isObject(request)) {
                    return;
                }

                jQuery.each(request, function(key, value) {
                    if (key in WebUtils.VM.form) {
                        if (key == "date") {
                            value = new Date(value);
                        }
                        WebUtils.VM.form[key](value);
                    }
                });
            },*/
            submitForm: function(){

                var form = ko.mapping.toJS(WebUtils.VM.taskDetails);
                var taskid = LABKEY.Utils.generateUUID();
                //var date = form.date.format("Y-m-d H:i:s");
                debugger;

                WebUtils.API.insertRows('study', 'waterAmount', [{
                    taskid:     taskid,
                    Id:         form.animalId,
                    date:       form.date,
                    assignedTo: form.assignedTo,
                    project:    form.project,
                    volume:     form.volumeCoalesced
                }])
                $('#waterExceptionPanel').collapse('hide')

                // Refresh the calendar view.
                $calendar.fullCalendar('refetchEvents');
            },
            ViewModel: function (){
                var self = this;
                self.wantsSpam = ko.observable(false);
                self.wantsSpam.subscribe(function(){
                    self.thealertIwantTosend();
                })
                self.thealertIwantTosend = function(){
                    if(self.wantsSpam() == true){
                        alert('I want Spam!')}
                    else{
                        alert('I dont want Spam!')
                    }
                }

            },
        });


    })();

    function ViewModel(){
        var self = this;
        self.wantsSpam = ko.observable(false);
        self.wantsSpam.subscribe(function(){
            self.thealertIwantTosend();
        })
        self.thealertIwantTosend = function(){
            if(self.wantsSpam() == true){
                alert('I want Spam!')}
            else{
                alert('I dont want Spam!')
            }
        }

    }





</script>