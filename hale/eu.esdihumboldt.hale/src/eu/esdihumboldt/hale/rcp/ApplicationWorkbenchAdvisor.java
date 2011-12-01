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
package eu.esdihumboldt.hale.rcp;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import eu.esdihumboldt.hale.Messages;
import eu.esdihumboldt.hale.cache.Request;
import eu.esdihumboldt.hale.models.ProjectService;
import eu.esdihumboldt.hale.models.project.RecentFilesService;
import eu.esdihumboldt.hale.rcp.utils.ExceptionHelper;
import eu.esdihumboldt.hale.rcp.wizards.io.SaveAlignmentProjectWizard;

/**
 * The {@link ApplicationWorkbenchAdvisor} controls the appearance of the 
 * application (menus, toolbars, perspectives, etc).
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "eu.esdihumboldt.hale.rcp.perspective.Default"; //$NON-NLS-1$
	
	/**
	 * A tag for the list of recent files in the workbench memento
	 */
	private static final String TAG_RECENTFILES = "recentFiles"; //$NON-NLS-1$

	/**
	 * @see WorkbenchAdvisor#createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer)
	 */
	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	/**
	 * @see WorkbenchAdvisor#getInitialWindowPerspectiveId()
	 */
	@Override
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	/**
	 * @see WorkbenchAdvisor#initialize(IWorkbenchConfigurer)
	 */
	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		
		configurer.setSaveAndRestore(true);
	}

	/**
	 * @see WorkbenchAdvisor#preShutdown()
	 */
	@Override
	public boolean preShutdown() {
		// Cache shutdown
		Request.getInstance().shutdown();
		
		ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(ProjectService.class);
		if (ps.isChanged()) {
			Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
			MessageBox mb = new MessageBox(shell, 
					SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_QUESTION);
    		mb.setMessage(Messages.ApplicationWorkbenchAdvisor_1); //$NON-NLS-1$
    		mb.setText(Messages.ApplicationWorkbenchAdvisor_2); //$NON-NLS-1$
    		int result = mb.open();
    		if (result == SWT.CANCEL) {
    			return false;
    		}
    		else if (result == SWT.YES) {
    			try {
    				// try saving project
    				if (!ps.save()) {
    					// have to use save as
    					IExportWizard iw = new SaveAlignmentProjectWizard();
    					// Instantiates the wizard container with the wizard and opens it
    					WizardDialog dialog = new WizardDialog(shell, iw);
    					if (dialog.open() == WizardDialog.CANCEL) {
    						return false;
    					}
    				}
					return true;
				} catch (JAXBException e) {
					ExceptionHelper.handleException(
							"Error saving alignment project", HALEActivator.PLUGIN_ID, e); //$NON-NLS-1$
					return false;
				}
			}
    		else {
    			return true;
    		}
		}
		else {
			return true;
		}
	}

	/**
	 * @see WorkbenchAdvisor#restoreState(IMemento)
	 */
	@Override
	public IStatus restoreState(IMemento memento) {
		MultiStatus result = new MultiStatus(PlatformUI.PLUGIN_ID, IStatus.OK,
				"Restored state", null); //$NON-NLS-1$
		
		result.add(super.restoreState(memento));
		
		//restore list of recent files
    	IWorkbench wb = getWorkbenchConfigurer().getWorkbench();
    	RecentFilesService rfs = (RecentFilesService)wb.getService(
    			RecentFilesService.class);
    	IMemento c = memento.getChild(TAG_RECENTFILES);
    	result.add(rfs.restoreState(c));
		
		return result;
	}

	/**
	 * @see WorkbenchAdvisor#saveState(IMemento)
	 */
	@Override
	public IStatus saveState(IMemento memento) {
		MultiStatus result = new MultiStatus(PlatformUI.PLUGIN_ID, IStatus.OK,
			"Saved state", null); //$NON-NLS-1$
		
		result.add(super.saveState(memento));
		
		//save list of recent files
    	IWorkbench wb = getWorkbenchConfigurer().getWorkbench();
    	RecentFilesService rfs = (RecentFilesService)wb.getService(
    			RecentFilesService.class);
    	IMemento c = memento.createChild(TAG_RECENTFILES);
    	result.add(rfs.saveState(c));
    	
    	return result;
	}

}