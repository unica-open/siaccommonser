/*
*SPDX-FileCopyrightText: Copyright 2020 | CSI Piemonte
*SPDX-License-Identifier: EUPL-1.2
*/
package it.csi.siac.siaccommonser.integration.dao.base;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import it.csi.siac.siaccommon.util.log.LogUtil;

@Transactional
public class JpaDao<E, PK> implements Dao<E, PK> {

	protected LogUtil log = new LogUtil(getClass());
	private final String listAllQueryName;

	protected final Class<E> entityClass;

	@PersistenceContext
	protected EntityManager entityManager;

	/**
	 * Crea una nuova istanza della classe.
	 */
	protected JpaDao() {
		this.entityClass = getEntityClass(getClass());
		listAllQueryName = entityClass.getSimpleName() + ".findAll";
	}

	@SuppressWarnings("unchecked")
	private Class<E> getEntityClass(Class<?> c) {
		Type type = c.getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			return (Class<E>) ((ParameterizedType) type).getActualTypeArguments()[0];
		}
		return getEntityClass((Class<?>) type);
	}

	@Override
	public void save(E entity) {
		final String methodName = "[save] - ";
		try {
			log.info(methodName, "inizio");
			//entity.setUid(null) quando E estendera' SiacTBase
			entityManager.persist(entity);
			log.info(methodName, "save successful");
		} catch (RuntimeException re) {
			log.error(methodName, "save failed", re);
			throw re;
		} finally {
			log.info(methodName, "fine");
		}
	}

	protected Query createNamedQuery(String queryName) {
		try {
			return entityManager.createNamedQuery(queryName);
		} catch (IllegalArgumentException e) {
			throw new UnsupportedOperationException("problema creazione NamedQuery " + queryName, e);
		}
	}
	protected TypedQuery<E> createTypedNamedQuery(String queryName) {
		return createTypedNamedQuery(queryName, entityClass);
	}
	protected <T> TypedQuery<T> createTypedNamedQuery(String queryName, Class<T> clazz) {
		try {
			return entityManager.createNamedQuery(queryName, clazz);
		} catch (IllegalArgumentException e) {
			throw new UnsupportedOperationException("problema creazione NamedQuery " + queryName, e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<E> findAll(int... rowStartIdxAndCount) {
		final String methodName = "[findAll] - ";
		try {
			log.info(methodName, "inizio");
			Query query = createNamedQuery(listAllQueryName);
			if (rowStartIdxAndCount != null && rowStartIdxAndCount.length > 0) {
				int rowStartIdx = Math.max(0, rowStartIdxAndCount[0]);
				if (rowStartIdx > 0) {
					query.setFirstResult(rowStartIdx);
				}

				if (rowStartIdxAndCount.length > 1) {
					int rowCount = Math.max(0, rowStartIdxAndCount[1]);
					if (rowCount > 0) {
						query.setMaxResults(rowCount);
					}
				}
			}
			return query.getResultList();
		} catch (RuntimeException re) {
			log.error(methodName, "findAll failed", re);
			throw re;
		} finally {
			log.info(methodName, "fine");
		}
	}

	@Override
	public E findById(PK id) {
		final String methodName = "[findById] - ";
		try {
			log.info(methodName, "inizio");
			return entityManager.find(entityClass, id);
		} catch (RuntimeException re) {
			log.error(methodName, "findById failed", re);
			throw re;
		} finally {
			log.info(methodName, "fine");
		}
	}

	@Override
	public void delete(E entity) {
		final String methodName = "[delete] - ";
		try {
			log.info(methodName, "inizio");
			entityManager.remove(entity);
			log.info(methodName, "delete successful");
		} catch (RuntimeException re) {
			log.error(methodName, "delete failed", re);
			throw re;
		} finally {
			log.info(methodName, "fine");
		}
	}
	
	@Override
	public E create(E entity){
		final String methodName = "[create] - ";
		try {
			log.info(methodName, "inizio");
			entityManager.persist(entity);
			log.info(methodName, "create successful");
			return entity;
		} catch (RuntimeException re) {
			log.error(methodName, "create failed", re);
			throw re;
		} finally {
			log.info(methodName, "fine");
		}
	}	
	
	@Override
	public E update(E entity) {
		final String methodName = "[update] - ";
		try {
			log.info(methodName, "inizio");
			E result = entityManager.merge(entity);
			log.info(methodName, "update successful");
			return result;
		} catch (RuntimeException re) {
			log.error(methodName, "update failed", re);
			throw re;
		} finally {
			log.info(methodName, "fine");
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> getList(String jpql, Map<String, Object> parameters) {
		return createQuery(jpql, parameters).getResultList();
	}
	
	protected <T> List<T> getTypedList(String jpql, Map<String, Object> parameters, Class<T> clazz) {
		return createTypedQuery(jpql, parameters, clazz).getResultList();
	}
	protected List<E> getTypedList(String jpql, Map<String, Object> parameters) {
		return getTypedList(jpql, parameters, entityClass);
	}

	
	@SuppressWarnings("unchecked")
	protected <T> Page<T> getPagedList(String jpql, Map<String, Object> parameters, Pageable pageable) {

		String jpqlCount = getCountQuery(jpql);
		Query qn = createQuery(jpqlCount, parameters);

		long count = ((Number) qn.getSingleResult()).longValue();

		List<T> resultList = new ArrayList<T>();
		if (count > 0) {
			Query query = createQuery(jpql, parameters);
			query.setFirstResult(pageable.getOffset());
			query.setMaxResults(pageable.getPageSize());
			resultList = query.getResultList();
		}

		Page<T> pagedList = new PageImpl<T>(resultList, pageable, count);

		return pagedList;
	}
	protected <T> Page<T> getTypedPagedList(String jpql, Map<String, Object> parameters, Pageable pageable, Class<T> clazz) {

		String jpqlCount = getCountQuery(jpql);
		TypedQuery<Number> qn = createTypedQuery(jpqlCount, parameters, Number.class);

		long count = qn.getSingleResult().longValue();

		List<T> resultList = new ArrayList<T>();
		if (count > 0) {
			TypedQuery<T> query = createTypedQuery(jpql, parameters, clazz);
			query.setFirstResult(pageable.getOffset());
			query.setMaxResults(pageable.getPageSize());
			resultList = query.getResultList();
		}
		return new PageImpl<T>(resultList, pageable, count);
	}
	protected Page<E> getTypedPagedList(String jpql, Map<String, Object> parameters, Pageable pageable) {
		return getTypedPagedList(jpql, parameters, pageable, entityClass);
	}

	private String getCountQuery(String jpql) {
		String jpqlCount = "";

		int fromIndex = jpql.toUpperCase(Locale.ITALIAN).indexOf("FROM");
		jpqlCount = jpql.substring(fromIndex);

		int toIndex = jpqlCount.toUpperCase(Locale.ITALIAN).lastIndexOf("ORDER BY");
		if (toIndex != -1) {
			jpqlCount = jpqlCount.substring(0, toIndex);
		}

		jpqlCount = String.format("SELECT COUNT(*) %s", jpqlCount);
		return jpqlCount;
	}

	protected Query createQuery(String jpql, Map<String, Object> parameters) {
		Query query = entityManager.createQuery(jpql);

		if (parameters != null) {
			setQueryParameters(query, parameters);
		}

		return query;
	}
	protected <T> TypedQuery<T> createTypedQuery(String jpql, Map<String, Object> parameters, Class<T> clazz) {
		TypedQuery<T> query = entityManager.createQuery(jpql, clazz);

		if (parameters != null) {
			setQueryParameters(query, parameters);
		}

		return query;
	}
	protected TypedQuery<E> createTypedQuery(String jpql, Map<String, Object> parameters) {
		return createTypedQuery(jpql, parameters, entityClass);
	}

	protected int executeNativeQuery(String sql, Map<String, Object> parameters) {
		Query query = entityManager.createNativeQuery(sql);

		if (parameters != null) {
			setQueryParameters(query, parameters);
		}

		return query.executeUpdate();
	}

	protected <T> Query createNativeQuery(String sql, Class<T> entityClass, Map<String, Object> parameters) {
		Query query = entityManager.createNativeQuery(sql, entityClass);

		if (parameters != null) {
			setQueryParameters(query, parameters);
		}

		return query;
	}

	protected Query createNativeQuery(String sql, Map<String, Object> parameters) {
		Query query = entityManager.createNativeQuery(sql);

		if (parameters != null) {
			setQueryParameters(query, parameters);
		}

		return query;
	}

	private void setQueryParameters(Query query, Map<String, Object> parameters) {
		for (Map.Entry<String, Object> param : parameters.entrySet()) {
			query.setParameter(param.getKey(), param.getValue());
		}
	}

	protected int executeQuery(String jpql, Map<String, Object> parameters) {
		return createQuery(jpql, parameters).executeUpdate();
	}
	
	protected PersistenceUnitUtil getPersistenceUnitUtil() {
		return entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
	}
	
	@Override
	public void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}
	
	@Override
	public void flush() {
		entityManager.flush();
	}
	
	
	protected String getSiacTClassDataValiditaSql(String tabAlias, String annoParam) {
		
		return new StringBuilder()
				
			.append(" AND (YEAR(CURRENT_DATE)!=:").append(annoParam).append(" AND YEAR(").append(tabAlias).append(".dataInizioValidita)<=:").append(annoParam).append(" ")
			.append("      OR YEAR(CURRENT_DATE)=:").append(annoParam).append(" AND DATE_TRUNC('day', ").append(tabAlias).append(".dataInizioValidita) <= CURRENT_DATE) ")
				
			.append(" AND (").append(tabAlias).append(".dataFineValidita IS NULL ")
			.append("		OR :").append(annoParam).append("<YEAR(CURRENT_DATE) AND ").append(tabAlias).append(".dataFineValidita>=DATE_TRUNC('day',TO_TIMESTAMP(CONCAT(:").append(annoParam).append(", ' 12 31'), 'YYYY MM DD')) ")
			.append("     OR :").append(annoParam).append(">=YEAR(CURRENT_DATE) AND DATE_TRUNC('day', ").append(tabAlias).append(".dataFineValidita)>=CURRENT_DATE) ")
				
			.toString();
	}
}
