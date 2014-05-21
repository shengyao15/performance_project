package com.hp.it.perf.ac.rest.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import com.hp.it.perf.ac.rest.exceptions.ResourceNotFoundException;
import com.hp.it.perf.ac.rest.model.Category;
import com.hp.it.perf.ac.rest.model.ChainEntry;
import com.hp.it.perf.ac.rest.model.Level;
import com.hp.it.perf.ac.rest.model.Type;
import com.hp.it.perf.ac.rest.service.IService;
import com.hp.it.perf.ac.rest.util.Constant;

import com.hp.it.perf.ac.common.model.AcCategory;
import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.common.model.AcDictionary;
import com.hp.it.perf.ac.common.model.AcLevel;
import com.hp.it.perf.ac.common.model.AcType;
import com.hp.it.perf.ac.common.model.ChainEntry.EntryData;
import com.hp.it.perf.ac.service.chain.AcRelation;
import com.hp.it.perf.ac.service.chain.ChainContext;
import com.hp.it.perf.ac.service.chain.ChainService;
import com.hp.it.perf.ac.service.chain.NodeNotFoundException;
import com.hp.it.perf.ac.service.data.AcRepositoryService;

@Service
public class ServiceImpl implements IService {

	private static final Logger log = LoggerFactory
			.getLogger(ServiceImpl.class);

	@Inject
	private ChainService chainService;

	@Inject
	private AcRepositoryService repositoryService;

	@Inject
	private AcDictionary dictionary;

	public ServiceImpl() {
		super();
	}

	public void setChainService(ChainService chainService) {
		this.chainService = chainService;
	}

