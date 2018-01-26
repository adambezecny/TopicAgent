package com.wily.field.mqmonitoring.topicagent.metricwriter;

import java.lang.reflect.Method;
import java.util.Date;
import java.io.PrintStream;

import com.wily.introscope.agent.stat.ILongAverageDataAccumulator;
import com.wily.introscope.agent.stat.ILongIntervalCounterDataAccumulator;
import com.wily.introscope.epagent.api.DataRecorderFactory;
import com.wily.introscope.epagent.api.IllegalMetricNameException;
import com.wily.introscope.epagent.api.IntCounterDataRecorder;
import com.wily.introscope.epagent.api.LongAverageDataRecorder;
import com.wily.introscope.epagent.api.LongCounterDataRecorder;
import com.wily.introscope.epagent.api.TimestampDataRecorder;
import com.wily.introscope.epagent.api.StringEventDataRecorder;

/**
 * Implementation for writing the Introscope data in XML format to the EPA
 * 
 * @author Andreas Reiss - CA Wily Professional Service
 * @author Adam Bezecny - CA Services
 *
 */
public class MetricWriterISC extends MetricWriter {

    private PrintStream printStream;

    public MetricWriterISC(PrintStream printStream, String queueManager, int pollingInterval) {
    	super(queueManager, pollingInterval);
        this.printStream = printStream;
    }

    @Override
    public void writePerIntervalCounter(String metric) {
        com.wily.introscope.epagent.api.PerIntervalCounterDataRecorder recorder;
        try {
            recorder = DataRecorderFactory.createPerIntervalCounterDataRecorder(metric);
            recorder.recordIncident();
        } catch (IllegalMetricNameException e) {
            e.printStackTrace();
        }
    }

	@Override
	public void writePerIntervalCounter(String metric, int value) {
        com.wily.introscope.epagent.api.PerIntervalCounterDataRecorder recorder;
        try {
            recorder = DataRecorderFactory.createPerIntervalCounterDataRecorder(metric);
            recorder.recordMultipleIncidents(value);
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}	
    
    
    @Override
    public void writeTimestamp(String metric, Date value) {
        TimestampDataRecorder recorder;
        try {
            recorder = DataRecorderFactory.createTimestampDataRecorder(metric);
            recorder.recordTimestamp(value.getTime());
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    @Override
    public void writeString(String metric, String value){

        StringEventDataRecorder recorder;
        try {
            recorder = DataRecorderFactory.createStringEventDataRecorder(metric);
            recorder.recordValue(value);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    } 
    
    @Override
    public void writeLongAverage(String metric, long value) {
        LongAverageDataRecorder recorder;
        try {
            recorder = DataRecorderFactory.createLongAverageDataRecorder(metric);
            recorder.recordDataPoint(value);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    @Override
    public void writeLongCounter(String metric, long value) {
        LongCounterDataRecorder recorder;
        try {
            recorder = DataRecorderFactory.createLongCounterDataRecorder(metric);
            recorder.recordCurrentValue(value);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }


    @Override
    public void writeErrorDetectorEntry(String subject, String errorEntry) {

        printStream
            .println("<event  resource=\"Topic Monitor\"> <param name=\"Trace Type\" value=\"ErrorSnapshot\"/> <calledComponent  resource=\""
                + subject
                + "\"><param name=\"Error Message\" value=\""
                + errorEntry
                + "\"/>  </calledComponent> </event>");

    }

    @Override
    public void forceExistPerIntervalCounter(String metric) {
        com.wily.introscope.epagent.api.PerIntervalCounterDataRecorder recorder;
        try {
            recorder = DataRecorderFactory.createPerIntervalCounterDataRecorder(metric);
            Method method =
                recorder.getClass().getDeclaredMethod("getIntervalCounterDataAccumulator",
                    new Class[0]);
            method.setAccessible(true);
            ILongIntervalCounterDataAccumulator accumulator =
                (ILongIntervalCounterDataAccumulator) method.invoke(recorder, new Object[0]);
            accumulator.forceMetricToExist(null);
        } catch (Exception e) {
        	e.printStackTrace();
        }

    }

    @Override
    public void forceExistLongIntervalCounter(String metric) {
        LongAverageDataRecorder recorder;
        try {
            recorder = DataRecorderFactory.createLongAverageDataRecorder(metric);
            Method method =
                recorder.getClass().getDeclaredMethod("getBackingLongAverageDataAccumulator",
                    new Class[0]);
            method.setAccessible(true);
            ILongAverageDataAccumulator accumulator =
                (ILongAverageDataAccumulator) method.invoke(recorder, new Object[0]);
            accumulator.forceMetricToExist(null);
        } catch (Exception e) {
        	e.printStackTrace();
        }

    }

    @Override
    public void writeIntCounter(String metric, int value) {
        IntCounterDataRecorder recorder;
        try {
            //recorder = DataRecorderFactory.createPerIntervalCounterDataRecorder(metric);
             recorder = DataRecorderFactory.createIntCounterDataRecorder(metric);
            recorder.recordCurrentValue(value);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    @Override
    public void decrementCounter(String metric, int value) {
        IntCounterDataRecorder recorder;
        try {
            recorder = DataRecorderFactory.createIntCounterDataRecorder(metric);
            recorder.subtract(value);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    @Override
    public void incrementCounter(String metric, int value) {
        IntCounterDataRecorder recorder;
        try {
            recorder = DataRecorderFactory.createIntCounterDataRecorder(metric);
            // recorder = DataRecorderFactory.createIntCounterDataRecorder(metric);
            recorder.add(value);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
	
}
