import 'dart:convert';

class ContactModel {
  ContactModel({
    required this.id,
    required this.name,
    required this.phone,
  });

  final int id;
  final String name;
  final String phone;

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'id': id,
      'name': name,
      'phone': phone,
    };
  }

  factory ContactModel.fromMap(Map<String, dynamic> map) {
    return ContactModel(
      id: map['id'] as int,
      name: map['name'] as String,
      phone: map['phone'] as String,
    );
  }

  String toJson() => json.encode(toMap());

  factory ContactModel.fromJson(String source) => ContactModel.fromMap(json.decode(source) as Map<String, dynamic>);

  @override
  String toString() => 'ContactModel(id: $id, name: $name, phone: $phone)';

  @override
  bool operator ==(covariant ContactModel other) {
    if (identical(this, other)) return true;
  
    return 
      other.id == id &&
      other.name == name &&
      other.phone == phone;
  }

  @override
  int get hashCode => id.hashCode ^ name.hashCode ^ phone.hashCode;
}
