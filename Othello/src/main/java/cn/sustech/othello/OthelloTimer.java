package cn.sustech.othello;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class OthelloTimer {
	
	private HashMap<String, OthelloTicker> tickers;
	private TaskTicker tasker;
	private static OthelloTimer timer;
	
	public OthelloTimer() {
		timer = this;
		tickers = new HashMap<>();
		tasker = new TaskTicker();
		tickers.put("taskticker", tasker);
	}
	
	public void start() {
		OthelloTimerThread thread = new OthelloTimerThread(this);
		thread.setDaemon(true);
		thread.start();
	}
	
	public static void registerTicker(String key, OthelloTicker ticker) {
		timer.tickers.put(key.toLowerCase(), ticker);
	}
	
	public static void unregisterTicker(String key) {
		timer.tickers.remove(key.toLowerCase());
	}
	
	public static interface OthelloTicker {
		public void tick();
	}
	
	private static class OthelloTask {
		private long tickTime;
		private Runnable runnable;
		
		public OthelloTask(Runnable runnable, long tickTime) {
			this.runnable = runnable;
			this.tickTime = tickTime;
		}
		
	}
	
	private static class TaskTicker implements OthelloTicker {
		
		private PriorityQueue<OthelloTask> heap;
		private long ticks = 0;
		
		private TaskTicker() {
			heap = new PriorityQueue<OthelloTask>(new Comparator<OthelloTask>() {

				@Override
				public int compare(OthelloTask task1, OthelloTask task2) {
					long val = task1.tickTime - task2.tickTime;
					return (int) (val == 0 ? 0 : (val / Math.abs(val)));
				}
				
			});
		}
		
		@Override
		public void tick() {
			++ticks;
			while ((!heap.isEmpty()) && heap.peek().tickTime == ticks) {
				heap.poll().runnable.run();
			}
		}
		
	}
	
	public static void runTaskLater(Runnable runnable, long tick) {
		timer.tasker.heap.add(new OthelloTask(runnable, timer.tasker.ticks + tick));
	}
	
	public static final long INTERVAL = 50;
	
	private static class OthelloTimerThread extends Thread{
		
		private OthelloTimer timer;
		
		private OthelloTimerThread(OthelloTimer timer) {
			this.timer = timer;
		}
		
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (OthelloTicker ticker : timer.tickers.values()) {
					ticker.tick();
				}
			}
		}
		
	}
	
}
