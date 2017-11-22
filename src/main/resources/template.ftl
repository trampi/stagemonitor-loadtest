<#include "point-details.ftl" />
<#-- @ftlvariable name="dashboard" type="org.stagemonitor.loadtest.dashboard.DashboardViewModel" -->
<!doctype html>

<html lang="en">
<head>
	<meta charset="utf-8">

	<title>stagemonitor Health Dashboard</title>
	<meta name="description" content="stagemonitor Health Dashboard">
	<link rel="stylesheet" href="bootstrap.min.css">
	<link rel="stylesheet" href="styles.css?v=1">
</head>

<body>

<div class="container" id="chart-container">

	<div class="col-12">
		<h1>Performance history <small class="text-muted">(last generated: ${dashboard.lastGenerated})</small></h1>

		<p>This dashboard shows the performance overhead of stagemonitor in spring-petclinic.</p>
	</div>


	<#list dashboard.chartViewModels as chart>
	<div class="row chart-container" style="height: 500px" data-labels='${chart.labels}' data-datasets='${chart.datasets}' data-y-axis-label='${chart.YAxisLabel}'>
		<div class="col-12">
			<h2>${chart.YAxisLabel}</h2>
		</div>
		<div class="col-6 line-container">
			<canvas width="400" height="150"></canvas>
		</div>
		<div class="col-6 boxplot-container">
		</div>
	</div>
	<#if chart_has_next>
		<hr />
	</#if>
	</#list>
</div>

<#--
<div class="container-fluid">

	<#list dashboard.runs as runList>
		<div class="row stagemonitor-version-data stagemonitor-version-data-${runList?index}" style="display: none">
			<div class="col-lg-12">
				<small class="text-muted float-right text-right">(run id ${runList?first.runId})</small>
				<p>
					The following tables show statistics of a JVM before and after a test
				</p>
			</div>
			<#list runList as run>
				<div class="col-lg-${12 / runList?size}">
					<@renderJmx run />
				</div>
			</#list>
		</div>
	</#list>

</div>
-->

<script src="jquery-3.2.1.min.js"></script>
<script src="Chart.bundle.min.js"></script>
<script type="text/javascript" src="https://cdn.plot.ly/plotly-latest.js"></script>
<script type="text/javascript">

	$(".chart-container").each(function() {
		var yAxisLabel = $(this).data("yAxisLabel");
		var datasets = $(this).data("datasets");
		var labels = $(this).data("labels");
		var chartContext = $("canvas", this);
		var uid = uuid();
		$(".boxplot-container", this).attr('id', uid);

		new Chart(chartContext, {
			type: 'line',
			data: {
				labels: labels,
				datasets: datasets
			},
			options: {
				maintainAspectRatio: false,
				responsive: true,
				tooltips: {
					mode: 'index',
					intersect: false,
					callbacks: {
						label: function(tooltipItem, data) {
							var dataset = data.datasets[tooltipItem.datasetIndex];
							if (dataset.isBaseline) {
								return "baseline";
							} else {
								var baseline = findBaseline(data.datasets);
								var percentDifference = (dataset.data[tooltipItem.index] / baseline.data[tooltipItem.index]) - 1;
								return Math.round((percentDifference) * 1000) / 10 + "%"
							}
						}
					}
				},
				hover: {
					mode: 'nearest',
					intersect: true
				},
				elements: {
					line: {
						tension: 0
					}
				},
				onClick: function(e, chartClick) {
					if (chartClick[0]) {
						var clickedDataPointIndex = chartClick[0]._index;
						$(".stagemonitor-version-data").hide();
						$(".stagemonitor-version-data-" + clickedDataPointIndex).show();
					}
				},
				scales: {
					xAxes: [{
						display: true,
						scaleLabel: {
							display: false,
							labelString: 'Date'
						},
						ticks: {
							maxRotation: 60,
							minRotation: 60
						}
					}],
					yAxes: [{
						display: true,
						scaleLabel: {
							display: true,
							labelString: yAxisLabel
						}
					}]
				}
			}
		});

		var boxplotData = [];
		for (var i = 0; i < datasets.length; i++) {
			var dataset = datasets[i];
			boxplotData[i] = {
				y: dataset.data,
				type: 'box',
				name: dataset.label,
				marker: {
					color: dataset.backgroundColor
				}
			}
		}
		var layout = {
			showlegend: false
		};
		Plotly.newPlot(uid, boxplotData, layout);
	});


	function findBaseline(datasets) {
		for (var i = 0; i < datasets.length; i++) {
			if (datasets[i].isBaseline) {
				return datasets[i];
			}
		}
	}

	function uuid() {
		var dt = new Date().getTime();
		var uid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
			var r = (dt + Math.random()*16)%16 | 0;
			dt = Math.floor(dt/16);
			return (c=='x' ? r :(r&0x3|0x8)).toString(16);
		});
		return uid;
	}
</script>

</body>
</html>
