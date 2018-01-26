package com.wily.introscope.epagent;


import com.wily.introscope.epagent.EPAgent;
import com.wily.util.feedback.IModuleFeedbackChannel;
import com.wily.util.feedback.SystemOutFeedbackChannel;

/**
 * Utility class used to log to EPAgent log file (feedback channel) and also
 * to standard output if logToStdOut is set to true
 * @author Adam Bezecny -  CA Services
 *
 */
public class EPALogger {

	private static IModuleFeedbackChannel feedback;
	
	
	static{
	   
		if (EPAgent.GetInstance() == null) {
				feedback = new SystemOutFeedbackChannel("CEM EPA");
	        } else {
	        	feedback = EPAgent.GetFeedback();
	    }		
		
	}
	
    public static void epadebug(String message){
    	feedback.debug(message);
    }
	
    public static void epainfo(String message){
    	feedback.info(message);
    }    
    
    public static void epaerror(String message){
    	feedback.error(message);
    }      
    
    public static void epadebug(Throwable t){
    	feedback.debug(t);
    }    
    
    public static void epatrace(String message){
    	feedback.trace(message);
    }         
   
    public static void epaverbose(String message){
    	feedback.verbose(message);
    }     
    
}
