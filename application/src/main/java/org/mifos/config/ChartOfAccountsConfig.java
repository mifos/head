/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.config;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.accounts.financial.business.COABO;
import org.mifos.accounts.financial.business.GLCategoryType;
import org.mifos.config.exceptions.ConfigurationException;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.core.ClasspathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Encapsulates chart of accounts configuration.
 * <p>
 * Use {@link #load(String)} to get an instance of this class.
 */
public class ChartOfAccountsConfig {
    // XML element names
    private static final String ASSETS_CATEGORY = "GLAssetsAccount";
    private static final String LIABILITIES_CATEGORY = "GLLiabilitiesAccount";
    private static final String INCOME_CATEGORY = "GLIncomeAccount";
    private static final String EXPENDITURE_CATEGORY = "GLExpenditureAccount";

    // XML attribute names
    private static final String GLCODE_ATTR = "code";
    protected static final String ACCOUNT_NAME_ATTR = "name";

    private ChartOfAccountsConfig() {
    }

    private Document coaDocument;

    /**
     * Factory method which loads the Chart of Accounts configuration from the
     * given filename. Given XML filename will be validated against
     * {@link FilePaths#CHART_OF_ACCOUNTS_SCHEMA}.
     *
     * @param chartOfAccountsXml
     *            a relative path to the Chart of Accounts configuration file.
     */
    public static ChartOfAccountsConfig load(String chartOfAccountsXml) throws ConfigurationException {
        ChartOfAccountsConfig instance = null;
        Document document = null;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = dbf.newDocumentBuilder();
            document = parser.parse(new File(ClasspathResource.getURI(chartOfAccountsXml)));

            // create a SchemaFactory capable of understanding XML schemas
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // load an XML schema
            Source schemaFile = new StreamSource(new File(ClasspathResource.getURI(FilePaths.CHART_OF_ACCOUNTS_SCHEMA)));
            Schema schema = factory.newSchema(schemaFile);

            // create a Validator instance and validate document
            Validator validator = schema.newValidator();
            validator.validate(new DOMSource(document));
        } catch (IOException e) {
            throw new ConfigurationException(e);
        } catch (SAXException e) {
            throw new ConfigurationException(e);
        } catch (URISyntaxException e) {
            throw new ConfigurationException(e);
        } catch (ParserConfigurationException e) {
            throw new ConfigurationException(e);
        }

        instance = new ChartOfAccountsConfig();
        instance.coaDocument = document;

        return instance;
    }

    /**
     * Get Chart of Accounts URI. Returns custom config file if present, returns
     * default otherwise.
     * <p>
     * Allows for easy overriding/customization of the chart of accounts by
     * placing a file called <code>mifosChartOfAccounts.custom.xml</code>
     * anywhere in the application server classpath.
     *
     * @return relative path to Chart of Accounts config file that the
     *         {@link ClasspathResource} can use to derive the actual on-disk
     *         location.
     */
    public static String getCoaUri(Session session) {
        final boolean customCoaExists = (null != ClasspathResource.findResource(FilePaths.CHART_OF_ACCOUNTS_CUSTOM));

        if (customCoaExists)
            return FilePaths.CHART_OF_ACCOUNTS_CUSTOM;

        // if data exists in the database, the only way to add GL accounts is
        // to create a custom chart of accounts XML file and place it on the
        // classpath
        if (isLoaded(session) && !customCoaExists)
            return null;

        return FilePaths.CHART_OF_ACCOUNTS_DEFAULT;
    }

    /**
     * The only time you <em>can't</em> load the chart of accounts is when the database
     * has existing chart of accounts data, but a custom chart of accounts
     * configuration file is not found on the classpath.
     */
    public static boolean canLoadCoa(Session session) {
        return null != getCoaUri(session);
    }

    /**
     * Convenience method to get all configured general ledger accounts. Result
     * set is guaranteed to be ordered since parent accounts will probably need
     * to be dealt with before children.
     */
    public Set<GLAccount> getGLAccounts() {
        // LinkedHashSet satisfies ordering guarantee, above
        Set<GLAccount> glAccounts = new LinkedHashSet<GLAccount>();
        for (GLCategoryType category : GLCategoryType.values())
            glAccounts.addAll(traverse(getCategory(category), null));
        return glAccounts;
    }

    /**
     * Get top-level general ledger account (aka category) from the chart of
     * accounts configuration.
     */
    protected Node getCategory(GLCategoryType category) {
        // these could also be placed in a HashTable, but with only four
        // choices, this seemed easier to maintain. Consider initializing a
        // table in the #load() method if this becomes unwieldy.
        if (category == GLCategoryType.ASSET)
            return coaDocument.getElementsByTagName(ASSETS_CATEGORY).item(0);
        else if (category == GLCategoryType.LIABILITY)
            return coaDocument.getElementsByTagName(LIABILITIES_CATEGORY).item(0);
        else if (category == GLCategoryType.INCOME)
            return coaDocument.getElementsByTagName(INCOME_CATEGORY).item(0);
        else if (category == GLCategoryType.EXPENDITURE)
            return coaDocument.getElementsByTagName(EXPENDITURE_CATEGORY).item(0);
        throw new RuntimeException("invalid category type: " + category);
    }

    private static GLCategoryType getTopLevelType(Node node) {
        assert Node.ELEMENT_NODE == node.getNodeType();
        String elementName = node.getNodeName();
        // these could also be placed in a HashTable, but with only four
        // choices, this seemed easier to maintain. Consider initializing a
        // table in the #load() method if this becomes unwieldy.
        if (ASSETS_CATEGORY.equals(elementName))
            return GLCategoryType.ASSET;
        else if (LIABILITIES_CATEGORY.equals(elementName))
            return GLCategoryType.LIABILITY;
        else if (INCOME_CATEGORY.equals(elementName))
            return GLCategoryType.INCOME;
        else if (EXPENDITURE_CATEGORY.equals(elementName))
            return GLCategoryType.EXPENDITURE;
        return null;
    }

    /**
     * Recursively traverses given {@link Node} tree.
     * <p>
     * Does not check for null glCode or name attributes. This is enforced
     * during validation by the schema.
     * <p>
     * Result set is guaranteed to be ordered since parent accounts will
     * probably need to be dealt with before children.
     *
     * @param node
     *            Usually a top-level account (aka category). This means
     *            GLAssetsAccount, GLLiabilitiesAccount, etc. Also accepts any
     *            lower-level GLAccount <code>Node</code>s (and this can be
     *            useful in testing). Must not be null.
     * @param parentGlCode
     *            General ledger code of parent account. May be null, as is the
     *            case for top-level accounts (aka categories).
     */
    protected static Set<GLAccount> traverse(Node node, String parentGlCode) {
        assert null != node;

        // LinkedHashSet satisfies ordering guarantee, above
        Set<GLAccount> glAccounts = new LinkedHashSet<GLAccount>();

        GLAccount glAccount = new GLAccount();
        glAccount.glCode = node.getAttributes().getNamedItem(GLCODE_ATTR).getNodeValue();
        glAccount.name = node.getAttributes().getNamedItem(ACCOUNT_NAME_ATTR).getNodeValue();
        glAccount.parentGlCode = parentGlCode;
        glAccount.categoryType = getTopLevelType(node);
        glAccounts.add(glAccount);

        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            // necessary since children may be Node.TEXT_NODE type
            if (Node.ELEMENT_NODE != child.getNodeType())
                continue;

            if (!glAccounts.addAll(traverse(child, glAccount.glCode))) {
                // A duplicate exists. The exception will help us avoid any
                // serious errors later on.
                throw new RuntimeException("duplicate account definition. " + GLCODE_ATTR + "=" + glAccount.glCode
                        + " " + ACCOUNT_NAME_ATTR + "=" + glAccount.name);
            }
        }

        return glAccounts;
    }

    /**
     * @return true if the chart of accounts has been loaded from the on-disk
     *         configuration file into the database.
     */
    public static boolean isLoaded(Session session) {
        // A more comprehensive check would also make sure no rows exist in
        // the following tables: coahierarchy, coa_idmapper, gl_code
        Query query = session.getNamedQuery(NamedQueryConstants.GET_ALL_COA);
        List<COABO> coaBoList = query.list();
        return !coaBoList.isEmpty();
    }
}
