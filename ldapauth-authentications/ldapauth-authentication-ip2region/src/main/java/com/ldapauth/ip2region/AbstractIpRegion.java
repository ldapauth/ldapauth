package com.ldapauth.ip2region;




/**
 * IpRegion转换抽象类，获取地址Location
 *
 * @author Crystal.Sea
 *
 */
public abstract class AbstractIpRegion implements IpRegion{

	int failCount = 0;

	public int getFailCount() {
		return failCount;
	};

	public int plusFailCount() {
		return failCount++;
	};


	public String getLocation(String region) {
		if(region.endsWith("电信") || region.endsWith("移动") || region.endsWith("联通")) {
			region.substring(0, region.length() - 2).trim();
		}

		if(region.indexOf(" ") > 0) {
			return region.split(" ")[0];
		}

		return region;
	}
}
