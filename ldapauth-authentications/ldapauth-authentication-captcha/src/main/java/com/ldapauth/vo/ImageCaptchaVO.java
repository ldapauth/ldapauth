

package com.ldapauth.vo;
/**
 * 图片验证码信息
 *
 * @author Crystal.Sea
 *
 */
public class ImageCaptchaVO {
	/**
	 * jwt
	 */
	String state;
	/**
	 * 图片验证码
	 */
	String image;

	public ImageCaptchaVO(String state, String image) {
		super();
		this.state = state;
		this.image = image;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}



}
