enum MessageType { create, get, delete, update }

abstract class MessageModel {
  MessageType get messageType;
  String toJson();
}
