<%@page import="edu.sjsu.cmpe275.domain.User" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en" ng-app="systemReportApp">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>YummyTeam9.Food -- home</title>
    <!-- font family -->
    <link href='https://fonts.googleapis.com/css?family=Oswald' rel='stylesheet' type='text/css'>
    <!-- Bootstrap Core CSS -->
    <link href="/static_res/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="/static_res/css/half-slider.css" rel="stylesheet">

    <!-- jQuery -->
    <script src="/static_res/js/jquery.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="/static_res/bootstrap/js/bootstrap.min.js"></script>

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
        <!--angular JS-->
    <script src="/static_res/angular/angular.js"></script>
    <script src="/static_res/angular/angular-animate.js"></script>
    <script src="/static_res/angular/ui-bootstrap-tpls-0.13.4.min.js"></script>
    <script src="/static_res/angular/ui-bootstrap-tpls-1.3.2.js"></script>
    <script src="/static_res/angular/ui-bootstrap-tpls-0.14.2.min.js"></script>
    <script src="/static_res/js/system_ReportJS.js"></script>
    <!-- Script to Activate the Carousel
    <!-- <script>
        $('#myCarousel').carousel({
            interval: 3000
            //changes the speed
        })
    </script> -->
    <![endif]-->
    <style>

        @font-face {
            font-family: "Bodoni SvtyTwo ITC TT";
            src: url("http://db.onlinewebfonts.com/t/8bc773512e829a52d207976b43c0ecca.eot");
            src: url("http://db.onlinewebfonts.com/t/8bc773512e829a52d207976b43c0ecca.eot?#iefix") format("embedded-opentype"),
            url("http://db.onlinewebfonts.com/t/8bc773512e829a52d207976b43c0ecca.woff2") format("woff2"),
            url("http://db.onlinewebfonts.com/t/8bc773512e829a52d207976b43c0ecca.woff") format("woff"),
            url("http://db.onlinewebfonts.com/t/8bc773512e829a52d207976b43c0ecca.ttf") format("truetype"),
            url("http://db.onlinewebfonts.com/t/8bc773512e829a52d207976b43c0ecca.svg#Bodoni SvtyTwo ITC TT") format("svg");
        }

        div.one {
            border: 1px darkgrey solid;
            height: 650px;
            margin: 10px 10px 10px 10px;
        }

        body {
            font-family: 'Oswald', sans-serif;
            background: url(/static_res/image/background5.jpg) repeat-y center center fixed;
            -webkit-background-size: cover;
            -moz-background-size: cover;
            -o-background-size: cover;
            background-size: cover;
        }

        p.item {
            text-align: center;
            color: black;
            margin-bottom: 5px;
        }

        a:hover {
            color: blue;
        }

        .main-content {
            margin-top: 30px;
            background-color: rgba(220, 220, 220, 0.7);
            border-bottom-left-radius: 20px;
            border-top-left-radius: 20px;
            border-bottom-right-radius: 20px;
            border-top-right-radius: 20px;
        }

        .menu-content {
            border-bottom-left-radius: 20px;
            border-top-left-radius: 20px;
            border-bottom-right-radius: 20px;
            border-top-right-radius: 20px;
        }

        .menu-nav {
            border-bottom-left-radius: 20px;
            border-top-left-radius: 20px;
        }

        .menu-item-list {
            border-bottom-right-radius: 20px;
            border-top-right-radius: 20px;
        }

        -moz-selection {
            color: #fff;
            color: rgba(255, 255, 255, 0.85);
            background: #ea4c89
        }

        ::selection {
            color: #fff;
            color: rgba(255, 255, 255, 0.85);
            background: #ea4c89
        }

        .popover-content {
            margin: 0 auto;
            width: 280px;
        }


    </style>
</head>

<body ng-controller="systemReportCtrl">

