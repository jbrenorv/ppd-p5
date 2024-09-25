import 'dart:convert';

import 'package:collection/collection.dart';

import 'contact_model.dart';
import 'message_model.dart';

class ContactListMessageModel extends MessageModel {
  ContactListMessageModel({
    required this.contacts,
  });

  @override
  MessageType get messageType => MessageType.get;
  
  final List<ContactModel> contacts;

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'messageType': MessageType.values.indexOf(messageType),
      'contacts': contacts.map((x) => x.toMap()).toList(),
    };
  }

  factory ContactListMessageModel.fromMap(Map<String, dynamic> map) {
    return ContactListMessageModel(
      contacts: List<ContactModel>.from(
        (map['contacts'] as List).map((x) => ContactModel.fromMap(x)),
      )
    );
  }

  @override
  String toJson() => json.encode(toMap());

  factory ContactListMessageModel.fromJson(String source) => ContactListMessageModel.fromMap(json.decode(source) as Map<String, dynamic>);

  @override
  bool operator ==(covariant ContactListMessageModel other) {
    if (identical(this, other)) return true;
    final listEquals = const DeepCollectionEquality().equals;
  
    return 
      other.messageType == messageType &&
      listEquals(other.contacts, contacts);
  }

  @override
  int get hashCode => messageType.hashCode ^ contacts.hashCode;
}
