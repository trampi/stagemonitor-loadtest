package org.stagemonitor.loadtest.runner.systemundertest

import groovy.transform.CompileStatic

import java.nio.file.Path

@CompileStatic
abstract class AbstractSystemUnderTest implements SystemUnderTest {

	private final String description
	private final boolean isBaseline
	private final String applicationName
	private final String stagemonitorPropertyOverrides
	private final Path jMeterConfigurationPath
	private final String jmxApplicationName

	AbstractSystemUnderTest(String description, boolean isBaseline, String applicationName,
							String stagemonitorPropertyOverrides, Path jMeterConfigurationPath,
							String jmxApplicationName) {
		this.description = description
		this.isBaseline = isBaseline
		this.applicationName = applicationName
		this.stagemonitorPropertyOverrides = stagemonitorPropertyOverrides
		this.jMeterConfigurationPath = jMeterConfigurationPath
		this.jmxApplicationName = jmxApplicationName
	}

	String getDescription() {
		return description
	}

	boolean isBaseline() {
		return isBaseline
	}

	String getApplicationName() {
		return applicationName
	}

	String getStagemonitorPropertyOverrides() {
		return stagemonitorPropertyOverrides
	}

	Path getJMeterConfigurationPath() {
		return jMeterConfigurationPath
	}

	String getJmxApplicationName() {
		return jmxApplicationName
	}
}
