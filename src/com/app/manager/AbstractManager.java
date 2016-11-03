package com.app.manager;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;


/**
 * Abstract class responsible for carrying out 
 * low-level transactions on the ORM.
 * 
 * @author dan
 *
 * @param <T> The entity type.
 */
public abstract class AbstractManager<T> {

	private final Class<T> _clazz;

	private EntityManager _em;
	private EntityManagerFactory _emf;
	
	public AbstractManager(Class<T> clazz) {
		_clazz = clazz;
		_emf = Persistence.createEntityManagerFactory("Hibernate");
	}
	
	public void writeTransaction(T object) {
		startTransaction();
		try {
			EntityTransaction t = _em.getTransaction();
			try {
				t.begin();
				_em.persist(object);
				t.commit();
			} finally {
				if (t.isActive()) t.rollback();
			}
		} finally {
			closeTransaction();
		}
	}
	
	public T readTransaction(long id) {
		startTransaction();
		try {
			EntityTransaction t = _em.getTransaction();
			try {
				t.begin();
				T object = _em.find(_clazz, id);
				t.commit();
				return object;
			} finally {
				if (t.isActive()) t.rollback();
			}
		} finally {
			closeTransaction();
		}	
	}
	
	@SuppressWarnings("unchecked")
	public List<T> readAllTransaction() {
		startTransaction();
		try {
			EntityTransaction t = _em.getTransaction();
			try {
				t.begin();
				List<T> resultList = 
						_em.createQuery("SELECT t from " + _clazz.getSimpleName() + " t")
							.getResultList();
				t.commit();
				return resultList;
			} finally {
				if (t.isActive()) t.rollback();
			}
		} finally {
			closeTransaction();
		}
	}
	
	@SuppressWarnings("unchecked")
	public T findTransaction(String key, String value) throws EntityNotFoundException {
		startTransaction();
		try {
			EntityTransaction t = _em.getTransaction();
			try {
				t.begin();
				List<T> resultList = _em.createQuery("SELECT t FROM " + _clazz.getSimpleName() + " t where t." + key + " = '" + value + "'")
						 .getResultList();
				t.commit();
				
				if (resultList.isEmpty())
				{
					return null;
				}
				 
				T entity = resultList.get(0);
				
				return entity;
			} finally {
				if (t.isActive()) t.rollback();
			}
		} finally {
			closeTransaction();
		}
	}
	
	/**
	 * Connect to Heroku db
	 * @return
	 * @throws URISyntaxException
	 * @throws SQLException
	 */
	private Connection getConnection() throws URISyntaxException, SQLException {
	    String dbUrl = System.getenv("JDBC_DATABASE_URL");
	    return DriverManager.getConnection(dbUrl);
	}
	
	/**
	 * Helper method to start EntityManager transaction
	 */
	private void startTransaction()
	{
		_em = _emf.createEntityManager();
	}
	
	/**
	 * Helper method to close EntityManager transactions
	 */
	private void closeTransaction()
	{
		_em.close();
	}
}
