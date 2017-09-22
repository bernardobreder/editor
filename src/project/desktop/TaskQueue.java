package project.desktop;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

public class TaskQueue implements Runnable {
	
	private final LinkedList<Task> readList = new LinkedList<Task>();
	
	private final LinkedList<Task> writeList = new LinkedList<Task>();
	
	private final ConcurrentLinkedDeque<Task> executingList = new ConcurrentLinkedDeque<Task>();
	
	private TaskQueueListener listener;
	
	private final Thread thread;
	
	private boolean quit;
	
	public TaskQueue() {
		thread = new Thread(this, "Task Queue");
	}
	
	@Override
	public void run() {
		List<TaskThread> threads = new ArrayList<TaskThread>();
		try {
			while (!quit) {
				boolean executed = false;
				for (;;) {
					Task task = popRead();
					if (task == null) {
						break;
					}
					executed = true;
					do {
						TaskThread thread = new TaskThread(task);
						threads.add(thread);
						thread.start();
						task = popRead();
					} while (task != null);
					for (TaskThread thread : threads) {
						thread.join();
					}
				}
				threads.clear();
				{
					Task task = popWrite();
					while (task != null) {
						executed = true;
						TaskThread thread = new TaskThread(task);
						thread.start();
						thread.join();
						task = popWrite();
					}
				}
				if (!executed) {
					Thread.sleep(10);
				}
			}
		} catch (InterruptedException e) {
			return;
		}
	}
	
	protected synchronized Task popRead() {
		return readList.isEmpty() ? null : readList.removeFirst();
	}
	
	protected synchronized Task popWrite() {
		return writeList.isEmpty() ? null : writeList.removeFirst();
	}
	
	public TaskQueue start() {
		thread.start();
		return this;
	}
	
	public TaskQueue stop() {
		quit = true;
		thread.interrupt();
		return this;
	}
	
	public void pushWrite(Task runnable) {
		writeList.addLast(runnable);
	}
	
	public void pushRead(Task runnable) {
		readList.addLast(runnable);
	}
	
	public TaskQueueListener getListener() {
		return listener;
	}
	
	public TaskQueue setListener(TaskQueueListener listener) {
		this.listener = listener;
		return this;
	}
	
	public ConcurrentLinkedDeque<Task> getExecutingList() {
		return executingList;
	}
	
	public static <E> E execute(final TaskQuery<E> task) {
		final AtomicReference<E> ref = new AtomicReference<E>();
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				
				@Override
				public void run() {
					ref.set(task.execute());
				}
			});
			return ref.get();
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			return null;
		}
	}
	
	public static void execute(final TaskLater task) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				task.execute();
			}
		});
	}
	
	public class TaskThread extends Thread {
		
		private Task task;
		
		public TaskThread(Task task) {
			super("Task Processor");
			this.task = task;
		}
		
		@Override
		public void run() {
			executingList.push(task);
			if (listener != null) {
				listener.onReadEventStarted(task);
			}
			try {
				task.perform();
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						task.updateUI();
					}
				});
			} catch (final Exception e) {
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						e.printStackTrace();
					}
				});
			} finally {
				executingList.remove(task);
				if (listener != null) {
					listener.onReadEventFinished(task);
				}
			}
		}
		
	}
	
	public interface Task {
		
		public String getTitle();
		
		public void perform() throws Exception;
		
		public void updateUI();
		
	}
	
	public interface TaskQueueListener {
		
		public void onReadEventStarted(Task task);
		
		public void onReadEventFinished(Task task);
		
		public void onWriteEventStarted(Task task);
		
		public void onWriteEventFinished(Task task);
		
	}
	
	public interface TaskQuery<E> {
		
		public E execute();
		
	}
	
	public interface TaskLater {
		
		public void execute();
		
	}
	
}
