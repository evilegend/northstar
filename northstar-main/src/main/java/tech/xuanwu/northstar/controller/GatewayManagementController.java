package tech.xuanwu.northstar.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tech.xuanwu.northstar.common.constant.GatewayUsage;
import tech.xuanwu.northstar.common.model.GatewayDescription;
import tech.xuanwu.northstar.controller.common.ResultBean;
import tech.xuanwu.northstar.engine.broadcast.SocketIOMessageEngine;
import tech.xuanwu.northstar.model.ContractManager;
import tech.xuanwu.northstar.service.GatewayService;

@RequestMapping("/mgt")
@RestController
public class GatewayManagementController {
	
	@Autowired
	protected GatewayService gatewayService;
	
	@Autowired
	protected ContractManager contractMgr;
	
	@Autowired
	protected SocketIOMessageEngine msgEngine;

	@PostMapping("/gateway")
	public ResultBean<Boolean> create(@RequestBody GatewayDescription gd) {
		Assert.notNull(gd, "传入对象不能为空");
		return new ResultBean<>(gatewayService.createGateway(gd));
	}
	
	@DeleteMapping("/gateway")
	public ResultBean<Boolean> remove(String gatewayId) {
		Assert.notNull(gatewayId, "网关ID不能为空");
		return new ResultBean<>(gatewayService.deleteGateway(gatewayId));
	}
	
	@PutMapping("/gateway")
	public ResultBean<Boolean> modify(@RequestBody GatewayDescription gd) {
		Assert.notNull(gd, "传入对象不能为空");
		return new ResultBean<>(gatewayService.updateGateway(gd));
	}
	
	@GetMapping("/gateway")
	public ResultBean<List<GatewayDescription>> list(String usage) { 
		if(StringUtils.isBlank(usage)) {
			return new ResultBean<>(gatewayService.findAllGateway());
		}
		if(GatewayUsage.valueOf(usage) == GatewayUsage.MARKET_DATA) {
			return new ResultBean<>(gatewayService.findAllMarketGateway());
		}
		return new ResultBean<>(gatewayService.findAllTraderGateway());
	}
	
	@GetMapping("/connection")
	public ResultBean<Boolean> connect(String gatewayId) {
		Assert.notNull(gatewayId, "网关ID不能为空");
		return new ResultBean<>(gatewayService.connect(gatewayId));
	}
	
	@DeleteMapping("/connection")
	public ResultBean<Boolean> disconnect(String gatewayId) {
		Assert.notNull(gatewayId, "网关ID不能为空");
		return new ResultBean<>(gatewayService.disconnect(gatewayId));
	}
	
	@GetMapping("/contracts/async")
	public ResultBean<Boolean> syncContracts() throws Exception {
		return new ResultBean<>(gatewayService.asyncUpdateContracts(contractMgr, msgEngine));
	}
}
