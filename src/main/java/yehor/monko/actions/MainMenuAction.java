package yehor.monko.actions;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import yehor.monko.utils.ActionConstants;
import yehor.monko.utils.StringConstants;
import yehor.monko.model.EditPojo;

import java.util.ArrayList;
import java.util.List;

public class MainMenuAction extends Action {
    private String text = "Головне меню";


    public SendMessage showMainMenu(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(mainMarkup(chatId));
        return sendMessage;
    }

    public EditMessageText getMainMessage(EditPojo pojo){
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(getRow(StringConstants.PASS_TEST, ActionConstants.FIND_TEST));
        rows.add(getRow(StringConstants.PASSED_TESTS, ActionConstants.MY_RESULTS));
        rows.add(getRow(StringConstants.CRATE_TEST, ActionConstants.CREATE_TEST));
        rows.add(getRow(StringConstants.MY_TESTS,ActionConstants.MY_TESTS));
        EditMessageText editMessage = getEditMessage(pojo, getMarkup(rows));
        editMessage.setText(text);
        return editMessage;
    }
    private InlineKeyboardMarkup mainMarkup(String chatId) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(getRow(StringConstants.PASS_TEST, ActionConstants.FIND_TEST));
        rows.add(getRow(StringConstants.PASSED_TESTS, ActionConstants.MY_RESULTS));
        rows.add(getRow(StringConstants.CRATE_TEST, ActionConstants.CREATE_TEST));
        rows.add(getRow(StringConstants.MY_TESTS,ActionConstants.MY_TESTS));
        return getMarkup(rows);
    }
}
