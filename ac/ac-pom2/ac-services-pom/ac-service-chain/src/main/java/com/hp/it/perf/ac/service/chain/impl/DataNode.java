package com.hp.it.perf.ac.service.chain.impl;

import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@TypeAlias("DataNode")
@NodeEntity
public class DataNode {

	@GraphId
	private Long id;
	
	@Indexed
	private long identifier;

	@RelatedTo(type = "LINK", direction = Direction.OUTGOING)
	private LinkNode link;

	public void setIdentifier(long identifier) {
		this.identifier = identifier;
	}

	public long getIdentifier() {
		return identifier;
	}

	public LinkNode getLink() {
		return link;
	}

	public void setLink(LinkNode link) {
		this.link = link;
	}

	public Long getId() {
    	return id;
    }
	
	@Override
	public int hashCode() {
        return (id == null) ? 0 : id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		DataNode other = (DataNode) obj;
		if (id == null) return other.id == null;
        return id.equals(other.id);
    }
	
	@Override
    public String toString() {
        return String.format("DataNode{identifier='%s'}", identifier);
    }
}
