// TODO: Investigate selecting and copying an entry to the clipboard

variables = {};

recognizedWords = [];

function highlightSyntax(element) {
	if (element.length != 1) {
		alert("highlightSyntax() called with " + element.length
				+ " elements (should be 1)");
	}
	var lineNumber = parseInt(element.parent().find(".question_no").text());
	var savedSel = rangy.saveSelection();
	// Remove any existing variables
	element.find("span.highlighted").replaceWith(function() {
		return $(this).contents();
	});
	inSpan = false;
	element.html(element.html().replace(
			/\.\.\.|\?|[0-9]*\.?[0-9]+|[a-zA-Z0-9]+|"(?:[^"\\]|\\.)*"/g,
			function(str) {
				// We have to do this because of the <span> inserted by rangy which was
				// tripping
				// up the regexp. Ugly but it works.
				if (str.match(/nbsp|amp/)) {
					return str;
				}
				if (str.match(/span/)) {
					inSpan = !inSpan;
					return str;
				}
				if (inSpan) {
					return str;
				}
				// if (str.match(/^"(?:[^"\\]|\\.)*"$/)) {
				// return "<span class=\"highlighted quoted\">"+str+"</span>";
				// }
				if (str.match(/^[-+]?[0-9]*\.?[0-9]+$/)) {
					return "<span class=\"highlighted number\">" + str + "</span>";
				}
				var nc = variables[str];
				if (nc && (nc <= lineNumber)) {
					var colors = [ "red", "green", "blue", "orange", "rosy", "pink",
							"white", "gray", "black" ];
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

// Set up variable clicking
function isOrContainsNode(ancestor, descendant) {
	var node = descendant;
	while (node) {
		if (node === ancestor) {
			return true;
		}
		node = node.parentNode;
	}
	return false;
}

function insertNodeOverSelection(node, containerNode) {
	var sel, range, html;
	if (window.getSelection) {
		sel = window.getSelection();
		if (sel.getRangeAt && sel.rangeCount) {
			range = sel.getRangeAt(0);
			if (isOrContainsNode(containerNode, range.commonAncestorContainer)) {
				range.deleteContents();
				range.insertNode(node);
			} else {
				containerNode.appendChild(node);
			}
		}
	} else if (document.selection && document.selection.createRange) {
		range = document.selection.createRange();
		if (isOrContainsNode(containerNode, range.parentElement())) {
			html = (node.nodeType == 3) ? node.data : node.outerHTML;
			range.pasteHTML(html);
		} else {
			containerNode.appendChild(node);
		}
	}
}

/*
// Catch clicks on variables
$(document).on("mousedown", ".variable", function(event) {
	var savedSel = rangy.saveSelection();
	var clickedvariable = $(this).get(0);
	var divWithFocus = $("div:focus").get(0);
	// Confirm that divWithFocus isn't a parent of clickedvariable
	if (clickedvariable.parentElement != divWithFocus) {
		event.preventDefault();
		insertNodeOverSelection(clickedvariable.cloneNode(true), divWithFocus);
		rangy.restoreSelection(savedSel);
	}
});
*/
function tidyQuestions() {
	$("div.question").each(function(ix) {
		highlightSyntax($(this).find(".editable"));
		$(this).find(".question_no").text((ix + 1).toString());
	});
}

// Catch keyups within questions for highlighting
$(document).on("keyup", "div.editable", function() {
	highlightSyntax($(this));
});

$(document).on("keydown", "div.editable", function(event) {
	if (event.keyCode == 13) {
		// Carriage return
		q = $(this).parent(".question").clone();
		q.find(".editable").text("");
		q.find(".answer").text("");
		$(this).parent(".question").after(q);
		q.find(".editable").focus();
		tidyQuestions();
		event.preventDefault();
	} else if (event.keyCode == 38) {
		// Up arrow
		$(this).parent().prev().find(".editable").focus();
		event.preventDefault();
	} else if (event.keyCode == 40) {
		// Down arrow
		$(this).parent().next().find(".editable").focus();
		event.preventDefault();
	}
});

$(document).on("focusout", "div.editable", function(event) {
	if (($.trim($(this).text()).length == 0)) {
		// Its empty, delete it unless it is the last one
		if ($(this).parent().next(".question").length != 0) {
			$(this).parent(".question").remove();
		} else { // It is the last one, so clear the answer
			$(this).parent(".question").find(".answer").html("");
		}
	} else {

	var toSend = {
		"worksheetId" : $("body").attr("data-worksheet-id"),
		"questions" : {}
	};
	var question = $(this).parent();
	do {
		var qText = $.trim(question.find(".editable").text());
		if (qText.length > 0) {
			toSend.questions[$.trim(question.find(".question_no").text())] = qText;
		} else {
			question.find(".answer").html("");
		}
		question = question.next();
	} while (question.length > 0);
	$.ajax({
		type : "POST",
		url : "/ws",
		data : JSON.stringify(toSend),
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		success : function(response) {
			variables = response.variables;
			$(".question").each(function(qIx) {
				if (response.answers[(qIx + 1).toString()]) {
					$(this).find(".answer").html(response.answers[(qIx + 1).toString()]);
				}
			});
			tidyQuestions();
		}
	});
	}
});

$(window).load(function() {
	// Grab a list of variables from the server
	$.ajax({
		type : "POST",
		url : "/ws",
		data : JSON.stringify({
			"worksheetId" : $("body").attr("data-worksheet-id"),
			"getRecognizedWords" : true
		}),
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		success : function(response) {
			recognizedWords = response.recognizedWords;
			variables = response.variables;
			tidyQuestions();
		}
	});

	$("div.editable").last().focus();
});