import java.io.Serializable;
import java.util.Date;
 
 
 
public class Question implements Serializable {
   
    private Integer id;

     public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}


	public Byte getEnable() {
		return enable;
	}

	public void setEnable(Byte enable) {
		this.enable = enable;
	}

	public Integer getUsedAnswerId() {
		return usedAnswerId;
	}

	public void setUsedAnswerId(Integer usedAnswerId) {
		this.usedAnswerId = usedAnswerId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getBrowseNum() {
		return browseNum;
	}

	public void setBrowseNum(Integer browseNum) {
		this.browseNum = browseNum;
	}

	public Integer getTopNum() {
		return topNum;
	}

	public void setTopNum(Integer topNum) {
		this.topNum = topNum;
	}

	public Integer getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(Integer commentNum) {
		this.commentNum = commentNum;
	}

	public Integer getAnswerNum() {
		return answerNum;
	}

	public void setAnswerNum(Integer answerNum) {
		this.answerNum = answerNum;
	}

	public Integer getBusinessScore() {
		return businessScore;
	}

	public void setBusinessScore(Integer businessScore) {
		this.businessScore = businessScore;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastActiveTime() {
		return lastActiveTime;
	}

	public void setLastActiveTime(Date lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}

	public Integer getLastActiveUserId() {
		return lastActiveUserId;
	}

	public void setLastActiveUserId(Integer lastActiveUserId) {
		this.lastActiveUserId = lastActiveUserId;
	}

	private Integer userId;
 
    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private String title;
 

    private Byte enable;

    private Integer usedAnswerId;

    private String description;

    private Integer browseNum;

    private Integer topNum;

    private Integer commentNum;

    private Integer answerNum;

    private Integer businessScore;

    private Date createTime;

    private Date lastActiveTime;

    private Integer lastActiveUserId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

 
 

 

 
}