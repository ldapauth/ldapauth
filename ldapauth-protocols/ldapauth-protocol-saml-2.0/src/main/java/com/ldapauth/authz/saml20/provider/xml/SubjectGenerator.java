package com.ldapauth.authz.saml20.provider.xml;


import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.entity.apps.details.ClientAppsSAMLDetails;
import com.ldapauth.web.WebContext;
import org.apache.commons.lang3.StringUtils;
import com.ldapauth.authz.saml.service.TimeService;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml2.core.impl.NameIDBuilder;
import org.opensaml.saml2.core.impl.SubjectBuilder;
import org.opensaml.saml2.core.impl.SubjectConfirmationBuilder;
import org.opensaml.saml2.core.impl.SubjectConfirmationDataBuilder;

public class SubjectGenerator {

	//private final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
	private final TimeService timeService;

	public SubjectGenerator(TimeService timeService) {
		super();
		this.timeService = timeService;
	}

	public Subject generateSubject( ClientAppsSAMLDetails saml20Details,
							String assertionConsumerURL,
							String inResponseTo,
							int validInSeconds,
							UserInfo userInfo) {




		String nameIDType = NameIDType.UNSPECIFIED;
		if(saml20Details.getNameIdFormat().equalsIgnoreCase("persistent")) {
			nameIDType = NameIDType.PERSISTENT;
		}else if(saml20Details.getNameIdFormat().equalsIgnoreCase("transient")) {
			nameIDType = NameIDType.TRANSIENT;
        }else if(saml20Details.getNameIdFormat().equalsIgnoreCase("unspecified")) {
        	nameIDType = NameIDType.UNSPECIFIED;
        }else if(saml20Details.getNameIdFormat().equalsIgnoreCase("emailAddress")) {
            nameIDType = NameIDType.EMAIL;
        }else if(saml20Details.getNameIdFormat().equalsIgnoreCase("X509SubjectName")) {
        	nameIDType = NameIDType.X509_SUBJECT;
        }else if(saml20Details.getNameIdFormat().equalsIgnoreCase("entity")) {
        	nameIDType = NameIDType.ENTITY;
        }else if(saml20Details.getNameIdFormat().equalsIgnoreCase("custom")) {

        }
		String nameIdValue = getValueByUserAttr(userInfo,saml20Details.getSubject());


		if(!StringUtils.isEmpty(saml20Details.getNameIdSuffix())) {
		    nameIdValue = nameIdValue + saml20Details.getNameIdSuffix();
		}

		if(saml20Details.getNameIdConvert().equalsIgnoreCase("uppercase")) {
		    nameIdValue = nameIdValue.toUpperCase();
        } else if(saml20Details.getNameIdConvert().equalsIgnoreCase("lowercase")) {
            nameIdValue = nameIdValue.toLowerCase();
        }

		NameID nameID = builderNameID(nameIdValue,assertionConsumerURL,nameIDType);
		Subject subject =builderSubject(nameID);

		String clientAddress = WebContext.getRequestIpAddress(WebContext.getRequest());
		SubjectConfirmation subjectConfirmation =builderSubjectConfirmation(
								assertionConsumerURL,
								inResponseTo,
								validInSeconds,
								clientAddress);

		subject.getSubjectConfirmations().add(subjectConfirmation);

		return subject;
	}

	public NameID builderNameID(String value,String strSPNameQualifier,String nameIDType){
		//Response/Assertion/Subject/NameID
		NameID nameID = new NameIDBuilder().buildObject();
		nameID.setValue(value);
		//nameID.setFormat(NameIDType.PERSISTENT);
		nameID.setFormat(nameIDType);
		//nameID.setSPNameQualifier(strSPNameQualifier);

		return nameID;
	}

	public static String getValueByUserAttr(UserInfo userInfo,String userAttr) {
		String value = "";
		if(StringUtils.isBlank(userAttr)) {
			value = userInfo.getUsername();
		}else if(userAttr.equalsIgnoreCase("username")){
			value = userInfo.getUsername();
		}else if(userAttr.equalsIgnoreCase("userId")){
			value = String.valueOf(userInfo.getId());
		}else if(userAttr.equalsIgnoreCase("email")){
			value = userInfo.getEmail();
		}else if(userAttr.equalsIgnoreCase("mobile")){
			value = userInfo.getMobile();
		}else {
			value = String.valueOf(userInfo.getId());
		}
		if (StringUtils.isBlank(value)) {
			value = userInfo.getUsername();
		}
		if (StringUtils.isBlank(value)) {
			value = String.valueOf(userInfo.getId());
		}
		return value;
	}

	public Subject builderSubject (NameID nameID){
		//Response/Assertion/Subject
		Subject subject = new SubjectBuilder().buildObject();
		subject.setNameID(nameID);
		return subject;
	}

	public SubjectConfirmation builderSubjectConfirmation(String recipient,String inResponseTo,int validInSeconds,String clientAddress){
		//SubjectConfirmationBuilder subjectConfirmationBuilder = (SubjectConfirmationBuilder)builderFactory.getBuilder(SubjectConfirmation.DEFAULT_ELEMENT_NAME);
		SubjectConfirmation subjectConfirmation = new SubjectConfirmationBuilder().buildObject();
		subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);

		//SubjectConfirmationDataBuilder subjectConfirmationDataBuilder = (SubjectConfirmationDataBuilder)builderFactory.getBuilder(SubjectConfirmationData.DEFAULT_ELEMENT_NAME);
		SubjectConfirmationData subjectConfirmationData = new SubjectConfirmationDataBuilder().buildObject();

		subjectConfirmationData.setRecipient(recipient);
		//if idp-init not need inResponseTo
		if(null!=inResponseTo){
			subjectConfirmationData.setInResponseTo(inResponseTo);
		}
		subjectConfirmationData.setNotOnOrAfter(timeService.getCurrentDateTime().plusSeconds(validInSeconds));
		subjectConfirmationData.setAddress(clientAddress);

		subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);

		return subjectConfirmation;
	}

}
