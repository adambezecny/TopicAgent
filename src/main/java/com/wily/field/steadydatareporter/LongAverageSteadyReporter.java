package com.wily.field.steadydatareporter;


import com.wily.field.mqmonitoring.topicagent.metricwriter.MetricWriter;

/**
 * Implementation for storing and reporting Long Average data
 * 
 * @author Andreas Reiss - CA Wily Professional Service
 * @author Adam Bezecny - CA Services
 *
 */
public class LongAverageSteadyReporter extends AbstractSteadyReporter {

	private MetricWriter metricWriter = null;
	
  /**
   * @param metricname
   */
  public LongAverageSteadyReporter(String metricname, MetricWriter metricWriter) {
    super(metricname);
    this.metricWriter = metricWriter;
  }

  /**
   * This method is used by client of SteadyReporter in order to write new metric value.
   * The value provided will be written both "physically" via provided metric writer
   * and also internally into SteadyReporter so that it can be used for value replication.
   * @param value
   */
  public void writeLongAverage(Object value){
	  //reports new metric value to SteadyReporter
	  reportInternal(value);
	  reportMetric(value);
  }
  
  
  @Override
  protected String getMetricType() {
    return "LongAverage";
  }

  
  @Override
  protected void reportMetric(Object value) {
	  
	  //reports new metric value "physically", 
	  //i.e. writes the metric using provided Metric writer
	  this.metricWriter.writeLongAverage(this.metricPath, (Long)this.value);
	  
  }


}
