package project.task;

import project.desktop.TaskQueue.Task;
import project.filesystem.FSFile;

public class FileSaveTask implements Task {

	private FSFile file;

	private String text;

	public FileSaveTask(FSFile file, String text) {
		this.file = file;
		this.text = text;
	}

	@Override
	public void perform() throws Exception {
		file.storeText(text);
	}

	@Override
	public void updateUI() {
	}

	@Override
	public String getTitle() {
		return "Saving file";
	}

}
