package org.stagemonitor.loadtest.runner

import org.stagemonitor.loadtest.runner.systemundertest.Petclinic
import org.stagemonitor.loadtest.runner.systemundertest.SystemUnderTest

class LoadTestProfiles {

	static List<SystemUnderTest> getProfiles() {
		return [
				new Petclinic("full", "profiling and widget enabled", false),
				new Petclinic("production", "profiling and widget disabled", false),
				new Petclinic("disabled", "completely disabled", true)
		]
	}

}
