#
# This is a system-wide cron tab, meant to be stored as "/etc/crontab" or in "/etc/cron.d/".  This
# is as opposed to user specific crontab files, which don't include the user.
#
# Possible Values:
#  - Min (0 - 59)
#  - Hour (0 - 23)
#  - Day [of Month] (1 - 31)
#  - Month (1 - 12)
#  - Weekday (0 - 6)
#     - 0 = Sunday, 6 = Saturday
#
#---------------------------------------------------------------------------------------
# Environment Variables
#---------------------------------------------------------------------------------------
ALERT_DIR=/usr/local/labkey/automaticAlerts/
#
#---------------------------------------------------------------------------------------
#Min   Hour     Day   Month   Weekday   User   Command
#---------------------------------------------------------------------------------------
0      10       *     *       *         root   $ALERT_DIR/adminAlerts.pl;
30     5,10,14  *     *       *         root   $ALERT_DIR/bloodAdminAlerts.pl;
15     5,8,9    *     *       *         root   $ALERT_DIR/bloodAlerts.pl;
5      7-17     *     *       *         root   $ALERT_DIR/clinpathAbnormalResultAlerts.pl;
0      11,13,16 *     *       *         root   $ALERT_DIR/clinpathAlerts.pl;
00     10       *     *       *         root   $ALERT_DIR/clinpathResultAlerts.pl;
0      5        *     *       *         root   $ALERT_DIR/colonyAlerts.pl;
0      9-17     *     *       *         root   $ALERT_DIR/colonyAlertsLite.pl;
15     5        *     *       *         root   $ALERT_DIR/colonyMgmtAlerts.pl;
12     6        *     *       *         root   $ALERT_DIR/largeInfantAlerts.pl;
15     9        *     *       *         root   $ALERT_DIR/overdueWeightAlerts.pl;
25     7-17     *     *       *         root   $ALERT_DIR/siteErrorAlerts.pl;
0      6,10,13  *     *       *         root   $ALERT_DIR/treatmentAlerts.pl;
0      15,17,19 *     *       *         root   $ALERT_DIR/treatmentAlerts.pl;
30     13       *     *       *         root   $ALERT_DIR/treatmentAlerts.pl;
10     15       *     *       *         root   $ALERT_DIR/weightAlerts.pl;

