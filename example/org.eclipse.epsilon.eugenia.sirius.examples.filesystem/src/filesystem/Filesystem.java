/**
 */
package filesystem;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Filesystem</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link filesystem.Filesystem#getDrives <em>Drives</em>}</li>
 *   <li>{@link filesystem.Filesystem#getSyncs <em>Syncs</em>}</li>
 * </ul>
 *
 * @see filesystem.FilesystemPackage#getFilesystem()
 * @model annotation="gmf.diagram"
 * @generated
 */
public interface Filesystem extends EObject {
	/**
	 * Returns the value of the '<em><b>Drives</b></em>' containment reference list.
	 * The list contents are of type {@link filesystem.Drive}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Drives</em>' containment reference list.
	 * @see filesystem.FilesystemPackage#getFilesystem_Drives()
	 * @model type="filesystem.Drive" containment="true"
	 * @generated
	 */
	EList getDrives();

	/**
	 * Returns the value of the '<em><b>Syncs</b></em>' containment reference list.
	 * The list contents are of type {@link filesystem.Sync}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Syncs</em>' containment reference list.
	 * @see filesystem.FilesystemPackage#getFilesystem_Syncs()
	 * @model type="filesystem.Sync" containment="true"
	 * @generated
	 */
	EList getSyncs();

} // Filesystem
