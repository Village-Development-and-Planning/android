package com.puthuvaazhvu.mapping.modals;

import java.io.Serializable;
import java.util.ArrayList;

import static com.puthuvaazhvu.mapping.modals.FlowPattern.QuestionFlow.Validation.NONE;
import static com.puthuvaazhvu.mapping.modals.FlowPattern.QuestionFlow.Validation.NUMBER;
import static com.puthuvaazhvu.mapping.modals.FlowPattern.QuestionFlow.Validation.TEXT;

/**
 * Created by muthuveerappans on 19/02/18.
 */

public class FlowPattern extends BaseObject implements Serializable {
    private PreFlow preFlow;
    private AnswerFlow answerFlow;
    private ChildFlow childFlow;
    private ExitFlow exitFlow;
    private PostFlow postFlow;
    private QuestionFlow questionFlow;

    public PreFlow getPreFlow() {
        return preFlow;
    }

    public void setPreFlow(PreFlow preFlow) {
        this.preFlow = preFlow;
    }

    public AnswerFlow getAnswerFlow() {
        return answerFlow;
    }

    public void setAnswerFlow(AnswerFlow answerFlow) {
        this.answerFlow = answerFlow;
    }

    public ChildFlow getChildFlow() {
        return childFlow;
    }

    public void setChildFlow(ChildFlow childFlow) {
        this.childFlow = childFlow;
    }

    public ExitFlow getExitFlow() {
        return exitFlow;
    }

    public void setExitFlow(ExitFlow exitFlow) {
        this.exitFlow = exitFlow;
    }

    public PostFlow getPostFlow() {
        return postFlow;
    }

    public void setPostFlow(PostFlow postFlow) {
        this.postFlow = postFlow;
    }

    public QuestionFlow getQuestionFlow() {
        return questionFlow;
    }

    public void setQuestionFlow(QuestionFlow questionFlow) {
        this.questionFlow = questionFlow;
    }

    public static class AnswerFlow extends BaseObject implements Serializable {
        public enum Modes {
            NONE, ONCE, OPTION, MULTIPLE
        }

        private Modes mode = Modes.NONE;

        public Modes getMode() {
            return mode;
        }

        public void setMode(Modes mode) {
            this.mode = mode;
        }

        public static Modes parseMode(String mode) {
            if (mode == null) {
                return Modes.NONE;
            }

            switch (mode) {
                case "once":
                    return Modes.ONCE;
                case "options":
                    return Modes.OPTION;
                case "multiple":
                    return Modes.MULTIPLE;
                default:
                    return Modes.NONE;
            }
        }
    }

    public static class ChildFlow extends BaseObject implements Serializable {
        private Strategy strategy = Strategy.NONE;
        private UI uiToBeShown = UI.NONE;
        private RepeatMode repeatMode = RepeatMode.NONE;

        public enum Strategy {
            NONE, CASCADE, SELECT, TOGETHER
        }

        public enum RepeatMode {
            NONE, ONCE, MULTIPLE
        }

        public enum UI {
            NONE, GRID;
        }

        public Strategy getStrategy() {
            return strategy;
        }

        public void setStrategy(Strategy strategy) {
            this.strategy = strategy;
        }

        public UI getUiToBeShown() {
            return uiToBeShown;
        }

        public void setUiToBeShown(UI uiToBeShown) {
            this.uiToBeShown = uiToBeShown;
        }

        public RepeatMode getRepeatMode() {
            return repeatMode;
        }

        public void setRepeatMode(RepeatMode repeatMode) {
            this.repeatMode = repeatMode;
        }

        public static Strategy parseStrategy(String mode) {
            if (mode == null) {
                return Strategy.NONE;
            }

            switch (mode) {
                case "cascade":
                    return Strategy.CASCADE;
                case "together":
                    return Strategy.TOGETHER;
                case "select":
                    return Strategy.SELECT;
                default:
                    return Strategy.NONE;
            }
        }

        public static UI parseUI(String ui) {
            if (ui == null) {
                return UI.NONE;
            }

            switch (ui) {
                case "grid":
                    return UI.GRID;
                default:
                    return UI.NONE;
            }
        }

        public static RepeatMode parseRepeatMode(String rmode) {
            if (rmode == null) {
                return RepeatMode.NONE;
            }

            switch (rmode) {
                case "once":
                    return RepeatMode.ONCE;
                case "multiple":
                    return RepeatMode.MULTIPLE;
                default:
                    return RepeatMode.NONE;
            }
        }
    }

    public static class ExitFlow extends BaseObject implements Serializable {
        public enum Strategy {
            NONE, PARENT, LOOP, END
        }

