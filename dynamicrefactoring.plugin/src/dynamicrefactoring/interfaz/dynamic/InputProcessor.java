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

package dynamicrefactoring.interfaz.dynamic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.text.MessageFormat;
import java.util.HashMap;

import moon.core.Name;

import org.apache.log4j.Logger;

import dynamicrefactoring.RefactoringConstants;

/**
 * Proporciona funciones de procesamiento de las entradas obtenidas a trav�s 
 * de una ventana de refactorizaci�n din�mica.
 * 
 * <p>Permite adaptar los valores introducidos en la ventana a las necesidades
 * de la refactorizaci�n que se debe ejecutar.</p>
 * 
 * @author <A HREF="mailto:sfd0009@alu.ubu.es">Sonia Fuente de la Fuente</A>
 * @author <A HREF="mailto:ehp0001@alu.ubu.es">Enrique Herrero Paredes</A>
 */
public class InputProcessor {
	
	/**
	 * Elemento de registro de errores y otros eventos de la clase.
	 */
	private static final Logger logger = Logger.getLogger(InputProcessor.class);
	
	/**
	 * Nombre completamente cualificado del tipo MOON que representa una clase.
	 */
	private final String CLASSDEF_NAME = "moon.core.classdef.ClassDef"; //$NON-NLS-1$
	
	/**
	 * Nombre completamente cualificado del tipo MOON que representa un tipo de
	 * una clase. 
	 */
	private final String CLASSTYPE_NAME = "moon.core.classdef.ClassType"; //$NON-NLS-1$
		
	/**
	 * Nombre completamente cualificado del tipo MOON que representa el nombre de
	 * un objeto MOON con nombre.
	 */
	private final String NAME_NAME = "moon.core.Name"; //$NON-NLS-1$
	
	/**
	 * Ventana a la que se consultan los datos necesarios para la composici�n del
	 * conjunto de entradas de la refactorizacion.
	 */
	private DynamicRefactoringWindow launcher;
	
	/**
	 * Constructor.
	 * 
	 * @param launcher ventana de configuraci�n de la refactorizaci�n.
	 */
	public InputProcessor(DynamicRefactoringWindow launcher){
		this.launcher = launcher;
	}
	
	/**
	 * Obtiene las entradas de la refactorizaci�n a partir de los valores cargados
	 * mediante la interfaz de {@link DynamicRefactoringWindow}.
	 * 
	 * @return una tabla asociativa con los valores de las entradas. Se utiliza
	 * como clave el nombre de cada entrada y como valor, el objeto MOON encontrado
	 * para la entrada.
	 */
	public HashMap<String, Object> getInputs(){
		
		HashMap<String, Object> inputs = new HashMap<String, Object>();
		
		for (String[] input : launcher.refactoringDefinition.getInputs()){
			// Si es la entrada principal.
			if (input[4] != null && input[4].equals("true")) //$NON-NLS-1$
				// Se obtiene la entrada principal seleccionada.
				inputs.put(input[1], launcher.currentObject);
			// Si es el modelo MOON.
			else if (input[0].equals("moon.core.Model")) //$NON-NLS-1$
				// Se obtiene el modelo MOON actual.
				inputs.put(input[1], launcher.model);
			// Si es una entrada no principal dependiente de otra.
			else if (input[2] != null && input[2].length() > 0){
				// Su valor tiene que estar ya calculado en la tabla de valores.
				Object value = launcher.inputValues.get(input[1]);
				Object processed = processInput(input, value);
				inputs.put(input[1], processed);
			}
			// Si es una entrada no principal no dependiente.
			else {
				// Se tiene que tratar de un campo de texto.
				Object field = launcher.inputValues.get(input[1]);
				String text = (field != null && field instanceof String) ? 
					(String)field : ""; //$NON-NLS-1$
				Object value = computeValue(input, text);
				if (value != null)
					inputs.put(input[1], value);
			}
		}
		
		return inputs;
	}
	
	/**
	 * Intenta obtener un objeto MOON asociado a una entrada cuyo valor especifica
	 * el usuario a trav�s de un campo de texto.
	 * 
	 * <p>Por defecto, se interpreta que los campos de texto solo pueden contener
	 * nombres (<code>moon.core.Name</code>) o nombres �nicos de clases (<code>
	 * moon.core.classdef.ClassDef</code>).</p>
	 * 
	 * @param input entrada cuyo valor asociado se intenta obtener.
	 * @param source contenido del campo de texto mediante el que el usuario 
	 * asigna valor a la entrada.
	 * 
	 * @return un objeto MOON asociado a la entrada, o <code>null</code> si no se
	 * pudo cargar ning�n objeto adecuado.
	 */
	public Object computeValue(String[] input, String source){
		
		// Se obtiene el nombre del elemento que habr� que buscar.
		String name = source.trim();
		
		// Si la entrada es de tipo moon.core.Name.
		if (input[0].equals(NAME_NAME))	
			// Se construye un nombre MOON.
			return launcher.model.getMoonFactory().createName(name);
		
		// Si no, se comprueba si es alg�n subtipo de moon.core.classdef.ClassDef.
		try {
			Class<?> classdef = Class.forName(CLASSDEF_NAME);
			Class<?> declaration = Class.forName(input[0]);
			
			if (classdef.isAssignableFrom(declaration)){
				Name className = launcher.model.getMoonFactory().createName(name);
				return launcher.model.getClassDef(className);				
			}
			
			// Si no, se comprueba si es subitpo de moon.core.classdef.ClassType.
			Class<?> classtype = Class.forName(CLASSTYPE_NAME);
			
			if (classtype.isAssignableFrom(declaration)){
				Name typeName = launcher.model.getMoonFactory().createName(name);
				return launcher.model.getType(typeName);
			}
		}
		catch (ClassNotFoundException exception){
			Object[] messageArgs = {input[1]};
			MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$
			formatter.applyPattern(
				Messages.InputProcessor_ObjectNotLoaded);
			
			String message = formatter.format(messageArgs) + 
				" .\n\n" + exception.getMessage(); //$NON-NLS-1$
			logger.error(message);		
		}
		
		return null;
	}

