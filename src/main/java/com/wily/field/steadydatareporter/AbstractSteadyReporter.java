package com.wily.field.steadydatareporter;

/**
 * Abstract class for implementations reporting steady metrics in every interval.
 * 
 * @author Andreas Reiss - CA Wily Professional Service
 * @author Adam Bezecny - CA Services
 *
 */

public abstract class AbstractSteadyReporter {

	/**
	 * fully qualified metric name, e.g. A|B|C:Metric1
	 */
    protected final String metricPath;
  
    /**
     * value of the metric
     */
    protected Object value;

    /**
     * how many times metric value can be 
     * forwarded/replicated by this steady reporter
     */
    private int forwardCounter;
  
    /**
     * Constructor implementation.
     * 
     * @param metricPath
     */
    public AbstractSteadyReporter(String metricPath) {
      this.metricPath = metricPath;
      
      synchronized(this){
    	  this.forwardCounter = 0;
      }
      
      
    }

   /**
    * Reports new value to SteadyReporter instance. Must be called by client
    * in order to really write metric to EPAgent by whatever means(REST API, internal APIs, etc.).
    * e.g. {@link com.wily.field.steadydatareporter.LongAverageSteadyReporter#writeLongAverage()}
    * 
    * @param averageValue
    */
    protected synchronized void reportInternal(Object averageValue) {
    	 //when new value is reported reset the counter to zero!
    	 this.value = averageValue;
    	 this.forwardCounter = 0;
    }

   /**
    * Replicates/forwards last reported value
    * of the metric
    */
    public void forwardMetric() {
      
     /**
      *this might happen if this SteadyReporter
      *is processed by SteadyReportingThread
      *exactly between call of constructor and actually
      *setting the value via  reportInternal method!
      *In this case let's just ignore it as there is
      *nothing to forward/replicate yet	
      */
      synchronized(this){
    	  if(this.value == null) return;	
      }
	  
      reportMetric(this.value);
	  
      synchronized(this){
		  this.forwardCounter++;  
	  }
      
	  
    }

	
    public String getMetricPath() {
     return this.metricPath;
    }

	public synchronized int getForwardCounter() {
		return this.forwardCounter;
	}
	
	/**
	  * 
	  * Physically reports metric to EPAgent. Descendant class
	  * needs to provide implementation of this method.
	  * 
	  * @param value
	*/
	protected abstract void reportMetric(Object value);
		
	protected abstract String getMetricType();

}
