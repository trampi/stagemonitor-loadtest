package org.stagemonitor.loadtest

import com.google.gson.Gson

class HistoryDao {

	static List<StagemonitorVersionData> getHistory() {
		File file = getHistoryFile()
		if (!file.exists()) {
			return []
		} else {
			return new Gson().fromJson(file.text, StagemonitorVersionData[].class).toList()
		}
	}

	private static File getHistoryFile() {
		new File("history.json")
	}

	static writeHistory(List<StagemonitorVersionData> history) {
		getHistoryFile().write(new Gson().toJson(history))
	}

}
