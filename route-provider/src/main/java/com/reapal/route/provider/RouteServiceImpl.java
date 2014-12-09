package com.reapal.route.provider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.reapal.route.API.RouteService;

public class RouteServiceImpl implements RouteService{

	public String findRoute(Map param) {
    	String strBankCode = null;

    	//取商户ID
    	String strUserId = null;
    	if(param.get("merchant_ID")!=null)
    		strUserId = (String)param.get("merchant_ID");
    	else
    	{
    		strBankCode = "8009";
	        System.out.println( "merchant_ID为空默认为8009渠道");
    		return strBankCode;
    	}
    	Connection conn = null;
    	try 
    	{
    		//连接数据库
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String url = "jdbc:oracle:thin:@192.168.0.27:1521:devdb";
			String UserName = "jjhs";
			String Password = "jjhs";
			conn = DriverManager.getConnection(url, UserName, Password);
			Statement stmt = conn.createStatement();
			//查找路由记录
			ResultSet rs = stmt.executeQuery("SELECT * FROM ROUTE_RULE WHERE MERCHANT_ID = " + strUserId );
			if( rs.next() )
			{//有路由设置则按照设置执行
				//路由设置暂定以下几种：固定渠道、利润优先、渠道健壮性优先、条件（条件需要更复杂的路由规则解析）
				//固定渠道：在配置的渠道中返回所需银行渠道（不支持该银行怎么办？跳转其他渠道？）
				//在所有可用渠道中选出利润最大的一家
				//在所有可用渠道中选出健壮性最优的一家
				//可用渠道中满足一定条件中利润最大的，条件太苛刻没有满足的渠道则回归利润优先（多种条件组合系统列出优先级规则）
				strBankCode = rs.getString("MERCHANT_RULE");
		        System.out.println( "找到记录" + strUserId + "---" + strBankCode);
			}
			else
			{//没有记录则为非固定路由
				strBankCode = "8009";
		        System.out.println( "没有记录" + strUserId );
				/*以下代码暂不执行，目前只有一种路由，CFCA
				//从map中取出该笔支付的银行卡所属银行
				String strCardBank = null;
				strCardBank = (String)param.get("card_bank");
				//从map中取出此次支付金额
				String strAmount = (String)param.get("total_fee");
				float fAmount = Float.parseFloat(strAmount);
				//查看目前系统中支持该银行的所有渠道中限额能够包住此次支付且状态可用的渠道，暂定    银行代码_渠道代码
				ResultSet rsBank = stmt.executeQuery("SELECT * FROM 银行渠道表  WHERE 渠道名称 like 支付银行 and 限额>=支付金额 " );

				//查询各渠道银行的费率
				//分别计算各渠道完成此笔交易所需要缴纳的银行费用
				//查询各渠道商户的费率
				//分别计算各渠道完成此笔交易所收取的客户费用
				//选出利润最高的一个渠道
				//返回渠道代码
*/
				
			}
		 stmt.close();
		 conn.close();

		} 
    	catch (ClassNotFoundException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	catch (SQLException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	    	
        return strBankCode;
	}

}
