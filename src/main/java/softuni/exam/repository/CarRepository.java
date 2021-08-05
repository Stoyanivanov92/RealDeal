package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entity.Car;

import java.util.List;

//ToDo
@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("Select c From Car c Order by c.pictures.size desc, c.make")
    List<Car> findAllCarsOrderedByPicturesCountThenByMake();
}
