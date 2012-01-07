package us.locut.parsers;

import java.util.ArrayList;

import us.locut.TokenList;

public class RewriteParser extends Parser {

	private static final long serialVersionUID = 7536991519287899564L;
	private final TokenList template;
	private final TokenList to;

	public RewriteParser(final Object from, final Object to) {
		if (from instanceof ArrayList) {
			template = TokenList.create((ArrayList<Object>) from);
		} else {
			template = TokenList.createD(from);
		}
		if (to instanceof ArrayList) {
			this.to = TokenList.create((ArrayList<Object>) to);
		} else {
			this.to = TokenList.createD(to);
		}
	}

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		return ParseResult.success(tokens.replaceWithTokenList(templatePos, templatePos + template.size(), to));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((template == null) ? 0 : template.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof RewriteParser))
			return false;
		final RewriteParser other = (RewriteParser) obj;
		if (template == null) {
			if (other.template != null)
				return false;
		} else if (!template.equals(other.template))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}


}
