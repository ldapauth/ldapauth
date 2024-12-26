package com.ldapauth.web.contorller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import com.ldapauth.crypto.cert.X509CertUtils;
import com.ldapauth.exception.BusinessException;
import com.ldapauth.pojo.entity.client.details.ClientSAMLDetails;
import com.ldapauth.pojo.vo.MetadataVo;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.utils.MetadataDescriptorUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Objects;


/**
 * SAML20 metadata 解析类
 */
@Slf4j
@RestController
@RequestMapping(value={"/idp/saml20/metadata"})
public class MetadataController {

	@Operation(
			summary = "解析SP-SAML元数据文件", description = "返回成功信息",
			security = {@SecurityRequirement(name = "Authorization")}
	)
	@PostMapping("/fileTransform")
	public Result<MetadataVo> fileTransform(@RequestPart("file") MultipartFile file){
		if (Objects.isNull(file)) {
			throw new BusinessException(400, "元数据文件不能为空");
		}
		try {
			InputStream inputStream = file.getInputStream();
			if(Objects.nonNull(inputStream)) {
				MetadataVo details = resolveMetaData(inputStream);
				return Result.success(details);
			}
		} catch (Exception e) {
			log.error("error",e);
		}
		throw new BusinessException(400, "解析元数据失败");
	}

	public MetadataVo resolveMetaData(InputStream inputStream) throws Exception {
		ClientSAMLDetails samlDetails = new ClientSAMLDetails();
		X509Certificate trustCert = null;
		EntityDescriptor entityDescriptor;
		try {
			entityDescriptor = MetadataDescriptorUtil.getInstance().getEntityDescriptor(inputStream);
		} catch (Exception e) {
			log.error("metadata  file resolve error .", e);
			throw new BusinessException(500, "解析元数据失败");
		}
		SPSSODescriptor sPSSODescriptor = entityDescriptor.getSPSSODescriptor(SAMLConstants.SAML20P_NS);
		String b64Encoder = sPSSODescriptor.getKeyDescriptors().get(0).getKeyInfo().getX509Datas().get(0).getX509Certificates().get(0).getValue();
		trustCert = X509CertUtils.loadCertFromB64Encoded(b64Encoder);
		samlDetails.setTrustCert(trustCert);
		if (Objects.nonNull(sPSSODescriptor.getAssertionConsumerServices()) &&
				!sPSSODescriptor.getAssertionConsumerServices().isEmpty() &&
				sPSSODescriptor.getAssertionConsumerServices().size() > 0){

			samlDetails.setSpAcsUri(sPSSODescriptor.getAssertionConsumerServices().get(0).getLocation());
		}
		if(Objects.nonNull(entityDescriptor.getEntityID())) {
			samlDetails.setEntityId(entityDescriptor.getEntityID());
			samlDetails.setIssuer(entityDescriptor.getEntityID());
			samlDetails.setAudience(entityDescriptor.getEntityID());
		}

		if (Objects.nonNull(samlDetails.getTrustCert())) {
			samlDetails.setCertSubject(samlDetails.getTrustCert().getSubjectDN().getName());
			samlDetails.setCertExpiration(samlDetails.getTrustCert().getNotAfter().toString());
			samlDetails.setCertIssuer(X509CertUtils.getCommonName(samlDetails.getTrustCert().getIssuerX500Principal()));
		}

		log.info("SPSSODescriptor EntityID "+ entityDescriptor.getEntityID());
		MetadataVo vo = BeanUtil.copyProperties(samlDetails,MetadataVo.class);
		vo.setB64Encoder(b64Encoder);
		if (Objects.nonNull(sPSSODescriptor.getAssertionConsumerServices()) &&
				!sPSSODescriptor.getAssertionConsumerServices().isEmpty() &&
				sPSSODescriptor.getAssertionConsumerServices().size() > 0){
			if(sPSSODescriptor.getAssertionConsumerServices().get(0).getBinding().equalsIgnoreCase("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST")) {
				vo.setFirstBinding("Post-Post");
			} else {
				vo.setFirstBinding("Redirect-Post");
			}
		}
		if (Objects.nonNull(sPSSODescriptor.getNameIDFormats()) &&
				!sPSSODescriptor.getNameIDFormats().isEmpty() &&
				sPSSODescriptor.getNameIDFormats().size() > 0){
			vo.setFirstNameIDFormat(sPSSODescriptor.getNameIDFormats().get(0).getFormat());

		}

		return vo;
	}


	@Operation(
			summary = "解析IDP-SAML元数据文件", description = "返回成功信息",
			security = {@SecurityRequirement(name = "Authorization")}
	)
	@PostMapping("/fileTransByIdp")
	public Result<MetadataVo> fileTransByIdp(@RequestPart("file") MultipartFile file){
		if (Objects.isNull(file)) {
			throw new BusinessException(400, "元数据文件不能为空");
		}
		try {
			InputStream inputStream = file.getInputStream();
			if(Objects.nonNull(inputStream)) {
				MetadataVo details = resolveMetaIDPData(inputStream);
				return Result.success(details);
			}
		} catch (Exception e) {
			log.error("error",e);
		}
		return Result.failed("解析元数据失败");
	}

	public MetadataVo resolveMetaIDPData(InputStream inputStream) throws Exception {
		MetadataVo vo = new MetadataVo();
		EntityDescriptor entityDescriptor;
		try {
			String b64Encoder = IoUtil.read(inputStream, CharsetUtil.UTF_8);
			vo.setB64Encoder(b64Encoder);
			inputStream = IoUtil.toStream(b64Encoder,CharsetUtil.UTF_8);
			entityDescriptor = MetadataDescriptorUtil.getInstance().getEntityDescriptor(inputStream);
		} catch (Exception e) {
			log.error("metadata  file resolve error .", e);
			throw new BusinessException(400, "解析IDP元数据失败");
		}

		IDPSSODescriptor idpssoDescriptor = entityDescriptor.getIDPSSODescriptor(SAMLConstants.SAML20P_NS);
		if(Objects.nonNull(entityDescriptor.getEntityID())) {
			vo.setEntityId(entityDescriptor.getEntityID());
			vo.setIssuer(entityDescriptor.getEntityID());
			vo.setAudience(entityDescriptor.getEntityID());
		}
		log.info("idpssoDescriptor EntityID "+ entityDescriptor.getEntityID());

		if (Objects.nonNull(idpssoDescriptor.getSingleSignOnServices()) &&
				!idpssoDescriptor.getSingleSignOnServices().isEmpty() &&
				idpssoDescriptor.getSingleSignOnServices().size() > 0){
			if(idpssoDescriptor.getSingleSignOnServices().get(0).getBinding().equalsIgnoreCase("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST")) {
				vo.setFirstBinding("Post");
			} else {
				vo.setFirstBinding("Redirect");
			}
			vo.setSpAcsUri(idpssoDescriptor.getSingleSignOnServices().get(0).getLocation());
		}
		return vo;
	}

}
