package org.stagemonitor.loadtest

import groovy.transform.Canonical

@Canonical
class JMeterStatistic {

	String uuid = UUID.randomUUID().toString()
	String label

	Integer sampleCount
	Integer errorCount
	Double errorRate

	Double responseTimeAverage
	Double responseTimeMin
	Double responseTimeMax
	Double responseTime90thPercentile
	Double responseTime95thPercentile
	Double responseTime99thPercentile

	Double throughputPerSecond

	Double kilobytesReceivedPerSecond
	Double kilobytesSentPerSecond

	Double kilobytesReceived
	Double kilobytesSent
	Integer durationInSeconds

}
