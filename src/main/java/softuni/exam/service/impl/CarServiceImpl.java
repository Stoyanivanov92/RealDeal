package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.CarDto;
import softuni.exam.models.entity.Car;
import softuni.exam.repository.CarRepository;
import softuni.exam.service.CarService;
import softuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CarServiceImpl implements CarService {
    private static final String PATH_CARS = "src/main/resources/files/json/cars.json";

    private final Gson gson;
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    public CarServiceImpl(Gson gson, CarRepository carRepository, ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.gson = gson;
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public boolean areImported() {
        if (carRepository.count() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public String readCarsFileContent() throws IOException {
        return Files.readString(Path.of(PATH_CARS));

    }

    @Override
    public String importCars() throws IOException {
        String jsonStringCars = readCarsFileContent();
        CarDto[] carDtos = gson.fromJson(jsonStringCars, CarDto[].class);
        StringBuilder stringBuilder = new StringBuilder();
        for (CarDto carDto : carDtos) {
           if (!validationUtil.isValid(carDto)) {
               stringBuilder.append("Invalid car");
               stringBuilder.append(System.lineSeparator());
           } else {
               Car car = modelMapper.map(carDto, Car.class);
               car.setRegisteredOn(LocalDate.parse(carDto.getRegisteredOn(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
               carRepository.save(car);
               stringBuilder.append(String.format("Successfully imported car - %s - %s", car.getMake(), car.getModel()));
               stringBuilder.append(System.lineSeparator());
           }

        }
        return stringBuilder.toString().trim();
    }

    @Override
    public String getCarsOrderByPicturesCountThenByMake() {
        List<Car> cars = carRepository.findAllCarsOrderedByPicturesCountThenByMake();
        StringBuilder stringBuilder = new StringBuilder();
        for (Car car : cars) {
            stringBuilder.append(String.format("Car make - %s, model - %s%n" +
                    "\tKilometers - %d%n" +
                    "\tRegistered on - %s%n" +
                    "\tNumber of pictures - %d%n", car.getMake(), car.getModel(), car.getKilometers(), car.getRegisteredOn(), car.getPictures().size()));
            stringBuilder.append(System.lineSeparator());

        }
        return stringBuilder.toString();
    }
}
