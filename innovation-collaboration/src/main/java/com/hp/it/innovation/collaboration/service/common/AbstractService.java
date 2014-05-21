package com.hp.it.innovation.collaboration.service.common;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.hp.it.innovation.collaboration.dao.BaseDAO;
import com.hp.it.innovation.collaboration.model.Component;

@SuppressWarnings("rawtypes")
public abstract class AbstractService <T extends Component, D extends BaseDAO<T, ID>, ID extends Serializable> implements ApplicationContextAware{

	protected static final String DEF_TX_NAME = "default";

	private ApplicationContext ctx;

	//protected StopWatch s = new StopWatch();

	protected final Log log = LogFactory.getLog(getClass());

	private D dao;
	private PlatformTransactionManager transactionManager;

	private ThreadLocal<Map<String, TransactionStatus>> txStatusMap = new ThreadLocal<Map<String, TransactionStatus>>();

	// private Session session;

	public AbstractService() {
	}

	public D getDao() {
		if (dao == null) {
			this.dao = instantiateDao();
		}

		return dao;
	}

	public void setDao(D dao) {
		this.dao = dao;
	}

	public void setApplicationContext(ApplicationContext ctx) {
		this.ctx = ctx;
	}

	public T findById(ID id) {
		T entity = getDao().findById(id, false);
		return entity;
	}

	public T fetchById(ID id) {
		return getDao().fetchById(id, false);
	}

	public List<T> findAll() {
		return getDao().findAll();
	}

	public List<T> findPageByPage(int firstResult, int maxResults) {
		return getDao().findPageByPage(firstResult, maxResults);
	}

	public List<T> findByExample(T exampleInstance, String... excludeProperty) {
		return getDao().findByExample(exampleInstance, excludeProperty);
	}

	public T save(T entity) {
//		T newEntity = getDao().merge(entity);
//		if (entity.getPrimaryKeyObj() == null) {
//			entity.setPrimaryKeyObj(newEntity.getPrimaryKeyObj());
//		}
//		
//		return newEntity;
		return getDao().save(entity);
	}
	
	public T merge(T entity) {
		return getDao().merge(entity);
	}
	
	public T persist(T entity) {
		return getDao().persist(entity);
	}

	public void delete(T entity) {
		getDao().remove(entity);
	}

	public void delete(ID id) {
		getDao().remove(id);
	}

	protected boolean startTxNew() {
		return startTxNew(null);
	}

	protected boolean startTxNew(String txName) {
		boolean started = false;
		if (transactionManager != null) {
			DefaultTransactionDefinition def = new DefaultTransactionDefinition();
			if (StringUtils.isNotBlank(txName)) {
				def.setName(txName);
			}
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

			TransactionStatus status = transactionManager.getTransaction(def);
			if (status != null) {
				Map<String, TransactionStatus> map = txStatusMap.get();
				if (map == null) {
					map = new HashMap<String, TransactionStatus>();
				}
				String key = getKey(txName);
				map.put(key, status);

				txStatusMap.set(map);
				started = true;
			}
		}

		return started;
	}

	protected boolean rollBackCurrentTx() {
		return rollBackCurrentTx(null);
	}

	protected boolean rollBackCurrentTx(String txName) {
		boolean rolledBack = false;
		Map<String, TransactionStatus> map = txStatusMap.get();
		if (map != null) {
			String key = getKey(txName);
			TransactionStatus status = map.get(key);
			if (status != null) {
				transactionManager.rollback(status);
				rolledBack = true;
			}
		}

		return rolledBack;
	}

	protected boolean commitCurrentTx() {
		return commitCurrentTx(null);
	}

	protected boolean commitCurrentTx(String txName) {
		boolean committed = false;
		Map<String, TransactionStatus> map = txStatusMap.get();
		if (map != null) {
			String key = getKey(txName);
			TransactionStatus status = map.get(key);
			if (status != null) {
				transactionManager.commit(status);
				committed = true;
			}
		}

		return committed;
	}

	protected String getKey(String txName) {
		String key = txName;
		if (StringUtils.isBlank(key))
			key = DEF_TX_NAME;

		return key;
	}

	@SuppressWarnings("unchecked")
	protected D instantiateDao() {
		Class<D> daoClass = null;

		Class clazz = getClass();
		boolean correctType = false;
		while (!correctType) {
			Type type = clazz.getGenericSuperclass();
			if (type instanceof ParameterizedType) {
				Type rawType = ((ParameterizedType) type).getRawType();
				if (rawType instanceof Class) {
					if (((Class) rawType).getName().equalsIgnoreCase(
							AbstractBaseComponentServiceImpl.class.getName())) {
						correctType = true;
						daoClass = (Class<D>) ((ParameterizedType) type).getActualTypeArguments()[2];
						break;
					}
				}
			}

		}

		return (D) ctx.getBean(daoClass.getName());
	}

	public PlatformTransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public void flush() {
		getDao().flush();
	}
	
	public void evict(T entity) {
		getDao().evict(entity);
	}

}
