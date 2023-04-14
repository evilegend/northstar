package org.dromara.northstar.domain.module;

import static org.assertj.core.api.Assertions.assertThat;

import org.dromara.northstar.common.constant.ClosingPolicy;
import org.dromara.northstar.common.constant.SignalOperation;
import org.dromara.northstar.domain.module.PriorBeforeAndHedgeTodayClosingStrategy;
import org.dromara.northstar.strategy.api.ClosingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import test.common.TestFieldFactory;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.PositionField;

class PriorBeforeAndHedgeTodayClosingStrategyTest {
	
	ClosingStrategy cs = new PriorBeforeAndHedgeTodayClosingStrategy();
	
	TestFieldFactory factory = new TestFieldFactory("testGateway");
	PositionField pf1, pf2;
	
	@BeforeEach
	void prepare() {
		ContractField contract = factory.makeContract("rb2210"); 
		
		pf1 = PositionField.newBuilder()
				.setContract(contract)
				.setPosition(4)
				.setTdPosition(2)
				.setYdPosition(2)
				.build();
		
		pf2 = PositionField.newBuilder()
				.setContract(contract)
				.setPosition(2)
				.setTdPosition(2)
				.build();
	}

	@Test
	void testResolveOperation() {
		assertThat(cs.resolveOperation(SignalOperation.BUY_OPEN, null)).isEqualTo(OffsetFlagEnum.OF_Open);
		assertThat(cs.resolveOperation(SignalOperation.BUY_OPEN, pf1)).isEqualTo(OffsetFlagEnum.OF_Close);
		assertThat(cs.resolveOperation(SignalOperation.BUY_CLOSE, pf1)).isEqualTo(OffsetFlagEnum.OF_Close);
		assertThat(cs.resolveOperation(SignalOperation.BUY_CLOSE, pf2)).isEqualTo(OffsetFlagEnum.OF_Open);
	}

	@Test
	void testGetClosingPolicy() {
		assertThat(cs.getClosingPolicy()).isEqualTo(ClosingPolicy.PRIOR_BEFORE_HEGDE_TODAY);
	}

}
