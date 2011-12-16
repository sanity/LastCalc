package us.locut.parsers.amounts;

import java.util.*;

import javax.measure.converter.ConversionException;

import org.jscience.physics.amount.Amount;

import us.locut.parsers.*;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public abstract class AmountMathOp extends Parser {
	private static final long serialVersionUID = -2510157348504286501L;
	private final String description;
	private final ArrayList<Object> template;

	public AmountMathOp(final String operator, final String description) {
		this.description = description;
		template = Lists.<Object> newArrayList(Amount.class, operator, Amount.class);
	}

	@Override
	public ParseResult parse(final ArrayList<Object> tokens, final int templatePos) {
		final Amount<?> a = (Amount<?>) tokens.get(templatePos);
		final Amount<?> b = (Amount<?>) tokens.get(templatePos + 2);
		try {
			return Parser.ParseResult.success(createResponse(tokens, templatePos, operation(a, b)), description);
		} catch (final ConversionException ce) {
			return Parser.ParseResult.fail();
		}
	}

	@Override
	public String toString() {
		return description;
	}

	protected abstract Amount<?> operation(final Amount<?> a, final Amount<?> b) throws ConversionException;

	@Override
	public ArrayList<Object> getTemplate() {
		return template;
	}

	public static List<Parser> getOps() {
		final List<Parser> ops = Lists.newLinkedList();

		ops.add(new AmountMathOp("^", "Raise to Power") {
			private static final long serialVersionUID = 4974989053479508332L;

			@Override
			protected Amount<?> operation(final Amount<?> a, final Amount<?> b) throws ConversionException {
				if (b.getEstimatedValue() != b.getExactValue())
					throw new ConversionException("Can only raise to an integer power");
				return a.pow((int) b.getExactValue());
			}
		});

		ops.add(new AmountMathOp("*", "Multiply") {

			private static final long serialVersionUID = 9166899968748997536L;

			@Override
			protected Amount<?> operation(final Amount<?> a, final Amount<?> b) throws ConversionException {
				return a.times(b);
			}
		});
		ops.add(new AmountMathOp("/", "Divide") {
			private static final long serialVersionUID = 4974989053479508332L;

			@Override
			protected Amount<?> operation(final Amount<?> a, final Amount<?> b) throws ConversionException {
				return a.divide(b);
			}
		});

		ops.add(new AmountMathOp("+", "Add") {
			private static final long serialVersionUID = 8999743708212969031L;

			@Override
			protected Amount<?> operation(final Amount<?> a, final Amount<?> b) throws ConversionException {
				return a.plus(b);
			}
		});
		ops.add(new AmountMathOp("-", "Subtract") {

			private static final long serialVersionUID = 7206664452245347470L;

			@Override
			protected Amount<?> operation(final Amount<?> a, final Amount<?> b) throws ConversionException {
				return a.minus(b);
			}
		});


		ops.add(new RewriteParser("plus", "+"));
		ops.add(new RewriteParser("x", "+"));
		ops.add(new RewriteParser("minus", "-"));
		ops.add(new RewriteParser("multiply", "*"));
		ops.add(new RewriteParser(Lists.newArrayList("multiplied", "by"), "*"));
		ops.add(new RewriteParser("divide", "/"));
		ops.add(new RewriteParser(Lists.newArrayList("divided", "by"), "/"));
		ops.add(new RewriteParser(Lists.newArrayList("to", "the", "power", "of"), "^"));

		return ops;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((template == null) ? 0 : template.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AmountMathOp))
			return false;
		final AmountMathOp other = (AmountMathOp) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (template == null) {
			if (other.template != null)
				return false;
		} else if (!template.equals(other.template))
			return false;
		return true;
	}

}