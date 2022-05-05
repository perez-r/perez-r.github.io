package com.snhu;

public class User {
	public String username;
	public String role;
	
	/**
	 * Default User Constructor
	 */
	public User() {}
	
	
		
	/**
	 * 
	 */
	public User(User user) {
		this.username = user.getUsername();
		this.role = user.getRole();
	}


	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}
	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}
	
	
}

