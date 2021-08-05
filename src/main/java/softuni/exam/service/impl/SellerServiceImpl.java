package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.SellerDto;
import softuni.exam.models.dto.SellersRootDto;
import softuni.exam.models.entity.Seller;
import softuni.exam.repository.SellerRepository;
import softuni.exam.service.SellerService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class SellerServiceImpl implements SellerService {
    private static final String PATH_SELLERS = "src/main/resources/files/xml/sellers.xml";

    private final SellerRepository sellerRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;

    public SellerServiceImpl(SellerRepository sellerRepository, ModelMapper modelMapper, ValidationUtil validationUtil, XmlParser xmlParser) {
        this.sellerRepository = sellerRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
    }

    @Override
    public boolean areImported() {
        if (sellerRepository.count() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public String readSellersFromFile() throws IOException {
       return Files.readString(Path.of(PATH_SELLERS));
    }

    @Override
    public String importSellers() throws IOException, JAXBException {
        String xmlSellers = readSellersFromFile();
        StringBuilder stringBuilder = new StringBuilder();
        SellersRootDto sellersRootDto = xmlParser.fromFile(PATH_SELLERS, SellersRootDto.class);
        List<SellerDto> sellerDtos = sellersRootDto.getSellerDtos();
        for (SellerDto sellerDto : sellerDtos) {
            if (!validationUtil.isValid(sellerDto)) {
                stringBuilder.append("Invalid seller");
                stringBuilder.append(System.lineSeparator());
            } else {
                Seller seller = modelMapper.map(sellerDto, Seller.class);
                sellerRepository.save(seller);
                stringBuilder.append(String.format("Successfully import seller %s - %s", sellerDto.getLastName(), sellerDto.getEmail()));
                stringBuilder.append(System.lineSeparator());
            }
        }
        return stringBuilder.toString().trim();
    }
}
