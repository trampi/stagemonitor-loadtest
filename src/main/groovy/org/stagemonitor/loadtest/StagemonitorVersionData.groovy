package org.stagemonitor.loadtest

import groovy.transform.Canonical

import java.time.Instant

@Canonical
class StagemonitorVersionData {

	String runId
	String version
	Instant when
	String host
	String application
	String loadTestProfile
	String description
	boolean isBaseline

	List<JMeterStatistic> jMeterStatistics

	JmxMetric beforeLoadtest
	JmxMetric afterLoadtest
	JmxMetric loadtestDifference

}