        private Strategy strategy = Strategy.NONE;
        private boolean incrementBubble;

        public Strategy getStrategy() {
            return strategy;
        }

        public void setStrategy(Strategy mode) {
            this.strategy = mode;
        }

        public boolean isIncrementBubble() {
            return incrementBubble;
        }

        public void setIncrementBubble(boolean incrementBubble) {
            this.incrementBubble = incrementBubble;
        }

        public static Strategy parseStrategy(String mode) {
            if (mode == null) {
                return Strategy.NONE;
            }

            switch (mode) {
                case "parent":
                    return Strategy.PARENT;
                case "LOOP":
                    return Strategy.LOOP;
                case "END":
                    return Strategy.END;
                default:
                    return Strategy.NONE;
            }
        }
    }

    public static class PostFlow extends BaseObject implements Serializable {
        ArrayList<String> tags = new ArrayList<>();

        public ArrayList<String> getTags() {
            return tags;
        }

        public void setTags(ArrayList<String> tags) {
            this.tags = tags;
        }
    }

    public static class QuestionFlow extends BaseObject implements Serializable {
        public enum Validation {
            NONE, NUMBER, TEXT
        }

        public enum UI {
            NONE, SINGLE_CHOICE, MULTIPLE_CHOICE, GPS, INPUT, INFO, CONFIRMATION, MESSAGE, DUMMY
        }

        private Validation validation = NONE;
        private UI uiMode = UI.NONE;
        private boolean back = true;
        private int optionsLimit;
        private boolean showImage;
        private int validationDigitsLimit;
        private int validationLimit;

        public Validation getValidation() {
            return validation;
        }

        public void setValidation(Validation validation) {
            this.validation = validation;
        }

        public UI getUiMode() {
            return uiMode;
        }

        public void setUiMode(UI uiMode) {
            this.uiMode = uiMode;
        }

        public boolean isBack() {
            return back;
        }

        public void setBack(boolean back) {
            this.back = back;
        }

        public int getOptionsLimit() {
            return optionsLimit;
        }

        public void setOptionsLimit(int optionsLimit) {
            this.optionsLimit = optionsLimit;
        }

        public boolean isShowImage() {
            return showImage;
        }

        public void setShowImage(boolean showImage) {
            this.showImage = showImage;
        }

        public int getValidationDigitsLimit() {
            return validationDigitsLimit;
        }

        public void setValidationDigitsLimit(int validationDigitsLimit) {
            this.validationDigitsLimit = validationDigitsLimit;
        }

        public int getValidationLimit() {
            return validationLimit;
        }

        public void setValidationLimit(int validationLimit) {
            this.validationLimit = validationLimit;
        }

        public static UI parseUI(String ui) {
            if (ui == null) {
                return UI.NONE;
            }

            switch (ui) {
                case "SINGLE_CHOICE":
                    return UI.SINGLE_CHOICE;
                case "MULTIPLE_CHOICE":
                    return UI.MULTIPLE_CHOICE;
                case "GPS":
                    return UI.GPS;
                case "INPUT":
                    return UI.INPUT;
                case "INFO":
                    return UI.INFO;
                case "CONFIRMATION":
                    return UI.CONFIRMATION;
                case "MESSAGE":
                    return UI.MESSAGE;
                default:
                    return UI.NONE;
            }
        }

        public static Validation parseValidation(String validation) {
            if (validation == null) {
                return NONE;
            }

            switch (validation) {
                case "[0-9]+":
                    return NUMBER;
                case "TEXT":
                    return TEXT;
                default:
                    return NONE;
            }
        }
    }

    public static class PreFlow extends BaseObject implements Serializable {
        private ArrayList<String> fill;
        private SkipUnless skipUnless;

        public ArrayList<String> getFill() {
            return fill;
        }

        public void setFill(ArrayList<String> fill) {
            this.fill = fill;
        }

        public SkipUnless getSkipUnless() {
            return skipUnless;
        }

        public void setSkipUnless(SkipUnless skipUnless) {
            this.skipUnless = skipUnless;
        }

        public static class SkipUnless extends BaseObject {
            private String questionNumber;
            private ArrayList<String> skipPositions;

            public String getQuestionNumber() {
                return questionNumber;
            }

            public void setQuestionNumber(String questionNumber) {
                this.questionNumber = questionNumber;
            }

            public ArrayList<String> getSkipPositions() {
                return skipPositions;
            }

            public void setSkipPositions(ArrayList<String> skipPositions) {
                this.skipPositions = skipPositions;
            }
        }
    }
}
