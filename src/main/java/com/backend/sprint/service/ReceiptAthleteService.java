package com.backend.sprint.service;

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

import com.backend.sprint.model.dao.AthleteDao;
import com.backend.sprint.model.dao.FamilyDao;
import com.backend.sprint.model.dao.GroupDao;
import com.backend.sprint.model.dao.ReceiptAthleteDao;
import com.backend.sprint.model.dao.ReceiptDao;
import com.backend.sprint.model.dao.SportSchoolDao;
import com.backend.sprint.model.dto.ReceiptAthleteDto;
import com.backend.sprint.repository.AthleteRepository;
import com.backend.sprint.repository.ReceiptAthleteRepository;
import com.backend.sprint.repository.ReceiptRepository;

@Service
@Transactional
public class ReceiptAthleteService {

	@Autowired
	private ReceiptAthleteRepository repository;

	@Autowired
	private ReceiptRepository receiptRepository;

	@Autowired
	private AthleteRepository athleteRepository;

	public ReceiptAthleteDto save(ReceiptAthleteDto dto) {

		return convertToDto(repository.save(convertToDao(dto)));
	}

	public Page<ReceiptAthleteDto> findPagintation(Specification<ReceiptAthleteDao> specification, Pageable pageable,
			String sort, Direction sortDirection) {
		Page<ReceiptAthleteDao> daoPage = repository.findAll(specification, PageRequest.of(pageable.getPageNumber(),
				pageable.getPageSize(), Sort.by(sortDirection, "athlete." + sort)));
		return daoPage.map(dao -> convertToDto(dao));
	}

	public ReceiptAthleteDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public Set<ReceiptAthleteDto> findByReceiptId(long receiptId) {
		return repository.findByReceiptId(receiptId).stream().map(this::convertToDto).collect(Collectors.toSet());
	}

	public Set<ReceiptAthleteDto> findByAthleteIdAndReceiptStatus(long athleteId, String status) {
		return repository.findByAthleteIdAndReceiptStatus(athleteId, status).stream().map(this::convertToDto)
				.collect(Collectors.toSet());
	}

	public ReceiptAthleteDto findByAthleteIdAndReceiptId(long athleteId, long receiptId) {
		return convertToDto(repository.findByAthleteIdAndReceiptId(athleteId, receiptId));
	}

	public List<ReceiptAthleteDao> findDaoByReceiptId(long receiptId) {
		return repository.findByReceiptId(receiptId);
	}

	public void validateReceiptId(long receiptId) {
		repository.validateReceiptId(receiptId);
		repository.athleteMembershipFeePaid(receiptId);
		repository.ahtleteEnrollmentFeePaid(receiptId);
	}

	public void deleteByReceiptId(long receiptId) {
		repository.deleteByReceiptId(receiptId);
	}

	private ReceiptAthleteDto convertToDto(ReceiptAthleteDao dao) {
		if (dao == null) {
			return null;
		}
		ReceiptAthleteDto dto = new ModelMapper().map(dao, ReceiptAthleteDto.class);

		AthleteDao athlete = dao.getAthlete();

		SportSchoolDao sportSchool = athlete.getSportSchool();
		dto.setSportSchoolId(sportSchool.getId());
		dto.setSportSchoolName(sportSchool.getName());

		FamilyDao family = athlete.getFamily();
		dto.setFamilyId(family.getId());
		dto.setFamilyCode(family.getCode());

		GroupDao group = athlete.getGroup();
		if (group != null) {
			dto.setGroupId(group.getId());
			dto.setGroupName(group.getName());
			dto.setSpecialization(group.isSpecialization());

//		dto.setScheduleIds(athlete.getSchedules().stream().map(ScheduleDao::getId).collect(Collectors.toSet()));
		}

		dto.setName(athlete.getName());
		dto.setFirstSurname(athlete.getFirstSurname());
		dto.setSecondSurname(athlete.getSecondSurname());

		dto.setCategory(athlete.getCategory());
		dto.setDorsalCategory(athlete.getDorsalCategory());
		dto.setDorsalNumber(athlete.getDorsalNumber());
		dto.setLicense(athlete.getLicense());
		dto.setLicenseType(athlete.getLicenseType());
		dto.setIban(athlete.getIban());
		dto.setPaymentType(athlete.getPaymentType());
		dto.setFeeType(athlete.getFeeType());

		dto.setPaymentType(athlete.getPaymentType());
		ReceiptDao receipt = dao.getReceipt();
		if (receipt != null) {
			dto.setReceiptCollection(receipt.getCollection());
			dto.setReceiptName(receipt.getName());
			dto.setReceiptCreatedAt(receipt.getCreatedAt());
		}

//		Set<ReceiptAthleteDao> receipts = athlete.getReceipts();
//		if (receipts != null && !receipts.isEmpty()) {
//			dto.setMonthlyFee(dto.getMonthlyFee() + receipts.stream()
//					.filter(d -> d.getCreatedAt().before(dto.getCreatedAt())
//							&& d.getStatus().equals(PaymentStatus.PENDING.name().toLowerCase()))
//					.map(ReceiptAthleteDao::getMonthlyFee).reduce(Float.valueOf(0), Float::sum));
//		}

		return dto;
	}

	private ReceiptAthleteDao convertToDao(ReceiptAthleteDto dto) {
		ReceiptAthleteDao dao = new ModelMapper().map(dto, ReceiptAthleteDao.class);

		dao.setAthlete(athleteRepository.findById(dto.getAthleteId()).get());
		dao.setReceipt(receiptRepository.findById(dto.getReceiptId()).get());

		return dao;
	}

}
