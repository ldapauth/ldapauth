package com.ldapauth.utils;

import com.ldapauth.crypto.cert.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.impl.EntityDescriptorImpl;
import org.opensaml.saml2.metadata.provider.DOMMetadataProvider;
import org.opensaml.saml2.metadata.provider.FilesystemMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.InputStream;

/**
 * @author Crystal.Sea
 *
 */
@Slf4j
public class MetadataDescriptorUtil {

	private static MetadataDescriptorUtil instance = null;

	/**
	 *
	 */
	public MetadataDescriptorUtil() {
		try {
			org.opensaml.DefaultBootstrap.bootstrap();
		} catch (ConfigurationException e) {
			/*e.printStackTrace();*/
			log.error("error",e);
		}
	}

	public static synchronized MetadataDescriptorUtil getInstance() {
		if (instance == null) {
			instance = new MetadataDescriptorUtil();
			// instance.bootstrap();
			log.debug("getInstance()" + " new ConfigFile instance");
		}
		return instance;
	}

	// public void bootstrap() throws ConfigurationException {
	// // DefaultBootstrap.bootstrap();
	// }

	public EntityDescriptor getEntityDescriptor(File file)
			throws Exception {
		try {
			FilesystemMetadataProvider filesystemMetadataProvider = new FilesystemMetadataProvider(
					file);
			filesystemMetadataProvider.setRequireValidMetadata(true); // Enable
			// validation
			filesystemMetadataProvider.setParserPool(new BasicParserPool());
			filesystemMetadataProvider.initialize();
			EntityDescriptor entityDescriptor = (EntityDescriptorImpl) filesystemMetadataProvider.getMetadata();
			return entityDescriptor;
		} catch (MetadataProviderException e) {
			log.error("元数据解析出错", e);
			throw new Exception("元数据文件解析出错", e);
		}

	}

	public EntityDescriptor getEntityDescriptor(InputStream inputStream)
			throws Exception {
		BasicParserPool basicParserPool = new BasicParserPool();
		basicParserPool.setNamespaceAware(true);
		try {
			Document inMetadataDoc = basicParserPool.parse(inputStream);
			Element metadataRoot = inMetadataDoc.getDocumentElement();

			UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
			Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(metadataRoot);

			// unmarshaller.unmarshall(arg0)
			// Unmarshall using the document root element, an EntitiesDescriptor
			// in this case
			XMLObject xMLObject = unmarshaller.unmarshall(metadataRoot);

			EntityDescriptor entityDescriptor = (EntityDescriptorImpl) xMLObject;
			return entityDescriptor;
		} catch (XMLParserException e) {
			log.error("元数据解析出错", e);
			throw new Exception("元数据文件解析出错", e);
		} catch (UnmarshallingException e) {
			log.error("元数据解析出错", e);
			throw new Exception("元数据文件解析出错", e);
		}

	}

	public EntityDescriptor getEntityDescriptor(String strMetadata)
			throws Exception {
		InputStream inputStream = StringUtil.String2InputStream(strMetadata);
		return getEntityDescriptor(inputStream);
	}

	// from dom
	public EntityDescriptor getEntityDescriptor(Element elementMetadata)
			throws Exception {
		try {
			DOMMetadataProvider dOMMetadataProvider = new DOMMetadataProvider(elementMetadata);
			dOMMetadataProvider.setRequireValidMetadata(true); // Enable
																// validation
			dOMMetadataProvider.setParserPool(new BasicParserPool());
			dOMMetadataProvider.initialize();
			EntityDescriptor entityDescriptor = (EntityDescriptorImpl) dOMMetadataProvider.getMetadata();
			return entityDescriptor;
		} catch (MetadataProviderException e) {
			log.error("元数据解析出错", e);
			throw new Exception("元数据解析出错", e);
		}

	}

}
