package com.app.manager;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	}
	
	public AbstractManager(Class<T> clazz, EntityManagerFactory emf) {
		_clazz = clazz;
		_emf = emf;
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
	
	@SuppressWarnings("unchecked")
	public List<T> findManyTransaction(String key, String value) throws EntityNotFoundException {
		startTransaction();
		try {
			EntityTransaction t = _em.getTransaction();
			try {
				t.begin();
				List<T> resultList = _em.createQuery("SELECT t FROM " + _clazz.getSimpleName() + " t where t." + key + " = '" + value + "'")
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
	public List<T> runNativeQuery(String nativeQuery) {
		startTransaction();
		try {
			EntityTransaction t = _em.getTransaction();
			try {
				t.begin();
				List<T> resultList = _em.createNativeQuery(nativeQuery, _clazz).getResultList();
				t.commit();
				return resultList;
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
		if (_emf == null || !_emf.isOpen()) {
			
			// override sensitive values in persistence config with env vars 
			Map<String, String> env = System.getenv();
			Map<String, Object> configOverrides = new HashMap<String, Object>();
			for (String envName : env.keySet()) {
			    if (envName.contains("JAWSDB_PASSWORD")) {
			        configOverrides.put("javax.persistence.jdbc.password", env.get(envName));    
			    }
			}
			
			_emf = Persistence.createEntityManagerFactory("Hibernate", configOverrides);	
		}
		
		_em = _emf.createEntityManager();
	}
	
	/**
	 * Helper method to close EntityManager transactions
	 */
	private void closeTransaction()
	{
		_em.close();
		
		//TODO: remove this emf close since it takes time to start emf
		_emf.close();
	}
	
	public EntityManagerFactory getEntityManagerFactory() {
		return _emf;
	}
}
