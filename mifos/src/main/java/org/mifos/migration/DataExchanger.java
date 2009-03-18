package org.mifos.migration;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.persistence.CenterPersistence;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.migration.generated.Center;
import org.mifos.migration.generated.MifosDataExchange;
import org.mifos.migration.mapper.CenterMapper;
import org.xml.sax.SAXException;


public class DataExchanger {
	private static final String GENERATED_CLASS_PACKAGE = "org.mifos.migration.generated";
	private static final String MIFOS_DATA_EXCHANGE_SCHEMA_PATH = "src/org/mifos/migration/schemas/generated/MifosDataExchange.xsd";

	private JAXBContext  jaxbContext;
	private Marshaller   marshaller;
	private Unmarshaller unmarshaller;
	private MifosValidationEventHandler validationEventHandler;

	public DataExchanger() {
		init();
	}
	
	public void init() {
    	try {
            jaxbContext = JAXBContext.newInstance( GENERATED_CLASS_PACKAGE );
            
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
      			  Boolean.TRUE);
            
            unmarshaller = jaxbContext.createUnmarshaller();
            SchemaFactory schemaFactory =
            	SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema =
            	schemaFactory.newSchema(new File(MIFOS_DATA_EXCHANGE_SCHEMA_PATH));
            unmarshaller.setSchema(schema);  
            
            validationEventHandler = new MifosValidationEventHandler();
            unmarshaller.setEventHandler(validationEventHandler);
        } catch( JAXBException je ) {
        	throw new RuntimeException(je);
        } catch (SAXException e) {
			throw new RuntimeException(e);
		}	
	}

	private MifosDataExchange parseDataExchangeXML(String xmlString) {
		StringReader stringReader = new StringReader(xmlString);

		try {
			return (MifosDataExchange) unmarshaller.unmarshal(stringReader);
		}
		catch (JAXBException je) {
			throw new RuntimeException(je);
		}		
	}
	
	public void importXML(String xmlString, UserContext userContext) {
		MifosDataExchange mifosDataExchange = parseDataExchangeXML(xmlString);
		
		persistDataExchange(mifosDataExchange, userContext);
	}
	
	private void persistDataExchange(MifosDataExchange mifosDataExchange, UserContext userContext) {
		List<Center> centers = mifosDataExchange.getCenter();
		for (Center center : centers) {
			CenterBO centerBO = CenterMapper.mapCenterToCenterBO(center, userContext);
			try {
				new CenterPersistence().saveCenter(centerBO);		
			}
			catch (CustomerException e) {
				throw new RuntimeException(e);
			}
		}
		StaticHibernateUtil.commitTransaction();

	}

	public String exportXML(CenterBO center) {
		StringWriter writer = new StringWriter();

		try {
			MifosDataExchange mifosDataExchange = new MifosDataExchange();
			
			mifosDataExchange.getCenter().add(CenterMapper.mapCenterBOToCenter(center));
			marshaller.marshal(mifosDataExchange, writer);
			
		}
		catch (JAXBException je) {
			System.out.println(validationEventHandler.getErrorString());
			throw new RuntimeException(je);
		}
		
		return writer.toString();
	}
}
