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

package repository.moon.concreterefactoring;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javamoon.construct.source.SourceLoader;
import javamoon.core.JavaModel;
import moon.core.MoonFactory;
import moon.core.Name;
import moon.core.classdef.ClassDef;
import moon.core.classdef.FormalArgument;
import moon.core.classdef.MethDec;

import org.junit.Test;

import refactoring.engine.PreconditionException;
import repository.RefactoringTemplateAbstractTest;
import repository.moon.MOONRefactoring;

/**
 * Comprueba que funciona correctamente la refactorizaci�n que renombra un
 * argumento formal de un m�todo.
 * 
 * <p>
 * Indirectamente, se comprueba tambi�n la correcci�n de las funciones, acciones
 * y predicados utilizados por la refactorizaci�n.
 * </p>
 * 
 * @author <A HREF="mailto:sfd0009@alu.ubu.es">Sonia Fuente de la Fuente</A>
 * @author <A HREF="mailto:ehp0001@alu.ubu.es">Enrique Herrero Paredes</A>
 * 
 * @see RenameParameter
 */
public class TestRenameParameter extends RefactoringTemplateAbstractTest {

	/**
	 * Comprueba que la refactorizaci�n funciona correctamente al hacer un
	 * renombrado sencillo de un argumento formal.
	 * 
	 * <p>
	 * Se toma un m�todo sencillo de una clase, con un �nico argumento formal en
	 * su lista de par�metros y sin variables locales, y se le asigna un nombre
	 * correcto, distinto al de cualquier atributo de la clase.
	 * </p>
	 * 
	 * @throws Exception
	 *             si se produce un error durante la ejecuci�n de la prueba.
	 */  
	@Test
	public void testSimple() throws Exception{

		SourceLoader sourceLoader = new SourceLoader();
		sourceLoader.loadFromDirectory(formatString(
			"./testdata/repository/moon/concreterefactoring/TestRenameParameter/testSimple")); //$NON-NLS-1$
		JavaModel jm = JavaModel.getInstance();
		MoonFactory factory = jm.getMoonFactory();

		ClassDef classDef = jm.getClassDef(jm.getMoonFactory().createName("paqueteA.ClaseA")); //$NON-NLS-1$
		List <MethDec> lMetodo = classDef.getMethDecByName(factory.createName("metodo1")); //$NON-NLS-1$
		MethDec metodo = lMetodo.get(0);

		List <FormalArgument> lArgumentos = metodo.getFormalArgument();
		FormalArgument argumento = lArgumentos.get(0);
		Name name = factory.createName("nuevoArgumento"); //$NON-NLS-1$

		MOONRefactoring renombrado = new RenameParameter(name, argumento, metodo, jm);			
		renombrado.run();							

		// Comienzan las comprobaciones.
		List <FormalArgument> lArgumentos2 = metodo.getFormalArgument();
		FormalArgument argumento2 = lArgumentos2.get(0);

		assertEquals("Test renombrar argumento simple: " + //$NON-NLS-1$
						"no se ha renombrado correctamente el par�metro.", //$NON-NLS-1$
				factory.createName("paqueteA.ClaseA~metodo1%int#int:nuevoArgumento(0)"), //$NON-NLS-1$
			argumento2.getUniqueName());
	}

