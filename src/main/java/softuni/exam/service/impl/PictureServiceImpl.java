package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.PictureDto;
import softuni.exam.models.entity.Car;
import softuni.exam.models.entity.Picture;
import softuni.exam.repository.CarRepository;
import softuni.exam.repository.PictureRepository;
import softuni.exam.service.PictureService;
import softuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class PictureServiceImpl implements PictureService {
    private static final String PATH_PICTURES = "src/main/resources/files/json/pictures.json";

    private final PictureRepository pictureRepository;
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final CarRepository carRepository;

    public PictureServiceImpl(PictureRepository pictureRepository, ModelMapper modelMapper, Gson gson, ValidationUtil validationUtil, CarRepository carRepository) {
        this.pictureRepository = pictureRepository;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.carRepository = carRepository;
    }

    @Override
    public boolean areImported() {
        if (pictureRepository.count() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public String readPicturesFromFile() throws IOException {
        return Files.readString(Path.of(PATH_PICTURES));
    }

    @Override
    public String importPictures() throws IOException {
        String jsonPictures = readPicturesFromFile();
        StringBuilder stringBuilder = new StringBuilder();
        PictureDto[] pictureDtos = gson.fromJson(jsonPictures, PictureDto[].class);
        for (PictureDto pictureDto : pictureDtos) {
            if (!validationUtil.isValid(pictureDto)) {
                stringBuilder.append("Invalid Picture");
                stringBuilder.append(System.lineSeparator());
            } else {
                Picture picture = modelMapper.map(pictureDto, Picture.class);
                picture.setDateAndTime(LocalDateTime.parse(pictureDto.getDateAndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                Car car = carRepository.findById(pictureDto.getCar()).orElse(null);
                picture.setCar(car);
                pictureRepository.save(picture);
                stringBuilder.append(String.format("Successfully import picture - %s", picture.getName()));
                stringBuilder.append(System.lineSeparator());
            }
        }
        return stringBuilder.toString().trim();
    }
}
