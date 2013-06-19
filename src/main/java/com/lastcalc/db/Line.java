/*******************************************************************************
 * LastCalc - The last calculator you'll ever need
 * Copyright (C) 2011, 2012 Uprizer Labs LLC
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU Affero General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE.  See the GNU Affero General Public License for more 
 * details.
 ******************************************************************************/
package com.lastcalc.db;

import com.googlecode.objectify.annotation.Unindex;
import com.lastcalc.TokenList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Line implements Serializable {
	private static final long serialVersionUID = 8234981855606438804L;

	public Line(final String question, final TokenList answer)
			throws IOException {
		this.question = question;
		this.answer = answer;
	}

    public Line(final int lineNum, final String question, final TokenList answer)
            throws IOException {
        this.lineNum=lineNum;
        this.question = question;
        this.answer = answer;
    }

    public int lineNum;

	@Unindex
	public String question;

	@Unindex
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
