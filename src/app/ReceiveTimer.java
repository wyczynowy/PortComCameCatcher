package app;

import java.util.concurrent.TimeUnit;

public class ReceiveTimer implements Runnable {

	private int timer = 0;
	
	public void setTimer(int val) { timer = val; }
	public int getTimer() { return timer; }
	
	@Override
	public void run() {
		while(true) {
			if(timer > 0) timer--;
	        try {
	            TimeUnit.MILLISECONDS.sleep(1);
	          } catch(InterruptedException e) {
	            System.out.println("Timeout odbioru");
	            return;
	          }
		}
	}
	
}
