package com.backend.sprint.service.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.backend.sprint.model.dto.NameDataDto;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class DatabaseService {

	DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
	DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
	static Map<String, String> registarDateMap = new HashMap<>();
	static {
		registarDateMap = new HashMap<>();
		registarDateMap.put("ene", "01");
		registarDateMap.put("feb", "02");
		registarDateMap.put("mar", "03");
		registarDateMap.put("abr", "04");
		registarDateMap.put("may", "05");
		registarDateMap.put("jun", "06");
		registarDateMap.put("jul", "07");
		registarDateMap.put("ago", "08");
		registarDateMap.put("sep", "09");
		registarDateMap.put("sept", "09");
		registarDateMap.put("oct", "10");
		registarDateMap.put("nov", "11");
		registarDateMap.put("dic", "12");
	}

	CSVReader readFile(String fileName) throws IOException {
		String csvFile = fileName;
		File file = new File(csvFile);
		CSVParser parser = new CSVParserBuilder().withSeparator(',').withIgnoreQuotations(false).build();
		CSVReader csvReader = new CSVReaderBuilder(Files.newBufferedReader(file.toPath())).withSkipLines(1)
				.withCSVParser(parser).build();
		return csvReader;
	}

	public NameDataDto getNameData(String[] line, int position) {
		NameDataDto name = new NameDataDto();
		if (line[position] != null && !line[position].trim().isEmpty()) {
			String nameCell = line[position];
			if (nameCell.contains(",")) {
				List<String> splitNameCell = Arrays.asList(nameCell.split(","));
				name.setName(splitNameCell.get(1));
				if (splitNameCell.get(0).split(" ").length > 1) {
					List<String> splitSurname = Arrays.asList(splitNameCell.get(0).split(" "));
					name.setFirstSurname(splitSurname.get(0));
					name.setSecondSurname(
							splitSurname.subList(1, splitSurname.size()).stream().reduce((identity, accumulator) -> {
								return identity.trim() + " " + accumulator.trim();
							}).orElse(null));
				}
			} else {
				List<String> split = Arrays.asList(nameCell.split(" "));
				name.setName(split.get(0));
				if (split.size() > 1) {
					name.setFirstSurname(split.get(1));
					if (split.get(0).split(" ").length > 1) {
						name.setSecondSurname(
								split.subList(2, split.size()).stream().reduce((identity, accumulator) -> {
									return identity.trim() + " " + accumulator.trim();
								}).orElse(null));
					}
				}
			}
		}
		return name;
	}
}
