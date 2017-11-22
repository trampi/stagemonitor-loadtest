package org.stagemonitor.loadtest.dashboard

import groovy.transform.Canonical
import groovy.transform.CompileStatic

@CompileStatic
@Canonical
class DashboardViewModel {

	List<List<StagemonitorVersionDataViewModel>> runs
	List<ChartViewModel> chartViewModels
	String lastGenerated

}
