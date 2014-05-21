package com.hp.it.perf.ac.service.persist.dao.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Repository;

import com.hp.it.perf.ac.service.persist.AcDaoContext;
import com.hp.it.perf.ac.service.persist.dao.AcNoSQLDatabaseDao;
import com.hp.it.perf.ac.service.persist.dao.AcSearchCriterial;

@Repository
public class AcMongoDBDao<K, V extends Serializable> implements
		AcNoSQLDatabaseDao<K, V> {

	private AcDaoContext daoContext;

	@Inject
	private MongoOperations mongoOperations;

	private Class<V> dataType;

	private String keyField;

	private boolean nativeKey;

	public AcMongoDBDao(Class<V> dataType, String keyField) {
		this.dataType = dataType;
		this.keyField = keyField;
	}

	public AcMongoDBDao(Class<V> dataType) {
		this.dataType = dataType;
	}

	@PostConstruct
	public void setupDao() {
		MongoRepositoryFactory repoFactory = new MongoRepositoryFactory(
				mongoOperations);
		MongoEntityInformation<V, Serializable> info = repoFactory
				.getEntityInformation(dataType);
		String idAttribute = info.getIdAttribute();
		if (keyField == null || idAttribute.equals(keyField)) {
			keyField = idAttribute;
			nativeKey = true;
		} else {
			nativeKey = false;
		}
	}

	@Override
	public V findByKey(K id) {
		return nativeKey ? mongoOperations.findById(id, dataType)
				: mongoOperations.findOne(
						Query.query(Criteria.where(keyField).is(id)), dataType);
	}

	@Override
	public void delete(V... data) {
		for(V object : data) {
			mongoOperations.remove(object);
		}
	}
	
	@Override
	public void deleteByCriteria(List<AcSearchCriterial> criteria) {
		Query query = prepareCriteria(criteria);
		mongoOperations.remove(query, dataType);
	}
	
	@Override
	public void deleteAll() {
		mongoOperations.dropCollection(dataType);
	}

	@Override
	public long count() {
		return mongoOperations.count(new Query(), dataType);
	}

	@Override
	public void add(Collection<? extends V> data) {
		// TODO handle rollback case
		mongoOperations.insert(data, dataType);
	}

	@Override
	public long count(List<AcSearchCriterial> criteria) {
		Query query = prepareCriteria(criteria);
		return mongoOperations.count(query, dataType);
	}

	@Override
	public List<V> findByKeys(K[] ids) {
		return mongoOperations.find(
				Query.query(Criteria.where(keyField).in(ids)), dataType);
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
	public List<V> findByCriteria(List<AcSearchCriterial> criteria,
			String orderByField, boolean ascOrderBy, int limit) {
		Query query = prepareCriteria(criteria);
		if (orderByField != null) {
			query.with(new Sort(ascOrderBy ? Sort.Direction.ASC
					: Sort.Direction.DESC, orderByField));
		}
		if (limit > 0) {
			query.limit(limit);
		}
		return mongoOperations.find(query, dataType);
	}

}
