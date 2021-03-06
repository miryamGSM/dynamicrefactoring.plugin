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

import dynamicrefactoring.util.processor.JavaMethodProcessor;


import java.io.IOException;

import repository.moon.concretefunction.MethodRetriever;

import moon.core.ObjectMoon;
import moon.core.classdef.ClassDef;
import moon.core.classdef.MethDec;

/**
 * Proporciona las funciones necesarias para obtener el m�todo MOON con el que
 * se corresponde un m�todo seleccionado en Eclipse.
 * 
 * @author <A HREF="mailto:sfd0009@alu.ubu.es">Sonia Fuente de la Fuente</A>
 * @author <A HREF="mailto:ehp0001@alu.ubu.es">Enrique Herrero Paredes</A>
 */
public abstract class MethodSelectionHandler implements ISelectionHandler {
	
	/**
	 * La clase MOON en la que se define el m�todo.
	 */
	protected ClassDef methodClass;
	
	/**
	 * La descripci�n MOON del m�todo seleccionado.
	 */
	protected MethDec methodDescription;
	
	/**
	 * Obtiene la descripci�n MOON del m�todo representado por una selecci�n del
	 * interfaz gr�fico.
	 * 
	 * M�todo plantilla (patr�n de dise�o M�todo Plantilla).
	 * 
	 * @return la descripci�n MOON del m�todo representado por una selecci�n del 
	 * interfaz gr�fico.
	 * 
	 * @throws ClassNotFoundException si se no se consigue encontrar la clase a
	 * la que pertenece el m�todo en el modelo MOON cargado.
	 * @throws IOException si se produce alg�n error al acceder al modelo MOON.
	 * 
	 * @see ISelectionHandler#getMainObject()
	 */
	public ObjectMoon getMainObject()
		throws ClassNotFoundException, IOException {
		
		if (methodDescription == null){
			// Llamada a la operaci�n primitiva 
			// (patr�n de dise�o M�todo Plantilla).
			JavaMethodProcessor methodProcessor = getMethodProcessor(); 
							
			String uniqueName = methodProcessor.getUniqueName();

			methodDescription = 
				(MethDec) new MethodRetriever(getMethodClass(), uniqueName).getValue();
		}
		
		return methodDescription;
	}
	
	/**
	 * Obtiene la clase del modelo MOON en la que se define el m�todo 
	 * representado por una selecci�n del interfaz gr�fico.
	 * 
	 * @return la clase del modelo MOON en la que se define el m�todo
	 * representado por una selecci�n del interfaz gr�fico.
	 * 
	 * @throws ClassNotFoundException si se no se consigue encontrar la clase en
	 * el modelo MOON cargado.
	 * @throws IOException si se produce alg�n error al acceder al modelo MOON.
	 */
	public abstract ClassDef getMethodClass() 
		throws ClassNotFoundException, IOException; 

	/**
	 * Obtiene un elemento capaz de procesar la informaci�n de un m�todo Java.
	 * 
	 * Operaci�n primitiva (patr�n de dise�o M�todo Plantilla).
	 * 
	 * @return un elemento capaz de procesar la informaci�n de un m�todo Java.
	 */
	protected abstract JavaMethodProcessor getMethodProcessor();
}
