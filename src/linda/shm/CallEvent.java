package linda.shm;

import linda.Callback;
import linda.Linda.eventMode;

public class CallEvent{
private Callback callback;
private eventMode event;

public CallEvent(Callback c, eventMode e){
	this.callback = c;
	this.event = e;
}

public Callback getCallback() {
	return callback;
}

public void setCallback(Callback callback) {
	this.callback = callback;
}

public eventMode getEvent() {
	return event;
}

public void setEvent(eventMode event) {
	this.event = event;
}
}
