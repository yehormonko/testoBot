package yehor.monko.actions;

import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import yehor.monko.utils.StringConstants;
import yehor.monko.model.EditPojo;

import java.util.ArrayList;
import java.util.List;

public class InputActions extends Action{

    public EditMessageText inputAction(EditPojo pojo, String text, String callback){
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(getRow(StringConstants.CANCEL, callback));
        return getEditMessage(pojo, getMarkup(rows)).setText(text);
    }
}
