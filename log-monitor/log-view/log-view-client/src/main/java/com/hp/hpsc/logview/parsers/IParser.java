package com.hp.hpsc.logview.parsers;

import java.util.List;

import com.hp.hpsc.logview.po.Link;
import com.hp.hpsc.logview.po.ParserParameters;

public interface IParser {

	public abstract List<Link> resolve(ParserParameters params);

}