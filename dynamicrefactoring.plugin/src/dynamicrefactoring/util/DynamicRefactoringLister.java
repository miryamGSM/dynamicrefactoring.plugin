/*<Dynamic Refactoring Plugin For Eclipse 2.0 - Plugin that allows to perform refactorings 
on Java code within Eclipse, as well as to dynamically create and manage new refactorings>

Copyright (C) 2009  Laura Fuente De La Fuente

This file is part of Foobar

Foobar is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package dynamicrefactoring.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;

import dynamicrefactoring.util.io.FileManager;
import dynamicrefactoring.util.io.filter.DynamicRefactoringFilter;

/**
 * Obtiene los ficheros XML con refactorizaciones din�micas disponibles.
 *
 * @author <A HREF="mailto:alc0022@alu.ubu.es">�ngel L�pez Campo</A>
 * @author <A HREF="mailto:epf0006@alu.ubu.es">Eduardo Pe�a Fern�ndez</A>
 * @author <A HREF="mailto:sfd0009@alu.ubu.es">Sonia Fuente de la Fuente</A>
 * @author <A HREF="mailto:ehp0001@alu.ubu.es">Enrique Herrero Paredes</A>
 */
public class DynamicRefactoringLister {

	/**
	 * La instancia �nica de la clase.
	 */
	private static DynamicRefactoringLister instance;

	/**
	 * El filtro para los nombres de fichero.
	 */
	private FilenameFilter fileFilter;
	
	/**
	 * N�mero de iteraci�n actual en la b�squeda recursiva.
	 */
	private int iteration = 0;
	
	/**
	 * Constructor.
	 */
	private DynamicRefactoringLister() {		
		fileFilter = new DynamicRefactoringFilter();
	}
		
	/**
	 * Devuelve la instancia �nica de la clase.
	 * 
	 * @return la instancia �nica de la clase.
	 */
	public static DynamicRefactoringLister getInstance() {
		if(instance == null)
			instance = new DynamicRefactoringLister();
		return instance;
	}
		
	/**
	 * Obtiene un conjunto de pares [nombre, ruta al fichero] de los ficheros XML
	 * con refactorizaciones din�micas encontrados (recursivamente) en un cierto
	 * directorio.
	 * 
	 * @param folder el directorio en que se buscan las refactorizaciones.
	 * @param recursive si la b�squeda ha de ser recursiva o no.
	 * @param monitor monitor al que deber� consultarse peri�dicamente si la
	 * operaci�n ha sido cancelada, en cuyo caso deber� detenerse.
	 * 
	 * @return una tabla de pares [clave, valor] en la que se utiliza como clave
	 * el nombre comprensible del fichero y como valor la ruta del fichero; 
	 * para cada fichero encontrado.
	 * 
	 * @throws IOException cuando no existe el directorio ra�z de b�squeda, o 
	 * bien no es un directorio.
	 */
    public HashMap<String, String> getDynamicRefactoringNameList(String folder,
    	boolean recursive, IProgressMonitor monitor) throws IOException {

        File dir = new File(folder);
        if (!dir.exists())
        	throw new IOException(
        		Messages.DynamicRefactoringLister_RefactoringDirectoryNotExists +
        		": " + dir.getAbsolutePath() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
        else if (!dir.isDirectory())
        	throw new IOException(
        		Messages.DynamicRefactoringLister_InvalidRefactoringPath +
        		": " + dir.getAbsolutePath() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
        
        HashMap<String, String> h = new HashMap<String, String>();
        iteration = 0;
        
        try {
        	dynamicRefactoringNameList(dir, h, recursive, monitor);
        }
        catch (InterruptedException e){}
        
        return h;
    }
    
	/**
	 * Obtiene un conjunto de pares [nombre, ruta al fichero] de los ficheros XML 
	 * con refactorizaciones din�micas encontrados (de forma recursiva) a partir de
	 * un directorio.
	 * 
	 * @param dir directorio desde a partir del que se comienza el listado.
	 * @param h una tabla en la que se almacenar�n los pares <nombre fichero,
	 * ruta fichero> encontrados.
	 * @param recursive si la b�squeda ha de ser recursiva en subdirectorios.
	 * @param monitor monitor de progreso al que habr� que consultar peri�dicamente
	 * si la b�squeda ha sido cancelada, en cuyo caso, habr� que detenerla y retornar.
	 * 
	 * @throws InterruptedException si la b�squeda se est� ejecutando bajo la
	 * vigilancia de un monitor de progreso que ha detectado una orden de cancelaci�n.
	 */
    private void dynamicRefactoringNameList(File dir, HashMap<String, String> h, 
    	boolean recursive, IProgressMonitor monitor) throws InterruptedException {
    	
    	if (monitor != null)
    		checkForCancellation(monitor);
    	
    	// Si es un directorio se contin�a recursivamente.
        if (dir.isDirectory() && (recursive || iteration < 2)) {
        	iteration++;
            String[] hijos = dir.list();
            for (int i=0; i < hijos.length; i++)
            	dynamicRefactoringNameList(new File(dir, hijos[i]), h, recursive, monitor);
            iteration--;
        }
        
    	// Si es un fichero de refactorizaci�n se almacena en la tabla.
        else if(fileFilter.accept(dir, dir.getName()) == true)
    		h.put(FileManager.getReadableName(dir.getName()), 
    			dir.getPath());
    }
    
    /**
	 * Comprueba si un monitor de progreso ha recibido una orden de 
	 * cancelaci�n por parte del usuario.
	 * 
	 * @param monitor el monitor cuyo estado de cancelaci�n se comprueba.
	 * 
	 * @throws InterruptedException si el monitor ha recibido una orden de
	 * cancelaci�n por parte del usuario.
	 */
	private void checkForCancellation(IProgressMonitor monitor) 
		throws InterruptedException {
		if(monitor.isCanceled())
			throw new InterruptedException(
				Messages.DynamicRefactoringLister_UserCancelled
				+ ".\n"); //$NON-NLS-1$
	}
}