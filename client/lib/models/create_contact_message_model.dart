import 'dart:convert';

import 'contact_model.dart';
import 'message_model.dart';

class CreateContactMessageModel extends MessageModel {
  CreateContactMessageModel({
    required this.contact,
  });

  @override
  MessageType get messageType => MessageType.create;

  final ContactModel contact;

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'messageType': MessageType.values.indexOf(messageType),
      'contact': contact.toMap(),
    };
  }

  factory CreateContactMessageModel.fromMap(Map<String, dynamic> map) {
    return CreateContactMessageModel(
      contact: ContactModel.fromMap(map['contact'] as Map<String,dynamic>),
    );
  }

  @override
  String toJson() => json.encode(toMap());

  factory CreateContactMessageModel.fromJson(String source) => CreateContactMessageModel.fromMap(json.decode(source) as Map<String, dynamic>);

  @override
  bool operator ==(covariant CreateContactMessageModel other) {
    if (identical(this, other)) return true;
  
    return 
      other.messageType == messageType &&
      other.contact == contact;
  }

  @override
  int get hashCode => messageType.hashCode ^ contact.hashCode;
}
