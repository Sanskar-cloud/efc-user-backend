package com.example.efc_user.controllers;

import com.example.efc_user.payloads.CreateHallRequest;
import com.example.efc_user.payloads.HallDto;
import com.example.efc_user.services.impl.HallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@RestController
//@RequestMapping("/api/v1/halls")
//public class HallController {
//
//    @Autowired
//    private HallService hallService;
//
//    @PostMapping
//    public ResponseEntity<HallDto> createHall(@RequestBody CreateHallRequest hallDto) {
//        HallDto createdHall = hallService.createHall(hallDto);
//        return ResponseEntity.ok(createdHall);
//    }
//}

