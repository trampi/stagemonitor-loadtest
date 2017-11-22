package org.stagemonitor.loadtest.runner.systemundertest

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

@CompileStatic
@Slf4j
class Petclinic extends AbstractSystemUnderTest {

	private Process process
	private static final Path petclinicPath = Paths.get(System.getProperty("petclinic.path"))

	Petclinic(String stagemonitorPropertyOverrides, String description, boolean isBaseline) {
		super(description,
				isBaseline,
				"spring-petclinic",
				stagemonitorPropertyOverrides,
				petclinicPath.resolve("src/test/jmeter"),
				"org.springframework.samples.petclinic.PetClinicApplication")

	}

	private boolean isRunning() {
		try {
			def connection = "http://localhost:8080/".toURL().openConnection()
			connection.setConnectTimeout(500)
			connection.setReadTimeout(500)
			connection.connect()
			return true
		} catch (ignored) {
			return false
		}
	}

	void start() {
		if (isRunning()) {
			throw new RuntimeException("petclinic seems to be running already")
		}

		log.info "starting application with profile ${stagemonitorPropertyOverrides}"
		def commandline = /mvn -e spring-boot:run "-Drun.jvmArguments=-Dstagemonitor.property.overrides=stagemonitor-${stagemonitorPropertyOverrides}.properties"/
		log.info "running petclinic: ${commandline}"
		process = commandline.execute((List) null, petclinicPath.toFile())
		process.waitFor(30, TimeUnit.SECONDS)
		if (!process.alive) {
			try {
				log.error("${process.errorStream.text}")
				log.error("${process.text}")
			} finally {
				throw new RuntimeException("system under test has quit prematurely, exit code ${process.exitValue()}")
			}
		}
		log.info "started application with profile ${stagemonitorPropertyOverrides}"
	}

	void stop() {
		log.info "stopping application with profile ${stagemonitorPropertyOverrides}"
		process.destroy()
		process.waitFor(5, TimeUnit.SECONDS)
		log.info "stopped application with profile ${stagemonitorPropertyOverrides}"
	}

}
