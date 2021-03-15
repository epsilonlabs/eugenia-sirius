package org.eclipse.epsilon.eugenia.sirius.jobs;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.epsilon.eol.EolModule;

public class SiriusPolishTransformationJob extends AbstractSiriusGenerationJob {
	
	IFile ecoreFile;
	IFile odesignFile;
	IFile polishFile;

	public SiriusPolishTransformationJob(String name, IFile ecore, IFile odesign, IFile polish) {
		super(name);
		this.ecoreFile = ecore;
		this.odesignFile = odesign;
		this.polishFile = polish;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {	
		module = new EolModule();
		try {
			module.parse(polishFile.getRawLocation().toFile());
			module.getContext().getModelRepository().addModel(loadEcoreModel(ecoreFile, true, false));
			module.getContext().getModelRepository().addModel(loadOdesignModel(odesignFile, true, true));
			return super.run(monitor);

		} catch (Exception e) {
			return new Status(Status.ERROR, "org.eclipse.epsilon.eugenia.sirius", "Failed to run polishing job", e);
		}
	}

}
