package com.app.manager;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
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
		_em = _emf.createEntityManager();
		
	}
	
	public void writeTransaction(T object) {
		try {
			if (!_em.getTransaction().isActive()) {
				startTransaction();
			}
			_em.persist(object);
			_em.getTransaction().commit();
		} finally {
			closeTransaction();
		}
	}
	
	public T readTransaction(long id) {
		try {
			if (!_em.getTransaction().isActive()) {
				startTransaction();
			}
			
			T object = _em.find(_clazz, id);
			_em.getTransaction().commit();

			return object;
		} finally {
			closeTransaction();
		}	
	}
	
	@SuppressWarnings("unchecked")
	public List<T> readAllTransaction() {
		try {
			if (!_em.getTransaction().isActive()) {
				startTransaction();
			}
			
			List<T> resultList = 
				_em.createQuery("SELECT t from " + _clazz.getSimpleName() + " t")
					.getResultList();
			_em.getTransaction().commit();
			
			return resultList;
		} finally {
			closeTransaction();
		}
	}
	
	@SuppressWarnings("unchecked")
	public T findTransaction(String key, String value) throws EntityNotFoundException {
		
		try {
			if (!_em.getTransaction().isActive()) {
				startTransaction();
			}
			
			List<T> resultList = _em.createQuery("SELECT t FROM " + _clazz.getSimpleName() + " t where t." + key + " = '" + value + "'")
					 .getResultList();
			_em.getTransaction().commit();
			
			if (resultList.isEmpty())
			{
				return null;
			}
			 
			T entity = resultList.get(0);
			
			return entity;
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
		_em.getTransaction().begin();
	}
	
	/**
	 * Helper method to close EntityManager transactions
	 */
	private void closeTransaction()
	{
		_em.close();
	}
}
