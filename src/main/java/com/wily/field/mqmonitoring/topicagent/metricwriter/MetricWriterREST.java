package com.wily.field.mqmonitoring.topicagent.metricwriter;

import java.util.Date;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import static com.wily.introscope.epagent.EPALogger.*;
/**
 * 
 * @author Adam Bezecny - CA Services
 * 
 * REST metric writer utilizes RESTFull interface of EP Agent in order to write metrics. It should be used
 * for local debugging from Eclipse when MQ Topic monitor runs outside of EP AGent JVM and cannot write metrics
 * directly utilizing internal API of EP Agent (which is implemented in MetricWriterISC class)
 *
 */
public class MetricWriterREST extends MetricWriter {

	private URL epaRestApiUrl;
	
	public MetricWriterREST(String queueManager, String epaRestApiUrl, int pollingInterval) throws MalformedURLException  {
		super(queueManager, pollingInterval);
		this.epaRestApiUrl = new URL(epaRestApiUrl);
	}
	
		// HTTP POST request
		private void sendPost(String payload) {

			try{

				HttpURLConnection con = (HttpURLConnection) epaRestApiUrl.openConnection();

				//add request header
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "application/json");


				// Send HTTP POST request
				con.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				wr.writeBytes(payload);
				wr.flush();
				wr.close();

				int responseCode = con.getResponseCode();

				BufferedReader in = new BufferedReader(
				        new InputStreamReader(con.getInputStream()));
				
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				if(responseCode!=200)
					epatrace("MetricWriterREST.sendPost.responseCode="+responseCode+" response="+response);
				
			}catch(Exception e){
				e.printStackTrace();
			}
			

		}	
	
	
	@Override
	public void writeLongAverage(String metric, long value) {

		String payload="{"
				+"	\"metrics\" : ["
				+"					{\"type\" : \"LongAverage\", \"name\" : \""+metric+"\", \"value\" : \""+value+"\"}"
				+"				  ]"
			+"}";		

		sendPost(payload);
		
	}

	@Override
	public void writeErrorDetectorEntry(String subject, String errorEntry) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void writePerIntervalCounter(String metric) {
		
		String payload="{"
				+"	\"metrics\" : ["
				+"					{\"type\" : \"PerIntervalCounter\", \"name\" : \""+metric+"\", \"value\" : \"1\"}"
				+"				  ]"
			+"}";		

		sendPost(payload);
		
	}

	@Override
	public void writePerIntervalCounter(String metric, int value) {
		
		String payload="{"
				+"	\"metrics\" : ["
				+"					{\"type\" : \"PerIntervalCounter\", \"name\" : \""+metric+"\", \"value\" : \""+value+"\"}"
				+"				  ]"
			+"}";		

		sendPost(payload);
		
	}
	
	
	@Override
	public void forceExistPerIntervalCounter(String metric) {
		
		String payload="{"
				+"	\"metrics\" : ["
				+"					{\"type\" : \"PerIntervalCounter\", \"name\" : \""+metric+"\", \"value\" : \"0\"}"
				+"				  ]"
			+"}";		

		sendPost(payload);
		
		
	}

	@Override
	public void forceExistLongIntervalCounter(String string) {
		
		String payload="{"
				+"	\"metrics\" : ["
				+"					{\"type\" : \"PerIntervalCounter\", \"name\" : \""+string+"\", \"value\" : \"0\"}"
				+"				  ]"
			+"}";		

		sendPost(payload);
		
		
	}

	@Override
	public void writeLongCounter(String metric, long value) {

		String payload="{"
				+"	\"metrics\" : ["
				+"					{\"type\" : \"LongCounter\", \"name\" : \""+metric+"\", \"value\" : \""+value+"\"}"
				+"				  ]"
			+"}";		

		sendPost(payload);
		
	}

	@Override
	public void writeIntCounter(String metric, int value) {
		
		String payload="{"
				+"	\"metrics\" : ["
				+"					{\"type\" : \"IntCounter\", \"name\" : \""+metric+"\", \"value\" : \""+value+"\"}"
				+"				  ]"
			+"}";		

		sendPost(payload);
		
	}

	@Override
	public void decrementCounter(String metric, int value) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void incrementCounter(String metric, int value) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void writeString(String metric, String value) {

		String payload="{"
							+"	\"metrics\" : ["
							+"					{\"type\" : \"StringEvent\", \"name\" : \""+metric+"\", \"value\" : \""+value+"\"}"
							+"				  ]"
						+"}";		

		sendPost(payload);

	}



	@Override
	public void writeTimestamp(String metric, Date value) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

}
