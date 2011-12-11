var tokens = {
	"alphabet" : "token blue",
	"emu" : "token green"
};

// Set up token highlighting
function tokenifier(parent) {
			parent.on(
					"keyup",
					function() {
						var savedSel = rangy.saveSelection();
						$(this).find("span.pill").replaceWith(function() {
							return $(this).contents();
						});
						var regexpStr;
						var oThis = $(this);
						$.each(tokens, function(tokenRX, classes) {
							var rgx = new RegExp("(^|[^\\w>])(" + tokenRX
									+ ")([^\\w])", "g");
							oThis.html(oThis.html().replace(
									rgx,
									"$1<span class=\"pill " + classes
											+ "\">$2</span>$3"));
						});
						rangy.restoreSelection(savedSel);
					});
}

// Set up token clicking
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

$(document).on("mousedown", ".token", function(event) {
	var savedSel = rangy.saveSelection();
	var clickedToken = $(this).get(0);
	var divWithFocus = $("div:focus").get(0);
	// Confirm that divWithFocus isn't a parent of clickedToken
	if (clickedToken.parentElement != divWithFocus) {
		event.preventDefault();
		insertNodeOverSelection(clickedToken.cloneNode(true), divWithFocus);
		rangy.restoreSelection(savedSel);
	}
});
		
$(document).ready(function () {
	tokenifier($("div.line.editable"));
});