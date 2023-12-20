package com.backend.sprint.service;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.ReceiptUnregisteredAthleteDao;
import com.backend.sprint.model.dto.ReceiptAthleteDto;
import com.backend.sprint.repository.ReceiptUnregisteredAthleteRepository;
import com.backend.sprint.utils.AthleteUtils;

@Service
@Transactional
public class ReceiptUnregisteredAthleteService {

	@Autowired
	private ReceiptUnregisteredAthleteRepository repository;

	public Page<ReceiptAthleteDto> findPagintation(Specification<ReceiptUnregisteredAthleteDao> specification,
			Pageable pageable, String sort, Direction sortDirection) {
		Page<ReceiptUnregisteredAthleteDao> daoPage = repository.findAll(specification,
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortDirection, sort)));
		return daoPage.map(dao -> convertToDto(dao));
	}

	public Page<ReceiptUnregisteredAthleteDao> findDaoPagintation(
			Specification<ReceiptUnregisteredAthleteDao> specification, Pageable pageable) {
		return repository.findAll(specification, pageable);
	}

	private ReceiptAthleteDto convertToDto(ReceiptUnregisteredAthleteDao dao) {
		if (dao == null) {
			return null;
		}
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		ReceiptAthleteDto dto = modelMapper.map(dao, ReceiptAthleteDto.class);

		dto.setTotalFee(dao.getEnrollmentFee() + dao.getMembershipFee() + dao.getMonthlyFee()
				+ dao.getLicenseNationalFee() + dao.getLicenseTerritorialFee() + dao.getSportSchoolAnnualFee());

		String translatedDays = "";
		String days = dto.getDays();
		if (days != null && !days.isEmpty()) {
			translatedDays = Arrays.asList(days.split(",")).stream().sorted(new AthleteUtils.WeekDayComparator())
					.map(day -> AthleteUtils.dayTranslateSP.get(day.trim())).collect(Collectors.joining(","));
		}
		dto.setDays(translatedDays);

		dto.setReceiptId(dao.getReceiptId());
		dto.setReceiptName(dao.getName());
		dto.setReceiptCreatedAt(dao.getCreatedAt());
		dto.setSeason(dao.getSeason());

		dto.setSportSchoolId(dao.getSportSchoolId());
		dto.setSportSchoolName(dao.getSportSchoolName());

		dto.setGroupId(dao.getGroupId());
		dto.setGroupName(dao.getGroupName());
		dto.setSpecialization(dao.isSpecialization());

		dto.setCode(dao.getCode());
		dto.setAthleteId(dao.getId());
		dto.setName(dao.getName());
		dto.setFirstSurname(dao.getFirstSurname());
		dto.setSecondSurname(dao.getSecondSurname());

		dto.setPaymentType(dao.getPaymentType());

		dto.setFeeTypeId(dao.getFeeTypeId());
		dto.setFeeTypeName(dao.getFeeTypeName());

		return dto;
	}

}
