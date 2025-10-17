package ru.practicum.shareit.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    // =============== createRequest ===============

    @Test
    void createRequest_ShouldReturnCreatedRequest() throws Exception {
        String description = "Need a drill";
        ItemRequestDto responseDto = new ItemRequestDto();
        responseDto.setId(1L);
        responseDto.setDescription(description);

        when(itemRequestService.createItemRequest(eq(1L), eq(description))).thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(description))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    // =============== getUserRequests ===============

    @Test
    void getUserRequests_ShouldReturnRequests() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Need a ladder");

        when(itemRequestService.getUserRequests(eq(1L))).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Need a ladder"));
    }

    // =============== getAllRequests ===============

    @Test
    void getAllRequests_ShouldReturnAllRequests() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(2L);
        requestDto.setDescription("Need a saw");

        when(itemRequestService.getAllRequests(eq(1L))).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].description").value("Need a saw"));
    }

    // =============== getRequestById ===============

    @Test
    void getRequestById_ShouldReturnRequest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(3L);
        requestDto.setDescription("Need a hammer");

        when(itemRequestService.getRequestById(eq(3L), eq(1L))).thenReturn(requestDto);

        mockMvc.perform(get("/requests/3")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.description").value("Need a hammer"));
    }
}