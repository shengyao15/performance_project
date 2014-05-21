package com.hp.it.perf.ac.service.persist.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.Index.Duplicates;
import org.springframework.data.mongodb.core.index.IndexField;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Repository;

import com.hp.it.perf.ac.common.model.AcCategory;
import com.hp.it.perf.ac.common.model.AcDictionary;
import com.hp.it.perf.ac.common.model.AcidHelper;
import com.hp.it.perf.ac.service.persist.AcPersistException;
import com.hp.it.perf.ac.service.persist.dao.AcCommonDataWrapper;
import com.hp.it.perf.ac.service.persist.dao.AcNoSQLDatabaseDao;
import com.hp.it.perf.ac.service.persist.dao.AcSearchCriterial;

@Repository
public class AcCommonDataMongoDBDao implements
		AcNoSQLDatabaseDao<Long, AcCommonDataWrapper> {

	@Inject
	private MongoOperations mongoOperations;

	@Inject
	private AcDictionary dictionary;

	private final static Class<AcCommonDataWrapper> dataType = AcCommonDataWrapper.class;

	private final static int COLLECTION_MAX_SIZE = 1 << 8;

	private String[] collectionNames = new String[COLLECTION_MAX_SIZE];

	private Set<String> allCollections = new HashSet<String>();

	private AcidHelper acidHelper;

	private String idField;

	private static Logger log = LoggerFactory
			.getLogger(AcCommonDataMongoDBDao.class);

	protected String getCollectionNameById(long acid) {
		int category = acidHelper.getCategory(acid);
		return getCollectionNameByCategory(category);
	}

	private String getCollectionNameByCategory(int categoryCode) {
		categoryCode %= COLLECTION_MAX_SIZE;
		String name = collectionNames[categoryCode];
		if (name == null) {
			name = AcCommonDataWrapper.COLLECTION_PREFIX + categoryCode;
			collectionNames[categoryCode] = name;
		}
		return name;
	}

	@PostConstruct
	public void setupDao() {
		IndexOperations indexTemplate = mongoOperations.indexOps(dataType);
		acidHelper = AcidHelper.getInstance();
		for (AcCategory category : dictionary.categorys()) {
			String collectionName = getCollectionNameByCategory(category.code());
			if (!mongoOperations.collectionExists(collectionName)) {
				mongoOperations.createCollection(collectionName);
			}
			// no geo index suppose
			IndexOperations indexOps = mongoOperations.indexOps(collectionName);
			for (IndexInfo index : indexTemplate.getIndexInfo()) {
				Index indexDef = new Index();
				indexDef.named(index.getName());
				for (IndexField idxField : index.getIndexFields()) {
					indexDef.on(idxField.getKey(), idxField.getOrder());
				}
				if (index.isSparse()) {
					indexDef.sparse();
				}
				if (index.isDropDuplicates()) {
					indexDef.unique(Duplicates.DROP);
				} else if (index.isUnique()) {
					indexDef.unique();
				}
				indexOps.ensureIndex(indexDef);
			}
			allCollections.add(collectionName);
		}
		MongoRepositoryFactory repoFactory = new MongoRepositoryFactory(
				mongoOperations);
		MongoEntityInformation<AcCommonDataWrapper, Serializable> info = repoFactory
				.getEntityInformation(dataType);
		idField = info.getIdAttribute();
	}

	@Override
	public AcCommonDataWrapper findByKey(Long id) {
		String collectionName = getCollectionNameById(id);
		return mongoOperations.findById(id, dataType, collectionName);
	}

	@Override
	public void add(Collection<? extends AcCommonDataWrapper> data) {
		Map<String, List<AcCommonDataWrapper>> groups = new HashMap<String, List<AcCommonDataWrapper>>();
		for (AcCommonDataWrapper d : data) {
			String collectionName = getCollectionNameById(d.getAcid());
			List<AcCommonDataWrapper> list = groups.get(collectionName);
			if (list == null) {
				list = new ArrayList<AcCommonDataWrapper>();
				groups.put(collectionName, list);
			}
			list.add(d);
		}
		for (Map.Entry<String, List<AcCommonDataWrapper>> entry : groups
				.entrySet()) {
			mongoOperations.insert(entry.getValue(), entry.getKey());
		}
	}

	@Override
	public void delete(AcCommonDataWrapper... data) {
		Map<String, List<AcCommonDataWrapper>> groups = new HashMap<String, List<AcCommonDataWrapper>>();
		for (AcCommonDataWrapper d : data) {
			String collectionName = getCollectionNameById(d.getAcid());
			List<AcCommonDataWrapper> list = groups.get(collectionName);
			if (list == null) {
				list = new ArrayList<AcCommonDataWrapper>();
				groups.put(collectionName, list);
			}
			list.add(d);
		}

		for (Map.Entry<String, List<AcCommonDataWrapper>> entry : groups
				.entrySet()) {
			mongoOperations.remove(entry.getValue(), entry.getKey());
		}
	}

	@Override
	public void deleteByCriteria(List<AcSearchCriterial> criteria) {
		List<AcSearchCriterial> newSearch = new ArrayList<AcSearchCriterial>();
		String collectionName = determinCategoryInCriteria(criteria, newSearch);
		Query query = prepareCriteria(newSearch);
		mongoOperations.remove(query, collectionName);
	}

	@Override
	public void deleteAll() {
		for (String collectionName : allCollections) {
			mongoOperations.dropCollection(collectionName);
		}
	}

	@Override
	public long count() {
		long total = 0;
		for (String collectionName : allCollections) {
			total += mongoOperations.count(new Query(), collectionName);
		}
		return total;
	}

	@Override
	public long count(List<AcSearchCriterial> criteria) {
		List<AcSearchCriterial> newSearch = new ArrayList<AcSearchCriterial>();
		String collectionName = determinCategoryInCriteria(criteria, newSearch);
		Query query = prepareCriteria(newSearch);
		return mongoOperations.count(query, collectionName);
	}

	@Override
	public List<AcCommonDataWrapper> findByCriteria(
			List<AcSearchCriterial> criteria, String orderByField,
			final boolean ascOrderBy, int limit) {
		// need only one category
		List<AcSearchCriterial> newSearch = new ArrayList<AcSearchCriterial>();
		String collectionName = determinCategoryInCriteria(criteria, newSearch);
		Query query = prepareCriteria(optimizeCriteria(newSearch));
		Comparator<AcCommonDataWrapper> sorter = null;
		if (orderByField != null) {
			boolean useDuration = false;
			if ("duration".equals(orderByField)) {
				// use duration sec
				orderByField = "ds";
				useDuration = true;
			} else {
				throw new UnsupportedOperationException(
						"unsupported order by field: " + orderByField);
			}
			query.with(new Sort(ascOrderBy ? Sort.Direction.ASC
					: Sort.Direction.DESC, orderByField));
			if (useDuration) {
				sorter = new Comparator<AcCommonDataWrapper>() {

					@Override
					public int compare(AcCommonDataWrapper o1,
							AcCommonDataWrapper o2) {
						int x = o1.getDuration();
						int y = o2.getDuration();
						int v = (x < y) ? -1 : ((x == y) ? 0 : 1);
						return ascOrderBy ? v : -v;
					}
				};
			}
		}
		if (limit > 0) {
			query.limit(limit);
		}
		List<AcCommonDataWrapper> result = mongoOperations.find(query,
				dataType, collectionName);
		if (sorter != null) {
			Collections.sort(result, sorter);
		}
		return result;
	}

	private List<AcSearchCriterial> optimizeCriteria(
			List<AcSearchCriterial> searchCriteria) {
		List<AcSearchCriterial> criterias = new ArrayList<AcSearchCriterial>();
		for (AcSearchCriterial criteria : searchCriteria) {
			// mapping created to created min
			if ("created".equals(criteria.getCriterialName())) {
				List<Object> createdValues = criteria.getValue();
				List<Object> createdMinValues = new ArrayList<Object>();
				Calendar cal = Calendar.getInstance();
				for (Object createdValue : createdValues) {
					Date createdDate = (Date) createdValue;
					cal.setTime(createdDate);
					if (cal.get(Calendar.SECOND) != 0
							|| cal.get(Calendar.MILLISECOND) != 0) {
						log.warn(
								"optimize 'created' criteria has second/millisecond - {}",
								createdDate);
					}
					int createdMinValue = (int) (createdDate.getTime() / 60000);
					createdMinValues.add(createdMinValue);
				}
				criteria = new AcSearchCriterial("createdMin",
						createdMinValues, criteria.getOperation());
			}
			criterias.add(criteria);
		}
		return criterias;
	}

	protected String determinCategoryInCriteria(
			List<AcSearchCriterial> criteria, List<AcSearchCriterial> newSearch) {
		String collectionName = null;
		for (AcSearchCriterial search : criteria) {
			if ("category".equals(search.getCriterialName())) {
				if (search.getOperation() == AcSearchCriterial.Operation.EQUAL) {
					collectionName = getCollectionNameByCategory(((Number) search
							.getValue().get(0)).intValue());
					continue;
				} else {
					throw new AcPersistException(
							"only support equal search on category field: "
									+ search);
				}
			}
			newSearch.add(search);
		}
		if (collectionName == null) {
			throw new AcPersistException("need category in search condition");
		}
		return collectionName;
	}

	private Query prepareCriteria(List<AcSearchCriterial> criterials) {
		Query crit = new Query();
		for (AcSearchCriterial criterial : criterials) {
			switch (criterial.getOperation()) {
			case EQUAL:
				crit.addCriteria(Criteria.where(criterial.getCriterialName())
						.is(criterial.getValue().get(0)));
				break;
			case GREATER:
				crit.addCriteria(Criteria.where(criterial.getCriterialName())
						.gt(criterial.getValue().get(0)));
				break;
			case LESS:
				crit.addCriteria(Criteria.where(criterial.getCriterialName())
						.lt(criterial.getValue().get(0)));
				break;
			case BETWEEN:
				crit.addCriteria(Criteria.where(criterial.getCriterialName())
						.gte(criterial.getValue().get(0))
						.lte(criterial.getValue().get(1)));
				break;
			case IN:
				crit.addCriteria(Criteria.where(criterial.getCriterialName())
						.in(criterial.getValue()));
				break;
			case LE:
				crit.addCriteria(Criteria.where(criterial.getCriterialName())
						.lte(criterial.getValue().get(0)));
				break;
			case GE:
				crit.addCriteria(Criteria.where(criterial.getCriterialName())
						.gte(criterial.getValue().get(0)));
				break;
			// REGEX version
			case LIKE:
				crit.addCriteria(Criteria.where(criterial.getCriterialName())
						.regex((String) criterial.getValue().get(0)));
				break;
			default:
				break;
			}
		}
		return crit;
	}

	@Override
	public List<AcCommonDataWrapper> findByKeys(Long[] ids) {
		Map<String, List<Long>> groups = new HashMap<String, List<Long>>();
		for (Long id : ids) {
			String collectionName = getCollectionNameById(id);
			List<Long> list = groups.get(collectionName);
			if (list == null) {
				list = new ArrayList<Long>();
				groups.put(collectionName, list);
			}
			list.add(id);
		}
		List<AcCommonDataWrapper> result = new ArrayList<AcCommonDataWrapper>();
		for (Map.Entry<String, List<Long>> entry : groups.entrySet()) {
			result.addAll(mongoOperations.find(
					Query.query(Criteria.where(idField).in((Object[]) ids)),
					dataType, entry.getKey()));
		}
		return result;
	}
}
