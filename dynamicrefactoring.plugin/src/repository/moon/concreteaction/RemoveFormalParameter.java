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

package repository.moon.concreteaction;

import refactoring.engine.Action;
import repository.RelayListenerRegistry;

import moon.core.classdef.*;
import moon.core.genericity.FormalPar;

/**
 * Permite borrar un par�metro formal de una clase.
 *
 * @author <A HREF="mailto:sam0006@alu.ubu.es">Sara Alcal� Mart�n</A>
 * @author <A HREF="mailto:dbm0005@alu.ubu.es">Diego Ba�uelos Molledo</A>
 * @author <A HREF="mailto:sfd0009@alu.ubu.es">Sonia Fuente de la Fuente</A>
 * @author <A HREF="mailto:ehp0001@alu.ubu.es">Enrique Herrero Paredes</A>
 */
public class RemoveFormalParameter extends Action {
 
	/**
	 * Clase de la que eliminar el par�metro formal.
	 */
	private ClassDef classDef;
	
	/**
	 * Par�metro formal que se debe eliminar.
	 */
	private FormalPar formalPar;
	
	/**
	 * Receptor de los mensajes enviados por la acci�n concreta.
	 */
	private RelayListenerRegistry listenerReg;
	
	/**
	 * Constructor de la acci�n eliminar par�metro formal.
	 *
	 * @param formalPar par�metro formal que se debe eliminar.
	 */	
	public RemoveFormalParameter(FormalPar formalPar) {
		super();
		this.classDef = formalPar.getClassDef();
		this.formalPar = formalPar;	
		
		listenerReg = RelayListenerRegistry.getInstance();
	}

	/**
	 * Ejecuta la eliminaci�n del par�metro formal.
	 */
	@Override
	public void run() {
		
		listenerReg.notify("# run():RemoveFormalParameter #"); //$NON-NLS-1$
		
		String uniqueName = formalPar.getUniqueName().toString(); 
		
		classDef.remove(formalPar);		
		
		listenerReg.notify("\t- Removing formal parameter "  //$NON-NLS-1$
			+ uniqueName + " from class " + classDef.getName().toString()); //$NON-NLS-1$
	}

	/**
	 * Deshace la eliminaci�n del par�metro formal, incluy�ndolo de nuevo en 
	 * la clase.
	 */
	@Override
	public void undo() {
		classDef.add(formalPar);
	}
}