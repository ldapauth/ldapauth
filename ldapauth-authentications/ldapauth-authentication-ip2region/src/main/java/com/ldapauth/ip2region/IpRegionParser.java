package com.ldapauth.ip2region;


/**
 * IP转换区域地址解析
 *
 * <p>
 * 依次顺序为Local(本地) ->OnLine(在线解析) ->OffLine(离线解析) -> unknown(未知)
 * </p>
 *
 * @author Crystal.Sea
 *
 */
public class IpRegionParser extends AbstractIpRegion  implements IpRegion{

	IpRegion ipRegionLocal = new IpRegionLocal();

	IpRegion ipRegionOnLine;

	IpRegion ipRegionOffLine;

	boolean isIpRegion;


	public IpRegionParser() {
	}


	public IpRegionParser(boolean isIpRegion,IpRegion ipRegionOnLine, IpRegion ipRegionOffLine) {
		super();
		this.ipRegionOnLine = ipRegionOnLine;
		this.ipRegionOffLine = ipRegionOffLine;
		this.isIpRegion = isIpRegion;
	}

	/**
	 * ip转换区域地址
	 */
	@Override
	public Region region(String ipAddress) {
		Region region = null;
		if(isIpRegion){//true 需要转换，否则跳过
			//本地转换
			region = ipRegionLocal.region(ipAddress);
			//在线转换
			if(ipRegionOnLine != null && region == null) {
				region = ipRegionOnLine.region(ipAddress);
			}
			//离线转换
			if(ipRegionOffLine!= null && region == null) {
				region = ipRegionOffLine.region(ipAddress);
			}
		}
		//不转换或者未找到返回unknown
		return region == null ? new Region("unknown") : region;
	}


}
