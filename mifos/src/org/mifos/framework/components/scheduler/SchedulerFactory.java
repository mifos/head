package org.mifos.framework.components.scheduler;

/**
 * @author navitas
 * Created on Aug 12, 2005
 * Factory to generate ScheduleData , ScheduleInputs and Scheduler instances.
 */
public class SchedulerFactory {
	/**
	 * Method to return ScheduleIntf holding the Scheduler class instance 
	 * @return Scheduler instance 
	 */
	public static SchedulerIntf getScheduler(){
		return new Scheduler(); 
	}
	
	/**
	 * Method to return ScheduleDataIntf holding the DayData/WeekData/MonthData class instance
	 * @param type  schedule data type which may be Day/Week/Month. 
	 * @return SchedulerData i.e. either DayData, WeekData or MonthData instance 
	 */
	public static ScheduleDataIntf getScheduleData(String type) throws SchedulerException{
		String str= Constants.Package + type + Constants.Data;
		try{
			Class c = Class.forName(str);
			return (ScheduleDataIntf)c.newInstance();
		}catch(ClassNotFoundException cne){
			throw SchedulerExceptionFactory.getSchedulerException(cne.getMessage(), false);
		}
		catch(InstantiationException ie){
			throw SchedulerExceptionFactory.getSchedulerException(ie.getMessage(), false);
		}
		catch(IllegalAccessException iae){
			throw SchedulerExceptionFactory.getSchedulerException(iae.getMessage(), false);
		}
	}
	
	/**
	 * Method to return ScheduleDataIntf holding the DayData/WeekData/MonthData class instance
	 * @param type  schedule data type which may be Day/Week/Month. 
	 * @return SchedulerData i.e. either DayData, WeekData or MonthData instance 
	 */
	public static ScheduleDataIntf getScheduleData(Short typeValue) throws SchedulerException{
		String type="";
		if (typeValue == 1)
			type=Constants.WEEK;
		if (typeValue.intValue() == 2)
			type=Constants.MONTH;
		if (typeValue.intValue() == 3)
			type=Constants.DAY;
		String str= Constants.Package + type + Constants.Data;
		try{
			Class c = Class.forName(str);
			return (ScheduleDataIntf)c.newInstance();
		}catch(ClassNotFoundException cne){
			throw SchedulerExceptionFactory.getSchedulerException(cne.getMessage(), false);
		}
		catch(InstantiationException ie){
			throw SchedulerExceptionFactory.getSchedulerException(ie.getMessage(), false);
		}
		catch(IllegalAccessException iae){
			throw SchedulerExceptionFactory.getSchedulerException(iae.getMessage(), false);
		}
	}
	
	/**
	 * Method to return ScheduleInputsIntf holding the SchedulerInputs class instance 
	 * @return SchedulerInputs instance 
	 */
	public static ScheduleInputsIntf getScheduleInputs(){
		return new ScheduleInputs(); 
	}
}
