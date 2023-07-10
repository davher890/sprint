package com.backend.sprint.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
import com.backend.sprint.model.dto.ReceiptDto;
import com.backend.sprint.repository.ReceiptAthleteRepository;
import com.backend.sprint.repository.ReceiptRepository;
import com.backend.sprint.repository.SportSchoolRepository;

@Service
@Transactional
public class ReceiptService {

	@Autowired
	private ReceiptRepository repository;

	@Autowired
	private ReceiptAthleteRepository receiptAthleteRepository;

	@Autowired
	private SportSchoolRepository sportSchoolRepository;

	public Page<ReceiptDto> findPagintation(Specification<ReceiptDao> specification, Pageable pageable, String sort,
			Direction sortDirection) {
		Page<ReceiptDao> daoPage = repository.findAll(specification,
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortDirection, sort)));
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<ReceiptDto> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).filter(d -> d.getName() != null)
				.map(this::convertToDto).sorted(Comparator.comparing(ReceiptDto::getName)).collect(Collectors.toList());
	}

	public ReceiptDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public void deleteByAthleteIds(long id, List<Long> athleteIds) {
		receiptAthleteRepository.deleteByAthleteIds(id, athleteIds);
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
		dto.setEnrollmentFee(repository.getSumEnrollmentFee(dto.getId()));
		dto.setMembershipFee(repository.getSumMembershipFee(dto.getId()));
		dto.setMonthlyFee(repository.getSumMonthlyFee(dto.getId()));
		dto.setLicenseFee(repository.getSumLicenseFee(dto.getId()));
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

}
