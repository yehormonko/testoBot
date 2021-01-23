package yehor.monko.actions;

import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import yehor.monko.utils.ActionConstants;
import yehor.monko.SQLiteManager;
import yehor.monko.utils.StringConstants;
import yehor.monko.model.EditPojo;

import java.util.ArrayList;
import java.util.List;

public class ResultsAction extends Action{
    private SQLiteManager manager = new SQLiteManager();

    public EditMessageText getResultsForTest(String testId, EditPojo pojo){
        ArrayList<String> resultsForTest = manager.getResultsForTest(testId);
        String text = "Результати тесту:"+System.lineSeparator();
        for(String s:resultsForTest){
            text+=s+System.lineSeparator();
        }
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(getRow(StringConstants.BACK_TO_TEST, ActionConstants.SEE_TEST+"|"+testId));
        EditMessageText editMessageText = getEditMessage(pojo, getMarkup(rows));
        editMessageText.setText(text);
        return editMessageText;
    }

    public EditMessageText getResultsForUser(String userId, EditPojo pojo){
        ArrayList<String> resultsForTest = manager.getResultsForUsers(userId);
        String text = "Пройдені тести:"+System.lineSeparator();
        for(String s:resultsForTest){
            text+=s+System.lineSeparator();
        }
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(getRow(StringConstants.MAIN_MENU, ActionConstants.MAIN_MENU));
        EditMessageText editMessageText = getEditMessage(pojo, getMarkup(rows));
        editMessageText.setText(text);
        return editMessageText;
    }
}
