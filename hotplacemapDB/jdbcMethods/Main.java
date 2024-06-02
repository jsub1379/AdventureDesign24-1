//수정본 확인용 주석입니다. 사용시삭제
package adventuredesign11;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();
        Report report = new Report();
        BuildingFacility bf = new BuildingFacility();
        DeviceManagement deviceManagement = new DeviceManagement();
        FacilitySearch facilitySearch = new FacilitySearch(); // FacilitySearch 클래스 인스턴스 생성

        while (true) {
            System.out.println("Select an option: ");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Delete Account");
            System.out.println("4. Exit");
            System.out.print("Your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter username: ");
                    String regUsername = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String regPassword = scanner.nextLine();

                    boolean isRegistered = login.register(name, regUsername, regPassword);
                    if (isRegistered) {
                        System.out.println("User registered successfully!");
                    } else {
                        System.out.println("Registration failed.");
                    }
                    break;
                case 2:
                    System.out.print("Enter username: ");
                    String logUsername = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String logPassword = scanner.nextLine();

                    boolean isLoggedIn = login.login(logUsername, logPassword);
                    if (isLoggedIn) {
                        System.out.println("Login successful!");

                        while (true) {
                            System.out.println("Select an option: ");
                            System.out.println("1. Select a building");
                            System.out.println("2. Device 관리");
                            System.out.println("3. Facility 검색");
                            System.out.println("4. Exit");
                            System.out.print("Your choice: ");
                            int loggedInChoice = scanner.nextInt();
                            scanner.nextLine(); // Consume newline

                            if (loggedInChoice == 1) {
                                // 빌딩 선택
                                ArrayList<String> buildings = bf.getBuildings();
                                System.out.println("Select a building: ");
                                for (int i = 0; i < buildings.size(); i++) {
                                    System.out.println((i + 1) + ". " + buildings.get(i));
                                }
                                System.out.println((buildings.size() + 1) + ". Exit");
                                int buildingChoice = scanner.nextInt();
                                scanner.nextLine(); // Consume newline

                                if (buildingChoice == buildings.size() + 1) {
                                    System.out.println("Exiting to main menu...");
                                    break;
                                }

                                if (buildingChoice > 0 && buildingChoice <= buildings.size()) {
                                    int buildingId = bf.getBuildingId(buildingChoice);

                                    // 선택한 빌딩의 시설 선택
                                    while (true) {
                                        ArrayList<String> facilities = bf.getFacilities(buildingId);
                                        System.out.println("Select a facility: ");
                                        for (int i = 0; i < facilities.size(); i++) {
                                            System.out.println((i + 1) + ". " + facilities.get(i));
                                        }
                                        int facilityChoice = scanner.nextInt();
                                        scanner.nextLine(); // Consume newline

                                        if (facilityChoice > 0 && facilityChoice <= facilities.size()) {
                                            int facilityId = bf.getFacilityId(buildingId, facilityChoice);

                                            // 시설 선택 후 메뉴 표시
                                            displayFacilityMenu(scanner, logUsername, report, facilityId);
                                            break; // 빌딩 선택 단계로 돌아가기
                                        } else {
                                            System.out.println("Invalid facility choice. Please try again.");
                                        }
                                    }
                                } else {
                                    System.out.println("Invalid building choice. Please try again.");
                                }
                            } else if (loggedInChoice == 2) {
                                // 기기 관리
                                while (true) {
                                    System.out.println("Select an option: ");
                                    System.out.println("1. Update Device Info");
                                    System.out.println("2. Delete Device");
                                    System.out.println("3. Back to Main Menu");
                                    System.out.print("Your choice: ");
                                    int deviceChoice = scanner.nextInt();
                                    scanner.nextLine(); // Consume newline

                                    if (deviceChoice == 1) {
                                        // 기기 정보 수정
                                        System.out.print("Enter device ID to update: ");
                                        int deviceId = scanner.nextInt();
                                        scanner.nextLine(); // Consume newline
                                        System.out.print("Enter new facility ID: ");
                                        int newFacilityId = scanner.nextInt();
                                        scanner.nextLine(); // Consume newline
                                        System.out.print("Enter new MAC address: ");
                                        String newMac = scanner.nextLine();

                                        boolean isUpdated = deviceManagement.updateDevice(deviceId, newFacilityId, newMac);
                                        if (isUpdated) {
                                            System.out.println("Device information updated successfully!");
                                        } else {
                                            System.out.println("Device information update failed.");
                                        }
                                    } else if (deviceChoice == 2) {
                                        // 기기 삭제
                                        System.out.print("Enter device ID to delete: ");
                                        int deviceId = scanner.nextInt();
                                        scanner.nextLine(); // Consume newline

                                        boolean isDeleted = deviceManagement.deleteDevice(deviceId);
                                        if (isDeleted) {
                                            System.out.println("Device deleted successfully!");
                                        } else {
                                            System.out.println("Device deletion failed.");
                                        }
                                    } else if (deviceChoice == 3) {
                                        // 메인 메뉴로 돌아가기
                                        break;
                                    } else {
                                        System.out.println("Invalid choice. Please try again.");
                                    }
                                }
                            } else if (loggedInChoice == 3) {
                                // 시설 검색
                                System.out.print("Enter search keyword: ");
                                String keyword = scanner.nextLine();
                                ArrayList<String> facilities = facilitySearch.searchFacilities(keyword);
                                if (facilities.isEmpty()) {
                                    System.out.println("No facilities found.");
                                } else {
                                    System.out.println("Facilities found:");
                                    for (int i = 0; i < facilities.size(); i++) {
                                        System.out.println((i + 1) + ". " + facilities.get(i));
                                    }
                                    System.out.println((facilities.size() + 1) + ". Exit");
                                    int facilityChoice = scanner.nextInt();
                                    scanner.nextLine(); // Consume newline

                                    if (facilityChoice == facilities.size() + 1) {
                                        System.out.println("Exiting to main menu...");
                                    } else if (facilityChoice > 0 && facilityChoice <= facilities.size()) {
                                        String selectedFacilityName = facilities.get(facilityChoice - 1);
                                        int facilityId = facilitySearch.getFacilityIdByName(selectedFacilityName);

                                        // 선택된 시설에 대해 기존 메뉴 표시
                                        displayFacilityMenu(scanner, logUsername, report, facilityId);
                                    } else {
                                        System.out.println("Invalid facility choice. Please try again.");
                                    }
                                }
                            } else if (loggedInChoice == 4) {
                                System.out.println("Exiting to main menu...");
                                break;
                            } else {
                                System.out.println("Invalid choice. Please try again.");
                            }
                        }
                    } else {
                        System.out.println("Invalid username or password.");
                    }
                    break;
                case 3:
                    System.out.print("Enter username: ");
                    String delUsername = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String delPassword = scanner.nextLine();

                    boolean isDeleted = login.deleteUser(delUsername, delPassword);
                    if (isDeleted) {
                        System.out.println("Account deleted successfully.");
                    } else {
                        System.out.println("Account deletion failed.");
                    }
                    break;
                case 4:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayFacilityMenu(Scanner scanner, String logUsername, Report report, int facilityId) {
        while (true) {
            System.out.println("Select an option: ");
            System.out.println("1. Check Current Personnel");
            System.out.println("2. Report Issue");
            System.out.println("3. Back to Building Selection");
            System.out.print("Your choice: ");
            int facilityOption = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (facilityOption == 1) {
                // 현재 인원 확인 기능 수행
                System.out.println("추가 예정");
            } else if (facilityOption == 2) {
                // 제보 기능 수행
                System.out.print("Enter photo URL: ");
                String photoUrl = scanner.nextLine();
                System.out.print("Enter report content: ");
                String reportContent = scanner.nextLine();
                System.out.print("Enter estimated actual personnel: ");
                int estimatedActualPersonnel = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                boolean isReported = report.report(photoUrl, reportContent, estimatedActualPersonnel, logUsername, facilityId);
                if (isReported) {
                    System.out.println("Report submitted successfully!");
                } else {
                    System.out.println("Report submission failed.");
                }
            } else if (facilityOption == 3) {
                // 빌딩 선택 단계로 돌아가기
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
