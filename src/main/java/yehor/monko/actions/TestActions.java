package yehor.monko.actions;

import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import yehor.monko.utils.ActionConstants;
import yehor.monko.SQLiteManager;
import yehor.monko.utils.StringConstants;
import yehor.monko.model.EditPojo;
import yehor.monko.model.Question;
import yehor.monko.model.Test;

import java.util.ArrayList;
import java.util.List;

public class TestActions extends Action {
    private SQLiteManager manager = new SQLiteManager();

    public EditMessageText getTestInfo(String testId, EditPojo pojo) {
        Test test = manager.getTest(testId);
        String text = test.getTitle() + System.lineSeparator()+"❓Питання❓"+System.lineSeparator();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int i = 0; i < test.getQuestions().size(); i++) {
            Question question = test.getQuestions().get(i);
            text += i + 1 + "). " + question.getQuestionText() + System.lineSeparator();
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton see = new InlineKeyboardButton(i + 1 + " ✏");
            see.setCallbackData("get_question|" + question.getId());
            row.add(see);
            InlineKeyboardButton delete = new InlineKeyboardButton("\uD83D\uDDD1");
            delete.setCallbackData("delete_question|" + question.getId() + "&" + testId);
            row.add(delete);
            rows.add(row);
        }
        if (test.getQuestions().size() < 15) {
            rows.add(getRow(StringConstants.ADD_QUESTION, ActionConstants.ADD_QUESTION + "|" + testId));
        }
        rows.add(getRow(StringConstants.TEST_ID, ActionConstants.SHOW_ID+"|"+testId));
        rows.add(getRow(StringConstants.TEST_RESULTS, ActionConstants.SHOW_TEST_RESULTS+"|"+testId));
        rows.add(getRow(StringConstants.EDIT_TEST_NAME, ActionConstants.EDIT_TEST + "|" + testId));
        rows.add(getRow(StringConstants.DELETE_TEST, ActionConstants.DELETE_TEST + "|" + testId));
        rows.add(getRow(StringConstants.BACK_TO_TEST_LIST, ActionConstants.MY_TESTS));
        EditMessageText editMessageText = getEditMessage(pojo, getMarkup(rows));
        editMessageText.setText(text);
        return editMessageText;
    }


    public EditMessageText createTest(Test test, EditPojo pojo) {
        manager.addTest(test);
        return getTestInfo(test.getId(), pojo);
    }

    public EditMessageText editTest(Test test, EditPojo pojo) {
        manager.editTest(test);
        return getTestInfo(test.getId(), pojo);
    }

    public EditMessageText deleteQuestion(String testId, String questionId, EditPojo pojo) {
        manager.deleteQuestion(questionId);
        return getTestInfo(testId, pojo);
    }

    public EditMessageText showId(String testId, EditPojo pojo) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(getRow(StringConstants.BACK_TO_TEST, ActionConstants.SEE_TEST + "|" + testId));
        EditMessageText editMessageText = getEditMessage(pojo, getMarkup(rows));
        editMessageText.setText(testId);
        return editMessageText;
    }


}
