package org.eclipse.epsilon.eugenia.sirius.jobs;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.epsilon.common.dt.console.EpsilonConsole;
import org.eclipse.epsilon.emc.emf.CachedResourceSet;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eugenia.sirius.models.OdesignModel;

public abstract class AbstractSiriusGenerationJob extends Job {
	
	protected EolModule module;

	public AbstractSiriusGenerationJob(String name) {
		super(name);
	}

	protected OdesignModel loadOdesignModel(IFile file, boolean readOnLoad, boolean storeOnDispose)
			throws EolModelLoadingException {
		OdesignModel odesign = new OdesignModel();
		odesign.setName("ODesign");
		odesign.setModelFileUri((URI.createPlatformResourceURI(file.getFullPath().toString(), true)));
		odesign.setReadOnLoad(readOnLoad);
		odesign.setStoredOnDisposal(storeOnDispose);
		odesign.load();
		return odesign;
	}

	protected EmfModel loadEcoreModel(IFile file, boolean readOnLoad, boolean storeOnDispose)
			throws EolModelLoadingException {
		EmfModel ecore = new EmfModel();
		ecore.setName("ECore");
		ecore.setModelFileUri((URI.createPlatformResourceURI(file.getFullPath().toString(), true)));
		ecore.setReadOnLoad(true);
		ecore.setStoredOnDisposal(false);
		ecore.load();
		return ecore;
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		Exception ex = null;
		// clear any cached models
		CachedResourceSet.getCache().clear();
		module.getContext().setErrorStream(EpsilonConsole.getInstance().getErrorStream());
		module.getContext().setOutputStream(EpsilonConsole.getInstance().getDebugStream());
		try {
			if (module.getParseProblems().isEmpty()) {
				module.execute();
			} else {
				throw new Exception ("Parse problems...");
			}
				
		} catch (Exception e) {
			ex = e;		
		} finally {
			module.getContext().getModelRepository().dispose();	
			module.getContext().dispose();
		}
		if (ex != null) {
			return new Status(Status.ERROR, "org.eclipse.epsilon.eugenia.sirius", "Failed to run job", ex);
		}
		
		return Status.OK_STATUS;
	}

}
