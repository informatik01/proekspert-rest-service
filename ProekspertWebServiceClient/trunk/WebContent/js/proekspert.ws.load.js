var wsURL = "http://192.168.1.73:11555/proekspert/service/data";
var currentRequestId = -1;

// Dealing with "console is not defined" issue in IE
if (!window.console) {
	console = {log: function() {}};
}

// Loading data using pure JavaScript
function loadData(callback) {
	var xdr = createXDR("GET", wsURL);
	xdr.timeout = 3000;
	var errorMessage = document.getElementById("errorMessage");
	xdr.onload = function() {
		callback(xdr.responseText);
	};
	xdr.onerror = function() {
		errorMessage.innerHTML = 'Error occured when sending AJAX request.';
	};
	xdr.ontimeout = function() {
		errorMessage.innerHTML = "Timeout (" + xdr.timeout + " ms) was exceeded.";
	};
	xdr.send();
}

// Loading data using jQuery.ajax()
function loadDataJQ(callback) {
	$.ajax({
		type: "GET",
		url: wsURL,
		dataType: "json",
		cache: false,
		timeout: 5000,
		success: callback,
		error:	function(jqXHR, textStatus, errorThrown) {
					if (textStatus === "timeout") {
						$("#errorMessage").html("Timeout (" +
												this.timeout +
												" ms) was exceeded.");
					} else {
						$("#errorMessage").html(
								"Web service is currently unavailable.");
					}
				}
	});
}

function process(data) {
	if (typeof message === "string") {
		message = JSON.parse(message);
	}

	var output = $("#output");
	if (data.id !== "undefined") {
		if (currentRequestId == data.id) {			// same data fetched
			return;
		} else if (currentRequestId !== data.id) {	// new data fetched
			currentRequestId = data.id;
			clearData();
		} else {									// first time
			currentRequestId = data.id;
		}
	} else {
		output.before("<b>No data available at the moment</b>");
	}
	$("#errorMessage").empty();
	
	/*
	 * IMPLEMENTATION NOTE
	 * Due to the issues with IE (excluding version 10, probably 9?)
	 * there is a lot plain string concatenations, instead of chaining
	 * ".text()" or ".html()". Although not a problem by itself, but still
	 * makes the code look less elegant. The current solution works on
	 * Internet Explorer 8+.
	 */
	$("<p>").appendTo(output).text('Request sequence ID: ').
		append($('<span class="data">'+ data.id + '</span>'));
	$("<p>").appendTo(output).text('Associated phone number: ').
	append($('<span class="data">' + data.servicePhoneNumber + '</span>'));
	$("<p>").appendTo(output).text("Service language: ").
	append($('<span class="data">' + data.serviceLanguage + '</span>'));
	
	// Checking whether the service is active, and if not - no more output
	$("<p>").appendTo(output).text('Service is ').
	append($('<span class="data">' +
			(data.active ? 'active' : 'inactive') + '</span>'));
	if (!data.active) {
		return;
	}
	$("<p>").appendTo(output).text('Service active until ').
	append($('<span class="data">' + 
			moment(data.serviceEndDate).format("MMMM D YYYY HH:mm") + '</span>'));
	
	// Checking whether the XL-Additional service is active
	var xlServiceActivationTime = "";
	var xlServiceEndTime = "";
	if (data.xlService) {
		xlServiceActivationTime = moment(
				data.xlServiceActivationTime.values.toString(),
				"HH,mm,SS,SSS").format("HH:mm");
		xlServiceEndTime = moment(
				data.xlServiceEndTime.values.toString(),
		"HH,mm,SS,SSS").format("HH:mm");
	}
	$("<p>").appendTo(output).text('XL-service ').
	append($('<span class="data">' + 
			(data.xlService ? 'active (' + xlServiceActivationTime +
							  		  ' - ' + xlServiceEndTime + ') in ' +
							  		  data.xlServiceLanguage + ' language'
							  : 'inactive') + '</span>'));
	
	// Populating the list of names and phone numbers
	// if the override list is in use
	if (data.overrideListInUse) {
		$('<p style="margin-bottom: 5px;">').appendTo(output).text('Except for');
	
		var table = $("<table>");
		$("<tr>").appendTo(table).
				append($("<th>").text("Phone")).
				append($("<th>").text("Name"));
		$.each(data.persons, function(index, person) {
			$("<tr>").appendTo(table).
					append($("<td>").text(person.phoneNumber)).
					append($("<td>").text(person.name));
		});
		table.appendTo($(output));
	}
}

function clearData() {
	$("#output").empty();
	currentRequestId = -1;
}

/********** Scheduling periodic AJAX load of data **********/
var intervalId;

function scheduleDataLoad(interval) {
	// Using anonymous function to call callback to overcome IE issues
	intervalId = setInterval(function() {
								loadDataJQ(process);
							},
							interval);
}

function stopScheduledDataLoad() {
	clearInterval(intervalId);
}

/**
 * Small utility function to create XMLHttpRequest objects for CORS.
 */
function createXDR(method, url) {
	var xdr = new XMLHttpRequest();
	if ("withCredentials" in xdr) {
		console.log("Using XMLHttpRequest 2.");
		xdr.open(method, url, true);
	} else if (typeof XDomainRequest != "undefined") {
		// Dealing with Internet Explorer.
		// NB! Minimum IE 8 required to be able to use XDomainRequest.
		console.log("Using XDomainRequest.");
		xdr = new XDomainRequest();
		xdr.open(method, url);
	} else {
		console.log("This browser does not support Cross-Origin Resource Sharing (CORS)");
		xdr = null;
	}
	
	return xdr;
}