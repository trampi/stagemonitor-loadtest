package org.stagemonitor.loadtest.dashboard

import groovy.transform.CompileStatic

@CompileStatic
class Colors {

	private static final String blue = "#2D5F73"
	private static final String yellow = "#F2D1B3"
	private static final String red = "#F28C8C"
	private static final List<String> colors = [
			red,
			yellow,
			blue
	]
	private static final Map<String, String> keyToColor = [
			"full"      : red,
			"production": yellow,
			"disabled"  : blue
	]
	private static int i = 0

	public static String get(String key) {
		if (!keyToColor.containsKey(key)) {
			def color = colors.get(i++ % colors.size())
			keyToColor[key] = color
		}
		return keyToColor[key]
	}
}
