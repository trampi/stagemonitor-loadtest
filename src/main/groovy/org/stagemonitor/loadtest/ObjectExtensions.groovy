package org.stagemonitor.loadtest

import groovy.json.JsonBuilder
import groovy.transform.CompileStatic

@CompileStatic
class ObjectExtensions {

	static String toJson(final Object self) {
		new JsonBuilder(self).toString()
	}

}
