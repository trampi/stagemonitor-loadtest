<#macro renderJmx version>
<#-- @ftlvariable name="version" type="org.stagemonitor.loadtest.dashboard.StagemonitorVersionDataViewModel" -->
<h3>${version.description} <span class="badge" style="background-color: ${version.color}">&nbsp;</span></h3>
<table class="table">
	<tbody>
	<tr>
		<th>Process CPU time (nanoseconds)</th>
		<td class="text-right">${version.loadtestDifference.processCpuTimeNanoSeconds}</td>
	</tr>
	<tr>
		<th>Used heap memory after warm up (kilo bytes)</th>
		<td class="text-right">${version.beforeLoadtest.usedMemoryHeapKiloBytes}</td>
	</tr>
	<tr>
		<th>Used non-heap memory after warm up (kilo bytes)</th>
		<td class="text-right">${version.beforeLoadtest.usedMemoryNonHeapKiloBytes}</td>
	</tr>
	<tr>
		<th>Mark & Sweep collection count</th>
		<td class="text-right">${version.loadtestDifference.markSweepCollectionCount}</td>
	</tr>
	<tr>
		<th>Mark & Sweep collection time (milliseconds)</th>
		<td class="text-right">${version.loadtestDifference.markSweepCollectionTimeInMilliseconds}</td>
	</tr>
	<tr>
		<th>PS Scavenge collection count</th>
		<td class="text-right">${version.loadtestDifference.scavengeCollectionCount}</td>
	</tr>
	<tr>
		<th>PS Scavenge collection time (milliseconds)</th>
		<td class="text-right">${version.loadtestDifference.scavengeCollectionTimeInMilliseconds}</td>
	</tr>
	</tbody>
</table>

	<#list version.jMeterStatistics as jMeter>

	<div class="card">
		<div class="card-header" role="tab">
			<h5 class="mb-0">
				<a data-toggle="collapse" href="#${jMeter.uuid}">
				${jMeter.label} <small class="text-muted">(took ${jMeter.durationInSeconds} seconds)</small>
				</a>
			</h5>
		</div>

		<div id="${jMeter.uuid}"
			 class="collapse <#if jMeter.label == 'All aggregated'>show</#if>"
			 role="tabpanel">
			<div class="card-body">
				<table class="table">
					<tbody>
					<tr>
						<th>Requests per second</th>
						<td class="text-right">${jMeter.throughputPerSecond}</td>
					</tr>
					<tr>
						<th>Samples / Errors / Error rate</th>
						<td class="text-right">${jMeter.sampleCount} / ${jMeter.errorCount} / ${jMeter.errorRate}</td>
					</tr>
					<tr>
						<th>Average response time (ms)</th>
						<td class="text-right">${jMeter.responseTimeAverage}</td>
					</tr>
					<tr>
						<th>Response time min</th>
						<td class="text-right">${jMeter.responseTimeMin}</td>
					</tr>
					<tr>
						<th>Response time max</th>
						<td class="text-right">${jMeter.responseTimeMax}</td>
					</tr>
					<tr>
						<th>Response time 90th percentile</th>
						<td class="text-right">${jMeter.responseTime90thPercentile}</td>
					</tr>
					<tr>
						<th>Response time 95th percentile</th>
						<td class="text-right">${jMeter.responseTime95thPercentile}</td>
					</tr>
					<tr>
						<th>Response time 99th percentile</th>
						<td class="text-right">${jMeter.responseTime99thPercentile}</td>
					</tr>
					<tr>
						<th>Kilobytes received / sent (total)</th>
						<td class="text-right">${jMeter.kilobytesReceived} / ${jMeter.kilobytesSent}</td>
					</tr>
					<tr>
						<th>Kilobytes per second received / sent</th>
						<td class="text-right">${jMeter.kilobytesReceivedPerSecond} / ${jMeter.kilobytesSentPerSecond}</td>
					</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>

	</#list>
</#macro>