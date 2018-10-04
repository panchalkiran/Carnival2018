package com.me.finalproj.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "email_table")
public class Email {
	
	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "visit"))
	@Column(name = "emailID", unique = true, nullable = false)
	private long id;

	@Column(name = "email_address")
	private String emailAddress;

	@OneToOne
	@PrimaryKeyJoinColumn
	private Visitor visit;

	public Email() {
	}

	public Email(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Visitor getVisit() {
		return visit;
	}

	public void setVisit(Visitor visit) {
		this.visit = visit;
	}

}
