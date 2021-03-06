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

package repository.moon.concretefunction;

import java.util.*;

import moon.core.classdef.MethDec;
import moon.core.instruction.*;

import refactoring.engine.Function;

/**
 * Permite obtener todas las instrucciones de un m�todo determinado, separando
 * las instrucciones compuestas en sus componentes simples.
 *
 * @author <A HREF="mailto:ehp0001@alu.ubu.es">Enrique Herrero Paredes</A>
 * @author <A HREF="mailto:alc0022@alu.ubu.es">�ngel L�pez Campo</A>
 * @author <A HREF="mailto:sfd0009@alu.ubu.es">Sonia Fuente de la Fuente</A>
 */ 
public class MethodInstructionsCollector extends Function {
	
	/**
	 * M�todo cuyas instrucciones se desea obtener.
	 */
	private MethDec methDec;
	
	/**
	 * Constructor.<p>
	 *
	 * Devuelve una nueva instancia de MethodInstructionsCollector.
	 *
	 * @param methDec el m�todo cuyas instrucciones se quiere obtener.
	 */
	public MethodInstructionsCollector(MethDec methDec) {		
		super();
		
		this.methDec = methDec;
	}

	/**
	 * Sin implementaci�n.
	 *
	 * @return null.
	 */
	public Object getValue() {
		return null;
	}
		
	/**
	 * Obtiene el conjunto de instrucciones simples (no compuestas) de un m�todo.
	 *
	 * @return El conjunto de instrucciones simples (no compuestas) del m�todo.
	 */
	public Collection<Instr> getCollection() {
		Collection<Instr> collectedInstructions = new Vector<Instr>(10,1);
		
		List<Instr> instructions = methDec.getInstructions();
				
		for (Instr next : instructions) {
			if(! (next instanceof CompoundInstr))
				collectedInstructions.add(next);
			
			else {
				SimpleInstructionCollector collector = 
					new SimpleInstructionCollector((CompoundInstr) next);
				collectedInstructions.addAll(collector.getCollection());				
			}			
		}				
		return collectedInstructions;
	}
}