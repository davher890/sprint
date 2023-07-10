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

import com.backend.sprint.model.dao.RegisteredAthleteDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.repository.RegisteredAthleteRepository;
import com.backend.sprint.utils.AthleteUtils;

@Service
@Transactional
public class RegisteredAthleteService {

	@Autowired
	private RegisteredAthleteRepository repository;

	public Page<AthleteDto> findPagintation(Specification<RegisteredAthleteDao> specification, Pageable pageable,
			String sort, Direction sortDirection) {
		Page<RegisteredAthleteDao> daoPage = repository.findAll(specification,
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortDirection, sort)));
		return daoPage.map(dao -> convertToDto(dao));
	}

	public Page<RegisteredAthleteDao> findDaoPagintation(Specification<RegisteredAthleteDao> specification,
			Pageable pageable) {
		return repository.findAll(specification, pageable);
	}

	public List<AthleteDto> findBySportSchoolIdAndLastRegisterDate(long sportSchoolId, Date lastRegisterDate,
			String paymentType) {
		List<String> paymentTypes = new ArrayList<String>();
		if (paymentType.equals("DOM")) {
			paymentTypes.add(paymentType);
		} else {
			paymentTypes.add("TRANSF");
			paymentTypes.add("MET");
		}
		return repository.findBySportSchoolIdAndLastRegisterDate(sportSchoolId, lastRegisterDate, paymentTypes).stream()
				.map(this::convertToDto).collect(Collectors.toList());
	}

	public List<AthleteDto> findByGroupId(long id) {
		return repository.findByGroupId(id).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public List<AthleteDto> findBySportSchoolId(long id) {
		return repository.findBySportSchoolId(id).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public List<AthleteDto> findByFamilyId(long id) {
		return repository.findByFamilyId(id).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	private AthleteDto convertToDto(RegisteredAthleteDao dao) {
		if (dao == null) {
			return null;
		}
		AthleteDto dto = new ModelMapper().map(dao, AthleteDto.class);

		String translatedDays = "";
		String days = dto.getDays();
		if (days != null && !days.isEmpty()) {
			translatedDays = Arrays.asList(days.split(",")).stream().sorted(new AthleteUtils.WeekDayComparator())
					.map(day -> AthleteUtils.dayTranslate.get(day.trim())).collect(Collectors.joining(","));
		}
		dto.setDays(translatedDays);
		return dto;
	}

}
