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

package dynamicrefactoring.writer;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import dynamicrefactoring.RefactoringConstants;
import dynamicrefactoring.reader.XMLRefactoringReaderException;


/**
 * Se encarga escribir la información almacenada en el xml que guarda el plan de 
 * refactorizaciones.
 * 
 * @author <A HREF="mailto:lfd0002@alu.ubu.es">Laura Fuente de la Fuente</A>
 */
public class RefactoringPlanWriter {
	
	/**
	 * El formato del fichero XML.
	 */
	private Format format;
	
	/**
	 * Instancia única de la clase.
	 * 
	 * Patrón de diseño Singleton.
	 */
	private static RefactoringPlanWriter myInstance;
	
	/**
	 * Constructor.
	 * 
	 * Privado, según la estructura del patrón de diseño Singleton.
	 */
	private RefactoringPlanWriter(){
		Element root = new Element("RefactoringPlan");
		
		DocType type = new DocType(root.getName(), 
		"refactoringPlanDTD.dtd"); 
		
		Document newdoc = new Document(root,type);
		
		initializeFormat(); 
		
		try{
			
		writeToFile(RefactoringConstants.REFACTORING_PLAN_FILE, newdoc);
		
		}catch(IOException e){
			
		}
	}

	/**
	 * Obtiene la instancia única del generador.
	 * 
	 * Patrón de diseño Singleton.
	 * 
	 * @return la instancia única del generador.
	 */
	public static RefactoringPlanWriter getInstance(){
		if (myInstance == null)
			myInstance = new RefactoringPlanWriter();
		return myInstance;
	}
	
	/**
	 * Inicializa las opciones de formato del fichero XML.
	 */
	private void initializeFormat() {		
		format = Format.getPrettyFormat();
		format.setIndent("\t"); //$NON-NLS-1$
		format.setLineSeparator("\n"); //$NON-NLS-1$
		format.setExpandEmptyElements(false);
		format.setEncoding("ISO-8859-1"); //$NON-NLS-1$
	}
	
	/**
	 * Escribe los datos del documento XML en el fichero indicado.
	 * 
	 * @param fname el nombre del fichero.
	 * @param doc el documento XML.
	 * 
	 * @throws IOException si se produce un error de lectura escritura al 
	 * trasladar los datos del documento XML al fichero.
	 */
	private void writeToFile(String fname, Document doc) throws IOException {
		
		FileOutputStream out = new FileOutputStream(fname);
		XMLOutputter op = new XMLOutputter(format);
		
		op.output(doc, out);
		out.flush();
		out.close();
	}
	
	/**
	 * Escribe los datos de una refactorización dentro del fichero que guarda el plan de 
	 * refactorizaciones.
	 * 
	 * @param refactoringName Nombre de la refactorización.
	 * @param fecha fecha en la ques e ejecuta la refactorización.
	 * @param InputsParameters Parámetros de entrada de la refactorización.
	 */
	public void writeRefactoring (String refactoringName,String fecha, HashMap<String, String> InputsParameters ){
		try {
			SAXBuilder builder = new SAXBuilder(true);
			builder.setIgnoringElementContentWhitespace(true);
			// El atributo SYSTEM del DOCTYPE de la definición XML de la
			// refactorización es solo la parte relativa de la ruta del fichero
			// DTD. Se le antepone la ruta del directorio del plugin que 
			// contiene los ficheros de refactorizaciones dinámicas.
			Document doc = builder.build(new File(RefactoringConstants
					.REFACTORING_PLAN_FILE).toURI().toString());;
			Element root = doc.getRootElement();
			Element refactoring = new Element("refactoring");
			
			Element name = new Element("name");
			name.setAttribute("value", refactoringName);
			refactoring.addContent(name);
			
			Element date = new Element("date");
			date.setAttribute("value", fecha);
			refactoring.addContent(date);
			
			Element parameters = new Element("parameters");
			for(Map.Entry<String ,String> param : InputsParameters.entrySet()){
				Element parameter = new Element("parameter");
				parameter.setAttribute("name", param.getKey());
				if(!param.getKey().equals("Model")){
						parameter.setAttribute("value", param.getValue());
						parameters.addContent(parameter);
				}
			}
			refactoring.addContent(parameters);
			root.addContent(refactoring);	
			writeToFile(RefactoringConstants.REFACTORING_PLAN_FILE, doc);
		} 
		catch (JDOMException jdomexception) {
			
		} 
		catch (IOException ioexception) {
			
		}
	}
	
	/**
	 * Elimina una refactorización y las siguientes (ya que al deshacer la primera se deshacen 
	 * el resto) del fichero xml que contiene el plan de refactorizaciones.
	 * 
	 * @param refactoringName refactorización a eliminar
	 * @param fecha fecha de ejecución de la refactorización.
	 * @throws XMLRefactoringReaderException XMLRefactoringReaderException
	 */
	public void deleteRefactorings (String refactoringName, String fecha) throws XMLRefactoringReaderException {
		try {
			SAXBuilder builder = new SAXBuilder(true);
			builder.setIgnoringElementContentWhitespace(true);
			Document doc = builder.build(new File(RefactoringConstants
					.REFACTORING_PLAN_FILE).toURI().toString());
			Element root = doc.getRootElement();
			boolean encontrado = false;
			ArrayList<Element> remove = new ArrayList<Element>();
			for(int i=0;  i < root.getChildren().size(); i++){
				Element refactor = (Element)root.getChildren().get(i);
				if(encontrado){
					remove.add(refactor);
				}else{
					Element name = refactor.getChild("name");
					String value = name.getAttribute("value").getValue();
					if(value.equals(refactoringName)){
						Element date = refactor.getChild("date");
						String d_value = date.getAttribute("value").getValue();
						if(d_value.equals(fecha)){
							encontrado=true;
							remove.add(refactor);
						}	
					}
				}	
			}
			for(Element refactor : remove)
				root.removeContent(refactor);
			writeToFile(RefactoringConstants.REFACTORING_PLAN_FILE, doc);
		} 
		catch (JDOMException jdomexception) {
			System.out.println("jdom " + jdomexception.getMessage());
			throw new XMLRefactoringReaderException(jdomexception.getMessage());
		} 
		catch (IOException ioexception) {
			System.out.println(ioexception.getMessage());
			throw new XMLRefactoringReaderException(ioexception.getMessage());
		}
	}
	
	/**
	 * La instancia de esta clase pasa a ser nulo.
	 */
	public void reset(){
		myInstance=null;
	}

}
