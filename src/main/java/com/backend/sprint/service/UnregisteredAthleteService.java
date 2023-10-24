package com.backend.sprint.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.UnregisteredAthleteDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.repository.UnregisteredAthleteRepository;
import com.backend.sprint.utils.AthleteUtils;

@Service
@Transactional
public class UnregisteredAthleteService {

	@Autowired
	private UnregisteredAthleteRepository repository;

	public Page<AthleteDto> findPagintation(Specification<UnregisteredAthleteDao> specification, Pageable pageable,
			String sort, Direction sortDirection) {
		Page<UnregisteredAthleteDao> daoPage = repository.findAll(specification,
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortDirection, sort)));
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<AthleteDto> findBySportSchoolIdAndLastUnregisterDate(long sportSchoolId, Date lastUnregisterDate,
			String paymentType, List<String> feeTypes) {
		List<String> paymentTypes = new ArrayList<String>();
		if (paymentType.equals("DOM")) {
			paymentTypes.add(paymentType);
		} else {
			paymentTypes.add("TRANSF");
			paymentTypes.add("MET");
		}
		return repository
				.findBySportSchoolIdAndLastUnregisterDate(sportSchoolId, lastUnregisterDate, paymentTypes, feeTypes)
				.stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public List<AthleteDto> findByLastUnregisterDate(Date lastUnregisterDate) {
		return repository.findByLastUnregisterDate(lastUnregisterDate).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public Page<UnregisteredAthleteDao> findDaoPagintation(Specification<UnregisteredAthleteDao> specification,
			Pageable pageable) {
		return repository.findAll(specification, pageable);
	}

	private AthleteDto convertToDto(UnregisteredAthleteDao dao) {
		if (dao == null) {
			return null;
		}
		AthleteDto dto = new ModelMapper().map(dao, AthleteDto.class);

		String translatedDays = "";
		String days = dto.getDays();
		if (days != null && !days.isEmpty()) {
			translatedDays = Arrays.asList(days.split(",")).stream().sorted(new AthleteUtils.WeekDayComparator())
					.map(day -> AthleteUtils.dayTranslateSP.get(day.trim())).collect(Collectors.joining(","));
		}
		dto.setDays(translatedDays);
		return dto;
	}

	public List<AthleteDto> findBySportSchoolIdAndLicense(long sportSchoolId, String paymentType,
			List<String> licenseTypes) {
		List<String> paymentTypes = new ArrayList<String>();
		if (paymentType.equals("DOM")) {
			paymentTypes.add(paymentType);
		} else {
			paymentTypes.add("TRANSF");
			paymentTypes.add("MET");
		}
		return repository.findBySportSchoolIdAndLicense(sportSchoolId, paymentTypes, licenseTypes).stream()
				.map(this::convertToDto).collect(Collectors.toList());
	}

}
