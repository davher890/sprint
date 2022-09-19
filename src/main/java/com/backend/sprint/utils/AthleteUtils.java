package com.backend.sprint.utils;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.backend.sprint.model.dao.AthleteDao;
import com.backend.sprint.model.dao.HistoricDao;
import com.backend.sprint.model.dto.HistoricDto;

public abstract class AthleteUtils {

	public static Date getLastRegisterDate(Set<HistoricDto> set) {
		List<HistoricDto> registerHistoric = set.stream()
				.filter(h -> h.getDate().before(new Date()) && h.getType().equals(HistoricType.REGISTER.name()))
				.sorted(Comparator.comparing(HistoricDto::getDate)).collect(Collectors.toList());

		if (registerHistoric.size() > 0) {
			return registerHistoric.get(0).getDate();
		} else {
			return null;
		}
	}

	public static Date getLastUnregisterDate(Set<HistoricDto> historic) {
		List<HistoricDto> unregisterHistoric = historic.stream()
				.filter(h -> h.getDate().before(new Date()) && h.getType().equals(HistoricType.UNREGISTER.name()))
				.sorted(Comparator.comparing(HistoricDto::getDate)).collect(Collectors.toList());

		if (unregisterHistoric.size() > 0) {
			return unregisterHistoric.get(0).getDate();
		} else {
			return null;
		}
	}

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

	public static Category calculateCategory(int year) {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		Category category = null;
		switch (currentYear - year) {
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			category = Category.CHUPETIN;
			break;
		case 6:
		case 7:
			category = Category.SUB8;
			break;
		case 8:
		case 9:
			category = Category.SUB10;
			break;
		case 10:
		case 11:
			category = Category.SUB12;
			break;
		case 12:
		case 13:
			category = Category.SUB14;
			break;
		case 14:
		case 15:
			category = Category.SUB16;
			break;
		case 16:
		case 17:
			category = Category.SUB18;
			break;
		case 18:
		case 19:
			category = Category.SUB20;
			break;
		case 20:
		case 21:
		case 22:
			category = Category.SUB23;
			break;
		default:
			if (currentYear - year > 22 && currentYear - year <= 35) {
				category = Category.SENIOR;
			} else {
				category = Category.MASTER;
			}
			break;
		}
		return category;
	}

	public static final Map<String, String> dayTranslate = new HashMap<String, String>() {

		private static final long serialVersionUID = 1L;

		{
			put("MONDAY", "Lunes");
			put("TUESDAY", "Martes");
			put("WEDNESDAY", "Miercoles");
			put("THURSDAY", "Jueves");
			put("FRIDAY", "Viernes");
			put("SATURDAY", "Sabado");
			put("SUNDAY", "Domingo");
		}
	};

	static Map<String, Integer> dayNumberMap = Stream
			.of(new Object[][] { { "monday", 1 }, { "tuesday", 2 }, { "wednesday", 3 }, { "thursday", 4 },
					{ "friday", 5 }, { "saturday", 6 }, { "sunday", 7 }, })
			.collect(Collectors.toMap(data -> (String) data[0], data -> (Integer) data[1]));

	public static class WeekDayComparator implements Comparator<String> {
		public int compare(String c1, String c2) {
			if (c1 == null || c2 == null) {
				return 0;
			}
			return dayNumberMap.get(c1.toLowerCase()).compareTo(dayNumberMap.get(c2.toLowerCase()));
		}
	}
}
