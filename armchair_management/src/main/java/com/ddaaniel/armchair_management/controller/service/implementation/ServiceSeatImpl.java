package com.ddaaniel.armchair_management.controller.service.implementation;

import com.ddaaniel.armchair_management.controller.exception.BadRequestException;
import com.ddaaniel.armchair_management.controller.exception.NotFoundException;
import com.ddaaniel.armchair_management.controller.exception.ValidationException;
import com.ddaaniel.armchair_management.controller.service.IGridService;
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
import com.ddaaniel.armchair_management.model.record.ShortSeatDTO;
import com.ddaaniel.armchair_management.model.record.ChartsResponceDTO;
import com.ddaaniel.armchair_management.model.record.GridEntityDTO;
import com.ddaaniel.armchair_management.model.record.RoomOccupacyDTO;
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
  private final IGridService gridService;

  @Autowired
  public ServiceSeatImpl(ISeatRepository seatRepository, IPersonRepository personRepository,
      IPersonRepository personRepository1, SeatMapper seatMapper, IGridRepository gridRepository,
      IGridService gridService) {
    this.seatRepository = seatRepository;
    this.personRepository = personRepository1;
    this.seatMapper = seatMapper;
    this.gridRepository = gridRepository;
    this.gridService = gridService;
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

  @Override
  public List<ShortSeatDTO> listAllSeats() {
    List<Seat> list = seatRepository.findAll();
    return seatMapper.toShortDTOList(list);
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

  @Override
  @Transactional
  public void updateModifiedSeats(List<List<SeatDTO>> seatGridDTO, UUID gridId) {
    GridEntity gridEntity = gridRepository.findById(gridId)
        .orElseThrow(() -> new NotFoundException("Grid não encontrado"));

    List<Seat> seatsToSave = new ArrayList<>();

    for (List<SeatDTO> row : seatGridDTO) {
      for (SeatDTO seatDTO : row) {
        Optional<Seat> seatOpt = seatRepository.getSeatByColumnAndRow(
            seatDTO.getColumn(), seatDTO.getRow(), gridId);

        if (seatOpt.isPresent()) {
          // Atualiza assento existente
          Seat seat = seatOpt.get();
          updateExistingSeat(seat, seatDTO);
          seatsToSave.add(seat);
        } else {
          // Cria novo assento
          Seat newSeat = createNewSeat(seatDTO, gridEntity);
          seatsToSave.add(newSeat);
        }
      }
    }

    seatRepository.saveAll(seatsToSave);
    cleanupOrphanedSeats(gridId, seatGridDTO);
  }

  private void updateExistingSeat(Seat seat, SeatDTO seatDTO) {
    seat.setFree(seatDTO.getFree());
    seat.setStatus(seatDTO.getStatus());

    // Verificação segura para pessoa
    if (seatDTO.getPerson() != null) {
      updatePersonForSeat(seat, seatDTO.getPerson());
    } else {
      seat.setPerson(null); // Remove pessoa se for null
    }
  }

  private Seat createNewSeat(SeatDTO seatDTO, GridEntity gridEntity) {
    Seat newSeat = Seat.builder()
        .position(seatDTO.getPosition())
        .row(seatDTO.getRow())
        .column(seatDTO.getColumn())
        .free(seatDTO.getFree())
        .status(seatDTO.getStatus())
        .currentGrid(gridEntity)
        .build();

    // Verificação segura para pessoa
    if (seatDTO.getPerson() != null) {
      updatePersonForSeat(newSeat, seatDTO.getPerson());
    }

    return newSeat;
  }

  private void updatePersonForSeat(Seat seat, Person personDTO) {
    // Verifica se a pessoa tem dados válidos
    if (personDTO.getCpf() != null && personDTO.getName() != null) {
      // Limpa o CPF se necessário
      String cleanCPF = personDTO.getCpf().replaceAll("\\D", "");
      personDTO.setCpf(cleanCPF);

      personRepository.save(personDTO);
      seat.setPerson(personDTO);
    }
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
  public ChartsResponceDTO charts() {
    var gridID = gridRepository.currentGrid().get().getGrid();
    Integer seatsOccupied = seatRepository.countSeatsOccupied(gridID);
    Integer countAllSeats = seatRepository.countAllSeats(gridID);

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
        .roomOccupacyDTOs(getOccupancyPerRoom())
        .build();
  }

  @Override
  public ChartsResponceDTO charts(UUID gridID) {
    Integer seatsOccupied = seatRepository.countSeatsOccupied(gridRepository.currentGrid().get().getGrid());
    Integer countAllSeats = seatRepository.countAllSeats(gridRepository.currentGrid().get().getGrid());

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
        .roomOccupacyDTOs(getOccupancyPerRoom())
        .build();
  }

  private List<RoomOccupacyDTO> getOccupancyPerRoom() {
    List<RoomOccupacyDTO> roomList = new ArrayList<>();

    var e = gridService.gridList();
    for (GridEntityDTO gridEntityDTO : e) {
      var countAllSeats = seatRepository.countAllSeats(gridEntityDTO.getGrid());
      var seatsOccupied = seatRepository.countSeatsOccupied(gridEntityDTO.getGrid());

      RoomOccupacyDTO dto = new RoomOccupacyDTO(gridEntityDTO.getGrid(), seatsOccupied * 100.0f / countAllSeats);
      roomList.add(dto);
    }

    return roomList;
  }

}
