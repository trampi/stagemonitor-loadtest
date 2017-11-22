package org.stagemonitor.loadtest.runner

import com.xlson.groovycsv.CsvParser
import org.stagemonitor.loadtest.JMeterStatistic

import java.nio.file.Path

class JMeterStatisticsAggregator {

	static List<JMeterStatistic> aggregate(Path report) {
		def lines = CsvParser.parseCsv(report.newReader()).toList()

		def grouped = lines.groupBy { it.label }

		def results = grouped.collect {
			def line = it.value
			JMeterStatistic statistic = aggregateLines(line)
			statistic.label = it.key
			return statistic
		}

		def allGroups = aggregateLines(lines)
		allGroups.label = "All aggregated"
		results.add(0, allGroups)

		return results
	}

	private static JMeterStatistic aggregateLines(linesForGroup) {
		def group = new JMeterStatistic()
		group.sampleCount = linesForGroup.size
		group.errorCount = linesForGroup.count { it.success == false }
		group.errorRate = (double) group.errorCount / group.sampleCount

		// TODO: these numbers are rounded. is it possible to enhance the precision of "elapsed"?
		List<Double> elapsedList = linesForGroup.collect { it.elapsed.toDouble() }.sort()
		group.responseTimeAverage = elapsedList.sum() / elapsedList.size()
		group.responseTimeMax = elapsedList.max()
		group.responseTimeMin = elapsedList.min()
		group.responseTime90thPercentile = elapsedList.get((int) (elapsedList.size() * 0.90))
		group.responseTime95thPercentile = elapsedList.get((int) (elapsedList.size() * 0.95))
		group.responseTime99thPercentile = elapsedList.get((int) (elapsedList.size() * 0.99))

		List<BigInteger> timestampsSorted = linesForGroup.collect { it.timeStamp.toBigInteger() }.sort()
		def durationInSeconds = (timestampsSorted.last() - timestampsSorted.first()) / 1000
		group.throughputPerSecond = group.sampleCount / durationInSeconds

		group.kilobytesReceived = linesForGroup.collect { it.bytes.toDouble() }.sum() / 1024
		group.kilobytesReceivedPerSecond = group.kilobytesReceived / durationInSeconds

		group.kilobytesSent = linesForGroup.collect { it.sentBytes.toDouble() }.sum() / 1024
		group.kilobytesSentPerSecond = group.kilobytesSent / durationInSeconds
		group.durationInSeconds = durationInSeconds.intValue()
		return group
	}

}
