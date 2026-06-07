package com.gzouli.ERP.service.car;

import com.gzouli.ERP.dto.car.CarAssignmentDTO;
import com.gzouli.ERP.dto.car.CarDTO;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface CarService {
    List<CarDTO> getAllCars();

    
    CarDTO createCar(CarDTO dto);

    
    CarDTO updateCar(Long id, CarDTO dto);

    
    void deleteCar(Long id);

    
    void assignCar(CarAssignmentDTO request);

    
    void releaseCar(Long carId, LocalDate returnDate);
}
