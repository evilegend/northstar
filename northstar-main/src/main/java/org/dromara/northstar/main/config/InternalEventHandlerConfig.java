package org.dromara.northstar.main.config;

import java.util.concurrent.ConcurrentMap;

import org.dromara.northstar.common.event.InternalEventBus;
import org.dromara.northstar.data.IGatewayRepository;
import org.dromara.northstar.domain.account.TradeDayAccount;
import org.dromara.northstar.domain.account.TradeDayAccountFactory;
import org.dromara.northstar.domain.gateway.GatewayAndConnectionManager;
import org.dromara.northstar.gateway.api.IContractManager;
import org.dromara.northstar.gateway.api.utils.MarketDataRepoFactory;
import org.dromara.northstar.gateway.sim.trade.SimMarket;
import org.dromara.northstar.main.handler.internal.AccountHandler;
import org.dromara.northstar.main.handler.internal.ConnectionHandler;
import org.dromara.northstar.main.handler.internal.MailBindedEventHandler;
import org.dromara.northstar.main.handler.internal.MarketDataHandler;
import org.dromara.northstar.main.handler.internal.ModuleManager;
import org.dromara.northstar.main.handler.internal.SimMarketHandler;
import org.dromara.northstar.main.mail.MailDeliveryManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
class InternalEventHandlerConfig {
	
	///////////////////
	/* Internal类事件 */
	///////////////////
	@Bean
	AccountHandler accountEventHandler(InternalEventBus eventBus, IContractManager contractMgr,
			ConcurrentMap<String, TradeDayAccount> accountMap, GatewayAndConnectionManager gatewayConnMgr) {
		AccountHandler handler = new AccountHandler(accountMap, new TradeDayAccountFactory(gatewayConnMgr, contractMgr));
		log.debug("注册：AccountHandler");
		eventBus.register(handler);
		return handler;
	}
	
	@Bean
	ConnectionHandler connectionEventHandler(InternalEventBus eventBus, GatewayAndConnectionManager gatewayConnMgr,
			IContractManager contractMgr, IGatewayRepository gatewayRepo) {
		ConnectionHandler handler = new ConnectionHandler(gatewayConnMgr, contractMgr, gatewayRepo);
		log.debug("注册：ConnectionHandler");
		eventBus.register(handler);
		return handler;
	}
	
	@Bean
	SimMarketHandler simMarketHandler(InternalEventBus eventBus, SimMarket market) {
		SimMarketHandler handler = new SimMarketHandler(market);
		log.debug("注册：SimMarketHandler");
		eventBus.register(handler);
		return handler;
	}
	
	@Bean
	ModuleManager moduleManager(InternalEventBus eventBus) {
		ModuleManager moduleMgr = new ModuleManager();
		log.debug("注册：ModuleManager");
		eventBus.register(moduleMgr);
		return moduleMgr;
	}
	
	@Bean 
	MarketDataHandler marketDataHandler(MarketDataRepoFactory mdRepoFactory, InternalEventBus eventBus) {
		MarketDataHandler mdHandler = new MarketDataHandler(mdRepoFactory);
		log.debug("注册：MarketDataHandler");
		eventBus.register(mdHandler);
		return mdHandler;
	}
	
	@Bean
	MailBindedEventHandler mailBindedEventHandler(MailDeliveryManager mailMgr, InternalEventBus eventBus) {
		MailBindedEventHandler handler = new MailBindedEventHandler(mailMgr);
		log.debug("注册：MailBindedEventHandler");
		eventBus.register(handler);
		return handler;
	}
	//////////////////////
	/* Internal类事件结束 */
	//////////////////////
	
}
