package org.mifos.config;

import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
import org.mifos.framework.components.configuration.business.ConfigurationKeyValueInteger;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.util.helpers.Constants;


public class ClientRules {
	
	public static final String ClientRulesCenterHierarchyExists = "ClientRules.CenterHierarchyExists";
	public static final String ClientRulesClientCanExistOutsideGroup = "ClientRules.ClientCanExistOutsideGroup";
	public static final String ClientRulesGroupCanApplyLoans = "ClientRules.GroupCanApplyLoans";
	public static final String ClientCanExistOutsideGroupKey = "ClientCanExistOutsideGroup";
	public static final String GroupCanApplyLoansKey = "GroupCanApplyLoans";
	private static Boolean centerHierarchyExists;
	private static Boolean groupCanApplyLoans;
	private static Boolean clientCanExistOutsideGroup;
	private static MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CONFIGURATION_LOGGER);
	
	public static void refresh()
	{
		centerHierarchyExists = null;
		groupCanApplyLoans = null;
		clientCanExistOutsideGroup = null;
		centerHierarchyExists = getCenterHierarchyExists();
		groupCanApplyLoans = getGroupCanApplyLoans();
		clientCanExistOutsideGroup = getClientCanExistOutsideGroup();
	}
	
	public static Boolean getCenterHierarchyExists()
	{
		if (centerHierarchyExists == null)
			centerHierarchyExists = getCenterHierarchyExistsFromConfig();
		return centerHierarchyExists;
	}
	
	public static Boolean getGroupCanApplyLoans()
	{
		if (groupCanApplyLoans == null)
			groupCanApplyLoans = getGroupCanApplyLoansFromConfig();
		return groupCanApplyLoans;
			
	}
	public static Boolean getClientCanExistOutsideGroup()
	{
		if (clientCanExistOutsideGroup == null)
			clientCanExistOutsideGroup = getClientCanExistOutsideGroupFromConfig();
		return getClientCanExistOutsideGroupFromConfig();
	}
	
	private static Boolean getCenterHierarchyExistsFromConfig()
	{
		short defaultValue = Constants.YES;
		short value;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(ClientRulesCenterHierarchyExists))
			value = configMgr.getShort(ClientRulesCenterHierarchyExists);
		else 
			value = defaultValue;
		return value == Constants.YES;
	}
	
	private static Boolean getGroupCanApplyLoansFromConfig()
	{
		ConfigurationPersistence configPersistence = new ConfigurationPersistence();
		short value;
		try
		{
			ConfigurationKeyValueInteger dbValue = configPersistence.getConfigurationKeyValueInteger(GroupCanApplyLoansKey);
			ConfigurationManager configMgr = ConfigurationManager.getInstance();
			if (configMgr.containsKey(ClientRulesGroupCanApplyLoans))
			{
				value = configMgr.getShort(ClientRulesGroupCanApplyLoans);
				if (value == Constants.YES)
				{
					if (dbValue.getValue() == Constants.NO)
						configPersistence.updateConfigurationKeyValueInteger(GroupCanApplyLoansKey, value);
				}
				else if (value == Constants.NO)
				{
					if (dbValue.getValue() == Constants.YES) // flag error, how should this be handled??
					{
						logger.error("The value ClientRules.CenterHierarchyExists in the properties file " +
								"applicationConfiguration.properties need to be set to 1 because it was set to 1 "
								+ "before and can't be changed to 0.");
								
						value = Constants.YES;
					}
				}
			}
			else  // get the value from db
				value = (short)dbValue.getValue();
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex.getMessage());
		}
		
		return value == Constants.YES;
	}
	
	private static Boolean getClientCanExistOutsideGroupFromConfig()
	{
		
		ConfigurationPersistence configPersistence = new ConfigurationPersistence();
		short value;
		try
		{
			ConfigurationKeyValueInteger dbValue = configPersistence.getConfigurationKeyValueInteger(ClientCanExistOutsideGroupKey);
			ConfigurationManager configMgr = ConfigurationManager.getInstance();
			if (configMgr.containsKey(ClientRulesClientCanExistOutsideGroup))
			{
				value = configMgr.getShort(ClientRulesClientCanExistOutsideGroup);
				if (value == Constants.YES)
				{
					if (dbValue.getValue() == Constants.NO)
						configPersistence.updateConfigurationKeyValueInteger(ClientCanExistOutsideGroupKey, value);
				}
				else if (value == Constants.NO)
				{
					if (dbValue.getValue() == Constants.YES) // flag error, how should this be handled??
					{
						value = Constants.YES;
						logger.error("The value ClientRules.CenterHierarchyExists in the properties file " +
								"applicationConfiguration.properties need to be set to 1 because it was set to 1 "
								+ "before and can't be changed to 0.");
					}
				}
			}
			else // get the value from db
				value = (short)dbValue.getValue();
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex.getMessage());
		}
		
		return value == Constants.YES;
	}

}

