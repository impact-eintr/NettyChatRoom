package com.eintr.NettyChatRoom.message;

public class UserStatusRequestMessage extends Message {

	@Override
	public int getMessageType() {
		return UserStatusRequestMessage;
	}
}
