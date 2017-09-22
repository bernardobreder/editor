package project.filesystem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class FSFile extends FSResource {

	public FSFile(FSFolder parent, File file) {
		super(parent, file);
	}

	public byte[] readContent() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FileInputStream in = new FileInputStream(file);
		try {
			byte[] bytes = new byte[1024];
			for (int n; (n = in.read(bytes)) != -1;) {
				out.write(bytes, 0, n);
			}
		} finally {
			in.close();
		}
		return out.toByteArray();
	}

	public String readText() throws IOException {
		return new String(readContent(), Charset.forName("utf-8"));
	}

	public void storeContent(byte[] bytes) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		try {
			out.write(bytes);
		} finally {
			out.close();
		}
	}

	public void storeText(String text) throws IOException {
		storeContent(text.getBytes(Charset.forName("utf-8")));
	}

	@Override
	public boolean isFile() {
		return true;
	}

}
