package com.eintr.NettyChatRoom.message;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class Message implements Serializable {
	private int messageType;
	private int squenceId;
	public abstract int getMessageType();

	public static final int LoginRequestMessage = 0;
	public static final int LoginResponseMessage = 1;
	public static final int ChatRequestMessage = 2;
	public static final int ChatResponseMessage = 3;
	public static final int GroupCreateRequestMessage = 4;
	public static final int GroupCreateResponseMessage = 5;
	public static final int GroupJoinRequestMessage = 6;
	public static final int GroupJoinResponseMessage = 7;
	public static final int GroupQuitRequestMessage = 8;
	public static final int GroupQuitResponseMessage = 9;
	public static final int GroupChatRequestMessage = 10;
	public static final int GroupChatResponseMessage = 11;
	public static final int GroupMembersRequestMessage = 12;
	public static final int GroupMembersResponseMessage = 13;
	public static final int UserStatusRequestMessage = 14;
	public static final int UserStatusResponseMessage = 15;
	public static final int PingMessage = -1;
	public static final int PongMessage = -2;
}