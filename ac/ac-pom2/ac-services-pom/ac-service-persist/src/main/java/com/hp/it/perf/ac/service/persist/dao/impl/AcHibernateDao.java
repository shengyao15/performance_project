package com.hp.it.perf.ac.service.persist.dao.impl;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.hp.it.perf.ac.service.persist.AcDaoContext;
import com.hp.it.perf.ac.service.persist.AcPersistException;
import com.hp.it.perf.ac.service.persist.dao.AcPersistDao;
import com.hp.it.perf.ac.service.persist.dao.AcSearchCriterial;

@Repository
public class AcHibernateDao implements AcPersistDao {

	@Inject
	private HibernateTemplate template;

	private static Logger logger = LoggerFactory
			.getLogger(AcHibernateDao.class);

	private AcDaoContext daoContext;

	public void setDaoContext(AcDaoContext daoContext) {
		this.daoContext = daoContext;
	}

	public boolean save(Serializable entity) throws AcPersistException {
		try {
			template.save(entity);
			return true;
		} catch (DataAccessException e) {
			logger.error("Save failed", e);
			daoContext.dataRollbacked(Arrays.asList(entity), e);
			return false;
		}
	}

	public boolean save(final List<? extends Serializable> entities)
			throws AcPersistException {
		try {
			template.executeWithNativeSession(new HibernateCallback<Object>() {
				public Object doInHibernate(Session session)
						throws HibernateException {
					Transaction transaction = session.beginTransaction();
					boolean success = false;
					try {
						for (Object entity : entities) {
							session.save(entity);
						}
						transaction.commit();
						success = true;
					} finally {
						if (!success) {
							transaction.rollback();
						}
					}
					return null;
				}
			});
			logger.debug("save successed with list of size {}", entities.size());
			return true;
		} catch (DataAccessException e) {
			logger.error("Save failed", e);
			daoContext.dataRollbacked(entities, e);
			return false;
		}
	}

	public <T extends Serializable> T get(long acid, Class<T> beanClass)
			throws AcPersistException {
		try {
			T data = beanClass.cast(template.get(beanClass, acid));
			return data;
		} catch (DataAccessException e) {
			logger.error("load error", e);
			throw new AcPersistException("load ac data error for acid '" + acid
					+ "' for bean " + beanClass, e);
		}
	}

	public Serializable find(long acid) throws AcPersistException {
		Class<?> beanClass = getBeanClass(acid);
		try {
			Serializable data = (Serializable) template.get(beanClass, acid);
			return data;
		} catch (DataAccessException e) {
			logger.error("load error", e);
			throw new AcPersistException("find ac data error for acid '" + acid
					+ "' for bean " + beanClass, e);
		}
	}

	public <T extends Serializable> List<T> get(long[] acids, Class<T> beanClass)
			throws AcPersistException {
		try {
			List<Long> acidList = new ArrayList<Long>(acids.length);
			for (long acid : acids) {
				acidList.add(acid);
			}
			// TODO why parameter is acid
			DetachedCriteria criteria = DetachedCriteria.forClass(beanClass);
			criteria.add(Restrictions.in("acid", acidList));
			List<T> retList = new ArrayList<T>(acids.length);
			for (Object obj : template.findByCriteria(criteria)) {
				// TODO do we need to keep null if it is not found
				retList.add(beanClass.cast(obj));
			}
			return retList;
		} catch (DataAccessException e) {
			logger.error("load error", e);
			throw new AcPersistException("get ac data list error for bean "
					+ beanClass, e);
		}
	}

	public List<Serializable> find(long[] acids) {
		try {
			Map<Class<?>, List<Long>> acidClassList = new HashMap<Class<?>, List<Long>>(
					acids.length);
			for (long acid : acids) {
				Class<?> beanClass = getBeanClass(acid);
				List<Long> acidList = acidClassList.get(beanClass);
				if (acidList == null) {
					acidList = new ArrayList<Long>();
					acidClassList.put(beanClass, acidList);
				}
				acidList.add(acid);
			}
			List<Serializable> retList = new ArrayList<Serializable>(
					acids.length);
			for (Map.Entry<Class<?>, List<Long>> entry : acidClassList
					.entrySet()) {
				// TODO why parameter is acid
				DetachedCriteria criteria = DetachedCriteria.forClass(entry
						.getKey());
				criteria.add(Restrictions.in("acid", entry.getValue()));
				for (Object obj : template.findByCriteria(criteria)) {
					// TODO do we need to keep sequence and null?
					retList.add((Serializable) (entry.getKey().cast(obj)));
				}
			}
			return retList;
		} catch (DataAccessException e) {
			logger.error("load error", e);
			throw new AcPersistException("get ac data list error", e);
		}
	}

