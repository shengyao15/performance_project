package com.hp.it.perf.ac.service.spfchain;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.hp.it.perf.ac.app.hpsc.HpscDictionary;
import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.core.AcDataSubscriber;
import com.hp.it.perf.ac.service.chain.ChainService;

@Service
public class SpfChainServiceImpl implements SpfChainService {

	private static final Logger log = LoggerFactory
			.getLogger(SpfChainServiceImpl.class);
	private AcLinkerManager linkerManager;

	@Resource
	private ApplicationContext applicationContext;

	private AtomicInteger count = new AtomicInteger();

	@Inject
	private ChainService chainService;
	

	@PostConstruct
	public void initLinkers() {
		// setup linkers
		linkerManager = new AcLinkerManager();
		for (AcLinker linker : applicationContext
				.getBeansOfType(AcLinker.class).values()) {
			linkerManager.registerLinker(linker);
			log.info("register linker: {}", linker);
		}
	}

	protected AcLinker getTargetLinker(AcCommonData data) {
		final List<AcLinker> list = linkerManager.getLinkersFor(data);
		if (list.size() == 0) {
			return null;
		} else if (list.size() == 1) {
			return list.get(0);
		} else {
			return new AcLinker() {

				@Override
				public void performLink(AcCommonData data, AcLinkContext context)
						throws AcProcessException {
					for (AcLinker linker : list) {
						linker.performLink(data, context);
					}
				}
			};
		}
	}

	@AcDataSubscriber(queueSize = 10000, maxBufferSize = 5000)
	public void onData(AcCommonData... data) {
		// setup link context
		AcBatchLinkContext batchLinkContext = new AcBatchLinkContext();
		batchLinkContext.setChainService(chainService);
		for (AcCommonData d : data) {
			onSingleData(d, batchLinkContext);
		}
		batchLinkContext.flush();
	}

	private void onSingleData(AcCommonData data, AcBatchLinkContext batchLinkContext) {
		// link executor
		AcLinker linker = getTargetLinker(data);
		if (linker != null) {
			linker.performLink(data, batchLinkContext);
		} else {
			if (log.isDebugEnabled()) {
				log.debug("no linker found for data: {}, with category: {}",
						data, data.getCategory(HpscDictionary.INSTANCE));
			}
		}
		if (count.incrementAndGet() % 5000 == 0) {
			log.info("Processed loaded data: {}", count);
		}
	}
	
	public void deleteChainData(List<Long> acids){
		long start = System.currentTimeMillis();
		int deletedNodeCount = chainService.batchDeletedNode(acids);
		log.info("Delete {} of nodes", deletedNodeCount);
		log.info("Total Time for the deleting in chain: {}ms", (System.currentTimeMillis() - start));
	}
	
	public void deleteAll(){
		long start = System.currentTimeMillis();
		chainService.clear();
		log.info("Total Time for the deleting in database: {}ms", (System.currentTimeMillis() - start));
	}
}
