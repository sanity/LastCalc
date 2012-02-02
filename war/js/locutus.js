variables = {};

recognizedWords = [];

function getLineNumber(lineEl) {
	return parseInt(lineEl.attr("id").substring(4));
}

function reassignIds() {
	// Two step process to ensure that no two elements get the same id
	var c = 1;
	$("DIV.line").each(function() {
		$(this).attr("id", "tmp" + c);
		c = c + 1;
	});
	c = 1;
	$("DIV.line").each(function() {
		$(this).attr("id", "line" + c);
		c = c + 1;
	});
}

function highlightSyntax(element) {
	rangy.init();
	
	if (element.length != 1) {
		alert("highlightSyntax() called with " + element.length
				+ " elements (should be 1)");
	}
	
	if (!rangy.getSelection().isCollapsed) {
		console.log("Aborting highlight because selection size > 0");
		return;
	}
	
	var lineNumber = getLineNumber(element.parent("DIV.line"));
	var savedSel = rangy.saveSelection();
	// Remove any existing variables or stray elements caused by a Webkit bug (see http://stackoverflow.com/questions/9018766/dom-elements-appearing-inexplicably_)
	element.find("font,div,span:not(.rangySelectionBoundary)").replaceWith(function() {
		return $(this).contents();
	});
	console.log("Scanning...");
	console.log("HTML: "+element.html());
	element.html(element.html().replace(
			/<span.*span>|\.\.\.|[0-9]*\.?[0-9]+|[a-zA-Z][a-zA-Z0-9]*/g,
			function(str) {
				console.log("token: \""+str+"\"");
				// We have to do this because of the <span> inserted by rangy which was
				// tripping
				// up the regexp. Ugly but it works.
				if (str.match(/nbsp|amp/)) {
					return str;
				}
				if (str.match(/span/)) {
					return str;
				}
				// if (str.match(/^"(?:[^"\\]|\\.)*"$/)) {
				// return "<span class=\"highlighted quoted\">"+str+"</span>";
				// }
				if (str.match(/^[-+]?[0-9]*\.?[0-9]+$/)) {
					return "<span class=\"highlighted number\">" + str + "</span>";
				}
				if (str.match(/^[A-Z]+[a-zA-Z0-9]*$/)) {
					return "<span class=\"highlighted variable white\">" + str
							+ "</span>";

				}
				var nc = variables[str];
				if (nc && (nc <= lineNumber)) {
					var colors = [ "red", "green", "blue", "orange", "rosy", "pink",
							"gray", "black" ];
					var color = colors[nc % colors.length];
					return "<span class=\"highlighted variable " + color + "\">" + str
							+ "</span>";
				} else if ($.inArray(str, recognizedWords) > -1) {
					return "<span class=\"highlighted recognized\">" + str + "</span>";
				} else {
					return str;
				}

			}));

	rangy.restoreSelection(savedSel);
}

