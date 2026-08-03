package com.deep.specification;

import com.deep.enums.FlightStatus;
import com.deep.model.FlightInstance;
import com.deep.payload.request.FlightSearchRequest;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FlightInstanceSpecification {

    private static final Set<FlightStatus> EXCLUDED_STATUSES =
            Set.of(FlightStatus.CANCELLED, FlightStatus.COMPLETED, FlightStatus.DIVERTED);

    private FlightInstanceSpecification() {}

    public static Specification<FlightInstance> buildSearchSpec(FlightSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isTrue(root.get("isActive")));
            predicates.add(root.get("status").in(EXCLUDED_STATUSES).not());
            predicates.add(cb.greaterThan(root.get("departureDateTime"), LocalDateTime.now()));

            if (request.getDepartureAirportId() != null)
                predicates.add(cb.equal(root.get("departureAirportId"), request.getDepartureAirportId()));
            if (request.getArrivalAirportId() != null)
                predicates.add(cb.equal(root.get("arrivalAirportId"), request.getArrivalAirportId()));

            if (request.getDepartureDate() != null) {
                LocalDateTime start = request.getDepartureDate().atStartOfDay();
                LocalDateTime end = request.getDepartureDate().atTime(LocalTime.MAX);
                predicates.add(cb.between(root.get("departureDateTime"), start, end));
            }

            if (request.getPassengers() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("availableSeats"), request.getPassengers()));

            if (request.getAirlines() != null && !request.getAirlines().isEmpty())
                predicates.add(root.get("airlineId").in(request.getAirlines()));

            if (isFilterableTimeRange(request.getDepartureTimeRange()))
                applyTimeRangePredicate(predicates, root, cb, "departureDateTime", request.getDepartureTimeRange());
            if (isFilterableTimeRange(request.getArrivalTimeRange()))
                applyTimeRangePredicate(predicates, root, cb, "arrivalDateTime", request.getArrivalTimeRange());

            if (request.getMaxDuration() != null) {
                Expression<Integer> durationMinutes = cb.function(
                        "TIMESTAMPDIFF", Integer.class,
                        cb.literal("MINUTE"), root.get("departureDateTime"), root.get("arrivalDateTime"));
                predicates.add(cb.lessThanOrEqualTo(durationMinutes, request.getMaxDuration()));
            }

            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static boolean isFilterableTimeRange(String range) {
        return range != null && !range.isBlank() && !range.equalsIgnoreCase("any");
    }

    private static void applyTimeRangePredicate(
            List<Predicate> predicates, Root<FlightInstance> root, CriteriaBuilder cb,
            String field, String range) {
        Expression<Integer> hour = cb.function("HOUR", Integer.class, root.get(field));
        switch (range.toLowerCase()) {
            case "morning" -> predicates.add(cb.between(hour, 6, 11));
            case "afternoon" -> predicates.add(cb.between(hour, 12, 17));
            case "evening" -> predicates.add(cb.between(hour, 18, 20));
            case "night" -> predicates.add(cb.or(cb.greaterThanOrEqualTo(hour, 21), cb.lessThanOrEqualTo(hour, 5)));
        }
    }
}