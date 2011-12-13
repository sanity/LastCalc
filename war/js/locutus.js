var variables = {
	"alphabet" : "variable blue",
	"emu" : "variable green",
	"andrew" : "variable red"
};

function highlightSyntax(element) {
	if (element.length != 1) {
		alert("highlightSyntax() called with "+element.length+" elements (should be 1)");
	}
	
	var savedSel = rangy.saveSelection();
	// Remove any existing variables
	element.find("span.highlighted").replaceWith(function() {
		return $(this).contents();
	});
	console.log("Scanning");
	inSpan = false;
	element.html(element.html().replace(/[0-9\.]+|[a-zA-Z0-9]+|"(?:[^"\\]|\\.)*"/g, function(str) {
		// We have to do this because of the <span> inserted by rangy - ugly but works
		if (str.match(/span/)) {
			inSpan = !inSpan;
		}
		if (inSpan) {
			return str;
		}
		if (str.match(/^"(?:[^"\\]|\\.)*"$/)) {
			return "<span class=\"highlighted quoted\">"+str+"</span>";
		}
		if (str.match(/^[0-9\.]+$/)) {
			return "<span class=\"highlighted number\">"+str+"</span>";
		}
		nc = variables[str];
		if (nc) {
			return "<span class=\"highlighted "+nc+"\">"+str+"</span>";
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

// Catch keyups within questions for highlighting
$(document).on("keyup", "div.editable", function() {
	highlightSyntax($(this));
});

$(document).on("keydown", "div.editable", function(event) {
	if (event.keyCode == 13) {
		// Carriage return
		console.log("Carriage return");
		event.preventDefault();
	} else if (event.keyCode == 38) {
		console.log("Arrow up");
		event.preventDefault();
	} else if (event.keyCode == 40) {
		// Arrow down
	}
});

$(document).on("focusout", "div.editable", function(event) {
	// $(this) and all below must be submitted to the server
});

$(document).ready(function () {
	$("#q1 div.editable").focus();
});