$(window).load(function() {

	$(document).on("keydown", "DIV.question", function(event) {
		if (event.keyCode == 13) {
			// Carriage return
			var origLine = $(this).parent("DIV.line");
			q = origLine.clone();
			q.attr("id", "line" + (getLineNumber(origLine) + 1));
			q.attr("class", "line");
			q.find("DIV.question").text("");
			q.hide();
			$(this).parent(".line").after(q);
			reassignIds();
			q.fadeIn(50, function() {
				q.find("DIV.question").focus();
			});
			event.preventDefault();
		} else if (event.keyCode == 38) {
			// Up arrow
			$(this).parent().prev().find(".question").focus();
			event.preventDefault();
		} else if (event.keyCode == 40) {
			// Down arrow
			$(this).parent().next().find(".question").focus();
			event.preventDefault();
		}
	});

	$(document).on("click", "DIV.line", function(event) {
		if (!$(this).find("DIV.question").is(":focus")) {
			$(this).find("DIV.question").focus();
		}
		event.stopPropagation();
		return false;
	});

	$(document).on("focus", "DIV.question", function(event) {
		$(this).parent().find("DIV.answer").hide();
		$(this).parent().find("DIV.equals").hide();
		$(this).addClass("editing");
	});

	$(document).on("focusout", "DIV.question", function(event) {
		var thisLine = $(this).parent("DIV.line");
		var thisLineNumber = getLineNumber(thisLine);
		var q = thisLine.find("DIV.question");
		q.attr("class", "question"); // Remove editing class, removeClass() didn't
		// work reliably for some reason
		thisLine.find("DIV.equals").html("&there4;").show();
		if (($.trim($(this).text()).length == 0)) {
			// Its empty, delete this line unless it is the last one
			if (thisLine.next("DIV.line").length != 0) {
				thisLine.remove();
				reassignIds();
			} else { // It is the last one, so hide the equals and the answer
				thisLine.find("DIV.answer").hide();
				thisLine.find("DIV.equals").hide();
			}
		} else {
			var toSend = {
				"worksheetId" : $("body").attr("data-worksheet-id"),
				"questions" : {}
			};

			$("DIV.line").each(function(lineNoM1, lineElement) {
				var lineNo = lineNoM1 + 1;
				if (lineNo >= thisLineNumber) {
					var qText = $.trim($(this).find("DIV.question").text());
					if (qText.length > 0) {
						toSend.questions[lineNo] = qText;
					}
				}
			});
			$.ajax({
				type : "POST",
				url : "/ws",
				data : JSON.stringify(toSend),
				contentType : "application/json; charset=utf-8",
				dataType : "json",
				success : function(response) {
					variables = response.variables;
					$("DIV.line").each(function() {
						var ln = getLineNumber($(this));
						if (response.answers[ln]) {
							// If we have a new answer for this
							$(this).find("DIV.answer").html(response.answers[ln]);
							highlightSyntax($(this).find("DIV.question"));
							if (!$(this).find("DIV.question").is(":focus")) {
								// Only show if this question doesn't currently have focus
								var answerType = response.answerTypes[ln];
								if (answerType == "NORMAL") {
									$(this).find("DIV.answer").fadeIn("fast");
									$(this).find("DIV.equals").text("=").fadeIn("fast");
								} else if (answerType == "FUNCTION") {
									$(this).find("DIV.answer").hide();
									$(this).find("DIV.equals").html("<span style=\"font-size:10pt;\">&#10003</span>").fadeIn("fast");
								}
							}
						}
					});
				}
			});
		}
	});

	// Catch keyups within questions for highlighting
	$(document).on("keyup", "div.question", function(event) {
		   highlightSyntax($(this));
	});
	variables = jQuery.parseJSON($('body').attr("data-variables"));
	$("DIV.question").each(function() {
		highlightSyntax($(this));
	});
	
	// Stupid change to force upload of file
	var helpDiv = $("DIV#helpframe");
	// Set up help button
	$("DIV#help-button").button();
	$("DIV#help-button").click(function() {
		if (helpDiv.is(":visible")) {
			$('DIV#worksheet').animate({width:'100%'}, 500);
			helpDiv.hide("fade", 250, function() {
				$("DIV#help-button span").text("Show Help");
				$.cookie('help-div', 'hidden', { expires: 30});
			});
		} else {
			$('DIV#worksheet').animate({width:'50%'}, 250);
			$("DIV#help-button span").text("Hide Help");
			helpDiv.show("fade", 500);
			$.cookie('help-div', 'visible', { expires: 30});
		}
	});
	
	// Show the help DIV if the cookie says we should
	// or if there is no cookie
	if ($.cookie('help-div') == null || $.cookie('help-div') == 'visible') {
		$('DIV#worksheet').animate({width:'50%'}, 250);
		$("DIV#help-button span").text("Hide Help");
		helpDiv.show("fade", 500);
		$.cookie('help-div', 'visible', { expires: 30});
	}
	
	// Set up help nav menu
//Set up help DIV navigation buttons
	$("DIV#helpframe ul#menu li").button().click(function(event) {
		var name = $(this).attr("data-section");
		$('DIV#helpframe').animate({scrollTop: $("div#helpframe").scrollTop()+$("a[name="+name+"]").offset().top-$("div#helpframe").offset().top},'slow');
	//	event.preventDefault();
		return true;
	});
	
	$("DIV.question").last().focus();
	$("BODY").click(function() {
		if (!$("DIV.question:focus").length) {
			$("DIV.question").last().focus();
		}
	});
});
