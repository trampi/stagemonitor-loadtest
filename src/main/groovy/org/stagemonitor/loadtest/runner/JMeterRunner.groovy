package org.stagemonitor.loadtest.runner

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.nio.file.Path

@CompileStatic
@Slf4j
class JMeterRunner {

	static warmup(Path jMeterSuitePath) {
		cleanOutput(jMeterSuitePath)
		run("jmeter -n -t petclinic_test_plan_warmup.jmx -l out/jtllog.csv -j out/jmeterrun.log -e -o out/report_output", jMeterSuitePath)
	}

	static runJmeter(Path jMeterSuitePath) {
		cleanOutput(jMeterSuitePath)
		run("jmeter -n -t petclinic_test_plan.jmx -l out/jtllog.csv -j out/jmeterrun.log -e -o out/report_output", jMeterSuitePath)
	}

	private static void run(String command, Path jMeterSuitePath) {
		def process = command.execute(
				(String[]) null,
				jMeterSuitePath.toFile())
		process.waitFor()

		if (process.exitValue() != 0) {
			try {
				log.error("${process.errorStream.text}")
				log.error("${process.text}")
			} finally {
				throw new RuntimeException("error executing jmeter: ${process.exitValue()}")
			}
		}
	}

	private static void cleanOutput(Path jMeterSuitePath) {
		def outputDirectory = jMeterSuitePath.resolve("out").toFile()
		def deleteResult = outputDirectory.deleteDir()

		if (!deleteResult) {
			throw new RuntimeException("error deleting")
		}

	}

}
