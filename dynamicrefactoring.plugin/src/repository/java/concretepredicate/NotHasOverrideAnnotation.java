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

package repository.java.concretepredicate;


import moon.core.classdef.MethDec;
import refactoring.engine.Predicate;

/**
 * Comprueba si un m�todo tiene una anotaci�n override.
 * 
 * @author <A HREF="mailto:lfd0002@alu.ubu.es">Laura Fuente de la Fuente</A>
 */
public class NotHasOverrideAnnotation extends Predicate{

	/**
	 * M�todo que se comprueba.
	 */
	private MethDec methDec;
	
	/**
	 * Constructor.
	 * 
	 * @param methDec m�todo que se comprueba.
	 */
	public NotHasOverrideAnnotation(MethDec methDec){
		super("NotHasOverrideannotation: \n\t" + //$NON-NLS-1$
				"Checks whether the method has not an override annotation." +  //$NON-NLS-1$
				"\n\n"); //$NON-NLS-1$
			this.methDec = methDec;
		}
	
	/**
	 * Comprueba si el m�todo no tiene una anotaci�n override.
	 * 
	 * @return <code>true</code> si el m�todo no tiene una anotaci�n override;
	 * <code>false</code> en caso contrario.
	 */
	@Override
	public boolean isValid() {		
		return !new HasOverrideAnnotation(methDec).isValid();
	}
}
