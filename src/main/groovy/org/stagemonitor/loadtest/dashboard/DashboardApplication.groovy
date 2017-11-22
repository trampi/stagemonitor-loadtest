package org.stagemonitor.loadtest.dashboard

import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import org.codehaus.groovy.runtime.InvokerHelper
import org.stagemonitor.loadtest.HistoryDao
import org.stagemonitor.loadtest.StagemonitorVersionData

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class DashboardApplication {

	public static final dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

	static void main(String[] args) {
		render(HistoryDao.getHistory())
	}

	static void render(List<StagemonitorVersionData> history) {

		DashboardViewModel dashboardViewModel = buildViewModel(history)

		def reportDirectory = new File("report")
		if (!reportDirectory.exists()) {
			reportDirectory.mkdir()
		}

		new File("report/index.html").withWriter { writer ->
			getTemplate().process([
					dashboard: dashboardViewModel
			], writer)
		}

		copyFromResources("report/bootstrap.min.css")
		copyFromResources("report/Chart.bundle.min.js")
		copyFromResources("report/jquery-3.2.1.min.js")

	}

	private static DashboardViewModel buildViewModel(List<StagemonitorVersionData> versionData) {
		def versionDataViewModels = versionData.collect {
			def model = new StagemonitorVersionDataViewModel()
			InvokerHelper.setProperties(model, it.properties)
			model.color = Colors.get(it.loadTestProfile)
			return model
		}

		def orderByDate = { Map.Entry<String, List<StagemonitorVersionDataViewModel>> a, Map.Entry<String, List<StagemonitorVersionDataViewModel>> b
			-> a.value[0].when <=> b.value[0].when
		}

		def idToStagemonitorVersionDataTuple = versionDataViewModels
				.sort { it.loadTestProfile }
				.groupBy { it.runId }
				.sort orderByDate

		Map<String, List<StagemonitorVersionData>> loadTestProfileToVersionData = versionDataViewModels
				.groupBy { it.loadTestProfile }
				.sort orderByDate

		def charts = [
				buildChart(loadTestProfileToVersionData, "Processor time (ms)") {
					StagemonitorVersionDataViewModel model -> TimeUnit.NANOSECONDS.toMillis(model.loadtestDifference.processCpuTimeNanoSeconds)
				},
				buildChart(loadTestProfileToVersionData, "Used Memory after warmup megabytes (heap)") {
					StagemonitorVersionDataViewModel model -> model.beforeLoadtest.usedMemoryHeapKiloBytes / 1024
				},
				buildChart(loadTestProfileToVersionData, "Used Memory after warmup megabytes (non heap)") {
					StagemonitorVersionDataViewModel model -> model.beforeLoadtest.usedMemoryNonHeapKiloBytes / 1024
				},
				buildChart(loadTestProfileToVersionData, "Mark&Sweep collection count") {
					StagemonitorVersionDataViewModel model -> model.loadtestDifference.markSweepCollectionCount
				},
				buildChart(loadTestProfileToVersionData, "Mark&Sweep collection time (ms)") {
					StagemonitorVersionDataViewModel model -> model.loadtestDifference.markSweepCollectionTimeInMilliseconds
				},
				buildChart(loadTestProfileToVersionData, "Scavenge collection count") {
					StagemonitorVersionDataViewModel model -> model.loadtestDifference.scavengeCollectionCount
				},
				buildChart(loadTestProfileToVersionData, "Scavenge collection time (ms)") {
					StagemonitorVersionDataViewModel model -> model.loadtestDifference.scavengeCollectionTimeInMilliseconds
				}
		]

		return new DashboardViewModel(
				runs: idToStagemonitorVersionDataTuple.values().toList(),
				chartViewModels: charts,
				lastGenerated: dateTimeFormatter.format(LocalDateTime.now())
		)
	}

	private
	static ChartViewModel buildChart(Map<String, List<StagemonitorVersionDataViewModel>> loadTestProfileToVersionData, String yAxisLabel, Closure<Double> extractor) {
		def datasets = loadTestProfileToVersionData.collect { key, value ->
			def chart = new ChartDataset()
			chart.data = value.collect(extractor)
			chart.label = value.first().description
			chart.isBaseline = value.first().isBaseline
			chart.setColor(Colors.get(value.first().loadTestProfile))
			return chart
		}

		def labels = loadTestProfileToVersionData.values().first().collect {
			it.when.atZone(ZoneId.systemDefault()).format(dateTimeFormatter)
		}
		return new ChartViewModel(labels, datasets, yAxisLabel)
	}

	private static Template getTemplate() {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_23)
		cfg.setClassForTemplateLoading(this.class, "/")
		cfg.setDefaultEncoding("UTF-8")
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER)
		cfg.setLogTemplateExceptions(false)

		cfg.getTemplate("template.ftl")
	}

	private static void copyFromResources(String file) {
		new File(file).withDataOutputStream { writer ->
			writer.write(DashboardApplication.class.getResourceAsStream("/" + file).bytes)
		}
	}

}
