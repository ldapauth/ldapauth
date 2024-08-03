package com.ldapauth.web.contorller;

import com.ldapauth.authn.SignPrincipal;
import com.ldapauth.authn.session.Session;
import com.ldapauth.authn.session.SessionManager;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.configuration.ApplicationConfig;
import com.ldapauth.constants.ConstsLoginType;
import com.ldapauth.persistence.service.LoginLogService;
import com.ldapauth.pojo.entity.LoginLog;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.web.WebContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Tag(
		name = "1-3-单点注销接口文档模块",
		description = "提供系统注销和单点注销基本接口"
)
@Controller
@Slf4j
public class LogoutEndpoint {


	@Autowired
	ApplicationConfig applicationConfig;

	@Autowired
	SessionManager sessionManager;

	@Autowired
	LoginLogService loginLogService;

	/**
	 * for front end；前端注销
	 * @return ResponseEntity
	 */
	@Operation(summary = "前端注销接口", description = "前端注销接口",method="POST")
	@PostMapping(value={"/logout"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
 	public Result<String> logout(){
		SignPrincipal signPrincipal = AuthorizationUtils.getPrincipal();
		String sessionId = signPrincipal.getSession().getId();
 		Session session = sessionManager.get(sessionId);
 		if(session != null) {
			UserInfo userInfo = signPrincipal.getUserInfo();
	        //terminate session
	        sessionManager.remove(session.getId());

			//history
			LoginLog historyLogin = new LoginLog();
			String requestIpAddress = WebContext.getRequestIpAddress();
			//test
			//requestIpAddress = "101.227.131.220";
			historyLogin.setUserId(userInfo.getId());
			historyLogin.setDisplayName(userInfo.getDisplayName());
			historyLogin.setLoginType(ConstsLoginType.LOCAL);
			historyLogin.setProvider("退出");
			//browser info
			Browser browser = resolveBrowser();
			historyLogin.setBrowser(browser.getName());
			historyLogin.setOperateSystem(browser.getPlatform());

			//ip address to region
			historyLogin.setIpAddr(requestIpAddress);
			historyLogin.setStatus(0);
			historyLogin.setMessage("success");
			historyLogin.setCreateTime(new Date());
			//insert
			loginLogService.save(historyLogin);

		}
 		return Result.success("注销成功");
 	}
	/**
	 * 单点注销
	 * @param request
	 * @param redirect_uri 注销后跳转地址
	 * @return 返回前端注销地址并带对应参数
	 */
	@Operation(summary = "单点注销接口", description = "redirect_uri跳转地址",method="GET")
	@RequestMapping(value={"/force/logout"})
 	public ModelAndView forceLogout(
 				HttpServletRequest request,
 				@RequestParam(value = "redirect_uri",required = false) String redirect_uri
 				){
		log.debug("/force/logout invalidate http Session id {}",request.getSession().getId());
		request.getSession().invalidate();
		StringBuffer logoutUrl = new StringBuffer("");
		logoutUrl.append(applicationConfig.getFrontendUri()).append("/logout");
		if(StringUtils.isNotBlank(redirect_uri)) {
			logoutUrl.append("?")
				.append("redirect_uri=").append(redirect_uri);
		}
		ModelAndView modelAndView=new ModelAndView("redirect");
		modelAndView.addObject("redirect_uri", logoutUrl);
		return modelAndView;
 	}

	public Browser resolveBrowser() {
		Browser browser =new Browser();
		String userAgent = WebContext.getRequest().getHeader("User-Agent");
		String[] arrayUserAgent = null;
		if (userAgent.indexOf("MSIE") > 0) {
			arrayUserAgent = userAgent.split(";");
			browser.setName(arrayUserAgent[1].trim());
			browser.setPlatform(arrayUserAgent[2].trim());
		} else if (userAgent.indexOf("Trident") > 0) {
			arrayUserAgent = userAgent.split(";");
			browser.setName( "MSIE/" + arrayUserAgent[3].split("\\)")[0]);

			browser.setPlatform( arrayUserAgent[0].split("\\(")[1]);
		} else if (userAgent.indexOf("Chrome") > 0) {
			arrayUserAgent = userAgent.split(" ");
			// browser=arrayUserAgent[8].trim();
			for (int i = 0; i < arrayUserAgent.length; i++) {
				if (arrayUserAgent[i].contains("Chrome")) {
					browser.setName( arrayUserAgent[i].trim());
					browser.setName( browser.getName().substring(0, browser.getName().indexOf('.')));
				}
			}
			browser.setPlatform( (arrayUserAgent[1].substring(1) + " " + arrayUserAgent[2] + " "
					+ arrayUserAgent[3].substring(0, arrayUserAgent[3].length() - 1)).trim());
		} else if (userAgent.indexOf("Firefox") > 0) {
			arrayUserAgent = userAgent.split(" ");
			for (int i = 0; i < arrayUserAgent.length; i++) {
				if (arrayUserAgent[i].contains("Firefox")) {
					browser.setName( arrayUserAgent[i].trim());
					browser.setName(browser.getName().substring(0, browser.getName().indexOf('.')));
				}
			}
			browser.setPlatform( (arrayUserAgent[1].substring(1) + " " + arrayUserAgent[2] + " "
					+ arrayUserAgent[3].substring(0, arrayUserAgent[3].length() - 1)).trim());

		}

		return browser;
	}


	public class Browser{

		private  String platform;

		private  String name;

		public String getPlatform() {
			return platform;
		}
		public void setPlatform(String platform) {
			this.platform = platform;
		}
		public String getName() {
			return name;
		}
		public void setName(String browser) {
			this.name = browser;
		}


	}
}
