package us.locut.reducers.amounts;

import java.util.*;

import javax.measure.converter.ConversionException;

import org.jscience.physics.amount.Amount;

import us.locut.reducers.Parser;

import com.google.appengine.repackaged.com.google.common.collect.*;

public abstract class AmountMathOp extends Parser {

	private final String description;
	private final ArrayList<Object> template;

	public AmountMathOp(final String operator, final String description) {
		this.description = description;
		template = Lists.<Object> newArrayList(Amount.class, operator, Amount.class);
	}

	@Override
	public ParseResult reduce(final ArrayList<Object> tokens, final int templatePos) {
		final Amount<?> a = (Amount<?>) tokens.get(templatePos);
		final Amount<?> b = (Amount<?>) tokens.get(templatePos + 2);
		try {
			return new Parser.ParseResult(createResponse(tokens, templatePos, operation(a, b)), description);
		} catch (final ConversionException ce) {
			return new Parser.ParseResult(null, ce.getMessage());
		}
	}

	protected abstract Amount<?> operation(final Amount<?> a, final Amount<?> b) throws ConversionException;

	@Override
	public ArrayList<Object> getTemplate() {
		return template;
	}

	public static Set<AmountMathOp> getOps() {
		final Set<AmountMathOp> ops = Sets.newHashSet();
		ops.add(new AmountMathOp("+", "Add") {

			@Override
			protected Amount<?> operation(final Amount<?> a, final Amount<?> b) throws ConversionException {
				return a.plus(b);
			}
		});
		ops.add(new AmountMathOp("-", "Subtract") {

			@Override
			protected Amount<?> operation(final Amount<?> a, final Amount<?> b) throws ConversionException {
				return a.minus(b);
			}
		});
		ops.add(new AmountMathOp("*", "Multiply") {

			@Override
			protected Amount<?> operation(final Amount<?> a, final Amount<?> b) throws ConversionException {
				return a.times(b);
			}
		});
		ops.add(new AmountMathOp("/", "Divide") {

			@Override
			protected Amount<?> operation(final Amount<?> a, final Amount<?> b) throws ConversionException {
				return a.divide(b);
			}
		});
		return ops;
	}

}