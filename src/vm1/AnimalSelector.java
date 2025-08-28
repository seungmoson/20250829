package vm1;

import java.util.Scanner;

public class AnimalSelector {

    public Animal chooseAnimal() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("먹이를 줄 동물을 선택하세요 (1: 호랑이,2: 사자 3: 팬더):");
        int choice = scanner.nextInt();

        if (choice == 1) {
            return new Tiger();
        } else if (choice == 2) {
            return new Lion();
        } else if (choice == 3) {
            return new Panda();
        } else {
            System.out.println("잘못된 선택입니다.");
            return null;
        }
    }
}