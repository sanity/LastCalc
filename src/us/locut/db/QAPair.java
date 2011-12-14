package us.locut.db;

import java.io.*;
import java.util.ArrayList;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Unindexed;

public class QAPair {
	@Id
	public long id;

	public QAPair(final int position, final String question, final ArrayList<Object> answer)
			throws IOException {
		this.position = position;
		this.question = question;
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(answer);
		oos.flush();
		this.answer = baos.toByteArray();
	}

	public int position;

	public String question;

	@Unindexed
	public byte[] answer;

	public ArrayList<Object> deserializeAnswer() throws IOException, ClassNotFoundException {
		final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(answer));
		return (ArrayList<Object>) ois.readObject();
	}
}
