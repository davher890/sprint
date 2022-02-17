package com.backend.sprint.utils;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.backend.sprint.model.dao.AthleteDao;
import com.backend.sprint.model.dao.HistoricDao;

public abstract class AthleteUtils {

	public static Date getLastRegisterDate(AthleteDao dao) {
		List<HistoricDao> registerHistoric = dao.getHistoric().stream()
				.filter(h -> h.getDate().before(new Date()) && h.getType().equals(HistoricType.REGISTER.name()))
				.sorted(Comparator.comparing(HistoricDao::getDate)).collect(Collectors.toList());

		if (registerHistoric.size() > 0) {
			return registerHistoric.get(0).getDate();
		} else {
			return null;
		}
	}

	public static Date getLastUnregisterDate(AthleteDao dao) {
		List<HistoricDao> unregisterHistoric = dao.getHistoric().stream()
				.filter(h -> h.getDate().before(new Date()) && h.getType().equals(HistoricType.UNREGISTER.name()))
				.sorted(Comparator.comparing(HistoricDao::getDate)).collect(Collectors.toList());

		if (unregisterHistoric.size() > 0) {
			return unregisterHistoric.get(0).getDate();
		} else {
			return null;
		}
	}
}
