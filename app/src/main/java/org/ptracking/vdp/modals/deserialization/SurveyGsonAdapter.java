package org.ptracking.vdp.modals.deserialization;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.ptracking.vdp.modals.Answer;
import org.ptracking.vdp.modals.FlowPattern;
import org.ptracking.vdp.modals.Option;
import org.ptracking.vdp.modals.Question;
import org.ptracking.vdp.modals.Survey;
import org.ptracking.vdp.modals.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by muthuveerappans on 19/02/18.
 */

public class SurveyGsonAdapter extends TypeAdapter<Survey> {
    @Override
    public void write(JsonWriter out, Survey value) throws IOException {
        out.beginObject();
        out.name("_id").value(value.getId());
        out.name("name").value(value.getName());
        out.name("description").value(value.getDescription());
        out.name("surveyorCode").value(value.getSurveyorCode());
        out.name("version").value(1);
        writeQuestion(out, value.getQuestion());
        out.endObject();
    }

    @Override
    public Survey read(JsonReader in) throws IOException {
        return readSurvey(in);
    }

    private void writeQuestion(JsonWriter out, Question node) throws IOException {
        out.name("question").beginObject();
        out.name("number").value(node.getNumber());
        out.name("answers").beginArray();
        for (Answer answer : node.getAnswers()) {
            out.beginObject();
            writeOptions(out, answer.getLoggedOptions());
            out.name("startTimestamp").value(answer.getStartTimeStamp());
            out.name("endTimestamp").value(answer.getExitTimestamp());
            out.name("children").beginArray();
            for (Question c : answer.getChildren()) {
                out.beginObject();
                out.name("position").value(c.getPosition());
                writeQuestion(out, c);
                out.endObject();
            }
            out.endArray();
            out.endObject();
        }
        out.endArray();
        out.endObject();
    }

    private void writeOptions(JsonWriter out, List<Option> options) throws IOException {
        out.name("logged_options").beginArray();
        for (Option o : options) {
            out.beginObject();
            out.name("position").value(o.getPosition());
            out.name("value").value(o.getValue());
            out.endObject();
        }
        out.endArray();
    }

    private Survey readSurvey(JsonReader in) throws IOException {
        String _id = "";
        String name = "";
        String description = "";
        boolean enabled = false;
        String modifiedAt = "";
        Question question = null;

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "_id":
                    _id = in.nextString();
                    break;
                case "name":
                    name = in.nextString();
                    break;
                case "description":
                    description = in.nextString();
                    break;
                case "enabled":
                    enabled = in.nextBoolean();
                    break;
                case "modifiedAt":
                    modifiedAt = in.nextString();
                    break;
                case "question":
                    question = readQuestion(in);
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();

        return new Survey(_id, name, description, question, modifiedAt, enabled);
    }

    private Question readQuestion(JsonReader in) throws IOException {
        Question question = new Question();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "options":
                    ArrayList<Option> options = new ArrayList<>();
                    in.beginArray();
                    while (in.hasNext())
                        options.add(readOption(in));
                    in.endArray();
                    question.setOptions(options);
                    break;
                case "children":
                    ArrayList<Question> children = new ArrayList<>();
                    in.beginArray();
                    while (in.hasNext()) {
                        Question child = null;
                        String position = "";

                        in.beginObject();
                        while (in.hasNext()) {
                            switch (in.nextName()) {
                                case "position":
                                    position = in.nextString();
                                    break;
                                case "question":
                                    child = readQuestion(in);
                                    break;
                                default:
                                    in.skipValue();
                                    break;
                            }
                        }
                        in.endObject();

                        if (child != null) {
                            child.setPosition(position);
                            child.setParent(question);
                            children.add(child);
                        }
                    }
                    in.endArray();
                    question.setChildren(children);
                    break;
                case "answers":
                    throw new UnsupportedOperationException("Answer parsing is not implemented.");
                case "flow":
                    question.setFlowPattern(readFlowPattern(in));
                    break;
                case "text":
                    question.setText(readText(in));
                    break;
                case "type":
                    question.setType(in.nextString());
                    break;
                case "tags":
                    question.setTags(new ArrayList<>(Arrays.asList(in.nextString().split(","))));
                    break;
                case "number":
                    question.setNumber(in.nextString());
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();

        return question;
    }

