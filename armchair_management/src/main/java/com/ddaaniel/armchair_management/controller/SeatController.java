package com.ddaaniel.armchair_management.controller;

import com.ddaaniel.armchair_management.controller.service.implementation.ServicePersonImpl;
import com.ddaaniel.armchair_management.controller.service.implementation.ServiceSeatImpl;
import com.ddaaniel.armchair_management.model.record.MessageResponseDTO;
import com.ddaaniel.armchair_management.model.record.RequestAllocationDTO;
import com.ddaaniel.armchair_management.model.record.SeatResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.aot.PublicMethodReflectiveProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/seats")
public class SeatController {

	private final ServiceSeatImpl serviceSeat;
	private final ServicePersonImpl servicePessoa;

	@Autowired
	public SeatController(ServiceSeatImpl serviceSeat, ServicePersonImpl servicePessoa) {
		this.serviceSeat = serviceSeat;
		this.servicePessoa = servicePessoa;
	}

	// buscando os status de cada poltrona pela sua posição
	@GetMapping
	public ResponseEntity<List<SeatResponseDTO>> getAllStatusPoltronas() {
		var response = serviceSeat.listStatusOfAllSeats();
		return ResponseEntity.ok().body(response);
	}

	// Buscando detalhes de uma poltrona específica pelo seu número de posição
	@GetMapping("/{position}")
	public ResponseEntity<SeatResponseDTO> getBySeat(@PathVariable Integer position) {
		var response = serviceSeat.detailsFromSpecificSeat(position);
		return ResponseEntity.ok().body(response);
	}

	// Alocando uma poltrona para pessoa
	@PutMapping("/allocate")
	public ResponseEntity<?> addPersonToSeat(@RequestBody RequestAllocationDTO dto) {
		serviceSeat.allocateSeatToPessoa(dto.position(), dto.name(), dto.cpf());

		MessageResponseDTO message = new MessageResponseDTO("Poltrona alocada com sucesso.");
		return ResponseEntity.ok(message);
	}

	// Removendo a relação de pessoa e poltrona e também removendo o registro da
	// respectiva
	// pessoa para não ficar acumulando no BD
	@PutMapping("/remove/{position}")
	public ResponseEntity<?> removePersonFromSeat(@PathVariable Integer position) {
		servicePessoa.removePessoaFromSeat(position);

		MessageResponseDTO message = new MessageResponseDTO("Pessoa removida da Poltrona.");
		return ResponseEntity.ok(message);
	}
}
