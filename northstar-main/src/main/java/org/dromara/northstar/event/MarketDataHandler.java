package org.dromara.northstar.event;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.dromara.northstar.common.constant.ChannelType;
import org.dromara.northstar.common.event.AbstractEventHandler;
import org.dromara.northstar.common.event.GenericEventHandler;
import org.dromara.northstar.common.event.NorthstarEvent;
import org.dromara.northstar.common.event.NorthstarEventType;
import org.dromara.northstar.gateway.common.utils.MarketDataRepoFactory;

import xyz.redtorch.pb.CoreField.BarField;

/**
 * 处理K线数据持久化
 * @author KevinHuangwl
 *
 */
public class MarketDataHandler extends AbstractEventHandler implements GenericEventHandler{

	private MarketDataRepoFactory mdRepoFactory;
	
	private ThreadPoolExecutor exec = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(500));
	
	public MarketDataHandler(MarketDataRepoFactory mdRepoFactory) {
		this.mdRepoFactory = mdRepoFactory;
	}
	
	@Override
	public boolean canHandle(NorthstarEventType eventType) {
		return eventType == NorthstarEventType.BAR;
	}

	@Override
	protected void doHandle(NorthstarEvent e) {
		if(e.getData() instanceof BarField bar && System.currentTimeMillis() - bar.getActionTimestamp() < 120000) {
			ChannelType channelType = ChannelType.valueOf(bar.getGatewayId());
			if(channelType != ChannelType.SIM) {
				exec.execute(() -> mdRepoFactory.getInstance(channelType).insert(bar)); 
			}
		}
	}

}
