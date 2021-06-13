package tech.xuanwu.northstar.strategy.cta.module;

import tech.xuanwu.northstar.strategy.common.ModuleAccount;
import xyz.redtorch.pb.CoreField.AccountField;

public class CtaModuleAccount implements ModuleAccount{
	
	private final double accountShare;
	
	private volatile AccountField account;

	public CtaModuleAccount(double share) {
		accountShare = share;
	}

	@Override
	public void updateAccount(AccountField account) {
		this.account = account;
	}
}
