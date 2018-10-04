package com.me.finalproj.dao;

import org.hibernate.HibernateException;
import org.hibernate.query.Query;

import com.me.finalproj.pojo.Event;
import com.me.finalproj.pojo.Visitor;

public class VisitorDAO extends DAO {

	public boolean insertVisitor(Visitor visitor) throws Exception {

		boolean userFound = false;
		try {
			begin();
			Query q = getSession().createQuery("from Person where username = :userName");
			q.setString("userName", visitor.getUserName());
			Visitor v = (Visitor) q.uniqueResult();
			if (v != null) {
				userFound = true;
				System.out.println("Vistor with USERNAME already exists in Database");
				return false;

			}

			if (userFound == false) {

				getSession().save(visitor);
				commit();
				return true;
			}
		} catch (HibernateException e) {
			rollback();
			throw new Exception("Exception while creating visitor: " + e.getMessage());
		} finally {
			close();
		}
		return false;
	}

	public Visitor searchVisitor(String username, String password) throws Exception {

		try {
			begin();
			Query q = getSession().createQuery("from Person where username = :userName AND password = :passwd");
			q.setString("userName", username);
			q.setString("passwd", password);
			Visitor v = (Visitor) q.uniqueResult();
			return v;
		} catch (HibernateException e) {
			rollback();
			throw new Exception("Exception while authenticating visitor: " + e.getMessage());

		} finally {
			close();
		}

	}

	public Visitor getVisitorById(String id) throws Exception {

		try {
			begin();
			Query q = getSession().createQuery("from Visitor where visitorId = :vid");
			q.setString("vid", id);
			Visitor e = (Visitor) q.uniqueResult();
			commit();
			return e;
		} catch (HibernateException e) {
			rollback();
			throw new Exception("Could not list the events", e);
		} finally {
			close();
		}

	}

	public boolean registerVisitorToEvent(Visitor v) throws Exception {
		try {
			begin();
			//Visitor vi=(Visitor)getSession().merge(v);
			getSession().update(v);
			commit();
			return true;
		} catch (HibernateException e) {
			rollback();
			throw new Exception("Exception while creating visitor: " + e.getMessage());
		} finally {
			close();
		}

	}
	
	public Visitor unregisterEvent(Visitor v) throws Exception {
		try {
			begin();
			getSession().update(v);
			commit();
			return v;
		} catch (HibernateException ex) {
			rollback();
			throw new Exception("Exception while creating visitor: " + ex.getMessage());
		} finally {
			close();
		}

	}

}
