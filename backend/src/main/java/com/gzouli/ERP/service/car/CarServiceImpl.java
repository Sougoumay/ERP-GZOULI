package com.gzouli.ERP.service.car;

import com.gzouli.ERP.dao.CarAssignmentRepository;
import com.gzouli.ERP.dao.CarRepository;
import com.gzouli.ERP.dao.EmployeeRepository;
import com.gzouli.ERP.dto.car.CarAssignmentDTO;
import com.gzouli.ERP.dto.car.CarDTO;
import com.gzouli.ERP.entity.Car;
import com.gzouli.ERP.entity.CarAssignment;
import com.gzouli.ERP.entity.Employee;
import com.gzouli.ERP.exception.BusinessException;
import com.gzouli.ERP.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarAssignmentRepository assignmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<CarDTO> getAllCars() {
        return carRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CarDTO createCar(CarDTO dto) {
        if (carRepository.existsByRegistrationNumber(dto.getRegistrationNumber())) {
            throw new BusinessException("Ce matricule existe déjà.");
        }
        Car car = new Car();
        car.setBrand(dto.getBrand());
        car.setModel(dto.getModel());
        car.setRegistrationNumber(dto.getRegistrationNumber());
        car.setMonthlyCost(dto.getMonthlyCost());
        car.setState(dto.getState());

        Car savedCar = carRepository.save(car);

        return mapToDTO(savedCar);
    }

    @Transactional
    @Override
    public CarDTO updateCar(Long id, CarDTO dto) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));

        // Vérification Unicité Matricule (Si on le change)
        if (!car.getRegistrationNumber().equals(dto.getRegistrationNumber())
                && carRepository.existsByRegistrationNumber(dto.getRegistrationNumber())) {
            throw new BusinessException("Ce matricule est déjà utilisé par un autre véhicule.");
        }

        // Mise à jour des champs
        car.setBrand(dto.getBrand());
        car.setModel(dto.getModel());
        car.setRegistrationNumber(dto.getRegistrationNumber());
        car.setMonthlyCost(dto.getMonthlyCost()); // Important pour les charges fixes [1]
        car.setState(dto.getState()); // Ex: EN_PANNE, VENDU...

        return mapToDTO(carRepository.save(car));
    }

    @Transactional
    @Override
    public void deleteCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));

        // Règle métier : On ne supprime pas un véhicule qui a un historique d'affectation
        if (!car.getAssignments().isEmpty()) {
            throw new BusinessException("Impossible de supprimer ce véhicule car il est lié à un historique d'affectations. Passez son état à 'HORS_SERVICE' ou 'VENDU' à la place.");
        }

        carRepository.delete(car);
    }

    @Transactional
    @Override
    public void assignCar(CarAssignmentDTO request) {
        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable"));

        // 1. Vérifier si la voiture est déjà prise
        boolean isCarBusy = car.getAssignments().stream().anyMatch(a -> a.getEndDate() == null);
        if (isCarBusy) {
            throw new BusinessException("Ce véhicule est déjà assigné à quelqu'un d'autre !");
        }

        // 2. Vérifier si l'employé a déjà une voiture (Optionnel, mais logique)
        // On pourrait faire une requête, mais supposons ici qu'un employé peut avoir max 1 voiture de fonction

        // 3. Créer l'assignation
        CarAssignment assignment = new CarAssignment();
        assignment.setCar(car);
        assignment.setEmployee(employee);
        assignment.setStartDate(request.getStartDate());
        // endDate null = actif

        assignmentRepository.save(assignment);
    }

    @Transactional
    @Override
    public void releaseCar(Long carId, LocalDate returnDate) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));

        // Trouver l'assignation active
        Optional<CarAssignment> activeAssignment = car.getAssignments().stream()
                .filter(a -> a.getEndDate() == null)
                .findFirst();

        if (activeAssignment.isEmpty()) {
            throw new BusinessException("Ce véhicule n'est pas assigné actuellement.");
        }

        CarAssignment assignment = activeAssignment.get();
        if (returnDate.isBefore(assignment.getStartDate())) {
            throw new BusinessException("La date de retour ne peut pas être avant la date de début.");
        }

        assignment.setEndDate(returnDate);
        assignmentRepository.save(assignment);
    }

    // Mapper
    private CarDTO mapToDTO(Car car) {
        CarDTO dto = new CarDTO();
        dto.setId(car.getId());
        dto.setBrand(car.getBrand());
        dto.setModel(car.getModel());
        dto.setRegistrationNumber(car.getRegistrationNumber());
        dto.setMonthlyCost(car.getMonthlyCost());
        dto.setState(car.getState());

        // Recherche du conducteur actuel
        Optional<CarAssignment> current = car.getAssignments().stream()
                .filter(a -> a.getEndDate() == null)
                .findFirst();

        if (current.isPresent()) {
            dto.setAssigned(true);
            dto.setCurrentDriverId(current.get().getEmployee().getId());
            dto.setCurrentDriverName(current.get().getEmployee().getFirstName() + " " + current.get().getEmployee().getLastName());
        } else {
            dto.setAssigned(false);
        }
        return dto;
    }
}
