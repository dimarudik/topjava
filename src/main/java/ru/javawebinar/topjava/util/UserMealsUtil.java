package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
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
        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> userMealsWithExcess = new ArrayList<>();
/*
        Map<LocalDate, Integer> caloriesPerDayMap = new HashMap<>();
        Function<UserMeal, UserMealWithExcess> userMeal2UserMealWithExcess = userMeal -> new UserMealWithExcess(
                userMeal.getDateTime(),
                userMeal.getDescription(),
                userMeal.getCalories(),
                false);
        int i = 0;
        for (UserMeal meal : meals) {
            caloriesPerDayMap.putIfAbsent(meal.getDateTime().toLocalDate(), 0);
            caloriesPerDayMap.computeIfPresent(meal.getDateTime().toLocalDate(), (k, v) -> v + meal.getCalories());
            UserMealWithExcess withExcess = userMeal2UserMealWithExcess.apply(meal);
            if (meal.getDateTime().toLocalTime().isAfter(startTime) && meal.getDateTime().toLocalTime().isBefore(endTime)) {
                userMealsWithExcess.add(withExcess);
            }
            i = userMealsWithExcess.indexOf(withExcess);
            if (i != -1) {
                userMealsWithExcess.get(i).setCalories(caloriesPerDayMap.get(userMealsWithExcess.get(i).getDateTime().toLocalDate()));
            }
        }
*/
        return userMealsWithExcess;
    }

    // O(n*n)
    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Function<UserMeal, Integer> caloriesPerDayFunction = userMeal -> meals
                .parallelStream()
                .filter(meal -> userMeal.getDateTime().toLocalDate().equals(meal.getDateTime().toLocalDate()))
                .mapToInt(UserMeal::getCalories)
                .sum();
        Function<UserMeal, UserMealWithExcess> userMeal2UserMealWithExcess = userMeal -> new UserMealWithExcess(
                userMeal.getDateTime(),
                userMeal.getDescription(),
                userMeal.getCalories(),
                caloriesPerDay < caloriesPerDayFunction.apply(userMeal));
        return meals
                .parallelStream()
                .filter(meal -> meal.getDateTime().toLocalTime().isAfter(startTime)
                        && meal.getDateTime().toLocalTime().isBefore(endTime))
                .map(userMeal2UserMealWithExcess)
                .collect(Collectors.toList());
    }
}
