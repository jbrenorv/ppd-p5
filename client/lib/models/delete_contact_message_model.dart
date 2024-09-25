import 'dart:convert';

import 'package:client/models/contact_model.dart';
import 'package:client/models/message_model.dart';

class DeleteContactMessageModel extends MessageModel {
  DeleteContactMessageModel({
    required this.contact,
  });

  final ContactModel contact;

  @override
  MessageType get messageType => MessageType.delete;

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'messageType': MessageType.values.indexOf(messageType),
      'contact': contact.toMap(),
    };
  }

  factory DeleteContactMessageModel.fromMap(Map<String, dynamic> map) {
    return DeleteContactMessageModel(
      contact: ContactModel.fromMap(map['contact'] as Map<String,dynamic>),
    );
  }

  @override
  String toJson() => json.encode(toMap());

  factory DeleteContactMessageModel.fromJson(String source) => DeleteContactMessageModel.fromMap(json.decode(source) as Map<String, dynamic>);

  @override
  String toString() => 'DeleteContactMessageModel(contact: $contact)';

  @override
  bool operator ==(covariant DeleteContactMessageModel other) {
    if (identical(this, other)) return true;
  
    return 
      other.contact == contact;
  }

  @override
  int get hashCode => contact.hashCode;
}
