class Facility {
  final int facilityId;
  final String facilityName;
  final String facilityAddress;
  final int buildingId;
  final String buildingName;
  final String buildingAddress;
  final double latitude;
  final double longitude;

  Facility({
    required this.facilityId,
    required this.facilityName,
    required this.facilityAddress,
    required this.buildingId,
    required this.buildingName,
    required this.buildingAddress,
    required this.latitude,
    required this.longitude,
  });

  factory Facility.fromJson(Map<String, dynamic> json) {
    return Facility(
      facilityId: json['facility']['facilityId'],
      facilityName: json['facility']['facilityName'],
      facilityAddress: json['facility']['facilityAddress'],
      buildingId: json['buildingId'],
      buildingName: json['buildingName'],
      buildingAddress: json['buildingAddress'],
      latitude: json['latitude'],
      longitude: json['longitude'],
    );
  }
}
