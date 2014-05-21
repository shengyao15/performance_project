package com.hp.it.perf.ac.service.persist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hp.it.perf.ac.common.core.AcSession;
import com.hp.it.perf.ac.common.model.AcCategory;
import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.common.model.AcDictionary;
import com.hp.it.perf.ac.common.model.AcidHelper;
import com.hp.it.perf.ac.common.model.AcidSelectors.AcidRange;
import com.hp.it.perf.ac.common.model.AcidSelectors.SingleAcidSelector;
import com.hp.it.perf.ac.common.realtime.TimeWindow;
import com.hp.it.perf.ac.core.AcDataSubscriber;
import com.hp.it.perf.ac.service.persist.dao.AcCommonDataWrapper;
import com.hp.it.perf.ac.service.persist.dao.AcNoSQLDatabaseDao;
import com.hp.it.perf.ac.service.persist.dao.AcPersistDao;
import com.hp.it.perf.ac.service.persist.dao.AcSearchCriterial;

@Service
public class AcPersistServiceImpl implements AcPersistService, AcDaoContext {

	private static Logger log = LoggerFactory
			.getLogger(AcPersistServiceImpl.class);

	@Inject
	private AcDictionary dictionary;

	private List<Class<?>> beanClassMapping = new ArrayList<Class<?>>();

	@Inject
	private AcPersistDao dao;

	@Inject
	private AcNoSQLDatabaseDao<Long, AcCommonDataWrapper> acCommonDataDao;

	@Inject
	private AcSession acSession;

	@PostConstruct
	protected void init() {
		// suppose bean class are all presented
		int maxCode = -1;
		for (AcCategory category : dictionary.categorys()) {
			maxCode = Math.max(maxCode, category.code());
		}
		for (int i = 0; i < maxCode + 1; i++) {
			beanClassMapping.add(null);
		}
		for (AcCategory category : dictionary.categorys()) {
			String className = category.getPayloadClassName();
			Class<?> clazz;
			try {
				clazz = Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("no class def", e);
			}
			beanClassMapping.set(category.code(), clazz);
		}
		dao.setDaoContext(this);
	}

	@Override
	public AcCommonDataWithPayLoad read(long acid) {
		AcCommonDataWithPayLoad acCommonDataWithPayLoad = new AcCommonDataWithPayLoad(
				readCommonData(acid));
		Serializable acBean = dao.find(acid);
		acCommonDataWithPayLoad.setPayLoad(acBean);
		return acCommonDataWithPayLoad;
	}

	// TODO update iterator read method to batch, need implement PayLoad
	// transform to AcCommonDataWithPayLoad function.
	@Override
	public AcCommonDataWithPayLoad[] read(long[] acids) {
		List<AcCommonDataWithPayLoad> acCommonDataWithPayLoads = new ArrayList<AcCommonDataWithPayLoad>();
		if (acids.length != 0 && acids != null) {
			for (int i = 0; i < acids.length; i++) {
				acCommonDataWithPayLoads.add(read(acids[i]));
			}
		} else {
			return new AcCommonDataWithPayLoad[0];
		}
		AcCommonDataWithPayLoad[] results = acCommonDataWithPayLoads
				.toArray(new AcCommonDataWithPayLoad[acCommonDataWithPayLoads
						.size()]);
		return results;
	}

	@Override
	public long count() {
		return acCommonDataDao.count();
	}

	@AcDataSubscriber(threadCount = 1, queueSize = 10000, maxBufferSize = 1000, value = "persist-payload")
	public void onDataForPayload(AcCommonDataWithPayLoad... data) {
		// TODO check profile id
		log.debug("Data, size : [{}] coming", data.length);
		Map<Class<?>, Integer> classIndex = new LinkedHashMap<Class<?>, Integer>();
		List<List<Serializable>> list = new ArrayList<List<Serializable>>();
		int count = 0;
		for (AcCommonDataWithPayLoad acCommonDataWithPayLoad : data) {
			Serializable acBean = (Serializable) acCommonDataWithPayLoad
					.getPayLoad();
			// only store top level common data
			if (acBean != null && acCommonDataWithPayLoad.getRefAcid() == 0L) {
				Integer index = classIndex.get(acBean.getClass());
				List<Serializable> dbList;
				if (index == null) {
					index = list.size();
					classIndex.put(acBean.getClass(), index);
					dbList = new ArrayList<Serializable>();
					list.add(dbList);
				} else {
					dbList = list.get(index.intValue());
				}
				dbList.add(acBean);
				count++;
			}
			count++;
		}
		List<Serializable> allList = new ArrayList<Serializable>(count);
		for (List<Serializable> dbList : list) {
			allList.addAll(dbList);
		}
		if (!allList.isEmpty()) {
			dao.save(allList);
		}
	}

