package com.lastcalc.parsers.meta;

import java.io.*;
import java.net.*;
import java.util.Collection;

import com.lastcalc.*;
import com.lastcalc.Tokenizer.QuotedString;
import com.lastcalc.cache.ObjectCache;
import com.lastcalc.parsers.*;

public class ImportParser extends Parser {

	private static TokenList template = TokenList.createD("import", QuotedString.class);

	@Override
	public TokenList getTemplate() {
		return template;
	}
	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos, final ParserContext context) {
		if (context.importDepth > 5)
			return ParseResult.fail();
		URL url;
		try {
			url = new URL(((QuotedString) tokens.get(templatePos + 1)).value);
		} catch (final MalformedURLException e) {
			return ParseResult.fail();
		}

		CachedImport cached = ObjectCache.getSlow(1000l * 60l * 60l * 24l, url);

		if (cached == null ||
				(System.currentTimeMillis() > cached.expires && System.currentTimeMillis() - cached.currentAt > 60l*1000l)) {
			// More than a minute ago and after expires time, let's check to see
			// whether the original URL is updated
			try {
				final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestProperty("Accept", "text/plain");
				if (cached != null && cached.lastModifiedHeader != null) {
					connection.setRequestProperty("If-Modified-Since", cached.lastModifiedHeader);
				}
				if (cached != null && cached.eTag != null) {
					connection.setRequestProperty("If-None-Match", cached.eTag);
				}
				final int code = connection.getResponseCode();
				if (code == 304) {
					// It was cached, update
					cached.currentAt = System.currentTimeMillis();
					ObjectCache.put(1000l * 60l * 60l * 24l, cached, url);
				} else {
					final String contentType = connection.getContentType();
					if (contentType.toLowerCase().contains("text/plain")) {
						cached = new CachedImport();
						final BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						final ParserContext pc = new ParserContext(context.parseEngine, context.timeout, context.importDepth + 1);
						final SequentialParser sp = SequentialParser.create(pc);
						while (true) {
							final String nextLine = br.readLine();
							if (nextLine == null) {
								break;
							}
							sp.parseNext(nextLine);
						}

						cached = new CachedImport();

						cached.userDefinedParsers = sp.getUserDefinedParsers().getParsers();
						cached.currentAt = System.currentTimeMillis();
						cached.lastModifiedHeader = connection.getHeaderField("Last-Modified");
						cached.eTag = connection.getHeaderField("ETag");
						cached.expires = connection.getHeaderFieldDate("Expires", 0);
						ObjectCache.put(1000l * 60l * 60l * 24l, cached, url);

					} else
						return ParseResult.fail();
				}

			} catch (final IOException e) {
				return ParseResult.fail();
			}
		}

		return ParseResult.success(tokens.replaceWithTokens(templatePos, template.size(), cached.userDefinedParsers));
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean equals(final Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	public static class CachedImport implements Serializable {
		private static final long serialVersionUID = -3304707639237443710L;
		public long currentAt, expires;

		public String eTag;

		public String lastModifiedHeader;

		public Collection<Parser> userDefinedParsers;
	}
}

