package org.dromara.northstar.main.engine.event.handler;

import java.util.HashSet;
import java.util.Set;

import org.dromara.northstar.common.event.NorthstarEvent;
import org.dromara.northstar.common.event.NorthstarEventType;
import org.dromara.northstar.common.event.FastEventEngine.NorthstarEventDispatcher;
import org.dromara.northstar.main.handler.broadcast.SocketIOMessageEngine;

public class BroadcastDispatcher implements NorthstarEventDispatcher {
	
	private SocketIOMessageEngine msgEngine;
	
	private static Set<NorthstarEventType> eventSet = new HashSet<>() {
		private static final long serialVersionUID = 1L;
		{
			add(NorthstarEventType.TICK);
			add(NorthstarEventType.BAR);
			add(NorthstarEventType.ACCOUNT);
			add(NorthstarEventType.ORDER);
			add(NorthstarEventType.POSITION);
			add(NorthstarEventType.TRADE);
			add(NorthstarEventType.NOTICE);
		}
	};
	
	public BroadcastDispatcher(SocketIOMessageEngine msgEngine) {
		this.msgEngine = msgEngine;
	}

	@Override
	public void onEvent(NorthstarEvent event, long sequence, boolean endOfBatch) throws Exception {
		if(!eventSet.contains(event.getEvent())) {
			return;
		}
		msgEngine.emitEvent(event);
	}

}
