package yehor.monko.actions;

import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import yehor.monko.utils.ActionConstants;
import yehor.monko.SQLiteManager;
import yehor.monko.utils.StringConstants;
import yehor.monko.model.EditPojo;
import yehor.monko.model.Test;

import java.util.ArrayList;
import java.util.List;

public class TestsListAction extends Action{
    private SQLiteManager manager = new SQLiteManager();
    public EditMessageText getTestsList(String ownerId, EditPojo pojo){
        List<Test> testList = manager.getTestList(ownerId);
        String text = "Ваші тести:"+System.lineSeparator();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Test test:testList) {
            rows.add(getRow(test.getTitle(), ActionConstants.SEE_TEST+"|"+test.getId()));
        }
        rows.add(getRow(StringConstants.MAIN_MENU,ActionConstants.MAIN_MENU));
        EditMessageText editMessageText = getEditMessage(pojo, getMarkup(rows));
        editMessageText.setText(text);
        return editMessageText;
    }
    public EditMessageText deleteTest(String testId, String ownerId, EditPojo pojo){
        Test test = manager.getTest(testId);
        manager.deleteTest(test);
        return getTestsList(ownerId, pojo);
    }

}
