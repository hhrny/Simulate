package Simulate;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class EventsQueue {
	private Queue<SEvent> events;
	public static Comparator<SEvent> eventComparator = new Comparator<SEvent>(){
		@Override
		public int compare(SEvent e1, SEvent e2){
			if(e1.time < e2.time){
				return -1;
			}else if(e1.time == e2.time){
				return e2.eventType - e1.eventType;
			}else{
				return 1;
			}
		}
	};
	public EventsQueue(){
		events = new PriorityQueue<>(128, eventComparator);
	}
	public void addEvent(SEvent e){
		events.add(e);
	}
	public SEvent getNextEvent(){
		return events.poll();
	}
	public boolean hasNextEvent(){
		return (events.size() != 0);
	}
	public void clear(){
		events.clear();
	}
}
