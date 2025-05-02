package com.example.examg6.controller;

import com.example.examg6.entity.Status;
import com.example.examg6.repo.StatusRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {


    private final StatusRepository statusRepository;

    public AdminController(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @GetMapping("/statuses")
    public String statuses(Model model) {
        List<Status> activeStatuses = statusRepository.findByIsActivePositionNumberNotNullOrderByIsActivePositionNumberAsc();
        List<Status> inactiveStatuses = statusRepository.findByIsNotActivePositionNumberNotNullOrderByIsNotActivePositionNumberAsc();
        model.addAttribute("activeStatuses", activeStatuses);
        model.addAttribute("inactiveStatuses", inactiveStatuses);
        return "statuses";
    }

    @PostMapping("/change")
    public String changeStatus(@RequestParam Integer id, @RequestParam String changeActive) {
        List<Status> activeStatuses = statusRepository.findByIsActivePositionNumberNotNullOrderByIsActivePositionNumberAsc();
        List<Status> notActiveStatuses = statusRepository.findByIsNotActivePositionNumberNotNullOrderByIsNotActivePositionNumberAsc();
        if (changeActive.equals("active")) {
            Status status = statusRepository.findById(id).get();
            status.setIsActivePositionNumber(activeStatuses.size() + 1);
            status.setIsNotActivePositionNumber(null);
            statusRepository.save(status);
        }else {
            Status status = statusRepository.findById(id).get();
            status.setIsNotActivePositionNumber(notActiveStatuses.size() + 1);
            status.setIsActivePositionNumber(null);
            statusRepository.save(status);
        }
        return "redirect:/admin/statuses";
    }

    @PostMapping("/save-change")
    public String saveChange(
            @RequestParam(value = "activeIds", required = false) List<Integer> activeIds,
            @RequestParam(value = "activePositions", required = false) List<Integer> activePositions,
            @RequestParam(value = "inactiveIds", required = false) List<Integer> inactiveIds,
            @RequestParam(value = "inactivePositions", required = false) List<Integer> inactivePositions
    ) {
        // Save active statuses
        if (activeIds != null && activePositions != null) {
            for (int i = 0; i < activeIds.size(); i++) {
                Integer id = activeIds.get(i);
                Integer position = activePositions.get(i);
                Status status = statusRepository.findById(id).orElse(null);
                if (status != null) {
                    status.setIsActivePositionNumber(position);
                    status.setIsNotActivePositionNumber(null);
                    statusRepository.save(status);
                }
            }
        }

        // Save inactive statuses
        if (inactiveIds != null && inactivePositions != null) {
            for (int i = 0; i < inactiveIds.size(); i++) {
                Integer id = inactiveIds.get(i);
                Integer position = inactivePositions.get(i);
                Status status = statusRepository.findById(id).orElse(null);
                if (status != null) {
                    status.setIsNotActivePositionNumber(position);
                    status.setIsActivePositionNumber(null);
                    statusRepository.save(status);
                }
            }
        }

        return "redirect:/";
    }

}