	public void setRepositoryService(AcRepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public void setDictionary(AcDictionary dictionary) {
		this.dictionary = dictionary;
	}

	@Override
	public ChainEntry getFullChain(long acid) {
		String logPrefix = this.getClass().getName()
				+ ".getFullChain(long acid): ";

		log.debug(logPrefix + "enter.");
		log.debug(logPrefix + "acid: {}", acid);

		ChainEntry chain = new ChainEntry();
		try {
			com.hp.it.perf.ac.common.model.ChainEntry<AcRelation, ChainContext> fullChain = chainService
					.getFullChainByIdentifier(acid);
			log.debug(logPrefix + "orignal full chain: {}", fullChain);
			// get all acids from chain
			List<Long> acidList = getAcidsFromChain(fullChain);
			long[] acids = new long[acidList.size()];
			for (int i = 0; i < acidList.size(); i++) {
				acids[i] = acidList.get(i);
			}
			// get log detail list
			AcCommonData[] result = null;
			try {
				// get common data for specific acid
				result = repositoryService.getCommonDataWithoutPayLoad(acids);
			} catch (Exception e) {
				log.error(
						logPrefix
								+ " Error occurs when getting common datas for acids {}; nested exception is {}",
						e);
				throw new ResourceNotFoundException(
						"Error occurs when getting common datas for acids!", e);
			}
			// build chain entry
			chain = buildChainEntry(fullChain, listToMap(result), null);
		} catch (NodeNotFoundException e) {
			log.error(logPrefix + "cannot find chain for acid {}", acid, e);
			throw new ResourceNotFoundException("cannot find chain for acid: "
					+ acid, e);
		}
		log.debug(logPrefix + "is return.");
		return chain;
	}

	@Override
	public ChainEntry getFullChain(ChainContext context) {
		String logPrefix = this.getClass().getName()
				+ ".getFullChain(ChainContext context): ";
		log.debug(logPrefix + "enter.");
		log.debug(logPrefix + "chainContext: {}", context);

		ChainEntry chain = new ChainEntry();
		try {
			com.hp.it.perf.ac.common.model.ChainEntry<AcRelation, ChainContext> fullChain = chainService
					.getFullChainByPrimaryTrack(context);
			// get all acids from chain
			List<Long> acidList = getAcidsFromChain(fullChain);
			long[] acids = new long[acidList.size()];
			for (int i = 0; i < acidList.size(); i++) {
				acids[i] = acidList.get(i);
			}
			// get log detail list
			AcCommonData[] result = null;
			try {
				// get common data for specific acid
				result = repositoryService.getCommonDataWithoutPayLoad(acids);
			} catch (Exception e) {
				log.error(
						logPrefix
								+ " Error occurs when getting common datas for acids {}; nested exception is {}",
						e);
				throw new ResourceNotFoundException(
						"Error occurs when getting common datas for acids!", e);
			}
			// build chain entry
			chain = buildChainEntry(fullChain, listToMap(result), null);
		} catch (NodeNotFoundException e) {
			log.error(logPrefix + "cannot find chain for chainContext {}",
					context, e);
			throw new ResourceNotFoundException(
					"cannot find chain for chainContext: " + context, e);
		}
		log.debug(logPrefix + "is return.");
		return chain;
	}

	private ChainEntry buildChainEntry(
			com.hp.it.perf.ac.common.model.ChainEntry<AcRelation, ChainContext> fullChain,
			Map<Long, AcCommonData> result, ChainEntry parent) {
		if (fullChain == null) {
			return null;
		}
		ChainEntry chain = new ChainEntry();
		if (!fullChain.getDataNodes().isEmpty()) {
			if (fullChain.getDataNodes().size() == 1) {
				// if dataNodes only contains one item, eg request, WSRP etc.
				Long acid = fullChain.getDataNodes().get(0).getData().getAcid();
				AcCommonData acCommonData = result.get(acid);
				if (acCommonData != null) {
					chain = buildChainEntry(acCommonData, parent);
				}
			} else {
				// if dataNodes contains more than one items.
				if (parent == null) {
					parent = new ChainEntry();
				}
				for (EntryData<AcRelation> data : fullChain.getDataNodes()) {
					Long acid = data.getData().getAcid();
					// get details from result map
					AcCommonData acCommonData = result.get(acid);
					if (acCommonData != null) {
						AcCategory acCategory = null;
						AcType acType = null;
						try {
							acCategory = acCommonData.getCategory(dictionary);
							acType = acCommonData.getType(dictionary);
						} catch (Exception e) {
							log.error("Cannot get ac category or type for acid {}!", acCommonData.getAcid(), e);
						}
						chain.getEntries().add(
								new com.hp.it.perf.ac.rest.model.ChainEntry.EntryData(
										acCommonData.getAcid(), acCommonData.getName(), acCategory != null ? acCategory.name() : "",
										acType != null ? acType.name() : "", acCommonData.getDuration()));
					}
				}
			}
		}
		if (!fullChain.getChildEntryNodes().isEmpty()) {
			for (com.hp.it.perf.ac.common.model.ChainEntry<AcRelation, ChainContext> child : fullChain
					.getChildEntryNodes()) {
				chain.getChildren().add(buildChainEntry(child, result, chain));
			}
		}
		return chain;
	}

	private ChainEntry buildChainEntry(AcCommonData data, ChainEntry parent) {
		String logPrefix = this.getClass().getName()
				+ ".buildChainEntry(...): ";

		if (data == null) {
			// should never happen...
			return null;
		}
		ChainEntry chain = new ChainEntry();
		// set acid
		chain.setAcid(data.getAcid());
		// set name
		chain.setName(data.getName());
		// set type
		AcType acType = null;
		try {
			acType = data.getType(dictionary);
		} catch (Exception e) {
			log.error(logPrefix + "cannot get ac type for acid {}",
					data.getAcid(), e);
			throw new ResourceNotFoundException("cannot get ac type for acid: "
					+ data.getAcid(), e);
		}
		if (acType != null) {
			chain.setType(acType.name());
		}
		// set duration
		chain.setDuration(data.getDuration());
		// set parent
		chain.setParent(parent);
		return chain;
	}

	private List<Long> getAcidsFromChain(
			com.hp.it.perf.ac.common.model.ChainEntry<AcRelation, ChainContext> chain) {
		List<Long> acids = new ArrayList<Long>();
		if (chain == null) {
			return acids;
		}
		// get acid from data nodes
		if (!chain.getDataNodes().isEmpty()) {
			for (EntryData<AcRelation> data : chain.getDataNodes()) {
				acids.add(data.getData().getAcid());
			}
		}
		// handle child nodes
		if (!chain.getChildEntryNodes().isEmpty()) {
			for (com.hp.it.perf.ac.common.model.ChainEntry<AcRelation, ChainContext> child : chain
					.getChildEntryNodes()) {
				acids.addAll(getAcidsFromChain(child));
			}
		}

		return acids;
	}

	private Map<Long, AcCommonData> listToMap(AcCommonData[] datas) {
		Map<Long, AcCommonData> result = new HashMap<Long, AcCommonData>();
		if (datas == null || datas.length == 0) {
			return result;
		}
		for (AcCommonData data : datas) {
			result.put(data.getAcid(), data);
		}
		return result;
	}

	@Override
	public Category[] getCategories(boolean includeall) {
		// get categories
		AcCategory[] acCategories = dictionary.categorys();
		if (acCategories == null || acCategories.length == 0) {
			return null;
		}
		Category[] categories = new Category[acCategories.length];
		for (int i = 0; i < acCategories.length; i++) {
			Category tmp = convertAcCategory(acCategories[i], includeall);
			categories[i] = tmp;
		}
		// if include all enabled
		if (includeall) {
			Category[] result = Arrays
					.copyOf(categories, categories.length + 1);
			result[categories.length] = getCategoryForAll();
			categories = null;
			return result;
		}
		return categories;
	}

	private Category convertAcCategory(AcCategory acCategory, boolean includeall) {
		if (acCategory == null) {
			return null;
		}
		Category category = new Category();
		category.setId(acCategory.code());
		category.setValue(acCategory.name());
		category.setTypes(convertAcTypes(acCategory.types(), includeall));
		category.setLevels(convertAcLevels(acCategory.levels(), includeall));
		return category;
	}

	private Type[] convertAcTypes(AcType[] acTypes, boolean includeall) {
		if (acTypes == null || acTypes.length == 0) {
			return null;
		}
		Type[] types = new Type[acTypes.length];
		for (int i = 0; i < acTypes.length; i++) {
			Type tmp = new Type();
			tmp.setId(acTypes[i].code());
			tmp.setValue(acTypes[i].name());
			types[i] = tmp;
		}
		// if include all enabled
		if (includeall) {
			Type[] result = Arrays.copyOf(types, types.length + 1);
			result[types.length] = getTypeForAll();
			types = null;
			return result;
		}
		return types;
	}

	private Level[] convertAcLevels(AcLevel[] acLevels, boolean includeall) {
		if (acLevels == null || acLevels.length == 0) {
			return null;
		}
		Level[] levels = new Level[acLevels.length];
		for(int i=0; i < acLevels.length; i++) {
			Level tmp = new Level();
			tmp.setId(acLevels[i].code());
			tmp.setValue(acLevels[i].name());
			levels[i] = tmp;
		}
		// if include all enabled
		if (includeall) {
			Level[] result = Arrays.copyOf(levels, levels.length + 1);
			result[levels.length] = getLevelForAll();
			levels = null;
			return result;
		}
		return levels;
	}
	
	private Type getTypeForAll() {
		Type all = new Type();
		all.setId(Constant.FLAG_INT);
		all.setValue(Constant.ALL);
		return all;
	}
	
	private Level getLevelForAll() {
		Level all = new Level();
		all.setId(Constant.FLAG_INT);
		all.setValue(Constant.ALL);
		return all;
	}

	private Category getCategoryForAll() {
		Category all = new Category();
		all.setId(Constant.FLAG_INT);
		all.setValue(Constant.ALL);
		all.setTypes(new Type[] { getTypeForAll() });
		return all;
	}
}