package com.ldapauth.autoconfigure;

import java.io.IOException;
import java.util.Properties;

import com.ldapauth.authz.saml20.binding.impl.*;
import com.ldapauth.authz.saml20.metadata.domain.Saml20Metadata;
import com.ldapauth.crypto.keystore.KeyStoreLoader;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import com.ldapauth.authz.saml.common.EndpointGenerator;
import com.ldapauth.authz.saml.service.IDService;
import com.ldapauth.authz.saml.service.TimeService;
import com.ldapauth.authz.saml20.binding.decoder.OpenHTTPPostDecoder;
import com.ldapauth.authz.saml20.binding.decoder.OpenHTTPPostSimpleSignDecoder;
import com.ldapauth.authz.saml20.binding.decoder.OpenHTTPRedirectDecoder;
import com.ldapauth.authz.saml20.provider.xml.AuthnResponseGenerator;
import com.ldapauth.authz.saml20.xml.SAML2ValidatorSuite;
import org.opensaml.common.binding.security.IssueInstantRule;
import org.opensaml.common.binding.security.MessageReplayRule;
import org.opensaml.util.storage.MapBasedStorageService;
import org.opensaml.util.storage.ReplayCache;
import org.opensaml.util.storage.ReplayCacheEntry;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.parse.BasicParserPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import com.springframework.ui.velocity.VelocityEngineFactoryBean;

@AutoConfiguration
@ComponentScan(basePackages = {
        "com.ldapauth.authz.saml20.provider.endpoint",
        "com.ldapauth.authz.saml20.metadata.endpoint",
})
public class Saml20AutoConfiguration implements InitializingBean {
    private static final  Logger _logger = LoggerFactory.getLogger(Saml20AutoConfiguration.class);

    /**
     * samlBootstrapInitializer.
     * @return samlBootstrapInitializer
     * @throws ConfigurationException
     */
    @Bean(name = "samlBootstrapInitializer")
    public String samlBootstrapInitializer() throws ConfigurationException {
        org.opensaml.DefaultBootstrap.bootstrap();
        return "";
    }

    /**
     * TimeService.
     * @return timeService
     */
    @Bean(name = "timeService")
    public TimeService TimeService() {
        TimeService timeService = new TimeService();
        return timeService;
    }

    /**
     * IDService.
     * @return idService
     */
    @Bean(name = "idService")
    public IDService idService() {
        IDService idService = new IDService();
        return idService;
    }

    /**
     * EndpointGenerator.
     * @return endpointGenerator
     */
    @Bean(name = "endpointGenerator")
    public EndpointGenerator endpointGenerator() {
        EndpointGenerator generator = new EndpointGenerator();
        return generator;
    }

    /**
     * AuthnResponseGenerator.
     * @return authnResponseGenerator
     */
    @Bean(name = "authnResponseGenerator")
    public AuthnResponseGenerator authnResponseGenerator(TimeService timeService,IDService idService,
            @Value("${ldapauth.saml.v20.idp.issuer}") String issuerEntityName) {
        _logger.debug("issuerEntityName " + issuerEntityName);
        AuthnResponseGenerator generator = new AuthnResponseGenerator(issuerEntityName,timeService,idService);
        return generator;
    }

    /**
     * IssuerEntityName.
     * @return issuerEntityName
     */
    @Bean(name = "issuerEntityName")
    public String issuerEntityName(
            @Value("${ldapauth.saml.v20.idp.issuer}") String issuerEntityName) {
        return issuerEntityName;
    }

    /**
     * Saml20Metadata.
     * @return saml20Metadata
     */
    @Bean(name = "saml20Metadata")
    public Saml20Metadata saml20Metadata(
            @Value("${ldapauth.saml.v20.metadata.orgName}") String orgName,
            @Value("${ldapauth.saml.v20.metadata.orgDisplayName}") String orgDisplayName,
            @Value("${ldapauth.saml.v20.metadata.orgURL}") String orgURL,
            @Value("${ldapauth.saml.v20.metadata.company}") String company,
            @Value("${ldapauth.saml.v20.metadata.contactType}") String contactType,
            @Value("${ldapauth.saml.v20.metadata.givenName}") String givenName,
            @Value("${ldapauth.saml.v20.metadata.surName}") String surName,
            @Value("${ldapauth.saml.v20.metadata.emailAddress}") String emailAddress,
            @Value("${ldapauth.saml.v20.metadata.telephoneNumber}") String telephoneNumber) {
        Saml20Metadata metadata = new Saml20Metadata();
        metadata.setOrgName(orgName);
        metadata.setOrgDisplayName(orgDisplayName);
        metadata.setOrgURL(orgURL);
        metadata.setCompany(company);
        metadata.setContactType(contactType);
        metadata.setGivenName(givenName);
        metadata.setSurName(surName);
        metadata.setEmailAddress(emailAddress);
        metadata.setTelephoneNumber(telephoneNumber);
        return metadata;
    }

