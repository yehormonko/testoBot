package yehor.monko.actions;

import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import yehor.monko.utils.ActionConstants;
import yehor.monko.SQLiteManager;
import yehor.monko.utils.StringConstants;
import yehor.monko.model.Answer;
import yehor.monko.model.EditPojo;
import yehor.monko.model.Question;
import yehor.monko.model.Test;

import java.util.ArrayList;
import java.util.List;

public class QuestionActions extends Action{
    private SQLiteManager manager = new SQLiteManager();
    public EditMessageText getQuestionInfo(String questionId, EditPojo pojo){
        Question question = manager.getQuestion(questionId);
        Test test = manager.getTest(question.getTestId());
        String text = "Тест: \""+test.getTitle()+"\""+System.lineSeparator()
                +question.getQuestionText()+System.lineSeparator();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for(int i = 0; i<question.getAnswers().size();i++){
            Answer answer = question.getAnswers().get(i);
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton answerNum = new InlineKeyboardButton((i+1)+"✏");
            answerNum.setCallbackData("update_answer|"+answer.getId()+"&"+questionId);
            row.add(answerNum);
            InlineKeyboardButton correct = new InlineKeyboardButton(" "+(answer.isCorrect() ? "✅":"❌"));
            correct.setCallbackData("upd_answ_co|"+answer.getId()+"&"+questionId);
            row.add(correct);
            InlineKeyboardButton delete = new InlineKeyboardButton("\uD83D\uDDD1");
            delete.setCallbackData("delete_answer|"+answer.getId()+"&"+questionId);
            row.add(delete);
            rows.add(row);
            text+=i+1+"). "+answer.getText()+System.lineSeparator();
        }
        if(question.getAnswers().size()<4) {
            rows.add(getRow(StringConstants.ADD_ANSWER, ActionConstants.ADD_ANSWER+"|"+questionId));
        }
        rows.add(getRow("Редагувати питання","edit_q|"+questionId));
        rows.add(getRow(StringConstants.BACK_TO_TEST, ActionConstants.SEE_TEST+"|"+test.getId()));
        EditMessageText editMessageText = getEditMessage(pojo, getMarkup(rows));
        editMessageText.setText(text);
        return editMessageText;
    }
    public EditMessageText addQuestion(Question question, EditPojo pojo){
        manager.addQuestion(question);
       return getQuestionInfo(question.getId(), pojo);
    }
    public EditMessageText addAnswer(Answer answer, EditPojo pojo){
        manager.addAnswer(answer);
        return getQuestionInfo(answer.getQuestionId(), pojo);
    }
    public EditMessageText changeAnswer(Answer answer, EditPojo pojo){
        manager.editAnswer(answer);
        return getQuestionInfo(answer.getQuestionId(), pojo);
    }
    public EditMessageText changeAnswerCorrect(Answer answer, EditPojo pojo){
        manager.editAnswerCorrect(answer);
        return getQuestionInfo(answer.getQuestionId(), pojo);
    }
    public EditMessageText deleteAnswer(Answer answer, EditPojo pojo){
        manager.deleteAnswer(answer.getId());
        return getQuestionInfo(answer.getQuestionId(), pojo);
    }
    public EditMessageText editQuestion(Question question, EditPojo pojo){
        manager.editQuestion(question);
        return getQuestionInfo(question.getId(), pojo);
    }

}
