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

package dynamicrefactoring.integration.selectionhandler;

import java.io.IOException;

import dynamicrefactoring.integration.ModelGenerator;

import dynamicrefactoring.util.processor.JavaMethodProcessor;
import dynamicrefactoring.util.selection.TextSelectionInfo;


import moon.core.Model;
import moon.core.Name;
import moon.core.ObjectMoon;
import moon.core.classdef.ClassDef;
import moon.core.classdef.MethDec;
import moon.core.genericity.FormalPar;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;

import repository.moon.concretefunction.MethodRetriever;

/**
 * Proporciona las funciones necesarias para obtener el par�metro formal MOON 
 * de una clase gen�rica con el que se corresponde un par�metro formal 
 * seleccionado en Eclipse.
 * 
 * @author <A HREF="mailto:sfd0009@alu.ubu.es">Sonia Fuente de la Fuente</A>
 * @author <A HREF="mailto:ehp0001@alu.ubu.es">Enrique Herrero Paredes</A>
 */
public class FormalParameterSelectionHandler implements ISelectionHandler {
	
	/**
	 * El proveedor de informaci�n concreto para la selecci�n de texto.
	 */
	private TextSelectionInfo infoProvider;
	
	/**
	 * El elemento Java seleccionado que maneja Eclipse.
	 */
	private ITypeParameter selectedParameter;
	
	/**
	 * La descripci�n MOON del par�metro formal seleccionado.
	 */
	private FormalPar formalParameter;
	
	/**
	 * La descripci�n MOON de la clase en cuya declaraci�n aparece el par�metro
	 * formal.
	 */
	private ClassDef fParClass;
	
	/**
	 * La descripci�n MOON del m�todo en cuya declaraci�n aparece el par�metro
	 * formal.
	 */
	private MethDec fParMethod;
	
	/**
	 * Constructor.
	 * 
	 * @param selectionInfo contenedor de la selecci�n que se desea manejar.
	 * 
	 * @throws Exception si la selecci�n contenida en #selectionInfo no es una
	 * selecci�n de un par�metro formal sobre una representaci�n textual.
	 */
	public FormalParameterSelectionHandler (
		TextSelectionInfo selectionInfo) throws Exception{
		
		if (! selectionInfo.isFormalParameterSelection())
			throw new Exception(
				Messages.FormalParameterSelectionHandler_InvalidSelection
				+ Messages.FormalParameterSelectionHandler_FormalParameterExpected);
		if (! selectionInfo.isTextSelection())
			throw new Exception(
				Messages.FormalParameterSelectionHandler_InvalidSelection
				+ Messages.FormalParameterSelectionHandler_TextExpected);
		
		infoProvider = selectionInfo;
		
		selectedParameter = (ITypeParameter)infoProvider.getSelectedJavaElement();
	}
	
	/**
	 * Obtiene la descripci�n MOON del par�metro formal representado por una 
	 * selecci�n del interfaz gr�fico.
	 * 
	 * @return la descripci�n MOON del argumento formal representado por una 
	 * selecci�n del interfaz gr�fico.
	 * 
	 * @throws ClassNotFoundException si se no se consigue encontrar la clase 
	 * del par�metro formal en el modelo MOON cargado.
	 * @throws IOException si se produce alg�n error al acceder al modelo MOON.
	 * 
	 * @see ISelectionHandler#getMainObject()
	 */
	@Override
	public ObjectMoon getMainObject() 
		throws IOException, ClassNotFoundException {
		
		if (formalParameter == null){
			// Se construye el nombre con el que se buscar� el par�metro formal.
			Model MOONModel = ModelGenerator.getInstance().getModel();
			Name fParName = MOONModel.getMoonFactory().createName(
				selectedParameter.getElementName());
			
			if (selectedParameter.getDeclaringMember() instanceof IMethod){
			
				// Primero se comprueba si pertenece a un m�todo gen�rico.
				MethDec declaringMethod = getFormalParameterMethod();
				formalParameter = declaringMethod.getFormalPar(fParName);
			}
			else if (selectedParameter.getDeclaringMember() instanceof IType){

				// Si no, se supone que pertenece a una clase gen�rica.
				ClassDef declaringClass = getFormalParameterClass();
				formalParameter = declaringClass.getFormalPar(fParName);
			}	
		}
		
		return formalParameter;
	}
	
	/**
	 * Obtiene la descripci�n MOON del m�todo al que pertenece el par�metro
	 * formal seleccionado.
	 * 
	 * @return la descripci�n MOON del m�todo al que pertenece el par�metro
	 * formal seleccionado.
	 * 
	 * @throws ClassNotFoundException si se no se consigue encontrar la clase a
	 * la que pertenece el m�todo en el modelo MOON cargado.
	 * @throws IOException si se produce alg�n error al acceder al modelo MOON.
	 */
	public MethDec getFormalParameterMethod() 
		throws IOException, ClassNotFoundException {
		
		// Si el par�metro formal no est� declarado en un m�todo, ser� de clase.
		if (! (selectedParameter.getDeclaringMember() instanceof IMethod))
			return null;
		
		if (fParMethod == null){
			JavaMethodProcessor methodProcessor = 
				new JavaMethodProcessor((IMethod)selectedParameter.getParent());
			
			String uniqueName = methodProcessor.getUniqueName();
			
			fParMethod = (MethDec) new MethodRetriever(getFormalParameterClass(), 
				uniqueName).getValue();
		}			
		return fParMethod;
	}
	
	/**
	 * Obtiene la descripci�n MOON de la clase a la que pertenece el par�metro
	 * formal seleccionado.
	 * 
	 * @return la descripci�n MOON de la clase a la que pertenece el par�metro
	 * formal seleccionado.
	 * 
	 * @throws ClassNotFoundException si se no se consigue encontrar la clase en
	 * el modelo MOON cargado.
	 * @throws IOException si se produce alg�n error al acceder al modelo MOON.
	 */
	public ClassDef getFormalParameterClass() 
		throws ClassNotFoundException, IOException {
		if (fParClass == null)
			fParClass = SelectionClassFinder.getTextSelectionClass(
				fParClass, infoProvider);
		return fParClass;
	}
}