	/**
	 * Realiza algunas comprobaciones y transformaciones b�sicas que permiten
	 * obtener unos tipos de datos del modelo a partir de otros.
	 * 
	 * @param input la entrada cuyo valor se procesa.
	 * @param value el valor original obtenido a trav�s de la interfaz.
	 * 
	 * @return el resultado de procesar la entrada original para comprobar si es
	 * necesario aplicarle alguna de las transformaciones b�sicas disponibles.
	 */
	private Object processInput (String[] input, Object value){
		String expectedName = input[0];
		
		Class<?> source = value.getClass();
		
		try {
			Class<?> expected = Class.forName(expectedName);
			// Si el valor obtenido es ya un tipo v�lido para la entrada.
			if (expected.isAssignableFrom(source))
				return value;
			
			// Si no, se obtienen todos los m�todos del tipo del objeto disponible.
			int count = 0, position = -1;
			Method[] methods = source.getMethods();			
			for (int i = 0; i < methods.length; i++)
				// Se busca un m�todo sin argumentos y cuyo tipo de retorno 
				// coincida con el deseado.
				if (expected.isAssignableFrom(methods[i].getReturnType()))
					if (methods[i].getParameterTypes().length == 0){
						count++;
						position = i;
					}
			
			// Si se ha encontrado m�s de un m�todo o no se ha encontrado ninguno
			// es una situaci�n ambigua o sin soluci�n.
			if (count > 1 || position == -1) {
				Object[] messageArgs = {input[1]};
				MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$
				formatter.applyPattern(
					Messages.InputProcessor_AmbiguousInput);
				
				throw new RuntimeException(formatter.format(messageArgs) + 
					".\n\n" + Messages.InputProcessor_ReviewDefinition + ".\n"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			return methods[position].invoke(value, (Object[])null);
		}
		catch (ClassNotFoundException exception){
			logger.error(Messages.InputProcessor_RuntimeClassNotLoaded 
				+ ":\n\n" + exception.getMessage()); //$NON-NLS-1$
		}
		catch (InvocationTargetException exception){
			logger.error(Messages.InputProcessor_ErrorInvoking 
				+ ":\n\n" + exception.getMessage()); //$NON-NLS-1$
		}
		catch (IllegalAccessException exception){
			logger.error(Messages.InputProcessor_ErrorInvoking 
				+ ":\n\n" + exception.getMessage());	 //$NON-NLS-1$
		}
		return value;
	}

	/**
	 * Comprueba si un determinado m�todo es v�lido como m�todo de obtenci�n del 
	 * valor o posibles valores de un determinado tipo.
	 * 
	 * @param method m�todo cuya validez se comprueba.
	 * @param returnType nombre completamente cualificado del tipo con el que debe
	 * conformar el tipo de retorno del m�todo, en caso de ser un �nico elemento.
	 * 
	 * @return <code>true</code> si el tipo de retorno del m�todo devuelve un
	 * iterador, una colecci�n o un objeto cuyo tipo conforma con el especificado;
	 * <code>false</code> en caso contrario. 
	 * 
	 * @throws ClassNotFoundException si se produce un error al cargar la clase
	 * asociada al nombre del tipo especificado, o a los tipos <code>
	 * java.util.Collection</code> y <code>java.util.Iterator</code>.
	 */
	public static boolean isMethodValid(Method method, String returnType) 
		throws ClassNotFoundException {
		
		Class<?> expected = Class.forName(returnType);
		
		// Las colecciones engloban tambi�n a las listas.
		Class<?> collection = Class.forName(
			RefactoringConstants.COLLECTION_PATH);
		Class<?> iterator = Class.forName(
			RefactoringConstants.ITERATOR_PATH);
			
		Class<?> actualType = method.getReturnType();
		if (expected.isAssignableFrom(actualType) ||
			collection.isAssignableFrom(actualType) ||
			iterator.isAssignableFrom(actualType))
			
			if (method.getParameterTypes().length == 0)
				return true;
		
		return false;
	}
}