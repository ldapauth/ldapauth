/*
 * Copyright [2020] [ldapauth of copyright http://www.ldapauth.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.ldapauth.constants;

/**
 * PROTOCOLS.
 * @author Crystal.Sea
 *
 */
public final class ConstsProtocols {

    public static final class LDAP_AUTH {
        //1=saml 2=oidc 3=jwt 4=cas

        public static final Integer SAML = 1;
        public static final Integer OIDC = 2;

        public static final Integer JWT = 3;
        public static final Integer CAS =4;

    }

    public static final String BASIC 				= "Basic";

    public static final String EXTEND_API 			= "Extend_API";

    public static final String FORMBASED 			= "Form_Based";

    public static final String TOKENBASED 			= "Token_Based";

    // SAML
    public static final String SAML20 				= "SAML_v2.0";

    public static final String CAS 					= "CAS";

    public static final String JWT 					= "JWT";

    // OAuth
    public static final String OAUTH20 				= "OAuth_v2.0";

    public static final String OAUTH21 				= "OAuth_v2.1";

    public static final String OPEN_ID_CONNECT10 	= "OpenID_Connect_v1.0";

}
