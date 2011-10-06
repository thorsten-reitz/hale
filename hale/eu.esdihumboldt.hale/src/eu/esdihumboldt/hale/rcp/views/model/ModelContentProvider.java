/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.views.model;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Default content provider for the schema model
 *
 * @author cjauss, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class ModelContentProvider 
	implements IStructuredContentProvider, ITreeContentProvider{
	
	/**
	 * Empty children array
	 */
	protected static final Object[] EMPTY_CHILDREN = new Object[0];
	
	/**
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	public void inputChanged(Viewer v, Object oldInput, Object newInput){
		// ignore
	} 
    
	/**
	 * @see IContentProvider#dispose()
	 */
	public void dispose(){
		// ignore
	}
    
	/**
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}
    
	/**
	 * @see ITreeContentProvider#getParent(Object)
	 */
	public Object getParent(Object child) {
		if (child instanceof TreeObject) {
			return ((TreeObject)child).getParent();
		}
		return null;
	}
    
	/**
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeParent) {
			return ((TreeParent)parent).getChildren();
		}
		return EMPTY_CHILDREN;
	}
	
	/**
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
    public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent)
			return ((TreeParent)parent).hasChildren();
		return false;
	}
}