	@AcDataSubscriber(threadCount = 1, queueSize = 10000, maxBufferSize = 1000, value = "persist-common")
	public void onDataForCommonData(AcCommonDataWithPayLoad... data) {
		// TODO check profile id
		log.debug("Data, size : [{}] coming", data.length);
		List<AcCommonDataWrapper> wrapperList = new ArrayList<AcCommonDataWrapper>();
		for (AcCommonDataWithPayLoad acCommonDataWithPayLoad : data) {
			wrapperList.add(AcCommonDataWrapper
					.toWrapper(acCommonDataWithPayLoad));
		}
		acCommonDataDao.add(wrapperList);
	}

	@Override
	public Class<?> mapBeanClass(long acid) {
		int categoryCode = AcidHelper.getInstance().getCategory(acid);
		AcCategory category = dictionary.category(categoryCode);
		Class<?> beanClass = beanClassMapping.get(category.code());
		if (beanClass == null) {
			throw new IllegalArgumentException("no class mapping for acid: "
					+ acid + " with category: " + category);
		}
		return beanClass;
	}

	@Override
	public void dataRollbacked(List<? extends Serializable> entities,
			Throwable cause) {
		if (entities.size() == 1) {
			log.error("data rollbacked: {} @{}", entities.get(0),
					entities.get(0).getClass().getName());
		} else {
			log.warn("data rollbacked: {}, try one-by-one store",
					entities.size());
			for (Serializable entity : entities) {
				dao.save(entity);
			}
		}
	}

	@Override
	public AcCommonData readCommonData(long acid) {
		AcCommonDataWrapper acCommonDataWrapper = acCommonDataDao
				.findByKey(acid);
		if (acCommonDataWrapper == null) {
			throw new AcPersistException("no ac commont data found by id: "
					+ acid);
		}
		return AcCommonDataWrapper.toCommonData(acCommonDataWrapper);
	}

	@Override
	public AcCommonData[] readCommonData(long[] acids) {
		Long[] acidList = new Long[acids.length];
		int index = 0;
		for (long id : acids) {
			acidList[index++] = id;
		}
		List<AcCommonDataWrapper> list = acCommonDataDao.findByKeys(acidList);
		AcCommonData[] ret = new AcCommonData[list.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = AcCommonDataWrapper.toCommonData(list.get(i));
		}
		return ret;
	}

	@Override
	public AcCommonData[] readCommonData(AcPersistCondition condition) {
		List<AcSearchCriterial> list = new ArrayList<AcSearchCriterial>();
		AcidRange acidSelector = condition.getAcidRange();
		if (acidSelector == null) {
			throw new AcPersistException("need acid range in query");
		}
		if (acidSelector.getSelectorObject() instanceof SingleAcidSelector) {
			SingleAcidSelector singleSelector = (SingleAcidSelector) acidSelector
					.getSelectorObject();
			Integer category = singleSelector.category();
			if (category != null) {
				list.add(new AcSearchCriterial("category", category));
			}
			Integer type = singleSelector.type();
			if (type != null) {
				list.add(new AcSearchCriterial("type", type));
			}
			Integer level = singleSelector.level();
			if (level != null) {
				list.add(new AcSearchCriterial("level", level));
			}
		}
		String name = condition.getName();
		if (name != null) {
			list.add(new AcSearchCriterial("name", name,
					AcSearchCriterial.Operation.EQUAL));
		}
		TimeWindow timeWindow = condition.getTimeWindow();
		if (timeWindow != null) {
			if (timeWindow.getStartTime() != null) {
				if (timeWindow.getEndTime() != null) {
					list.add(new AcSearchCriterial("created",
							Arrays.asList(timeWindow.getStartTime(),
									timeWindow.getEndTime()),
							AcSearchCriterial.Operation.BETWEEN));
				} else {
					list.add(new AcSearchCriterial("created", timeWindow
							.getStartTime(), AcSearchCriterial.Operation.GE));
				}
			} else if (timeWindow.getEndTime() != null) {
				list.add(new AcSearchCriterial("created", timeWindow
						.getEndTime(), AcSearchCriterial.Operation.LE));
			}
		}
		list.add(new AcSearchCriterial("acid", Arrays.asList(
				acidSelector.getMinBound(), acidSelector.getMaxBound()),
				AcSearchCriterial.Operation.BETWEEN));
		List<AcCommonDataWrapper> result = acCommonDataDao.findByCriteria(list,
				condition.getOrderBy(), condition.isOrderByDirection(),
				condition.getLimitNumber());
		AcCommonData[] ret = new AcCommonData[result.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = AcCommonDataWrapper.toCommonData(result.get(i));
		}
		return ret;
	}

	@Override
	public void delete(long[] acids) throws AcPersistException {
		dao.delete(acids);
		List<AcSearchCriterial> criteriaList = new ArrayList<AcSearchCriterial>();
		criteriaList.add(new AcSearchCriterial("acid", Arrays.asList(acids), AcSearchCriterial.Operation.IN));
		acCommonDataDao.deleteByCriteria(criteriaList);
	}

	@Override
	public void deleteAll() throws AcPersistException {
		//dao.deleteAll(beanClass);
		
		acCommonDataDao.deleteAll();
	}
}
