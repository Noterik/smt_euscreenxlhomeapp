package org.springfield.lou.application.types.conditions;

import java.util.Calendar;
import java.util.Date;

import org.springfield.fs.FsNode;

public class TimeRangeCondition extends FilterCondition {
	
	private String field;
	private Date timeStart;
	private Date timeEnd;
	private static Calendar calendar = Calendar.getInstance();

	public TimeRangeCondition(int timeStartInt, int timeEndInt, String field) {
		calendar.clear();
		calendar.set(Calendar.YEAR, timeStartInt);
		timeStart = calendar.getTime();
		
		calendar.clear();
		calendar.set(Calendar.YEAR, timeEndInt);
		timeEnd = calendar.getTime();
		
		this.field = field;
	}

	@Override
	public boolean allow(FsNode node) {
		// TODO Auto-generated method stub
		
		try{
			int year = Integer.parseInt(node.getProperty(field));
			calendar.clear();
			calendar.set(Calendar.YEAR, year);
			
			Date date = calendar.getTime();
			
			if(timeStart.before(date) && timeEnd.after(date)){
				this.getPassed().add(node);
				return true;
			}
		}catch(NumberFormatException nfe){
			System.out.println("No date set!");
		}
		
		return false;
	}

}
