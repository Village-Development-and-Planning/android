package org.ptracking.vdp.filestorage.modals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by muthuveerappans on 18/02/18.
 */

public class AnswerInfo implements Serializable {
    private ArrayList<Answer> answers;

    public AnswerInfo() {
        answers = new ArrayList<>();
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    public Answer getAnswer(String id) {
        for (Answer answer : answers) {
            if (answer.getAnswerID().equals(id)) {
                return answer;
            }
        }
        return null;
    }

    public int getAnswersCount(String surveyID) {
        int count = 0;
        for (Answer answer : answers) {
            if (answer.getSurveyID().equals(surveyID)) {
                count++;
            }
        }
        return count;
    }

    public boolean removeAnswer(String id) {
        Iterator<Answer> answerIterator = answers.iterator();
        while (answerIterator.hasNext()) {
            if (answerIterator.next().getAnswerID().equals(id)) {
                answerIterator.remove();
                return true;
            }
        }
        return false;
    }

    public static class Answer implements Serializable {
        private String surveyID;
        private String answerID;
        private String surveyName;
        private long timeStamp;

        public String getAnswerID() {
            return answerID;
        }

        public void setAnswerID(String answerID) {
            this.answerID = answerID;
        }

        public String getSurveyID() {
            return surveyID;
        }

        public void setSurveyID(String surveyID) {
            this.surveyID = surveyID;
        }

        public String getSurveyName() {
            return surveyName;
        }

        public void setSurveyName(String surveyName) {
            this.surveyName = surveyName;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }
    }
}
