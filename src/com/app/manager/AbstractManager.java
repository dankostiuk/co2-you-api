package com.app.manager;

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
		startPersistence();
		_em.persist(object);
		_em.getTransaction().commit();
		closePersistence();
	}
	
	public T readTransaction(long id) {
		T object = _em.find(_clazz, id);
		
		return object;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> readAllTransaction() {
		List<T> resultList = 
			_em.createQuery("SELECT t from " + _clazz.getSimpleName() + " t")
				.getResultList();

		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public T findTransaction(String key, String value) throws EntityNotFoundException {
		List<T> resultList = _em.createQuery("SELECT t FROM " + _clazz.getSimpleName() + " t where t." + key + " = '" + value + "'")
				 .getResultList();
		 
		if (resultList.isEmpty())
		{
			throw new EntityNotFoundException("Entity could not be found.");
		}
		 
		T entity = resultList.get(0);
		return entity;
	}
	
	/**
	 * Helper method to start EntityManager transaction
	 */
	private void startPersistence()
	{
		_em.getTransaction().begin();
	}
	
	/**
	 * Helper method to close EntityManager and EntityManagerFactor transactions
	 */
	private void closePersistence()
	{
		_em.close();
		_emf.close();
	}
}
