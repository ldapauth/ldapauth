package com.ldapauth.ip2region;




/**
 * 本地(127.0.0.1/0:0:0:0:0:0:0:1)地址
 *
 * @author Crystal.Sea
 *
 */
public class IpRegionLocal extends AbstractIpRegion implements IpRegion{

	@Override
	public Region region(String ipAddress) {
		if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
			return new Region("local");
		}
		return null;
	}
}
