package com.mlog.controller;


import com.mlog.dto.Result;
import com.mlog.service.IShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {

    private final IShopService shopService;

    @GetMapping("/list")
    public Result list() {
        // Return active shop list;
        return shopService.list();
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable Long id) {
        // Return shop detail by id
        return shopService.detail(id);
    }

    @PostMapping("/geo/load")
    public Result loadShopGeoData() {
        return shopService.loadShopGeoData();
    }

    @GetMapping("/nearby")
    public Result queryNearbyShops(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "3") Double radius
    ) {
        return shopService.queryNearbyShops(lat, lng, radius);
    }

}
