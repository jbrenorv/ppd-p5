import 'dart:convert';

import 'contact_model.dart';
import 'message_model.dart';

class UpdateContactMessageModel extends MessageModel {
  UpdateContactMessageModel({required this.contact});

  final ContactModel contact;

  @override
  MessageType get messageType => MessageType.update;

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'messageType': MessageType.values.indexOf(messageType),
      'contact': contact.toMap(),
    };
  }

  factory UpdateContactMessageModel.fromMap(Map<String, dynamic> map) {
    return UpdateContactMessageModel(
      contact: ContactModel.fromMap(map['contact'] as Map<String,dynamic>),
    );
  }

  @override
  String toJson() => json.encode(toMap());

  factory UpdateContactMessageModel.fromJson(String source) => UpdateContactMessageModel.fromMap(json.decode(source) as Map<String, dynamic>);

  @override
  String toString() => 'UpdateContactMessageModel(contact: $contact)';

  @override
  bool operator ==(covariant UpdateContactMessageModel other) {
    if (identical(this, other)) return true;
  
    return other.contact == contact;
  }

  @override
  int get hashCode => contact.hashCode;
}
