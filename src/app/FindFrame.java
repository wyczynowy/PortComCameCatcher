package app;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FindFrame implements Runnable {
	
	private LocalTime localTime;
	
	public FindFrame() {
		localTime = LocalTime.from(LocalDateTime.now());
	}

	@Override
	public void run() {
		while(true) {
			if(FirstTabContainer.searchButton.isSelected()) {
				if(ReceiveData.isNewFrameRedyToRead()) {
					List<Integer> newFrameList = new ArrayList<Integer>();
					List<Integer> searchingFrameList = new ArrayList<Integer>();
					
					newFrameList.addAll(ReceiveData.getNewFrame());
					searchingFrameList.addAll(FirstTabContainer.intFrameToFind);
					
					int newFrameLength = newFrameList.size();
					int searchingFrameLength = searchingFrameList.size();
					
					if(newFrameLength == searchingFrameLength) {
						
						int correctByteCntr = 0;
						for(int i = 0; i < newFrameLength; i ++) {
							if(newFrameList.get(i).equals(searchingFrameList.get(i))) {
								correctByteCntr++;
							}
						}
						if(correctByteCntr == newFrameLength) {
							localTime = LocalTime.from(LocalDateTime.now());
							FirstTabContainer.consoleArea.append("Frame found: ");
							FirstTabContainer.consoleArea.append("" + localTime);
							FirstTabContainer.consoleArea.append("\r\n");
						}	
					}
				}
			}

			try {
				TimeUnit.MILLISECONDS.sleep(1);
			} catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
}
