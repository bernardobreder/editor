package org.fife.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import project.filesystem.FSFolder;

public class OSProcess {

	private ProcessModel model;

	private ProcessBuilder builder;

	private Process process;

	public OSProcess(ProcessModel model, List<String> arguments) {
		this.model = model;
		builder = new java.lang.ProcessBuilder(arguments);
	}

	public OSProcess setFolder(FSFolder folder) {
		builder.directory(folder.toExternalFile());
		return this;
	}

	public OSProcess start() throws IOException {
		process = builder.start();
		new Thread(new OutputCollector(process.getInputStream(), false), "Process Input Reader").start();
		new Thread(new OutputCollector(process.getErrorStream(), false), "Process Error Reader").start();
		return this;
	}

	public int waitForExit() throws InterruptedException {
		int rc = -1;
		try {
			rc = process.waitFor();
		} catch (InterruptedException ie) {
			process.destroy();
		}
		return rc;
	}

	public static interface ProcessModel {

		public void receiveLine(String line, boolean isError);

	}

	public class OutputCollector implements Runnable {

		private InputStream in;

		private boolean isError;

		/**
		 * Constructor.
		 *
		 * @param in The input stream.
		 * @param isError
		 */
		public OutputCollector(InputStream in, boolean isError) {
			this.in = in;
			this.isError = isError;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(in, Charset.forName("utf-8")));
				try {
					String line = null;
					while ((line = r.readLine()) != null) {
						model.receiveLine(line, isError);
					}
				} finally {
					r.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

	}

}
