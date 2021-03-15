package org.eclipse.epsilon.eugenia.sirius.ui.wizards;

import org.eclipse.sirius.editor.tools.internal.wizards.ViewpointSpecificationProjectWizard;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

@SuppressWarnings("restriction")
public class EugeniaViewpointSpecificationProjectWizard extends ViewpointSpecificationProjectWizard{
	
	private String initialProjectName = "my.project.design";
	
	public EugeniaViewpointSpecificationProjectWizard(String initialProjectName) {
		super();
		this.initialProjectName = initialProjectName;
	}


	@Override
    public void addPages() {
        super.addPages();
        if (this.getPages()[0] instanceof WizardNewProjectCreationPage) {
        	WizardNewProjectCreationPage p = (WizardNewProjectCreationPage) this.getPages()[0];
        	p.setInitialProjectName(initialProjectName);
        }
    }

}
