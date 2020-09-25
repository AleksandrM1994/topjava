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

        Collections.sort(meals, new Comparator<UserMeal>() {
            @Override
            public int compare(UserMeal o1, UserMeal o2) {
                return o1.getDateTime().compareTo(o2.getDateTime());
            }
        });

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000).forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        HashMap<LocalDate, Integer> map = new HashMap<>();
        for (UserMeal userMeal : meals) {
            map.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), (oldVal, newVal) -> oldVal + newVal);
        }

        List<UserMealWithExcess> mealsWithExcesses = new ArrayList<>();
        for (UserMeal userMeal : meals) {
            if (TimeUtil.isBetweenHalfOpen(LocalTime.from(userMeal.getDateTime()), startTime, endTime) &&
                    map.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay) {
                mealsWithExcesses.add(new UserMealWithExcess(
                        userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), true));
            } else if (TimeUtil.isBetweenHalfOpen(LocalTime.from(userMeal.getDateTime()), startTime, endTime) &&
                    map.get(userMeal.getDateTime().toLocalDate()) <= caloriesPerDay) {
                mealsWithExcesses.add(new UserMealWithExcess(
                        userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), false));
            } else {
                continue;
            }
        }
        return mealsWithExcesses;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> list = meals.stream().filter(x -> x.getDateTime().getHour() > startTime.getHour() &&
                x.getDateTime().getHour() < endTime.getHour()).map(
                x -> {
                    UserMealWithExcess mealsWithExcess = null;
                    if (meals.stream().collect(Collectors.groupingBy(a -> a.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories))).
                            get(x.getDateTime().toLocalDate()) > caloriesPerDay) {
                        mealsWithExcess = new UserMealWithExcess(x.getDateTime(), x.getDescription(),
                                x.getCalories(), true);
                    } else {
                        mealsWithExcess = new UserMealWithExcess(x.getDateTime(), x.getDescription(),
                                x.getCalories(), false);
                    }
                    return mealsWithExcess;
                }
        ).collect(Collectors.toList());

        return list;
    }
}
