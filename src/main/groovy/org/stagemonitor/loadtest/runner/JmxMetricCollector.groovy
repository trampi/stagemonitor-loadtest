package org.stagemonitor.loadtest.runner

import com.sun.tools.attach.VirtualMachine
import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.stagemonitor.loadtest.JmxMetric
import org.stagemonitor.loadtest.runner.systemundertest.SystemUnderTest

import javax.management.JMX
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import javax.management.openmbean.CompositeData
import javax.management.remote.JMXConnector
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL
import java.lang.management.MemoryMXBean

@CompileStatic
@Slf4j
class JmxMetricCollector {

	static final String CONNECTOR_ADDRESS =
			"com.sun.management.jmxremote.localConnectorAddress"

	static JmxMetricResult collectMetrics(SystemUnderTest systemUnderTest) {
		def jmxConnector = connect(systemUnderTest.jmxApplicationName)
		def connection = jmxConnector.getMBeanServerConnection()

		log.info("before warmup")
		JMeterRunner.warmup(systemUnderTest.getJMeterConfigurationPath())
		log.info("after warmup")

		triggerGc(connection)
		triggerGc(connection)
		triggerGc(connection)
		triggerGc(connection)
		sleep(2000)
		log.info("before load test")
		def beforeJMeter = collectMetric(connection)
		JMeterRunner.runJmeter(systemUnderTest.getJMeterConfigurationPath())

		triggerGc(connection)
		triggerGc(connection)
		triggerGc(connection)
		triggerGc(connection)
		sleep(2000)
		def afterJMeterAndGc = collectMetric(connection)
		log.info("after load test")

		def difference = afterJMeterAndGc.difference(beforeJMeter)

		jmxConnector.close()

		return new JmxMetricResult(beforeJMeter, afterJMeterAndGc, difference)
	}

	@Canonical
	static class JmxMetricResult {
		JmxMetric beforeLoadtest, afterLoadtestAndGc, difference
	}


	private static triggerGc(MBeanServerConnection mBeanServerConnection) {
		def proxy = JMX.newMBeanProxy(mBeanServerConnection, new ObjectName("java.lang:type=Memory"), MemoryMXBean)
		proxy.gc()
	}

	private static JmxMetric collectMetric(MBeanServerConnection mBeanServerConnection) {

		def operatingSystemObjectName = new ObjectName("java.lang:type=OperatingSystem")

		def metric = new JmxMetric()

		metric.processCpuTimeNanoSeconds = mBeanServerConnection.getAttribute(operatingSystemObjectName, "ProcessCpuTime") as Long

		def heapAttribute = mBeanServerConnection.getAttribute(new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage") as CompositeData
		metric.usedMemoryHeapKiloBytes = ((heapAttribute.get("used") as Long) / 1024).toLong()

		def nonHeapAttribute = mBeanServerConnection.getAttribute(new ObjectName("java.lang:type=Memory"), "NonHeapMemoryUsage") as CompositeData
		metric.usedMemoryNonHeapKiloBytes = ((nonHeapAttribute.get("used") as Long) / 1024).toLong()

		def markSweepGarbageCollector = new ObjectName("java.lang:type=GarbageCollector,name=PS MarkSweep")
		metric.markSweepCollectionCount = mBeanServerConnection.getAttribute(
				markSweepGarbageCollector,
				"CollectionCount") as Long
		metric.markSweepCollectionTimeInMilliseconds = mBeanServerConnection.getAttribute(
				markSweepGarbageCollector,
				"CollectionTime") as Long

		def scavengeGarbageCollector = new ObjectName("java.lang:type=GarbageCollector,name=PS Scavenge")
		metric.scavengeCollectionCount = mBeanServerConnection.getAttribute(scavengeGarbageCollector,
				"CollectionCount") as Long
		metric.scavengeCollectionTimeInMilliseconds = mBeanServerConnection.getAttribute(scavengeGarbageCollector,
				"CollectionTime") as Long

		return metric
	}

	private static JMXConnector connect(String applicationDisplayName) {
		def petClinicVmDescriptor = VirtualMachine.list().find {
			(it.displayName() == applicationDisplayName)
		}

		def petClinicVm = VirtualMachine.attach(petClinicVmDescriptor)

		def connectorAddress = petClinicVm.getAgentProperties().get(CONNECTOR_ADDRESS) as String

		if (connectorAddress == null) {
			def agent = petClinicVm.getSystemProperties().getProperty("java.home") +
					File.separator + "lib" + File.separator + "management-agent.jar"
			petClinicVm.loadAgent(agent)

			// agent is started, get the connector address
			connectorAddress = petClinicVm.getAgentProperties().getProperty(CONNECTOR_ADDRESS) as String
		}

		JMXServiceURL jmxUrl = new JMXServiceURL(connectorAddress)
		return JMXConnectorFactory.connect(jmxUrl)
	}

}
