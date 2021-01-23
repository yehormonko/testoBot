package yehor.monko;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import yehor.monko.actions.*;
import yehor.monko.model.Answer;
import yehor.monko.model.EditPojo;
import yehor.monko.model.Question;
import yehor.monko.model.Test;
import yehor.monko.utils.ActionConstants;
import yehor.monko.utils.StringConstants;
import yehor.monko.utils.UserState;

import java.time.LocalDateTime;
import java.util.HashMap;


public class Bot extends TelegramLongPollingBot {
    private SQLiteManager manager = new SQLiteManager();
    private HashMap<String, UserState> userState = new HashMap<>();
    private HashMap<String, Integer> mainMessages = new HashMap<String, Integer>();
    private HashMap<String, String> cache = new HashMap<>();
    private MainMenuAction mainMenuAction = new MainMenuAction();
    private TestActions testActions = new TestActions();
    private InputActions inputActions = new InputActions();
    private TestsListAction testsListAction = new TestsListAction();
    private QuestionActions questionActions = new QuestionActions();
    private PassTestActions passTestActions = new PassTestActions();
    private ResultsAction resultsAction = new ResultsAction();

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            Bot bot = new Bot();
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                processMessage(update.getMessage());
            }
            if (update.hasCallbackQuery()) {
                processCallback(update);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void processCallback(Update update) throws TelegramApiException {
        String data = update.getCallbackQuery().getData();
        String from = String.valueOf(update.getCallbackQuery().getFrom().getId());
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        EditPojo pojo = new EditPojo(chatId, messageId);
        mainMessages.put(from, messageId);
        userState.remove(from);
        System.out.println(LocalDateTime.now().toString()+": "+
                update.getCallbackQuery().getFrom().getUserName()+" ["+from+"] "+data);
        if (!data.contains("|") && !data.contains("&")) {
            processNoDataCallback(data, pojo, from);
        } else if (data.contains("|") && !data.contains("&")) {
            processSingleDataCallback(data, pojo, from);
        } else {
            processDoubleDataCallback(data, pojo, from);
        }
    }

    private void processDoubleDataCallback(String data, EditPojo pojo, String from) throws TelegramApiException {
        String firstData = data.substring(data.indexOf("|") + 1, data.indexOf("&"));
        String secondData = data.substring(data.indexOf("&") + 1);
        String action = data.substring(0, data.indexOf("|"));
        EditMessageText editMessageText = null;
        switch (action) {
            case ActionConstants.UPDATE_ANSWER:
                editMessageText = inputActions.inputAction(pojo, StringConstants.INPUT_NEW_ANSWER, ActionConstants.GET_QUESTION + "|" + secondData);
                userState.put(from, UserState.EDIT_ANSWER);
                cache.put(from, data);
                break;
            case ActionConstants.UPDATE_ANSWER_CORRECT: {
                Answer answer = new Answer(firstData, secondData);
                editMessageText = questionActions.changeAnswerCorrect(answer, pojo);
                break;
            }
            case ActionConstants.DELETE_ANSWER: {
                Answer answer = new Answer(firstData, secondData);
                editMessageText = questionActions.deleteAnswer(answer, pojo);
                break;
            }
            case ActionConstants.DELETE_QUESTION:
                editMessageText = testActions.deleteQuestion(secondData, firstData, pojo);
                break;
            case ActionConstants.CORRECT:
                editMessageText = passTestActions.getQuestion(firstData, pojo, from, 1, Integer.parseInt(secondData));
                break;
            case ActionConstants.WRONG:
                editMessageText = passTestActions.getQuestion(firstData, pojo, from, 0, Integer.parseInt(secondData));
                break;
        }
        execute(editMessageText);
    }

    private void processSingleDataCallback(String data, EditPojo pojo, String from) throws TelegramApiException {
        String id = data.substring(data.indexOf("|") + 1);
        String action = data.substring(0, data.indexOf("|"));
        EditMessageText editMessageText = null;
        switch (action) {
            case ActionConstants.ADD_QUESTION:
                editMessageText = inputActions.inputAction(pojo, StringConstants.INPUT_QUESTION, ActionConstants.SEE_TEST + "|" + id);
                cache.put(from, id);
                userState.put(from, UserState.INPUT_QUESTION);
                break;
            case ActionConstants.EDIT_TEST:
                editMessageText = inputActions.inputAction(pojo, StringConstants.INPUT_NEW_TEST_NAME, ActionConstants.SEE_TEST + "|" + id);
                cache.put(from, id);
                userState.put(from, UserState.EDIT_TEST);
                break;
            case ActionConstants.SHOW_ID:
                editMessageText = testActions.showId(id, pojo);
                break;
            case ActionConstants.SEE_TEST:
                editMessageText = testActions.getTestInfo(id, pojo);
                break;
            case ActionConstants.GET_QUESTION:
                editMessageText = questionActions.getQuestionInfo(id, pojo);
                break;
            case ActionConstants.EDIT_QUESTION:
                cache.put(from, id);
                userState.put(from, UserState.EDIT_QUESTION);
                editMessageText = inputActions.inputAction(pojo, StringConstants.INPUT_NEW_QUESTION, ActionConstants.GET_QUESTION + "|" + id);
                break;
            case ActionConstants.ADD_ANSWER:
                editMessageText = inputActions.inputAction(pojo, StringConstants.INPUT_QUESTION, ActionConstants.GET_QUESTION + "|" + id);
                userState.put(from, UserState.INPUT_ANSWER);
                cache.put(from, id);
                break;
            case ActionConstants.DELETE_TEST:
                editMessageText = testsListAction.deleteTest(id, from, pojo);
                break;
            case ActionConstants.INPUT_NAME:
                editMessageText = inputActions.inputAction(pojo, StringConstants.INPUT_YOUR_NAME, ActionConstants.MAIN_MENU);
                userState.put(from, UserState.INPUT_OWN_NAME);
                cache.put(from, id);
                break;
            case ActionConstants.END_TEST:
                editMessageText = passTestActions.getResults(id, pojo, from);
                break;
            case ActionConstants.SHOW_TEST_RESULTS:
                editMessageText = resultsAction.getResultsForTest(id, pojo);
                break;
        }
        execute(editMessageText);
    }

    private void processNoDataCallback(String data, EditPojo pojo, String from) throws TelegramApiException {
        if (data.equals(ActionConstants.MAIN_MENU)) {
            boolean del = false;
            try {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(pojo.getChatId());
                deleteMessage.setMessageId(pojo.getMessageId());
                execute(deleteMessage);
                execute(mainMenuAction.showMainMenu(pojo.getChatId()));
                del = true;
            } finally {
                if (!del)
                    execute(mainMenuAction.getMainMessage(pojo));
            }
        } else {
            EditMessageText editMessageText = null;
            switch (data) {
                case ActionConstants.CREATE_TEST:
                    editMessageText = inputActions.inputAction(pojo, StringConstants.INPUT_TEST_NAME, ActionConstants.MAIN_MENU);
                    userState.put(from, UserState.INPUT_TEST_NAME);
                    break;
                case ActionConstants.MY_TESTS:
                    editMessageText = testsListAction.getTestsList(from, pojo);
                    break;
                case ActionConstants.FIND_TEST:
                    editMessageText = inputActions.inputAction(pojo, StringConstants.INPUT_TEST_ID, ActionConstants.MAIN_MENU);
                    userState.put(from, UserState.INPUT_TEST_ID);
                    break;
                case ActionConstants.MY_RESULTS:
                    editMessageText = resultsAction.getResultsForUser(from, pojo);
                    break;
            }
            execute(editMessageText);
        }
    }

    private void processMessage(Message message) throws TelegramApiException {
        if (message.hasText()) {
            String fromId = String.valueOf(message.getFrom().getId());
            String chatId = String.valueOf(message.getChat().getId());
            int messageId = mainMessages.get(fromId);
            EditPojo pojo = new EditPojo(chatId, messageId);
            System.out.println(LocalDateTime.now().toString()+": "+message.getFrom().getUserName()+
                    "["+fromId+"] "+message.getText()+" [state="+userState.get(fromId)+"]");
            if (message.getText().equals("/start")) {
                manager.addUser(String.valueOf(message.getChatId()), message.getFrom().getUserName());
                execute(mainMenuAction.showMainMenu(chatId));
            } else {
                String text = message.getText();
                String id = System.currentTimeMillis() + "" + fromId;
                String cachedId = cache.get(fromId);
                EditMessageText editMessageText = null;
                switch (userState.get(fromId)) {
                    case INPUT_TEST_NAME:
                        Test test = new Test(id, text, fromId);
                        editMessageText = testActions.createTest(test, pojo);
                        break;
                    case INPUT_QUESTION:
                        Question newQuestion = new Question(text, id, cachedId);
                        editMessageText = questionActions.addQuestion(newQuestion, pojo);
                        break;
                    case EDIT_QUESTION:
                        Question questionEd = new Question(text, cachedId);
                        editMessageText = questionActions.editQuestion(questionEd, pojo);
                        break;
                    case EDIT_TEST:
                        Test editedTest = new Test(cachedId, text, fromId);
                        editMessageText = testActions.editTest(editedTest, pojo);
                        break;
                    case INPUT_ANSWER:
                        Answer answer = new Answer(text, id, cachedId);
                        editMessageText = questionActions.addAnswer(answer, pojo);
                        break;
                    case EDIT_ANSWER:
                        String questionIdE = cachedId.substring(cachedId.indexOf("&") + 1);
                        String answerIdE = cachedId.substring(cachedId.indexOf("|") + 1, cachedId.indexOf("&"));
                        Answer answerE = new Answer(text, answerIdE, questionIdE);
                        editMessageText = questionActions.changeAnswer(answerE, pojo);
                        break;
                    case INPUT_TEST_ID:
                        editMessageText = passTestActions.getTestInfo(text, pojo);
                        break;
                    case INPUT_OWN_NAME:
                        editMessageText = passTestActions.getFirsQuestion(cachedId, pojo, fromId, text);
                        break;
                }
                userState.remove(fromId);
                execute(editMessageText);
            }
        }
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(message.getMessageId());
        deleteMessage.setChatId(String.valueOf(message.getChatId()));
        execute(deleteMessage);
    }


    @Override
    public String getBotUsername() {
        return "testobobot";
    }

    @Override
    public String getBotToken() {
        return "1577045107:AAFJkJAOU1MRvXrEXdOREwqJjujCNgAFcNw";
    }

}
