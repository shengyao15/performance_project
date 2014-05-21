package com.hp.it.perf.ac.service.spfchain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.service.spfchain.AcLinkStrategy.AcLinkStrategyHandler;
import com.hp.it.perf.ac.service.spfchain.PerfLinkTarget.PerfLinkStrategyHandler;
import com.hp.it.perf.ac.app.hpsc.HpscDictionary;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@AcLinkStrategy(PerfLinkStrategyHandler.class)
public @interface PerfLinkTarget {

	String[] value() default {};

	static class PerfLinkStrategyHandler implements AcLinkStrategyHandler {
		public boolean accept(AcCommonData data, Object ann) {
			PerfLinkTarget annotation = (PerfLinkTarget) ann;
			for (String category : annotation.value()) {
				if (category.equals(data.getCategory(HpscDictionary.INSTANCE).name())) {
					return true;
				}
			}
			return false;
		}
	}

}
