package softuni.exam.models.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "sellers")
@XmlAccessorType(XmlAccessType.FIELD)
public class SellersRootDto {
    @XmlElement(name = "seller")
    private List<SellerDto> sellerDtos;

    public List<SellerDto> getSellerDtos() {
        return sellerDtos;
    }

    public void setSellerDtos(List<SellerDto> sellerDtos) {
        this.sellerDtos = sellerDtos;
    }
}
