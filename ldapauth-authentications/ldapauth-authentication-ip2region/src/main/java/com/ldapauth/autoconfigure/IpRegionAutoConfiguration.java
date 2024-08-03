


package com.ldapauth.autoconfigure;

import com.ldapauth.ip2region.IpRegion;
import com.ldapauth.ip2region.IpRegionIpchaxun;
import com.ldapauth.ip2region.IpRegionOffLineV2;
import org.lionsoul.ip2region.xdb.Searcher;
import com.ldapauth.ip2region.IpRegionIp138;
import com.ldapauth.ip2region.IpRegionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

/**
 *
 * @author Crystal.Sea
 *
 */
@AutoConfiguration
public class IpRegionAutoConfiguration   implements InitializingBean {
	static final  Logger _logger = LoggerFactory.getLogger(IpRegionAutoConfiguration.class);


	/**
	 * 加载Ip2Region离线库数据 version 2.6.4
	 * @return IpRegionOffLineV2
	 * @throws Exception
	 */
	@Bean
	public IpRegionOffLineV2 ipRegionOffLine() throws Exception {
		_logger.debug("IpRegion OffLine init...");
		ClassPathResource resource = new ClassPathResource("/ip2region/ip2region.xdb");
        byte[] dbBinStr = StreamUtils.copyToByteArray(resource.getInputStream());
        _logger.debug("ip2region length {}",dbBinStr.length);
        Searcher searcher = Searcher.newWithBuffer(dbBinStr);
        return new IpRegionOffLineV2(searcher);
	}

	/**
	 * IP转换区域地址解析
	 * @param ipRegionV2OffLine
	 * @param isIpRegion 是否转换
	 * @param ipRegionProvider 在线转换实现提供商none/Ip138/Ipchaxun
	 * @return IpRegionParser
	 * @throws Exception
	 */
	@Bean
	public IpRegionParser ipRegionParser(IpRegionOffLineV2 ipRegionV2OffLine,
			@Value("${ldapauth.login.ipregion:false}") boolean isIpRegion,
			@Value("${ldapauth.login.ipregion.provider:none}") String ipRegionProvider) throws Exception {
		IpRegion ipRegionOnLine = null;
		//need on line provider
		if(ipRegionProvider.equalsIgnoreCase("none")) {
			//do nothing
		}else if(ipRegionProvider.equalsIgnoreCase("Ip138")){
			ipRegionOnLine = new IpRegionIp138();
			_logger.debug("ip2region online Provider Ip138");
		}else if(ipRegionProvider.equalsIgnoreCase("Ipchaxun")){
			ipRegionOnLine = new IpRegionIpchaxun();
			_logger.debug("ip2region online Provider Ipchaxun");
		}
		IpRegionParser ipRegionParser = new IpRegionParser(isIpRegion,ipRegionOnLine,ipRegionV2OffLine);
        return ipRegionParser;
	}



	@Override
	public void afterPropertiesSet() throws Exception {

	}

}
