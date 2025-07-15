package com.ddaaniel.armchair_management.controller.service.implementation;

import com.ddaaniel.armchair_management.controller.exception.AssentoInvalidoException;
import com.ddaaniel.armchair_management.controller.exception.BadRequestException;
import com.ddaaniel.armchair_management.controller.exception.NotFoundException;
import com.ddaaniel.armchair_management.controller.exception.ValidationException;
import com.ddaaniel.armchair_management.controller.service.ISeatService;
import com.ddaaniel.armchair_management.controller.service.mapper.SeatMapper;
import com.ddaaniel.armchair_management.model.Person;

import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.record.SeatResponseDTO;
import com.ddaaniel.armchair_management.model.repository.IPersonRepository;
import com.ddaaniel.armchair_management.model.repository.ISeatRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceSeatImpl implements ISeatService {

    private final Logger logger = LoggerFactory.getLogger(ServiceSeatImpl.class);

    private final IPersonRepository personRepository;
    private final ISeatRepository seatRepository;
    private final SeatMapper seatMapper;

    @Autowired
    public ServiceSeatImpl(ISeatRepository seatRepository, IPersonRepository personRepository, IPersonRepository personRepository1, SeatMapper seatMapper) {
        this.seatRepository = seatRepository;
        this.personRepository = personRepository1;
        this.seatMapper = seatMapper;
    }

    // listando status de todas as poltronas
    @Override
    public List<SeatResponseDTO> listStatusOfAllSeats(){
        List<Seat> list = seatRepository.findAll();

        return seatMapper.toDTOList(list);
    }

    // listando detalhes de uma poltrona específica
    @Override
    public SeatResponseDTO detailsFromSpecificSeat (Integer position){
        positionValidation(position);
        Seat seat = seatRepository.findByPosition(position)
                .orElseThrow(() -> new NotFoundException("Poltrona não encontrada."));

        return seatMapper.toDTO(seat);
    }

    @Transactional
    @Override
    public void allocateSeatToPessoa(Integer position, String name, String cpf) {
        lenghtNameValidation(name);
        cpfValidation(cpf);
        positionValidation(position);
        Seat seat = getSeatFromPosition(position);

        if (!seat.getFree()) {
            throw new BadRequestException("Poltrona já está ocupada.");
        }

        Person person = Person.builder()
                .name(name)
                .cpf(cpf)
                .build();
        allocating(seat, person);
    }




    private void positionValidation(Integer position) {
        if (position <= 0 || position > 15) {   // Verifica se é um parâmetro válido
                throw new AssentoInvalidoException("O assento informado é inválido.");
        }
    }

    private Seat getSeatFromPosition(Integer position) {
        return seatRepository.findByPosition(position)
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
        //seat.setFree(false);
        //seat.setPerson(pessoa);
        personRepository.save(pessoa);
        logger.info("Foreign key ( Person --> Seat ) successfully linked !");

        seatRepository.save(seat);
        logger.info("Entity Seat, successfully saved !");
        //seatRepository.save(seat);
    }
}
