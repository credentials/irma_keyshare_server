package org.irmacard.keyshare.common;

public class IRMAHeaders {
	// TODO remove these when old protocol is deprecated
	public static final String USERNAME_OLD = "IRMA_Username";
	public static final String AUTHORIZATION_OLD = "IRMA_Authorization";

	public static final String USERNAME = "X-IRMA-Keyshare-Username";
	public static final String AUTHORIZATION = "X-IRMA-Keyshare-Authorization";
	public static final String VERSION = "X-IRMA-Keyshare-ProtocolVersion";
}
