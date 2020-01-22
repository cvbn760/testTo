package com.space.service;

import com.space.exceptions.BadRequestException;
import com.space.exceptions.ShipNotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.TableRepository;
import com.space.specifications.SearchCriteria;
import com.space.specifications.SearchOperaton;
import com.space.specifications.SpecificationShip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Transactional
@Service
@ComponentScan("com.space.specifications")
public class ShipService
{
    private TableRepository tableRepository;
    private SpecificationShip specificationShip = new SpecificationShip();

   // @Autowired
    public void setSpecificationShip(@Qualifier("spec") SpecificationShip specificationShip)
    {
        this.specificationShip = specificationShip;
    }

    @Autowired
    public void setTableRepository(TableRepository tableRepository)
    {
        this.tableRepository = tableRepository;
    }

    public List<Ship> getShipList(Specification<Ship> specification, Pageable pageable)
    {

        return tableRepository.findAll(specification, pageable).getContent();

    }

    @Transactional
    public Integer getCount(Specification<Ship> specification)
    {
        return Math.toIntExact(tableRepository.count(specification));
    }

    @Transactional
    public Specification<Ship> getSpec(String name, String planet,
                                       ShipType shipType, Long dateAfter,
                                       Long dateBefore, Boolean used,
                                       Double minSpeed, Double maxSpeed,
                                       Integer minCrewSize, Integer maxCrewSize,
                                       Double minRating, Double maxRating)
    {
        specificationShip.addSearchCriteria(new SearchCriteria("new_search_criteria_list",1, SearchOperaton.NULL));

        if (name != null)
        {
            specificationShip.addSearchCriteria(new SearchCriteria("name",name,SearchOperaton.CONSTAINS));
        }

        if (planet != null)
        {
            specificationShip.addSearchCriteria(new SearchCriteria("planet",planet,SearchOperaton.CONSTAINS));
        }

        if (shipType != null)
        {
            specificationShip.addSearchCriteria(new SearchCriteria("shipType",shipType,SearchOperaton.SHIP_TYPE_EQUAL));
        }

        if (dateAfter != null)
        {
            Date after = new Date(dateAfter);
            specificationShip.addSearchCriteria(new SearchCriteria("prodDate",after,SearchOperaton.DATE_GREAT_OR_EQUAL));
        }

        if (dateBefore != null)
        {
            Date before = new Date(dateBefore);
            specificationShip.addSearchCriteria(new SearchCriteria("prodDate",before,SearchOperaton.DATE_LESS_OR_EQUAL));
        }

        if (used != null)
        {
            if (used)
            {
                specificationShip.addSearchCriteria(new SearchCriteria("isUsed",used,SearchOperaton.USED_TRUE));
            }
            else
            {
                specificationShip.addSearchCriteria(new SearchCriteria("isUsed",used,SearchOperaton.USED_FALSE));
            }
        }

        if (minSpeed != null)
        {
            specificationShip.addSearchCriteria(new SearchCriteria("speed",minSpeed,SearchOperaton.SPEED_GREAT_OR_EQUAL));
        }

        if (maxSpeed != null)
        {
            specificationShip.addSearchCriteria(new SearchCriteria("speed",maxSpeed,SearchOperaton.SPEED_LESS_OR_EQUAL));
        }

        if (minCrewSize != null)
        {
            specificationShip.addSearchCriteria(new SearchCriteria("crewSize",minCrewSize,SearchOperaton.CREW_SIZE_GREAT_OR_EQUAL));
        }

        if (maxCrewSize != null)
        {
            specificationShip.addSearchCriteria(new SearchCriteria("crewSize",maxCrewSize,SearchOperaton.CREW_SIZE_LESS_OR_EQUAL));
        }

        if (minRating != null)
        {
            specificationShip.addSearchCriteria(new SearchCriteria("rating",minRating,SearchOperaton.RATING_GREAT_OR_EQUAL));
        }

        if (maxRating != null)
        {
            specificationShip.addSearchCriteria(new SearchCriteria("rating",maxRating,SearchOperaton.RATING_LESS_OR_EQUAL));
        }

        return specificationShip;
    }

    @Transactional
    public Ship addShip(Ship ship)
    {
        if (ship.getName() == null
                || ship.getPlanet() == null
                || ship.getShipType() == null
                || ship.getProdDate() == null
                || ship.getSpeed() == null
                || ship.getCrewSize() == null)
        {
            throw new BadRequestException("Данные о корабле введены не корректно");
        }

        if (ship.getUsed() == null)
        {
            ship.setUsed(false);
        }

        checkShipParams(ship);

        Double rating = countRating(ship);
        ship.setRating(rating);

        return tableRepository.save(ship);
    }

    // Получить корабль по id
    @Transactional
    public Ship getShipById(Long id)
    {
        if (tableRepository.existsById(id))
        {
            return tableRepository.findById(id).get();
        }
        else
        {
            throw new ShipNotFoundException("Корабль с данным id не найден");
        }
    }


