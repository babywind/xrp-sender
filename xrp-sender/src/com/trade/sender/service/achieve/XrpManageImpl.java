package com.trade.sender.service.achieve;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.quqian.framework.config.ConfigureProvider;
import com.quqian.framework.service.ServiceFactory;
import com.quqian.framework.service.ServiceResource;
import com.quqian.framework.service.query.ArrayParser;
import com.quqian.framework.service.query.ItemParser;
import com.quqian.p2p.common.enums.IsPass;
import com.quqian.p2p.common.enums.XlbType;
import com.quqian.p2p.variables.P2PConst;
import com.quqian.p2p.variables.defines.SystemVariable;
import com.quqian.util.Formater;
import com.quqian.util.HttpRequest;
import com.quqian.util.MyCrypt;
import com.quqian.util.SendMsg;
import com.quqian.util.StringHelper;
import com.quqian.util.parser.EnumParser;
import com.trade.sender.entity.HttpEntity;
import com.trade.sender.entity.Lsqbdz;
import com.trade.sender.entity.XrpEntity;
import com.trade.sender.service.XrpManage;

import net.sf.json.JSONObject;

public class XrpManageImpl extends AbstractXrpService implements XrpManage {
	final String bjc = "XRP"; // 币简称
	final int bid = selectId(bjc); // 币ID
	final BigDecimal CHANGE_FEE = new BigDecimal("0.000012");

	public XrpManageImpl(ServiceResource serviceResource) {
		super(serviceResource);
	}

	public static class XrpManageFactory implements ServiceFactory<XrpManage> {
		@Override
		public XrpManage newInstance(ServiceResource serviceResource) {
			return new XrpManageImpl(serviceResource);
		}
	}

	private XrpEntity getXrpInfo() throws Throwable {
		return select(getConnection(P2PConst.DB_USER), new ItemParser<XrpEntity>() {
			@Override
			public XrpEntity parse(ResultSet re) throws SQLException {
				XrpEntity b = new XrpEntity();
				while (re.next()) {
					b.r_qbdz = re.getString(1);
					b.r_sy = re.getString(2);
					b.l_qbdz = re.getString(3);
					b.count = re.getBigDecimal(4);
					b.is = EnumParser.parse(IsPass.class, re.getString(5));
					b.ip = re.getString(6);
				}
				return b;
			}
		}, "SELECT F12,F13,F14,F16,F15,F18 FROM T6013 WHERE F01=?", bid);

	}

	@Override
	public void xrp_rqb(Lsqbdz l) throws Throwable {
		// 获取xrp服务参数
		XrpEntity xrp = this.getXrpInfo();

		// 查询用户钱包信息
		
		// 判断账户总额
		
		// 扣减手续费
		
		// 转账
		String hash = "";
		String rs = "";
		
		// 更新转账记录状态
		execute(getConnection(P2PConst.DB_USER), "UPDATE  T6012_3 SET F05=?,F06=CURRENT_TIMESTAMP(),F08=?,F09=? WHERE F01=?",
				IsPass.S, hash, 0, l.id);
		execute(getConnection(P2PConst.DB_USER), "UPDATE  T6012_4 SET F03=? WHERE F01=?", rs, l.id);
	}

	@Override
	public void xrp_lqb() throws Throwable {
		// TODO Auto-generated method stub

	}

	@Override
	public Lsqbdz[] getZrjl() throws Throwable {
		return selectAll(getConnection(P2PConst.DB_USER), new ArrayParser<Lsqbdz>() {
			ArrayList<Lsqbdz> list = new ArrayList<>();

			@Override
			public Lsqbdz[] parse(ResultSet re) throws SQLException {
				while (re.next()) {
					Lsqbdz s = new Lsqbdz();
					s.id = re.getLong(1);
					s.zrje = re.getBigDecimal(2);
					s.qbdz = re.getString(3);
					s.sy = re.getString(4);
					s.T6012_id = re.getLong(5);
					list.add(s);
				}
				return list == null || list.size() == 0 ? null : list.toArray(new Lsqbdz[list.size()]);
			}
		}, "SELECT T6012_3.F01,T6012_3.F04,T6.F03,T6.F04,T6.F01 FROM T6012_3 LEFT JOIN T6012_" + bjc
				+ " AS T6 ON T6.F01=T6012_3.F07 WHERE T6012_3.F05=? AND T6012_3.F10=? ", IsPass.F, bid);
	}

	private String xrpTransBalance() throws Throwable {
		Map<String, String> data = new HashMap<>();

		JSONObject json = JSONObject.fromObject(data);
			
		HttpEntity p = new HttpEntity();
		p.method = "POST";
		p.data = json.toString();
		p.url = "";
		
		return post(p);
	}
}