package org.stagemonitor.loadtest.dashboard

import groovy.transform.Canonical

@Canonical
class ChartDataset {
	String label
	String backgroundColor
	String borderColor
	List<Double> data
	final boolean fill = false
	final int pointHitRadius = 50
	boolean isBaseline

	public void setColor(String color) {
		backgroundColor = color
		borderColor = color
	}
}