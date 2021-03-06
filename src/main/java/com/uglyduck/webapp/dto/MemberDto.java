package com.uglyduck.webapp.dto;

import java.sql.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class MemberDto {

	// Fields
	@NotEmpty(message = "아이디를 작성해주세요.")
	@Pattern(regexp = "^[0-9a-z]([-_]?[0-9a-z]){4,20}$", message="5~20자의 영문 소문자, 숫자와 특수기호(_),(-)만 가능")
	private String id;
	@NotEmpty(message = "비밀번호를 작성해주세요.")
	@Pattern(regexp = "^[a-zA-Z].{8,20}$", message="8~20자 영문 대소문자, 숫자, 특수문자를 사용")
	private String pw;
	@NotEmpty(message = "이름을 작성해주세요.")
	@Pattern(regexp = "^[가-힣a-zA-Z]{2,20}$", message="한글과 영문 대소문자를 사용 (특수기호, 공백 사용 불가)")
	private String name;
	@NotEmpty(message = "닉네임을 작성해주세요.")
	@Pattern(regexp = "^[가-힣a-zA-Z]{2,20}$", message="한글과 영문 대소문자를 사용 (특수기호, 공백 사용 불가)")
	private String nickname;
	@NotEmpty(message = "이메일을 작성해주세요.")
	@Email(message = "이메일 형식으로 입력해주세요.")
	private String email;
	private String ip;
	private Date reg_date;
	private String role;
	private String uuid;
	private Date log;
	
	// Constructor
	public MemberDto() { }
	public MemberDto( String id, String pw, String name, String nickname, String email, String ip,
			Date reg_date, String role, String uuid, Date log) {
		this.id = id;
		this.pw = pw;
		this.name = name;
		this.nickname = nickname;
		this.email = email;
		this.ip = ip;
		this.reg_date = reg_date;
		this.role = role;
		this.uuid = uuid;
		this.log = log;
	}
	// getters/setters
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Date getReg_date() {
		return reg_date;
	}
	public void setReg_date(Date reg_date) {
		this.reg_date = reg_date;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Date getLog() {
		return log;
	}
	public void setLog(Date log) {
		this.log = log;
	}
	@Override
	public String toString() {
		return "MemberDto [id=" + id + ", pw=" + pw + ", name=" + name + ", nickname=" + nickname + ", email=" + email
				+ ", ip=" + ip + ", reg_date=" + reg_date + ", role=" + role + ", uuid=" + uuid + ", log=" + log + "]";
	}
	
	
}
