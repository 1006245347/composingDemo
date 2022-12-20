package cn.hwj.search

class Status {
    private var isRetweet = false
    private var text: String? = null
    private var userName: String? = null
    private var userAvatar: String? = null
    private var createdAt: String? = null

    fun isRetweet(): Boolean {
        return isRetweet
    }

    fun setRetweet(retweet: Boolean) {
        isRetweet = retweet
    }

    fun getText(): String? {
        return text
    }

    fun setText(text: String?) {
        this.text = text
    }

    fun getUserName(): String? {
        return userName
    }

    fun setUserName(userName: String?) {
        this.userName = userName
    }

    fun getUserAvatar(): String? {
        return userAvatar
    }

    fun setUserAvatar(userAvatar: String?) {
        this.userAvatar = userAvatar
    }

    fun getCreatedAt(): String? {
        return createdAt
    }

    fun setCreatedAt(createdAt: String?) {
        this.createdAt = createdAt
    }

    override fun toString(): String {
        return "Status{" +
                "isRetweet=" + isRetweet +
                ", text='" + text + '\'' +
                ", userName='" + userName + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}'
    }
}