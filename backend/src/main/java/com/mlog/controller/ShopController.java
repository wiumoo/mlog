package com.mlog.controller;


import com.mlog.dto.Result;
import com.mlog.service.IShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
