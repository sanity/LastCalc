package com.lastcalc.db;

import java.io.*;

import com.googlecode.objectify.annotation.Unindexed;
import com.lastcalc.TokenList;

public class Line implements Serializable {
	private static final long serialVersionUID = 8234981855606438804L;

	public Line(final String question, final TokenList answer)
			throws IOException {
		this.question = question;
		this.answer = answer;
	}

	@Unindexed
	public String question;

	@Unindexed
	public TokenList answer;

	// We serialize manually to ensure that TokenLists are serialized
	// efficiently
	private void writeObject(final ObjectOutputStream out) throws IOException {
		out.writeObject(question);
		out.writeInt(answer.size());
		for (final Object o : answer) {
			out.writeObject(o);
		}
	}

	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
		question = (String) in.readObject();
		final Object[] answer = new Object[in.readInt()];
		for (int x = 0; x < answer.length; x++) {
			answer[x] = in.readObject();
		}
		this.answer = TokenList.createD(answer);
	}
}
