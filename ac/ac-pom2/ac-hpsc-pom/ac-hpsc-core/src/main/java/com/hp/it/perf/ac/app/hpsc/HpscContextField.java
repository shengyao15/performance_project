package com.hp.it.perf.ac.app.hpsc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

import com.hp.it.perf.ac.app.hpsc.HpscDictionary.HpscContextType;
import com.hp.it.perf.ac.common.model.AcContext;
import com.hp.it.perf.ac.common.model.support.AcCommonDataField;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HpscContextField {

	public class HpscContextConverter implements AcCommonDataField.Converter {

		private HpscContextField hpscContextField;

		@Override
		public void setSource(AnnotatedElement source)
				throws IllegalArgumentException {
			HpscContextField hpscContextField = source
					.getAnnotation(HpscContextField.class);
			if (hpscContextField == null) {
				throw new IllegalArgumentException("no "
						+ HpscContextField.class + " present on " + source);
			}
			this.hpscContextField = hpscContextField;
		}

		@Override
		public Object convert(Object value) {
			String strValue;
			if (value != null) {
				strValue = String.valueOf(value);
			} else {
				strValue = "";
			}
			if (hpscContextField.ignoreEmpty() && strValue.trim().length() == 0) {
				return null;
			}
			AcContext context = new AcContext();
			context.setCode(hpscContextField.value().code());
			context.setValue(strValue);
			return context;
		}

	}

	HpscContextType value();

	boolean ignoreEmpty() default false;

}
