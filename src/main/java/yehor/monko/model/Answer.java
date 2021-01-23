package yehor.monko.model;

public class Answer {
    private boolean isCorrect;
    private String text;
    private String id;
    private String questionId;

    public Answer() {
    }

    public Answer(String text, String id, String questionId) {
        this.text = text;
        this.id = id;
        this.questionId = questionId;
        isCorrect = false;
    }

    public Answer(String id, String questionId) {
        this.id = id;
        this.questionId = questionId;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
}
