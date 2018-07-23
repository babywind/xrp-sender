package com.trade.sender.service;

import com.quqian.framework.service.Service;
import com.trade.sender.entity.Lsqbdz;

public abstract interface XrpManage extends Service {

	/**
	 * XRP转入热钱包
	 * 
	 * @throws Throwable
	 */
	void xrp_rqb(Lsqbdz l) throws Throwable;
	
	/**
	 *  XRP转入冷钱包
	 * 
	 * @throws Throwable
	 */
	void xrp_lqb() throws Throwable;

	/**
	 * XRP转账记录
	 * @throws Throwable
	 */
	Lsqbdz[] getZrjl() throws Throwable;
}
