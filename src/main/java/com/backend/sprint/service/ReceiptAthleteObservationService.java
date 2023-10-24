package com.backend.sprint.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.ReceiptAthleteObservationDao;
import com.backend.sprint.model.dto.ReceiptAthleteObservationDto;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.repository.ReceiptAthleteObservationRepository;
import com.backend.sprint.repository.ReceiptAthleteRepository;
import com.backend.sprint.repository.UserRepository;

@Service
@Transactional
public class ReceiptAthleteObservationService {

	@Autowired
	private ReceiptAthleteObservationRepository repository;

	@Autowired
	private ReceiptAthleteRepository receiptAthleteRepository;

	@Autowired
	private UserRepository userRepository;

	public Page<ReceiptAthleteObservationDto> findPagintation(Specification<ReceiptAthleteObservationDao> specification,
			Pageable pageable) {
		Page<ReceiptAthleteObservationDao> daoPage = repository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<ReceiptAthleteObservationDto> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public ReceiptAthleteObservationDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public List<ReceiptAthleteObservationDto> findReceiptAthleteObservations(long id) {
		return repository.findReceiptAthleteObservations(id).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Transactional
	public ReceiptAthleteObservationDto save(ReceiptAthleteObservationDto dto) throws DataIntegrityViolationException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() != null) {
			UserDto user = (UserDto) auth.getPrincipal();
			dto.setUpdatedById(user.getId());
		}

		return convertToDto(repository.save(convertToDao(dto)));
	}

	private ReceiptAthleteObservationDto convertToDto(ReceiptAthleteObservationDao dao) {
		if (dao == null) {
			return null;
		}
		ReceiptAthleteObservationDto dto = new ModelMapper().map(dao, ReceiptAthleteObservationDto.class);
		dto.setReceiptAthleteId(dao.getReceiptAthlete().getId());

		if (dao.getUpdatedBy() != null) {
			dto.setUpdatedById(dao.getUpdatedBy().getId());
		}

		return dto;
	}

	private ReceiptAthleteObservationDao convertToDao(ReceiptAthleteObservationDto dto) {
		ReceiptAthleteObservationDao dao = new ModelMapper().map(dto, ReceiptAthleteObservationDao.class);
		if (dto.getReceiptAthleteId() != 0) {
			dao.setReceiptAthlete(receiptAthleteRepository.findById(dto.getReceiptAthleteId()).get());
		}

		if (dto.getUpdatedById() != 0) {
			dao.setUpdatedBy(userRepository.findById(dto.getUpdatedById()).get());
		} else {
			dao.setUpdatedBy(null);
		}

		return dao;
	}

	public ReceiptAthleteObservationDto deleteById(long id) {
		ReceiptAthleteObservationDto dto = this.findById(id);
		dto.setActive(false);
		return this.save(dto);
	}

}