    // Проверка значения id
    @Transactional
    public Long checkId(String id)
    {
        if (id == null || id.equals("") || id.equals("0"))
        {
            throw new BadRequestException("Данный id{" + id + "} введен некорректно");
        }

        try
        {
            long result = Long.parseLong(id);
            if (result <= 0)
            {
                throw new BadRequestException("Данный id{" + id + "} - отрицательное число.");
            }
            else
            {
                return result;
            }
        }
        catch (NumberFormatException exc)
        {
            throw new BadRequestException("Данный id{" + id + "} не парсится в long");
        }
    }

    // Удалить корабль по id
    @Transactional
    public void deleteShip(Long id)
    {
        if (tableRepository.existsById(id))
        {
            tableRepository.deleteById(id);
        }
        else
        {
            throw new ShipNotFoundException("По данному id корабль не найден.");
        }
    }

    // Редактирование корабля
    @Transactional
    public Ship editShip(Long id, Ship ship)
    {
        checkShipParams(ship);

        if (tableRepository.existsById(id))
        {
            Ship editedShip = tableRepository.getOne(id);
            if (ship.getName() != null)
            {
                editedShip.setName(ship.getName());
            }

            if (ship.getPlanet() != null)
            {
                editedShip.setPlanet(ship.getPlanet());
            }

            if (ship.getShipType() != null)
            {
                editedShip.setShipType(ship.getShipType());
            }

            if (ship.getProdDate() != null)
            {
                editedShip.setProdDate(ship.getProdDate());
            }

            if (ship.getSpeed() != null)
            {
                editedShip.setSpeed(ship.getSpeed());
            }

            if (ship.getUsed() != null)
            {
                editedShip.setUsed(ship.getUsed());
            }

            if (ship.getCrewSize() != null)
            {
                editedShip.setCrewSize(ship.getCrewSize());
            }

            Double rating = countRating(editedShip);
            editedShip.setRating(rating);


            return tableRepository.save(editedShip);
        }
        else
        {
            throw new ShipNotFoundException("Ship not found");
        }
    }


    @Transactional
    public Double countRating(Ship ship)
    {
        /*
        Double k = null;
        if (ship.getUsed())
        {
            k = 0.5;
        }
        else
            {
                k = 1.0;
            }


        long year = 3019;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ship.getProdDate());
        long shipYear = calendar.get(Calendar.YEAR);
        double speedShip = ship.getSpeed();

        BigDecimal bigDecimal = new BigDecimal((80 * speedShip * k)/(year - shipYear + 1));
        bigDecimal = bigDecimal.setScale(2,RoundingMode.HALF_UP);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return bigDecimal.doubleValue();

         */

        Calendar cal = Calendar.getInstance();
        cal.setTime(ship.getProdDate());
        int year = cal.get(Calendar.YEAR);

        //calculate rating
        BigDecimal raiting = new BigDecimal((80 * ship.getSpeed() * (ship.getUsed() ? 0.5 : 1)) / (3019 - year + 1));
        //round rating to 2 decimal places
        raiting = raiting.setScale(2, RoundingMode.HALF_UP);
        return raiting.doubleValue();
    }

    @Transactional
    public void checkShipParams(Ship ship)
    {
        // Проверяем имя. Название корабля (до 50 знаков включительно). Не может быть пустым.
        if (ship.getName() != null && (ship.getName().length() < 1 || ship.getName().length() > 50))
        {
            throw new BadRequestException("Название корабля введено не корректно.");
        }

        // Проверяем планету. Планета пребывания (до 50 знаков включительно). Не может быть пустым.
        if (ship.getPlanet() != null && (ship.getPlanet().length() < 1 || ship.getPlanet().length() > 50))
        {
            throw new BadRequestException("Планета пребывания введена не корректно.");
        }

        // Проверяем тип корабля.
        if (ship.getShipType() != null && ShipType.isElementOfEnum(ship.getShipType().toString()))
        {
            throw new BadRequestException("Тип корабля указан не корректно");
        }

        // Проверяем максимальную скорость. Диапазон значений 0,01..0,99 включительно.
        // Используй математическое округление до сотых.
        if (ship.getSpeed() != null && (ship.getSpeed() < 0.01D || ship.getSpeed() > 0.99D))
        {
            throw new BadRequestException("Скорость корабля указана не корректно");
        }

        // Проверяем количество членов экипажа. Диапазон значений 1..9999 включительно.
        if (ship.getCrewSize() != null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999))
        {
            throw new BadRequestException("Количество членов экипажа указана не корректно");
        }

        if (ship.getProdDate() != null)
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(ship.getProdDate());
            if (cal.get(Calendar.YEAR) < 2800 || cal.get(Calendar.YEAR) > 3019)
                throw new BadRequestException("Incorrect Ship.date");
        }
    }
}
