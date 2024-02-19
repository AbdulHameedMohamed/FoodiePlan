package com.abdulhameed.foodieplan.home.details;

import com.abdulhameed.foodieplan.model.data.Step;

import java.util.ArrayList;
import java.util.List;

public class InstructionsConverter {
    public static List<Step> convertStringToInstructions(String stepsString) {
        List<Step> stepList = new ArrayList<>();
        String[] stepsArray = stepsString.split("\r\n");
        for (int i = 0; i < stepsArray.length; i++) {
            stepList.add(new Step(i + 1, stepsArray[i]));
        }
        return stepList;
    }
}