


package com.ldapauth.ip2region;

/**
 * IP所属区域实体
 *
 * @author Crystal.sea
 *
 */
public class Region {

	/**
	 * 国家
	 */
	String country;

	/**
	 * 省/州
	 */
	String province;

	/**
	 * 城市
	 */
	String city;

	/**
	 * 区域位置
	 */
	String addr;

	public Region() {

	}

	public Region(String addr) {
		this.addr = addr;
	}

	public Region(String country, String province, String city, String addr) {
		super();
		this.country = country;
		this.province = province;
		this.city = city;
		this.addr = addr;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Region [country=");
		builder.append(country);
		builder.append(", province=");
		builder.append(province);
		builder.append(", city=");
		builder.append(city);
		builder.append(", addr=");
		builder.append(addr);
		builder.append("]");
		return builder.toString();
	}

}
