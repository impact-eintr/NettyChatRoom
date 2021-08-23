package com.eintr.NettyChatRoom.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class LoginResponseMessage extends AbstractResponseMessage {
	public LoginResponseMessage(boolean success, String reason) {
		super(success, reason);
	}

	public int getMessageType() {
		return LoginResponseMessage;
	}
}
