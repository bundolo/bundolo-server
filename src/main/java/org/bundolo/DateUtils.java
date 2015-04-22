package org.bundolo;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DateUtils {

    private static final Logger logger = Logger.getLogger(DateUtils.class.getName());

    @Autowired
    @Qualifier("properties")
    private Properties properties;

    public Date newDate() {
	return newCalendar().getTime();
    }

    public Calendar newCalendar() {
	long dateOffset = Long.parseLong(properties.getProperty("date.offset"));
	double dateFactor = Double.parseDouble(properties.getProperty("date.factor"));

	Calendar now = Calendar.getInstance();
	long systemTimeInMillis = now.getTimeInMillis();
	long applicationTimeInMillis = Math.round((systemTimeInMillis + dateOffset) * dateFactor);
	now.setTimeInMillis(applicationTimeInMillis);
	return now;
    }

}
