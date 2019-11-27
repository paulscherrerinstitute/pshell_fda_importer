/**
 * 
 * Copyright 2010 Paul Scherrer Institute. All rights reserved.
 * 
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This code is distributed in the hope that it will be useful,
 * but without any warranty; without even the implied warranty of
 * merchantability or fitness for a particular purpose. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this code. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package ch.psi.fda.model;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ch.psi.fda.model.v1.Configuration;

/**
 * Manage the serialization and deserialization of the FDA data model
 */
public class ModelManager {

	/**
	 * De-serialize an instance of the FDA data model
	 * 
	 * @param file	File to deserialize
	 * @throws JAXBException	Something went wrong while unmarshalling
	 * @throws SAXException		Cannot read model schema file
	 */
	public static Configuration unmarshall(File file) throws JAXBException, SAXException {

		JAXBContext context = JAXBContext.newInstance(Configuration.class);
		Unmarshaller u = context.createUnmarshaller();

		// Validation
		SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Source s = new StreamSource(Configuration.class.getResourceAsStream("/model-v1.xsd"));
		Schema schema = sf.newSchema(new Source[]{s}); // Use schema reference provided in XML
		u.setSchema(schema);
		
		try{
			Configuration model = (Configuration) u.unmarshal(new StreamSource(file), Configuration.class).getValue();
			return (model);
		}
		catch(UnmarshalException e){
			// Check 
			if(e.getLinkedException() instanceof SAXParseException){
				throw new RuntimeException("Configuration file does not comply to required model specification\nCause: "+e.getLinkedException().getMessage(), e);
			}
			throw e;
		}
	}
	
	/**
	 * Serialize an instance of the FDA data model
	 * 
	 * @param model				Model datastructure
	 * @param file				File to write the model data into
	 * @throws JAXBException	Something went wrong while marshalling model
	 * @throws SAXException		Cannot read model schema files
	 */
	public static void marshall(Configuration model, File file) throws JAXBException, SAXException{
		QName qname = new QName("http://www.psi.ch/~ebner/models/scan/1.0", "configuration");
		
		JAXBContext context = JAXBContext.newInstance(Configuration.class);
		Marshaller m = context.createMarshaller();
		m.setProperty("jaxb.formatted.output", true);
		
		// Validation
		SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Source s = new StreamSource(Configuration.class.getResourceAsStream("/model-v1.xsd"));
		Schema schema = sf.newSchema(new Source[]{s}); // Use schema reference provided in XML
		m.setSchema(schema);
		
		m.marshal( new JAXBElement<Configuration>(qname, Configuration.class, model ), file);
	}
}
