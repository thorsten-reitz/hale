/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.functions.core;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Parameter page using a source viewer.
 * 
 * @author Simon Templer
 */
public abstract class SourceViewerParameterPage extends SourceListParameterPage<SourceViewer> {

	/**
	 * @see SourceListParameterPage#SourceListParameterPage(String, String,
	 *      ImageDescriptor)
	 */
	public SourceViewerParameterPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * @see SourceListParameterPage#SourceListParameterPage(String)
	 */
	public SourceViewerParameterPage(String pageName) {
		super(pageName);
	}

	/**
	 * @see SourceListParameterPage#setText(Object, String)
	 */
	@Override
	protected void setText(SourceViewer viewer, String value) {
		viewer.getDocument().set(value);
	}

	/**
	 * @see SourceListParameterPage#getText(Object)
	 */
	@Override
	protected String getText(SourceViewer viewer) {
		return viewer.getDocument().get();
	}

	/**
	 * @see SourceListParameterPage#insertTextAtCurrentPos(Object, String)
	 */
	@Override
	protected void insertTextAtCurrentPos(SourceViewer viewer, String insert) {
		Point selRange = viewer.getSelectedRange();
		try {
			viewer.getDocument().replace(selRange.x, selRange.y, insert);
		} catch (BadLocationException e) {
			// ignore
		}
	}

	/**
	 * @see SourceListParameterPage#createAndLayoutTextField(Composite)
	 */
	@Override
	protected SourceViewer createAndLayoutTextField(Composite parent) {
		// init editor
		IVerticalRuler ruler = createRuler();
		SourceViewer viewer = new SourceViewer(parent, ruler, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.getTextWidget().setFont(JFaceResources.getTextFont());

		return viewer;
	}

	/**
	 * Create the vertical ruler for the source viewer.
	 * 
	 * @return the vertical ruler
	 */
	protected IVerticalRuler createRuler() {
		final Display display = Display.getCurrent();
		CompositeRuler ruler = new CompositeRuler(3);
		LineNumberRulerColumn lineNumbers = new LineNumberRulerColumn();
		lineNumbers.setBackground(display.getSystemColor(SWT.COLOR_GRAY)); // SWT.COLOR_INFO_BACKGROUND));
		lineNumbers.setForeground(display.getSystemColor(SWT.COLOR_BLACK)); // SWT.COLOR_INFO_FOREGROUND));
		lineNumbers.setFont(JFaceResources.getTextFont());
		ruler.addDecorator(0, lineNumbers);
		return ruler;
	}

	/**
	 * Configure the source viewer. Here the configuration and the document are
	 * set.
	 */
	@Override
	protected void configure(SourceViewer viewer) {
		SourceViewerConfiguration conf = createConfiguration();
		viewer.configure(conf);

		createAndSetDocument(viewer);

		viewer.getDocument().addDocumentListener(new IDocumentListener() {

			@Override
			public void documentChanged(DocumentEvent event) {
				updateState(event.getDocument());
			}

			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
				// ignore
			}
		});

		updateState(viewer.getDocument());
	}

	/**
	 * Get the source viewer document.
	 * 
	 * @return the document
	 */
	protected IDocument getDocument() {
		return getTextField().getDocument();
	}

	/**
	 * Update the page state.
	 * 
	 * @param document the current document
	 */
	protected void updateState(IDocument document) {
		boolean valid = validate(document);

		setPageComplete(valid);
	}

	/**
	 * Validate the given document. The default implementation always returns
	 * <code>true</code>.
	 * 
	 * @param document the document to validate
	 * @return if the document is valid
	 */
	protected boolean validate(IDocument document) {
		return true;
	}

	/**
	 * Create the initial document and set it for the viewer.
	 * 
	 * @param viewer the source viewer
	 */
	protected void createAndSetDocument(SourceViewer viewer) {
		IDocument doc = new Document();
		doc.set(""); //$NON-NLS-1$

		viewer.setDocument(doc);
	}

	/**
	 * Create the source viewer configuration.
	 * 
	 * @return the source viewer configuration
	 */
	protected SourceViewerConfiguration createConfiguration() {
		return new SourceViewerConfiguration();
	}

}
