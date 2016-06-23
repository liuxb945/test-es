/**
 * 
 */


/**
 * 用户对象的搜索VO
 * @author 400
 *
 */
public class UserSearchVO {


	private Integer id ;
	private String name ;
	private String picture ;
	/**
	 * 标签id集合 如:5,6 英文逗号分开
	 */
	private String label ;
	/**
	 * 已经回答的问题或者自己提的问题id组合
	 */
	private String  question ;
	/**
	 * 已经回答过的存货或者自己发的存货id组合
	 */
	private String inventory ;
	/**
	 * 当天被邀请的次数
	 */
	private int inviteCount ;
	/**
	 * 回答问题的次数
	 */
	private int questionAnswerCount ;
	/**
	 * 回答存货的次数
	 */
	private int inventoryAnswerCount ;
	
 
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
 
	public int getInviteCount() {
		return inviteCount;
	}
	public void setInviteCount(int inviteCount) {
		this.inviteCount = inviteCount;
	}
	public int getQuestionAnswerCount() {
		return questionAnswerCount;
	}
	public void setQuestionAnswerCount(int questionAnswerCount) {
		this.questionAnswerCount = questionAnswerCount;
	}
	public int getInventoryAnswerCount() {
		return inventoryAnswerCount;
	}
	public void setInventoryAnswerCount(int inventoryAnswerCount) {
		this.inventoryAnswerCount = inventoryAnswerCount;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getInventory() {
		return inventory;
	}
	public void setInventory(String inventory) {
		this.inventory = inventory;
	}
	

}
