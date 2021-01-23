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

public class PassTestActions extends Action {
    private SQLiteManager manager = new SQLiteManager();

    public EditMessageText getTestInfo(String testId, EditPojo pojo) {
        Test test = manager.getValidTest(testId);
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        String text = "Такого тесту не існує";
        if (test != null) {
            text = test.getTitle() + System.lineSeparator() + "Питань: " + test.getQuestions().size();
            rows.add(getRow(StringConstants.PASS_TEST, ActionConstants.INPUT_NAME + "|" + testId));
        } else {
            rows.add(getRow(StringConstants.PASS_TEST, ActionConstants.FIND_TEST));
        }
        rows.add(getRow(StringConstants.MAIN_MENU, ActionConstants.MAIN_MENU));
        EditMessageText editMessageText = getEditMessage(pojo, getMarkup(rows));
        editMessageText.setText(text);
        return editMessageText;
    }


    public EditMessageText getQuestion(String testId, EditPojo pojo, String userId, int score, int num) {
        if (score != 0) manager.addCorrectAnswRes(testId, userId);
        Test test = manager.getValidTest(testId);
        if (num == test.getQuestions().size()) {
            return getResults(testId, pojo, userId);
        }
        String text = test.getTitle() + System.lineSeparator() + (num + 1) + "/" + test.getQuestions().size() + System.lineSeparator();
        Question question = manager.getQuestion(test.getQuestions().get(num).getId());
        text += question.getQuestionText();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Answer answer : question.getAnswers()) {
            String callback;
            if (answer.isCorrect()) {
                callback = ActionConstants.CORRECT + "|" + testId + "&" + (num + 1);
            } else {
                callback = ActionConstants.WRONG + "|" + testId + "&" + (num + 1);

            }
            rows.add(getRow(answer.getText(), callback));
        }
        rows.add(getRow(StringConstants.END_TEST, ActionConstants.END_TEST + "|" + testId));
        EditMessageText editMessageText = getEditMessage(pojo, getMarkup(rows));
        editMessageText.setText(text);
        return editMessageText;
    }

    public EditMessageText getResults(String testId, EditPojo pojo, String userId) {
        Test test = manager.getValidTest(testId);
        String text = test.getTitle() + System.lineSeparator();
        text += manager.getResultsForTestAndUser(testId, userId) + "/" + test.getQuestions().size();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(getRow(StringConstants.MAIN_MENU, ActionConstants.MAIN_MENU));
        EditMessageText editMessageText = getEditMessage(pojo, getMarkup(rows));
        editMessageText.setText(text);
        return editMessageText;
    }

    public EditMessageText getFirsQuestion(String testId, EditPojo pojo, String userId, String name) {
        manager.initiateRes(testId, userId, name);
        return getQuestion(testId, pojo, userId, 0, 0);
    }
}