	/**
	 * Comprueba que el renombrado de argumentos formales se realiza
	 * correctamente cuando afecta a un argumento que no se encuentre en los
	 * extremos de la lista de par�metros del m�todo.
	 * 
	 * <p>
	 * Se toma en una clase sin atributos un m�todo que tiene variables locales
	 * y varios argumentos formales, y se intenta renombrar el argumento que
	 * ocupa la posici�n central en la lista de par�metros, d�ndole un nombre
	 * distinto al de cualquiera de las variables locales o de los dem�s
	 * argumentos.
	 * </p>
	 * 
	 * @throws Exception
	 *             si se produce un error durante la ejecuci�n de la prueba.
	 */ 
	@Test
	public void testParameterInMiddle()throws Exception{

		SourceLoader sourceLoader = new SourceLoader();
		sourceLoader.loadFromDirectory(formatString(
			"./testdata/repository/moon/concreterefactoring/TestRenameParameter/testParameterInMiddle")); //$NON-NLS-1$
		JavaModel jm = JavaModel.getInstance();
		MoonFactory factory = jm.getMoonFactory();

		ClassDef classDef = jm.getClassDef(factory.createName("paqueteA.ClaseA")); //$NON-NLS-1$
		List <MethDec> lMetodo = classDef.getMethDecByName(factory.createName("metodo1")); //$NON-NLS-1$
		MethDec metodo = lMetodo.get(0);

		List <FormalArgument> lArgumentos = metodo.getFormalArgument();
		FormalArgument argumento = lArgumentos.get(1);
		Name name = factory.createName("nuevoArgumento"); //$NON-NLS-1$

		MOONRefactoring renombrado = new RenameParameter(name, argumento, metodo, jm);			
		renombrado.run();			

		// Comienzan las comprobaciones.
		List <FormalArgument> lArgumentos2 = metodo.getFormalArgument();
		FormalArgument argumento2 = lArgumentos2.get(0);

		assertEquals(
				"Test renombrar par�metro central: " + //$NON-NLS-1$
						"se ha renombrado el primer par�metro.", //$NON-NLS-1$
				factory.createName("paqueteA.ClaseA~metodo1%int%int%int#int:p1(0)"), //$NON-NLS-1$
			argumento2.getUniqueName());

		FormalArgument argumento3 = lArgumentos2.get(1);
		assertEquals(
				"Test renombrar par�metro central: " + //$NON-NLS-1$
						"no se ha renombrado correctamente el par�metro que deb�a.", //$NON-NLS-1$
				factory.createName("paqueteA.ClaseA~metodo1%int%int%int#int:nuevoArgumento(0)"), //$NON-NLS-1$
			argumento3.getUniqueName());

		FormalArgument argumento4 = lArgumentos2.get(2);
		assertEquals(
				"Test renombrar par�metro central: " + //$NON-NLS-1$
						"se ha renombrado el �ltimo par�metro.", //$NON-NLS-1$
				factory.createName("paqueteA.ClaseA~metodo1%int%int%int#int:p3(0)"), //$NON-NLS-1$
			argumento4.getUniqueName());
	}

	/**
	 * Verifica el funcionamiento de las precondiciones de la refactorizaci�n.
	 * 
	 * <p>
	 * Comprueba que se lanza la excepci�n cuando se intenta renombrar un
	 * argumento formal para darle un nombre que ya est� asignado a otro
	 * par�metro del m�todo.
	 * </p>
	 * 
	 * <p>
	 * En un m�todo con dos argumentos formales, intenta asignarle al primero el
	 * mismo nombre que ya tiene el segundo.
	 * </p>
	 * 
	 * @throws Exception
	 *             si se produce un error durante la ejecuci�n de la prueba.
	 */
	@Test(expected=PreconditionException.class) 
	public void testCheckNotExistsParameterWithSameName() throws Exception{

		SourceLoader sourceLoader = new SourceLoader();
		sourceLoader.loadFromDirectory(formatString(
			"./testdata/repository/moon/concreterefactoring/TestRenameParameter/testCheckNotExistsParameterWithSameName")); //$NON-NLS-1$
		JavaModel jm = JavaModel.getInstance();
		MoonFactory factory = jm.getMoonFactory();

		ClassDef classDef = jm.getClassDef(factory.createName("paqueteA.ClaseA")); //$NON-NLS-1$
		List <MethDec> lMetodo = classDef.getMethDecByName(factory.createName("metodo1")); //$NON-NLS-1$
		MethDec metodo = lMetodo.get(0);

		List <FormalArgument> lArgumentos = metodo.getFormalArgument();
		FormalArgument argumento = lArgumentos.get(0);
		Name name = factory.createName("p2"); //$NON-NLS-1$

		MOONRefactoring renombrado = new RenameParameter(name, argumento, metodo, jm);			
		renombrado.run();	
	}

