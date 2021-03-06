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
package eu.esdihumboldt.hale.ui.application;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import eu.esdihumboldt.hale.ui.application.internal.Messages;
import eu.esdihumboldt.hale.ui.launchaction.LaunchAction;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.RecentFilesService;

/**
 * The {@link ApplicationWorkbenchAdvisor} controls the appearance of the
 * application (menus, toolbars, perspectives, etc).
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "eu.esdihumboldt.hale.ui.application.perspective.default"; //$NON-NLS-1$

	/**
	 * A tag for the list of recent files in the workbench memento
	 */
	private static final String TAG_RECENTFILES = "recentFiles"; //$NON-NLS-1$

	private final OpenDocumentEventProcessor openDocProcessor;

	private final LaunchAction action;

	/**
	 * Create the application workbench advisor
	 * 
	 * @param openDocProcessor the processor for {@link SWT#OpenDocument} events
	 * @param action the application launch action, may be <code>null</code>
	 */
	public ApplicationWorkbenchAdvisor(OpenDocumentEventProcessor openDocProcessor,
			LaunchAction action) {
		super();

		this.openDocProcessor = openDocProcessor;
		this.action = action;
	}

	/**
	 * @see WorkbenchAdvisor#createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer)
	 */
	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer, action);
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
	 * @see WorkbenchAdvisor#eventLoopIdle(Display)
	 */
	@Override
	public void eventLoopIdle(Display display) {
		openDocProcessor.openFiles();
		super.eventLoopIdle(display);
	}

	/**
	 * @see WorkbenchAdvisor#preShutdown()
	 */
	@Override
	public boolean preShutdown() {
		// ask for save if there are changes
		ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(
				ProjectService.class);
		if (ps.isChanged()) {
			Shell shell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
			MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_QUESTION);
			mb.setMessage(Messages.ApplicationWorkbenchAdvisor_1); //$NON-NLS-1$
			mb.setText(Messages.ApplicationWorkbenchAdvisor_2); //$NON-NLS-1$
			int result = mb.open();
			if (result == SWT.CANCEL) {
				return false;
			}
			else if (result == SWT.YES) {
				// try saving project
				ps.save();

				if (ps.isChanged()) {
					return false;
				}
				return true;
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

		// restore list of recent files
		IWorkbench wb = getWorkbenchConfigurer().getWorkbench();
		RecentFilesService rfs = (RecentFilesService) wb.getService(RecentFilesService.class);
		IMemento c = memento.getChild(TAG_RECENTFILES);
		result.add(rfs.restoreState(c));

		return result;
	}

	/**
	 * @see WorkbenchAdvisor#saveState(IMemento)
	 */
	@Override
	public IStatus saveState(IMemento memento) {
		MultiStatus result = new MultiStatus(PlatformUI.PLUGIN_ID, IStatus.OK, "Saved state", null); //$NON-NLS-1$

		result.add(super.saveState(memento));

		// save list of recent files
		IWorkbench wb = getWorkbenchConfigurer().getWorkbench();
		RecentFilesService rfs = (RecentFilesService) wb.getService(RecentFilesService.class);
		IMemento c = memento.createChild(TAG_RECENTFILES);
		result.add(rfs.saveState(c));

		return result;
	}

}
