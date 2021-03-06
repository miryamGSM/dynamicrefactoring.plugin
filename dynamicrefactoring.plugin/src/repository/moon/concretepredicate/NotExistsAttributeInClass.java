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

package repository.moon.concretepredicate;

import moon.core.classdef.ClassDef;
import moon.core.classdef.AttDec;

import refactoring.engine.Predicate;

/**
 * Permite verificar que no existe un cierto atributo en una clase determinada.
 *
 * @author <A HREF="mailto:alc0022@alu.ubu.es">�ngel L�pez Campo</A>
 * @author <A HREF="mailto:ehp0001@alu.ubu.es">Enrique Herrero Paredes</A>
 * @author <A HREF="mailto:sfd0009@alu.ubu.es">Sonia Fuente de la Fuente</A>
 */ 
public class NotExistsAttributeInClass extends Predicate {
	
	/**
	 * Condici�n opuesta a la que se comprueba.
	 */
	private Predicate oppositePredicate;
		
	/**
	 * Constructor.<p>
	 *
	 * Obtiene una nueva instancia del predicado NotExistsAttributeInClass.
	 *
	 * @param attribute el atributo que se desea buscar en la clase.
	 * @param classDef la clase en la que se busca el atributo.
	 */
	public NotExistsAttributeInClass(AttDec attribute, ClassDef classDef) {
		
		super("NotExistsAttributeInClass: \n\t" + //$NON-NLS-1$
			  "Makes sure the attribute " + '"' + attribute.getName().toString() + //$NON-NLS-1$
			  '"' + " does not belong to the class " + '"' +  //$NON-NLS-1$
			  classDef.getName().toString() + '"' + "\n\n"); //$NON-NLS-1$
		
		this.oppositePredicate = 
			new ExistsAttributeInClass(attribute, classDef);
	}
		
	/**
	 * Comprueba el valor de verdad del predicado.
	 * 
	 * @return <code>true</code> si no existe el atributo en la clase especificada;
	 * <code>false</code> en caso contrario.
	 */	 
	public boolean isValid() {		
		return !oppositePredicate.isValid();		
	}
}