	/**
	 * Verifica el funcionamiento de las precondiciones de la refactorizaci�n.
	 * 
	 * <p>
	 * Comprueba que se lanza la excepci�n cuando se intenta renombrar un
	 * argumento formal para darle un nombre que ya est� asignado a una variable
	 * local del m�todo.
	 * </p>
	 * 
	 * <p>
	 * En un m�todo con un argumento formal y una variable local, intenta
	 * asignarle al primero el mismo nombre que ya tiene la variable local.
	 * </p>
	 * 
	 * @throws Exception
	 *             si se produce un error durante la ejecuci�n de la prueba.
	 */
	@Test(expected=PreconditionException.class) 
	public void testCheckNotExistsLocalDecWithSameName() throws Exception{

		SourceLoader sourceLoader = new SourceLoader();
		sourceLoader.loadFromDirectory(formatString(
			"./testdata/repository/moon/concreterefactoring/TestRenameParameter/testCheckNotExistsLocalDecWithSameName")); //$NON-NLS-1$
		JavaModel jm = JavaModel.getInstance();
		MoonFactory factory = jm.getMoonFactory();

		ClassDef classDef = jm.getClassDef(factory.createName("paqueteA.ClaseA")); //$NON-NLS-1$
		List <MethDec> lMetodo = classDef.getMethDecByName(factory.createName("metodo1")); //$NON-NLS-1$
		MethDec metodo = lMetodo.get(0);

		List <FormalArgument> lArgumentos = metodo.getFormalArgument();
		FormalArgument argumento = lArgumentos.get(0);
		Name name = factory.createName("p2"); //$NON-NLS-1$

		MOONRefactoring renombrado = new RenameParameter(name, argumento, metodo, jm);			
		renombrado.run();	
	}

	/**
	 * Comprueba que funciona correctamente la operaci�n que deshace un
	 * renombrado de un argumento formal.
	 * 
	 * <p>
	 * Se toma un m�todo sencillo de una clase, con un �nico argumento formal en
	 * su lista de par�metros y sin variables locales, y se le asigna un nombre
	 * correcto, distinto al de cualquier atributo de la clase. Despu�s, se
	 * deshace el renombrado.
	 * </p>
	 * 
	 * @throws Exception
	 *             si se produce un error durante la ejecuci�n de la prueba.
	 */   
	@Test
	public void testUndoSimple() throws Exception{

		SourceLoader sourceLoader = new SourceLoader();
		sourceLoader.loadFromDirectory(formatString(
			"./testdata/repository/moon/concreterefactoring/TestRenameParameter/testUndoSimple")); //$NON-NLS-1$
		JavaModel jm = JavaModel.getInstance();
		MoonFactory factory = jm.getMoonFactory();

		ClassDef classDef = jm.getClassDef(factory.createName("paqueteA.ClaseA")); //$NON-NLS-1$
		List <MethDec> lMetodo = classDef.getMethDecByName(factory.createName("metodo1")); //$NON-NLS-1$
		MethDec metodo = lMetodo.get(0);

		List <FormalArgument> lArgumentos = metodo.getFormalArgument();
		FormalArgument argumento = lArgumentos.get(0);
		Name name = factory.createName("nuevoArgumento"); //$NON-NLS-1$

		MOONRefactoring renombrado = new RenameParameter(name, argumento, metodo, jm);			
		renombrado.run();
		renombrado.undoActions();

		// Comienzan las comprobaciones.
		List <FormalArgument> lArgumentos2 = metodo.getFormalArgument();
		FormalArgument argumento2 = lArgumentos2.get(0);

		assertEquals(
				"Test deshacer renombrado de par�metro simple: " + //$NON-NLS-1$
			"no se ha restaurado correctamente el nombre del argumento formal.", //$NON-NLS-1$
				"paqueteA.ClaseA~metodo1%int#int:p1(0)", argumento2.getUniqueName().toString()); //$NON-NLS-1$
	}
}