    /**
     * SAML2ValidatorSuite.
     * @return samlValidaotrSuite
     */
    @Bean(name = "samlValidaotrSuite")
    public SAML2ValidatorSuite validatorSuite() {
        SAML2ValidatorSuite validatorSuite = new SAML2ValidatorSuite();
        return validatorSuite;
    }

    /**
     * MapBasedStorageService.
     * @return mapBasedStorageService
     */
    @SuppressWarnings("rawtypes")
    @Bean(name = "mapBasedStorageService")
    public MapBasedStorageService mapBasedStorageService() {
        MapBasedStorageService mapBasedStorageService = new MapBasedStorageService();
        return mapBasedStorageService;
    }

    /**
     * VelocityEngineFactoryBean.
     * @return velocityEngine
     * @throws IOException
     * @throws VelocityException
     */
    @SuppressWarnings({ "deprecation"})
    @Bean(name = "velocityEngine")
    public VelocityEngine velocityEngine() throws VelocityException, IOException {
        VelocityEngineFactoryBean factory = new VelocityEngineFactoryBean();
        factory.setPreferFileSystemAccess(false);
        Properties velocityProperties = new Properties();
        velocityProperties.put("resource.loader", "classpath");
        velocityProperties.put("classpath.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        factory.setVelocityProperties(velocityProperties);
        return factory.createVelocityEngine();
    }

    /**
     * ReplayCache.
     * @return replayCache
     */
    @Bean(name = "replayCache")
    public ReplayCache replayCache(MapBasedStorageService<String, ReplayCacheEntry> mapBasedStorageService,
            @Value("${ldapauth.saml.v20.replay.cache.life.in.millis}") long duration) {
        ReplayCache replayCache = new ReplayCache(mapBasedStorageService,duration);
        return replayCache;
    }

    /**
     * MessageReplayRule.
     * @return messageReplayRule
     */
    @Bean(name = "messageReplayRule")
    public MessageReplayRule messageReplayRule(ReplayCache replayCache) {
        MessageReplayRule messageReplayRule = new MessageReplayRule(replayCache);
        return messageReplayRule;
    }

    /**
     * BasicParserPool.
     * @return samlParserPool
     */
    @Bean(name = "samlParserPool")
    public BasicParserPool samlParserPool(
            @Value("${ldapauth.saml.v20.max.parser.pool.size}") int maxPoolSize) {
        BasicParserPool samlParserPool = new BasicParserPool();
        samlParserPool.setMaxPoolSize(maxPoolSize);
        return samlParserPool;
    }

    /**
     * IssueInstantRule.
     * @return issueInstantRule
     */
    @Bean(name = "issueInstantRule")
    public IssueInstantRule issueInstantRule(
            @Value("${ldapauth.saml.v20.issue.instant.check.clock.skew.in.seconds}") int newClockSkew,
            @Value("${ldapauth.saml.v20.issue.instant.check.validity.time.in.seconds}") int newExpires) {
        IssueInstantRule decoder = new IssueInstantRule(newClockSkew,newExpires);
        decoder.setRequiredRule(true);
        return decoder;
    }

    /**
     * OpenHTTPPostSimpleSignDecoder.
     * @return openHTTPPostSimpleSignDecoder
     */
    @Bean(name = "openHTTPPostSimpleSignDecoder")
    public OpenHTTPPostSimpleSignDecoder openHTTPPostSimpleSignDecoder(BasicParserPool samlParserPool,
            @Value("${ldapauth.saml.v20.idp.receiver.endpoint}") String receiverEndpoint) {
        OpenHTTPPostSimpleSignDecoder decoder = new OpenHTTPPostSimpleSignDecoder(samlParserPool);
        decoder.setReceiverEndpoint(receiverEndpoint);
        return decoder;
    }

    /**
     * OpenHTTPPostDecoder.
     * @return openHTTPPostDecoder
     */
    @Bean(name = "openHTTPPostDecoder")
    public OpenHTTPPostDecoder openHTTPPostDecoder(BasicParserPool samlParserPool,
            @Value("${ldapauth.saml.v20.idp.receiver.endpoint}") String receiverEndpoint) {
        OpenHTTPPostDecoder decoder = new OpenHTTPPostDecoder(samlParserPool);
        decoder.setReceiverEndpoint(receiverEndpoint);
        return decoder;
    }

    /**
     * OpenHTTPRedirectDecoder.
     * @return openHTTPRedirectDecoder
     */
    @Bean(name = "openHTTPRedirectDecoder")
    public OpenHTTPRedirectDecoder openHTTPRedirectDecoder(BasicParserPool samlParserPool,
            @Value("${ldapauth.saml.v20.idp.receiver.endpoint}") String receiverEndpoint) {
        OpenHTTPRedirectDecoder decoder = new OpenHTTPRedirectDecoder(samlParserPool);
        decoder.setReceiverEndpoint(receiverEndpoint);
        return decoder;
    }

    /**
     * ExtractPostBindingAdapter.
     * @return extractPostBindingAdapter
     */
    @Bean(name = "extractPostBindingAdapter")
    public ExtractPostBindingAdapter extractPostBindingAdapter(OpenHTTPPostDecoder openHTTPPostDecoder,
            KeyStoreLoader keyStoreLoader,IssueInstantRule issueInstantRule,MessageReplayRule messageReplayRule) {
        ExtractPostBindingAdapter adapter = new ExtractPostBindingAdapter(openHTTPPostDecoder);
        adapter.setIssueInstantRule(issueInstantRule);
        adapter.setKeyStoreLoader(keyStoreLoader);
        adapter.setMessageReplayRule(messageReplayRule);
        return adapter;
    }

    /**
     * ExtractRedirectBindingAdapter.
     * @return extractRedirectBindingAdapter
     */
    @Bean(name = "extractRedirectBindingAdapter")
    public ExtractRedirectBindingAdapter extractRedirectBindingAdapter(OpenHTTPRedirectDecoder openHTTPRedirectDecoder,
                                                                       KeyStoreLoader keyStoreLoader, IssueInstantRule issueInstantRule, MessageReplayRule messageReplayRule) {
        ExtractRedirectBindingAdapter adapter = new ExtractRedirectBindingAdapter(openHTTPRedirectDecoder);
        adapter.setIssueInstantRule(issueInstantRule);
        adapter.setKeyStoreLoader(keyStoreLoader);
        adapter.setMessageReplayRule(messageReplayRule);
        return adapter;
    }


    /**
     * PostSimpleSignBindingAdapter.
     * @return postSimpleSignBindingAdapter
     */
    @Bean(name = "postSimpleSignBindingAdapter")
    public PostSimpleSignBindingAdapter postSimpleSignBindingAdapter(VelocityEngine velocityEngine,
            @Value("${ldapauth.saml.v20.idp.issuer}") String issuerEntityName) {
        PostSimpleSignBindingAdapter adapter = new PostSimpleSignBindingAdapter();
        adapter.setVelocityEngine(velocityEngine);
        adapter.setIssuerEntityName(issuerEntityName);
        return adapter;
    }


    /**
     * PostBindingAdapter.
     * @return postBindingAdapter
     */
    @Bean(name = "postBindingAdapter")
    public PostBindingAdapter postBindingAdapter(VelocityEngine velocityEngine,
            @Value("${ldapauth.saml.v20.idp.issuer}") String issuerEntityName) {
        PostBindingAdapter adapter = new PostBindingAdapter();
        adapter.setVelocityEngine(velocityEngine);
        adapter.setIssuerEntityName(issuerEntityName);
        return adapter;
    }

    @Bean(name = "redirectSimpleSignBindingAdapter")
    public RedirectSimpleSignBindingAdapter redirectSimpleSignBindingAdapter(
            VelocityEngine velocityEngine,
            @Value("${ldapauth.saml.v20.idp.issuer}") String issuerEntityName) {
        RedirectSimpleSignBindingAdapter adapter = new RedirectSimpleSignBindingAdapter();
        adapter.setVelocityEngine(velocityEngine);
        adapter.setIssuerEntityName(issuerEntityName);
        return adapter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
