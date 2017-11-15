package kr.asv.apps.salarytax.items;

/**
 *
 * Created by EXIZT on 2016-04-30.
 */
public class WordDictionaryItem {
    private int key;
    public String id;
    public String subject;
    public String explanation;
    public String process;
    public String history;

    public String toString()
    {
        return "DataItem{id=[" + id + "], subject=["+subject + "] explanation=[" + explanation + "], process=[" + process + "], history=[" + history + "]}";
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    @SuppressWarnings("unused")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @SuppressWarnings("unused")
    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    @SuppressWarnings("unused")
    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    @SuppressWarnings("unused")
    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }
}
