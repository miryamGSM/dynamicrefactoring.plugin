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

package dynamicrefactoring;

import java.util.ArrayList;

import dynamicrefactoring.listener.IMainSelectionListener;
import dynamicrefactoring.util.selection.SelectionInfo;


/**
 * Mantiene un registro de todos los observadores que quieren recibir notificaciones
 * de que un elemento v�lido como entrada para una refactorizaci�n ha sido seleccionado.
 * 
 * @author <A HREF="mailto:lfd0002@alu.ubu.es">Laura Fuente de la Fuente</A>
 *
 */
public class SelectionListenerRegistry {

	/**
	 * Instancia �nica del generador.
	 * 
	 * Patr�n de dise�o Singleton.
	 */
	private static SelectionListenerRegistry myInstance;
	
	/**
	 * Listeners registrados.
	 */
	private ArrayList<IMainSelectionListener> listeners;
	
	/**
	 * Obtiene la instancia �nica del generador.
	 * 
	 * Patr�n de dise�o Singleton.
	 * 
	 * @return la instancia �nica del generador.
	 */
	public static SelectionListenerRegistry getInstance(){
		if (myInstance == null)
			myInstance = new SelectionListenerRegistry();
		return myInstance;
	}

	/**
	 * Constructor for SelectionListenerRegistry.
	 * 
	 *  Privado, seg�n la estructura del patr�n de dise�o Singleton.
	 */
	private SelectionListenerRegistry() {
		listeners = new ArrayList<IMainSelectionListener>();
	}
	
	/**
	 * Notifica a todos los listener registrados que un objeto del espacio de trabajo
	 * v�lido como entrada para una refactorizaci�n ha sido seleccionado.
	 * 
	 * @param selection elemento seleccionado en el espacio de trabajo.
	 */
	public void notifySelection(SelectionInfo selection){
		for (int i = 0; i < listeners.size() ; ++i){
			IMainSelectionListener listener = listeners.get(i);
			listener.elementSelected(selection);
		}	
	}
	

	/**
	 * A�ade un listener dentro de la lista de listeners que son atendidos por esta clase
	 * que act�a como sujeto dentro del patr�n observador.
	 * 
	 * @param listener listener a a�adir.
	 */
	public void addListener(IMainSelectionListener listener){
		if (listener != null)
			listeners.add(listener) ;
	}

	/**
	 * Elimina un listener dentro de la lista de listeners que son atendidos por esta clase
	 * que act�a como sujeto dentro del patr�n observador.
	 * 
	 * @param listener listener a eliminar.
	 * @see #addListener
	 */
	public void removeListener(IMainSelectionListener listener){
		if (listener != null)
			listeners.remove(listener);
	}
	
}
