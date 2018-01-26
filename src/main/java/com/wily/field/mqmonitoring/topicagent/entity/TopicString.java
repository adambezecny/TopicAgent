package com.wily.field.mqmonitoring.topicagent.entity;

/**
 * 
 * @author Adam Bezecny - CA Services
 * 
 * This class represents Topic String in IBM MQ. Topic string can be
 * referred by many entities like topic, subscription, publication, etc.
 * Purpose of this class is not only to wrap the topic string itself
 * but also provide placeholder for all advanced functionality about topic string,
 * primarily functionality around topic wild cards.
 *
 */
public class TopicString {

	private final String topicString;

	public TopicString(String topicString) {
		this.topicString = topicString;
	}

	public String getTopicString() {
		return topicString;
	}

	@Override
	public String toString() {
		return "TopicString [topicString=" + topicString + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((topicString == null) ? 0 : topicString.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TopicString other = (TopicString) obj;
		if (topicString == null) {
			if (other.topicString != null)
				return false;
		} else if (!topicString.equals(other.topicString))
			return false;
		return true;
	}
	
	
	/**
	 * Returns parent topic without any wild cards in case they are used in topic string.
	 * If no wild cards are used it will return original topic string
	 * E.g. for /A/B/C return A/B/C
	 *      for A/B/# or A/B/* return A/B
	 *      for A/#/C  return A
	 * @return
	 */
	public String getUnwildcardedParentTopic(){
		
		//if wild-card characters are not contained in topic string just return topic string without any change 
		if(!this.topicString.contains("#") && !this.topicString.contains("*"))	return this.topicString;
		
		int indexFirstWildCardHash = this.topicString.indexOf("#");
		int indexFirstWildCardStar = this.topicString.indexOf("*");
		
		int indexFirstWildCard;
		
		if(indexFirstWildCardHash == -1)/*if topic string contains only stars*/{
			indexFirstWildCard=indexFirstWildCardStar;
		}else if(indexFirstWildCardStar == -1)/*if topic string contains only hashes*/{
			indexFirstWildCard=indexFirstWildCardHash;
		}else/*if it contains both stars & hashes take the first occurring*/{
			//first wild card index is the smaller value from indexFirstWildCardHash & indexFirstWildCardStar. 
			indexFirstWildCard = (indexFirstWildCardHash > indexFirstWildCardStar ? indexFirstWildCardStar : indexFirstWildCardHash);
		}
		
		String topicStringSubstr = this.topicString.substring(0, indexFirstWildCard);//take everything until very first wild card
		int indexLastBackwardSlash = topicStringSubstr.lastIndexOf("/");//locate very last segment separator "/" (which is before very first wild card)
		
		String result = topicStringSubstr.substring(0, indexLastBackwardSlash);//take everything until segment separator determined in previous step 
		
		return result;
		
	}
	
	/**
	 * converts topic string to format appropriate for Introscope metric tree
	 * basically replaces all '/' with '\|' (regular expression for '|' literal)
	 * If topic string starts with / (e.g. /Price) it will remove first '/'
	 * @return
	 */
	public String toMetricPath(){
		
		String topicStr = this.topicString;
		
		if(topicStr.startsWith("/"))
			topicStr = topicStr.substring(1);
		
		return topicStr.replaceAll("/", "\\|");
	}
	
}
