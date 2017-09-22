package project.builder.c;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;

import org.fife.util.OSProcess;
import org.fife.util.OSProcess.ProcessModel;
import org.fife.util.OSUtil;

import project.console.ConsoleInstance;
import project.filesystem.FSFile;
import project.filesystem.FSProject;

public class CBuilder {
	
	public void compile(final ConsoleInstance console, FSFile file) {
		try {
			File executableFile = getExecutableFile();
			if (executableFile.exists() && !executableFile.delete()) {
				throw new IOException("can not delete the executable file");
			}
			String projectName = file.getProject().toString();
			String name = file.toString();
			// name = name.substring(projectName.length());
			new OSProcess(new ProcessModel() {
				
				@Override
				public void receiveLine(String line, boolean isError) {
					writeETD(console, line + "\n");
				}
			}, Arrays.asList("make", name, "dist")).setFolder(file.getProject()).start().waitForExit();
		} catch (Exception e) {
			writeETD(console, "error: " + e.getMessage());
			console.write("error: " + e.getMessage());
		}
	}
	
	private List<String> createArguments(FSProject project, List<FSFile> files) {
		List<String> list = new ArrayList<String>();
		list.add("gcc");
		for (FSFile file : files) {
			list.add(file.toExternalFile().getAbsolutePath().toString());
		}
		list.add("-o");
		list.add(getExecutableFile().toString());
		return list;
	}
	
	private File getExecutableFile() {
		return new File("main" + OSUtil.getExecutableExtension());
	}
	
	protected void writeETD(final ConsoleInstance console, final String line) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				console.write(line);
			}
		});
	}
	
}
