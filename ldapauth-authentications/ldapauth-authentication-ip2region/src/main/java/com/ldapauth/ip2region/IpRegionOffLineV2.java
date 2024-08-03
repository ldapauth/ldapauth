package com.ldapauth.ip2region;





import org.lionsoul.ip2region.xdb.Searcher;


/**
 * 基于ip2region离线库ip查询
 *
 * <p>
 * 官方文档：https://gitee.com/lionsoul/ip2region Apache-2.0
 * </p>
 *
 * <p>
 * Ip2region (2.0 - xdb) 是一个离线 IP 数据管理框架和定位库，支持亿级别的数据段，10微秒级别的查询性能，提供了许多主流编程语言的 xdb 数据管理引擎的实现。
 * </p>
 *
 * @author Crystal.Sea
 *
 */
public class IpRegionOffLineV2 extends AbstractIpRegion implements IpRegion{

	Searcher searcher;;

	public IpRegionOffLineV2(Searcher searcher) {
		super();
		this.searcher = searcher;
	}

	@Override
	public Region region(String ipAddress) {
		try {
			String regionAddr =searcher.search(ipAddress);
			if(regionAddr.indexOf("内网IP")>-1) {
				return new Region("内网IP");
			}
			String[] regionAddrs =regionAddr.split("\\|");
			return new Region(regionAddrs[0],regionAddrs[2],regionAddrs[3],regionAddrs[0]+regionAddrs[2]+regionAddrs[3]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
