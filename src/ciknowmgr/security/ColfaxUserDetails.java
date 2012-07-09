package ciknowmgr.security;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

import ciknowmgr.domain.Role;
import ciknowmgr.domain.User;


public class ColfaxUserDetails implements UserDetails{
	private static final long serialVersionUID = 5376329304842140187L;
	private User user;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
	
    public ColfaxUserDetails(User user){
    	this(user, true, true, true);
    }
    
	public ColfaxUserDetails(User user, boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired) {
		if (user == null 
			|| user.getUsername() == null 
			|| user.getUsername().equals("") 
			|| user.getPassword() == null)
			throw new IllegalArgumentException("username or password cannot be empty.");
		this.user = user;
		this.accountNonExpired = accountNonExpired;
		this.accountNonLocked = accountNonLocked;
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public Collection<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new LinkedList<GrantedAuthority>();
		Set<Role> roles = user.getRoles();
		if (roles == null) return authorities;

		for (Role role : roles){
			authorities.add(new GrantedAuthorityImpl(role.getName()));
		}
		
		return authorities;
	}

	public String getPassword() {
		return user.getPassword();
	}

	public String getUsername() {
		return user.getUsername();
	}

	public boolean isEnabled() {
		return user.getEnabled();
	}
	
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}
	
	public String[] getRoles(){
		Set<Role> roles = user.getRoles();
		if (roles == null) return null;
		String[] roleArray = new String[roles.size()];
		int i=0; 
		for (Role role : roles){
			roleArray[i] = role.getName();
			i++;
		}
		return roleArray;
	}
	
	public boolean hasRole(String role){
		Set<Role> roles = user.getRoles();
		for (Role r:roles){
			if (r.getName().equals(role)) return true;
		}
		return false;
	}
	
	public Long getId(){
		return user.getId();
	}
	
	public User getUser(){
		return user;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("ColfaxUserDetails[userId=").append(user.getId()).append(",");
		sb.append("username=").append(user.getUsername()).append(",");
		sb.append("]");
		return sb.toString();
	}
}
