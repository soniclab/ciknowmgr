package ciknowmgr.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ciknowmgr.domain.Role;

public class User implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long version;
	private String username;	// required
	private String password = "";
	private String firstName = "";
	private String lastName = "";
	private String midName = "";
	private String addr1 = "";
	private String addr2 = "";
	private String city = "";
	private String state = "";
	private String country = "";
	private String zipcode = "";
	private String email = "";
	private String phone = "";
	private String cell = "";
	private String fax = "";
    private String department = "";
    private String organization = "";
    private String unit = "";
    private Boolean enabled = true;	//required
    
    private Map<String, String> attributes = new HashMap<String, String>();
    private Set<Role> roles = new HashSet<Role>();
    private Set<Project> projects = new HashSet<Project>();
    
    public boolean isAdmin(){
    	for (Role role : roles){
    		if (role.getName().equals("ROLE_ADMIN")) return true;    		
    	}
    	return false;
    }
    
    
    public void update(User u){
    	this.id = u.id;
    	this.version = u.version;
    	this.username = u.username;
    	this.password = u.password;
    	this.firstName = u.firstName;
    	this.lastName = u.lastName;
    	this.midName = u.midName;
    	this.addr1 = u.addr1;
    	this.addr2 = u.addr2;
    	this.city = u.city;
    	this.state = u.state;
    	this.country = u.country;
    	this.zipcode = u.zipcode;
    	this.email = u.email;
    	this.phone = u.phone;
    	this.cell = u.cell;
    	this.fax = u.fax;
    	this.department = u.department;
    	this.organization = u.organization;
    	this.unit = u.unit;
    	this.enabled = u.enabled;
    	this.attributes = new HashMap<String, String>(u.getAttributes());
    	this.roles = new HashSet<Role>(u.getRoles());
    	this.projects = new HashSet<Project>(u.getProjects());
    }
    
    
    
    //////////////////// GETTERS/SETTERS ////////////////////////////
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
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
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMidName() {
		return midName;
	}
	public void setMidName(String midName) {
		this.midName = midName;
	}
	public String getAddr1() {
		return addr1;
	}
	public void setAddr1(String addr1) {
		this.addr1 = addr1;
	}
	public String getAddr2() {
		return addr2;
	}
	public void setAddr2(String addr2) {
		this.addr2 = addr2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCell() {
		return cell;
	}
	public void setCell(String cell) {
		this.cell = cell;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}	
	
	public Set<Project> getProjects() {
		return projects;
	}
	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("User[username=").append(username).append(", ");
		sb.append("email=").append(email).append(", ");
		sb.append("enabled=").append(enabled);
		sb.append("]");
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
    
    
}
