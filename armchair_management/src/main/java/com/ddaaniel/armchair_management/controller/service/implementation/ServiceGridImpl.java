package com.ddaaniel.armchair_management.controller.service.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ddaaniel.armchair_management.controller.exception.InitialGridNotFoundException;
import com.ddaaniel.armchair_management.controller.service.IGridService;
import com.ddaaniel.armchair_management.model.GridEntity;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.record.GridDTO;
import com.ddaaniel.armchair_management.model.record.GridEntityDTO;
import com.ddaaniel.armchair_management.model.record.SeatDTO;
import com.ddaaniel.armchair_management.model.repository.IGridRepository;
import com.ddaaniel.armchair_management.model.repository.ISeatRepository;

import lombok.var;

@Service
public class ServiceGridImpl implements IGridService {

  private final IGridRepository gridRepository;
  private final ISeatRepository seatRepository;

  @Autowired
  public ServiceGridImpl(IGridRepository gridRepository, ISeatRepository seatRepository) {
    this.gridRepository = gridRepository;
    this.seatRepository = seatRepository;
  }


  @Override
  public Optional<GridEntity> findGridEntityById(UUID uuid){
    return gridRepository.findGridEntityById(uuid);
  }

  @Override
  public GridDTO currentGrid() {
    List<List<SeatDTO>> currentGrid = generateGrid();
    GridEntity initialGrid = gridRepository.initialGrid().orElseThrow(() -> new InitialGridNotFoundException("Grid inicial não encontrado."));
    List<Seat> allSeats = seatRepository.findSeatsByGridId(initialGrid.getGrid());

    for (List<SeatDTO> seatList: currentGrid) {
      parseSeatList(seatList, allSeats);
    }
    return pushToGridDTO(initialGrid, currentGrid);
  }


  private void parseSeatList(List<SeatDTO> seatList, List<Seat> allSeats){
    for (SeatDTO seat : seatList) {
      for (Seat mirror: allSeats) {
        /*
         * (Autounboxing)
         * O Java automaticamente converte o Integer (objeto) para int (primitivo) através do autounboxing.
         * Permitindo a comparar os seus tipos primitivos com outros.
         *
         * // int position = seat.getPosition();
         * // int row = seat.getRow();
         * // int column = seat.getColumn();
         * //
         * // int position_mirror = mirror.getPosition();
         * // int row_mirror = mirror.getRow();
         * // int column_mirror = mirror.getColumn();
         *
         * // if (position == position_mirror && row == row_mirror && column == column_mirror) {
         *
         * */

         if ( /* seat.getPosition().intValue() == mirror.getPosition().intValue() && */
              seat.getRow().intValue() == mirror.getRow().intValue() &&
              seat.getColumn().intValue() == mirror.getColumn().intValue() ){
        /* HOT-FIX
         *
         * Conditions never reached, since with '==' we are
         * comparing the 'seat' pointer with the 'mirror' pointer.
         * '.equals()' shoud be used to compare the pointers value.
         * Or make the guffi approach above.
         *
         * if (
         * seat.getPosition().equals(mirror.getPosition()) &&
         * seat.getRow().equals(mirror.getRow()) &&
         * seat.getColumn().equals(mirror.getColumn())) {
         *
         * Fun fact: "Java does a (cache) optimization to values between -128 e 127"
         *
         * Integer a = 127;  // Use the cached object
         * Integer b = 127;  // Use the SAME cached object
         * a == b            // TRUE - Same reference!
         *
         * Integer c = 128;  // New object
         * Integer d = 128;  // Other new object
         * c == d            // FALSE - Diferent Reference!
         *
         * */
          // seat.setSeatID(mirror.getSeatID());
          // seat.setStatus(mirror.getStatus());
          // seat.setFree(mirror.getFree());
          // seat.setPerson(mirror.getPerson());
          // seat.setCurrentGrid(mirror.getCurrentGrid());

          seat.setStatus(mirror.getStatus());
          seat.setFree(mirror.getFree());
          break;
        }
      }
    }
  }



  private List<List<SeatDTO>> generateGrid() {
    List<List<SeatDTO>> grid = new ArrayList<>();
    int position = 0;

    var initialEntity = gridRepository.initialGrid();
    var rows = initialEntity.get().getRowNumber();
    var columns = initialEntity.get().getColumnNumber();

    for (int rowCount = 1; rowCount <= rows; rowCount++) {

      ArrayList<SeatDTO> rowList = new ArrayList<>();
      grid.add(generateColumns(rowCount, columns, rowList, position));
      position = position + columns;
    }

    return grid;
  }

  private List<SeatDTO> generateColumns(int rowCount, int columns, List<SeatDTO> rowList, int position){
    for (int c = 1; c <= columns; c++) {

      // rowList.add(Seat.builder()
      //    .position(position + 1)
      //    .row(rowCount)
      //    .column(c)
      //    .build()
      //  );
      rowList.add(SeatDTO.builder()
         .position(String.valueOf(Integer.valueOf(position) + 1))
         .row(rowCount)
         .column(c)
         .build()
       );
      position++;
    }
    return rowList;
  }


  public GridDTO pushToGridDTO(GridEntity gridEntity, List<List<SeatDTO>> grid) {

    GridEntityDTO entityDTO = GridEntityDTO.builder()
      .grid(gridEntity.getGrid())
      .rowNumber(gridEntity.getRowNumber())
      .columnNumber(gridEntity.getColumnNumber())
      .is_currentGrid(gridEntity.getIs_currentGrid()).build();

    GridDTO dto = new GridDTO(entityDTO, grid);
    return dto;
  }

}
