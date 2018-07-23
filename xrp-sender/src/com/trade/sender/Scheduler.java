package com.trade.sender;

import com.quqian.framework.config.ConfigureProvider;
import com.quqian.framework.resource.ResourceProvider;
import com.quqian.framework.service.ServiceProvider;
import com.quqian.framework.service.ServiceSession;
import com.trade.sender.entity.Lsqbdz;
import com.trade.sender.service.XrpManage;

public class Scheduler extends Thread {

	protected transient boolean alive = true;
	protected final ResourceProvider resourceProvider;
	protected final ConfigureProvider configureProvider;
	protected final ServiceProvider serviceProvider;
	protected static int EXPIRES_TOKEN_TIME = 0;

	public Scheduler(ResourceProvider resourceProvider) {
		this.resourceProvider = resourceProvider;
		this.configureProvider = resourceProvider.getResource(ConfigureProvider.class);
		this.serviceProvider = resourceProvider.getResource(ServiceProvider.class);
	}

	@Override
	public void run() {
		while (alive) {
			try {
				xrp_rqb();
				xrp_lqb();
				
				sleep(1000 * 60 * 5);
			} catch (InterruptedException e) {
				alive = false;
				break;
			}
		}
	}

	/**
	 * XRP转入热钱包
	 */
	private void xrp_rqb() {
		try (ServiceSession serviceSession = serviceProvider.createServiceSession()) {
			XrpManage manage = serviceSession.getService(XrpManage.class);
			Lsqbdz[] ls = manage.getZrjl();
			if (ls != null) {
				for (Lsqbdz l : ls) {
					serviceSession.openTransactions();
					try {
						manage.xrp_rqb(l);
						serviceSession.commit();
					} catch (Exception e) {
						e.printStackTrace();
						serviceSession.rollback();
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			resourceProvider.log(e);
		}

	}

	/**
	 * XRP转入冷钱包
	 */
	private void xrp_lqb() {
		try (ServiceSession serviceSession = serviceProvider.createServiceSession()) {
			XrpManage manage = serviceSession.getService(XrpManage.class);
			serviceSession.openTransactions();
			try {
				manage.xrp_lqb();
				serviceSession.commit();
			} catch (Exception e) {
				e.printStackTrace();
				serviceSession.rollback();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			resourceProvider.log(e);
		}
	}
}
