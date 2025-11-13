package com.ddaaniel.armchair_management.controller.service.implementation;

import com.ddaaniel.armchair_management.controller.exception.BadRequestException;
import com.ddaaniel.armchair_management.controller.exception.NotFoundException;
import com.ddaaniel.armchair_management.controller.exception.ValidationException;
import com.ddaaniel.armchair_management.controller.service.ISeatService;
import com.ddaaniel.armchair_management.controller.service.mapper.SeatMapper;
import com.ddaaniel.armchair_management.model.GridEntity;
import com.ddaaniel.armchair_management.model.Person;

import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.enums.SeatType;
import com.ddaaniel.armchair_management.model.record.RowOccupacyDTO;
import com.ddaaniel.armchair_management.model.record.RowOccupacyProjection;
import com.ddaaniel.armchair_management.model.record.SeatDTO;
import com.ddaaniel.armchair_management.model.record.SeatResponseDTO;
import com.ddaaniel.armchair_management.model.record.ChartsResponceDTO;
import com.ddaaniel.armchair_management.model.repository.IGridRepository;
import com.ddaaniel.armchair_management.model.repository.IPersonRepository;
import com.ddaaniel.armchair_management.model.repository.ISeatRepository;

// import org.hibernate.mapping.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServiceSeatImpl implements ISeatService {

  private final Logger logger = LoggerFactory.getLogger(ServiceSeatImpl.class);

  private final IPersonRepository personRepository;
  private final ISeatRepository seatRepository;
  private final SeatMapper seatMapper;
  private final IGridRepository gridRepository;

  @Autowired
  public ServiceSeatImpl(ISeatRepository seatRepository, IPersonRepository personRepository,
      IPersonRepository personRepository1, SeatMapper seatMapper, IGridRepository gridRepository) {
    this.seatRepository = seatRepository;
    this.personRepository = personRepository1;
    this.seatMapper = seatMapper;
    this.gridRepository = gridRepository;
  }

  @Override
  public void eraseAllSeatsState(UUID uuid) {
    List<Seat> seatList = seatRepository.findSeatsByGridId(uuid);
    for (Seat seat : seatList) {
      seat.setFree(true);
      seat.setStatus(SeatType.AVAILABLE);
      seat.setPerson(null);
    }
    seatRepository.saveAll(seatList);

  }

  // listando status de todas as poltronas
  @Override
  public List<SeatResponseDTO> listStatusOfAllSeats() {
    List<Seat> list = seatRepository.findAll();

    return seatMapper.toDTOList(list);
  }

  // listando detalhes de uma poltrona específica
  @Override
  public SeatResponseDTO detailsFromSpecificSeat(Integer row, Integer column) {
    Seat seat = seatRepository.findByPosition(row, column)
        .orElseThrow(() -> new NotFoundException("Poltrona não encontrada."));

    return seatMapper.toDTO(seat);
  }

  @Transactional
  @Override
  public void allocateSeatToPessoa(Integer row, Integer column, String name, String cpf) {
    lenghtNameValidation(name);
    cpfValidation(cpf);
    // positionValidation(position);
    Seat seat = getSeatFromPosition(row, column);

    if (!seat.getFree()) {
      throw new BadRequestException("Poltrona já está ocupada.");
    }
    Person person = Person.builder()
        .name(name)
        .cpf(cpf)
        .build();
    allocating(seat, person);
  }

  @Override
  @Transactional
  public void updateModifiedSeats(List<List<SeatDTO>> seatGridDTO, UUID gridId) {
    // Busca o grid entity uma vez
    GridEntity gridEntity = gridRepository.findById(gridId)
        .orElseThrow(() -> new NotFoundException("Grid não encontrado"));

    List<Seat> seatsToSave = new ArrayList<>();

    for (List<SeatDTO> row : seatGridDTO) {
      for (SeatDTO seatDTO : row) {
        Optional<Seat> seatOpt = seatRepository.getSeatByColumnAndRow(
            seatDTO.getColumn(), seatDTO.getRow(), seatDTO.getGridId());

        if (seatOpt.isPresent()) {
          // Atualiza assento existente
          Seat seat = seatOpt.get();
          seat.setFree(seatDTO.getFree());
          seat.setStatus(seatDTO.getStatus());
          if (seatDTO.getPerson() != null && seatDTO.getPerson().getCpf() != null
              && seatDTO.getPerson().getName() != null) {

            // Person person = seatDTO.getPerson();
            // String cleanCPF = person.getCpf().replaceAll("\\D", "");
            // person.setCpf(cleanCPF);

            personRepository.save(seatDTO.getPerson());
            seat.setPerson(seatDTO.getPerson());
          }
          seatsToSave.add(seat);
        } else {

          // Cria novo assento com currentGrid
          Seat newSeat = Seat.builder()
              .position(seatDTO.getPosition())
              .row(seatDTO.getRow())
              .column(seatDTO.getColumn())
              .free(seatDTO.getFree())
              .status(seatDTO.getStatus())
              .currentGrid(gridEntity)
              // .person(seatDTO.getPerson())
              .build();

          if (seatDTO.getPerson().getCpf() != null && seatDTO.getPerson().getName() != null) {
            personRepository.save(seatDTO.getPerson());
            newSeat.setPerson(seatDTO.getPerson());
          }

          seatsToSave.add(newSeat);
        }
      }
    }

    // Salva todos de uma vez (mais eficiente)
    seatRepository.saveAll(seatsToSave);
    // Remove assentos que não estão mais no grid
    cleanupOrphanedSeats(gridId, seatGridDTO);
  }

  private void cleanupOrphanedSeats(UUID gridId, List<List<SeatDTO>> newGrid) {
    List<Seat> existingSeats = seatRepository.findSeatsByGridId(gridId);
    Set<String> validPositions = new HashSet<String>();

    // Itera sobre cada elemento 'Seat' do novo Grid obtendo
    // seu conjunto de 'row-column' e os salvando no Set
    for (List<SeatDTO> row : newGrid) {
      for (SeatDTO seat : row) {
        validPositions.add(seat.getRow() + "-" + seat.getColumn());
      }
    }

    // Verificamos se as 'Seats' do novo Grid tambem se encontram no Grid "antigo"
    // persistido no banco de dados e caso não so deletamos do banco de dados
    for (Seat seat : existingSeats) {
      String positionKey = seat.getRow() + "-" + seat.getColumn();
      if (!validPositions.contains(positionKey)) {
        seatRepository.delete(seat);
      }
    }
  }

  private Seat getSeatFromPosition(Integer row, Integer column) {
    return seatRepository.findByPosition(row, column)
        .orElseThrow(() -> new NotFoundException("Poltrona não encontrada."));
  }

  private void cpfValidation(String cpf) {
    if (cpf == null || cpf.length() != 11 || !cpf.matches("\\d{11}")) {
      throw new ValidationException("O CPF deve conter exatamente 11 dígitos numéricos.");
    }
  }

  private void lenghtNameValidation(String name) {
    if (name == null || name.length() > 50) {
      throw new ValidationException("O nome deve ter no máximo 50 caracteres.");
    }
  }

  private void allocating(Seat seat, Person pessoa) {
    seat.setPerson(pessoa);
    seat.setFree(false);

    personRepository.save(pessoa);
    logger.info("Foreign key ( Person --> Seat ) successfully linked !");

    seatRepository.save(seat);
    logger.info("Entity Seat, successfully saved !");
  }

  @Override
  public ChartsResponceDTO charts(UUID gridID) {
    Integer seatsOccupied = seatRepository.countSeatsOccupied();
    Integer countAllSeats = seatRepository.countAllSeats();

    Integer seatsUnoccupied = seatRepository.countSeatsUnoccupied(gridID);

    Float percentOccupation = seatsOccupied * 100.0f / countAllSeats;

    // * Interface-based Projections *
    List<RowOccupacyProjection> results = seatRepository.getOccupacyByRow(gridID);

    List<RowOccupacyDTO> occupacyByRow = results.stream()
        .map(projection -> RowOccupacyDTO.builder()
            .fileira(projection.getFileira())
            .totalAssentos(projection.getTotalAssentos())
            .assentosLivre(projection.getAssentosLivres())
            .taxaOcupacaoPercentual(projection.getTaxaOcupacaoPercentual())
            .build())
        .collect(Collectors.toList());

    return ChartsResponceDTO.builder()
        .percentOccupied(percentOccupation)
        .seatsUnoccupied(seatsUnoccupied)
        .rowOccupacyDTO(occupacyByRow)
        .build();
  }

}
