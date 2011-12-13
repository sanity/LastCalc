var variables = {
	"alphabet" : "variable blue",
	"emu" : "variable green",
	"andrew" : "variable red"
};

// Set up highlighting
function variableifier(parent) {
			parent.on(
					"keyup",
					function() {
						var savedSel = rangy.saveSelection();
						// Remove any existing pills
						$(this).find("span.variable").replaceWith(function() {
							return $(this).contents();
						});
						$(this).html($(this).html().replace(/[0-9,.]+|[a-zA-Z0-9]+|[\+-\/*=()\[\]]/g, function(str) {
							nc = variables[str];
							if (nc) {
								return "<span class=\""+nc+"\">"+str+"</span>";
							} else {
								return str;
							}
						}));
						rangy.restoreSelection(savedSel);
					});
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
		
$(document).ready(function () {
	variableifier($("div.question div.editable"));
	$("#q1 div.editable").focus();
});