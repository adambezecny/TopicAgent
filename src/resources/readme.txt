##########################################################################################
##########################################################################################
##                                                                                      ##
##                                                                                      ##
##                          CA APM TopicAgent for IBM MQ 7.5                            ##
##                          Binary distribution                                         ##
##                          Created by CA Technologies, 2016                            ##	
##                                                                                      ##
##                                                                                      ##
##########################################################################################
##########################################################################################


In order to install TopicAgent plug-in of CA APM EPAgent follow the procedure below:

1.  Copy the jar files from 'lib' folder of the distribution into <<EPAGENT_INSTALL_DIR>>/lib
2.  Configure the jar files copied in previous step:
  2a. (Windows) in <<EPAGENT_INSTALL_DIR>>/conf/EPAService.conf as explained in the file EPAService.conf.diff contained in 'conf' folder of the distribution
  2b  (Unix)    in  <<EPAGENT_INSTALL_DIR>>/bin/EPACtrl.sh as explained in file EPACtrl.sh.diff contained in 'conf' folder of the distribution 
3.  Configure Topic Agent plug-in(s) in <<EPAGENT_INSTALL_DIR>>/IntroscopeEPAgent.properties as explained in the file IntroscopeEPAgent.properties.diff contained in 'conf' folder of the distribution
4.  File conf/ibm.mq.topicagent.tv.xml copy into <<ENTERPRISE_MANAGER_MOM>>/ext/xmltv folder and restart the server	 
