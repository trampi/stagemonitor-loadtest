package org.stagemonitor.loadtest.runner

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.stagemonitor.loadtest.HistoryDao
import org.stagemonitor.loadtest.StagemonitorVersionData

import java.time.Instant

@CompileStatic
@Slf4j
class RunnerApplication {

	static void main(String[] args) {
		runLoadtest()
	}

	public static void runLoadtest() {
		def runId = UUID.randomUUID().toString()

		def results = []

		LoadTestProfiles.getProfiles().each { systemUnderTest ->

			try {
				systemUnderTest.start()

				def jmxResult = JmxMetricCollector.collectMetrics(systemUnderTest)

				log.info("aggregating jmeter statistics")
				def jMeterStatistics = JMeterStatisticsAggregator.aggregate(systemUnderTest.getJMeterConfigurationPath().resolve("out/jtllog.csv"))

				def data = new StagemonitorVersionData([
						runId             : runId,
						version           : System.getProperty("stagemonitor.version"),
						when              : Instant.now(),
						host              : InetAddress.getLocalHost().getHostName(),
						application       : systemUnderTest.applicationName,
						loadTestProfile   : systemUnderTest.getStagemonitorPropertyOverrides(),
						description       : systemUnderTest.description,
						isBaseline        : systemUnderTest.isBaseline(),
						jMeterStatistics  : jMeterStatistics,
						beforeLoadtest    : jmxResult.beforeLoadtest,
						afterLoadtest     : jmxResult.afterLoadtestAndGc,
						loadtestDifference: jmxResult.difference,
				])

				log.info("aggregating jmeter statistics done")
				results.add(data)
			} catch (Exception e) {
				log.error("error running loadtest", e)
			} finally {
				systemUnderTest.stop()
			}
		}


		def history = HistoryDao.getHistory()
		history.addAll(results)
		HistoryDao.writeHistory(history)
	}

}
