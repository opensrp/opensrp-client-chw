package org.smartregister.chw.util;

public class BirthCertDataModel {
    private String question;
    private String answer;
    private String birthCertDate;
    private String birthCertNumber;
    private boolean isBirthCertHas;

    public boolean isBirthCertHas() {
        return isBirthCertHas;
    }

    public void setBirthCertHas(boolean birthCertHas) {
        isBirthCertHas = birthCertHas;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getBirthCertDate() {
        return birthCertDate;
    }

    public void setBirthCertDate(String birthCertDate) {
        this.birthCertDate = birthCertDate;
    }

    public String getBirthCertNumber() {
        return birthCertNumber;
    }

    public void setBirthCertNumber(String birthCertNumber) {
        this.birthCertNumber = birthCertNumber;
    }
}
