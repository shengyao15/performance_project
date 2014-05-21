package com.hp.it.perf.ac.service.spfchain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hp.it.perf.ac.common.model.AcCommonData;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface AcLinkStrategy {
    Class<? extends AcLinkStrategyHandler> value();

    static interface AcLinkStrategyHandler {
	public boolean accept(AcCommonData data, Object annotation);
    }
}
