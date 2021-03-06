<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>YummyTeam9.Food -- lacation</title>

    <!-- Bootstrap Core CSS -->
    <link href="static_res/css/bootstrap.min.css" rel="stylesheet">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>

<body onload="initMap()">

    <!-- Navigation -->
    <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
        <div class="container">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
                <a class="navbar-brand" href="home.html" style="font-family:Kokonor;">YummyTeam9.Food</a>
            </div>
            <!-- Collect the nav links, forms, and other content for toggling -->
            <!-- <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1"> -->
                <ul class="nav navbar-nav navbar-right">
                    <li>
                        <a href="/">Home</a>
                    </li>
                    <li>
                        <a href="/signin">Login</a>
                    </li>
                    <li>
                        <a href="/signupform">Create Account</a>
                    </li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">About Us<b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li>
                                <a href="#">Contacts</a>
                            </li>
                            <li>
                                <a href="/locations">Locations</a>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
            <!-- /.navbar-collapse -->
        <!-- </div> -->
        <!-- /.container -->
    </nav>

    <!-- Full Page Image Background Carousel Header -->
    <div class="container">
        <h2 style="margin-top:60px;">Our Location</h2>
        <p>1 Washington Sq, San Jose, CA 95192</p>
        <div class="panel-body" id="map-loc-trace">
            <div id="map-canvas" style="height: 500px;" ></div>
        </div>
        <hr>

        <!-- Footer -->
        <footer style="text-align:center;">
            <div class="row">
                <div class="col-lg-12">
                    <p>Copyright &copy; CMPE275 -- Project Group 9</p>
                </div>
            </div>
            <!-- /.row -->
        </footer>

    </div>
    <!-- /.container -->

    <!-- jQuery -->
    <script src="static_res/js/jquery.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="static_res/js/bootstrap.min.js"></script>

    <!-- Script for the map -->
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp"></script>
    <script>
    
    function initMap() {
        var myLatLng = {lat: 37.3352, lng: -121.8811};
        
        // Create a map object and specify the DOM element for display.
        var map = new google.maps.Map(document.getElementById('map-canvas'), {
                                      center: myLatLng,
                                      scrollwheel: false,
                                      zoom: 14
                                      });
                                      
                                      // Create a marker and set its position.
                                      var marker = new google.maps.Marker({
                                                                          map: map,
                                                                          position: myLatLng,
                                                                          title: 'You find us!'
                                                                          });
    }
    </script>

</body>

</html>
