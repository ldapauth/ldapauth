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


/*
 * KeyPairType.java
 */

package com.ldapauth.crypto;

/**
 * Key pair type. Enum constant names are compatible with JCA standard names.
 *
 * @see <a
 *      href="http://download.oracle.com/javase/6/docs/technotes/guides/security/StandardNames.html">JCA
 *      Standard Names</a>
 */
public enum KeyPairType {
	/** RSA key pair type. */
	RSA,
	/** DSA key pair type. */
	DSA,
	/** ECDSA key pair type. */
	ECDSA;
}
