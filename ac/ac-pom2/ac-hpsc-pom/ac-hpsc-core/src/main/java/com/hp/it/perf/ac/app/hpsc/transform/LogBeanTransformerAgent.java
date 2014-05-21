package com.hp.it.perf.ac.app.hpsc.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.it.perf.ac.common.model.AcCategory;
import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.common.model.AcDictionary;
import com.hp.it.perf.ac.common.model.AcidHelper;
import com.hp.it.perf.ac.common.model.support.AcRelatedFieldExtractor;
import com.hp.it.perf.ac.common.model.support.AnnotatedAcRelatedFieldExtractor;
import com.hp.it.perf.ac.common.model.support.AnnotatedToAcCommonDataAdapter;
import com.hp.it.perf.ac.common.model.support.ToAcCommonDataAdapter;
import com.hp.it.perf.ac.service.transform.AcTransformContext;
import com.hp.it.perf.ac.service.transform.AcTransformException;

public class LogBeanTransformerAgent {

	private Map<Class<?>, ToAcCommonDataAdapter> adpaters = new HashMap<Class<?>, ToAcCommonDataAdapter>();

	private Map<Class<?>, AcRelatedFieldExtractor> extractors = new HashMap<Class<?>, AcRelatedFieldExtractor>();

	public LogBeanTransformerAgent(Class<?> dbBeanClass, AcDictionary dictionary) {
		registerDbBean(dbBeanClass, dictionary);
	}

	public LogBeanTransformerAgent() {
	}

	public void registerDbBean(Class<?> dbBeanClass, AcDictionary dictionary) {
		AnnotatedToAcCommonDataAdapter dataAdpater = new AnnotatedToAcCommonDataAdapter();
		dataAdpater.setAnnotatedSource(dbBeanClass);
		dataAdpater.setDictionary(dictionary);
		adpaters.put(dbBeanClass, dataAdpater);
		AnnotatedAcRelatedFieldExtractor extractor = new AnnotatedAcRelatedFieldExtractor();
		extractor.setAnnotatedSource(dbBeanClass);
		extractors.put(dbBeanClass, extractor);
	}

	public AcCommonDataWithPayLoad[] transformTo(Object source,
			AcTransformContext collector) throws AcTransformException {
		List<AcCommonDataWithPayLoad> list = new ArrayList<AcCommonDataWithPayLoad>();
		setupCommonData(source, collector, list,
				new IdentityHashMap<Object, Object>(), null);
		return list.toArray(new AcCommonDataWithPayLoad[list.size()]);
	}

	private void setupCommonData(Object source, AcTransformContext collector,
			List<AcCommonDataWithPayLoad> commonDataList,
			Map<Object, Object> processed, AcCommonDataWithPayLoad refAcCommonData) {
		AcCommonDataWithPayLoad commonData = setupCommonData(source, collector);
		if (refAcCommonData != null) {
			commonData.setRefAcid(refAcCommonData.getAcid());
			// use ref ac created if this sub has no created value
			if (commonData.getCreated() == 0) {
				commonData.setCreated(refAcCommonData.getCreated());
			}
		}
		processed.put(source, null);
		commonDataList.add(commonData);
		Class<?> sourceClass = source.getClass();
		AcRelatedFieldExtractor extractor = extractors.get(sourceClass);
		Iterator<?> iterator = extractor.extract(source);
		while (iterator.hasNext()) {
			Object related = iterator.next();
			if (!processed.containsKey(related)) {
				setupCommonData(related, collector, commonDataList, processed,
						commonData);
			}
		}
	}

	protected AcCommonDataWithPayLoad setupCommonData(Object source,
			AcTransformContext collector) {
		Class<?> sourceClass = source.getClass();
		ToAcCommonDataAdapter dataAdpater = adpaters.get(sourceClass);
		if (dataAdpater == null) {
			throw new AcTransformException(sourceClass + " is not registered");
		}
		// create ac common data bean
		AcCommonDataWithPayLoad commonData = new AcCommonDataWithPayLoad();
		int categoryId = 0;
		for (AcCategory category : collector.getCoreRuntime().getSession()
				.getProfile().getDictionary().categorys()) {
			if (category.getPayloadClassName().equals(sourceClass.getName())) {
				categoryId = category.code();
				break;
			}
		}
		// create default acid
		long acid = AcidHelper.getInstance().getAcid(
				collector.getCoreRuntime().getProfileId(),
				collector.getCoreRuntime().nextSid(), categoryId, 0, 0);
		// assign ac id first
		commonData.setAcid(acid);
		dataAdpater.toCommonData(source, commonData);
		commonData.setPayLoad(source);
		return commonData;
	}

	void syncPayloadAcid(Object source, AcCommonData commonData) {
		Class<?> sourceClass = source.getClass();
		ToAcCommonDataAdapter dataAdpater = adpaters.get(sourceClass);
		if (dataAdpater == null) {
			throw new AcTransformException(sourceClass + " is not registered");
		}
		dataAdpater.setPayloadAcid(source, commonData);
	}

}
