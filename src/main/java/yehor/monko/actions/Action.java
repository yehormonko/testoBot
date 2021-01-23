package yehor.monko.actions;

import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import yehor.monko.model.EditPojo;

import java.util.ArrayList;
import java.util.List;

public abstract class Action {
    public List<InlineKeyboardButton> getRow(String text, String callback){
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton(text);
        back.setCallbackData(callback);
        row.add(back);
        return row;
    }

    public EditMessageText getEditMessage(EditPojo pojo, InlineKeyboardMarkup markup){
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(pojo.getMessageId());
        editMessageText.setChatId(pojo.getChatId());
        editMessageText.setReplyMarkup(markup);
        return editMessageText;
    }
    public InlineKeyboardMarkup getMarkup(List<List<InlineKeyboardButton>> rows){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }
}
