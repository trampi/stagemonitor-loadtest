package org.stagemonitor.loadtest

import org.stagemonitor.loadtest.dashboard.DashboardApplication
import org.stagemonitor.loadtest.runner.RunnerApplication

class Main {

	public static void main(String[] args) {
		RunnerApplication.runLoadtest()
		DashboardApplication.render(HistoryDao.getHistory())
	}

}
