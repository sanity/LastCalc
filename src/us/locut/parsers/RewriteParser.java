package us.locut.parsers;

import java.util.*;

import com.google.common.collect.Lists;

public class RewriteParser extends Parser {

	private static final long serialVersionUID = 7536991519287899564L;
	private final ArrayList<Object> template;
	private final ArrayList<Object> to;

	public RewriteParser(final Object from, final Object to) {
		if (from instanceof ArrayList) {
			template = (ArrayList<Object>) from;
		} else {
			template = Lists.newArrayList(from);
		}
		if (to instanceof ArrayList) {
			this.to = (ArrayList<Object>) to;
		} else {
			this.to = Lists.newArrayList(to);
		}
	}

	@Override
	public ArrayList<Object> getTemplate() {
		return template;
	}

	@Override
	public ParseResult parse(final List<Object> tokens, final int templatePos) {
		return ParseResult.success(createResponse(tokens, templatePos, to.toArray()));
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
