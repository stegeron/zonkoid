package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 19.09.2016
 */
public class Question implements Serializable {

    /**
     *
     * {
     * "id":5781,
     * "message":"Hyundai v podstat ... jen 191Kč.",
     * "answer":{
     *     "id":3916,
     *     "message":"Děkuji doufám, že ano. Moje... sem si vybral zase Hyundai.",
     *     "status":"ACTIVE",
     *     "timeCreated":"2016-09-19T13:33:25.881+02:00",
     *     "timeModified":"2016-09-19T13:33:25.881+02:00"
     *     },
     * "status":"ACTIVE",
     * "questedBy":{
     *     "id":18567,
     *     "nickName":"LadaLoveMoney"
     *     },
     * "timeCreated":"2016-09-19T12:11:03.200+02:00",
     * "timeModified":"2016-09-19T12:11:03.200+02:00"
     * }
     */

    Integer id;
    String message;
    Answer answer;
    String status; // mel by byt enum
    QuestedBy questedBy;
    Date timeCreated;
    Date timeModified;
    Boolean deleted;

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public QuestedBy getQuestedBy() {
        return questedBy;
    }

    public void setQuestedBy(QuestedBy questedBy) {
        this.questedBy = questedBy;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Date getTimeModified() {
        return timeModified;
    }

    public void setTimeModified(Date timeModified) {
        this.timeModified = timeModified;
    }

    public class Answer {
        int id;
        String message;
        String status; // enum?
        Date timeCreated;
        Date timeModified;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Date getTimeCreated() {
            return timeCreated;
        }

        public void setTimeCreated(Date timeCreated) {
            this.timeCreated = timeCreated;
        }

        public Date getTimeModified() {
            return timeModified;
        }

        public void setTimeModified(Date timeModified) {
            this.timeModified = timeModified;
        }
    }

    public class QuestedBy {
        int id;
        String nickName;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }
    }
}
