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

package eu.esdihumboldt.hale.rcp.utils.definition.internal.editors;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.Messages;

/**
 * Editor for URIs
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class URIAttributeEditor extends StringValidatingAttributeEditor<URI> {

	/**
	 * @see StringValidatingAttributeEditor#StringValidatingAttributeEditor(Composite)
	 */
	public URIAttributeEditor(Composite parent) {
		super(parent);
	}

	/**
	 * @see StringValidatingAttributeEditor#validate(String)
	 */
	@Override
	protected String validate(String text) {
		if (text == null) {
			// allow empty value by default
			return null;
		}
		
		try {
			new URI(text);
			return null;
		} catch (URISyntaxException e) {
			return e.getLocalizedMessage();
		}
	}
	
	/**
	 * @see StringValidatingAttributeEditor#getValidToolTip()
	 */
	@Override
	protected String getValidToolTip() {
		return Messages.URIAttributeEditor_0; //$NON-NLS-1$
	}

	/**
	 * @see StringValidatingAttributeEditor#stringFromValue(Object)
	 */
	@Override
	protected String stringFromValue(URI value) {
		return value.toString();
	}

	/**
	 * @see StringValidatingAttributeEditor#valueFromString(String)
	 */
	@Override
	protected URI valueFromString(String text) {
		return (text == null)?(null):(URI.create(text));
	}

	/**
	 * @see StringValidatingAttributeEditor#emptyStringIsNull()
	 */
	@Override
	protected boolean emptyStringIsNull() {
		return true;
	}

}