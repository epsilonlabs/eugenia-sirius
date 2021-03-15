package org.eclipse.epsilon.eugenia.sirius.jobs;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.epsilon.common.util.UriUtil;
import org.eclipse.epsilon.etl.EtlModule;

public class SiriusVspGenerationJob extends AbstractSiriusGenerationJob {
	
	private static final String ETL = "platform:/plugin/org.eclipse.epsilon.eugenia.sirius/resources/Ecore2ODesign.etl";
	IFile ecoreFile;
	IFile odesignFile;
	
	public SiriusVspGenerationJob(String name, IFile ecore, IFile odesign) {
		super(name);
		this.ecoreFile = ecore;
		this.odesignFile = odesign;
	}
	

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		module = new EtlModule();
		java.net.URI transformationURI;
		try {
			transformationURI = UriUtil.resolve(ETL);
			module.parse(transformationURI);
			module.getContext().getModelRepository().addModel(loadEcoreModel(ecoreFile, true, false));
			module.getContext().getModelRepository().addModel(loadOdesignModel(odesignFile, false, true));
			return super.run(monitor);
		} catch (Exception e) {
			return new Status(Status.ERROR, "org.eclipse.epsilon.eugenia.sirius", "Failed to run generation job", e);
		}		
	}

}
