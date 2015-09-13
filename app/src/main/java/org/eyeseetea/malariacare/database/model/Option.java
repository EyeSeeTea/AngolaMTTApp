package org.eyeseetea.malariacare.database.model;

import com.orm.SugarRecord;


public class Option extends SugarRecord<Option> {

    //FIXME A 'Yes' answer shows children questions, this should be configurable by some additional attribute in Option
    public static final String CHECKBOX_YES_OPTION="Yes";

    String code;
    String name;
    Float factor;
    Answer answer;
    String path;
    OptionAttribute optionAttribute;

    public Option() {
    }

    public Option(String name, Float factor, Answer answer, String code, OptionAttribute optionAttribute) {
        this.name = name;
        this.factor = factor;
        this.answer = answer;
        this.code = code;
        this.optionAttribute = optionAttribute;
    }

    public Option(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getFactor() {
        return factor;
    }

    public void setFactor(Float factor) {
        this.factor = factor;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public OptionAttribute getOptionAttribute() {
        return optionAttribute;
    }

    public void setOptionAttribute(OptionAttribute optionAttribute) {
        this.optionAttribute = optionAttribute;
    }

    /**
     * Checks if this option actives the children questions
     * @return true: Children questions should be shown, false: otherwise.
     */
    public boolean isActiveChildren(){
        return CHECKBOX_YES_OPTION.equals(name);
    }

    /**
     * Checks if this option name is equals to a given string.
     *
     * @return true|false
     */
    public boolean is(String given){
        return given.equals(name);
    }



    @Override
    public String toString() {
        return "Option{" +
                "name='" + name + '\'' +
                ", code=" + code +
                ", factor=" + factor +
                ", answer=" + answer +
                ", path=" + path +
                ", optionAttribute=" + optionAttribute +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Option option = (Option) o;

        if (answer != null ? !answer.equals(option.answer) : option.answer != null) return false;
        if (factor != null ? !factor.equals(option.factor) : option.factor != null) return false;
        if (code != null ? !code.equals(option.code) : option.code != null) return false;
        if (name != null ? !name.equals(option.name) : option.name != null) return false;
        if (path != null ? !path.equals(option.path) : option.path != null) return false;
        if (optionAttribute != null ? !optionAttribute.equals(option.optionAttribute) : option.optionAttribute != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (factor != null ? factor.hashCode() : 0);
        result = 31 * result + (answer != null ? answer.hashCode() : 0);
        result = 31 * result + (optionAttribute != null ? optionAttribute.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }
}
