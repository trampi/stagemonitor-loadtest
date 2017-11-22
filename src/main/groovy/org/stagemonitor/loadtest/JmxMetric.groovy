package org.stagemonitor.loadtest

import groovy.transform.Canonical

@Canonical
class JmxMetric {

	// TODO: allocation rate
	// TODO: network traffic
	// TODO: startup time

	Long processCpuTimeNanoSeconds
	Long usedMemoryHeapKiloBytes
	Long usedMemoryNonHeapKiloBytes

	Long markSweepCollectionCount
	Long markSweepCollectionTimeInMilliseconds

	Long scavengeCollectionCount
	Long scavengeCollectionTimeInMilliseconds

	JmxMetric difference(JmxMetric other) {
		def result = new JmxMetric()

		this.properties.each {
			def propertyName = it.key.toString()
			if (this.getProperty(propertyName) instanceof Number) {
				def difference = (this.getProperty(propertyName) as Number) - (other.getProperty(propertyName) as Number)
				result.setProperty(propertyName, difference)
			}
		}

		return result
	}

}
