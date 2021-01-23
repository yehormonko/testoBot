package yehor.monko;

import yehor.monko.model.Answer;
import yehor.monko.model.Question;
import yehor.monko.model.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLiteManager {
    private String url = "jdbc:sqlite:db";

    public SQLiteManager() {
        createNewDatabase();
    }

    public void createNewDatabase() {
        File file = new File("db");

        if (file.exists()) {
            return;
        }
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String sql = "CREATE TABLE users (" +
                        "chat_id text PRIMARY KEY," +
                        "user_id text);" +
                        "";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.execute();
                statement.close();
                String createTableTests = "CREATE TABLE tests (" +
                        "test_id text PRIMARY KEY,"+
                        "user_id text," +
                        "test_name text);" +
                        "";
                PreparedStatement statement1 = conn.prepareStatement(createTableTests);
                statement1.execute();
                statement1.close();
                String createTableQuestions = "CREATE TABLE questions (" +
                        "question_id text PRIMARY KEY,"+
                        "test_id text," +
                        "question_text text);" +
                        "";
                PreparedStatement statement2 = conn.prepareStatement(createTableQuestions);
                statement2.execute();
                statement2.close();
                String createTableAnswers = "CREATE TABLE answers (" +
                        "answer_id text PRIMARY KEY,"+
                        "answer text," +
                        "question_id text," +
                        "isCorrect boolean);" +
                        "";
                PreparedStatement statement3 = conn.prepareStatement(createTableAnswers);
                statement3.execute();
                statement3.close();
                String createTableResults = "CREATE TABLE res (" +
                        "test_id text," +
                        "user_id text," +
                        "name text," +
                        "d text," +
                        "score number default 0);" +
                        "";
                PreparedStatement statement4 = conn.prepareStatement(createTableResults);
                statement4.execute();
                statement4.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addUser(String chatId, String user_id) {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String select = "select count(*) from users where chat_id = ?";
                PreparedStatement statement = conn.prepareStatement(select);
                statement.setString(1, chatId);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                if (resultSet.getInt(1) == 0) {
                    String insert = "insert into users (chat_id, user_id) values (?,?)";
                    statement = conn.prepareStatement(insert);
                    statement.setString(1, chatId);
                    statement.setString(2, user_id);
                    statement.execute();
                }
                statement.close();
                resultSet.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addTest(Test test) {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String insert = "insert into tests (test_id, user_id, test_name) values (?,?,?)";
                PreparedStatement statement = conn.prepareStatement(insert);
                statement.setString(1, test.getId());
                statement.setString(2, test.getOwner());
                statement.setString(3, test.getTitle());
                statement.execute();
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void editTest(Test test) {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String update = "update tests set test_name = ? where test_id = ?";
                PreparedStatement statement = conn.prepareStatement(update);
                statement.setString(1, test.getTitle());
                statement.setString(2, test.getId());
                statement.execute();
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void editQuestion(Question question) {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String update = "update questions set question_text = ? where question_id = ?";
                PreparedStatement statement = conn.prepareStatement(update);
                statement.setString(1, question.getQuestionText());
                statement.setString(2, question.getId());
                statement.execute();
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Test> getTestList(String ownerId){
        List<Test> tests = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String select = "select * from tests where user_id = ?;";
                PreparedStatement statement = conn.prepareStatement(select);
                statement.setString(1, ownerId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Test test = new Test();
                    test.setId(resultSet.getString(1));
                    test.setOwner(resultSet.getString(2));
                    test.setTitle(resultSet.getString(3));
                    tests.add(test);
                }
                statement.close();
                resultSet.close();
                return tests;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return tests;
    }

    public void addQuestion(Question question) {
        System.out.println("MANAGER INSERT "+question.getTestId());
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String insert = "insert into questions (question_id, test_id, question_text) values (?,?,?)";
                PreparedStatement statement = conn.prepareStatement(insert);
                statement.setString(1, question.getId());
                statement.setString(2, question.getTestId());
                statement.setString(3, question.getQuestionText());
                statement.execute();
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Question getQuestion(String questionId){
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String select = "select * from questions where question_id = ?";
                PreparedStatement statement = conn.prepareStatement(select);
                statement.setString(1, questionId);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                Question question = new Question();
                question.setId(questionId);
                question.setTestId(resultSet.getString(2));
                question.setQuestionText(resultSet.getString(3));
                statement.close();
                String selectAnswers = "select * from answers where question_id = ?";
                PreparedStatement answersStatement = conn.prepareStatement(selectAnswers);
                answersStatement.setString(1, questionId);
                ResultSet answersRS = answersStatement.executeQuery();
                ArrayList<Answer> answers = new ArrayList<>();
                while (answersRS.next()){
                    Answer answer = new Answer();
                    answer.setId(answersRS.getString(1));
                    answer.setText(answersRS.getString(2));
                    answer.setQuestionId(questionId);
                    answer.setCorrect(answersRS.getBoolean(4));
                    answers.add(answer);
                }
                question.setAnswers(answers);
                resultSet.close();
                return question;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Test getTest(String id){
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String select = "select * from tests where test_id = ?";
                PreparedStatement statement = conn.prepareStatement(select);
                statement.setString(1, id);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                Test test = new Test();
                test.setId(id);
                test.setOwner(resultSet.getString(2));
                test.setTitle(resultSet.getString(3));
                statement.close();
                resultSet.close();
                String questionsSelect = "select * from questions where test_id = ?";
                PreparedStatement questionsSt = conn.prepareStatement(questionsSelect);
                questionsSt.setString(1, id);
                ResultSet questionsRS = questionsSt.executeQuery();
                ArrayList<Question> questions = new ArrayList<>();
                while (questionsRS.next()) {
                    Question question = new Question();
                    question.setId(questionsRS.getString(1));
                    question.setTestId(questionsRS.getString(2));
                    question.setQuestionText(questionsRS.getString(3));
                    questions.add(question);
                }
                test.setQuestions(questions);
                questionsRS.close();
                questionsSt.close();
                return test;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public Test getValidTest(String id){
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String select = "select * from tests where test_id = ?";
                PreparedStatement statement = conn.prepareStatement(select);
                statement.setString(1, id);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                Test test = new Test();
                test.setId(id);
                test.setOwner(resultSet.getString(2));
                test.setTitle(resultSet.getString(3));
                statement.close();
                resultSet.close();

                String questionsSelect = "select * from questions q where test_id = ? and (select count(*) from answers where question_id = q.question_id and isCorrect = true)>0";
                PreparedStatement questionsSt = conn.prepareStatement(questionsSelect);
                questionsSt.setString(1, id);
                ResultSet questionsRS = questionsSt.executeQuery();
                ArrayList<Question> questions = new ArrayList<>();
                while (questionsRS.next()) {
                    Question question = new Question();
                    question.setId(questionsRS.getString(1));
                    question.setTestId(questionsRS.getString(2));
                    question.setQuestionText(questionsRS.getString(3));
                    questions.add(question);
                }
                if(questions.size()<1) return null;
                test.setQuestions(questions);
                questionsRS.close();
                questionsSt.close();
                return test;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void addAnswer(Answer answer) {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String insert = "insert into answers (answer_id, answer, question_id, isCorrect) values (?,?,?,?)";
                PreparedStatement statement = conn.prepareStatement(insert);
                statement.setString(1, answer.getId());
                statement.setString(2, answer.getText());
                statement.setString(3, answer.getQuestionId());
                statement.setBoolean(4, false);
                statement.execute();
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void editAnswer(Answer answer) {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String insert = "update answers set answer = ? where answer_id = ?";
                PreparedStatement statement = conn.prepareStatement(insert);
                statement.setString(1, answer.getText());
                statement.setString(2, answer.getId());
                statement.execute();
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void editAnswerCorrect(Answer answer) {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String insert = "update answers set isCorrect = not isCorrect where answer_id = ?";
                PreparedStatement statement = conn.prepareStatement(insert);
                statement.setString(1, answer.getId());
                statement.execute();
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteAnswer(String answerId) {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String insert = "delete from answers where answer_id = ?";
                PreparedStatement statement = conn.prepareStatement(insert);
                statement.setString(1, answerId);
                statement.execute();
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void deleteQuestion(String questionId) {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String deleteAnsw = "delete from answers where question_id = ?";
                PreparedStatement statementA = conn.prepareStatement(deleteAnsw);
                statementA.setString(1, questionId);
                statementA.execute();
                statementA.close();
                String insert = "delete from questions where question_id = ?";
                PreparedStatement statement = conn.prepareStatement(insert);
                statement.setString(1, questionId);
                statement.execute();
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void deleteTest(Test test) {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                for (Question q:test.getQuestions()) {
                    deleteQuestion(q.getId());
                }
                String deleteAnsw = "delete from tests where test_id = ?";
                PreparedStatement statementA = conn.prepareStatement(deleteAnsw);
                statementA.setString(1, test.getId());
                statementA.execute();
                statementA.close();
                String deleteRes = "delete from res where test_id = ?";
                PreparedStatement statement = conn.prepareStatement(deleteRes);
                statement.setString(1, test.getId());
                statement.execute();
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void initiateRes(String testId, String userId, String name){
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String insert = "insert into res (test_id, user_id, name, score, d) values (?,?,?,0,?)";
                PreparedStatement statement = conn.prepareStatement(insert);
                statement.setString(1, testId);
                statement.setString(2, userId);
                statement.setString(3, name);
                statement.setString(4, System.currentTimeMillis()+"");
                statement.execute();
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void addCorrectAnswRes(String testId, String userId){
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String insert = "update res set score = score+1 where test_id = ? and user_id = ?";
                PreparedStatement statement = conn.prepareStatement(insert);
                statement.setString(1, testId);
                statement.setString(2, userId);
                statement.execute();
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public String getResultsForTestAndUser(String testId, String userId){
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String select = "select name, score from res where test_id = ? and user_id = ? order by d desc";
                PreparedStatement statement = conn.prepareStatement(select);
                statement.setString(1, testId);
                statement.setString(2, userId);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                String name = resultSet.getString(1);
                int score = resultSet.getInt(2);
                resultSet.close();
                statement.close();
                return name+": "+score;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return "";
    }
    public ArrayList<String> getResultsForTest(String testId){
        ArrayList<String> res = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String select = "select name, score from res where test_id = ? order by d desc";
                PreparedStatement statement = conn.prepareStatement(select);
                statement.setString(1, testId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String name = resultSet.getString(1);
                    int score = resultSet.getInt(2);
                    res.add(name+" отримав "+score);
                }
                resultSet.close();
                statement.close();
                return res;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return res;
    }

    public ArrayList<String> getResultsForUsers(String userId){
        ArrayList<String> res = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String select = "select t.test_name, score, (select count(*) from questions where test_id = t.test_id) from res r join tests t on r.test_id = t.test_id where r.user_id = ? order by d desc";
                PreparedStatement statement = conn.prepareStatement(select);
                statement.setString(1, userId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String name = resultSet.getString(1);
                    int score = resultSet.getInt(2);
                    int t = resultSet.getInt(3);
                    res.add(name+": "+score+"/"+t);
                }
                resultSet.close();
                statement.close();
                return res;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return res;
    }

}
