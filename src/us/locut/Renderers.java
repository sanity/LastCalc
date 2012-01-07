package us.locut;

import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;
import org.jsoup.nodes.*;
import org.jsoup.parser.Tag;

import us.locut.parsers.UserDefinedParserParser.UserDefinedParser;
import us.locut.parsers.amounts.UnitParser;

public class Renderers {


	public static Element toHtml(final String baseUri, final TokenList tokens) {
		final Element ret = new Element(Tag.valueOf("span"), baseUri);
		for (final Object obj : tokens) {
			if (obj instanceof Amount) {
				final Amount<?> amount = (Amount<?>) obj;
				final Element amountSpan = ret.appendElement("span");
				final double estimatedValue = amount.getEstimatedValue();
				final String numStr = Misc.numberFormat.format(estimatedValue);
				amountSpan.appendElement("span").addClass("number").text(numStr);
				amountSpan.appendText(" ");
				if (!amount.getUnit().equals(Unit.ONE)) {
					final Element unitSpan = amountSpan.appendElement("span").addClass("recognized");
					final String verboseName = estimatedValue == 1.0 ? UnitParser.verboseNamesSing.get(amount.getUnit())
							: UnitParser.verboseNamesPlur.get(amount.getUnit());
					if (verboseName != null) {
						unitSpan.text(verboseName);
					} else {
						unitSpan.text(amount.getUnit().toString());
					}
				}
			} else if (obj instanceof UserDefinedParser) {
				ret.appendChild(toHtml(baseUri, ((UserDefinedParser) obj).after));
			} else {
				ret.appendChild(new TextNode(" " + obj.toString() + " ", baseUri));
			}
		}
		return ret;
	}
}
