package org.stagemonitor.loadtest.runner.systemundertest

import groovy.transform.CompileStatic

import java.nio.file.Path

@CompileStatic
interface SystemUnderTest {

	String getDescription()
	boolean isBaseline()
	String getApplicationName()
	String getStagemonitorPropertyOverrides()
	String getJmxApplicationName()
	Path getJMeterConfigurationPath()
	void start()
	void stop()

}