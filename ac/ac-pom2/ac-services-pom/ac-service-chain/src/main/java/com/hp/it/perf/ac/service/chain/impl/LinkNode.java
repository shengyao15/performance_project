package com.hp.it.perf.ac.service.chain.impl;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.GraphTraversal;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.core.FieldTraversalDescriptionBuilder;
import org.springframework.data.neo4j.mapping.Neo4jPersistentProperty;

@NodeEntity
@TypeAlias("LinkNode")
public class LinkNode {

	@GraphId
	private Long id;

	@Indexed
	private String tValue;

	@GraphTraversal(traversal = DataLinkTraversalBuilder.class, elementClass = DataNode.class, params = { "LINK" })
	private Iterable<DataNode> dataList;

	@RelatedTo(type = "PARENT", direction = Direction.OUTGOING)
	private LinkNode parent;

	@GraphTraversal(traversal = ChildLinkTraversalBuilder.class, elementClass = LinkNode.class, params = { "PARENT" })
	private Iterable<LinkNode> childLinkList;

	public int getType() {
		return tValue != null ? tValue.charAt(0) : 0;
	}

	public String getValue() {
		return tValue != null ? tValue.substring(1) : null;
	}

	public Iterable<DataNode> getDataList() {
		return dataList;
	}

	// type:(only 2-bytes)
	// 1: DiagnosticID
	// 2: PortletName
	// 3: PORTLET_TRANSACTION
	// value: any none-null string
	public void setTypeValue(int type, String value) {
		if (value == null) {
			throw new IllegalArgumentException("null value is not accept");
		}
		this.tValue = toTypeValue(type, value);
	}
	
	static String toTypeValue(int type, String value) {
		return Character.toString((char) type) + value;
	}

	public void setParent(LinkNode parent) {
		// TODO detect cycle parent
		this.parent = parent;
	}

	public LinkNode getParent() {
		return parent;
	}
	
	String getTypeValue() {
		return tValue;
	}

	public Iterable<LinkNode> getChildLinkList() {
		return childLinkList;
	}

	public LinkNode getAncestor() {
		if (parent == null) {
			return this;
		}
		return parent.getAncestor();
	}

	private static class DataLinkTraversalBuilder implements FieldTraversalDescriptionBuilder {

		@Override
		public TraversalDescription build(Object start, Neo4jPersistentProperty field, String... params) {
			return Traversal.description().relationships(DynamicRelationshipType.withName(params[0]))
					.evaluator(Evaluators.atDepth(1));
		}

	}

	private static class ChildLinkTraversalBuilder implements FieldTraversalDescriptionBuilder {

		@Override
		public TraversalDescription build(Object start, Neo4jPersistentProperty field, String... params) {
			return Traversal.description()
					.relationships(DynamicRelationshipType.withName(params[0]), Direction.INCOMING)
					.evaluator(Evaluators.atDepth(1));
		}

	}

	public Long getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tValue == null) ? 0 : tValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LinkNode other = (LinkNode) obj;
		if (tValue == null) {
			if (other.tValue != null)
				return false;
		} else if (!tValue.equals(other.tValue))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("LinkNode{type='%s', value='%s'}",
				getType(), getValue());
	}
}
