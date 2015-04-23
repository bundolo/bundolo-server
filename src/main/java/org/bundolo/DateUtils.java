package org.bundolo;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DateUtils {

    private static final Logger logger = Logger.getLogger(DateUtils.class.getName());

    @Autowired
    @Qualifier("properties")
    private Properties properties;

    private long dateOffset;
    private double dateFactor;

    @PostConstruct
    public void init() {
	dateOffset = Long.parseLong(properties.getProperty("date.offset"));
	dateFactor = Double.parseDouble(properties.getProperty("date.factor"));
    }

    public Date newDate() {
	return newCalendar().getTime();
    }

    public Calendar newCalendar() {
	Calendar now = Calendar.getInstance();
	long systemTimeInMillis = now.getTimeInMillis();
	long applicationTimeInMillis = Math.round((systemTimeInMillis + dateOffset) * dateFactor);
	now.setTimeInMillis(applicationTimeInMillis);
	return now;
    }

    public void setDateOffset(long dateOffset) {
	this.dateOffset = dateOffset;
    }

    public void setDateFactor(double dateFactor) {
	this.dateFactor = dateFactor;
    }

}
