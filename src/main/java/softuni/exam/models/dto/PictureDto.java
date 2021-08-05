package softuni.exam.models.dto;

import com.google.gson.annotations.Expose;
import softuni.exam.models.entity.Car;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class PictureDto {
    @Expose
    private String name;
    @Expose
    private String dateAndTime;
    @Expose
    private long car;

    public PictureDto() {
    }

    @Size(min = 2, max = 19)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public long getCar() {
        return car;
    }

    public void setCar(long car) {
        this.car = car;
    }
}
