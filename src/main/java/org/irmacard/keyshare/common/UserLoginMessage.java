package org.irmacard.keyshare.common;

public class UserLoginMessage {
	private String username;
	private String password;
	private String pin;
	private String email;
	private String language;

	public UserLoginMessage() {};

	public UserLoginMessage(String username, String password, String pin) {
		this.username = username;
		this.password = password;
		this.pin = pin;
	}

	public UserLoginMessage(String username, String password, String pin) {
		this.username = username;
		this.password = password;
		this.pin = pin;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getPin() {
		return pin;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