    private FlowPattern readFlowPattern(JsonReader in) throws IOException {
        FlowPattern flowPattern = new FlowPattern();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "pre":
                    FlowPattern.PreFlow preFlow = new FlowPattern.PreFlow();

                    in.beginObject();
                    while (in.hasNext()) {
                        switch (in.nextName()) {
                            case "fill":
                                in.beginArray();
                                ArrayList<String> fill = new ArrayList<>();
                                while (in.hasNext()) {
                                    fill.add(in.nextString());
                                }
                                preFlow.setFill(fill);
                                in.endArray();
                                break;
                            case "skipUnless":
                                FlowPattern.PreFlow.SkipUnless skipUnless = new FlowPattern.PreFlow.SkipUnless();
                                in.beginObject();
                                while (in.hasNext()) {
                                    switch (in.nextName()) {
                                        case "question":
                                            skipUnless.setQuestionNumber(in.nextString());
                                            break;
                                        case "option":
                                            ArrayList<String> o =
                                                    new ArrayList<>(Arrays.asList(in.nextString().split(",")));
                                            skipUnless.setSkipPositions(o);
                                            break;
                                        default:
                                            in.skipValue();
                                            break;
                                    }
                                }
                                preFlow.setSkipUnless(skipUnless);
                                in.endObject();
                                break;
                            default:
                                in.skipValue();
                                break;
                        }
                    }

                    in.endObject();

                    flowPattern.setPreFlow(preFlow);
                    break;
                case "question":
                    in.beginObject();
                    FlowPattern.QuestionFlow questionFlow = new FlowPattern.QuestionFlow();
                    while (in.hasNext()) {
                        switch (in.nextName()) {
                            case "validation":
                                if (in.peek() == JsonToken.NULL) {
                                    in.skipValue();
                                    break;
                                }
                                questionFlow.setValidation(FlowPattern.QuestionFlow.parseValidation(in.nextString()));
                                break;
                            case "ui":
                                if (in.peek() == JsonToken.NULL) {
                                    in.skipValue();
                                    break;
                                }
                                questionFlow.setUiMode(FlowPattern.QuestionFlow.parseUI(in.nextString()));
                                break;
                            case "back":
                                questionFlow.setBack(in.nextBoolean());
                                break;
                            case "optionsLimit":
                                questionFlow.setOptionsLimit(in.nextInt());
                                break;
                            case "showImage":
                                questionFlow.setShowImage(in.nextBoolean());
                                break;
                            case "validationLimit":
                                questionFlow.setValidationLimit(in.nextInt());
                                break;
                            case "validationDigitsLimit":
                                questionFlow.setValidationDigitsLimit(in.nextInt());
                                break;
                            default:
                                in.skipValue();
                                break;

                        }
                    }
                    in.endObject();

                    flowPattern.setQuestionFlow(questionFlow);
                    break;
                case "answer":
                    FlowPattern.AnswerFlow answerFlow = new FlowPattern.AnswerFlow();

                    in.beginObject();
                    while (in.hasNext()) {
                        switch (in.nextName()) {
                            case "scope":
                                answerFlow.setMode(FlowPattern.AnswerFlow.parseMode(in.nextString()));
                                break;
                            default:
                                in.skipValue();
                                break;
                        }
                    }
                    in.endObject();

                    flowPattern.setAnswerFlow(answerFlow);
                    break;
                case "child":
                    FlowPattern.ChildFlow childFlow = new FlowPattern.ChildFlow();

                    in.beginObject();
                    while (in.hasNext()) {
                        switch (in.nextName()) {
                            case "strategy":
                                childFlow.setStrategy(FlowPattern.ChildFlow.parseStrategy(in.nextString()));
                                break;
                            case "select":
                                in.beginObject();
                                while (in.hasNext()) {
                                    switch (in.nextName()) {
                                        case "ui":
                                            childFlow.setUiToBeShown(FlowPattern.ChildFlow.parseUI(in.nextString()));
                                            break;
                                        case "repeat":
                                            childFlow.setRepeatMode(FlowPattern.ChildFlow.parseRepeatMode(in.nextString()));
                                            break;
                                        default:
                                            in.skipValue();
                                            break;
                                    }
                                }
                                in.endObject();
                                break;
                        }
                    }
                    in.endObject();

                    flowPattern.setChildFlow(childFlow);
                    break;
                case "post":
                    ArrayList<String> tags = new ArrayList<>();
                    in.beginArray();
                    while (in.hasNext()) {
                        tags.add(in.nextString());
                    }
                    in.endArray();
                    FlowPattern.PostFlow postFlow = new FlowPattern.PostFlow();
                    postFlow.setTags(tags);
                    flowPattern.setPostFlow(postFlow);
                    break;
                case "exit":
                    FlowPattern.ExitFlow exitFlow = new FlowPattern.ExitFlow();

                    in.beginObject();
                    while (in.hasNext()) {
                        switch (in.nextName()) {
                            case "strategy":
                                exitFlow.setStrategy(FlowPattern.ExitFlow.parseStrategy(in.nextString()));
                                break;
                            case "incrementBubble":
                                exitFlow.setIncrementBubble(in.nextBoolean());
                                break;
                            default:
                                in.skipValue();
                                break;
                        }
                    }
                    in.endObject();

                    flowPattern.setExitFlow(exitFlow);
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();

        return flowPattern;
    }

    private Option readOption(JsonReader in) throws IOException {
        String position = "";
        String type = "";
        Text text = null;
        String imageData = "";

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "position":
                    position = in.nextString();
                    break;
                case "option":
                    in.beginObject();
                    while (in.hasNext()) {
                        switch (in.nextName()) {
                            case "type":
                                type = in.nextString();
                                break;
                            case "text":
                                text = readText(in);
                                break;
                            case "image":
                                in.beginObject();
                                while (in.hasNext()) {
                                    switch (in.nextName()) {
                                        case "data":
                                            imageData = in.nextString();
                                            break;
                                        default:
                                            in.skipValue();
                                            break;
                                    }
                                }
                                in.endObject();
                                break;
                            default:
                                in.skipValue();
                                break;
                        }
                    }
                    in.endObject();
                    break;
                default:
                    in.skipValue();
                    break;

            }
        }
        in.endObject();

        return new Option(type, text, position, imageData);
    }

    private Text readText(JsonReader in) throws IOException {
        String english = "";
        String tamil = "";

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "english":
                    english = in.nextString();
                    break;
                case "tamil":
                    tamil = in.nextString();
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();

        return new Text(english, tamil);
    }

}
