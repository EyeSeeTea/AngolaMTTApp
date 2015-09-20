package org.eyeseetea.malariacare.database.model;

import com.orm.SugarRecord;
import com.orm.query.Select;

import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class Value extends SugarRecord<Value> {

    Option option;
    Question question;
    String value;
    Survey survey;

    public Value() {
    }

    public Value(String value, Question question, Survey survey) {
        this.option = null;
        this.question = question;
        this.value = value;
        this.survey = survey;
    }

    public Value(Option option, Question question, Survey survey) {
        this.option = option;
        this.question = question;
        this.value = option.getName();
        this.survey = survey;
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    /**
     * Checks if the current value contains an answer
     * @return true|false
     */
    public boolean isAnAnswer(){
        return (getValue() != null && !getValue().equals("")) || getOption() != null;
    }

    /**
     * Checks if the current value belongs to a 'required' question
     * @return
     */
    public boolean belongsToAParentQuestion(){
        return !getQuestion().hasParent();
    }

    /**
     * The value is 'Yes' from a dropdown
     * @return true|false
     */
    public boolean isAYes() {
        return getOption() != null && getOption().getName().equals("Yes");
    }

    /**
     * The value is 'No' from a dropdown
     * @return true|false
     */
    public boolean isANo() {
        return getOption() != null && getOption().getName().equals("No");
    }

    public static int countBySurvey(Survey survey){
        if(survey==null || survey.getId()==null){
            return 0;
        }
        String[] whereArgs={survey.getId().toString()};
        return (int)Value.count(Value.class,"survey=?",whereArgs);
    }

    public static List<Value> listAllBySurvey(Survey survey){
        if(survey==null || survey.getId()==null){
            return new ArrayList<Value>();
        }
        return Select.from(Value.class)
                .where(com.orm.query.Condition.prop("survey").eq(survey.getId()))
                .orderBy("id")
                .list();
    }

    @Override
    public String toString() {
        return "Value{" +
                "option=" + option +
                ", question=" + question +
                ", value='" + value + '\'' +
                ", survey=" + survey +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Value)) return false;

        Value value1 = (Value) o;

        if (!option.equals(value1.option)) return false;
        if (!question.equals(value1.question)) return false;
        if (!survey.equals(value1.survey)) return false;
        if (!value.equals(value1.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = option.hashCode();
        result = 31 * result + question.hashCode();
        result = 31 * result + value.hashCode();
        result = 31 * result + survey.hashCode();
        return result;
    }

}
