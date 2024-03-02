package com.abdulhameed.foodieplan.home.details.view;

import com.abdulhameed.foodieplan.model.data.Step;

import java.util.ArrayList;
import java.util.List;

public class InstructionsConverter {
    public static List<Step> convertStringToInstructions(String stepsString) {
        List<Step> stepList = new ArrayList<>();
        String[] stepsArray = stepsString.split("\r\n+");
        int stepIndex= 0;
        for (String s : stepsArray) {
            String stepText = s.trim();
            if (!stepText.isEmpty() && stepText.length() > 10)
                stepList.add(new Step(++stepIndex, stepText));
        }
        return stepList;
    }
}