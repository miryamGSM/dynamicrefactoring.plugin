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

package dynamicrefactoring.action;

import dynamicrefactoring.interfaz.view.ProgressView;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * Implementa una acci�n capaz de vaciar la tabla asociada a la vista
 * de proceso de una refactorizaci�n.
 * 
 * @author <A HREF="mailto:sfd0009@alu.ubu.es">Sonia Fuente de la Fuente</A>
 * @author <A HREF="mailto:ehp0001@alu.ubu.es">Enrique Herrero Paredes</A>
 */
public class CleanViewAction implements IViewActionDelegate {

	/**
	 * Vista para la que se implementa la acci�n de limpieza.
	 */
	private IViewPart view;
	
	/**
	 * Inicializa la acci�n con la vista a la que queda asociada.
	 * 
	 * @param view la vista a la que se asocia la acci�n de limpieza.
	 */
	@Override
	public void init(IViewPart view) {
		this.view = view;
	}

	/**
	 * Ejecuta la acci�n de limpieza sobre la vista.
	 * 
	 * <p>Vac�a la tabla en que se muestran los pasos dados durante la 
	 * ejecuci�n de una refactorizaci�n.</p>
	 * 
	 * @param action el <i>proxy</i> que representa esta acci�n hasta su
	 * activaci�n.
	 */
	@Override
	public void run(IAction action) {
		if (view instanceof ProgressView)
			((ProgressView)view).cleanTable();
	}

	/**
	 * Sin implementaci�n.
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}
}