<!-- Navigation -->
<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation" style="height: 200px;">
    <div class="container">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <a class="navbar-brand" href="/"
               style="font-family: Kokonor;">YummyTeam9.Food</a>
        </div>
        <ul class="nav navbar-nav navbar-right">
            <li style="width: 30%"><a href="/admin">Dashboard</a></li>
            <li style="width: 50%"><a href="/admin/systemReport">System Report</a></li>
            <li style="width: 10%"><a href="/logout">Logout</a></li>
        </ul>
        <div align="center">
            <h2 style="color: #fff;">System Report</h2>
        </div>
        <div class="row">
            <div class="col-md-4">
                <table>
                    <tr>
                        <td style="color: white"> start time</td>
                        <td>
                            <div class="input-group">
                                <input type="text" class="form-control" uib-datepicker-popup="{{format}}"
                                       ng-model="startTime"
                                       is-open="popup.start" datepicker-options="dateOptions" ng-required="true"
                                       close-text="Close"
                                       alt-input-formats="altInputFormats" ng-change="selectedStartTime(startTime)"/>
                                <div class="input-group-btn">
                                    <button type="button" class="btn btn-default" ng-click="start()"><i
                                            class="glyphicon glyphicon-calendar"></i></button>
                                </div>
                            </div>
                        </td>
                        <td>
                            <uib-timepicker ng-model="startTime"
                                            show-meridian="false"
                                            ng-change="selectedStartTime(startTime)"></uib-timepicker>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="col-md-8">
                <table>
                    <tr>
                        <td style="color: white"> End time</td>
                        <td>
                            <div class="input-group">
                                <input type="text" class="form-control" uib-datepicker-popup="{{format}}"
                                       ng-model="endTime"
                                       is-open="popup.end" datepicker-options="dateOptions" ng-required="true"
                                       close-text="Close"
                                       alt-input-formats="altInputFormats" ng-change="selectedEndTime(endTime)"/>
                                <div class="input-group-btn">
                                    <button type="button" class="btn btn-default" ng-click="end()"><i
                                            class="glyphicon glyphicon-calendar"></i></button>
                                </div>
                            </div>
                        </td>
                        <td>
                            <uib-timepicker ng-model="endTime"
                                            show-meridian="false"
                                            ng-change="selectedEndTime(endTime)"></uib-timepicker>
                        </td>
                        <td>
                            <button type="button" class="btn btn-success" ng-click="getSystemReport()">Submit</button>
                        </td>
                        <td>
                            <font color=white>Order By:</font>
                        </td>
                        <td>
                            <button type="button" class="btn btn-info" ng-click="sortByOrderTime()">Order Time
                                </button>
                        </td>
                        <td>

                            <button type="button" class="btn btn-primary" ng-click="sortByStartTime()">Start Time
                                </button>

                        </td>
                    </tr>
                </table>
            </div>
        </div>
        <div align="center">
            <table class="table" style="width:100%; margin:0 auto;">
                <tbody style="font-size: small;">
                <tr>
                    <td align="left" style="width: 50px"><font color=white>ID</font></td>
                    <td align="left" style="width: 220px"><font color=white>Order Time</font></td>
                    <td align="left" style="width: 220px"><font color=white>Start Time</font></td>
                    <td align="left" style="width: 220px"><font color=white>Ready Time</font></td>
                    <td align="left" style="width: 220px"><font color=white>Pickup Time</font></td>
                    <td align="left" style="width: 100px"><font color=white>Items</font></td>
                    <td align="left" style="width: 80px"><font color=white>Total</font></td>
                    <td align="left" style="width: 150px"><font color=white>Email</font></td>
                    <td align="left" style="width: 150px"><font color=white>Status</font></td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
    <!-- /.navbar-collapse -->
    <!-- /.container -->
</nav>

<!-- content -->
<div align="center" style="margin-top: 200px; min-height: 100%; margin-bottom: -142px;">
    <table class="table" style="width:80%; margin:0 auto;" ng-model="orderHistory">
        <tbody ng-model="trStatus" style="font-size: small;">
        <tr ng-repeat="historyItem in orderHistory" class="{{historyItem.status}}">
            <td align="left" style="width: 50px">{{historyItem.orderId}}</td>
            <td align="left" style="width: 220px">{{historyItem.orderTime}}</td>
            <td align="left" style="width: 220px">{{historyItem.startTime}}</td>
            <td align="left" style="width: 220px">{{historyItem.readyTime}}</td>
            <td align="left" style="width: 220px">{{historyItem.pickupTime}}</td>
            <td align="left" style="width: 150px">
                <button popover-placement="bottom" uib-popover-template="itemsInOrder" popover-title="Order in Items"
                        type="button"
                        class="btn btn-default">Order Content
                </button>
                <script type="text/ng-template" id="itemsInOrder" style="width: 350px">
                    <div>
                        <table class="table table-hover" style="margin: 0 auto">
                            <tr ng-repeat="item in historyItem.itemAndCount">
                                <td align="left">{{item.itemName}}</td>
                                <td align="left">x {{item.count}}</td>
                            </tr>
                        </table>
                    </div>
                </script>
            </td>
            <td align="left" style="width: 50px">$ {{historyItem.totalPrice}}</td>
            <td align="left" style="width: 150px">{{historyItem.customerEmail}}</td>
            <td align="left" style="width: 150px;">
                <div ng-if="historyItem.status == 'warning'">
                    <uib-progressbar class="progress-striped active" value="40"
                                     type="warning">Preparing
                    </uib-progressbar>
                </div>
                <div ng-if="historyItem.status == 'info'">
                    <uib-progressbar class="progress-striped active" value="70"
                                     type="info"> Processing
                    </uib-progressbar>
                </div>
                <div ng-if="historyItem.status == 'success'">
                    <uib-progressbar class="progress-striped active"
                                     type="success">Done
                    </uib-progressbar>
                </div>
            </td>
        </tr>
        </tbody>
    </table>

</div>

<!-- Footer -->
<footer class="footer">
    <div align="center" class="container">
        <p class="text-muted">copyright &copy; CMPE275 -- Project Group 9</p>
    </div>
</footer>

<!-- /.container -->


</body>

</html>