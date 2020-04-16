package org.irmacard.keyshare.web;

import foundation.privacybydesign.common.BaseConfiguration;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.ArrayList;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class KeyshareConfiguration extends BaseConfiguration<KeyshareConfiguration> {
	private static Logger logger = LoggerFactory.getLogger(KeyshareConfiguration.class);

	static {
		BaseConfiguration.clazz = KeyshareConfiguration.class;
		BaseConfiguration.environmentVarPrefix = "IRMA_KEYSHARE_CONF_";
		BaseConfiguration.confDirEnvironmentVarName = "IRMA_KEYSHARE_CONF";
		BaseConfiguration.logger = KeyshareConfiguration.logger;
		BaseConfiguration.printOnLoad = true;
		BaseConfiguration.confDirName = "irma_keyshare_server";
	}

	private String server_name = "IRMATestCloud";
	private String human_readable_name;

	private String jwt_privatekey = "sk.der";
	private String jwt_publickey = "pk.der";

	private String jwt_kid = "0";

	private int pinExpiry = 900; // 15 minutes

	private String mail_user = "";
	private String mail_password = "";
	private String mail_host = "";
	private String mail_from = "";
	private boolean mail_starttls_required = true;
	private int mail_port = 587;

	private Map<String,String> webclient_url;
	private String url = "http://localhost:8080/irma_keyshare_server/api/v1";
	private String cookie_domain;

	private String scheme_manager = "";
	private String issuer = "";
	private String login_credential = "";
	private String login_attribute = "";

	private ArrayList<String> email_attributes;
	private ArrayList<String> alt_login_attributes;

	private String defaultLanguage = "en";
	private Map<String, String> login_email_subject;
	private Map<String, String> login_email_body;
	private Map<String, String> confirm_email_body;
	private Map<String, String> confirm_email_subject;
	private Map<String, String> expire_email_subject;

	private boolean check_user_enrolled = true;

	private boolean unregister_expired_users = false;

	private int session_timeout = 30;
	private int rate_limit = 3;

    private String client_ip_header = null;

	// For postgres, use openejb:Resource/irma_keyshare
	private String database_jndi = "java:comp/env/jdbc/irma_keyshare"; // For MySQL

	private boolean go_irma_server = true;


	private String apiserver_publickey = "apiserver.der";
	private String schemeManager_publickey = "schemeManager.pk.pem";

	private transient PrivateKey jwtPrivateKey;
	private transient PublicKey jwtPublicKey;

    String events_webhook_uri = null;
    String events_webhook_authorizationToken = null;

    String schemeManager_update_uri = null;

	private String apiserver_url;
	private String apiserver_pk;

	public KeyshareConfiguration() {}

	public static KeyshareConfiguration getInstance() {
		return (KeyshareConfiguration) BaseConfiguration.getInstance();
	}

	public int getPinExpiry() {
		return pinExpiry;
	}

	public void setPinExpiry(int pinExpiry) {
		this.pinExpiry = pinExpiry;
	}

	public String getMailUser() {
		return mail_user;
	}

	public String getMailPassword() {
		return mail_password;
	}

	public String getMailHost() {
		return mail_host;
	}

	public boolean getStarttlsRequired() { return mail_starttls_required; }

	public int getMailPort() {
		return mail_port;
	}

	public String getMailFrom() {
		return mail_from;
	}

	public String getWebclientUrl(String lang) {
		return getTranslatedString(webclient_url, lang);
	}

	public boolean isHttpsEnabled() {
		return getWebclientUrl("").startsWith("https://");
	}

	public String getUrl() {
		return url;
	}

	public String getCookieDomain() {
		return cookie_domain;
	}

	public String getSchemeManager() {
		return scheme_manager;
	}

	public String getIssuer() {
		return issuer;
	}

	public String getLoginCredential() {
		return login_credential;
	}

	public String getLoginAttribute() {
		return login_attribute;
	}

	public ArrayList<String> getEmailAttributes() {
		return email_attributes;
	}

	public ArrayList<String> getAltLoginAttributes() {
		return alt_login_attributes;
	}

	public String getLoginEmailSubject(String lang) {
		return getTranslatedString(login_email_subject, lang);
	}

	public String getLoginEmailBody(String lang) {
		return getTranslatedString(login_email_body, lang);
	}

	public String getConfirmEmailSubject(String lang) {
		return getTranslatedString(confirm_email_subject, lang);
	}

	public String getConfirmEmailBody(String lang) {
		return getTranslatedString(confirm_email_body, lang);
	}

	public String getExpireEmailSubject(String lang) {
		return getTranslatedString(expire_email_subject, lang);
	}

	public String getExpireEmailBody(String lang) {
		if (!"en".equals(lang) && !"nl".equals(lang))
			lang = defaultLanguage;
		String filename = "expire-email." + lang + ".html";
		try {
			return new String(getResource(filename));
		} catch (Exception e) {
			logger.error("Failed to read " + filename);
			throw new RuntimeException(e);
		}
	}

	private String getTranslatedString(Map<String, String> map, String lang) {
		if (!map.containsKey(lang)) // TODO this is ugly, should keep track of supported languages
			lang = defaultLanguage;
		String retval = map.containsKey(lang) ? map.get(lang) : "";
		if (retval.isEmpty())
			logger.warn("Translation for %s in language %s not found", map.get(defaultLanguage), lang);
		return retval;
	}

	public boolean getCheckUserEnrolled() { return check_user_enrolled; }

	public boolean getUnregisterExpiredUsers() { return unregister_expired_users; }

	public int getSessionTimeout() {
		return session_timeout;
	}

	public int getRateLimit() {
		return rate_limit;
	}

	public PrivateKey getJwtPrivateKey() {
		if (jwtPrivateKey == null) {
			try {
				jwtPrivateKey = getPrivateKey(jwt_privatekey);
			} catch (KeyManagementException e) {
				throw new RuntimeException(e);
			}
		}

		return jwtPrivateKey;
	}

	public String getServerName() {
		return server_name;
	}

	public String getHumanReadableName() {
		if (human_readable_name == null || human_readable_name.length() == 0)
			return server_name;
		else
			return human_readable_name;
	}

    public String getSchemeManagerPublicKeyString() {
        try {
            return new String(getResource(schemeManager_publickey));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	public String getApiServerUrl() {
		return apiserver_url;
	}

	public PublicKey getApiServerPublicKey() {
		try {
			return getPublicKey(apiserver_publickey);
		} catch (KeyManagementException e) {
			throw new RuntimeException(e);
		}
	}

	public PublicKey getJwtPublicKey() {
		if (jwtPublicKey == null) {
			try {
				jwtPublicKey = getPublicKey(jwt_publickey);
			} catch (KeyManagementException e) {
				throw new RuntimeException(e);
			}
		}

		return jwtPublicKey;
	}

	public SignatureAlgorithm getJwtAlgorithm() {
		return SignatureAlgorithm.RS256;
	}

	public String getClientIp(HttpServletRequest req) {
		String ret;
		if (this.client_ip_header != null) {
			ret = req.getHeader(this.client_ip_header);
			if (ret != null) {
				return ret;
			}
		}
		return req.getRemoteAddr();
	}

	public String getDatabaseJndi() {
		return database_jndi;
	}

	public boolean goIrmaServer() {
		return go_irma_server;
	}

	public String getJwtKeyIdentifier() {
		return jwt_kid;
	}
}
