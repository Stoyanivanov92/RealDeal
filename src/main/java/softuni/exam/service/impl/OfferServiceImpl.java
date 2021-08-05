package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.OfferDto;
import softuni.exam.models.dto.OffersRootDto;
import softuni.exam.models.entity.Car;
import softuni.exam.models.entity.Offer;
import softuni.exam.models.entity.Picture;
import softuni.exam.repository.CarRepository;
import softuni.exam.repository.OfferRepository;
import softuni.exam.repository.PictureRepository;
import softuni.exam.service.OfferService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class OfferServiceImpl implements OfferService {
    private static final String PATH_OFFERS = "src/main/resources/files/xml/offers.xml";

    private final OfferRepository offerRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;
    private final CarRepository carRepository;
    private final PictureRepository pictureRepository;

    public OfferServiceImpl(OfferRepository offerRepository, ModelMapper modelMapper, ValidationUtil validationUtil, XmlParser xmlParser, CarRepository carRepository, PictureRepository pictureRepository) {
        this.offerRepository = offerRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
        this.carRepository = carRepository;
        this.pictureRepository = pictureRepository;
    }

    @Override
    public boolean areImported() {
        if (offerRepository.count() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public String readOffersFileContent() throws IOException {
      return Files.readString(Path.of(PATH_OFFERS));
    }

    @Override
    public String importOffers() throws IOException, JAXBException {
        OffersRootDto offersRootDto = xmlParser.fromFile(PATH_OFFERS, OffersRootDto.class);
        StringBuilder stringBuilder =new StringBuilder();
        List<OfferDto> offerDtos = offersRootDto.getOfferDtos();
        for (OfferDto offerDto : offerDtos) {
            if (!validationUtil.isValid(offerDto)) {
                stringBuilder.append("Invalid offer");
                stringBuilder.append(System.lineSeparator());
            } else {
                Offer offer = modelMapper.map(offerDto, Offer.class);
                Car car = carRepository.findById(offerDto.getCar().getId()).orElse(null);
                offer.setAddedOn(LocalDateTime.parse(offerDto.getAddedOn(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                Set<Picture> pictures = new LinkedHashSet<>();
                List<Picture> allPictures = pictureRepository.findAll();
                for (Picture picture : allPictures) {
                    assert car != null;
                    if (picture.getCar().getId() == car.getId()) {
                        pictures.add(picture);
                    }
                }
                offer.setPictures(pictures);
                offerRepository.save(offer);
                stringBuilder.append(String.format("Successfully import offer %s - %s",offerDto.getAddedOn(), offerDto.isHasGoldStatus()));
                stringBuilder.append(System.lineSeparator());
            }
        }
        return stringBuilder.toString().trim();
    }
}
