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

<style type="text/css">
    /* Full Calendar heading */
    #calendar .fc-toolbar h1 {
        font-size: 20px;
        margin: 0;
    }
</style>

<div class="col-xs-12 col-xl-8">
    <div class="col-xs-12 col-md-4">
        <div class="panel panel-primary">
            <div class="panel-heading"><span>Water Details</span></div>
            <div class="panel-body" data-bind="with: taskDetails">


                <dl class="dl-horizontal">
                    <dt>Task ID:            </dt> <dd>{{taskid}}</dd>
                    <dt>Animal ID:          </dt> <dd><a href="{{animalLink}}">{{animalid}}</a></dd>
                    <dt>Volume:             </dt> <dd>{{volume}}</dd>
                    <dt>Project (Account):  </dt> <dd>{{project}} ({{account}})</dd>
                    <dt>Date:               </dt> <dd>{{date}}</dd>
                </dl>


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
                                    var eventObj = {
                                        title: row.animalid + ' ' + row.volumeCoalesced+'ml',
                                        start: row.date,
                                        allDay: true,
                                       // vol: row.volume,
                                        rawRowData: row,
                                        color: '#00FFFF',
                                        description: 'Water for animal '+ row.animalid
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
                    jQuery.each(calEvent.rawRowData, function (key, value) {
                        if (key in WebUtils.VM.taskDetails) {
                            if (key == "date") {
                                value = displayDate(value);
                            }
                            WebUtils.VM.taskDetails[key](value);
                        }
                    });
                }

            })
        });

        var displayDate = function(dateString) {
            return moment(dateString, "YYYY/MM/DD HH:mm:ss").calendar(null, {
                sameElse: 'MMM D[,] YYYY'
            })
        };

        // For some reason, type-ahead makes this transparent.  In order to allow bootstrap to "disable" it
        // by greying it out, we need to remove that property.
        //$assignedToField.css('background-color', '');

        //var $scheduleForm = $('.scheduleForm');

        _.extend(WebUtils.VM, {

            taskDetails: {
                lsid:                 ko.observable(),
                taskid:               ko.observable(),
                animalid:             ko.observable(),
                date:                 ko.observable(),
                volume:               ko.observable(),
                project:              ko.observable(),
                account:              ko.observable(),

            },
            form: ko.mapping.fromJS({
                lsid:        '',
                animalid:    '',
                date:        new Date()

            }),


            requestTableClickAction: function(row) {
                WebUtils.VM.requestRowInForm = row;
                WebUtils.VM.updateForm(row.otherData.lsid);
            }
        });

        WebUtils.VM.taskDetails.animalLink = ko.pureComputed(function() {
            var animalId = WebUtils.VM.taskDetails.animalid();

            return LABKEY.ActionURL.buildURL('ehr', 'participantView', null, {
                participantId: animalId
            });
        });


    })();
</script>