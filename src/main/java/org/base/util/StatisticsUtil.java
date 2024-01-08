package org.base.util;

import org.base.dto.RecordData;

import java.util.List;

public class StatisticsUtil {

    public static int findPeronByMedianAgeRoundUp(List<RecordData> allData, double medianEvenAge) {
        int index = findPersonIndexByAge(allData, medianEvenAge);

        if (index == -1) {
            // Try rounding up
            double roundUpAge = Math.ceil(medianEvenAge);
            index = findPersonIndexByAge(allData, roundUpAge);
                if (index == -1) {
                    // The next person is checked by incrementing values. Considering the nature of
                    // even median values, rounding up first will always get the higher aged person.
                    // The client needs to decide whether to prioritize finding the older person first
                    // or the younger one.
                    double incrementedAge = roundUpAge + 1;
                    if (incrementedAge > medianEvenAge && incrementedAge <= 150) {
                        return findPeronByMedianAgeRoundUp(allData, incrementedAge);
                    }
                }
            }
            return index;
        }

    private static int findPersonIndexByAge(List<RecordData> allData, double targetAge) {
        for (int i = 0; i < allData.size(); i++) {
            if (allData.get(i).getAge() == targetAge) {
                return i;
            }
        }
        return -1;
    }
//    public static int findPeronByMedianAge(List<RecordData> allData, double medianEvenAge) {
//        int index = findPersonIndexByAge(allData, medianEvenAge);
//
//        if (index == -1) {
//            // Try rounding up
//            index = findPersonIndexByAge(allData, Math.ceil(medianEvenAge));
//
//            if (index == -1) {
//                // Try rounding down
//                index = findPersonIndexByAge(allData, Math.floor(medianEvenAge));
//
//                if (index == -1) {
//                    // Try incrementing and decrementing alternatively
//                    double incrementedAge = Math.ceil(medianEvenAge) + 1;
//                    double decrementedAge = Math.floor(medianEvenAge) - 1;
//                    boolean increment = true;
//
//                    while (index == -1 && (incrementedAge <= 150 || decrementedAge >= 0)) {
//                        if (increment) {
//                            index = findPersonIndexByAge(allData, incrementedAge);
//                            incrementedAge++;
//                        } else {
//                            index = findPersonIndexByAge(allData, decrementedAge);
//                            decrementedAge--;
//                        }
//                        increment = !increment;
//                    }
//                }
//            }
//        }
//
//        return index;
//    }
}
