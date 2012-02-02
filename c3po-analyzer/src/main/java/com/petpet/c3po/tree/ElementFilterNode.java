package com.petpet.c3po.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.swing.tree.TreeNode;

import com.google.common.collect.Iterators;
import com.petpet.c3po.datamodel.Value;

public class ElementFilterNode extends NamedNode implements TreeNode {

  private static final long serialVersionUID = 1419587799023184617L;

  private List<ElementNode> elementNodes;

  private FilterNode parent;

  private List<String> properties;

  public ElementFilterNode() {
    this.elementNodes = new ArrayList<ElementNode>();
  }

  public ElementFilterNode(FilterNode parent, String name) {
    this();
    this.setType("elementfilter");
    this.setName(name);
    this.setParent(parent);
  }

  @Override
  public Enumeration children() {
    return Iterators.asEnumeration(elementNodes.iterator());
  }

  @Override
  public boolean getAllowsChildren() {
    return true;
  }

  @Override
  public TreeNode getChildAt(int childIndex) {
    return this.elementNodes.get(childIndex);
  }

  @Override
  public int getChildCount() {
    return this.elementNodes.size();
  }

  @Override
  public int getIndex(TreeNode node) {
    return this.elementNodes.indexOf(node);
  }

  @Override
  public FilterNode getParent() {
    return this.parent;
  }

  public void setParent(FilterNode parent) {
    this.parent = parent;
    this.parent.getChildren().add(this);
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  public List<ElementNode> getChildren() {
    return this.elementNodes;
  }

  public List<String> getDistinctProperties() {
		if (this.properties == null) {
			Set<String> props = new HashSet<String>();
			for (ElementNode en : this.elementNodes) {
				for (Value v : en.getElement().getValues()) {
				  if (!v.getProperty().getName().equals("file.name")) {
				    props.add(v.getProperty().getHumanReadableName());
				  }
				}
			}
			
			this.properties = new ArrayList<String>(props);
			this.properties.add(0, "File Name");
		}

System.out.println("RETRIEVING PROPERTIES FOR TABLE: " + this.properties.size());
		return this.properties;
	}
}