package org.stagemonitor.loadtest.dashboard

class ChartViewModel {

	private final List<String> labels
	private final List<ChartDataset> datasets
	private final String yAxisLabel

	ChartViewModel(List<String> labels, List<ChartDataset> datasets, String yAxisLabel) {
		this.labels = labels
		this.datasets = datasets
		this.yAxisLabel = yAxisLabel
	}

	String getLabels() {
		return labels.toJson()
	}

	String getDatasets() {
		return datasets.toJson()
	}

	String getYAxisLabel() {
		return yAxisLabel
	}

}
