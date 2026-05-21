package com.example.flightapp.flight.api;

import com.example.flightapp.flight.domain.Flight;
import java.util.ArrayList;
import java.util.List;

public class FlightPageResponse {

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private List<FlightResponse> items;

    public static FlightPageResponse from(List<Flight> flights, int page, int size, long totalElements) {
        FlightPageResponse response = new FlightPageResponse();
        response.page = page;
        response.size = size;
        response.totalElements = totalElements;
        response.totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalElements / (double) size);
        response.items = new ArrayList<FlightResponse>();
        for (Flight flight : flights) {
            response.items.add(FlightResponse.from(flight));
        }
        return response;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<FlightResponse> getItems() {
        return items;
    }
}
