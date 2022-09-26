package com.backend.sprint.service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
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

import com.backend.sprint.model.dao.ReceiptDao;
import com.backend.sprint.model.dao.RegisteredAthleteDao;
import com.backend.sprint.model.dto.FeeDto;
import com.backend.sprint.model.dto.ReceiptAthleteDto;
import com.backend.sprint.model.dto.ReceiptDto;
import com.backend.sprint.repository.ReceiptAthleteRepository;
import com.backend.sprint.repository.ReceiptRepository;
import com.backend.sprint.repository.RegisteredAthleteRepository;
import com.backend.sprint.repository.SportSchoolRepository;
import com.backend.sprint.utils.AthleteUtils;

@Service
@Transactional
public class ReceiptService {

	@Autowired
	private ReceiptRepository repository;

	@Autowired
	private ReceiptAthleteRepository receiptAthleteRepository;

	@Autowired
	private RegisteredAthleteRepository registeredAthleteRepository;

	@Autowired
	private SportSchoolRepository sportSchoolRepository;

	@Autowired
	private FeeService feeService;

	public Page<ReceiptDto> findPagintation(Specification<ReceiptDao> specification, Pageable pageable, String sort,
			Direction sortDirection) {
		Page<ReceiptDao> daoPage = repository.findAll(specification,
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortDirection, sort)));
		return daoPage.map(dao -> convertToDto(dao));
	}

	public Page<ReceiptAthleteDto> findReceiptAthletes(Specification<RegisteredAthleteDao> specification,
			Pageable pageable, String sort, Direction sortDirection, String collection) {

		Page<RegisteredAthleteDao> daoPage = registeredAthleteRepository.findAll(specification,
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortDirection, sort)));
		return daoPage.map(dao -> convertToDto(dao, collection));
	}

	public List<ReceiptAthleteDto> findReceiptAthletesList(Specification<RegisteredAthleteDao> specification,
			String collection) {

		List<RegisteredAthleteDao> daoPage = registeredAthleteRepository.findAll(specification);
		return daoPage.stream().map(dao -> convertToDto(dao, collection)).collect(Collectors.toList());
	}

	public ReceiptDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public ReceiptDto deleteById(long id) {
		ReceiptDto receipt = this.findById(id);
		receipt.setActive(false);

		receipt = this.save(receipt);

		receiptAthleteRepository.deleteByReceiptId(receipt.getId());

		return receipt;
	}

	public ReceiptDto save(ReceiptDto dto) {
		return convertToDto(repository.save(convertToDao(dto)));
	}

	private ReceiptDto convertToDto(ReceiptDao dao) {
		if (dao == null) {
			return null;
		}
		ReceiptDto dto = new ModelMapper().map(dao, ReceiptDto.class);

		if (dao.getSportSchool() != null) {
			dto.setSportSchoolId(dao.getSportSchool().getId());
			dto.setSportSchoolName(dao.getSportSchool().getName());
		}
		return dto;
	}

	private ReceiptDao convertToDao(ReceiptDto dto) {
		ReceiptDao dao = new ModelMapper().map(dto, ReceiptDao.class);

		if (dto.getSportSchoolId() > 0) {
			dao.setSportSchool(sportSchoolRepository.findById(dto.getSportSchoolId()).get());
		} else {
			dao.setSportSchool(null);
		}
		return dao;
	}

	private ReceiptAthleteDto convertToDto(RegisteredAthleteDao dao, String collection) {
		if (dao == null) {
			return null;
		}
//		AthleteDto athlete = new ModelMapper().map(dao, AthleteDto.class);

		ReceiptAthleteDto receiptAthlete = new ModelMapper().map(dao, ReceiptAthleteDto.class);

		Set<Long> scheduleIds = dao.getScheduleIds() != null && !dao.getScheduleIds().isEmpty() ? Arrays
				.asList(dao.getScheduleIds().split(",")).stream().map(Long::parseLong).collect(Collectors.toSet())
				: null;
		FeeDto fee = feeService.getAthleteFee(dao.getId(), dao.getFamilyCode(), dao.getPaymentType(),
				dao.getSportSchoolId(), dao.isSpecialization(), scheduleIds, receiptAthlete.getCategory(),
				dao.getLicenseType(), dao.isEnrollmentFeePaid(), collection);

		receiptAthlete.setEnrollmentFee(fee.getEnrollmentFee());
		receiptAthlete.setMonthlyFee(fee.getMonthlyFee());
		receiptAthlete.setMembershipFee(fee.getMembershipFee());

		String translatedDays = "";
		String days = receiptAthlete.getDays();
		if (days != null && !days.isEmpty()) {
			translatedDays = Arrays.asList(days.split(",")).stream().sorted(new AthleteUtils.WeekDayComparator())
					.map(day -> AthleteUtils.dayTranslate.get(day)).collect(Collectors.joining(","));
		}
		receiptAthlete.setDays(translatedDays);
		return receiptAthlete;
	}

}
