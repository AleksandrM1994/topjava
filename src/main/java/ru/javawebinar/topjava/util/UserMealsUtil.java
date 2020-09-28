package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000).forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        HashMap<LocalDate, Integer> userMealMap = new HashMap<>();
        for (UserMeal userMeal : meals) {
            userMealMap.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum);
        }

        List<UserMealWithExcess> mealsWithExcesses = new ArrayList<>();
        for (UserMeal userMeal : meals) {
            boolean isExcess = false;
            if (userMealMap.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay) {
                isExcess = true;
            }

            if (TimeUtil.isBetweenHalfOpen(LocalTime.from(userMeal.getDateTime()), startTime, endTime)) {
                mealsWithExcesses.add(new UserMealWithExcess(
                        userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), isExcess));
            }
        }
        return mealsWithExcesses;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> userMealMap = meals.stream()
                .collect(Collectors.groupingBy(a -> a.getDateTime().toLocalDate(),
                        Collectors.summingInt(UserMeal::getCalories)));

        List<UserMealWithExcess> userMealList = meals.stream()
                .filter(m -> m.getDateTime().getHour() > startTime.getHour() &&
                        m.getDateTime().getHour() < endTime.getHour())
                .map(
                        um -> {
                            UserMealWithExcess mealsWithExcess = null;
                            if (userMealMap.get(um.getDateTime().toLocalDate()) > caloriesPerDay) {
                                mealsWithExcess = new UserMealWithExcess(um.getDateTime(), um.getDescription(),
                                        um.getCalories(), true);
                            } else {
                                mealsWithExcess = new UserMealWithExcess(um.getDateTime(), um.getDescription(),
                                        um.getCalories(), false);
                            }
                            return mealsWithExcess;
                        }
                )
                .collect(Collectors.toList());

        return userMealList;
    }
}
