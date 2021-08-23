package com.eintr.NettyChatRoom.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class UserStatusResponseMessage extends Message {
	private Boolean Online; // 该用户是否在线
	private Boolean Logined; // 有用户上线 通知给所有人

	public int getMessageType() {
		return UserStatusResponseMessage;
	}
}
