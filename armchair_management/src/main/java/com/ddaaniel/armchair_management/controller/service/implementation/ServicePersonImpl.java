package com.ddaaniel.armchair_management.controller.service.implementation;

import com.ddaaniel.armchair_management.controller.exception.AssentoInvalidoException;
import com.ddaaniel.armchair_management.controller.exception.BadRequestException;
import com.ddaaniel.armchair_management.controller.exception.NotFoundException;
// import com.ddaaniel.armchair_management.controller.service.IGridService;
import com.ddaaniel.armchair_management.controller.service.IPersonService;
import com.ddaaniel.armchair_management.model.Person;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.repository.IGridRepository;
import com.ddaaniel.armchair_management.model.repository.IPersonRepository;
import com.ddaaniel.armchair_management.model.repository.ISeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ServicePersonImpl implements IPersonService {

  private final Logger logger = LoggerFactory.getLogger(ServicePersonImpl.class);

  // Dependências
  private final IPersonRepository iPersonRepository;
  private final ISeatRepository iSeatRepository;
  private final IGridRepository gridRepository;


  @Autowired
  public ServicePersonImpl(IPersonRepository iPersonRepository, ISeatRepository iSeatRepository, IGridRepository gridRepository) {
    this.iPersonRepository = iPersonRepository;
    this.iSeatRepository = iSeatRepository;
    this.gridRepository = gridRepository;
  }


  @Override
  @Transactional
  public void removePessoaFromSeat(Integer row, Integer column) {
    // positionIsValid(position);
    Seat armchair = findSeat(row, column);

    if (selectedSeatIsFree(armchair)) {
      throw new BadRequestException("A Poltrona já está desocupada.");
    }

    Person pessoa = armchair.getPerson();
    removeOccupantFromSeat(armchair);
    iPersonRepository.delete(pessoa);
    logger.info("Person deleted from Seat !");
  }





  private void positionIsValid(Integer position) {
    var entity = gridRepository.isCurrentGrid().get();
    var totalSeats = entity.getRowNumber() * entity.getColumnNumber();

    if (position <= 0 || position > totalSeats) {   // Verifica se é um parâmetro válido
      throw new AssentoInvalidoException("O assento informado é inválido.");
    }
  }

  private Seat findSeat(Integer row, Integer column) {
    return iSeatRepository.findByPosition(row, column)
    .orElseThrow(()-> new NotFoundException("Poltrona não encontrada."));
  }

  private boolean selectedSeatIsFree(Seat armchair){
    return armchair.getFree();
  }

  private void removeOccupantFromSeat(Seat armchair) {
    armchair.setPerson(null);
    armchair.setFree(true);
    iSeatRepository.save(armchair);
    logger.info("Foreign key ( Person - x -> Seat ) successfully unlinked !");
  }

}
