/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 *
 * Componet     : hale
 * 	 
 * Classname    : eu.esdihumboldt.hale.rcp.wizards.functions.generic.AlgorithmWizardPage.java 
 * 
 * Author       : Josef Bezdek
 * 
 * Created on   : Jan, 2010
 *
 */

package eu.esdihumboldt.hale.rcp.wizards.functions.core.generic;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizardPage;

public class AlgorithmWizardPage extends AbstractSingleCellWizardPage  {


	protected boolean algorithmIsSelected = false;	 // if is algorithm is selected, not FunctionType
	private int numberOfParameters = 0;	// number of parameters in selected algorithm
	private Text inputAttributeText = null;
	private Text outputAttributeText = null;
	protected Text[] params = null;
	private Label[] labels = null;
	
	/**
	 * constructor 
	 * @param pageName  
	 */
	protected AlgorithmWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setPageComplete(false);
	}
	

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        composite.setFont(parent.getFont());
        setErrorMessage(null);	
        
		Group configurationGroup = new Group(composite, SWT.NONE);
		configurationGroup.setText("Parameters:");
		configurationGroup.setLayout(new GridLayout());
		GridData configurationAreaGD = new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL);
		configurationAreaGD.grabExcessHorizontalSpace = true;
		configurationGroup.setLayoutData(configurationAreaGD);
		configurationGroup.setSize(configurationGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		configurationGroup.setFont(composite.getFont());
		
		Composite configurationComposite = new Composite(configurationGroup, SWT.NONE);
		GridData configurationLayoutData = new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		configurationLayoutData.grabExcessHorizontalSpace = true;
		configurationComposite.setLayoutData(configurationLayoutData);
		GridLayout fileSelectionLayout = new GridLayout();
		fileSelectionLayout.numColumns = 2;
		fileSelectionLayout.makeColumnsEqualWidth = false;
		fileSelectionLayout.marginWidth = 0;
		fileSelectionLayout.marginHeight = 0;
		configurationComposite.setLayout(fileSelectionLayout);
		
		final Label inputAttributeLabel = new Label(configurationComposite, SWT.NONE);
		inputAttributeLabel.setText("Source attribute:");
		this.inputAttributeText = new Text(configurationComposite, SWT.BORDER);
		this.inputAttributeText.setLayoutData(configurationLayoutData);
		this.inputAttributeText.setText(getParent().getSourceItem().getName().getLocalPart());
		inputAttributeText.setEnabled(false);
		
		final Label outputAttributeLabel = new Label(configurationComposite, SWT.NONE);
		outputAttributeLabel.setText("Target attribute:");
		this.outputAttributeText = new Text(configurationComposite, SWT.BORDER);
		this.outputAttributeText.setLayoutData(configurationLayoutData);
		this.outputAttributeText.setText(getParent().getTargetItem().getName().getLocalPart());
		outputAttributeText.setEnabled(false);
		numberOfParameters = ((GenericFunctionWizard)getWizard()).maximumParameters;
		labels = new Label[numberOfParameters];
		params = new Text [numberOfParameters];
		
		for (int i=0; i<params.length; i++){
			labels[i] = new Label(configurationComposite, SWT.NONE);			
			labels[i].setText("                                        ");
			params[i] = new Text(configurationComposite, SWT.BORDER);
			params[i].setLayoutData(configurationLayoutData);
			params[i].setToolTipText("                                                              ");
			params[i].addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e){
					GenericFunctionWizard wizard = (GenericFunctionWizard)getWizard();
					wizard.parametersCompleted = isPageComplete();
					setPageComplete(isPageComplete());
					wizard.getContainer().updateButtons();	
				}
			});
		}
		
		setErrorMessage(null);	// should not initially have error message
		super.setControl(composite);	
	
	}

	/**
	 * The method creates text fields for input function parameters
	 */
	protected void setPageParameters (){
		GenericFunctionWizard wizard = (GenericFunctionWizard)getWizard();
		if (wizard.algorithmModel.numberOfParameters == 0){
			wizard.parametersCompleted = true;
			wizard.getContainer().updateButtons();
		}
		else{
			wizard.parametersCompleted = false;
		}
		Iterator <String> iter = null;
		Map <String, Class <?>> parameters = wizard.algorithmModel.getParameters();
		if (parameters != null)
			iter =  parameters.keySet().iterator();
		for (int i=0; i<numberOfParameters; i++){
			labels[i].setVisible(false);
			params[i].setVisible(false);
			if (iter != null && iter.hasNext()){
				Object name = iter.next();
				labels[i].setText((String)name.toString());
				params[i].setToolTipText((String)parameters.get(name).toString());
				labels[i].setVisible(true);
				params[i].setVisible(true);
			}
		}
	}
	
	/**
	 * @see Wizard#isPageComplet()
	 */
	@Override
	public boolean isPageComplete()	{
		GenericFunctionWizard wizard = (GenericFunctionWizard)getWizard();
		for (int i=0; i<wizard.algorithmModel.numberOfParameters; i++){
			if (labels[i].getText().length() == 0)
				return false;
		}
		return true;
	}
	

}
