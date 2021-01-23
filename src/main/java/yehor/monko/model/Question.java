package yehor.monko.model;

import java.util.ArrayList;

public class Question {
    private String questionText;
    private String id;
    private String testId;
    private ArrayList<Answer> answers = new ArrayList<>();

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    public Question() {
    }

    public Question(String questionText, String id) {
        this.questionText = questionText;
        this.id = id;
    }

    public Question(String questionText, String id, String testId) {
        this.questionText = questionText;
        this.id = id;
        this.testId = testId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }
}
