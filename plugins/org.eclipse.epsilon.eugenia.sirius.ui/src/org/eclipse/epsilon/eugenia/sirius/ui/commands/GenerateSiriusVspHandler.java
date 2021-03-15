package org.eclipse.epsilon.eugenia.sirius.ui.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Manifest;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.epsilon.eugenia.sirius.jobs.SiriusPolishTransformationJob;
import org.eclipse.epsilon.eugenia.sirius.jobs.SiriusVspGenerationJob;
import org.eclipse.epsilon.eugenia.sirius.ui.wizards.EugeniaViewpointSpecificationProjectWizard;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class GenerateSiriusVspHandler extends AbstractHandler {
	
	private IFile ecoreFile;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// get the ecore file from the handler
		ecoreFile = getEcoreProject(event);
		
		// get the Sirius project (Sirius project should be ecore project name + "design" by convention)
		IProject siriusProject = ResourcesPlugin.getWorkspace().getRoot().getProject(getSiriusProjectName());
		
		if (!siriusProject.exists()) {
			// create the project if it doesn't exist in the workspace
			createSiriusProject(siriusProject);
		}
		
		// refresh the project to ensure we are in sync with the FS	
		refreshProject(siriusProject);
		

		try {
			// run the main transformation	
			SiriusVspGenerationJob job = new SiriusVspGenerationJob("Generation", ecoreFile, getOdesignFromSirusProject(siriusProject));
			job.schedule();
			job.join();
			
			for (IFile f : getPolishingTransformationFiles(siriusProject)) {
				// refresh the project to ensure we are in sync with the FS	
				refreshProject(siriusProject);
				SiriusPolishTransformationJob p = new SiriusPolishTransformationJob("Polishing", ecoreFile, getOdesignFromSirusProject(siriusProject), f);
				p.schedule();			
				p.join();
			}
				
			// refresh the project to ensure we are in sync with the FS	
			refreshProject(siriusProject);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		
		return null;
	}
	
	private IFile getEcoreProject(ExecutionEvent event) {
		ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
		if (currentSelection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) currentSelection;
			return (IFile) treeSelection.getFirstElement();
		}
		return null;
	}
	
	private void createSiriusProject(IProject siriusProject) {
		IWorkbenchWizard wizard = new EugeniaViewpointSpecificationProjectWizard(getSiriusProjectName());		
		wizard.init(PlatformUI.getWorkbench(), StructuredSelection.EMPTY);
		WizardDialog wd = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		wd.setTitle(wizard.getWindowTitle());
		wd.open();	
		refreshProject(siriusProject);
		addImportToManifest(siriusProject);
	}
	
	private void addImportToManifest(IProject siriusProject) {
		File f = siriusProject.getFile("/META-INF/MANIFEST.MF").getLocation().toFile();			
		try (FileInputStream inStream = new FileInputStream(f)){
			Manifest m = new Manifest(inStream);
			String bundles = m.getMainAttributes().getValue("Require-Bundle");
			if (!Arrays.asList(bundles.split(",")).contains(getEcoreProjectName())) {
				bundles = bundles += "," + getEcoreProjectName();
				m.getMainAttributes().putValue("Require-Bundle", bundles);	
				try (FileOutputStream outStream = new FileOutputStream(f)){
					m.write(outStream);
				} catch (IOException e) {
					e.printStackTrace();
				}			
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private String getEcoreProjectName() {
		return ecoreFile.getProject().getName();
	}
	
	private String getSiriusProjectName() {
		return getEcoreProjectName() + ".design";
	}
	
	private void refreshProject(IProject project) {
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	private IFile getOdesignFromSirusProject(IProject siriusProject) {
		try {
			for (IResource r : siriusProject.getFolder("description").members()) {
				if (r instanceof IFile && r.getFileExtension().equals("odesign")) {
					return (IFile) r;
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<IFile> getPolishingTransformationFiles(IProject siriusProject) {
		List<IFile> polishingTransformations = new ArrayList<>();
		try {
			for (IResource r : siriusProject.getFolder("description").members()) {
				if (r instanceof IFile && r.getFileExtension().equals("eol")) {
					polishingTransformations.add((IFile) r);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return polishingTransformations;
	}

}
