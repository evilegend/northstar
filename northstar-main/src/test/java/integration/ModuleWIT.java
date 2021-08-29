package integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.corundumstudio.socketio.SocketIOServer;

import common.TestMongoUtils;
import tech.xuanwu.northstar.NorthstarApplication;
import tech.xuanwu.northstar.common.model.GatewayDescription;
import tech.xuanwu.northstar.domain.GatewayConnection;
import tech.xuanwu.northstar.gateway.api.TradeGateway;
import tech.xuanwu.northstar.manager.GatewayAndConnectionManager;
import tech.xuanwu.northstar.restful.ModuleController;
import tech.xuanwu.northstar.strategy.common.constants.ModuleType;
import tech.xuanwu.northstar.strategy.common.model.ModuleInfo;
import tech.xuanwu.northstar.strategy.common.model.meta.ComponentAndParamsPair;
import tech.xuanwu.northstar.strategy.common.model.meta.ComponentMetaInfo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NorthstarApplication.class, value="spring.profiles.active=test")
public class ModuleWIT {
	
	@Autowired
	private ModuleController ctrlr;

	@MockBean
	private GatewayAndConnectionManager gatewayConnMgr;
	
	@MockBean
	private SocketIOServer server;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		TestMongoUtils.clearDB();
	}
	
	// 获取模组交易策略元配置
	@Test
	public void shouldHaveSampleDealer() {
		List<ComponentMetaInfo> dealers = ctrlr.getRegisteredDealers().getData();
		assertThat(dealers.stream().filter(c -> c.getName().equals("示例交易策略")).findAny().isPresent()).isTrue();
	}
	// 获取模组信号策略元配置
	@Test
	public void shouldHaveSamplePolicy() {
		List<ComponentMetaInfo> signalPolicies = ctrlr.getRegisteredSignalPolicies().getData();
		assertThat(signalPolicies.stream().filter(c -> c.getName().equals("示例策略")).findAny().isPresent()).isTrue();
	}
	// 获取模组风控策略元配置
	@Test
	public void shouldHaveBasicRiskRules() {
		List<ComponentMetaInfo> rules = ctrlr.getRegisteredRiskControlRules().getData();
		assertThat(rules.stream().filter(c -> c.getName().equals("模组占用账户资金限制")).findAny().isPresent()).isTrue();
		assertThat(rules.stream().filter(c -> c.getName().equals("日内开仓次数限制")).findAny().isPresent()).isTrue();
		assertThat(rules.stream().filter(c -> c.getName().equals("委托超价限制")).findAny().isPresent()).isTrue();
		assertThat(rules.stream().filter(c -> c.getName().equals("委托超时限制")).findAny().isPresent()).isTrue();
	}

	// 新增模组
	@Test
	public void shouldSuccessfullyCreate() throws Exception {
		GatewayConnection conn = mock(GatewayConnection.class);
		GatewayDescription gwDes = mock(GatewayDescription.class);
		when(gwDes.getBindedMktGatewayId()).thenReturn("testGw");
		when(conn.getGwDescription()).thenReturn(gwDes);
		when(gatewayConnMgr.getGatewayConnectionById("testGateway")).thenReturn(conn);
		when(gatewayConnMgr.getGatewayById("testGateway")).thenReturn(mock(TradeGateway.class));
		ComponentMetaInfo dealer = ctrlr.getRegisteredDealers().getData().stream().filter(c -> c.getName().equals("示例交易策略")).findAny().get();
		ComponentMetaInfo signalPolicy = ctrlr.getRegisteredSignalPolicies().getData().stream().filter(c -> c.getName().equals("示例策略")).findAny().get();
		ComponentAndParamsPair signalPolicyMeta = ComponentAndParamsPair.builder()
				.componentMeta(signalPolicy)
				.initParams(ctrlr.getComponentParams(signalPolicy).getData().values().stream().collect(Collectors.toList()))
				.build();
		ComponentAndParamsPair dealerMeta = ComponentAndParamsPair.builder()
				.componentMeta(dealer)
				.initParams(ctrlr.getComponentParams(dealer).getData().values().stream().collect(Collectors.toList()))
				.build();
		ModuleInfo info = ModuleInfo.builder()
				.moduleName("testModule")
				.enabled(true)
				.type(ModuleType.CTA)
				.accountGatewayId("testGateway")
				.signalPolicy(signalPolicyMeta)
				.dealer(dealerMeta)
				.riskControlRules(Collections.EMPTY_LIST)
				.build();
		
		assertThat(ctrlr.createModule(info).getData()).isTrue();
	}
	
	
	// 修改模组
	@Test
	public void shouldSuccessfullyModify() throws Exception {
		GatewayConnection conn = mock(GatewayConnection.class);
		GatewayDescription gwDes = mock(GatewayDescription.class);
		when(gwDes.getBindedMktGatewayId()).thenReturn("testGw");
		when(conn.getGwDescription()).thenReturn(gwDes);
		when(gatewayConnMgr.getGatewayConnectionById("testGateway")).thenReturn(conn);
		when(gatewayConnMgr.getGatewayById("testGateway")).thenReturn(mock(TradeGateway.class));
		ComponentMetaInfo dealer = ctrlr.getRegisteredDealers().getData().stream().filter(c -> c.getName().equals("示例交易策略")).findAny().get();
		ComponentMetaInfo signalPolicy = ctrlr.getRegisteredSignalPolicies().getData().stream().filter(c -> c.getName().equals("示例策略")).findAny().get();
		ComponentAndParamsPair signalPolicyMeta = ComponentAndParamsPair.builder()
				.componentMeta(signalPolicy)
				.initParams(ctrlr.getComponentParams(signalPolicy).getData().values().stream().collect(Collectors.toList()))
				.build();
		ComponentAndParamsPair dealerMeta = ComponentAndParamsPair.builder()
				.componentMeta(dealer)
				.initParams(ctrlr.getComponentParams(dealer).getData().values().stream().collect(Collectors.toList()))
				.build();
		ModuleInfo info = ModuleInfo.builder()
				.moduleName("testModule")
				.enabled(false)
				.type(ModuleType.CTA)
				.accountGatewayId("testGateway")
				.signalPolicy(signalPolicyMeta)
				.dealer(dealerMeta)
				.riskControlRules(Collections.EMPTY_LIST)
				.build();
		
		ctrlr.createModule(info);
		info.setEnabled(true);
		assertThat(ctrlr.updateModule(info).getData()).isTrue();
	}
	
	// 查询模组
	@Test
	public void shouldFindModule() {
		assertThat(ctrlr.getAllModules().getData()).isNotNull();
	}
	
	// 查询模组历史
	
}