	public <T extends Serializable> List<T> query(
			List<AcSearchCriterial> criterials, Class<T> clazz) {
		if (criterials == null || criterials.size() == 0) {
			return Collections.emptyList();
		}
		DetachedCriteria crit = prepareCriteria(criterials, clazz);
		return template.findByCriteria(crit);
	}

	private DetachedCriteria prepareCriteria(
			List<AcSearchCriterial> criterials, Class<?> clazz) {
		DetachedCriteria crit = DetachedCriteria.forClass(clazz);
		for (AcSearchCriterial criterial : criterials) {
			switch (criterial.getOperation()) {
			case EQUAL:
				crit.add(Restrictions.eq(criterial.getCriterialName(),
						criterial.getValue().get(0)));
				break;
			case GREATER:
				crit.add(Restrictions.gt(criterial.getCriterialName(),
						criterial.getValue().get(0)));
				break;
			case LESS:
				crit.add(Restrictions.lt(criterial.getCriterialName(),
						criterial.getValue().get(0)));
				break;
			case BETWEEN:
				crit.add(Restrictions.between(criterial.getCriterialName(),
						criterial.getValue().get(0), criterial.getValue()
								.get(1)));
				break;
			case IN:
				crit.add(Restrictions.in(criterial.getCriterialName(),
						criterial.getValue()));
				break;
			case LE:
				crit.add(Restrictions.le(criterial.getCriterialName(),
						criterial.getValue().get(0)));
				break;
			case GE:
				crit.add(Restrictions.ge(criterial.getCriterialName(),
						criterial.getValue().get(0)));
				break;
			case LIKE:
				crit.add(Restrictions.like(criterial.getCriterialName(),
						criterial.getValue().get(0)));
				break;
			default:
				break;
			}
		}
		return crit;
	}

	public long count(List<AcSearchCriterial> criterials, Class<?> clazz) {
		if (criterials == null || criterials.size() == 0) {
			return 0;
		}
		DetachedCriteria crit = prepareCriteria(criterials, clazz);
		crit.setProjection(Projections.rowCount());
		return ((Number) template.findByCriteria(crit).get(0)).longValue();
	}

	protected Class<?> getBeanClass(long acid) {
		return daoContext.mapBeanClass(acid);
	}

	@SuppressWarnings("unchecked")
	public <T extends Serializable> List<T> queryBySQL(final String sql,
			final Map<String, String> params, final Class<T> clazz) {
		
		return template.executeFind(new HibernateCallback<List<T>>() {

			@Override
			public List<T> doInHibernate(Session session)
					throws HibernateException, SQLException {
				try {
					Query query;
					if (clazz != null) {
						query = session.createSQLQuery(sql).addEntity(clazz);
					} else {
						query = session.createSQLQuery(sql);
					}
					if (params != null) {
						for (Map.Entry<String, String> param : params
								.entrySet()) {
							query.setParameter(param.getKey(), param.getValue());
						}
					}
					return query.list();
				} catch (HibernateException ex) {
					logger.error("Query failed", ex);
					// FIXME error handling
					return null;
				}
			}
		});
	}
	
	public void delete(long [] acids) throws AcPersistException {
		final List<Serializable> entities = this.find(acids);
		try {
			template.executeWithNativeSession(new HibernateCallback<Object>() {
				public Object doInHibernate(Session session)
						throws HibernateException {
					Transaction transaction = session.beginTransaction();
					boolean success = false;
					try {
						for (Object entity : entities) {
							session.delete(entity);
						}
						transaction.commit();
						success = true;
					} finally {
						if (!success) {
							//transaction.rollback();
						}
					}
					return null;
				}
			});
			logger.debug("delete successed with list of size {}", entities.size());
		} catch (DataAccessException e) {
			logger.error("Delete failed", e);
		}
	}
	
	public void deleteAll(Class<?> beanClass) throws AcPersistException {
		throw new UnsupportedOperationException();
		
	}



}
