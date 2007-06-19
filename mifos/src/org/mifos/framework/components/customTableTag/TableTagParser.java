package org.mifos.framework.components.customTableTag;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.mifos.framework.exceptions.TableTagParseException;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class TableTagParser {

  private static TableTagParser instance=new TableTagParser();

  public static TableTagParser getInstance(){
    return instance;
  }

  public Table parser(String filename) throws  TableTagParseException{
   Table table=null;
   try{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();


		SchemaFactory schfactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		schfactory.setErrorHandler(null);
		Schema schema = schfactory.newSchema(new StreamSource(new File(ResourceLoader.getURI(FilePaths.CUSTOMTABLETAGXSD))));
		factory.setNamespaceAware(false);
		factory.setValidating(false);
		factory.setSchema(schema);
		
		
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setErrorHandler(null);
		Document document = builder.parse(filename);

        table=createTable(document);

      } catch(URISyntaxException e){
          throw new TableTagParseException(e);
      }catch (ParserConfigurationException e) {
        throw new TableTagParseException(e);
      } catch (IOException e) {
        throw new TableTagParseException(e);
      } catch (SAXParseException e) {
        throw new TableTagParseException(e);
      } catch (SAXException e) {
        throw new TableTagParseException(e);
      }
      return table;
    }

    protected Table createTable(Document document) throws TableTagParseException{
        NodeList tableNode = document.getChildNodes();

        if (tableNode.getLength() ==0)
          throw new TableTagParseException(tableNode.toString());

        Table  table = new Table();
        table.setHeaderDetails(createHeaderDetails(tableNode.item(0)));
        table.setRow(createRow(tableNode.item(0)));

        return table;

    }

    protected HeaderDetails createHeaderDetails(Node table) throws TableTagParseException {
     NodeList headerNodeList = ((Element) table)
                       .getElementsByTagName(TableTagConstants.HEADERDETAILS);

     if (headerNodeList.getLength()==0)
       throw new TableTagParseException(headerNodeList.toString());

     HeaderDetails headerDetails=new HeaderDetails();


     headerDetails.setHeaderStyle(headerNodeList.item(0).getAttributes().getNamedItem(TableTagConstants.HEADERSTYLE).getNodeValue());


     return headerDetails;

    }

    protected Row createRow(Node table) throws TableTagParseException {
    
       NodeList rowNodeList = ((Element) table)
                       .getElementsByTagName(TableTagConstants.ROW);
       if (rowNodeList.getLength() == 0) {
                 throw new TableTagParseException(rowNodeList.toString());
      }
       Row row = new Row();

       row.setTotWidth((rowNodeList.item(0).getAttributes().getNamedItem(TableTagConstants.TOTWIDTH).getNodeValue()));
       
       row.setBottomLineRequired((rowNodeList.item(0).getAttributes().getNamedItem(TableTagConstants.BOTTOMLINEREQUIRED).getNodeValue()));
       
       row.setColumn(createColumn(rowNodeList.item(0)));

       return row;
     }

     protected Column[] createColumn(Node row) throws TableTagParseException{
    
       NodeList columnNodeList =((Element) row).getElementsByTagName(TableTagConstants.COLUMN);

       if (columnNodeList.getLength() ==0)
             throw new TableTagParseException(columnNodeList.toString());

       Column[] column= new Column[columnNodeList.getLength()];

       for(int i=0; i<columnNodeList.getLength();i++){
         column[i]= new Column();
         setColumnAttributes(column[i], columnNodeList.item(i).getAttributes());
         column[i].setColumnDetials(createColumnDetails(columnNodeList.item(i)));
         if(TableTagConstants.LINK.equalsIgnoreCase(column[i].getColumnType()))
              column[i].setLinkDetails(createLinkDetails(columnNodeList.item(i)));
       }

       return column;
     }

     protected void setColumnAttributes(Column column,NamedNodeMap map) throws TableTagParseException{
       column.setLabel(map.getNamedItem(TableTagConstants.LABEL).getNodeValue());
    
       column.setValue(map.getNamedItem(TableTagConstants.VALUE).getNodeValue());
    
       column.setValueType(map.getNamedItem(TableTagConstants.VALUETYPE).getNodeValue());
    
       column.setColumnType(map.getNamedItem(TableTagConstants.COLUMNTYPE).getNodeValue());
     }

     protected ColumnDetails createColumnDetails(Node column) throws TableTagParseException{
       NodeList columnDetailsNodeList = ((Element) column).getElementsByTagName(TableTagConstants.COLUMNDETAILS);

       if (columnDetailsNodeList.getLength()==0)
         throw new TableTagParseException(columnDetailsNodeList.toString());

       ColumnDetails columnDetails=new ColumnDetails();

    
       columnDetails.setRowStyle(columnDetailsNodeList.item(0).getAttributes().getNamedItem(TableTagConstants.ROWSTYLE).getNodeValue());
    
       columnDetails.setColWidth(columnDetailsNodeList.item(0).getAttributes().getNamedItem(TableTagConstants.COLWIDTH).getNodeValue());
       
       columnDetails.setAlign(columnDetailsNodeList.item(0).getAttributes().getNamedItem(TableTagConstants.ALIGN).getNodeValue());

       return columnDetails;

     }


     protected LinkDetails createLinkDetails(Node column) throws TableTagParseException{
       NodeList linkDetailsNodeList = ((Element) column).getElementsByTagName(TableTagConstants.LINKDETAILS);

       if (linkDetailsNodeList.getLength()==0)
         throw new TableTagParseException(linkDetailsNodeList.toString());

       LinkDetails linkDetails=new LinkDetails();

       if(linkDetailsNodeList.item(0).getAttributes().getNamedItem(TableTagConstants.ACTION).getNodeValue()==null)
         throw new TableTagParseException(linkDetailsNodeList.toString());

    
       linkDetails.setAction(linkDetailsNodeList.item(0).getAttributes().getNamedItem(TableTagConstants.ACTION).getNodeValue());

       linkDetails.setActionParam(createActionParams(linkDetailsNodeList.item(0)));

       return linkDetails;

     }

     protected ActionParam[] createActionParams(Node linkDetail) throws TableTagParseException{
       NodeList actionParamNodeList =((Element) linkDetail).getElementsByTagName(TableTagConstants.ACTIONPARAM);

       if (actionParamNodeList.getLength() == 0)
         throw new TableTagParseException(actionParamNodeList.toString());

       ActionParam[] actionParam=new ActionParam[actionParamNodeList.getLength()];

       for (int i=0; i<actionParamNodeList.getLength();i++){
         actionParam[i]=new ActionParam();
    
         actionParam[i].setName(actionParamNodeList.item(i).getAttributes().getNamedItem(TableTagConstants.NAME).getNodeValue());
    
         actionParam[i].setValue(actionParamNodeList.item(i).getAttributes().getNamedItem(TableTagConstants.VALUE).getNodeValue());
    
         actionParam[i].setValueType(actionParamNodeList.item(i).getAttributes().getNamedItem(TableTagConstants.VALUETYPE ).getNodeValue());
       }
       return actionParam;
     }



     public static void main(String args[]) throws Exception {
       TableTagParser ttp=new TableTagParser();
        ttp.parser("classes/component/example.xml");
     }

}
