package com.puthuvaazhvu.mapping.Surveyour;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 8/31/17.
 */
public class DetailsActivityPresenter {
    DetailsActivityCommunicationInterface communicationInterface;

    public DetailsActivityPresenter(DetailsActivityCommunicationInterface communicationInterface) {
        this.communicationInterface = communicationInterface;
    }

    public void fetchData() {
        communicationInterface.onSurveyorDetailsFetched(getDummyData());
    }

    private ArrayList<SurveyorDetailsModal> getDummyData() {
        ArrayList<SurveyorDetailsModal> surveyorDetailsModals = new ArrayList<>();
        String[] testData = new String[]{"CST Name", "CST Code", "District Code", "District Name",
                "Block Code", "Cluster Code", "Panchayat Code", "Panchayat Name"};
        for (int i = 0; i < testData.length; i++) {
            surveyorDetailsModals.add(new SurveyorDetailsModal(testData[i], "Dummy", String.valueOf(i)));
        }
        return surveyorDetailsModals;
    }
}
