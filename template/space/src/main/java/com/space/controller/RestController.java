package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
@ComponentScan("com.space")
@RequestMapping(value = "/rest")
public class RestController
{
    private ShipService shipService;

    @Autowired
    public void setShipService(ShipService shipService)
    {
        this.shipService = shipService;
    }

    @GetMapping(value = "/ships")
    @ResponseStatus(HttpStatus.OK)
    public List<Ship> getShipList(@RequestParam(name = "name",required = false) String name,
                                  @RequestParam(name = "planet",required = false) String planet,
                                  @RequestParam(name = "shipType",required = false) ShipType shipType,
                                  @RequestParam(name = "after",required = false) Long after,
                                  @RequestParam(name = "before",required = false) Long before,
                                  @RequestParam(name = "isUsed",required = false) Boolean isUsed,
                                  @RequestParam(name = "minSpeed",required = false) Double minSpeed,
                                  @RequestParam(name = "maxSpeed",required = false) Double maxSpeed,
                                  @RequestParam(name = "minCrewSize",required = false) Integer minCrewSize,
                                  @RequestParam(name = "maxCrewSize",required = false) Integer maxCrewSize,
                                  @RequestParam(name = "minRating",required = false) Double minRating,
                                  @RequestParam(name = "maxRating",required = false) Double maxRating,
                                  @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order,
                                  @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize)
    {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        Specification<Ship> shipSpecification = shipService.getSpec(name,planet,shipType,after,
                before,isUsed,minSpeed,maxSpeed,
                minCrewSize,maxCrewSize,minRating,maxRating);

        return shipService.getShipList(shipSpecification, pageable);
    }

    @RequestMapping(value = "/ships/count", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Integer getShipCount(@RequestParam(name = "name",required = false) String name,
                                @RequestParam(name = "planet",required = false) String planet,
                                @RequestParam(name = "shipType",required = false) ShipType shipType,
                                @RequestParam(name = "after",required = false) Long after,
                                @RequestParam(name = "before",required = false) Long before,
                                @RequestParam(name = "isUsed",required = false) Boolean isUsed,
                                @RequestParam(name = "minSpeed",required = false) Double minSpeed,
                                @RequestParam(name = "maxSpeed",required = false) Double maxSpeed,
                                @RequestParam(name = "minCrewSize",required = false) Integer minCrewSize,
                                @RequestParam(name = "maxCrewSize",required = false) Integer maxCrewSize,
                                @RequestParam(name = "minRating",required = false) Double minRating,
                                @RequestParam(name = "maxRating",required = false) Double maxRating)
    {
        Specification<Ship> shipSpecification = shipService.getSpec(name,planet,shipType,after,
                before,isUsed,minSpeed,maxSpeed,
                minCrewSize,maxCrewSize,minRating,maxRating);

        return shipService.getCount(shipSpecification);
    }

    @PostMapping(value = "/ships")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Ship addShip(@RequestBody Ship ship)
    {
        return shipService.addShip(ship);
    }

    @GetMapping(value = "/ships/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Ship getShip(@PathVariable(value = "id") String id)
    {
        Long validId = shipService.checkId(id);
        return shipService.getShipById(validId);
    }

    @PostMapping(value = "/ships/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Ship editShip(@PathVariable(value = "id") String id, @RequestBody Ship ship)
    {
        Long validId = shipService.checkId(id);
        return shipService.editShip(validId,ship);
    }

    @RequestMapping(value = "/ships/{id}",method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteShip(@PathVariable(value = "id") String id)
    {

        Long validId = shipService.checkId(id);
        shipService.deleteShip(validId);
    }

}
